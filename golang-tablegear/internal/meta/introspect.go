package meta

import (
	"context"
	"database/sql"
	"fmt"
	"github.com/yujiaao/golang-tablegear/internal/config"
	"strings"
)

type Inspector struct {
	db     *sql.DB
	driver string
	schema string
}

func NewInspector(db *sql.DB, driver, schema string) *Inspector {
	return &Inspector{
		db:     db,
		driver: strings.ToLower(driver),
		schema: schema,
	}
}

func (i *Inspector) InspectTables(ctx context.Context, include []string) ([]Table, error) {
	tableNames, err := i.listTables(ctx, include)
	if err != nil {
		return nil, err
	}
	out := make([]Table, 0, len(tableNames))
	for _, t := range tableNames {
		cols, pkSet, err := i.readColumns(ctx, t)
		if err != nil {
			return nil, err
		}
		pks := make([]string, 0)
		for _, c := range cols {
			if pkSet[c.Name] {
				c.IsPrimary = true
				pks = append(pks, c.Name)
			}
		}
		for idx := range cols {
			cols[idx].IsPrimary = pkSet[cols[idx].Name]
		}
		out = append(out, Table{Name: t, Columns: cols, PKs: pks})
	}
	return out, nil
}

func (i *Inspector) InspectLogicalViews(ctx context.Context, defs []config.LogicalViewDef) ([]Table, error) {
	out := make([]Table, 0, len(defs))
	for _, def := range defs {
		wrapped := fmt.Sprintf("SELECT * FROM (%s) tg_logic_view LIMIT 0", def.SQL)
		rows, err := i.db.QueryContext(ctx, wrapped)
		if err != nil {
			return nil, fmt.Errorf("inspect logical view %s failed: %w", def.Name, err)
		}
		types, err := rows.ColumnTypes()
		rows.Close()
		if err != nil {
			return nil, fmt.Errorf("read logical view columns %s failed: %w", def.Name, err)
		}
		cols := make([]Column, 0, len(types))
		for _, ct := range types {
			nullable, _ := ct.Nullable()
			dbType := ct.DatabaseTypeName()
			cols = append(cols, Column{
				Name:     ct.Name(),
				DBType:   dbType,
				Nullable: nullable,
				GoType:   mapGoType(dbType, nullable),
			})
		}
		out = append(out, Table{
			Name:          def.Name,
			Columns:       cols,
			PKs:           nil,
			IsLogicalView: true,
			CoreSQL:       def.SQL,
			ReadOnly:      true,
		})
	}
	return out, nil
}

func (i *Inspector) listTables(ctx context.Context, include []string) ([]string, error) {
	var query string
	var args []any
	switch i.driver {
	case "mysql":
		query = "select table_name from information_schema.tables where table_schema = ? and table_type = 'BASE TABLE' order by table_name"
		args = append(args, i.schema)
	case "postgres", "postgresql":
		query = "select table_name from information_schema.tables where table_schema = $1 and table_type = 'BASE TABLE' order by table_name"
		args = append(args, i.schema)
	default:
		return nil, fmt.Errorf("unsupported driver: %s", i.driver)
	}
	rows, err := i.db.QueryContext(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	filter := map[string]bool{}
	if len(include) > 0 {
		for _, t := range include {
			filter[strings.ToLower(t)] = true
		}
	}

	names := make([]string, 0)
	for rows.Next() {
		var t string
		if err = rows.Scan(&t); err != nil {
			return nil, err
		}
		if len(filter) > 0 && !filter[strings.ToLower(t)] {
			continue
		}
		names = append(names, t)
	}
	return names, rows.Err()
}

func (i *Inspector) readColumns(ctx context.Context, tableName string) ([]Column, map[string]bool, error) {
	var qCols string
	var qPK string
	var args []any
	switch i.driver {
	case "mysql":
		qCols = "select column_name, data_type, is_nullable from information_schema.columns where table_schema = ? and table_name = ? order by ordinal_position"
		qPK = "select k.column_name from information_schema.table_constraints t join information_schema.key_column_usage k on t.constraint_name = k.constraint_name and t.table_schema = k.table_schema and t.table_name = k.table_name where t.table_schema = ? and t.table_name = ? and t.constraint_type = 'PRIMARY KEY' order by k.ordinal_position"
		args = []any{i.schema, tableName}
	case "postgres", "postgresql":
		qCols = "select column_name, data_type, is_nullable from information_schema.columns where table_schema = $1 and table_name = $2 order by ordinal_position"
		qPK = "select kcu.column_name from information_schema.table_constraints tc join information_schema.key_column_usage kcu on tc.constraint_name = kcu.constraint_name and tc.table_schema = kcu.table_schema where tc.table_schema = $1 and tc.table_name = $2 and tc.constraint_type = 'PRIMARY KEY' order by kcu.ordinal_position"
		args = []any{i.schema, tableName}
	default:
		return nil, nil, fmt.Errorf("unsupported driver: %s", i.driver)
	}

	colRows, err := i.db.QueryContext(ctx, qCols, args...)
	if err != nil {
		return nil, nil, err
	}
	defer colRows.Close()

	cols := make([]Column, 0)
	for colRows.Next() {
		var c Column
		var nullable string
		if err = colRows.Scan(&c.Name, &c.DBType, &nullable); err != nil {
			return nil, nil, err
		}
		c.Nullable = strings.EqualFold(nullable, "YES")
		c.GoType = mapGoType(c.DBType, c.Nullable)
		cols = append(cols, c)
	}
	if err = colRows.Err(); err != nil {
		return nil, nil, err
	}

	pkRows, err := i.db.QueryContext(ctx, qPK, args...)
	if err != nil {
		return nil, nil, err
	}
	defer pkRows.Close()

	pkSet := make(map[string]bool)
	for pkRows.Next() {
		var pk string
		if err = pkRows.Scan(&pk); err != nil {
			return nil, nil, err
		}
		pkSet[pk] = true
	}
	return cols, pkSet, pkRows.Err()
}

func mapGoType(dbType string, nullable bool) string {
	t := strings.ToLower(dbType)
	var base string
	switch {
	case strings.Contains(t, "bigint"):
		base = "int64"
	case strings.Contains(t, "int"):
		base = "int"
	case strings.Contains(t, "decimal"), strings.Contains(t, "numeric"), strings.Contains(t, "double"), strings.Contains(t, "float"), strings.Contains(t, "real"):
		base = "float64"
	case strings.Contains(t, "bool"):
		base = "bool"
	case strings.Contains(t, "date"), strings.Contains(t, "time"):
		base = "time.Time"
	default:
		base = "string"
	}
	if nullable && base != "string" {
		return "*" + base
	}
	return base
}
