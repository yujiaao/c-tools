package search

type ActiveUserLogicViewSearchBean struct {
	Start  int    `json:"start"`
	Count  int    `json:"count"`
	SortBy string `json:"sortBy"`
	Id *int64 `json:"id"`
	BeginId *int64 `json:"begin_id"`
	EndId *int64 `json:"end_id"`
	Name *string `json:"name"`
	BeginName *string `json:"begin_name"`
	EndName *string `json:"end_name"`
	Status *int `json:"status"`
	BeginStatus *int `json:"begin_status"`
	EndStatus *int `json:"end_status"`
}
