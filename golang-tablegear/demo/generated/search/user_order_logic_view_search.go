package search
import "time"

type UserOrderLogicViewSearchBean struct {
	Start  int    `json:"start"`
	Count  int    `json:"count"`
	SortBy string `json:"sortBy"`
	UserId *int64 `json:"user_id"`
	BeginUserId *int64 `json:"begin_user_id"`
	EndUserId *int64 `json:"end_user_id"`
	UserName *string `json:"user_name"`
	BeginUserName *string `json:"begin_user_name"`
	EndUserName *string `json:"end_user_name"`
	OrderNo *string `json:"order_no"`
	BeginOrderNo *string `json:"begin_order_no"`
	EndOrderNo *string `json:"end_order_no"`
	Amount *float64 `json:"amount"`
	BeginAmount *float64 `json:"begin_amount"`
	EndAmount *float64 `json:"end_amount"`
	CreatedAt *time.Time `json:"created_at"`
	BeginCreatedAt *time.Time `json:"begin_created_at"`
	EndCreatedAt *time.Time `json:"end_created_at"`
}
