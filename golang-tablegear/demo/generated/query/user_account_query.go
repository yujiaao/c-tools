package query

import (
	corequery "github.com/yujiaao/golang-tablegear/pkg/query"
	"github.com/yujiaao/golang-tablegear/demo/generated/search"
)

type UserAccountFields struct {
	Id corequery.Field[int64]
	Name corequery.Field[string]
	Status corequery.Field[int]
}

var UserAccountField = UserAccountFields{
	Id: corequery.Field[int64]{Name: "id"},
	Name: corequery.Field[string]{Name: "name"},
	Status: corequery.Field[int]{Name: "status"},
}

type UserAccountQuery struct {
	*corequery.QueryBase[UserAccountQuery]
	F UserAccountFields
}

func NewUserAccountQuery() *UserAccountQuery {
	q := &UserAccountQuery{}
	q.QueryBase = corequery.NewQueryBase[UserAccountQuery](q)
	q.F = UserAccountField
	return q
}

func NewUserAccountQueryBySearch(bean *search.UserAccountSearchBean) *UserAccountQuery {
	q := NewUserAccountQuery()
	return q.ApplySearchBean(bean)
}

func (q *UserAccountQuery) ApplySearchBean(bean *search.UserAccountSearchBean) *UserAccountQuery {
	if bean == nil {
		return q
	}
	corequery.ApplySearchBean(q.Builder, bean, map[string]corequery.NamedField{
		"id": q.F.Id,
		"name": q.F.Name,
		"status": q.F.Status,
	})
	return q
}

// Query conditions are intentionally composed with generic predicates
// instead of generating per-column EqXxx/LikeXxx methods.
//
// Example:
//   q := NewUserAccountQuery()
//   q.Where(
//     corequery.Eq(q.F.Id, value),
//   )
//
// String-like example:
//   q.Where(
//     corequery.Like(q.F.Id, "keyword"),
//   )
