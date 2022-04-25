package com.bixuebihui.elasticsearch;

import com.bixuebihui.elasticsearch.query.Query;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * a helper class for EsRequest to build query json string
 * @author xwx
 */
public class EsQueryBuilder {
    public static final String EMPTY_JSON_STRING = "{}";
    static  final Logger logger = LoggerFactory.getLogger(EsQueryBuilder.class);
    static final String ACTION_SEARCH = "_search";
    static final String ACTION_BULK = "_bulk";
    static final String ACTION_CAT = "_cat";
    static final String ACTION_INDICES = "indices";
    static final String ACTION_COUNT = "_count";
    static final String ACTION_DELETE_BY_QUERY = "_delete_by_query";
    static final String ACTION_UPDATE = "_update";
    public static final String UPDATE_DOC = "doc";

    static final String QUERY_ALL_FIELDS = "q=?";
    static final String SIZE = "size";
    static final String FROM = "from";
    static final String SOURCE = "_source";
    static final String QUERY ="query";
    static final String COUNT ="count";
    static final String INCLUDES ="includes";
    static final String EXCLUDES ="excludes";



    static ObjectMapper objectMapper = new ObjectMapper();


    static  boolean isNotEmpty(Set<String> fields){
       return   fields != null &&  !fields.isEmpty();
    }


    public static String build(Query query, int from, int size, Set<String> commaSeparatedIncludeFields, Set<String> excludesFields, LinkedHashMap<String, String> sort)  {
        Map queryConditions = query.toArray();

        HashMap<Object, Object> body = Maps.newHashMap();
        body.put(QUERY, queryConditions);
        if(size>0) {
            body.put(SIZE, size);
        }
        if(from>0) {
            body.put(FROM, from);
        }

        if(isNotEmpty(commaSeparatedIncludeFields)
             ||  isNotEmpty(excludesFields)
        ) {
            Map<String, Set<String>> map = isNotEmpty(commaSeparatedIncludeFields) && isNotEmpty(excludesFields) ?
                    Map.of(INCLUDES, commaSeparatedIncludeFields, EXCLUDES, excludesFields):
                    ( isNotEmpty(commaSeparatedIncludeFields) ?
                    Map.of( INCLUDES, commaSeparatedIncludeFields) : Map.of(EXCLUDES, excludesFields));

            body.put(SOURCE, map);

        }

        if(sort!=null && !sort.isEmpty()){
            body.put("sort", sort);
        }

        return toJsonString(body);
    }

    private static String toJsonString(HashMap<Object, Object> body) {
        String s = EMPTY_JSON_STRING;
        try {
            s = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        logger.debug(s);
        return s;
    }


    public static String buildUpdate(Map<String, Object> updateFieldsValues)  {

        HashMap<Object, Object> body = Maps.newHashMap();
        body.put(UPDATE_DOC, updateFieldsValues);

        return toJsonString(body);
    }


}
