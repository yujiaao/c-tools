package config

import (
	"encoding/json"
	"errors"
	"fmt"
	"os"
	"path/filepath"
	"strings"
)

type Config struct {
	Driver                  string           `json:"driver"`
	DSN                     string           `json:"dsn"`
	Schema                  string           `json:"schema"`
	Module                  string           `json:"module"`
	OutDir                  string           `json:"out"`
	Tables                  []string         `json:"tables"`
	DryRun                  bool             `json:"dryRun"`
	ViewsConfig             string           `json:"viewsConfig"`
	GenerateControllers     bool             `json:"generateControllers"`
	GenerateRuntimeScaffold bool             `json:"generateRuntimeScaffold"`
	ControllerTemplate      string           `json:"controllerTemplate"`
	ViewDefs                []LogicalViewDef `json:"-"`
}

type LogicalViewDef struct {
	Name    string `json:"name"`
	SQLFile string `json:"sqlFile"`
	SQL     string `json:"-"`
}

type logicalViewFile struct {
	Views []LogicalViewDef `json:"views"`
}

func (c *Config) LoadFromFile(path string) error {
	content, err := os.ReadFile(path)
	if err != nil {
		return fmt.Errorf("read config file failed: %w", err)
	}
	var loaded Config
	if err = json.Unmarshal(content, &loaded); err != nil {
		return fmt.Errorf("parse config file failed: %w", err)
	}
	*c = loaded
	if c.ViewsConfig != "" && !filepath.IsAbs(c.ViewsConfig) {
		c.ViewsConfig = filepath.Join(filepath.Dir(path), c.ViewsConfig)
	}
	return nil
}

func (c Config) Validate() error {
	if c.DSN == "" {
		return errors.New("dsn is required")
	}
	if c.OutDir == "" && !c.DryRun {
		return errors.New("out is required")
	}
	switch strings.ToLower(c.Driver) {
	case "mysql", "postgres", "postgresql":
	default:
		return fmt.Errorf("unsupported driver: %s", c.Driver)
	}
	if c.ViewsConfig != "" && len(c.ViewDefs) == 0 {
		return errors.New("views config is set but no valid logical views loaded")
	}
	switch strings.ToLower(strings.TrimSpace(c.ControllerTemplate)) {
	case "", "std", "gin", "fiber":
	default:
		return fmt.Errorf("unsupported controllerTemplate: %s", c.ControllerTemplate)
	}
	return nil
}

func (c *Config) LoadLogicalViews() error {
	if c.ViewsConfig == "" {
		return nil
	}
	content, err := os.ReadFile(c.ViewsConfig)
	if err != nil {
		return fmt.Errorf("read views config failed: %w", err)
	}
	var file logicalViewFile
	if err = json.Unmarshal(content, &file); err != nil {
		return fmt.Errorf("parse views config failed: %w", err)
	}
	base := filepath.Dir(c.ViewsConfig)
	out := make([]LogicalViewDef, 0, len(file.Views))
	for _, v := range file.Views {
		if strings.TrimSpace(v.Name) == "" || strings.TrimSpace(v.SQLFile) == "" {
			continue
		}
		sqlPath := v.SQLFile
		if !filepath.IsAbs(sqlPath) {
			sqlPath = filepath.Join(base, sqlPath)
		}
		sqlBytes, e := os.ReadFile(sqlPath)
		if e != nil {
			return fmt.Errorf("read view sql failed for %s: %w", v.Name, e)
		}
		v.SQL = strings.TrimSpace(string(sqlBytes))
		if v.SQL == "" {
			return fmt.Errorf("view sql is empty: %s", v.Name)
		}
		out = append(out, v)
	}
	c.ViewDefs = out
	return nil
}
