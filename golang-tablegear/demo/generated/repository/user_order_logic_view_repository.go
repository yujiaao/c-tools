package repository

import (
	"context"
	"database/sql"

	"github.com/yujiaao/golang-tablegear/demo/generated/model"
	"github.com/yujiaao/golang-tablegear/demo/generated/query"
	"github.com/yujiaao/golang-tablegear/demo/generated/search"
	"github.com/yujiaao/golang-tablegear/pkg/runtime"
)

// UserOrderLogicViewRepository provides basic CRUD methods.
type UserOrderLogicViewRepository struct {
	*runtime.GenericRepository[model.UserOrderLogicView]
}

func NewUserOrderLogicViewRepository(db *runtime.DB) (*UserOrderLogicViewRepository, error) {
	d, err := runtime.NewDialect("mysql")
	if err != nil {
		return nil, err
	}
	base := runtime.NewGenericRepository[model.UserOrderLogicView](db, d, &UserOrderLogicViewAdapter{})
	return &UserOrderLogicViewRepository{GenericRepository: base}, nil
}

type UserOrderLogicViewAdapter struct{}

func (a *UserOrderLogicViewAdapter) TableName() string {
	return "(" + "SELECT\r\n  u.id AS user_id,\r\n  u.name AS user_name,\r\n  o.order_no,\r\n  o.amount,\r\n  o.created_at\r\nFROM user_account u\r\nINNER JOIN order_info o ON o.user_id = u.id\r\nWHERE u.status = 1" + ") AS user_order_logic_view"
}

func (a *UserOrderLogicViewAdapter) Columns() []string {
	return []string{
		"user_id",
		"user_name",
		"order_no",
		"amount",
		"created_at",
	}
}

func (a *UserOrderLogicViewAdapter) PrimaryKeyColumn() string {
	return "user_id"
}

func (a *UserOrderLogicViewAdapter) IsReadOnly() bool {
	return true
}

func (a *UserOrderLogicViewAdapter) NewEntity() *model.UserOrderLogicView {
	return &model.UserOrderLogicView{}
}

func (a *UserOrderLogicViewAdapter) ScanRow(rows *sql.Rows, item *model.UserOrderLogicView) error {
	return rows.Scan(
		&item.UserId,
		&item.UserName,
		&item.OrderNo,
		&item.Amount,
		&item.CreatedAt,
	)
}

func (a *UserOrderLogicViewAdapter) InsertArgs(item *model.UserOrderLogicView) []any {
	return []any{
		item.UserId,
		item.UserName,
		item.OrderNo,
		item.Amount,
		item.CreatedAt,
	}
}

func (r *UserOrderLogicViewRepository) ListBySearchBean(ctx context.Context, bean *search.UserOrderLogicViewSearchBean) ([]*model.UserOrderLogicView, error) {
	return r.ListByQuery(ctx, query.NewUserOrderLogicViewQueryBySearch(bean))
}

func (r *UserOrderLogicViewRepository) CountBySearchBean(ctx context.Context, bean *search.UserOrderLogicViewSearchBean) (int64, error) {
	return r.CountByQuery(ctx, query.NewUserOrderLogicViewQueryBySearch(bean))
}

func (r *UserOrderLogicViewRepository) FindOneBySearchBean(ctx context.Context, bean *search.UserOrderLogicViewSearchBean) (*model.UserOrderLogicView, error) {
	return r.FindOneByQuery(ctx, query.NewUserOrderLogicViewQueryBySearch(bean))
}

