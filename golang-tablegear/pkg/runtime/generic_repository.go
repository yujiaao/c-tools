package runtime

import (
	"context"
	"database/sql"
	"fmt"
	"strings"
)

// QueryBuilder is the minimal DSL contract used by repositories.
type QueryBuilder interface {
	BuildWhere() (string, []any)
	BuildOrder() string
	Page() (limit, offset int, hasLimit bool)
}

// EntityAdapter bridges generated model code to generic repository logic.
type EntityAdapter[T any] interface {
	TableName() string
	Columns() []string
	PrimaryKeyColumn() string
	IsReadOnly() bool
	NewEntity() *T
	ScanRow(rows *sql.Rows, item *T) error
	InsertArgs(item *T) []any
}

// GenericRepository provides reusable CRUD/query operations.
type GenericRepository[T any] struct {
	db      *DB
	dialect Dialect
	adapter EntityAdapter[T]
}

type pageOnlyQuery struct {
	limit  int
	offset int
}

func (p pageOnlyQuery) BuildWhere() (string, []any) { return "", nil }
func (p pageOnlyQuery) BuildOrder() string          { return "" }
func (p pageOnlyQuery) Page() (int, int, bool)      { return p.limit, p.offset, true }

func NewGenericRepository[T any](db *DB, dialect Dialect, adapter EntityAdapter[T]) *GenericRepository[T] {
	return &GenericRepository[T]{
		db:      db,
		dialect: dialect,
		adapter: adapter,
	}
}

func (r *GenericRepository[T]) BuildInsertSQL() string {
	cols := r.adapter.Columns()
	holders := make([]string, 0, len(cols))
	for i := range cols {
		holders = append(holders, r.dialect.Placeholder(i+1))
	}
	return fmt.Sprintf(
		"INSERT INTO %s (%s) VALUES (%s)",
		r.adapter.TableName(),
		strings.Join(cols, ", "),
		strings.Join(holders, ", "),
	)
}

func (r *GenericRepository[T]) BuildUpsertSQL() (string, error) {
	if r.adapter.IsReadOnly() {
		return "", fmt.Errorf("upsert is not supported for readonly adapter")
	}
	cols := r.adapter.Columns()
	pk := r.adapter.PrimaryKeyColumn()
	if pk == "" {
		return "", fmt.Errorf("primary key column is empty")
	}
	updateCols := make([]string, 0, len(cols))
	for _, c := range cols {
		if c == pk {
			continue
		}
		updateCols = append(updateCols, c)
	}
	if len(updateCols) == 0 {
		return "", fmt.Errorf("no updatable columns")
	}
	return r.dialect.Upsert(r.adapter.TableName(), cols, []string{pk}, updateCols)
}

func (r *GenericRepository[T]) Insert(ctx context.Context, item *T) error {
	if r.adapter.IsReadOnly() {
		return fmt.Errorf("insert is not supported for readonly adapter")
	}
	if item == nil {
		return fmt.Errorf("item is nil")
	}
	_, err := r.db.ExecContext(ctx, r.BuildInsertSQL(), r.adapter.InsertArgs(item)...)
	return err
}

func (r *GenericRepository[T]) ListPage(ctx context.Context, limit, offset int) error {
	_, err := r.ListByQuery(ctx, pageOnlyQuery{limit: limit, offset: offset})
	return err
}

func (r *GenericRepository[T]) ListByQuery(ctx context.Context, q QueryBuilder) ([]*T, error) {
	base := "SELECT * FROM " + r.adapter.TableName()
	where, args := q.BuildWhere()
	order := q.BuildOrder()
	sqlText := base + where + order
	limit, offset, hasLimit := q.Page()
	if hasLimit {
		sqlText = r.dialect.Paginate(sqlText, limit, offset)
	}
	rows, err := r.db.QueryContext(ctx, sqlText, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := make([]*T, 0, 16)
	for rows.Next() {
		item := r.adapter.NewEntity()
		if err = r.adapter.ScanRow(rows, item); err != nil {
			return nil, err
		}
		items = append(items, item)
	}
	if err = rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

func (r *GenericRepository[T]) CountByQuery(ctx context.Context, q QueryBuilder) (int64, error) {
	base := "SELECT COUNT(*) FROM " + r.adapter.TableName()
	where, args := q.BuildWhere()
	row := r.db.QueryRowContext(ctx, base+where, args...)
	var total int64
	if err := row.Scan(&total); err != nil {
		return 0, err
	}
	return total, nil
}

func (r *GenericRepository[T]) FindOneByQuery(ctx context.Context, q QueryBuilder) (*T, error) {
	base := "SELECT * FROM " + r.adapter.TableName()
	where, args := q.BuildWhere()
	order := q.BuildOrder()
	sqlText := base + where + order
	limit, offset, hasLimit := q.Page()
	if !hasLimit {
		limit, offset = 1, 0
	}
	sqlText = r.dialect.Paginate(sqlText, limit, offset)
	rows, err := r.db.QueryContext(ctx, sqlText, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	if rows.Next() {
		item := r.adapter.NewEntity()
		if err = r.adapter.ScanRow(rows, item); err != nil {
			return nil, err
		}
		return item, nil
	}
	return nil, nil
}
