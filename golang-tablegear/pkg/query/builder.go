package query

import (
	"fmt"
	"reflect"
	"strings"
)

type Field[T any] struct {
	Name string
}

func (f Field[T]) Column() string {
	return f.Name
}

type NamedField interface {
	Column() string
}

type Predicate struct {
	SQL  string
	Args []any
}

type Order struct {
	Field string
	Desc  bool
}

type Builder struct {
	predicates []Predicate
	orders     []Order
	limit      int
	offset     int
	hasLimit   bool
}

func New() *Builder {
	return &Builder{}
}

func (b *Builder) Where(parts ...Predicate) *Builder {
	for _, p := range parts {
		if strings.TrimSpace(p.SQL) == "" {
			continue
		}
		b.predicates = append(b.predicates, p)
	}
	return b
}

func Eq[T any](field Field[T], value T) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s = ?", field.Name), Args: []any{value}}
}

func EqField(field string, value any) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s = ?", field), Args: []any{value}}
}

func Ne[T any](field Field[T], value T) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s <> ?", field.Name), Args: []any{value}}
}

func Gt[T any](field Field[T], value T) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s > ?", field.Name), Args: []any{value}}
}

func Ge[T any](field Field[T], value T) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s >= ?", field.Name), Args: []any{value}}
}

func GeField(field string, value any) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s >= ?", field), Args: []any{value}}
}

func Lt[T any](field Field[T], value T) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s < ?", field.Name), Args: []any{value}}
}

func Le[T any](field Field[T], value T) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s <= ?", field.Name), Args: []any{value}}
}

func LeField(field string, value any) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s <= ?", field), Args: []any{value}}
}

func Like(field Field[string], value string) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s LIKE ?", field.Name), Args: []any{"%" + value + "%"}}
}

func LikeField(field string, value string) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s LIKE ?", field), Args: []any{"%" + value + "%"}}
}

func In[T any](field Field[T], values ...T) Predicate {
	if len(values) == 0 {
		return Predicate{}
	}
	holders := strings.TrimRight(strings.Repeat("?,", len(values)), ",")
	args := make([]any, 0, len(values))
	for _, v := range values {
		args = append(args, v)
	}
	return Predicate{
		SQL:  fmt.Sprintf("%s IN (%s)", field.Name, holders),
		Args: args,
	}
}

func InField(field string, values ...any) Predicate {
	if len(values) == 0 {
		return Predicate{}
	}
	holders := strings.TrimRight(strings.Repeat("?,", len(values)), ",")
	return Predicate{
		SQL:  fmt.Sprintf("%s IN (%s)", field, holders),
		Args: values,
	}
}

func NotIn[T any](field Field[T], values ...T) Predicate {
	if len(values) == 0 {
		return Predicate{}
	}
	holders := strings.TrimRight(strings.Repeat("?,", len(values)), ",")
	args := make([]any, 0, len(values))
	for _, v := range values {
		args = append(args, v)
	}
	return Predicate{
		SQL:  fmt.Sprintf("%s NOT IN (%s)", field.Name, holders),
		Args: args,
	}
}

func NotInField(field string, values ...any) Predicate {
	if len(values) == 0 {
		return Predicate{}
	}
	holders := strings.TrimRight(strings.Repeat("?,", len(values)), ",")
	return Predicate{
		SQL:  fmt.Sprintf("%s NOT IN (%s)", field, holders),
		Args: values,
	}
}

func Between[T any](field Field[T], left, right T) Predicate {
	return Predicate{
		SQL:  fmt.Sprintf("%s BETWEEN ? AND ?", field.Name),
		Args: []any{left, right},
	}
}

func BetweenField(field string, left, right any) Predicate {
	return Predicate{
		SQL:  fmt.Sprintf("%s BETWEEN ? AND ?", field),
		Args: []any{left, right},
	}
}

func NotBetween[T any](field Field[T], left, right T) Predicate {
	return Predicate{
		SQL:  fmt.Sprintf("%s NOT BETWEEN ? AND ?", field.Name),
		Args: []any{left, right},
	}
}

func NotBetweenField(field string, left, right any) Predicate {
	return Predicate{
		SQL:  fmt.Sprintf("%s NOT BETWEEN ? AND ?", field),
		Args: []any{left, right},
	}
}

func Or(parts ...Predicate) Predicate {
	return joinPredicates("OR", parts...)
}

func AndGroup(parts ...Predicate) Predicate {
	return joinPredicates("AND", parts...)
}

func Not(part Predicate) Predicate {
	if strings.TrimSpace(part.SQL) == "" {
		return Predicate{}
	}
	return Predicate{
		SQL:  "NOT (" + part.SQL + ")",
		Args: append([]any(nil), part.Args...),
	}
}

func IsNull[T any](field Field[T]) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s IS NULL", field.Name)}
}

func IsNotNull[T any](field Field[T]) Predicate {
	return Predicate{SQL: fmt.Sprintf("%s IS NOT NULL", field.Name)}
}

func (b *Builder) OrderByAsc(field NamedField) *Builder {
	b.orders = append(b.orders, Order{Field: field.Column()})
	return b
}

func (b *Builder) OrderByDesc(field NamedField) *Builder {
	b.orders = append(b.orders, Order{Field: field.Column(), Desc: true})
	return b
}

func (b *Builder) Limit(limit int) *Builder {
	b.limit = limit
	b.hasLimit = true
	return b
}

func (b *Builder) Offset(offset int) *Builder {
	b.offset = offset
	return b
}

func (b *Builder) BuildWhere() (string, []any) {
	if len(b.predicates) == 0 {
		return "", nil
	}
	parts := make([]string, 0, len(b.predicates))
	args := make([]any, 0, len(b.predicates)*2)
	for _, p := range b.predicates {
		parts = append(parts, p.SQL)
		args = append(args, p.Args...)
	}
	return " WHERE " + strings.Join(parts, " AND "), args
}

func (b *Builder) BuildOrder() string {
	if len(b.orders) == 0 {
		return ""
	}
	parts := make([]string, 0, len(b.orders))
	for _, o := range b.orders {
		if o.Desc {
			parts = append(parts, o.Field+" DESC")
		} else {
			parts = append(parts, o.Field+" ASC")
		}
	}
	return " ORDER BY " + strings.Join(parts, ", ")
}

func (b *Builder) Page() (limit, offset int, hasLimit bool) {
	return b.limit, b.offset, b.hasLimit
}

func asAnySlice(v reflect.Value) []any {
	out := make([]any, 0, v.Len())
	for i := 0; i < v.Len(); i++ {
		out = append(out, v.Index(i).Interface())
	}
	return out
}

func joinPredicates(op string, parts ...Predicate) Predicate {
	sqlParts := make([]string, 0, len(parts))
	args := make([]any, 0, len(parts)*2)
	for _, p := range parts {
		if strings.TrimSpace(p.SQL) == "" {
			continue
		}
		sqlParts = append(sqlParts, "("+p.SQL+")")
		args = append(args, p.Args...)
	}
	if len(sqlParts) == 0 {
		return Predicate{}
	}
	return Predicate{
		SQL:  "(" + strings.Join(sqlParts, " "+op+" ") + ")",
		Args: args,
	}
}
