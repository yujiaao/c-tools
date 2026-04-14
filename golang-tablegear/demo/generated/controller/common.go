package controller

import (
	"context"
	"strings"

	"github.com/yujiaao/golang-tablegear/pkg/runtime"
)

type APIResponse struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
	Data    any    `json:"data,omitempty"`
}

const (
	CodeOK           = 0
	CodeInvalidParam = 40001
	CodeDBError      = 50001
	CodeInternal     = 50000
)

type PageMeta struct {
	Start int   `json:"start"`
	Count int   `json:"count"`
	Total int64 `json:"total"`
}

type PageData struct {
	List  any      `json:"list"`
	Total int64    `json:"total"`
	Page  PageMeta `json:"page"`
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
