package controller

import (
	"encoding/json"
	"net/http"
	"strings"
	"github.com/yujiaao/golang-tablegear/demo/generated/model"
	"github.com/yujiaao/golang-tablegear/demo/generated/repository"
	"github.com/yujiaao/golang-tablegear/demo/generated/search"
)

type UserAccountManager struct {
	repo *repository.UserAccountRepository
}

func RegisterUserAccountManagerRoutes(mux *http.ServeMux, repo *repository.UserAccountRepository) *UserAccountManager {
	m := &UserAccountManager{repo: repo}
	base := "/admin/user_account"
	mux.HandleFunc(base+"/list", m.List)
	mux.HandleFunc(base+"/count", m.Count)
	mux.HandleFunc(base+"/one", m.One)
	mux.HandleFunc(base+"/create", m.Create)
	return m
}

// List godoc
// @Summary List user_account
// @Tags admin-user_account
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.UserAccountSearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/user_account/list [post]
func (m *UserAccountManager) List(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	bean, err := decodeUserAccountSearchBean(r)
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
// @Summary Count user_account
// @Tags admin-user_account
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.UserAccountSearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/user_account/count [post]
func (m *UserAccountManager) Count(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	bean, err := decodeUserAccountSearchBean(r)
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
// @Summary Find one user_account
// @Tags admin-user_account
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.UserAccountSearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/user_account/one [post]
func (m *UserAccountManager) One(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	bean, err := decodeUserAccountSearchBean(r)
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
// Create godoc
// @Summary Create user_account
// @Tags admin-user_account
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body model.UserAccount true "entity"
// @Success 200 {object} APIResponse
// @Router /admin/user_account/create [post]
func (m *UserAccountManager) Create(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	var item model.UserAccount
	if err := json.NewDecoder(r.Body).Decode(&item); err != nil {
		writeJSONError(w, CodeInvalidParam, err, http.StatusBadRequest)
		return
	}
	ctx := routeDBContext(r.Context(), r.Header.Get("X-DB-Role"))
	err := m.repo.Insert(ctx, &item)
	if err != nil {
		writeJSONError(w, CodeDBError, err, http.StatusInternalServerError)
		return
	}
	writeJSONOK(w, map[string]bool{"ok": true})
}

func decodeUserAccountSearchBean(r *http.Request) (*search.UserAccountSearchBean, error) {
	bean := &search.UserAccountSearchBean{}
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
