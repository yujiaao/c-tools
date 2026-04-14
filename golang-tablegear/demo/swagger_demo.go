//go:build swagger

package main

import (
	"net/http"

	_ "github.com/yujiaao/golang-tablegear/demo/docs"
	httpSwagger "github.com/swaggo/http-swagger"
)

// RegisterSwaggerDocs mounts swagger UI route.
// Visit: http://127.0.0.1:8080/swagger/index.html
func RegisterSwaggerDocs(mux *http.ServeMux) {
	mux.Handle("/swagger/", httpSwagger.WrapHandler)
}

