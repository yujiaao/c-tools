package query

import "testing"

func TestBuilder_CompositePredicates(t *testing.T) {
	id := Field[int64]{Name: "id"}
	status := Field[int]{Name: "status"}
	name := Field[string]{Name: "name"}

	b := New().
		Where(
			Eq(status, 1),
			Or(
				Like(name, "alice"),
				Like(name, "bob"),
			),
			Not(Eq(id, int64(9))),
		).
		OrderByDesc(id).
		Limit(20).
		Offset(40)

	where, args := b.BuildWhere()
	if where == "" {
		t.Fatalf("where should not be empty")
	}
	wantWhere := " WHERE status = ? AND ((name LIKE ?) OR (name LIKE ?)) AND NOT (id = ?)"
	if where != wantWhere {
		t.Fatalf("unexpected where:\nwant: %s\ngot : %s", wantWhere, where)
	}
	if len(args) != 4 {
		t.Fatalf("unexpected args length: %d", len(args))
	}
	if args[0] != 1 || args[1] != "%alice%" || args[2] != "%bob%" || args[3] != int64(9) {
		t.Fatalf("unexpected args: %#v", args)
	}

	order := b.BuildOrder()
	if order != " ORDER BY id DESC" {
		t.Fatalf("unexpected order: %s", order)
	}
	limit, offset, hasLimit := b.Page()
	if !hasLimit || limit != 20 || offset != 40 {
		t.Fatalf("unexpected page: limit=%d offset=%d hasLimit=%v", limit, offset, hasLimit)
	}
}

func TestBuilder_NotInAndBetween(t *testing.T) {
	status := Field[int]{Name: "status"}
	amount := Field[float64]{Name: "amount"}
	id := Field[int64]{Name: "id"}

	b := New().Where(
		NotIn(status, 2, 3, 4),
		Between(amount, 10.5, 99.9),
		NotBetween(id, int64(1), int64(10)),
	)
	where, args := b.BuildWhere()
	wantWhere := " WHERE status NOT IN (?,?,?) AND amount BETWEEN ? AND ? AND id NOT BETWEEN ? AND ?"
	if where != wantWhere {
		t.Fatalf("unexpected where:\nwant: %s\ngot : %s", wantWhere, where)
	}
	if len(args) != 7 {
		t.Fatalf("unexpected args length: %d", len(args))
	}
	if args[0] != 2 || args[1] != 3 || args[2] != 4 {
		t.Fatalf("unexpected notIn args: %#v", args[:3])
	}
	if args[3] != 10.5 || args[4] != 99.9 {
		t.Fatalf("unexpected between args: %#v", args[3:5])
	}
	if args[5] != int64(1) || args[6] != int64(10) {
		t.Fatalf("unexpected notBetween args: %#v", args[5:])
	}
}
