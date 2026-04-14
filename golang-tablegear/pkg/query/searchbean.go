package query

import (
	"reflect"
	"strings"
)

const (
	beginPrefix = "begin_"
	endPrefix   = "end_"
)

type rangeVal struct {
	begin any
	end   any
}

func ApplySearchBean(b *Builder, bean any, fieldMap map[string]NamedField) {
	if b == nil || bean == nil {
		return
	}
	v := reflect.ValueOf(bean)
	if v.Kind() == reflect.Ptr {
		if v.IsNil() {
			return
		}
		v = v.Elem()
	}
	if v.Kind() != reflect.Struct {
		return
	}

	normMap := make(map[string]string, len(fieldMap))
	for k, f := range fieldMap {
		normMap[normalizeField(k)] = f.Column()
	}

	ranges := map[string]rangeVal{}
	t := v.Type()
	for i := 0; i < v.NumField(); i++ {
		sf := t.Field(i)
		if !sf.IsExported() {
			continue
		}
		fv := v.Field(i)
		name := sf.Name
		switch name {
		case "Start", "start":
			if n, ok := toInt(fv); ok {
				b.Offset(n)
			}
			continue
		case "Count", "count":
			if n, ok := toInt(fv); ok && n > 0 {
				b.Limit(n)
			}
			continue
		case "SortBy", "sortBy":
			if s, ok := toString(fv); ok && strings.TrimSpace(s) != "" {
				applySort(b, s, normMap)
			}
			continue
		}
		val, ok := derefValue(fv)
		if !ok {
			continue
		}
		norm := normalizeField(name)
		if strings.HasPrefix(norm, beginPrefix) {
			target := strings.TrimPrefix(norm, beginPrefix)
			col, exists := normMap[target]
			if !exists {
				continue
			}
			r := ranges[col]
			r.begin = val
			ranges[col] = r
			continue
		}
		if strings.HasPrefix(norm, endPrefix) {
			target := strings.TrimPrefix(norm, endPrefix)
			col, exists := normMap[target]
			if !exists {
				continue
			}
			r := ranges[col]
			r.end = val
			ranges[col] = r
			continue
		}

		col, exists := normMap[norm]
		if !exists {
			continue
		}
		switch val.Kind() {
		case reflect.String:
			s := strings.TrimSpace(val.String())
			if s == "" {
				continue
			}
			if strings.HasSuffix(s, "%") || strings.HasSuffix(s, "*") {
				b.Where(Predicate{
					SQL:  col + " LIKE ?",
					Args: []any{s[:len(s)-1] + "%"},
				})
			} else if strings.HasPrefix(s, "%") || strings.HasPrefix(s, "*") {
				b.Where(LikeField(col, s[1:]))
			} else {
				b.Where(EqField(col, s))
			}
		case reflect.Slice, reflect.Array:
			values := asAnySlice(val)
			if len(values) > 0 {
				b.Where(InField(col, values...))
			}
		default:
			b.Where(EqField(col, val.Interface()))
		}
	}

	for col, rv := range ranges {
		if rv.begin != nil && rv.end != nil {
			b.Where(BetweenField(col, rv.begin, rv.end))
		} else if rv.begin != nil {
			b.Where(GeField(col, rv.begin))
		} else if rv.end != nil {
			b.Where(LeField(col, rv.end))
		}
	}
}

func applySort(b *Builder, sortBy string, normMap map[string]string) {
	items := strings.Split(sortBy, ",")
	for _, item := range items {
		item = strings.TrimSpace(item)
		if item == "" {
			continue
		}
		name := item
		order := "asc"
		if strings.Contains(item, ":") {
			parts := strings.SplitN(item, ":", 2)
			name = parts[0]
			order = strings.ToLower(strings.TrimSpace(parts[1]))
		}
		col, ok := normMap[normalizeField(name)]
		if !ok {
			continue
		}
		f := rawField{name: col}
		if order == "desc" {
			b.OrderByDesc(f)
		} else {
			b.OrderByAsc(f)
		}
	}
}

type rawField struct {
	name string
}

func (r rawField) Column() string { return r.name }

func normalizeField(s string) string {
	return strings.ToLower(strings.ReplaceAll(s, "_", ""))
}

func derefValue(v reflect.Value) (reflect.Value, bool) {
	for v.Kind() == reflect.Ptr {
		if v.IsNil() {
			return reflect.Value{}, false
		}
		v = v.Elem()
	}
	return v, true
}

func toString(v reflect.Value) (string, bool) {
	v, ok := derefValue(v)
	if !ok || v.Kind() != reflect.String {
		return "", false
	}
	return v.String(), true
}

func toInt(v reflect.Value) (int, bool) {
	v, ok := derefValue(v)
	if !ok {
		return 0, false
	}
	switch v.Kind() {
	case reflect.Int, reflect.Int8, reflect.Int16, reflect.Int32, reflect.Int64:
		return int(v.Int()), true
	case reflect.Uint, reflect.Uint8, reflect.Uint16, reflect.Uint32, reflect.Uint64:
		return int(v.Uint()), true
	default:
		return 0, false
	}
}
