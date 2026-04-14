package runtime

import (
	"context"
	"database/sql"
	"log"
	"regexp"
	"strconv"
	"strings"
	"sync/atomic"
	"time"
)

type DBConfig struct {
	SlowSQLThreshold     time.Duration
	VerySlowSQLThreshold time.Duration
	EnableRedact         bool
}

type DB struct {
	master     *sql.DB
	slaves     []*sql.DB
	readCursor uint64
	logger     *log.Logger
	stats      *SQLStats
	cfg        DBConfig
}

func NewDB(db *sql.DB, logger *log.Logger, cfg DBConfig) *DB {
	return NewRWDB(db, nil, logger, cfg)
}

func NewRWDB(master *sql.DB, slaves []*sql.DB, logger *log.Logger, cfg DBConfig) *DB {
	if logger == nil {
		logger = log.Default()
	}
	if cfg.SlowSQLThreshold <= 0 {
		cfg.SlowSQLThreshold = 500 * time.Millisecond
	}
	if cfg.VerySlowSQLThreshold <= 0 {
		cfg.VerySlowSQLThreshold = 2 * time.Second
	}
	return &DB{
		master: master,
		slaves: slaves,
		logger: logger,
		stats:  NewSQLStats(),
		cfg:    cfg,
	}
}

func (d *DB) Raw() *sql.DB { return d.master }

func (d *DB) Stats() map[string]SQLStat { return d.stats.Snapshot() }

func (d *DB) ExecContext(ctx context.Context, query string, args ...any) (sql.Result, error) {
	targetDB := d.pickDB(ctx, true)
	start := time.Now()
	res, err := targetDB.ExecContext(ctx, query, args...)
	d.after(query, time.Since(start), err)
	return res, err
}

func (d *DB) QueryContext(ctx context.Context, query string, args ...any) (*sql.Rows, error) {
	targetDB := d.pickDB(ctx, false)
	start := time.Now()
	rows, err := targetDB.QueryContext(ctx, query, args...)
	d.after(query, time.Since(start), err)
	return rows, err
}

func (d *DB) QueryRowContext(ctx context.Context, query string, args ...any) *sql.Row {
	targetDB := d.pickDB(ctx, false)
	start := time.Now()
	row := targetDB.QueryRowContext(ctx, query, args...)
	d.after(query, time.Since(start), nil)
	return row
}

func (d *DB) WithTx(ctx context.Context, fn func(tx *sql.Tx) error) error {
	targetDB := d.pickDB(ctx, true)
	tx, err := targetDB.BeginTx(ctx, nil)
	if err != nil {
		return err
	}
	if err = fn(tx); err != nil {
		_ = tx.Rollback()
		return err
	}
	return tx.Commit()
}

type DBRole string

const (
	DBRoleAuto   DBRole = "auto"
	DBRoleMaster DBRole = "master"
	DBRoleSlave  DBRole = "slave"
)

type dbRoleContextKey struct{}

func WithDBRole(ctx context.Context, role DBRole) context.Context {
	if ctx == nil {
		ctx = context.Background()
	}
	return context.WithValue(ctx, dbRoleContextKey{}, role)
}

func (d *DB) pickDB(ctx context.Context, isWrite bool) *sql.DB {
	role := DBRoleAuto
	if ctx != nil {
		if v, ok := ctx.Value(dbRoleContextKey{}).(DBRole); ok {
			role = v
		}
	}
	switch role {
	case DBRoleMaster:
		return d.master
	case DBRoleSlave:
		if db := d.pickSlave(); db != nil {
			return db
		}
		return d.master
	default:
		if isWrite {
			return d.master
		}
		if db := d.pickSlave(); db != nil {
			return db
		}
		return d.master
	}
}

func (d *DB) pickSlave() *sql.DB {
	if len(d.slaves) == 0 {
		return nil
	}
	idx := atomic.AddUint64(&d.readCursor, 1)
	return d.slaves[(idx-1)%uint64(len(d.slaves))]
}

func (d *DB) after(query string, cost time.Duration, err error) {
	sqlText := normalizeSQL(query)
	normalized := maybeReducePlaceholders(sqlText)
	if d.cfg.EnableRedact {
		normalized = redactSQL(normalized)
	}
	slow := cost >= d.cfg.SlowSQLThreshold
	d.stats.Add(normalized, cost, slow)
	if err != nil {
		d.logger.Printf("[sql][error] cost=%s sql=%s err=%v", cost, normalized, err)
		return
	}
	if cost >= d.cfg.VerySlowSQLThreshold {
		d.logger.Printf("[sql][very-slow] cost=%s sql=%s", cost, normalized)
		return
	}
	if slow {
		d.logger.Printf("[sql][slow] cost=%s sql=%s", cost, normalized)
	}
}

func normalizeSQL(query string) string {
	res := strings.TrimSpace(query)
	res = strings.ReplaceAll(res, "\n", " ")
	for strings.Contains(res, "  ") {
		res = strings.ReplaceAll(res, "  ", " ")
	}
	return res
}

var placeholderSeq = regexp.MustCompile(`(\?,\s*){20,}\?`)
var quotedValue = regexp.MustCompile(`'[^']*'`)

func maybeReducePlaceholders(sqlText string) string {
	return placeholderSeq.ReplaceAllStringFunc(sqlText, func(in string) string {
		num := strings.Count(in, "?")
		if num <= 2 {
			return in
		}
		return "?, ...omit " + itoa(num-2) + ", ?"
	})
}

func redactSQL(in string) string {
	return quotedValue.ReplaceAllString(in, "'***'")
}

func itoa(v int) string {
	return strconv.Itoa(v)
}
