package main

import (
	"context"
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"time"

	_ "github.com/go-sql-driver/mysql"
	"github.com/yujiaao/golang-tablegear/demo/generated/controller"
	"github.com/yujiaao/golang-tablegear/demo/generated/model"
	"github.com/yujiaao/golang-tablegear/demo/generated/query"
	"github.com/yujiaao/golang-tablegear/demo/generated/repository"
	"github.com/yujiaao/golang-tablegear/demo/generated/search"
	corequery "github.com/yujiaao/golang-tablegear/pkg/query"
	runtime "github.com/yujiaao/golang-tablegear/pkg/runtime"
)

func main() {
	ctx := context.Background()
	cfg, err := loadDemoDBConfig("./demo/runtime.json")
	if err != nil {
		log.Fatalf("load demo db config failed: %v", err)
	}
	if err = ensureSchema(ctx, cfg.AdminDSN); err != nil {
		log.Fatalf("init schema failed: %v", err)
	}

	masterDB, err := sql.Open("mysql", cfg.MasterDSN)
	if err != nil {
		log.Fatal(err)
	}
	defer masterDB.Close()
	if err = masterDB.PingContext(ctx); err != nil {
		log.Fatalf("mysql master ping failed: %v", err)
	}
	log.Println("mysql master ping success")

	slaveDBs := make([]*sql.DB, 0, len(cfg.SlaveDSNs))
	for _, dsn := range cfg.SlaveDSNs {
		db, openErr := sql.Open("mysql", dsn)
		if openErr != nil {
			log.Fatalf("open mysql slave failed: %v", openErr)
		}
		if pingErr := db.PingContext(ctx); pingErr != nil {
			_ = db.Close()
			log.Fatalf("mysql slave ping failed: %v", pingErr)
		}
		slaveDBs = append(slaveDBs, db)
	}
	defer func() {
		for _, db := range slaveDBs {
			_ = db.Close()
		}
	}()
	log.Printf("mysql slave count=%d", len(slaveDBs))

	db := runtime.NewRWDB(masterDB, slaveDBs, log.Default(), runtime.DBConfig{
		SlowSQLThreshold: 500 * time.Millisecond,
	})
	repo, err := repository.NewUserAccountRepository(db)
	if err != nil {
		log.Fatal(err)
	}
	logicViewRepo, err := repository.NewActiveUserLogicViewRepository(db)
	if err != nil {
		log.Fatal(err)
	}
	userOrderViewRepo, err := repository.NewUserOrderLogicViewRepository(db)
	if err != nil {
		log.Fatal(err)
	}
	adminMux := http.NewServeMux()
	controller.RegisterUserAccountManagerRoutes(adminMux, repo)
	controller.RegisterActiveUserLogicViewManagerRoutes(adminMux, logicViewRepo)
	controller.RegisterUserOrderLogicViewManagerRoutes(adminMux, userOrderViewRepo)
	log.Println("admin routes registered: /admin/*")

	// 1) insert entity
	id := time.Now().Unix()
	u := &model.UserAccount{
		Id:     id,
		Name:   "alice",
		Status: 1,
	}
	if err = repo.Insert(ctx, u); err != nil {
		log.Printf("insert failed: %v", err)
	}
	if _, err = masterDB.ExecContext(ctx,
		"INSERT INTO order_info (order_no, user_id, amount, created_at) VALUES (?, ?, ?, ?)",
		fmt.Sprintf("O-%d", id), id, 99.9, time.Now(),
	); err != nil {
		log.Printf("insert order failed: %v", err)
	}

	// 2) query list
	q := query.NewUserAccountQuery()
	q.Where(
		corequery.Eq(q.F.Status, 1),
		corequery.Like(q.F.Name, "ali"),
	).
		OrderByDesc(q.F.Id).
		Limit(20).
		Offset(0)

	list, err := repo.ListByQuery(ctx, q)
	if err != nil {
		log.Printf("list failed: %v", err)
	} else {
		log.Printf("list size=%d", len(list))
	}

	// 3) query one
	oneQuery := query.NewUserAccountQuery()
	oneQuery.Where(corequery.Eq(oneQuery.F.Id, id))
	one, err := repo.FindOneByQuery(ctx, oneQuery)
	if err != nil {
		log.Printf("find one failed: %v", err)
	} else if one != nil {
		log.Printf("found user: %+v", *one)
	}

	// 4) count
	countQuery := query.NewUserAccountQuery()
	countQuery.Where(corequery.Eq(countQuery.F.Status, 1))
	total, err := repo.CountByQuery(ctx, countQuery)
	if err != nil {
		log.Printf("count failed: %v", err)
	} else {
		log.Printf("total active users=%d", total)
	}

	// 5) logical view query example (sql from demo/views/*.sql)
	viewQ := query.NewActiveUserLogicViewQuery()
	viewQ.Where(
		corequery.Like(viewQ.F.Name, "ali"),
	).OrderByDesc(viewQ.F.Id).Limit(10)
	viewList, err := logicViewRepo.ListByQuery(ctx, viewQ)
	if err != nil {
		log.Printf("logic view list failed: %v", err)
	} else {
		log.Printf("logic view list size=%d", len(viewList))
	}

	viewCount, err := logicViewRepo.CountByQuery(ctx, query.NewActiveUserLogicViewQuery())
	if err != nil {
		log.Printf("logic view count failed: %v", err)
	} else {
		log.Printf("logic view total=%d", viewCount)
	}

	viewOneQ := query.NewActiveUserLogicViewQuery()
	viewOneQ.OrderByDesc(viewOneQ.F.Id).Limit(1)
	viewOne, err := logicViewRepo.FindOneByQuery(ctx, viewOneQ)
	if err != nil {
		log.Printf("logic view find one failed: %v", err)
	} else if viewOne != nil {
		log.Printf("logic view one: %+v", *viewOne)
	}

	// 6) join logical view query example (user + order)
	joinQ := query.NewUserOrderLogicViewQuery()
	joinQ.Where(
		corequery.Gt(joinQ.F.Amount, 10.0),
	).OrderByDesc(joinQ.F.CreatedAt).Limit(10)
	joinList, err := userOrderViewRepo.ListByQuery(ctx, joinQ)
	if err != nil {
		log.Printf("join logic view list failed: %v", err)
	} else {
		log.Printf("join logic view list size=%d", len(joinList))
	}

	joinCount, err := userOrderViewRepo.CountByQuery(ctx, query.NewUserOrderLogicViewQuery())
	if err != nil {
		log.Printf("join logic view count failed: %v", err)
	} else {
		log.Printf("join logic view total=%d", joinCount)
	}

	// 7) SearchBean usage example (without Query DSL)
	searchStatus := 1
	searchBean := &search.UserAccountSearchBean{
		Start:  0,
		Count:  5,
		SortBy: "id:desc",
		Status: &searchStatus,
	}
	sbList, err := repo.ListBySearchBean(ctx, searchBean)
	if err != nil {
		log.Printf("searchBean list failed: %v", err)
	} else {
		log.Printf("searchBean list size=%d", len(sbList))
	}
	sbCount, err := repo.CountBySearchBean(ctx, searchBean)
	if err != nil {
		log.Printf("searchBean count failed: %v", err)
	} else {
		log.Printf("searchBean total=%d", sbCount)
	}
	if len(sbList) > 0 {
		log.Printf("searchBean first: %+v", *sbList[0])
	}
	sbOne, err := repo.FindOneBySearchBean(ctx, searchBean)
	if err != nil {
		log.Printf("searchBean find one failed: %v", err)
	} else if sbOne != nil {
		log.Printf("searchBean one: %+v", *sbOne)
	}

	// 8) force query route
	forceMasterCtx := runtime.WithDBRole(ctx, runtime.DBRoleMaster)
	forceSlaveCtx := runtime.WithDBRole(ctx, runtime.DBRoleSlave)
	_, _ = repo.CountByQuery(forceMasterCtx, query.NewUserAccountQuery())
	_, _ = repo.CountByQuery(forceSlaveCtx, query.NewUserAccountQuery())
	log.Println("forced route done: one query on master, one query on slave")
}

type demoDBConfig struct {
	AdminDSN  string   `json:"adminDsn"`
	MasterDSN string   `json:"masterDsn"`
	SlaveDSNs []string `json:"slaveDsns"`
}

func loadDemoDBConfig(path string) (*demoDBConfig, error) {
	content, err := os.ReadFile(path)
	if err != nil {
		return nil, err
	}
	var cfg demoDBConfig
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

func ensureSchema(ctx context.Context, adminDSN string) error {
	adminDB, err := sql.Open("mysql", adminDSN)
	if err != nil {
		return err
	}
	defer adminDB.Close()
	if err = adminDB.PingContext(ctx); err != nil {
		return err
	}

	stmts := []string{
		"CREATE DATABASE IF NOT EXISTS demo",
		`CREATE TABLE IF NOT EXISTS demo.user_account (
			id BIGINT PRIMARY KEY,
			name VARCHAR(128) NOT NULL,
			status INT NOT NULL
		)`,
		`CREATE TABLE IF NOT EXISTS demo.order_info (
			id BIGINT PRIMARY KEY AUTO_INCREMENT,
			order_no VARCHAR(64) NOT NULL,
			user_id BIGINT NOT NULL,
			amount DECIMAL(10,2) NOT NULL,
			created_at DATETIME NOT NULL,
			INDEX idx_order_user_id (user_id)
		)`,
	}
	for _, stmt := range stmts {
		if _, err = adminDB.ExecContext(ctx, stmt); err != nil {
			return fmt.Errorf("exec init sql failed: %w", err)
		}
	}
	return nil
}
