package repository

import (
	"context"
	"database/sql"

	"github.com/yujiaao/golang-tablegear/demo/generated/model"
	"github.com/yujiaao/golang-tablegear/demo/generated/query"
	"github.com/yujiaao/golang-tablegear/demo/generated/search"
	"github.com/yujiaao/golang-tablegear/pkg/runtime"
)

// UserAccountRepository provides basic CRUD methods.
type UserAccountRepository struct {
	*runtime.GenericRepository[model.UserAccount]
}

func NewUserAccountRepository(db *runtime.DB) (*UserAccountRepository, error) {
	d, err := runtime.NewDialect("mysql")
	if err != nil {
		return nil, err
	}
	base := runtime.NewGenericRepository[model.UserAccount](db, d, &UserAccountAdapter{})
	return &UserAccountRepository{GenericRepository: base}, nil
}

type UserAccountAdapter struct{}

func (a *UserAccountAdapter) TableName() string {
	return "user_account"
}

func (a *UserAccountAdapter) Columns() []string {
	return []string{
		"id",
		"name",
		"status",
	}
}

func (a *UserAccountAdapter) PrimaryKeyColumn() string {
	return "id"
}

func (a *UserAccountAdapter) IsReadOnly() bool {
	return false
}

func (a *UserAccountAdapter) NewEntity() *model.UserAccount {
	return &model.UserAccount{}
}

func (a *UserAccountAdapter) ScanRow(rows *sql.Rows, item *model.UserAccount) error {
	return rows.Scan(
		&item.Id,
		&item.Name,
		&item.Status,
	)
}

func (a *UserAccountAdapter) InsertArgs(item *model.UserAccount) []any {
	return []any{
		item.Id,
		item.Name,
		item.Status,
	}
}

func (r *UserAccountRepository) ListBySearchBean(ctx context.Context, bean *search.UserAccountSearchBean) ([]*model.UserAccount, error) {
	return r.ListByQuery(ctx, query.NewUserAccountQueryBySearch(bean))
}

func (r *UserAccountRepository) CountBySearchBean(ctx context.Context, bean *search.UserAccountSearchBean) (int64, error) {
	return r.CountByQuery(ctx, query.NewUserAccountQueryBySearch(bean))
}

func (r *UserAccountRepository) FindOneBySearchBean(ctx context.Context, bean *search.UserAccountSearchBean) (*model.UserAccount, error) {
	return r.FindOneByQuery(ctx, query.NewUserAccountQueryBySearch(bean))
}

