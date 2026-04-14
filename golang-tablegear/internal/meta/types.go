package meta

type Table struct {
	Name          string
	Columns       []Column
	PKs           []string
	IsLogicalView bool
	CoreSQL       string
	ReadOnly      bool
}

type Column struct {
	Name       string
	DBType     string
	Nullable   bool
	IsPrimary  bool
	GoType     string
	ColumnNote string
}
