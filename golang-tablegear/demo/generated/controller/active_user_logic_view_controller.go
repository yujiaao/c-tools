package controller

import (
	"encoding/json"
	"net/http"
	"strings"
	"github.com/yujiaao/golang-tablegear/demo/generated/repository"
	"github.com/yujiaao/golang-tablegear/demo/generated/search"
)

type ActiveUserLogicViewManager struct {
	repo *repository.ActiveUserLogicViewRepository
}

func RegisterActiveUserLogicViewManagerRoutes(mux *http.ServeMux, repo *repository.ActiveUserLogicViewRepository) *ActiveUserLogicViewManager {
	m := &ActiveUserLogicViewManager{repo: repo}
	base := "/admin/active_user_logic_view"
	mux.HandleFunc(base+"/list", m.List)
	mux.HandleFunc(base+"/count", m.Count)
	mux.HandleFunc(base+"/one", m.One)
	return m
}

// List godoc
// @Summary List active_user_logic_view
// @Tags admin-active_user_logic_view
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.ActiveUserLogicViewSearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/active_user_logic_view/list [post]
func (m *ActiveUserLogicViewManager) List(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	bean, err := decodeActiveUserLogicViewSearchBean(r)
	if err != nil {
		writeJSONError(w, CodeInvalidParam, err, http.StatusBadRequest)
		return
	}
	ctx := routeDBContext(r.Context(), r.Header.Get("X-DB-Role"))
	items, err := m.repo.ListBySearchBean(ctx, bean)
	if err != nil {
		writeJSONError(w, CodeDBError, err, http.StatusInternalServerError)
		return
	}
	total, err := m.repo.CountBySearchBean(ctx, bean)
	if err != nil {
		writeJSONError(w, CodeDBError, err, http.StatusInternalServerError)
		return
	}
	page := NewPageMeta(bean.Start, bean.Count, total)
	writeJSONOK(w, NewPageData(items, total, page))
}

// Count godoc
// @Summary Count active_user_logic_view
// @Tags admin-active_user_logic_view
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.ActiveUserLogicViewSearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/active_user_logic_view/count [post]
func (m *ActiveUserLogicViewManager) Count(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	bean, err := decodeActiveUserLogicViewSearchBean(r)
	if err != nil {
		writeJSONError(w, CodeInvalidParam, err, http.StatusBadRequest)
		return
	}
	ctx := routeDBContext(r.Context(), r.Header.Get("X-DB-Role"))
	total, err := m.repo.CountBySearchBean(ctx, bean)
	if err != nil {
		writeJSONError(w, CodeDBError, err, http.StatusInternalServerError)
		return
	}
	writeJSONOK(w, map[string]int64{"total": total})
}

// One godoc
// @Summary Find one active_user_logic_view
// @Tags admin-active_user_logic_view
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.ActiveUserLogicViewSearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/active_user_logic_view/one [post]
func (m *ActiveUserLogicViewManager) One(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	bean, err := decodeActiveUserLogicViewSearchBean(r)
	if err != nil {
		writeJSONError(w, CodeInvalidParam, err, http.StatusBadRequest)
		return
	}
	ctx := routeDBContext(r.Context(), r.Header.Get("X-DB-Role"))
	one, err := m.repo.FindOneBySearchBean(ctx, bean)
	if err != nil {
		writeJSONError(w, CodeDBError, err, http.StatusInternalServerError)
		return
	}
	writeJSONOK(w, one)
}

func decodeActiveUserLogicViewSearchBean(r *http.Request) (*search.ActiveUserLogicViewSearchBean, error) {
	bean := &search.ActiveUserLogicViewSearchBean{}
	if r.Body == nil {
		return bean, nil
	}
	if err := json.NewDecoder(r.Body).Decode(bean); err != nil {
		if strings.Contains(err.Error(), "EOF") {
			return bean, nil
		}
		return nil, err
	}
	return bean, nil
}
