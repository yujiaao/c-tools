package runtime

import (
	"errors"
	"fmt"
	"strings"
)

type Dialect interface {
	Name() string
	Placeholder(idx int) string
	Paginate(baseSQL string, limit, offset int) string
	Upsert(table string, columns, conflictColumns, updateColumns []string) (string, error)
}

func NewDialect(driver string) (Dialect, error) {
	switch strings.ToLower(driver) {
	case "mysql":
		return mysqlDialect{}, nil
	case "postgres", "postgresql":
		return postgresDialect{}, nil
	default:
		return nil, fmt.Errorf("unsupported driver: %s", driver)
	}
}

type mysqlDialect struct{}

func (mysqlDialect) Name() string { return "mysql" }

func (mysqlDialect) Placeholder(_ int) string { return "?" }

func (m mysqlDialect) Paginate(baseSQL string, limit, offset int) string {
	return fmt.Sprintf("%s LIMIT %d OFFSET %d", baseSQL, limit, offset)
}

func (m mysqlDialect) Upsert(table string, columns, _ []string, updateColumns []string) (string, error) {
	if len(columns) == 0 {
		return "", errors.New("columns is empty")
	}
	if len(updateColumns) == 0 {
		return "", errors.New("updateColumns is empty")
	}
	colList := strings.Join(columns, ", ")
	values := strings.TrimRight(strings.Repeat("?, ", len(columns)), ", ")
	updates := make([]string, 0, len(updateColumns))
	for _, c := range updateColumns {
		updates = append(updates, fmt.Sprintf("%s=VALUES(%s)", c, c))
	}
	return fmt.Sprintf(
		"INSERT INTO %s (%s) VALUES (%s) ON DUPLICATE KEY UPDATE %s",
		table,
		colList,
		values,
		strings.Join(updates, ", "),
	), nil
}

type postgresDialect struct{}

func (postgresDialect) Name() string { return "postgres" }

func (postgresDialect) Placeholder(idx int) string { return fmt.Sprintf("$%d", idx) }

func (p postgresDialect) Paginate(baseSQL string, limit, offset int) string {
	return fmt.Sprintf("%s LIMIT %d OFFSET %d", baseSQL, limit, offset)
}

func (p postgresDialect) Upsert(table string, columns, conflictColumns, updateColumns []string) (string, error) {
	if len(columns) == 0 {
		return "", errors.New("columns is empty")
	}
	if len(conflictColumns) == 0 {
		return "", errors.New("conflictColumns is empty for postgres upsert")
	}
	if len(updateColumns) == 0 {
		return "", errors.New("updateColumns is empty")
	}
	colList := strings.Join(columns, ", ")
	values := make([]string, 0, len(columns))
	for i := range columns {
		values = append(values, p.Placeholder(i+1))
	}
	updates := make([]string, 0, len(updateColumns))
	for _, c := range updateColumns {
		updates = append(updates, fmt.Sprintf("%s=EXCLUDED.%s", c, c))
	}
	return fmt.Sprintf(
		"INSERT INTO %s (%s) VALUES (%s) ON CONFLICT (%s) DO UPDATE SET %s",
		table,
		colList,
		strings.Join(values, ", "),
		strings.Join(conflictColumns, ", "),
		strings.Join(updates, ", "),
	), nil
}
