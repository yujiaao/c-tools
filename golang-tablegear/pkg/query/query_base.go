package query

// QueryBase provides reusable fluent APIs for generated query types.
// Q is the concrete generated query type, enabling fluent methods
// to return the concrete type instead of a generic base type.
type QueryBase[Q any] struct {
	*Builder
	self *Q
}

func NewQueryBase[Q any](self *Q) *QueryBase[Q] {
	return &QueryBase[Q]{
		Builder: New(),
		self:    self,
	}
}

func (q *QueryBase[Q]) Where(parts ...Predicate) *Q {
	q.Builder.Where(parts...)
	return q.self
}

func (q *QueryBase[Q]) And(parts ...Predicate) *Q {
	q.Builder.Where(AndGroup(parts...))
	return q.self
}

func (q *QueryBase[Q]) Or(parts ...Predicate) *Q {
	q.Builder.Where(Or(parts...))
	return q.self
}

func (q *QueryBase[Q]) Not(part Predicate) *Q {
	q.Builder.Where(Not(part))
	return q.self
}

func (q *QueryBase[Q]) OrderByAsc(field NamedField) *Q {
	q.Builder.OrderByAsc(field)
	return q.self
}

func (q *QueryBase[Q]) OrderByDesc(field NamedField) *Q {
	q.Builder.OrderByDesc(field)
	return q.self
}

func (q *QueryBase[Q]) Limit(n int) *Q {
	q.Builder.Limit(n)
	return q.self
}

func (q *QueryBase[Q]) Offset(n int) *Q {
	q.Builder.Offset(n)
	return q.self
}
