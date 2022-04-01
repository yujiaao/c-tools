package com.bixuebihui.elasticsearch;

import com.bixuebihui.DbException;
import com.bixuebihui.db.GroupFunction;
import com.bixuebihui.db.Record;
import com.bixuebihui.db.SqlPocket;
import com.bixuebihui.elasticsearch.query.Query;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.NotImplementedException;


import java.io.IOException;
import java.util.*;

/**
 * @author xwx
 */
public abstract class EsOperator<T,V> implements Record<T>{
    EsRequest request;

    public EsOperator(EsRequest request) {
        this.request = request;
    }

    @Override
    public T find() throws DbException {
        throw new NotImplementedException();
    }

    @Override
    public boolean delete() throws DbException {
        throw new NotImplementedException();
    }

    @Override
    public int count() throws DbException {
        throw new NotImplementedException();
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

    public static class Paging{
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

    public static  class Wrapper<T>{
        List<T> data;

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

        Paging paging;
        String caption;
        List<String> titles;
        List<String> _id;
    }





    public Wrapper<T> toObjectWrapper(String jsonString) {
        try {

           String json =  EsRequest.esJsonToTableJson(jsonString, 0,0,0,0,0);
            JsonWapper wapper = builder();
            JavaType type = wapper.getObjectMapper().getTypeFactory().constructParametricType(Wrapper.class, getType());
            Wrapper<T> res=  wapper.getObjectMapper().readValue(json, type);
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
            JsonNode node = EsRequest.getTableJsonNode(jsonString, 0,0,0,0,0);
            return builder().getObjectMapper().readerForListOf(clz).readValue(node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


//    public abstract List<T> toObjectList(String jsonString);


    @Override
    public List<Object> getVector(String field) throws DbException {
        throw new NotImplementedException();
    }

    @Override
    public List<T> findAll() throws DbException {
        int from=0;
        int size =10;
        LinkedHashMap<String, String> sort = new LinkedHashMap<>();
        String res = request.search(EsQueryBuilder.build(Query.match_all(), from, size, "", sort));

        Wrapper<T> w =  toObjectWrapper(res);

        return w.data;
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
        HashMap<String, Object> map = Maps.newHashMap();
        for(int i=0; i<fields.length; i++) {
            map.put(fields[i], values[i]);
        }
        try {
            return request.update(_id, map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DbException(e.getMessage());
        }
    }


    public int update(String _id, String fields, Object values) throws DbException {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put(fields, values);
        try {
            return request.update(_id, map);
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
    public Wrapper<T> selectWithJoin(String resultFields, Query query, LinkedHashMap<String, String> sort, int begin, int end) {
        int size =end-begin;

        String res = request.search(EsQueryBuilder.build(query, begin, size,resultFields, sort));

        return toObjectWrapper(res);
    }

    public List<EsSearchHitResult<T>> select(String resultFields, Query query, LinkedHashMap<String, String> sort, int begin, int end) {
        int size =end-begin;

        String res = request.search(EsQueryBuilder.build(query, begin, size,resultFields, sort));

        return toEsResultWrapper(res);
    }

    public <K> List<K> select(String resultFields, Query query, LinkedHashMap<String, String> sort, int begin, int end, Class<K> clz) {
        int size =end-begin;

        String res = request.search(EsQueryBuilder.build(query, begin, size,resultFields, sort));

        return toObjectList(res, clz);
    }

    public int deleteByQuery(Query sql) {
        throw new NotImplementedException();
    }

}
