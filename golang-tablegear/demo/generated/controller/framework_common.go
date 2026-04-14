package controller

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
