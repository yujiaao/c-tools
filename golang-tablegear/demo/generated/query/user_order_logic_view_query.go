package query

import (
	corequery "github.com/yujiaao/golang-tablegear/pkg/query"
	"github.com/yujiaao/golang-tablegear/demo/generated/search"
	"time"
)

type UserOrderLogicViewFields struct {
	UserId corequery.Field[int64]
	UserName corequery.Field[string]
	OrderNo corequery.Field[string]
	Amount corequery.Field[float64]
	CreatedAt corequery.Field[time.Time]
}

var UserOrderLogicViewField = UserOrderLogicViewFields{
	UserId: corequery.Field[int64]{Name: "user_id"},
	UserName: corequery.Field[string]{Name: "user_name"},
	OrderNo: corequery.Field[string]{Name: "order_no"},
	Amount: corequery.Field[float64]{Name: "amount"},
	CreatedAt: corequery.Field[time.Time]{Name: "created_at"},
}

type UserOrderLogicViewQuery struct {
	*corequery.QueryBase[UserOrderLogicViewQuery]
	F UserOrderLogicViewFields
}

func NewUserOrderLogicViewQuery() *UserOrderLogicViewQuery {
	q := &UserOrderLogicViewQuery{}
	q.QueryBase = corequery.NewQueryBase[UserOrderLogicViewQuery](q)
	q.F = UserOrderLogicViewField
	return q
}

func NewUserOrderLogicViewQueryBySearch(bean *search.UserOrderLogicViewSearchBean) *UserOrderLogicViewQuery {
	q := NewUserOrderLogicViewQuery()
	return q.ApplySearchBean(bean)
}

func (q *UserOrderLogicViewQuery) ApplySearchBean(bean *search.UserOrderLogicViewSearchBean) *UserOrderLogicViewQuery {
	if bean == nil {
		return q
	}
	corequery.ApplySearchBean(q.Builder, bean, map[string]corequery.NamedField{
		"user_id": q.F.UserId,
		"user_name": q.F.UserName,
		"order_no": q.F.OrderNo,
		"amount": q.F.Amount,
		"created_at": q.F.CreatedAt,
	})
	return q
}

// Query conditions are intentionally composed with generic predicates
// instead of generating per-column EqXxx/LikeXxx methods.
//
// Example:
//   q := NewUserOrderLogicViewQuery()
//   q.Where(
//     corequery.Eq(q.F.UserId, value),
//   )
//
// String-like example:
//   q.Where(
//     corequery.Like(q.F.UserId, "keyword"),
//   )
