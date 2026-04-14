package runtime

import (
	"sync"
	"time"
)

type SQLStat struct {
	Count       int64
	SlowCount   int64
	Total       time.Duration
	MaxDuration time.Duration
}

type SQLStats struct {
	mu   sync.RWMutex
	data map[string]SQLStat
}

func NewSQLStats() *SQLStats {
	return &SQLStats{data: make(map[string]SQLStat)}
}

func (s *SQLStats) Add(sql string, cost time.Duration, slow bool) {
	s.mu.Lock()
	defer s.mu.Unlock()
	st := s.data[sql]
	st.Count++
	if slow {
		st.SlowCount++
	}
	st.Total += cost
	if cost > st.MaxDuration {
		st.MaxDuration = cost
	}
	s.data[sql] = st
}

func (s *SQLStats) Snapshot() map[string]SQLStat {
	s.mu.RLock()
	defer s.mu.RUnlock()
	out := make(map[string]SQLStat, len(s.data))
	for k, v := range s.data {
		out[k] = v
	}
	return out
}
