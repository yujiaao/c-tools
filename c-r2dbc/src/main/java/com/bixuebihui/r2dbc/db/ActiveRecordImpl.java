package com.bixuebihui.r2dbc.db;


import com.bixuebihui.DbException;
import com.bixuebihui.db.*;
import com.bixuebihui.jdbc.SqlFilter;
import com.bixuebihui.jdbc.SqlSort;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;
import com.bixuebihui.r2dbc.sql.*;
import io.r2dbc.spi.Connection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import java.util.*;

import static com.bixuebihui.db.Dialect.*;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-4-18 Time: 下午5:57 To
 * change this template use File | Settings | File Templates.
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class ActiveRecordImpl<T, V> implements ActiveRecord<T> {

	private static final Logger LOG = LoggerFactory.getLogger(ActiveRecordImpl.class);
	/**
	 * NOTE that: class field make this class can't use in spring as singleton!
	 */
	SqlSort orderStack;
	SqlHelper filterStack;

	public SqlLimit getLimit() {
		return limit;
	}

	SqlLimit limit;
	BaseDao<T, V> operator;
	Mono<Connection> cn;
	private SqlPocket sqlPocket;
	private boolean useLast=false;

	private String tableAlias;
	private String resultFields =" * ";
	private List<String> joins;


	private void init(BaseDao<T, V> operator){
		this.operator = operator;
		orderStack = new SqlSort();
		filterStack = new SqlHelper();
		limit = SqlLimit.LIMIT_MAX;
	}

	/**
	 * <p>Constructor for ActiveRecordImpl.</p>
	 *
	 * @param operator a {@link BaseDao} object.
	 */
	public ActiveRecordImpl(BaseDao<T, V> operator) {
		init(operator);
	}

	/**
	 * <p>Constructor for ActiveRecordImpl.</p>
	 *
	 * @param operator a {@link BaseDao} object.
	 * @param cn a {@link Connection} object.
	 */
	public ActiveRecordImpl(BaseDao<T, V> operator, Mono<Connection> cn) {
		this.cn = cn;
		init(operator);
	}


	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> in(String field, Object[] values) {
		filterStack.in(field, values);
		return this;
	}

	@Override
	public ActiveRecord<T> emptyStringAsNullCondition() {
		filterStack.setAcceptEmptyStringAsNullObjectInCondition(true);
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> in(String field, Object values) {
		if(!(values instanceof Collection) && !(values instanceof SqlString)){
			throw new  IllegalArgumentException("InFilter values must be Collection or SqlString objects");
		}
		if (values instanceof Collection) {
			filterStack.in(field, (Collection) values);
		} else {
			filterStack.in(field, (SqlString) values);
		}

		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> like(String field, String value) {
		filterStack.like(field, value);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> startWith(String field, String value) {
		filterStack.startWith(field, value);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> eq(String field, Object value) {
		filterStack.eq(field, value);
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> ne(String field, Object value){
		filterStack.ne(field, value);
		return this;
	}


	/** {@inheritDoc} */

	@Override
	public ActiveRecord<T> eq(String[] fields, Object[] value) {
		filterStack.eq(fields, value);
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> greaterThan(String field, Object value) {
		filterStack.greaterThan(field, value);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> smallerThan(String field, Object value) {
		filterStack.smallerThan(field, value);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> greaterOrEqualThan(String field, Object value) {
		filterStack.greaterOrEqualThan(field, value);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> smallerOrEqualThan(String field, Object value) {
		filterStack.smallerOrEqualThan(field, value);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public SqlHelper getCondStack(){
		return filterStack;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> or(SqlHelper andStack){
		filterStack.or(andStack);
		return this;
	}

	protected ActiveRecord<T> order(String field, int order) {
		orderStack.addSort(field, order == ORDER_DESC ? SqlSort.Sort.DESC	: SqlSort.Sort.ASC);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> orderByIn(String field, Object[] order) {
		operator.getDbType().flatMap(type -> {
			if( type != DERBY) {
				orderStack.addSortByIn(field, order);
			}
			return null;
		}).subscribe();
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> limit(int begin, int num) {
		limit = new SqlLimit(begin, num);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Mono<List<T>> findAll() throws DbException {
		try {
			sqlPocket = this.getSql();
			String where = formWhereClause();
			Object[] params = getParams().toArray();

			return operator.selectWithJoin(resultFields, where, params, parseOrder(),
					limit.getBegin(), limit.getEnd());
		} finally {
			clear();
		}
	}

	protected List<Object> getParams(){
		List<Object> res = sqlPocket.getParams();

		operator.getDbType().flatMap(type -> {
			if(type!= DERBY) {
				res.addAll(orderStack.getParams());
			}
			return null;
		}).subscribe();

		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <K> Mono<List<K>> findAll(Class<K> clz) throws DbException {
		try {
			sqlPocket = this.getSql();
			String where = formWhereClause();
			Object[] params = getParams().toArray();
			String select = "select " + resultFields + " from " + operator.getTableName() + " ";
			return operator.select(select, where, parseOrder(), params, limit.getBegin(),
					limit.getEnd(), clz);
		} finally {
			clear();
		}
	}

	/**
	 * <p>formWhereClause.</p>
	 *
	 * @return a {@link String} object.
	 */
	protected String formWhereClause() {
		String res = sqlPocket.getCondition().toString();
		if(StringUtils.isNotBlank(this.tableAlias) ){
			StringBuilder join =new StringBuilder();
			if(joins !=null && joins.size()>0){
				for(String t : joins) {
					join.append(t);
				}
			}
			res = " "+this.tableAlias +" "+join+" "+res;
		}
		return res;
	}

	private void clear() {
		this.filterStack.clear();
		this.orderStack.clear();
		this.joins =null;
		this.limit = SqlLimit.LIMIT_MAX;
		useLast = false;
		this.cn = null;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Mono<T> find() throws DbException {
		try {
			sqlPocket = this.getSql();
			String where = formWhereClause();
			Object[] params = getParams().toArray();
			Mono<List<T>> res = operator.selectWithJoin(resultFields, where, params, parseOrder(),
					SqlLimit.LIMIT_ONE.getBegin(), SqlLimit.LIMIT_ONE.getEnd());

			return res.map(list -> list.isEmpty()?null: list.get(0));
		} finally {
			clear();
		}

	}

	/**
	 * {@inheritDoc}
	 * @return
	 */
	@Override
	public Mono<Boolean> delete() throws DbException {
		try {
			sqlPocket = this.getSql();
			String where = formWhereClause();
			Object[] params = getParams().toArray();
			if (where.length() < " where ".length()) {
				return Mono.just(false);
			}
			String sql = "delete from " + operator.getTableName() + " " + where;

			if (limit != null && limit.getBegin()==0 && limit.getEnd()>0) {
				sql +=" limit "+limit.getEnd();
			}
			if(cn==null) {
				return operator.getDbHelper().executeNoQuery(sql, params).map(x -> x>0);
			}
			String finalSql = sql;
			return cn.flatMap(connection -> operator.getDbHelper().executeNoQuery(finalSql, params, connection)).map(x -> x > 0 );
		} finally {
			clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Mono<Object> get(String field) throws DbException {
		try {
			String sql = getVectorSql(field);
			Object[] params = getParams().toArray();
			if (cn == null) {
				return operator.getDbHelper().executeScalar(sql, params);
			}
			return operator.getDbHelper().executeScalar(sql, params, cn);
		} finally {
			clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Mono<List<Object>> getVector(String field) throws DbException {
		try {
			String sql = getVectorSql(field);
			Object[] params = getParams().toArray();
			RowCallbackHandler<Object> handle = result -> Flux.from(result.map((row, meta) -> row.get(0))).collectList();
			return operator.getDbHelper().executeQuery(sql, params, handle);
		} finally {
			clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Mono<List<Long>> getLongVector(String field) throws DbException {
		try {
			String sql = getVectorSql(field);
			Object[] params = getParams().toArray();

			return operator.getDbHelper().executeQuery(sql, params, new LongReader());

		} finally {
			clear();
		}
	}

	public Mono<String> parseLimitSql() {
		return operator.getDbType().flatMap(type  -> {
			if (type == MYSQL || type == H2) {

				if (limit == null) {
					limit = SqlLimit.LIMIT_ONE;
				}

				return Mono.just(limit.toString());
			} else {
				LOG.warn("limit not implemented for this type of BaseDao.getDBTYPE()=" + operator.getDbType());
				return Mono.just("");
			}
		});
	}


	private String getVectorSql(String field) throws DbException {
		field = SqlFilter.transactSQLInjection(field);
		sqlPocket = this.getSql();
		String where = formWhereClause();
		where += " " + parseOrder();
		where += " " + parseLimitSql();

		return "select " + field + " from " + operator.getTableName()
				+ " " + where;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SqlPocket getSql() throws DbException {
		if (useLast && sqlPocket != null) {
			return sqlPocket;
		}
		sqlPocket = filterStack.build();
		return sqlPocket;
	}

	public String parseOrder() {
		return this.orderStack.toString();
	}

	/**
	 * {@inheritDoc}
	 * @return
	 */
	@Override
	public Mono<Integer> count() throws DbException {
		try {
			sqlPocket = this.getSql();
			String where = formWhereClause();
			Object[] params = getParams().toArray();
			return operator.countWhere(where, params);
		} finally {
			clear();
		}
	}

	/**
	 * {@inheritDoc}
	 * @return
	 */
	@Override
	public Mono<Boolean> exists() throws DbException {
		try {
			sqlPocket = this.getSql();
			String where = formWhereClause();
			Object[] params = getParams().toArray();
			return operator.exists(where, params);
		} finally {
			clear();
		}
	}


	/**
	 * <p>Constructor for ActiveRecordImpl.</p>
	 *
	 * @param src a {@link ActiveRecordImpl} object.
	 */
	public ActiveRecordImpl(ActiveRecordImpl src) {
		this.filterStack = new SqlHelper(src.filterStack);
		this.limit = new SqlLimit(src.limit);
		this.orderStack = new SqlSort(this.orderStack);
	}

	/** {@inheritDoc} */
	@Override
	public Record<T> last() {
		if(sqlPocket==null){
			LOG.error("ERROR: ActiveRecordImpl.last() - there not conditions set to use last.");
		}
		useLast = true;
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> asc(String field) {
		return this.order(field, ORDER_ASC);
	}


	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> desc(String field) {
		return this.order(field, ORDER_DESC);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Mono<List<String>> getStringVector(String field) throws DbException {
		try {
			String sql = getVectorSql(field);
			Object[] params = getParams().toArray();
			RowCallbackHandler<String> handle = result -> Flux.from(result.map((row, meta) -> Objects.requireNonNull(row.get(0)).toString())).collectList();
			return  operator.getDbHelper().executeQuery(sql, params, handle );
		} finally {
			clear();
		}
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> alias(String shortName) {
		tableAlias = shortName;
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> fields(String resultFields) {
		this.resultFields = resultFields;
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> join(String joinClause) {
		if (joins == null) {
			joins = new ArrayList<>();
		}
		joins.add(joinClause);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public Mono<CountValue> countSum(String field)throws DbException {
		 return countValue(field, GroupFunction.SUM);
	}

	/** {@inheritDoc}
	 * @return*/
	@Override
	@Deprecated
	public Mono<CountValue> countValue(String field, GroupFunction fun) {
		try {
			sqlPocket = this.getSql();

			String where = formWhereClause();

			Object[] params = getParams().toArray();

			Mono<List<CountValue>> res = operator.countGroupValue(field, fun.toString(), where,
					null, null, params);

			return res.map(list -> list.size() == 0 ? new CountValue() : list.get(0));
		} finally {
			clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Mono<Integer> inc(@NotNull String field){
		try {
			sqlPocket = this.getSql();
			String whereClause = formWhereClause();
			Object[] params = getParams().toArray();
			String sql = "update " + operator.getTableName()
					+ " set " + field + "=" + field + "+1 " + whereClause;
			if (cn==null) {
				return operator.getDbHelper().executeNoQuery(sql, params);
			}
			return cn.flatMap(conn ->
			  operator.getDbHelper().executeNoQuery(sql, params, conn)
			);
		} finally {
			clear();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Mono<Integer> update(@NotNull String[] fields, @NotNull Object[] values) throws DbException {
		try {
			if (fields.length != values.length) {
				throw new IllegalArgumentException("fields and values must have some length");
			}

			sqlPocket = this.getSql();
			String whereClause = formWhereClause();

			List<Object> params = new ArrayList<>();
			StringBuilder sb = new StringBuilder();
			int i=0;
			for(String field:fields){
				Object val = values[i];
				if(val instanceof SqlString){
					sb.append(field).append(" = ").append(val);
				}else{
					sb.append(field).append(" = ? ");
					params.add(val);
				}
				i++;
				if(i<fields.length) {
					sb.append(",");
				}
			}

			Collections.addAll(params, getParams().toArray());

			String sql ="update "+operator.getTableName()+" set "+
					sb.toString()+ whereClause;
			if(cn==null) {
				return operator.getDbHelper().executeNoQuery(sql, params.toArray());
			}
			return cn.flatMap(conn ->
					operator.getDbHelper().executeNoQuery(sql, params.toArray(), conn)
			);
		} finally {
			clear();
		}
	}

	/** {@inheritDoc} */
	@Override
	public Mono<Integer> update(@NotNull  String fields, Object values)throws DbException {
		return update(new String[]{fields}, new Object[]{values});
	}

	/** {@inheritDoc} */
	@Override
	public <K> Mono<CountObject<K>> countObject(String field, GroupFunction fun, Class<K> objectType)
			 {
		try {
			sqlPocket = this.getSql();

			String where = formWhereClause();

			Object[] params = getParams().toArray();

			Mono<List<CountObject<K>>> res =  operator.countGroupObject(field, fun.toString(), where,
					null, null, objectType, params);

			return res.map(list ->  list.size() == 0 ? new CountObject<>() : list.get(0));
		} finally {
			clear();
		}
	}



}
