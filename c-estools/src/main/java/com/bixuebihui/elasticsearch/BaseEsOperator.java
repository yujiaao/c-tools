package com.bixuebihui.elasticsearch;

import com.bixuebihui.DbException;
import com.bixuebihui.db.GroupFunction;
import com.bixuebihui.db.Record;
import com.bixuebihui.db.SqlPocket;
import com.bixuebihui.elasticsearch.aggregation.*;
import com.bixuebihui.elasticsearch.query.Query;
import com.bixuebihui.jdbc.SqlFilter;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

import static com.bixuebihui.elasticsearch.EsRequest.getHits;
import static java.util.Collections.*;
import static java.util.Collections.emptySet;

/**
 * @author xwx
 */
public abstract class BaseEsOperator<T, V> implements Record<T> {
    EsRequest request;

    public BaseEsOperator(EsRequest request) {
        this.request = request;
    }

    public static JsonWapper builder() {
        return new JsonWapper();
    }

    public EsRequest getRequest() {
        return request;
    }

    @Override
    public T find() throws DbException {
        throw new NotImplementedException();
    }

    @Override
    public boolean delete() throws DbException {
        throw new NotImplementedException();
    }

    public long count(Query query) throws DbException {
        String res = request.search(EsQueryBuilder.buildCount(query));
        return getHits(res);
    }

    @Override
    public int count() throws DbException {
        throw new NotImplementedException();
    }

    public List<String> getAllFields() {
        return emptyList();
    }

    public Wrapper<T> toObjectWrapper(String jsonString) {
        try {

            String json = request.esJsonToTableJson(jsonString, 0, 0, 0, 0, 0);
            JsonWapper wapper = builder();
            JavaType type = wapper.getObjectMapper().getTypeFactory().constructParametricType(Wrapper.class, getType());
            Wrapper<T> res = wapper.getObjectMapper().readValue(json, type);
            return res;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<EsSearchHitResult<T>> toEsResultWrapper(String jsonString) {
        try {

            JsonWapper wrapper = builder();
            JavaType inner = wrapper.getObjectMapper().getTypeFactory().constructParametricType(EsSearchHitResult.class, getType());
            JavaType type = wrapper.getObjectMapper().getTypeFactory().constructParametricType(List.class, inner);


            return EsRequest.json2result(jsonString, type);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Class<T> getType();

    public <K> List<K> toObjectList(String jsonString, Class<K> clz) {
        try {
            JsonNode node = request.getTableJsonNode(jsonString, 0, 0, 0, 0, 0);
            if (node == null) {
                return Collections.emptyList();
            }
            return builder().getObjectMapper().readerForListOf(clz).readValue(node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Object> getVector(String field) throws DbException {
        throw new NotImplementedException();
    }

    @Override
    public List<T> findAll() throws DbException {
        int from = 0;
        int size = 10;
        LinkedHashMap<String, String> sort = new LinkedHashMap<>();
        String res = request.search(EsQueryBuilder.build(Query.match_all(), from, size,
                Collections.emptySet(), Collections.emptySet(), sort, null,
                emptyMap(), null, emptyList()));

        Wrapper<T> w = toObjectWrapper(res);

        return w.data;
    }

    @Override
    public <K> List<K> findAll(Class<K> clz) throws DbException {
        throw new NotImplementedException();
    }

    @Override
    public Pair<Long,  List<T>> findAllWithCount() throws DbException {
        throw new NotImplementedException();
    }


    @Override
    public Object get(String field) throws DbException {
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


    public List<CountObj> dateHistCountList(String dateField, String interval, String[] groupByFields, GroupFunction fun) {
        return dateHistCountList(Query.match_all(), dateField, interval, groupByFields, fun);
    }

    public List<CountObj> dateHistCountList(Query query, String dateField, String interval, String[] groupByFields, GroupFunction fun)  {
        DateHistogram aggs = EsAggregationBuilder.buildDateHistogram(dateField, interval, groupByFields[0], fun==null? null:fun.name());

        String res = request.search(EsQueryBuilder.build(query, 0, 0, Collections.emptySet(),
                Collections.emptySet(), null, null, emptyMap(), aggs, emptyList()));

        try {
            return EsAggregationParser.parse(res, aggs.getName(), aggs.getAggs().isEmpty() ? null: aggs.getAggs().entrySet().stream().findFirst().get().getValue().getName());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public CountValue countSum(String field) throws DbException {
        throw new NotImplementedException();
    }

    @Override
    public SqlPocket getSql() throws DbException {
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

    @Override
    public List<Long> getLongVector(String field) throws DbException {
        throw new NotImplementedException();
    }

    @Override
    public int inc(String field) throws DbException {
        throw new NotImplementedException();
    }

    @Override
    public int update(String[] fields, Object[] values) throws DbException {
        throw new NotImplementedException();
    }

    public int update(String _id, String[] fields, Object[] values) throws DbException {
        return update(_id, fields, values, true);
    }

    public int update(String _id, String[] fields, Object[] values, boolean refresh) throws DbException {
        HashMap<String, Object> map = Maps.newHashMap();
        for (int i = 0; i < fields.length; i++) {
            map.put(fields[i], values[i]);
        }
        try {
            return request.update(_id, map, refresh);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DbException(e.getMessage());
        }
    }

    public int update(String _id, String fields, Object values) throws DbException {
        return update(_id, fields, values, true);
    }

    public int update(String _id, String fields, Object values, boolean refresh) throws DbException {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put(fields, values);
        try {
            return request.update(_id, map, refresh);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public int update(String fields, Object values) throws DbException {
        throw new NotImplementedException();
    }

    /**
     * POST my-index-000001/_search
     * {
     * "query": {
     * "match": {
     * "user.id": "kimchy"
     * }
     * },
     * "fields": [
     * "user.id",
     * "http.response.*",
     * {
     * "field": "@timestamp",
     * "format": "epoch_millis"
     * }
     * ],
     * "_source": false
     * }
     *
     * @param resultFields
     * @param query
     * @param sort
     * @param begin
     * @param end
     * @return
     */
    public Wrapper<T> selectWithJoin(Set<String> resultFields, Set<String> exludeFields, Query query,
                                     LinkedHashMap<String,
                                             String> sort, int begin, int end, List<Object> searchAfter) {
        int size = end - begin;

        String res = request.search(EsQueryBuilder.build(query, begin, size, resultFields, exludeFields, sort,
                null, emptyMap(), null, searchAfter));

        return toObjectWrapper(res);
    }

    public List<EsSearchHitResult<T>> select(Set<String> resultFields, Set<String> exludeFields,
                                             Query query, LinkedHashMap<String, String> sort,
                                             int begin, int end, List<Object> searchAfter) {
        int size = end - begin;

        String res = request.search(EsQueryBuilder.build(query, begin, size, resultFields,
                exludeFields, sort, null, emptyMap(), null, searchAfter));

        return toEsResultWrapper(res);
    }

    public <K> List<K> select(Set<String> resultFields, Set<String> exludeFields, Query query,
                              LinkedHashMap<String, String> sort, int begin, int end, Class<K> clz, List<Object> searchAfter) {
        int size = end - begin;

        String res = request.search(EsQueryBuilder.build(query, begin, size,
                resultFields, exludeFields, sort, null, emptyMap(), null, searchAfter));

        return toObjectList(res, clz);
    }

    public int deleteByQuery(Query query) {
        try {
            return request.deleteByQuery(query, true);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DbException(e.getMessage());
        }
    }

    public List<CountValueObj> aggregationList(Query query,
                                               List<String> groupByFields,
                                               Map<String, GroupFunction> termFieldAggFunctionMap,
                                               Set<String> textFieldsNeededConvertToDouble) {

        Terms aggregation = EsAggregationBuilder.buildTermsAggMulti(groupByFields,
                termFieldAggFunctionMap.entrySet().stream().map(
                e -> Map.of(e.getKey(), e.getValue().name().toLowerCase()))
                        .reduce((a,b) ->{ Map<String, String> c= new HashMap<>(a); c.putAll(b); return c;}
                ).or(() -> Optional.of(emptyMap())).get(),
                textFieldsNeededConvertToDouble);

        String res = request.search(EsQueryBuilder.build(query, 0, 1,
                Collections.emptySet(), Collections.emptySet(),
                null, null, emptyMap(), aggregation, emptyList()));

        try {
            return EsAggregationParser.parseCountValue(res,
                    aggregation.getName(),
                    aggregation.getAggs().isEmpty() ? null:
                            aggregation.getAggs().entrySet().stream().findFirst().get().getValue().getName(),
                    new ObjectMapper());

        } catch (JsonProcessingException e) {
            throw new DbException(e);
        }

    }

    public Pair<Long, List<EsSearchHitResult<T>>> selectWithHits(Set<String> resultFields,
                                                                 Set<String> exludeFields,
                                                                 Query query, LinkedHashMap<String, String> sort,
                                                                 int begin, int end, List<Object> searchAfter) {
        int size = end - begin;

        String res = request.search(EsQueryBuilder.build(query, begin, size, resultFields,
                exludeFields, sort, null, emptyMap(), null, searchAfter));

        return Pair.of(getHits(res), toEsResultWrapper(res));
    }

    static class JsonWapper {
        private ObjectMapper objectMapper;

        public ObjectMapper getObjectMapper() {
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            }

            return objectMapper;
        }

        private JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
            return getObjectMapper().getTypeFactory().constructParametricType(collectionClass, elementClasses);
        }
    }

    public static class Paging {
        int page;
        int maxRows;
        int rowEnd;
        int rowStart;
        int totalRows;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getMaxRows() {
            return maxRows;
        }

        public void setMaxRows(int maxRows) {
            this.maxRows = maxRows;
        }

        public int getRowEnd() {
            return rowEnd;
        }

        public void setRowEnd(int rowEnd) {
            this.rowEnd = rowEnd;
        }

        public int getRowStart() {
            return rowStart;
        }

        public void setRowStart(int rowStart) {
            this.rowStart = rowStart;
        }

        public int getTotalRows() {
            return totalRows;
        }

        public void setTotalRows(int totalRows) {
            this.totalRows = totalRows;
        }
    }

    public static class Wrapper<T> {
        List<T> data;
        Paging paging;
        String caption;
        List<String> titles;
        List<String> _id;

        public List<T> getData() {
            return data;
        }

        public void setData(List<T> data) {
            this.data = data;
        }

        public Paging getPaging() {
            return paging;
        }

        public void setPaging(Paging paging) {
            this.paging = paging;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public List<String> getTitles() {
            return titles;
        }

        public void setTitles(List<String> titles) {
            this.titles = titles;
        }

        public List<String> get_id() {
            return _id;
        }

        public void set_id(List<String> _id) {
            this._id = _id;
        }
    }


}
