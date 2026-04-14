package generator

const modelTemplate = `package {{.PackageName}}

{{if .NeedTime}}import "time"

{{end}}// {{.ClassName}} maps table {{.TableName}}.
type {{.ClassName}} struct {
{{- range .Columns}}
	{{.Name}} {{.GoType}} ` + "`db:\"{{.ColumnName}}\" json:\"{{.ColumnName}}\"`" + `
{{- end}}
}
`

const repositoryTemplate = `package {{.PackageName}}

import (
	"context"
	"database/sql"

	"{{.ModelImport}}"
	"{{.QueryImport}}"
	"{{.SearchImport}}"
	"{{.RuntimeImport}}"
)

// {{.ClassName}}Repository provides basic CRUD methods.
type {{.ClassName}}Repository struct {
	*runtime.GenericRepository[model.{{.ClassName}}]
}

func New{{.ClassName}}Repository(db *runtime.DB) (*{{.ClassName}}Repository, error) {
	d, err := runtime.NewDialect("{{.DialectName}}")
	if err != nil {
		return nil, err
	}
	base := runtime.NewGenericRepository[model.{{.ClassName}}](db, d, &{{.ClassName}}Adapter{})
	return &{{.ClassName}}Repository{GenericRepository: base}, nil
}

type {{.ClassName}}Adapter struct{}

func (a *{{.ClassName}}Adapter) TableName() string {
{{- if .IsLogicalView}}
	return "(" + {{.CoreSQLLiteral}} + ") AS {{.TableName}}"
{{- else}}
	return "{{.TableName}}"
{{- end}}
}

func (a *{{.ClassName}}Adapter) Columns() []string {
	return []string{
{{- range .Columns}}
		"{{.ColumnName}}",
{{- end}}
	}
}

func (a *{{.ClassName}}Adapter) PrimaryKeyColumn() string {
	return "{{.PrimaryKey.ColumnName}}"
}

func (a *{{.ClassName}}Adapter) IsReadOnly() bool {
{{- if .ReadOnly}}
	return true
{{- else}}
	return false
{{- end}}
}

func (a *{{.ClassName}}Adapter) NewEntity() *model.{{.ClassName}} {
	return &model.{{.ClassName}}{}
}

func (a *{{.ClassName}}Adapter) ScanRow(rows *sql.Rows, item *model.{{.ClassName}}) error {
	return rows.Scan(
{{- range .Columns}}
		&item.{{.Name}},
{{- end}}
	)
}

func (a *{{.ClassName}}Adapter) InsertArgs(item *model.{{.ClassName}}) []any {
	return []any{
{{- range .Columns}}
		item.{{.Name}},
{{- end}}
	}
}

func (r *{{.ClassName}}Repository) ListBySearchBean(ctx context.Context, bean *search.{{.ClassName}}SearchBean) ([]*model.{{.ClassName}}, error) {
	return r.ListByQuery(ctx, query.New{{.ClassName}}QueryBySearch(bean))
}

func (r *{{.ClassName}}Repository) CountBySearchBean(ctx context.Context, bean *search.{{.ClassName}}SearchBean) (int64, error) {
	return r.CountByQuery(ctx, query.New{{.ClassName}}QueryBySearch(bean))
}

func (r *{{.ClassName}}Repository) FindOneBySearchBean(ctx context.Context, bean *search.{{.ClassName}}SearchBean) (*model.{{.ClassName}}, error) {
	return r.FindOneByQuery(ctx, query.New{{.ClassName}}QueryBySearch(bean))
}

`

const queryTemplate = `package {{.PackageName}}

import (
	corequery "{{.QueryCoreImport}}"
	"{{.SearchImport}}"
{{- if .NeedTime}}
	"time"
{{- end}}
)

type {{.ClassName}}Fields struct {
{{- range .Columns}}
	{{.Name}} corequery.Field[{{.GoType}}]
{{- end}}
}

var {{.ClassName}}Field = {{.ClassName}}Fields{
{{- range .Columns}}
	{{.Name}}: corequery.Field[{{.GoType}}]{Name: "{{.ColumnName}}"},
{{- end}}
}

type {{.ClassName}}Query struct {
	*corequery.QueryBase[{{.ClassName}}Query]
	F {{.ClassName}}Fields
}

func New{{.ClassName}}Query() *{{.ClassName}}Query {
	q := &{{.ClassName}}Query{}
	q.QueryBase = corequery.NewQueryBase[{{.ClassName}}Query](q)
	q.F = {{.ClassName}}Field
	return q
}

func New{{.ClassName}}QueryBySearch(bean *search.{{.ClassName}}SearchBean) *{{.ClassName}}Query {
	q := New{{.ClassName}}Query()
	return q.ApplySearchBean(bean)
}

func (q *{{.ClassName}}Query) ApplySearchBean(bean *search.{{.ClassName}}SearchBean) *{{.ClassName}}Query {
	if bean == nil {
		return q
	}
	corequery.ApplySearchBean(q.Builder, bean, map[string]corequery.NamedField{
{{- range .Columns}}
		"{{.ColumnName}}": q.F.{{.Name}},
{{- end}}
	})
	return q
}

// Query conditions are intentionally composed with generic predicates
// instead of generating per-column EqXxx/LikeXxx methods.
//
// Example:
//   q := New{{.ClassName}}Query()
//   q.Where(
//     corequery.Eq(q.F.{{(index .Columns 0).Name}}, value),
//   )
//
// String-like example:
//   q.Where(
//     corequery.Like(q.F.{{(index .Columns 0).Name}}, "keyword"),
//   )
`

const searchTemplate = `package {{.PackageName}}

{{- if .NeedTime}}
import "time"

{{- end}}

type {{.ClassName}}SearchBean struct {
	Start  int    ` + "`json:\"start\"`" + `
	Count  int    ` + "`json:\"count\"`" + `
	SortBy string ` + "`json:\"sortBy\"`" + `
{{- range .Columns}}
	{{.Name}} {{if hasPtr .GoType}}{{.GoType}}{{else}}*{{.GoType}}{{end}} ` + "`json:\"{{.ColumnName}}\"`" + `
	Begin{{.Name}} {{if hasPtr .GoType}}{{.GoType}}{{else}}*{{.GoType}}{{end}} ` + "`json:\"begin_{{.ColumnName}}\"`" + `
	End{{.Name}} {{if hasPtr .GoType}}{{.GoType}}{{else}}*{{.GoType}}{{end}} ` + "`json:\"end_{{.ColumnName}}\"`" + `
{{- end}}
}
`

const controllerStdTemplate = `package {{.PackageName}}

import (
	"encoding/json"
	"net/http"
	"strings"

{{- if not .ReadOnly}}
	"{{.ModelImport}}"
{{- end}}
	"{{.RepoImport}}"
	"{{.SearchImport}}"
)

type {{.ClassName}}Manager struct {
	repo *repository.{{.ClassName}}Repository
}

func Register{{.ClassName}}ManagerRoutes(mux *http.ServeMux, repo *repository.{{.ClassName}}Repository) *{{.ClassName}}Manager {
	m := &{{.ClassName}}Manager{repo: repo}
	base := "/admin/{{.TableName}}"
	mux.HandleFunc(base+"/list", m.List)
	mux.HandleFunc(base+"/count", m.Count)
	mux.HandleFunc(base+"/one", m.One)
{{- if not .ReadOnly}}
	mux.HandleFunc(base+"/create", m.Create)
{{- end}}
	return m
}

// List godoc
// @Summary List {{.TableName}}
// @Tags admin-{{.TableName}}
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.{{.ClassName}}SearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/{{.TableName}}/list [post]
func (m *{{.ClassName}}Manager) List(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	bean, err := decode{{.ClassName}}SearchBean(r)
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
// @Summary Count {{.TableName}}
// @Tags admin-{{.TableName}}
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.{{.ClassName}}SearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/{{.TableName}}/count [post]
func (m *{{.ClassName}}Manager) Count(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	bean, err := decode{{.ClassName}}SearchBean(r)
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
// @Summary Find one {{.TableName}}
// @Tags admin-{{.TableName}}
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.{{.ClassName}}SearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/{{.TableName}}/one [post]
func (m *{{.ClassName}}Manager) One(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	bean, err := decode{{.ClassName}}SearchBean(r)
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

{{- if not .ReadOnly}}
// Create godoc
// @Summary Create {{.TableName}}
// @Tags admin-{{.TableName}}
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body model.{{.ClassName}} true "entity"
// @Success 200 {object} APIResponse
// @Router /admin/{{.TableName}}/create [post]
func (m *{{.ClassName}}Manager) Create(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	var item model.{{.ClassName}}
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
{{- end}}

func decode{{.ClassName}}SearchBean(r *http.Request) (*search.{{.ClassName}}SearchBean, error) {
	bean := &search.{{.ClassName}}SearchBean{}
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
`

const controllerGinTemplate = `package {{.PackageName}}

import (
{{- if not .ReadOnly}}
	"{{.ModelImport}}"
{{- end}}
	"{{.RepoImport}}"
	"{{.SearchImport}}"
	"github.com/gin-gonic/gin"
)

type {{.ClassName}}Manager struct {
	repo *repository.{{.ClassName}}Repository
}

func Register{{.ClassName}}ManagerRoutes(r gin.IRoutes, repo *repository.{{.ClassName}}Repository) *{{.ClassName}}Manager {
	m := &{{.ClassName}}Manager{repo: repo}
	base := "/admin/{{.TableName}}"
	r.POST(base+"/list", m.List)
	r.POST(base+"/count", m.Count)
	r.POST(base+"/one", m.One)
{{- if not .ReadOnly}}
	r.POST(base+"/create", m.Create)
{{- end}}
	return m
}

// List godoc
// @Summary List {{.TableName}}
// @Tags admin-{{.TableName}}
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.{{.ClassName}}SearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/{{.TableName}}/list [post]
func (m *{{.ClassName}}Manager) List(c *gin.Context) {
	bean := &search.{{.ClassName}}SearchBean{}
	if err := c.ShouldBindJSON(bean); err != nil && err.Error() != "EOF" {
		writeJSONErrorGin(c, CodeInvalidParam, err, 400)
		return
	}
	ctx := routeDBContext(c.Request.Context(), c.GetHeader("X-DB-Role"))
	items, err := m.repo.ListBySearchBean(ctx, bean)
	if err != nil {
		writeJSONErrorGin(c, CodeDBError, err, 500)
		return
	}
	total, err := m.repo.CountBySearchBean(ctx, bean)
	if err != nil {
		writeJSONErrorGin(c, CodeDBError, err, 500)
		return
	}
	page := NewPageMeta(bean.Start, bean.Count, total)
	writeJSONOKGin(c, NewPageData(items, total, page))
}

// Count godoc
// @Summary Count {{.TableName}}
// @Tags admin-{{.TableName}}
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.{{.ClassName}}SearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/{{.TableName}}/count [post]
func (m *{{.ClassName}}Manager) Count(c *gin.Context) {
	bean := &search.{{.ClassName}}SearchBean{}
	if err := c.ShouldBindJSON(bean); err != nil && err.Error() != "EOF" {
		writeJSONErrorGin(c, CodeInvalidParam, err, 400)
		return
	}
	ctx := routeDBContext(c.Request.Context(), c.GetHeader("X-DB-Role"))
	total, err := m.repo.CountBySearchBean(ctx, bean)
	if err != nil {
		writeJSONErrorGin(c, CodeDBError, err, 500)
		return
	}
	writeJSONOKGin(c, map[string]int64{"total": total})
}

// One godoc
// @Summary Find one {{.TableName}}
// @Tags admin-{{.TableName}}
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.{{.ClassName}}SearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/{{.TableName}}/one [post]
func (m *{{.ClassName}}Manager) One(c *gin.Context) {
	bean := &search.{{.ClassName}}SearchBean{}
	if err := c.ShouldBindJSON(bean); err != nil && err.Error() != "EOF" {
		writeJSONErrorGin(c, CodeInvalidParam, err, 400)
		return
	}
	ctx := routeDBContext(c.Request.Context(), c.GetHeader("X-DB-Role"))
	one, err := m.repo.FindOneBySearchBean(ctx, bean)
	if err != nil {
		writeJSONErrorGin(c, CodeDBError, err, 500)
		return
	}
	writeJSONOKGin(c, one)
}

{{- if not .ReadOnly}}
// Create godoc
// @Summary Create {{.TableName}}
// @Tags admin-{{.TableName}}
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body model.{{.ClassName}} true "entity"
// @Success 200 {object} APIResponse
// @Router /admin/{{.TableName}}/create [post]
func (m *{{.ClassName}}Manager) Create(c *gin.Context) {
	var item model.{{.ClassName}}
	if err := c.ShouldBindJSON(&item); err != nil {
		writeJSONErrorGin(c, CodeInvalidParam, err, 400)
		return
	}
	ctx := routeDBContext(c.Request.Context(), c.GetHeader("X-DB-Role"))
	err := m.repo.Insert(ctx, &item)
	if err != nil {
		writeJSONErrorGin(c, CodeDBError, err, 500)
		return
	}
	writeJSONOKGin(c, map[string]bool{"ok": true})
}
{{- end}}
`

const controllerFiberTemplate = `package {{.PackageName}}

import (
{{- if not .ReadOnly}}
	"{{.ModelImport}}"
{{- end}}
	"{{.RepoImport}}"
	"{{.SearchImport}}"
	"github.com/gofiber/fiber/v2"
)

type {{.ClassName}}Manager struct {
	repo *repository.{{.ClassName}}Repository
}

func Register{{.ClassName}}ManagerRoutes(r fiber.Router, repo *repository.{{.ClassName}}Repository) *{{.ClassName}}Manager {
	m := &{{.ClassName}}Manager{repo: repo}
	base := "/admin/{{.TableName}}"
	r.Post(base+"/list", m.List)
	r.Post(base+"/count", m.Count)
	r.Post(base+"/one", m.One)
{{- if not .ReadOnly}}
	r.Post(base+"/create", m.Create)
{{- end}}
	return m
}

// List godoc
// @Summary List {{.TableName}}
// @Tags admin-{{.TableName}}
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.{{.ClassName}}SearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/{{.TableName}}/list [post]
func (m *{{.ClassName}}Manager) List(c *fiber.Ctx) error {
	bean := &search.{{.ClassName}}SearchBean{}
	if len(c.Body()) > 0 {
		if err := c.BodyParser(bean); err != nil {
			return writeJSONErrorFiber(c, CodeInvalidParam, err, 400)
		}
	}
	ctx := routeDBContext(c.UserContext(), c.Get("X-DB-Role"))
	items, err := m.repo.ListBySearchBean(ctx, bean)
	if err != nil {
		return writeJSONErrorFiber(c, CodeDBError, err, 500)
	}
	total, err := m.repo.CountBySearchBean(ctx, bean)
	if err != nil {
		return writeJSONErrorFiber(c, CodeDBError, err, 500)
	}
	page := NewPageMeta(bean.Start, bean.Count, total)
	return writeJSONOKFiber(c, NewPageData(items, total, page))
}

// Count godoc
// @Summary Count {{.TableName}}
// @Tags admin-{{.TableName}}
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.{{.ClassName}}SearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/{{.TableName}}/count [post]
func (m *{{.ClassName}}Manager) Count(c *fiber.Ctx) error {
	bean := &search.{{.ClassName}}SearchBean{}
	if len(c.Body()) > 0 {
		if err := c.BodyParser(bean); err != nil {
			return writeJSONErrorFiber(c, CodeInvalidParam, err, 400)
		}
	}
	ctx := routeDBContext(c.UserContext(), c.Get("X-DB-Role"))
	total, err := m.repo.CountBySearchBean(ctx, bean)
	if err != nil {
		return writeJSONErrorFiber(c, CodeDBError, err, 500)
	}
	return writeJSONOKFiber(c, map[string]int64{"total": total})
}

// One godoc
// @Summary Find one {{.TableName}}
// @Tags admin-{{.TableName}}
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body search.{{.ClassName}}SearchBean false "search bean"
// @Success 200 {object} APIResponse
// @Router /admin/{{.TableName}}/one [post]
func (m *{{.ClassName}}Manager) One(c *fiber.Ctx) error {
	bean := &search.{{.ClassName}}SearchBean{}
	if len(c.Body()) > 0 {
		if err := c.BodyParser(bean); err != nil {
			return writeJSONErrorFiber(c, CodeInvalidParam, err, 400)
		}
	}
	ctx := routeDBContext(c.UserContext(), c.Get("X-DB-Role"))
	one, err := m.repo.FindOneBySearchBean(ctx, bean)
	if err != nil {
		return writeJSONErrorFiber(c, CodeDBError, err, 500)
	}
	return writeJSONOKFiber(c, one)
}

{{- if not .ReadOnly}}
// Create godoc
// @Summary Create {{.TableName}}
// @Tags admin-{{.TableName}}
// @Accept json
// @Produce json
// @Param X-DB-Role header string false "master|slave"
// @Param body body model.{{.ClassName}} true "entity"
// @Success 200 {object} APIResponse
// @Router /admin/{{.TableName}}/create [post]
func (m *{{.ClassName}}Manager) Create(c *fiber.Ctx) error {
	var item model.{{.ClassName}}
	if err := c.BodyParser(&item); err != nil {
		return writeJSONErrorFiber(c, CodeInvalidParam, err, 400)
	}
	ctx := routeDBContext(c.UserContext(), c.Get("X-DB-Role"))
	err := m.repo.Insert(ctx, &item)
	if err != nil {
		return writeJSONErrorFiber(c, CodeDBError, err, 500)
	}
	return writeJSONOKFiber(c, map[string]bool{"ok": true})
}
{{- end}}
`

const controllerCommonTemplate = `package controller

import (
	"context"
	"strings"

	"{{.RuntimeImport}}"
)

type APIResponse struct {
	Code    int    ` + "`json:\"code\"`" + `
	Message string ` + "`json:\"message\"`" + `
	Data    any    ` + "`json:\"data,omitempty\"`" + `
}

const (
	CodeOK           = 0
	CodeInvalidParam = 40001
	CodeDBError      = 50001
	CodeInternal     = 50000
)

type PageMeta struct {
	Start int   ` + "`json:\"start\"`" + `
	Count int   ` + "`json:\"count\"`" + `
	Total int64 ` + "`json:\"total\"`" + `
}

type PageData struct {
	List  any      ` + "`json:\"list\"`" + `
	Total int64    ` + "`json:\"total\"`" + `
	Page  PageMeta ` + "`json:\"page\"`" + `
}

func NewPageMeta(start, count int, total int64) PageMeta {
	if start < 0 {
		start = 0
	}
	if count < 0 {
		count = 0
	}
	return PageMeta{
		Start: start,
		Count: count,
		Total: total,
	}
}

func NewPageData(list any, total int64, page PageMeta) PageData {
	return PageData{
		List:  list,
		Total: total,
		Page:  page,
	}
}

func routeDBContext(ctx context.Context, roleHeader string) context.Context {
	switch strings.ToLower(strings.TrimSpace(roleHeader)) {
	case "master":
		return runtime.WithDBRole(ctx, runtime.DBRoleMaster)
	case "slave":
		return runtime.WithDBRole(ctx, runtime.DBRoleSlave)
	default:
		return ctx
	}
}
`

const controllerCommonStdTemplate = `package controller

import (
	"encoding/json"
	"net/http"
)

func writeJSONOK(w http.ResponseWriter, data any) {
	writeJSONResponse(w, APIResponse{Code: CodeOK, Message: "ok", Data: data}, http.StatusOK)
}

func writeJSONError(w http.ResponseWriter, code int, err error, status int) {
	msg := "internal error"
	if err != nil {
		msg = err.Error()
	}
	writeJSONResponse(w, APIResponse{Code: code, Message: msg}, status)
}

func writeJSONResponse(w http.ResponseWriter, resp APIResponse, statusCode int) {
	w.Header().Set("Content-Type", "application/json")
	if statusCode <= 0 {
		statusCode = http.StatusOK
	}
	w.WriteHeader(statusCode)
	_ = json.NewEncoder(w).Encode(resp)
}
`

const controllerCommonGinTemplate = `package controller

import "github.com/gin-gonic/gin"

func writeJSONOKGin(c *gin.Context, data any) {
	c.JSON(200, APIResponse{Code: CodeOK, Message: "ok", Data: data})
}

func writeJSONErrorGin(c *gin.Context, code int, err error, status int) {
	msg := "internal error"
	if err != nil {
		msg = err.Error()
	}
	if status <= 0 {
		status = 500
	}
	c.JSON(status, APIResponse{Code: code, Message: msg})
}
`

const controllerCommonFiberTemplate = `package controller

import "github.com/gofiber/fiber/v2"

func writeJSONOKFiber(c *fiber.Ctx, data any) error {
	return c.Status(200).JSON(APIResponse{Code: CodeOK, Message: "ok", Data: data})
}

func writeJSONErrorFiber(c *fiber.Ctx, code int, err error, status int) error {
	msg := "internal error"
	if err != nil {
		msg = err.Error()
	}
	if status <= 0 {
		status = 500
	}
	return c.Status(status).JSON(APIResponse{Code: code, Message: msg})
}
`

const controllerOpenAPITemplate = `package controller

// @title Tablegear Admin API
// @version 1.0
// @description Auto generated management APIs by golang-tablegear.
// @BasePath /
`

const runtimeConfigTemplate = `package {{.PackageName}}

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"os"

	_ "github.com/go-sql-driver/mysql"
	"{{.RuntimeImport}}"
)

type Config struct {
	AdminDSN  string   ` + "`json:\"adminDsn\"`" + `
	MasterDSN string   ` + "`json:\"masterDsn\"`" + `
	SlaveDSNs []string ` + "`json:\"slaveDsns\"`" + `
}

func Load(path string) (*Config, error) {
	content, err := os.ReadFile(path)
	if err != nil {
		return nil, err
	}
	var cfg Config
	if err = json.Unmarshal(content, &cfg); err != nil {
		return nil, err
	}
	if cfg.MasterDSN == "" {
		return nil, fmt.Errorf("masterDsn is empty")
	}
	if cfg.AdminDSN == "" {
		cfg.AdminDSN = cfg.MasterDSN
	}
	return &cfg, nil
}

func OpenRWDB(cfg *Config) (*runtime.DB, func(), error) {
	master, err := sql.Open("mysql", cfg.MasterDSN)
	if err != nil {
		return nil, nil, err
	}
	slaves := make([]*sql.DB, 0, len(cfg.SlaveDSNs))
	for _, dsn := range cfg.SlaveDSNs {
		s, e := sql.Open("mysql", dsn)
		if e != nil {
			_ = master.Close()
			return nil, nil, e
		}
		slaves = append(slaves, s)
	}
	closeFn := func() {
		_ = master.Close()
		for _, s := range slaves {
			_ = s.Close()
		}
	}
	return runtime.NewRWDB(master, slaves, nil, runtime.DBConfig{}), closeFn, nil
}
`

const runtimeJSONTemplate = `{
  "adminDsn": "root@tcp(127.0.0.1:3306)/?parseTime=true",
  "masterDsn": "root@tcp(127.0.0.1:3306)/your_db?parseTime=true",
  "slaveDsns": [
    "root@tcp(127.0.0.1:3306)/your_db?parseTime=true"
  ]
}
`
