package query

import (
	corequery "github.com/yujiaao/golang-tablegear/pkg/query"
	"github.com/yujiaao/golang-tablegear/demo/generated/search"
)

type ActiveUserLogicViewFields struct {
	Id corequery.Field[int64]
	Name corequery.Field[string]
	Status corequery.Field[int]
}

var ActiveUserLogicViewField = ActiveUserLogicViewFields{
	Id: corequery.Field[int64]{Name: "id"},
	Name: corequery.Field[string]{Name: "name"},
	Status: corequery.Field[int]{Name: "status"},
}

type ActiveUserLogicViewQuery struct {
	*corequery.QueryBase[ActiveUserLogicViewQuery]
	F ActiveUserLogicViewFields
}

func NewActiveUserLogicViewQuery() *ActiveUserLogicViewQuery {
	q := &ActiveUserLogicViewQuery{}
	q.QueryBase = corequery.NewQueryBase[ActiveUserLogicViewQuery](q)
	q.F = ActiveUserLogicViewField
	return q
}

func NewActiveUserLogicViewQueryBySearch(bean *search.ActiveUserLogicViewSearchBean) *ActiveUserLogicViewQuery {
	q := NewActiveUserLogicViewQuery()
	return q.ApplySearchBean(bean)
}

func (q *ActiveUserLogicViewQuery) ApplySearchBean(bean *search.ActiveUserLogicViewSearchBean) *ActiveUserLogicViewQuery {
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
//   q := NewActiveUserLogicViewQuery()
//   q.Where(
//     corequery.Eq(q.F.Id, value),
//   )
//
// String-like example:
//   q.Where(
//     corequery.Like(q.F.Id, "keyword"),
//   )
