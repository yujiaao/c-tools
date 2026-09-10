package com.bixuebihui.db;

import com.bixuebihui.DbException;
import com.bixuebihui.jdbc.*;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;
import com.bixuebihui.query.Paging;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

import static com.bixuebihui.db.Dialect.*;
import static com.bixuebihui.jdbc.BaseDao.getFieldAlias;

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
     * NOTE that: class field makes this class can't use in spring as singleton!
     */
    SqlSort orderStack;
    SqlHelper filterStack;
    SqlLimit limit;
    SqlGroup groupByClause;
    SqlHelper havingStack;
    BaseDao<T, V> operator;
    Connection cn;
    private boolean keepNullForNumberField = false;
    private Function<SqlHelper, SqlHelper> filterInterceptor = Function.identity();
    private Function<SqlHelper, SqlHelper> havingInterceptor = Function.identity();
    private Function<SqlSort, SqlSort> sortInterceptor = Function.identity();
    private Function<SqlGroup, SqlGroup> groupInterceptor = Function.identity();
    private Function<List<Object>, List<Object>> paramsInterceptor = Function.identity();
    private Function<String, String> coreSqlInterceptor = Function.identity();
    private SqlPocket sqlPocket;
    private boolean useLast = false;
    private String tableAlias;
    private String tableName;
    private String resultFields = " * ";
    private List<String> joins;
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
     * @param cn       a {@link java.sql.Connection} object.
     */
    public ActiveRecordImpl(BaseDao<T, V> operator, Connection cn) {
        this.cn = cn;
        init(operator);
    }


    /**
     * <p>Constructor for ActiveRecordImpl.</p>
     *
     * @param src a {@link ActiveRecordImpl} object.
     */
    public ActiveRecordImpl(ActiveRecordImpl<T, V> src) {
        this.filterStack = new SqlHelper(src.filterStack);
        this.limit = new SqlLimit(src.limit);
        this.orderStack = new SqlSort(this.orderStack);
        this.operator = src.operator;
        this.resultFields = src.resultFields;
        this.havingStack = new SqlHelper(src.havingStack);
        this.groupByClause = new SqlGroup(src.groupByClause);
    }


    public SqlLimit getLimit() {
        return limit;
    }

    public void setLimit(SqlLimit limit) {
        this.limit = limit;
    }

    public String getTableName() {
        return coreSqlInterceptor.apply(tableName == null ? operator.getTableName() : tableName);
    }

    public String getResultFields() {
        return resultFields;
    }

    @Override
    public Set<String> getResultFieldsSet() {
        return ActiveRecord.toFieldsSet(resultFields);
    }

    private void init(BaseDao<T, V> operator) {
        this.operator = operator;
        orderStack = new SqlSort();
        filterStack = new SqlHelper();
        groupByClause = new SqlGroup();
        havingStack = new SqlHelper(true);
        limit = SqlLimit.LIMIT_MAX;
    }

    /**
     * {@inheritDoc}
     */
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

    @Override
    public ActiveRecord<T> in(String field, SqlString sqlString) {
        filterStack.in(field, sqlString);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> notIn(String field, Object[] values) {
        filterStack.notIn(field, values);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> notIn(String field, List<?> values) {
        filterStack.notIn(field, values);
        return this;
    }

    @Override
    public ActiveRecord<T> notIn(String field, SqlString sqlString) {
        filterStack.notIn(field, sqlString);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> in(String field, Object values) {
        if(values == Optional.empty()){
            LOG.debug(this.getTableName(), field, " in filter values is empty, ignore it");
            return this;
        }
        if (!(values instanceof Collection) && !(values instanceof SqlString)) {
            throw new IllegalArgumentException("InFilter values must be Collection or SqlString objects");
        }
        if (values instanceof Collection) {
            filterStack.in(field, (Collection<?>) values);
        } else {
            filterStack.in(field, (SqlString) values);
        }

        return this;
    }

    /**
     * -- works in PostgreSQL, Oracle, MySQL, DB2, HSQLDB
     * SELECT whatever
     * FROM t                               --- you missed the FROM
     * WHERE (col1, col2)                    --- parentheses here
     * IN ((val1a, val2a), (val1b, val2b), ...) ;
     * not work in StarRocks, so we use this:
     * SELECT whatever
     * FROM t
     * WHERE ( (col1 = val1a AND col2 = val2a) or (col1 = val1b AND col2 = val2b) or ... );
     * *  see:
     * <a href="https://dba.stackexchange.com/a/34270/127082"> in grammar</a>
     *
     * @param fields primary key fields
     * @param values primary key values
     * @return a {@link ActiveRecord} object.
     */
    @Override
    public ActiveRecord<T> in(Pair<String, String> fields, List<Pair<?, ?>> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("InFilter values must not be empty");
        }
        ActiveRecordImpl<T, V> localAr = new ActiveRecordImpl<>(this.operator);
        values.forEach(it -> localAr.or(new ActiveRecordImpl<>(this.operator)
                .eq(fields.getLeft(), it.getLeft())
                .eq(fields.getRight(), it.getRight())));
        return and(localAr);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> like(String field, String value) {
        filterStack.like(field, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> startWith(String field, String value) {
        filterStack.startWith(field, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> ne(String field, Object value) {
        filterStack.ne(field, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public ActiveRecord<T> eq(String[] fields, Object[] value)
            throws SQLException {
        filterStack.eq(fields, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> greaterThan(String field, Object value) {
        filterStack.greaterThan(field, value);
        return this;
    }

    @Override
    public ActiveRecord<T> gt(String field, Object value) {
        return greaterThan(field, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> smallerThan(String field, Object value) {
        filterStack.smallerThan(field, value);
        return this;
    }

    @Override
    public ActiveRecord<T> sql(String field, Object... value) {
        filterStack.sql(field, value);
        return this;
    }

    @Override
    public ActiveRecord<T> lt(String field, Object value) {
        return smallerThan(field, value);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> greaterOrEqualThan(String field, Object value) {
        filterStack.greaterOrEqualThan(field, value);
        return this;
    }

    @Override
    public ActiveRecord<T> ge(String field, Object value) {
        return greaterOrEqualThan(field, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> smallerOrEqualThan(String field, Object value) {
        filterStack.smallerOrEqualThan(field, value);
        return this;
    }

    @Override
    public ActiveRecord<T> le(String field, Object value) {
        return smallerOrEqualThan(field, value);
    }

    @Override
    public ActiveRecord<T> wildcard(String field, String[] value) {
        throw new NotImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlHelper getCondStack() {
        return filterStack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> or(SqlHelper orStack) {
        filterStack.or(orStack);
        return this;
    }

    @Override
    public ActiveRecord<T> and(SqlHelper andStack) {
        filterStack.and(andStack);
        return this;
    }

    @Override
    public ActiveRecord<T> not(SqlHelper notStack) {
        filterStack.not(notStack);
        return this;
    }

    protected ActiveRecord<T> order(String field, int order) {
        orderStack.addSort(field, order == ORDER_DESC ? SqlSort.Sort.DESC : SqlSort.Sort.ASC);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> orderByIn(String field, Object[] order) {
        if (operator.getDbType() != DERBY) {
            orderStack.addSortByIn(field, order);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
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
            String where = formJoinAndWhereClause() + parseGroupBy() + having.getCondition();
            List<Object> list = Arrays.asList(getParams().toArray());
            list.addAll(having.getParams());

            return operator.selectWithJoin(resultFields, where, list.toArray(), parseOrder(),
                    limit.getBegin(), limit.getEnd(), getTableName());
        } finally {
            clear();
        }
    }

    @Override
    public Pair<Long, List<T>> findAllWithCount() throws DbException {
        try {
            sqlPocket = this.getSql();
            SqlPocket having = parseHaving();
            String where = formJoinAndWhereClause() + parseGroupBy() + having.getCondition();
            List<Object> list = Arrays.asList(getParams().toArray());
            list.addAll(having.getParams());

            return operator.selectWithJoinWithCount(resultFields, where, list.toArray(), parseOrder(),
                    limit.getBegin(), limit.getEnd(), getTableName());
        } finally {
            clear();
        }
    }

    protected List<Object> getParams() {
        List<Object> res = sqlPocket.getParams();
        if (operator.getDbType() != DERBY) {
            res.addAll(orderStack.getParams());
        }
        return paramsInterceptor.apply(res);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K> List<K> findAll(Class<K> clz) throws DbException {
        try {
            sqlPocket = this.getSql();
            SqlPocket having = parseHaving();
            String where = formJoinAndWhereClause() + parseGroupBy() + having.getCondition();
            List<Object> list = Arrays.asList(getParams().toArray());
            list.addAll(having.getParams());

            String select = operator.useTableNameOrSelect(resultFields, this.getTableName());

            return operator.select(select, where, parseOrder(), list.toArray(), limit.getBegin(),
                    limit.getEnd(), clz, true);
        } finally {
            clear();
        }
    }

    /**
     * <p>formJoinAndWhereClause.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected String formJoinAndWhereClause() {
        String res = sqlPocket.getCondition().toString();
        if (StringUtils.isNotBlank(this.tableAlias)) {
            StringBuilder join = new StringBuilder();
            if (joins != null && !joins.isEmpty()) {
                for (String t : joins) {
                    join.append(t);
                }
            }
            res = " " + this.tableAlias + " " + join + " " + res;
        }
        return res;
    }

    protected Pair<String, String> formJoinAndWherePair() {
        String res = sqlPocket.getCondition().toString();
        String joinString="";
        if (StringUtils.isNotBlank(this.tableAlias)) {
            StringBuilder join = new StringBuilder();
            if (joins != null && !joins.isEmpty()) {
                for (String t : joins) {
                    join.append(t);
                }
            }
            joinString = " " + this.tableAlias + " " + join;
        }
        return Pair.of(joinString, res);
    }

    public void clear() {
        this.filterStack.clear();
        this.orderStack.clear();
        this.groupByClause.clear();
        this.havingStack.clear();
        this.joins = null;
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
            String where = formJoinAndWhereClause() + parseGroupBy() + having.getCondition();
            List<Object> list = Arrays.asList(getParams().toArray());
            list.addAll(having.getParams());
            List<T> res = operator.selectWithJoin(resultFields, where, list.toArray(), parseOrder(),
                    SqlLimit.LIMIT_ONE.getBegin(), SqlLimit.LIMIT_ONE.getEnd(), getTableName());
            if (!res.isEmpty()) {
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
            String whereClause = formJoinAndWhereClause() + parseGroupBy() + having.getCondition();
            List<Object> list = Arrays.asList(getParams().toArray());
            list.addAll(having.getParams());

            ensureNotEmptyWhere(whereClause);

            whereClause = applyDmlLimit(whereClause);

            return operator.delete(whereClause, list.toArray(), cn);
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



    @Override
    public  <Key, Value> Map<Key, Value> getMap(String keyField, String valueField,
                                        Class<Key> keyClass,
                                        Class<Value> valueClass) throws DbException     {
    List<Pair<Key, Value>> list = new ArrayList<>();
        try {
            String sql = getVectorSql(keyField + "," + valueField);
            Object[] params = getParams().toArray();
            operator.getDbHelper().executeQuery(sql, params, new LongKeyMapReader<>(list, keyClass, valueClass));

            Map<Key, Value> map = new LinkedHashMap<>();
            for (Pair<Key, Value> pair : list) {
                Key key = pair.getLeft();
                Value value = pair.getRight();
                if (key != null) {
                    map.put(key, value);
                } else {
                    LOG.warn("getLongKeyMap: key is null for keyField={}, valueField={}", keyField, valueField);
                }
            }
            return map;
        } catch (Exception e) {
            throw new DbException(e);
        } finally {
            clear();
        }
    }

    // 私有通用方法
    private <K> Map<K, Object> getMap(String keyField, String valueField, RowCallbackHandler reader,
                                      List<Pair<K, Object>> list) throws DbException {
        try {
            String sql = getVectorSql(keyField + "," + valueField);
            Object[] params = getParams().toArray();

            Map<K, Object> map = new LinkedHashMap<>();
            operator.getDbHelper().executeQuery(sql, params, reader);

            for (Pair<K, Object> pair : list) {
                K key = pair.getLeft();
                Object value = pair.getRight();
                if (key != null && value != null) {
                    map.put(key, value);
                } else {
                    LOG.warn("getMap: key or value is null for keyField={}, valueField={}", keyField, valueField);
                }
            }
            return map;
        } catch (Exception e) {
            throw new DbException(e);
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
        String where = formJoinAndWhereClause();
        where += " " + parseGroupBy();
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
           return innerCount();
        } finally {
            clear();
        }
    }

    private int innerCount() throws DbException {

            sqlPocket = this.getSql();
            SqlPocket having = parseHaving();
            String where = formJoinAndWhereClause() + parseGroupBy() + having.getCondition();
            List<Object> list = Arrays.asList(getParams().toArray());
            list.addAll(having.getParams());

            String tableNameAlias = getTableName();
            boolean innerSelect = false;
            if (tableNameAlias != null && tableNameAlias.toUpperCase().startsWith("SELECT ")) {
                tableNameAlias = "(" + tableNameAlias + " " + where + ") as counting_table";
                innerSelect = true;
            }

            return operator.countWhere(tableNameAlias, innerSelect ? "" : where, list.toArray());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists() throws DbException {
        try {
            this.limit(0,1);

            sqlPocket = this.getSql();
            SqlPocket having = parseHaving();
            String where = formJoinAndWhereClause() + parseGroupBy() + having.getCondition();
            where += " " + parseLimitSql();
            List<Object> list = Arrays.asList(getParams().toArray());
            list.addAll(having.getParams());
            return operator.exists(getTableName(), where, list.toArray());
        } finally {
            clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Record<T> last() {
        if (sqlPocket == null) {
            LOG.error("ERROR: ActiveRecordImpl.last() - there not conditions set to use last.");
        }
        useLast = true;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> asc(String field) {
        return this.order(field, ORDER_ASC);
    }


    /**
     * {@inheritDoc}
     */
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
    public ActiveRecord<T> alias(String shortName) {
        tableAlias = shortName;
        return this;
    }

    @Override
    public ActiveRecord<T> tableName(String partitionTableName) {
        this.tableName = partitionTableName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> fields(String... fields) {
        this.resultFields = fields.length==1 ? fields[0] : String.join(",", fields);
        return this;
    }


    /**
     * {@inheritDoc}
     */
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
    public CountValue countSum(String field) throws DbException {
        return countValue(field, GroupFunction.SUM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public CountValue countValue(String field, GroupFunction fun) {
        try {
            sqlPocket = this.getSql();

            String where = formJoinAndWhereClause();

            SqlPocket sqlPocket = parseHaving();

            List<Object> list = Arrays.asList(getParams().toArray());
            list.addAll(sqlPocket.getParams());

            Object[] params = list.toArray();
            List<CountValue> res = operator.countGroupValue(field, fun.toString(), where,
                    sqlPocket.getCondition().toString(), groupInterceptor.apply(groupByClause).toArray(), params);

            return res.isEmpty() ? new CountValue() : res.get(0);
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
            String whereClause = formJoinAndWhereClause();
            Object[] params = getParams().toArray();
            String sql = "update " + operator.getTableName()
                    + " set " + field + "=" + field + "+1 " + whereClause;
            ensureNotEmptyWhere(whereClause);
            if (cn == null) {
                return operator.getDbHelper().executeNoQuery(sql, params);
            }
            return operator.getDbHelper().executeNoQuery(sql, params, cn);
        } finally {
            clear();
        }

    }

    private void ensureNotEmptyWhere(String whereClause) {
        if (whereClause.length() < " where ".length()) {
            throw new IllegalArgumentException("where clause must not be empty");
        }
    }

    /**
     * Whether current limit is the default unbounded placeholder ({@link SqlLimit#LIMIT_MAX}).
     */
    private boolean isDefaultMaxLimit() {
        return limit == null
                || (limit.getBegin() == SqlLimit.LIMIT_MAX.getBegin()
                && limit.getNum() == SqlLimit.LIMIT_MAX.getNum());
    }

    /**
     * Apply LIMIT for DELETE/UPDATE according to database dialect.
     * <ul>
     *   <li>MySQL / H2: {@code ... ORDER BY ... LIMIT n}</li>
     *   <li>PostgreSQL: {@code WHERE pk IN (SELECT pk FROM t WHERE ... LIMIT n)}；无主键时用 ctid</li>
     *   <li>SQL Server: {@code WHERE pk IN (SELECT TOP (n) pk FROM t WHERE ...)}</li>
     *   <li>Oracle: {@code WHERE pk IN (SELECT pk FROM (SELECT pk FROM t WHERE ...) WHERE ROWNUM <= n)}；无主键时用 ROWID</li>
     *   <li>Derby: {@code WHERE pk IN (SELECT pk FROM t WHERE ... FETCH FIRST n ROWS ONLY)}</li>
     * </ul>
     * Only applied when {@link #limit(int, int)} was called explicitly (not {@link SqlLimit#LIMIT_MAX}).
     * Offset ({@code begin > 0}) is not supported for DELETE/UPDATE.
     *
     * @param whereClause where (and optional join alias prefix for delete path)
     * @return where clause with dialect-specific limit applied, or original if no explicit limit
     */
    protected String applyDmlLimit(String whereClause) {
        if (isDefaultMaxLimit()) {
            return whereClause;
        }
        if (limit.getBegin() != 0) {
            throw new IllegalArgumentException(
                    "DELETE/UPDATE 的 limit 不支持 offset，begin 必须为 0，当前 begin=" + limit.getBegin());
        }
        if (limit.getNum() <= 0) {
            return whereClause;
        }

        LOG.info("DML table {} operation with a limit({}), please make sure this is correct:\n{}",
                operator.getTableName(), limit, whereClause);

        int num = limit.getNum();
        String order = parseOrder();
        int dbType = operator.getDbType();

        switch (dbType) {
            case MYSQL:
            case H2:
                return whereClause + order + " LIMIT " + num;
            case POSTGRESQL:
                return buildPkOrPseudoIdInSubqueryWhere(whereClause, order, " LIMIT " + num, "", "ctid");
            case SQLSERVER:
            case SQLSERVER_2005_AND_UP:
            case SQLSERVER_2010_AND_UP:
                return buildPkInSubqueryWhere(whereClause, order, "", "TOP (" + num + ") ");
            case ORACLE:
                return buildOracleLimitedWhere(whereClause, order, num);
            case DERBY:
                return buildPkInSubqueryWhere(whereClause, order, " FETCH FIRST " + num + " ROWS ONLY", "");
            default:
                throw new NotImplementedException(
                        "DELETE/UPDATE LIMIT not supported for dbType=" + dbType);
        }
    }

    /**
     * Wrap where as {@code WHERE id_col IN (SELECT [TOP n] id_col FROM table WHERE ... [LIMIT/FETCH])}
     */
    private String buildPkInSubqueryWhere(String whereClause, String order, String limitSuffix, String selectPrefix) {
        String key = operator.getKeyName();
        if (StringUtils.isBlank(key)) {
            throw new DbException("当前数据库类型的 DELETE/UPDATE LIMIT 需要主键字段 getKeyName()");
        }
        return buildInSubqueryWhere(key, whereClause, order, limitSuffix, selectPrefix);
    }

    /**
     * Like {@link #buildPkInSubqueryWhere} but falls back to a physical row id column (e.g. ctid).
     */
    private String buildPkOrPseudoIdInSubqueryWhere(String whereClause, String order,
                                                    String limitSuffix, String selectPrefix,
                                                    String pseudoIdColumn) {
        String key = operator.getKeyName();
        if (StringUtils.isBlank(key)) {
            key = pseudoIdColumn;
        }
        return buildInSubqueryWhere(key, whereClause, order, limitSuffix, selectPrefix);
    }

    private String buildInSubqueryWhere(String idColumn, String whereClause, String order,
                                        String limitSuffix, String selectPrefix) {
        String table = getTableName();
        return " where " + idColumn + " in (select " + selectPrefix + idColumn
                + " from " + table + whereClause + order + limitSuffix + ")";
    }

    /**
     * Oracle: nested subquery so ORDER BY works with ROWNUM.
     */
    private String buildOracleLimitedWhere(String whereClause, String order, int num) {
        String key = operator.getKeyName();
        if (StringUtils.isBlank(key)) {
            key = "ROWID";
        }
        String table = getTableName();
        return " where " + key + " in (select " + key + " from (select " + key
                + " from " + table + whereClause + order + ") where ROWNUM <= " + num + ")";
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
            Pair<String, String>   whereClause = formJoinAndWherePair();
            ensureNotEmptyWhere(whereClause.getRight());

            whereClause = Pair.of(whereClause.getLeft(), applyDmlLimit(whereClause.getRight()));
            return operator.update(fields, values, whereClause, getParams().toArray(), cn);
        } finally {
            clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(Map<String, Object> keyValuePairs) throws DbException {
        try {

            sqlPocket = this.getSql();
            Pair<String, String>   whereClause = formJoinAndWherePair();

            ensureNotEmptyWhere(whereClause.getRight());
            String[] fields = keyValuePairs.keySet().toArray(new String[0]);
            Object[] values = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                values[i] = keyValuePairs.get(fields[i]);
            }

            whereClause = Pair.of(whereClause.getLeft(), applyDmlLimit(whereClause.getRight()));
            return operator.update(fields, values, whereClause, getParams().toArray(), cn);
        } finally {
            clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(@NotNull String fields, Object values) throws DbException {
        return update(new String[]{fields}, new Object[]{values});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K> CountObject<K> countObject(String field, GroupFunction fun, Class<K> objectType) {
        try {
            sqlPocket = this.getSql();
            SqlPocket having = parseHaving();
            String where = formJoinAndWhereClause();
            List<Object> list = Arrays.asList(getParams().toArray());
            list.addAll(having.getParams());
            List<CountObject<K>> res = operator.countGroupObject(field, fun.toString(), where,
                    having.getCondition().toString(), groupInterceptor.apply(groupByClause).toArray(), objectType, list.toArray());

            return res.isEmpty() ? new CountObject<>() : res.get(0);
        } finally {
            clear();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <K> List<CountObject<K>> countList(String returnField, String[] groupByFields, String having, GroupFunction fun, Class<K> objectType) {
        try {
            sqlPocket = this.getSql();
            String where = formJoinAndWhereClause();
            List<Object> list = Arrays.asList(getParams().toArray());
            return operator.countGroupObject(returnField, fun.toString(), where,
                    having, groupByFields, objectType, list.toArray());
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

    @Override
    public ActiveRecord<T> paramsInterceptor(Function<List<Object>, List<Object>> paramsInterceptor) {
        this.paramsInterceptor = paramsInterceptor;
        return this;
    }


    @Override
    public ActiveRecord<T> coreSqlInterceptor(Function<String, String> coreSqlInterceptor) {
        this.coreSqlInterceptor = coreSqlInterceptor;
        return this;
    }


    @Override
    public Object percentile(String field, float portion) throws DbException {
        int count = this.innerCount();
        if (count == 0) {
            return 0;
        }

        return this.fields(field)
                .desc(field)
                .limit((int) ((count-1) * (1-portion)), 1)
                .get(getFieldAlias(field));
    }
}
