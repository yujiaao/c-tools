package com.bixuebihui.jmesa.elasticsearch;

import com.bixuebihui.DbException;
import com.bixuebihui.db.GroupFunction;
import com.bixuebihui.db.Record;
import com.bixuebihui.db.SqlPocket;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;
import com.bixuebihui.jmesa.elasticsearch.query.Query;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author xwx
 */
public abstract class EsOperator<T,V> implements Record<T>{
    EsRequest request;

    public EsOperator(EsRequest request) {
        this.request = request;
    }


    public static JsonWapper builder() {
        return new JsonWapper();
    }




    static  class JsonWapper {
        private  ObjectMapper objectMapper;

        public  ObjectMapper getObjectMapper() {
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            }

            return objectMapper;
        }

        private  JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
            return getObjectMapper().getTypeFactory().constructParametricType(collectionClass, elementClasses);
        }
    }

    public static  <K> List<K> toObjectList(String jsonString, Class<K> clz) {
        //JavaType javaType = builder().getCollectionType(ArrayList.class, clz);
        try {
            JsonNode node = EsRequest.getSource(jsonString);
            return builder().getObjectMapper().readerForListOf(clz).readValue(node);
            //return builder().getObjectMapper().readValue(jsonString, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String getIndexName();


    public abstract List<T> toObjectList(String jsonString);


    @Override
    public List<Object> getVector(String field) throws DbException {
       //return queryBuilder;
        return Collections.emptyList();
    }

    @Override
    public List<T> findAll() throws DbException {
        int from=0;
        int size =10;
        LinkedHashMap<String, String> sort = new LinkedHashMap<>();
        String res = request.search(EsQueryBuilder.build(Query.match_all(), from, size, "", sort));

        return toObjectList(res);
    }

    @Override
    public <K> List<K> findAll(Class<K> clz) throws DbException {
        int from=0;
        int size =10;
        LinkedHashMap<String, String> sort = new LinkedHashMap<>();
        String res = request.search(EsQueryBuilder.build(Query.match_all(), from, size,"", sort));

        return toObjectList(res, clz);
    }

    @Override
    public Object get(String field) throws DbException {
        return null;
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
    public SqlPocket getSql() throws DbException {
        return null;
    }

    @Override
    public boolean exists() throws DbException {
       throw new NotImplementedException();
    }

    @Override
    public List<String> getStringVector(String field) throws DbException {
        return null;
    }

    @Override
    public List<Long> getLongVector(String field) throws DbException {
        return null;
    }

    @Override
    public int inc(String field) throws DbException {
        return 0;
    }

    @Override
    public int update(String[] fields, Object[] values) throws DbException {
        return 0;
    }

    @Override
    public int update(String fields, Object values) throws DbException {
        return 0;
    }

    /**
     *
     * POST my-index-000001/_search
     * {
     *   "query": {
     *     "match": {
     *       "user.id": "kimchy"
     *     }
     *   },
     *   "fields": [
     *     "user.id",
     *     "http.response.*",
     *     {
     *       "field": "@timestamp",
     *       "format": "epoch_millis"
     *     }
     *   ],
     *   "_source": false
     * }
     * @param resultFields
     * @param query
     * @param sort
     * @param begin
     * @param end
     * @return
     */
    public List<T> selectWithJoin(String resultFields, Query query, LinkedHashMap<String, String> sort, int begin, int end) {
        int from=begin;
        int size =end-begin;

        String res = request.search(EsQueryBuilder.build(query,  from, size,resultFields, sort));

        return toObjectList(res);
    }

    public <K> List<K> select(String resultFields, Query query, LinkedHashMap<String, String> sort, int begin, int end, Class<K> clz) {
        int from=begin;
        int size =end-begin;

        String res = request.search(EsQueryBuilder.build(query,  from, size,resultFields, sort));

        return toObjectList(res, clz);
    }

    public int deleteByQuery(Query sql) {
            return 0;
    }

}
