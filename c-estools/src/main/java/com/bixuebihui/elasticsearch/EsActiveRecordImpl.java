package com.bixuebihui.elasticsearch;

import com.bixuebihui.DbException;
import com.bixuebihui.db.*;
import com.bixuebihui.db.Record;
import com.bixuebihui.elasticsearch.query.Query;
import com.bixuebihui.jdbc.*;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class EsActiveRecordImpl<T, V> implements EsActiveRecord<T> {
    private static final Logger LOG = LoggerFactory.getLogger(EsActiveRecordImpl.class);
    /**
     * NOTE that: class field make this class can't use in spring as singleton!
     */
    EsSort orderStack;
    ElasticSearchFilter filterStack;


    SqlLimit limit;

//    SqlGroup groupByClause;
    ElasticSearchFilter havingStack;

    public EsActiveRecordImpl(EsOperator<T, V> operator) {
       init(operator);
    }

    EsOperator<T, V> operator;
    private boolean useLast=false;

    private String indexAlias;

    private String resultFields ="";

    public EsActiveRecordImpl() {
        init(null);
    }

    public String getResultFields() {
        return resultFields;
    }

    @Override
    public Set<String> getResultFieldsSet(){
        return ActiveRecord.toFieldsSet(resultFields);
    }
    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> in(String field, Object[] values) {
        filterStack.in(field, values);
        return this;
    }

    @Override
    public EsActiveRecord<T> emptyStringAsNullCondition() {

        throw new NotImplementedException();

    }


    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> in(String field, Object values) {

        filterStack.in(field, (Object[]) values);


        return this;
    }


    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> like(String field, String value) {
        filterStack.contain(field, value);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> startWith(String field, String value) {
        filterStack.startWith(field, value);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> eq(String field, Object value) {
        filterStack.is(field, value);
        return this;
    }

    @Override
    public EsActiveRecord<T> between(String field, Object leftValue, Object rightValue) {
        filterStack.between(field, leftValue, rightValue);
        return this;
    }

    @Override
    public EsActiveRecord<T> notBetween(String field, Object leftValue, Object rightValue) {
        filterStack.notBetween(field, leftValue, rightValue);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> ne(String field, Object value){
        filterStack.isNot(field, value);
        return this;
    }


    /** {@inheritDoc} */

    @Override
    public EsActiveRecord<T> eq(String[] fields, Object[] value)
            throws SQLException {
        for(int i=0; i<fields.length; i++) {
            filterStack.is(fields[i], value[i]);
        }
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> greaterThan(String field, Object value) {
        filterStack.gt(field, value);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> smallerThan(String field, Object value) {
        filterStack.lt(field, value);
        return this;
    }

    @Override
    public EsActiveRecord<T> isNull(String field) {
        filterStack.isNull(field);
        return this;
    }

    @Override
    public EsActiveRecord<T> isNotNull(String field) {
        filterStack.isNotNull(field);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> greaterOrEqualThan(String field, Object value) {
        filterStack.gte(field, value);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> smallerOrEqualThan(String field, Object value) {
        filterStack.lte(field, value);
        return this;
    }


    protected EsActiveRecord<T> order(String field, int order) {
        orderStack.addSort(field, order == EsActiveRecord.ORDER_DESC ? SqlSort.Sort.DESC	: SqlSort.Sort.ASC);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> orderByIn(String field, Object[] order) {
            orderStack.addSortByIn(field, order);

        return this;
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> limit(int begin, int num) {
        limit = new SqlLimit(begin, num);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll() throws DbException {
        try {
            Query where = filterStack.toQuery();

            List<EsSearchHitResult<T>> res = operator.select(resultFields, where
                    , toEsSort(), limit.getBegin(), limit.getEnd());
            if(res.size()>0){
                return res.stream().map(EsSearchHitResult::get_source).collect(Collectors.toList());
            }
            return Collections.emptyList();
        } finally {
            clear();
        }
    }

    public List<EsSearchHitResult<T>> findAllDocMeta() throws DbException {
        try {
            Query where = filterStack.toQuery();

            return operator.select(resultFields, where
                    , toEsSort() ,limit.getBegin(), limit.getEnd());
        } finally {
            clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K> List<K> findAll(Class<K> clz) throws DbException {
        try {
            return operator.select(resultFields, filterStack.toQuery(), toEsSort(), limit.getBegin(),
                    limit.getEnd(), clz);
        } finally {
            clear();
        }
    }


    private void clear() {
        if(this.filterStack.getFilters()!=null) {
            this.filterStack.getFilters().clear();
        }
        this.orderStack.clear();
//        this.groupByClause.clear();
        this.limit = SqlLimit.LIMIT_MAX;
        useLast = false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public T find() throws DbException {
        try {
           EsOperator.Wrapper<T> res = operator.selectWithJoin(resultFields, filterStack.toQuery() , toEsSort(),
                    SqlLimit.LIMIT_ONE.getBegin(), SqlLimit.LIMIT_ONE.getEnd());
            if (res!=null && res.data !=null && res.data.size() > 0) {
                return res.data.get(0);
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
                return 0 < operator.deleteByQuery(filterStack.toQuery());
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
            return operator.selectWithJoin(field, filterStack.toQuery(),null, 0,1);
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
            // orderStack.build()
            return (List<Object>) operator.selectWithJoin(field, filterStack.toQuery(),null, 0,10000);
        } finally {
            clear();
        }
    }

    private LinkedHashMap<String,String> toEsSort() {
        return orderStack.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getLongVector(String field) throws DbException {
        try {

            return (List<Long>) operator.selectWithJoin(field, filterStack.toQuery(),null, 0,10000);

        } finally {
            clear();
        }
    }

    public String parseLimitSql() throws DbException {

            if (limit == null) {
                limit = SqlLimit.LIMIT_ONE;
            }

            return limit.toString();

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SqlPocket getSql() throws DbException {
        throw new NotImplementedException();
    }


    @Override
    public EsActiveRecord<T> asc(String field) {
        order(field, ORDER_ASC);
        return this;
    }


    @Override
    public EsActiveRecord<T> desc(String field) {
        order(field, ORDER_DESC);
        return this;
    }

    @Override
    public Record<T> last() {
        throw new NotImplementedException();
    }


    @Override
    public SqlHelper getCondStack() {
        throw new NotImplementedException();
    }

    @Override
    public EsActiveRecord<T> or(SqlHelper andStack) {
        throw new NotImplementedException();
    }

    @Override
    public EsActiveRecord<T> alias(String shortName) {
        throw new NotImplementedException();
    }

    @Override
    public EsActiveRecord<T> fields(String resultFields) {
        this.resultFields = resultFields;
        return this;
    }

    @Override
    public EsActiveRecord<T> join(String joinClause) {
        throw new NotImplementedException();
    }

    @Override
    public EsActiveRecord<T> groupBy(String... fields) {
        throw new NotImplementedException();
    }

    @Override
    public EsActiveRecord<T> having(HavingConditon fieldProcessor) {
        throw new NotImplementedException();
    }

    @Override
    public int count() throws DbException {

        throw new NotImplementedException();

    }

    @Override
    public CountValue countValue(String field, GroupFunction fun) throws DbException {
        throw new NotImplementedException();

    }

    @Override
    public <K> CountObject<K> countObject(String field, GroupFunction fun, Class<K> objectType) throws DbException {
        throw new NotImplementedException();

    }

    @Override
    public <K> List<CountObject<K>> countList(String returnField, String[] groupByFields, String having, GroupFunction fun, Class<K> objectType) {
        throw new NotImplementedException();

    }

    @Override
    public CountValue countSum(String field) throws DbException {
        throw new NotImplementedException();

    }

    @Override
    public boolean exists() throws DbException {
        throw new NotImplementedException();

    }

    @Override
    public List<String> getStringVector(String field) throws DbException {
        throw new NotImplementedException();

    }

    /**
     * https://www.cnblogs.com/xing901022/p/5330778.html
     * curl -XPOST 'localhost:9200/test/type1/1/_update' -d '{
     *     "script" : {
     *         "inline": "ctx._source.counter += count",
     *         "params" : {
     *             "count" : 4
     *         }
     *     }
     * }'
     *
     * curl -XPOST 'localhost:9200/test/type1/1/_update' -d '{
     *     "script" : {
     *         "inline": "ctx._source.counter += count",
     *         "params" : {
     *             "count" : 4
     *         }
     *     },
     *     "upsert" : {
     *         "counter" : 1
     *     }
     * }'
     *
     *
     * @param field 字段名
     * @return
     * @throws DbException
     */
    @Override
    public int inc(String field) throws DbException {
        throw new NotImplementedException();

    }


    /**
     * curl -XPOST 'localhost:9200/test/type1/1/_update' -d '{
     *     "doc" : {
     *         "name" : "new_name"
     *     },
     *     "doc_as_upsert" : true
     * }'
     *
     * post参数据：fields 执行完更新后，返回的字段
     *
     * @param fields 表字段名
     * @param values 字段值， 如果values是SqlString 类型，将不参与形成预编译语句的占位符，而是原样输出
     * @return
     * @throws DbException
     */
    @Override
    public int update(String[] fields, Object[] values) throws DbException {
        throw new NotImplementedException();
    }

    @Override
    public int update(String field, Object value) throws DbException {
        return operator.update(field, value);
    }


    @Override
    public int update(String _id, String field, Object value) throws DbException {
        return operator.update(_id, field, value);
    }

    @Override
    public int update(String _id, String[] fields, Object[] values) throws DbException {
        return operator.update(_id, fields, values);
    }


    public SqlLimit getLimit() {
        return limit;
    }

    public void setLimit(SqlLimit limit) {
        this.limit = limit;
    }


    private void init(EsOperator<T, V> operator){
        this.operator = operator;
        orderStack = new EsSort();
        filterStack = new ElasticSearchFilter();
//        groupByClause = new SqlGroup();
        havingStack = new ElasticSearchFilter();
        limit = SqlLimit.LIMIT_MAX;
    }
}
