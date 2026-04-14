package runtimeconfig

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"os"

	_ "github.com/go-sql-driver/mysql"
	"github.com/yujiaao/golang-tablegear/pkg/runtime"
)

type Config struct {
	AdminDSN  string   `json:"adminDsn"`
	MasterDSN string   `json:"masterDsn"`
	SlaveDSNs []string `json:"slaveDsns"`
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
