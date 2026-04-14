package main

import (
	"context"
	"database/sql"
	"flag"
	"log"
	"os"
	"strings"
	"time"

	_ "github.com/go-sql-driver/mysql"
	"github.com/yujiaao/golang-tablegear/internal/config"
	"github.com/yujiaao/golang-tablegear/internal/generator"
	"github.com/yujiaao/golang-tablegear/internal/meta"
)

func main() {
	cfg := parseFlags()
	if err := cfg.LoadLogicalViews(); err != nil {
		log.Fatalf("load logical views failed: %v", err)
	}
	if err := cfg.Validate(); err != nil {
		log.Fatalf("invalid config: %v", err)
	}

	db, err := sql.Open(cfg.Driver, cfg.DSN)
	if err != nil {
		log.Fatalf("open db failed: %v", err)
	}
	defer db.Close()

	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()

	inspector := meta.NewInspector(db, cfg.Driver, cfg.Schema)
	tables, err := inspector.InspectTables(ctx, cfg.Tables)
	if err != nil {
		log.Fatalf("inspect metadata failed: %v", err)
	}
	if len(cfg.ViewDefs) > 0 {
		viewTables, e := inspector.InspectLogicalViews(ctx, cfg.ViewDefs)
		if e != nil {
			log.Fatalf("inspect logical views failed: %v", e)
		}
		tables = append(tables, viewTables...)
	}
	if len(tables) == 0 {
		log.Println("no tables found, nothing generated")
		return
	}

	gen := generator.New(cfg)
	if cfg.DryRun {
		if err = gen.Preview(os.Stdout, tables); err != nil {
			log.Fatalf("preview failed: %v", err)
		}
		return
	}
	if err = gen.Generate(tables); err != nil {
		log.Fatalf("generate failed: %v", err)
	}
	log.Printf("generated %d tables into %s", len(tables), cfg.OutDir)
}

func parseFlags() config.Config {
	var cfg config.Config
	var cfgFile string
	var tables string
	var driver string
	var dsn string
	var schema string
	var module string
	var out string
	var viewsConfig string
	var dryRun bool
	var genControllers bool
	var genRuntimeScaffold bool
	var controllerTemplate string

	flag.StringVar(&cfgFile, "config", "", "path to json config file")
	flag.StringVar(&driver, "driver", "", "database driver: mysql or postgres")
	flag.StringVar(&dsn, "dsn", "", "database dsn")
	flag.StringVar(&schema, "schema", "", "database schema/catalog")
	flag.StringVar(&module, "module", "", "go module path for generated import")
	flag.StringVar(&out, "out", "", "output directory")
	flag.StringVar(&tables, "tables", "", "comma separated table names, empty means all")
	flag.BoolVar(&dryRun, "dry-run", false, "print generated code without writing files")
	flag.StringVar(&viewsConfig, "views-config", "", "path to logical views json config")
	flag.BoolVar(&genControllers, "gen-controllers", false, "generate management HTTP handlers")
	flag.BoolVar(&genRuntimeScaffold, "gen-runtime-scaffold", false, "generate runtime.json loader scaffold")
	flag.StringVar(&controllerTemplate, "controller-template", "", "controller template: std|gin|fiber")
	flag.Parse()

	if cfgFile != "" {
		if err := cfg.LoadFromFile(cfgFile); err != nil {
			log.Fatalf("load --config failed: %v", err)
		}
	}
	// CLI args override config file values when provided.
	if driver != "" {
		cfg.Driver = driver
	}
	if dsn != "" {
		cfg.DSN = dsn
	}
	if schema != "" {
		cfg.Schema = schema
	}
	if module != "" {
		cfg.Module = module
	}
	if out != "" {
		cfg.OutDir = out
	}
	if viewsConfig != "" {
		cfg.ViewsConfig = viewsConfig
	}
	if dryRun {
		cfg.DryRun = true
	}
	if genControllers {
		cfg.GenerateControllers = true
	}
	if genRuntimeScaffold {
		cfg.GenerateRuntimeScaffold = true
	}
	if controllerTemplate != "" {
		cfg.ControllerTemplate = controllerTemplate
	}
	if tables != "" {
		cfg.Tables = nil
		for _, t := range strings.Split(tables, ",") {
			name := strings.TrimSpace(t)
			if name != "" {
				cfg.Tables = append(cfg.Tables, name)
			}
		}
	}
	// Keep legacy defaults when no config file provided.
	if cfg.Driver == "" {
		cfg.Driver = "mysql"
	}
	if cfg.OutDir == "" {
		cfg.OutDir = "./generated"
	}
	if cfg.ControllerTemplate == "" {
		cfg.ControllerTemplate = "std"
	}
	return cfg
}
