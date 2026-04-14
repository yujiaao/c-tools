package repository

import (
	"context"
	"database/sql"

	"github.com/yujiaao/golang-tablegear/demo/generated/model"
	"github.com/yujiaao/golang-tablegear/demo/generated/query"
	"github.com/yujiaao/golang-tablegear/demo/generated/search"
	"github.com/yujiaao/golang-tablegear/pkg/runtime"
)

// ActiveUserLogicViewRepository provides basic CRUD methods.
type ActiveUserLogicViewRepository struct {
	*runtime.GenericRepository[model.ActiveUserLogicView]
}

func NewActiveUserLogicViewRepository(db *runtime.DB) (*ActiveUserLogicViewRepository, error) {
	d, err := runtime.NewDialect("mysql")
	if err != nil {
		return nil, err
	}
	base := runtime.NewGenericRepository[model.ActiveUserLogicView](db, d, &ActiveUserLogicViewAdapter{})
	return &ActiveUserLogicViewRepository{GenericRepository: base}, nil
}

type ActiveUserLogicViewAdapter struct{}

func (a *ActiveUserLogicViewAdapter) TableName() string {
	return "(" + "SELECT\r\n  id,\r\n  name,\r\n  status\r\nFROM user_account\r\nWHERE status = 1" + ") AS active_user_logic_view"
}

func (a *ActiveUserLogicViewAdapter) Columns() []string {
	return []string{
		"id",
		"name",
		"status",
	}
}

func (a *ActiveUserLogicViewAdapter) PrimaryKeyColumn() string {
	return "id"
}

func (a *ActiveUserLogicViewAdapter) IsReadOnly() bool {
	return true
}

func (a *ActiveUserLogicViewAdapter) NewEntity() *model.ActiveUserLogicView {
	return &model.ActiveUserLogicView{}
}

func (a *ActiveUserLogicViewAdapter) ScanRow(rows *sql.Rows, item *model.ActiveUserLogicView) error {
	return rows.Scan(
		&item.Id,
		&item.Name,
		&item.Status,
	)
}

func (a *ActiveUserLogicViewAdapter) InsertArgs(item *model.ActiveUserLogicView) []any {
	return []any{
		item.Id,
		item.Name,
		item.Status,
	}
}

func (r *ActiveUserLogicViewRepository) ListBySearchBean(ctx context.Context, bean *search.ActiveUserLogicViewSearchBean) ([]*model.ActiveUserLogicView, error) {
	return r.ListByQuery(ctx, query.NewActiveUserLogicViewQueryBySearch(bean))
}

func (r *ActiveUserLogicViewRepository) CountBySearchBean(ctx context.Context, bean *search.ActiveUserLogicViewSearchBean) (int64, error) {
	return r.CountByQuery(ctx, query.NewActiveUserLogicViewQueryBySearch(bean))
}

func (r *ActiveUserLogicViewRepository) FindOneBySearchBean(ctx context.Context, bean *search.ActiveUserLogicViewSearchBean) (*model.ActiveUserLogicView, error) {
	return r.FindOneByQuery(ctx, query.NewActiveUserLogicViewQueryBySearch(bean))
}

