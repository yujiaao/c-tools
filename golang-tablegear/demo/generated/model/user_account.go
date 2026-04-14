package model

// UserAccount maps table user_account.
type UserAccount struct {
	Id int64 `db:"id" json:"id"`
	Name string `db:"name" json:"name"`
	Status int `db:"status" json:"status"`
}
