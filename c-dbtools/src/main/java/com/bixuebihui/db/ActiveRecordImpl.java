package com.bixuebihui.db;

import com.bixuebihui.DbException;
import com.bixuebihui.jdbc.*;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;
import com.bixuebihui.query.Paging;
import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import java.util.function.Function;

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
	private boolean keepNullForNumberField = false;
	private Function<SqlHelper, SqlHelper> filterInterceptor = it -> it;
	private Function<SqlHelper, SqlHelper>  havingInterceptor = it -> it;
	private Function<SqlSort, SqlSort> sortInterceptor= it -> it;
	private Function<SqlGroup, SqlGroup> groupInterceptor= it -> it;

	public SqlLimit getLimit() {
		return limit;
	}

	public void setLimit(SqlLimit limit) {
		this.limit = limit;
	}

	SqlLimit limit;

	SqlGroup groupByClause;
	SqlHelper havingStack;


	BaseDao<T, V> operator;
	Connection cn;
	private SqlPocket sqlPocket;
	private boolean useLast=false;

	private String tableAlias;

	public String getTableName() {
		return tableName==null? operator.getTableName(): tableName;
	}


	private  String tableName;

	private String resultFields =" * ";


	public String getResultFields() {
		return resultFields;
	}

	@Override
	public Set<String> getResultFieldsSet(){
		return ActiveRecord.toFieldsSet(resultFields);
	}


	private List<String> joins;


	private void init(BaseDao<T, V> operator){
		this.operator = operator;
		orderStack = new SqlSort();
		filterStack = new SqlHelper();
		groupByClause = new SqlGroup();
		havingStack = new SqlHelper(true);
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
	 * @param cn a {@link java.sql.Connection} object.
	 */
	public ActiveRecordImpl(BaseDao<T, V> operator, Connection cn) {
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
	public ActiveRecord<T> in(String field, List<?> values) {
		 filterStack.in(field, values);
		 return this;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> notIn(String field, Object[] values) {
		filterStack.notIn(field, values);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ActiveRecord<T> notIn(String field,  List<?>  values) {
		filterStack.notIn(field, values);
		return this;
	}



	@Override
	public ActiveRecord<T> emptyStringAsNullCondition() {
		filterStack.setAcceptEmptyStringAsNullObjectInCondition(true);
		return this;
	}

	@Override
	public ActiveRecord<T> keepNullForNumberField() {
		 this.keepNullForNumberField = true;
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

	@Override
	public ActiveRecord<T> between(String field, Object leftValue, Object rightValue) {
		filterStack.between(field, leftValue, rightValue);
		return this;
	}

	@Override
	public ActiveRecord<T> notBetween(String field, Object leftValue, Object rightValue) {
		filterStack.notBetween(field, leftValue, rightValue);
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
	public ActiveRecord<T> eq(String[] fields, Object[] value)
			throws SQLException {
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

	@Override
	public ActiveRecord<T> isNull(String field) {
		filterStack.isNull(field);
		return this;
	}

	@Override
	public ActiveRecord<T> isNotNull(String field) {
		filterStack.isNotNull(field);
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

	@Override
	public ActiveRecord<T> wildcard(String field, String[] value) {
		throw new NotImplementedException();
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
		if(operator.getDbType()!=DERBY) {
			orderStack.addSortByIn(field, order);
		}
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
	public List<T> findAll() throws DbException {
		try {
			sqlPocket = this.getSql();
			SqlPocket having = parseHaving();
			String where = formWhereClause()+parseGroupBy()+having.getCondition();
			List<Object> list = Arrays.asList(getParams().toArray());
			list.addAll(having.getParams());

			return operator.selectWithJoin(resultFields, where, list.toArray(), parseOrder(),
					limit.getBegin(), limit.getEnd(), tableName);
		} finally {
			clear();
		}
	}

	protected List<Object> getParams(){
		List<Object> res = sqlPocket.getParams();
		if(operator.getDbType()!= DERBY) {
			res.addAll(orderStack.getParams());
		}
		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <K> List<K> findAll(Class<K> clz) throws DbException {
		try {
			sqlPocket = this.getSql();
			SqlPocket having = parseHaving();
			String where = formWhereClause()+parseGroupBy()+having.getCondition();
			List<Object> list = Arrays.asList(getParams().toArray());
			list.addAll(having.getParams());
			String select = "select " + resultFields + " from " + this.getTableName() + " ";
			return operator.select(select, where, parseOrder(), list.toArray(), limit.getBegin(),
					limit.getEnd(), clz);
		} finally {
			clear();
		}
	}

	/**
	 * <p>formWhereClause.</p>
	 *
	 * @return a {@link java.lang.String} object.
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

	public void clear() {
		this.filterStack.clear();
		this.orderStack.clear();
		this.groupByClause.clear();
		this.havingStack.clear();
		this.joins =null;
		this.limit = SqlLimit.LIMIT_MAX;
		useLast = false;
		this.cn = null;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public T find() throws DbException {
		try {
			sqlPocket = this.getSql();
			SqlPocket having = parseHaving();
			String where = formWhereClause()+parseGroupBy()+having.getCondition();
			List<Object> list = Arrays.asList(getParams().toArray());
			list.addAll(having.getParams());
			List<T> res = operator.selectWithJoin(resultFields, where, list.toArray(), parseOrder(),
					SqlLimit.LIMIT_ONE.getBegin(), SqlLimit.LIMIT_ONE.getEnd(), tableName);
			if (res.size() > 0) {
				return res.get(0);
			}
			return null;
		} finally {
			clear();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean delete() throws DbException {
		try {
			sqlPocket = this.getSql();
			SqlPocket having = parseHaving();
			String where = formWhereClause()+parseGroupBy()+having.getCondition();
			List<Object> list = Arrays.asList(getParams().toArray());
			list.addAll(having.getParams());
			if (where.length() < " where ".length()) {
				return false;
			}
			String sql = "delete from " + this.getTableName() + " " + where;

			if (limit != null && limit.getBegin()==0 && limit.getEnd()>0) {
				//sql +=" limit "+limit.getEnd();
				LOG.warn("DELETE operation with not limit, please make sure this is correct:\n"+sql);
			}
			if(cn==null) {
				return 0 < operator.getDbHelper().executeNoQuery(sql, list.toArray());
			}
			return 0 < operator.getDbHelper().executeNoQuery(sql, list.toArray(), cn);
		} finally {
			clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object get(String field) throws DbException {
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
	public List<Object> getVector(String field) throws DbException {
		try {
			String sql = getVectorSql(field);
			Object[] params = getParams().toArray();
			final List<Object> res = new ArrayList<>();
			RowCallbackHandler handle = resultSet -> res.add(resultSet.getObject(1));
			operator.getDbHelper().executeQuery(sql, params, handle);

			return res;
		} finally {
			clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Long> getLongVector(String field) throws DbException {
		try {
			String sql = getVectorSql(field);
			Object[] params = getParams().toArray();
			final List<Long> res = new ArrayList<>();

			operator.getDbHelper().executeQuery(sql, params, new LongReader(res));

			return res;
		} finally {
			clear();
		}
	}

	public String parseLimitSql() throws DbException {
		if (operator.getDbType() == MYSQL || operator.getDbType() == H2) {

			if (limit == null) {
				limit = SqlLimit.LIMIT_ONE;
			}

			return limit.toString();
		} else {
			LOG.warn("limit not implemented for this type DERBY and others, BaseDao.getDBTYPE()=" + operator.getDbType());
			return "";
		}
	}


	String getVectorSql(String field) throws DbException {
		field = SqlFilter.transactSQLInjection(field);
		sqlPocket = this.getSql();
		String where = formWhereClause();
		where += " " +parseGroupBy();
		where += " " + parseOrder();
		where += " " + parseLimitSql();

		return "select " + field + " from " + this.getTableName()
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
		sqlPocket = filterInterceptor.apply(filterStack).build();
		return sqlPocket;
	}

	public String parseOrder() {
		return sortInterceptor.apply(this.orderStack).toString();
	}

	public String parseGroupBy() {
		return groupInterceptor.apply(this.groupByClause).toString();
	}

	public SqlPocket parseHaving() {
		return havingInterceptor.apply(this.havingStack).toCondition();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public int count() throws DbException {
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
	 */
	@Override
	public boolean exists() throws DbException {
		try {
			sqlPocket = this.getSql();
			SqlPocket having = parseHaving();
			String where = formWhereClause()+parseGroupBy()+having.getCondition();
			List<Object> list = Arrays.asList(getParams().toArray());
			list.addAll(having.getParams());
			return operator.exists(tableName, where, list.toArray());
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
		this.operator = src.operator;
		this.resultFields = src.resultFields;
		this.havingStack = new SqlHelper(src.havingStack);
		this.groupByClause = new SqlGroup(src.groupByClause);
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
	public List<String> getStringVector(String field) throws DbException {
		try {
			String sql = getVectorSql(field);
			Object[] params = getParams().toArray();
			final List<String> res = new ArrayList<>();
			RowCallbackHandler handle = resultSet -> res.add(resultSet.getString(1));
			operator.getDbHelper().executeQuery(sql, params, handle );

			return res;
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

	@Override
	public ActiveRecord<T> tableName(String partitionTableName) {
		this.tableName = partitionTableName;
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

	@Override
	public ActiveRecord<T> groupBy(String... fields) {
		groupByClause.add(fields);
		return this;
	}

	@Override
	public ActiveRecord<T> having(HavingConditon fieldProcessor) {
		 fieldProcessor.apply(havingStack);
		 return this;
	}

	@Override
	public <S> ActiveRecord<T> query(Paging<S> searchBean) {
		return query(searchBean, Set.of(operator.getAllFields().toArray(new String[0])));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public CountValue countSum(String field)throws DbException {
		 return countValue(field, GroupFunction.SUM);
	}

	/** {@inheritDoc} */
	@Override
	@Deprecated
	public CountValue countValue(String field, GroupFunction fun) {
		try {
			sqlPocket = this.getSql();

			String where = formWhereClause();

			SqlPocket sqlPocket = parseHaving();

			List<Object> list = Arrays.asList(getParams().toArray());
			list.addAll(sqlPocket.getParams());

			Object[] params = list.toArray();
			List<CountValue> res = operator.countGroupValue(field, fun.toString(), where,
					sqlPocket.getCondition().toString(), groupInterceptor.apply(groupByClause).toArray(), params);

			return res.size() == 0 ? new CountValue() : res.get(0);
		} finally {
			clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int inc(@NotNull String field) throws DbException {
		try {
			sqlPocket = this.getSql();
			String whereClause = formWhereClause();
			Object[] params = getParams().toArray();
			String sql = "update " + operator.getTableName()
					+ " set " + field + "=" + field + "+1 " + whereClause;
			if (cn==null) {
				return operator.getDbHelper().executeNoQuery(sql, params);
			}
			return operator.getDbHelper().executeNoQuery(sql, params, cn);
		} finally {
			clear();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(@NotNull String[] fields, @NotNull Object[] values) throws DbException {
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
					sb + whereClause;
			if(cn==null) {
				return operator.getDbHelper().executeNoQuery(sql, params.toArray());
			}
			return operator.getDbHelper().executeNoQuery(sql, params.toArray(), cn);
		} finally {
			clear();
		}
	}

	/** {@inheritDoc} */
	@Override
	public int update(@NotNull  String fields, Object values)throws DbException {
		return update(new String[]{fields}, new Object[]{values});
	}

	/** {@inheritDoc} */
	@Override
	public <K> CountObject<K> countObject(String field, GroupFunction fun, Class<K> objectType)
			 {
		try {
			sqlPocket = this.getSql();
			SqlPocket having = parseHaving();
			String where = formWhereClause();
			List<Object> list = Arrays.asList(getParams().toArray());
			list.addAll(having.getParams());
			List<CountObject<K>> res =  operator.countGroupObject(field, fun.toString(), where,
					having.getCondition().toString(), groupInterceptor.apply(groupByClause).toArray(), objectType, list.toArray());

			return res.size() == 0 ? new CountObject<>() : res.get(0);
		} finally {
			clear();
		}
	}


	/** {@inheritDoc} */
	@Override
	public <K> List<CountObject<K>> countList(String returnField, String[] groupByFields, String having, GroupFunction fun, Class<K> objectType)
	{
		try {
			sqlPocket = this.getSql();
			String where = formWhereClause();
			List<Object> list = Arrays.asList(getParams().toArray());
			List<CountObject<K>> res =  operator.countGroupObject(returnField, fun.toString(), where,
					having, groupByFields, objectType, list.toArray());
			return res;
		} finally {
			clear();
		}
	}


	@Override
	public ActiveRecord<T> filterInterceptor(Function<SqlHelper, SqlHelper> filterInterceptor) {
		this.filterInterceptor = filterInterceptor;
		return this;
	}

	@Override
	public ActiveRecord<T> havingInterceptor(Function<SqlHelper, SqlHelper> havingInterceptor) {
		this.havingInterceptor = havingInterceptor;
		return this;
	}

	@Override
	public ActiveRecord<T> sortInterceptor(Function<SqlSort, SqlSort> sortInterceptor) {
		this.sortInterceptor = sortInterceptor;
		return this;
	}

	@Override
	public ActiveRecord<T> groupInterceptor(Function<SqlGroup, SqlGroup> groupInterceptor) {
		this.groupInterceptor = groupInterceptor;
		return this;
	}
}
