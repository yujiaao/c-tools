package model

import "time"

// UserOrderLogicView maps table user_order_logic_view.
type UserOrderLogicView struct {
	UserId int64 `db:"user_id" json:"user_id"`
	UserName string `db:"user_name" json:"user_name"`
	OrderNo string `db:"order_no" json:"order_no"`
	Amount float64 `db:"amount" json:"amount"`
	CreatedAt time.Time `db:"created_at" json:"created_at"`
}
