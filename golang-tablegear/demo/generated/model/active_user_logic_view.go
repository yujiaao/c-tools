package model

// ActiveUserLogicView maps table active_user_logic_view.
type ActiveUserLogicView struct {
	Id int64 `db:"id" json:"id"`
	Name string `db:"name" json:"name"`
	Status int `db:"status" json:"status"`
}
