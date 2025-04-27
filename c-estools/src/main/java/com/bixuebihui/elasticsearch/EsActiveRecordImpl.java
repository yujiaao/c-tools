package com.bixuebihui.elasticsearch;

import com.bixuebihui.DbException;
import com.bixuebihui.db.*;
import com.bixuebihui.db.Record;
import com.bixuebihui.elasticsearch.aggregation.CountObj;
import com.bixuebihui.elasticsearch.aggregation.CountValueObj;
import com.bixuebihui.elasticsearch.query.Query;
import com.bixuebihui.jdbc.*;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;
import com.bixuebihui.query.Paging;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;

public class EsActiveRecordImpl<T, V> implements EsActiveRecord<T> {
    private static final Logger LOG = LoggerFactory.getLogger(EsActiveRecordImpl.class);
    /**
     * NOTE that: class field make this class can't use in spring as singleton!
     */
    EsSort orderStack;
    ElasticSearchFilter filterStack;

    SqlLimit limit;

    ElasticSearchFilter havingStack;
    BaseEsOperator<T, V> operator;
    Set<String> excludeFieldsSet = emptySet();
    private String resultFields;

    private List<Object> searchAfter;

    public EsActiveRecordImpl(BaseEsOperator<T, V> operator) {
       init(operator);
    }
    public EsActiveRecordImpl() {
        init(null);
    }

    public BaseEsOperator<T, V> getOperator() {
        return operator;
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
    public EsActiveRecord<T> setFilter(ElasticSearchFilter filter) {
        filterStack = filter;
        return this;
    }



     /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> notIn(String field, Object[] values) {
        filterStack.notIn(field, values);
        return this;
    }

    @Override
    public EsActiveRecord<T> emptyStringAsNullCondition() {

        throw new NotImplementedException();

    }

    @Override
    public EsActiveRecord<T> keepNullForNumberField() {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> in(String field, Object values) {
        filterStack.in(field, (Object[]) values);
        return this;
    }

    @Override
    public ActiveRecord<T> in(Pair<String, String> fields, List<Pair<?, ?>> values) {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> in(String field, List<?> values) {
        filterStack.in(field, values);
        return this;
    }

    @Override
    public ActiveRecord<T> in(String field, SqlString sqlString) {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> notIn(String field, List<?> values) {
        filterStack.notIn(field, values);
        return this;
    }

    @Override
    public ActiveRecord<T> notIn(String field, SqlString sqlString) {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> like(String field, String value) {
        filterStack.contain(field, value);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> wildcard(String field, String[] value) {
        filterStack.wildcard(field, value);
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

    @Override
    public ActiveRecord<T> gt(String field, Object value) {
        return greaterThan(field, value);
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> smallerThan(String field, Object value) {
        filterStack.lt(field, value);
        return this;
    }

    @Override
    public ActiveRecord<T> sql(String field, Object... value) {
        throw new NotImplementedException();
    }

    @Override
    public ActiveRecord<T> lt(String field, Object value) {
        return smallerThan(field, value);
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

    @Override
    public ActiveRecord<T> ge(String field, Object value) {
        return greaterOrEqualThan(field, value);
    }

    /** {@inheritDoc} */
    @Override
    public EsActiveRecord<T> smallerOrEqualThan(String field, Object value) {
        filterStack.lte(field, value);
        return this;
    }

    @Override
    public ActiveRecord<T> le(String field, Object value) {
        return smallerOrEqualThan(field, value);
    }


    protected EsActiveRecord<T> order(String field, int order) {
        orderStack.addSort(field, order == EsActiveRecord.ORDER_DESC ? SqlSort.Sort.DESC	: SqlSort.Sort.ASC);
        return this;
    }

    @Override
    public EsActiveRecord<T> searchAfter(List<Object> lastOrderFieldsValue){
        this.searchAfter =  lastOrderFieldsValue;
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

            List<EsSearchHitResult<T>> res = operator.select(getResultFieldsSet(), getExcludeFieldsSet(), where
                    , toEsSort(), limit.getBegin(), limit.getEnd(), searchAfter);
            if(!res.isEmpty()){
                return res.stream().map(EsSearchHitResult::get_source).collect(Collectors.toList());
            }
            return Collections.emptyList();
        } finally {
            clear();
        }
    }

    @Override
    public Pair<Long, List<T>> findAllWithCount() throws DbException {
        try {
            Query where = filterStack.toQuery();

            Pair<Long, List<EsSearchHitResult<T>>> res = operator.selectWithHits(getResultFieldsSet(), getExcludeFieldsSet(), where
                    , toEsSort(), limit.getBegin(), limit.getEnd(), searchAfter);
            if(!res.getRight().isEmpty()){
                return Pair.of( res.getLeft() , res.getRight().stream().map(EsSearchHitResult::get_source).collect(Collectors.toList()));
            }
            return Pair.of(0L,Collections.emptyList());
        } finally {
            clear();
        }
    }

    public List<EsSearchHitResult<T>> findAllDocMeta() throws DbException {
        try {
            Query where = filterStack.toQuery();

            return operator.select(getResultFieldsSet(), getExcludeFieldsSet(), where
                    , toEsSort() ,limit.getBegin(), limit.getEnd(),searchAfter);
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
            return operator.select(getResultFieldsSet(),getExcludeFieldsSet(),
                    filterStack.toQuery(), toEsSort(), limit.getBegin(),
                    limit.getEnd(), clz, searchAfter);
        } finally {
            clear();
        }
    }


    private void clear() {
        if(this.filterStack.getFilters()!=null) {
            this.filterStack.getFilters().clear();
        }
        this.orderStack.clear();
        this.limit = SqlLimit.LIMIT_MAX;
        this.fields("");
        this.excludeFieldsSet = emptySet();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public T find() throws DbException {
        try {
           BaseEsOperator.Wrapper<T> res = operator.selectWithJoin(getResultFieldsSet(), getExcludeFieldsSet(), filterStack.toQuery() , toEsSort(),
                    SqlLimit.LIMIT_ONE.getBegin(), SqlLimit.LIMIT_ONE.getEnd(), searchAfter);
            if (res!=null && res.data !=null && !res.data.isEmpty()) {
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
            return operator.selectWithJoin(Set.of(field), emptySet(),
                    filterStack.toQuery(),toEsSort(), 0,1, searchAfter);
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
            return (List<Object>) operator.selectWithJoin(Set.of(field), emptySet(), filterStack.toQuery(),
                    toEsSort(), limit.getBegin(),limit.getEnd(), searchAfter);
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
            return (List<Long>) operator.selectWithJoin(Set.of(field), emptySet(), filterStack.toQuery(),
                    toEsSort(), limit.getBegin(),limit.getEnd(), searchAfter);

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
    public EsActiveRecord<T> and(SqlHelper andStack) {
        throw new NotImplementedException();
    }

    @Override
    public EsActiveRecord<T> not(SqlHelper notStack) {
        throw new NotImplementedException();
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
    public EsActiveRecord<T> tableName(String partitionTableName) {
        throw new NotImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveRecord<T> fields(String... fields) {
        this.resultFields = fields.length==1 ? fields[0] : String.join(",", fields);
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
    public <S> EsActiveRecord<T> query(Paging<S> searchBean, Set<String> fields) {
        return ( EsActiveRecord<T>)searchBean.toActiveRecordParams(this, fields);
    }

    @Override
    public <S> EsActiveRecord<T> query(Paging<S> searchBean) {
        return query(searchBean, Set.of(operator.getAllFields().toArray(new String[0])));
    }


    @Override
    public int count() throws DbException {
        return (int) operator.count(filterStack.toQuery());
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
        return countSum(field, false);
    }

    @Override
    public List<CountObj> dateHistCountList(String dateField, String interval, String[] groupByFields, GroupFunction fun)  {
        return  operator.dateHistCountList(filterStack.toQuery(), dateField, interval, groupByFields,fun);
    }

    @Override
    public EsActiveRecord<T> moreLikeThis(Map<String, Object> fieldValueMap, Map<String, Object> otherParams) {
        filterStack.moreLikeThis(fieldValueMap, otherParams);
        return this;
    }

    @Override
    public List<CountValueObj> aggregationList(List<String> groupByFields,
                                               Map<String, GroupFunction> termFieldAggFunctionMap, Set<String> textFieldsNeededConvertToDouble)  {
        return  operator.aggregationList(filterStack.toQuery(), groupByFields, termFieldAggFunctionMap, textFieldsNeededConvertToDouble);
    }

    @Override
    public List<CountValueObj> aggregationList(List<String> groupByFields, Map<String, GroupFunction> termFieldAggFunctionMap) {
        return aggregationList(groupByFields, termFieldAggFunctionMap, emptySet());
    }

    @Override
    public CountValue countSum(String field, boolean needConvertToDouble) throws DbException {
        List<CountValueObj> res = operator.aggregationList(filterStack.toQuery(), List.of(field),
                Map.of(field, GroupFunction.SUM), needConvertToDouble ? Set.of(field):emptySet()
        );
        if(res.isEmpty()){
            return new CountValue();
        }

        CountValue result = new CountValue();
        result.setCount(res.get(0).getDoc_count());
        result.setKey(field);
        result.setValue(Double.parseDouble(((Map<String, Object>)(
                res.get(0).getAdditionalProperties()
                        .get(GroupFunction.SUM.name().toLowerCase()+"_"+field)))
                .getOrDefault("value", 0).toString()));
        return result;
    }

    @Override
    public boolean exists() throws DbException {
        throw new NotImplementedException();

    }

    @Override
    public List<String> getStringVector(String field) throws DbException {
        try {
            return (List<String>) operator.selectWithJoin(Set.of(field),
                    emptySet(), filterStack.toQuery(),toEsSort(), limit.getBegin(),limit.getEnd(), searchAfter);

        } finally {
            clear();
        }
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
        HashMap<String, Object> map = Maps.newHashMap();
        for(int i=0; i<fields.length;i++) {
            map.put(fields[i], values[i]);
        }
        try {
            return operator.request.updateByQuery(filterStack.toQuery(), map, true, true);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public int update(String field, Object value) throws DbException {
        HashMap<String, Object> map = Maps.newHashMap();

        map.put(field, value);

        try {
            return operator.request.updateByQuery(filterStack.toQuery(), map, true, true);
        } catch (JsonProcessingException e) {
            throw new DbException(e);
        }
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


    private void init(BaseEsOperator<T, V> operator){
        this.operator = operator;
        orderStack = new EsSort();
        filterStack = new ElasticSearchFilter();
//        groupByClause = new SqlGroup();
        havingStack = new ElasticSearchFilter();
        limit = SqlLimit.LIMIT_MAX;
    }

    public Set<String> getExcludeFieldsSet() {
        return excludeFieldsSet;
    }

    @Override
    public EsActiveRecord<T> excludeFields(Set<String> excludeFieldsSet) {
        this.excludeFieldsSet = excludeFieldsSet;
        return this;
    }

    @Override
    public int updateByScript(String script, Map<String, Object> params) throws DbException {
        return 0;
    }

    @Override
    public boolean insert(String indexName, T info) {
        return insertBatch(indexName, List.of(info), null);
    }

    @Override
    public boolean insert(String indexName, T info, Function<T, String> getId) {
        return insertBatch(indexName, List.of(info), getId);
    }


    public boolean insert(String indexName, T info, Function<T, String> getId, boolean refresh) {
        return insertBatch(indexName, List.of(info), getId, refresh);
    }

    public boolean insertBatch(String indexName, List<T> infos, Function<T, String> getId) {
        return insertBatch( indexName,infos,getId, false);
    }
    public boolean insertBatch(String indexName, List<T> infos, Function<T, String> getId, boolean refresh) {
        try {
            String response =  operator.request.insertBatch(indexName, infos,
                    (t) -> (getId!=null) ? StringUtils.trimToNull(getId.apply(t)):null, refresh);

            ObjectMapper mapper = EsRequest.getMapper();

            JsonNode jsonNode = mapper.readTree(response);

            LOG.debug(response);

            String errorField = "errors";
            return jsonNode.has(errorField) && !jsonNode.get(errorField).asBoolean();


        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public EsActiveRecord<T> filterInterceptor(Function<SqlHelper, SqlHelper> filterInterceptor) {
        return this;
    }

    @Override
    public EsActiveRecord<T> havingInterceptor(Function<SqlHelper, SqlHelper> havingInterceptor) {
        return this;
    }

    @Override
    public EsActiveRecord<T> sortInterceptor(Function<SqlSort, SqlSort> sortInterceptor) {
        return this;
    }

    @Override
    public EsActiveRecord<T> groupInterceptor(Function<SqlGroup, SqlGroup> groupInterceptor) {
        return this;
    }

    @Override
    public EsActiveRecord<T> paramsInterceptor(Function<List<Object>, List<Object>> groupInterceptor) {
        return this;
    }

    @Override
    public EsActiveRecord<T> coreSqlInterceptor(Function<String, String> coreSqlInterceptor) {
        return this;
    }

    @Override
    public Object percentile(String field, float portion) throws DbException {
        //return operator.percentile(field, portion);
        throw new NotImplementedException();
    }
}
