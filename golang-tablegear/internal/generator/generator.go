package generator

import (
	"bytes"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"strconv"
	"strings"
	"text/template"

	"github.com/yujiaao/golang-tablegear/internal/config"
	"github.com/yujiaao/golang-tablegear/internal/meta"
)

const runtimeModuleBase = "github.com/yujiaao/golang-tablegear"

type Generator struct {
	cfg config.Config
}

func New(cfg config.Config) *Generator {
	return &Generator{cfg: cfg}
}

func (g *Generator) Preview(w io.Writer, tables []meta.Table) error {
	for _, t := range tables {
		model, repo, queryFile, searchFile, err := g.renderAll(t)
		if err != nil {
			return err
		}
		if _, err = fmt.Fprintf(w, "===== %s/model.go =====\n%s\n", t.Name, model); err != nil {
			return err
		}
		if _, err = fmt.Fprintf(w, "===== %s/repository.go =====\n%s\n", t.Name, repo); err != nil {
			return err
		}
		if _, err = fmt.Fprintf(w, "===== %s/query.go =====\n%s\n", t.Name, queryFile); err != nil {
			return err
		}
		if _, err = fmt.Fprintf(w, "===== %s/search.go =====\n%s\n", t.Name, searchFile); err != nil {
			return err
		}
		if g.cfg.GenerateControllers {
			controllerText, e := g.renderController(t)
			if e != nil {
				return e
			}
			if _, err = fmt.Fprintf(w, "===== %s/controller.go =====\n%s\n", t.Name, controllerText); err != nil {
				return err
			}
		}
	}
	if g.cfg.GenerateControllers {
		commonText, err := g.renderControllerCommon()
		if err != nil {
			return err
		}
		if _, err = fmt.Fprintf(w, "===== controller/common.go =====\n%s\n", commonText); err != nil {
			return err
		}
		fwCommonText, err := g.renderControllerFrameworkCommon()
		if err != nil {
			return err
		}
		if _, err = fmt.Fprintf(w, "===== controller/framework_common.go =====\n%s\n", fwCommonText); err != nil {
			return err
		}
		openapiText, err := g.renderControllerOpenAPI()
		if err != nil {
			return err
		}
		if _, err = fmt.Fprintf(w, "===== controller/openapi.go =====\n%s\n", openapiText); err != nil {
			return err
		}
	}
	if g.cfg.GenerateRuntimeScaffold {
		scaffoldText, err := g.renderRuntimeConfigLoader()
		if err != nil {
			return err
		}
		if _, err = fmt.Fprintf(w, "===== runtimeconfig/runtime_config.go =====\n%s\n", scaffoldText); err != nil {
			return err
		}
	}
	return nil
}

func (g *Generator) Generate(tables []meta.Table) error {
	modelDir := filepath.Join(g.cfg.OutDir, "model")
	repoDir := filepath.Join(g.cfg.OutDir, "repository")
	queryDir := filepath.Join(g.cfg.OutDir, "query")
	searchDir := filepath.Join(g.cfg.OutDir, "search")
	controllerDir := filepath.Join(g.cfg.OutDir, "controller")
	if err := os.MkdirAll(modelDir, 0o755); err != nil {
		return err
	}
	if err := os.MkdirAll(repoDir, 0o755); err != nil {
		return err
	}
	if err := os.MkdirAll(queryDir, 0o755); err != nil {
		return err
	}
	if err := os.MkdirAll(searchDir, 0o755); err != nil {
		return err
	}
	if g.cfg.GenerateControllers {
		if err := os.MkdirAll(controllerDir, 0o755); err != nil {
			return err
		}
		commonText, err := g.renderControllerCommon()
		if err != nil {
			return err
		}
		commonPath := filepath.Join(controllerDir, "common.go")
		if err = os.WriteFile(commonPath, []byte(commonText), 0o644); err != nil {
			return err
		}
		fwCommonText, err := g.renderControllerFrameworkCommon()
		if err != nil {
			return err
		}
		fwCommonPath := filepath.Join(controllerDir, "framework_common.go")
		if err = os.WriteFile(fwCommonPath, []byte(fwCommonText), 0o644); err != nil {
			return err
		}
		openapiText, err := g.renderControllerOpenAPI()
		if err != nil {
			return err
		}
		openapiPath := filepath.Join(controllerDir, "openapi.go")
		if err = os.WriteFile(openapiPath, []byte(openapiText), 0o644); err != nil {
			return err
		}
	}

	for _, t := range tables {
		model, repo, queryFile, searchFile, err := g.renderAll(t)
		if err != nil {
			return err
		}
		modelPath := filepath.Join(modelDir, snakeToLower(t.Name)+".go")
		repoPath := filepath.Join(repoDir, snakeToLower(t.Name)+"_repository.go")
		queryPath := filepath.Join(queryDir, snakeToLower(t.Name)+"_query.go")
		searchPath := filepath.Join(searchDir, snakeToLower(t.Name)+"_search.go")
		if err = os.WriteFile(modelPath, []byte(model), 0o644); err != nil {
			return err
		}
		if err = os.WriteFile(repoPath, []byte(repo), 0o644); err != nil {
			return err
		}
		if err = os.WriteFile(queryPath, []byte(queryFile), 0o644); err != nil {
			return err
		}
		if err = os.WriteFile(searchPath, []byte(searchFile), 0o644); err != nil {
			return err
		}
		if g.cfg.GenerateControllers {
			controllerText, e := g.renderController(t)
			if e != nil {
				return e
			}
			controllerPath := filepath.Join(controllerDir, snakeToLower(t.Name)+"_controller.go")
			if err = os.WriteFile(controllerPath, []byte(controllerText), 0o644); err != nil {
				return err
			}
		}
	}
	if g.cfg.GenerateRuntimeScaffold {
		if err := g.generateRuntimeScaffold(); err != nil {
			return err
		}
	}
	return nil
}

func (g *Generator) renderController(t meta.Table) (string, error) {
	className := snakeToCamel(t.Name)
	cd := fileData{
		PackageName:   "controller",
		TableName:     t.Name,
		ClassName:     className,
		ModelImport:   joinModulePath(g.cfg.Module, "model"),
		QueryImport:   joinModulePath(g.cfg.Module, "query"),
		SearchImport:  joinModulePath(g.cfg.Module, "search"),
		RepoImport:    joinModulePath(g.cfg.Module, "repository"),
		RuntimeImport: joinModulePath(runtimeModuleBase, "pkg/runtime"),
		ReadOnly:      t.ReadOnly,
	}
	switch strings.ToLower(strings.TrimSpace(g.cfg.ControllerTemplate)) {
	case "gin":
		return render(controllerGinTemplate, cd)
	case "fiber":
		return render(controllerFiberTemplate, cd)
	default:
		return render(controllerStdTemplate, cd)
	}
}

func (g *Generator) renderRuntimeConfigLoader() (string, error) {
	fd := fileData{
		PackageName:   "runtimeconfig",
		RuntimeImport: joinModulePath(runtimeModuleBase, "pkg/runtime"),
	}
	return render(runtimeConfigTemplate, fd)
}

func (g *Generator) renderControllerCommon() (string, error) {
	fd := fileData{
		PackageName:   "controller",
		RuntimeImport: joinModulePath(runtimeModuleBase, "pkg/runtime"),
	}
	return render(controllerCommonTemplate, fd)
}

func (g *Generator) renderControllerFrameworkCommon() (string, error) {
	fd := fileData{
		PackageName: "controller",
	}
	switch strings.ToLower(strings.TrimSpace(g.cfg.ControllerTemplate)) {
	case "gin":
		return render(controllerCommonGinTemplate, fd)
	case "fiber":
		return render(controllerCommonFiberTemplate, fd)
	default:
		return render(controllerCommonStdTemplate, fd)
	}
}

func (g *Generator) renderControllerOpenAPI() (string, error) {
	fd := fileData{
		PackageName: "controller",
	}
	return render(controllerOpenAPITemplate, fd)
}

func (g *Generator) generateRuntimeScaffold() error {
	runtimeCfgDir := filepath.Join(g.cfg.OutDir, "runtimeconfig")
	if err := os.MkdirAll(runtimeCfgDir, 0o755); err != nil {
		return err
	}
	loaderText, err := g.renderRuntimeConfigLoader()
	if err != nil {
		return err
	}
	loaderPath := filepath.Join(runtimeCfgDir, "runtime_config.go")
	if err = os.WriteFile(loaderPath, []byte(loaderText), 0o644); err != nil {
		return err
	}
	jsonPath := filepath.Join(filepath.Dir(g.cfg.OutDir), "runtime.json")
	_, statErr := os.Stat(jsonPath)
	if statErr == nil {
		return nil
	}
	if !os.IsNotExist(statErr) {
		return statErr
	}
	return os.WriteFile(jsonPath, []byte(runtimeJSONTemplate), 0o644)
}

func (g *Generator) renderAll(t meta.Table) (string, string, string, string, error) {
	className := snakeToCamel(t.Name)
	fd := fileData{
		PackageName:    "model",
		TableName:      t.Name,
		ClassName:      className,
		Columns:        buildColumns(t.Columns),
		NeedTime:       hasTimeType(t.Columns),
		IsLogicalView:  t.IsLogicalView,
		CoreSQL:        t.CoreSQL,
		ReadOnly:       t.ReadOnly,
		CoreSQLLiteral: toGoStringLiteral(t.CoreSQL),
	}
	modelText, err := render(modelTemplate, fd)
	if err != nil {
		return "", "", "", "", err
	}

	rd := fileData{
		PackageName:    "repository",
		TableName:      t.Name,
		ClassName:      className,
		Columns:        buildColumns(t.Columns),
		ModelImport:    joinModulePath(g.cfg.Module, "model"),
		QueryImport:    joinModulePath(g.cfg.Module, "query"),
		SearchImport:   joinModulePath(g.cfg.Module, "search"),
		RuntimeImport:  joinModulePath(runtimeModuleBase, "pkg/runtime"),
		PrimaryKey:     detectPK(t),
		DialectName:    g.cfg.Driver,
		IsLogicalView:  t.IsLogicalView,
		CoreSQL:        t.CoreSQL,
		ReadOnly:       t.ReadOnly,
		CoreSQLLiteral: toGoStringLiteral(t.CoreSQL),
	}
	repoText, err := render(repositoryTemplate, rd)
	if err != nil {
		return "", "", "", "", err
	}
	qd := fileData{
		PackageName:     "query",
		ClassName:       className,
		Columns:         buildColumns(t.Columns),
		QueryCoreImport: joinModulePath(runtimeModuleBase, "pkg/query"),
		SearchImport:    joinModulePath(g.cfg.Module, "search"),
		NeedTime:        hasTimeType(t.Columns),
		IsLogicalView:   t.IsLogicalView,
		CoreSQL:         t.CoreSQL,
		ReadOnly:        t.ReadOnly,
		CoreSQLLiteral:  toGoStringLiteral(t.CoreSQL),
	}
	queryText, err := render(queryTemplate, qd)
	if err != nil {
		return "", "", "", "", err
	}
	sd := fileData{
		PackageName: "search",
		ClassName:   className,
		Columns:     buildColumns(t.Columns),
		NeedTime:    hasTimeType(t.Columns),
	}
	searchText, err := render(searchTemplate, sd)
	if err != nil {
		return "", "", "", "", err
	}
	return modelText, repoText, queryText, searchText, nil
}

func render(text string, data fileData) (string, error) {
	tpl, err := template.New("tpl").Funcs(template.FuncMap{
		"hasPtr": func(t string) bool { return strings.HasPrefix(t, "*") },
	}).Parse(text)
	if err != nil {
		return "", err
	}
	var buf bytes.Buffer
	if err = tpl.Execute(&buf, data); err != nil {
		return "", err
	}
	return buf.String(), nil
}

func detectPK(t meta.Table) fieldData {
	for _, c := range t.Columns {
		if c.IsPrimary {
			return fieldData{Name: snakeToCamel(c.Name), ColumnName: c.Name, GoType: c.GoType}
		}
	}
	if len(t.Columns) == 0 {
		return fieldData{}
	}
	first := t.Columns[0]
	return fieldData{Name: snakeToCamel(first.Name), ColumnName: first.Name, GoType: first.GoType}
}

func buildColumns(cols []meta.Column) []fieldData {
	out := make([]fieldData, 0, len(cols))
	for _, c := range cols {
		out = append(out, fieldData{
			Name:       snakeToCamel(c.Name),
			ColumnName: c.Name,
			GoType:     c.GoType,
		})
	}
	return out
}

func hasTimeType(cols []meta.Column) bool {
	for _, c := range cols {
		if strings.Contains(c.GoType, "time.Time") {
			return true
		}
	}
	return false
}

func snakeToCamel(in string) string {
	parts := strings.FieldsFunc(in, func(r rune) bool {
		return r == '_' || r == '-'
	})
	for i := range parts {
		if parts[i] == "" {
			continue
		}
		parts[i] = strings.ToUpper(parts[i][:1]) + strings.ToLower(parts[i][1:])
	}
	return strings.Join(parts, "")
}

func snakeToLower(in string) string {
	return strings.ToLower(strings.ReplaceAll(in, "-", "_"))
}

func joinModulePath(module, suffix string) string {
	if module == "" {
		return suffix
	}
	return strings.TrimRight(module, "/") + "/" + suffix
}

type fileData struct {
	PackageName     string
	TableName       string
	ClassName       string
	Columns         []fieldData
	NeedTime        bool
	ModelImport     string
	QueryImport     string
	SearchImport    string
	RuntimeImport   string
	RepoImport      string
	QueryCoreImport string
	PrimaryKey      fieldData
	DialectName     string
	IsLogicalView   bool
	CoreSQL         string
	CoreSQLLiteral  string
	ReadOnly        bool
	ControllerStyle string
}

type fieldData struct {
	Name       string
	ColumnName string
	GoType     string
}

func toGoStringLiteral(src string) string {
	return strconv.Quote(src)
}
