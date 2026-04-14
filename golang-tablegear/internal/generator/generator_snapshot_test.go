package generator

import (
	"strings"
	"testing"

	"github.com/yujiaao/golang-tablegear/internal/config"
	"github.com/yujiaao/golang-tablegear/internal/meta"
)

func TestRenderAll_SnapshotContainsM3Features(t *testing.T) {
	g := New(config.Config{
		Driver: "mysql",
		Module: "github.com/acme/demo",
	})
	table := meta.Table{
		Name: "user_account",
		Columns: []meta.Column{
			{Name: "id", GoType: "int64", IsPrimary: true},
			{Name: "name", GoType: "string"},
			{Name: "status", GoType: "int"},
		},
	}

	modelText, repoText, queryText, searchText, err := g.renderAll(table)
	if err != nil {
		t.Fatalf("renderAll error: %v", err)
	}

	mustContain(t, modelText, "type UserAccount struct")
	mustContain(t, repoText, "type UserAccountAdapter struct{}")
	mustContain(t, repoText, "*runtime.GenericRepository[model.UserAccount]")
	mustContain(t, repoText, "func (a *UserAccountAdapter) InsertArgs(item *model.UserAccount) []any")
	mustContain(t, repoText, "func (r *UserAccountRepository) ListBySearchBean")
	mustContain(t, queryText, "*corequery.QueryBase[UserAccountQuery]")
	mustContain(t, queryText, "q.QueryBase = corequery.NewQueryBase[UserAccountQuery](q)")
	mustContain(t, queryText, "F UserAccountFields")
	mustContain(t, queryText, "q.F = UserAccountField")
	mustContain(t, queryText, "func (q *UserAccountQuery) ApplySearchBean(bean *search.UserAccountSearchBean)")
	mustContain(t, queryText, "instead of generating per-column EqXxx/LikeXxx methods")
	mustContain(t, queryText, "corequery.Eq(q.F.Id, value)")
	mustContain(t, searchText, "type UserAccountSearchBean struct")
	mustContain(t, searchText, "BeginId *int64")
	mustContain(t, searchText, "SortBy string")
}

func mustContain(t *testing.T, text, part string) {
	t.Helper()
	if !strings.Contains(text, part) {
		t.Fatalf("snapshot mismatch, missing: %s", part)
	}
}
