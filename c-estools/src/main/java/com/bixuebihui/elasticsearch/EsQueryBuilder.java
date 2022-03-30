package com.bixuebihui.elasticsearch;

import com.bixuebihui.elasticsearch.query.Query;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    static final String FIELDS ="fields";


    static ObjectMapper objectMapper = new ObjectMapper();


    public static String build(Query query, int from, int size, String commaSeparatedFields, LinkedHashMap<String, String> sort)  {
        Map queryConditions = query.toArray();

        HashMap<Object, Object> body = Maps.newHashMap();
        body.put(QUERY, queryConditions);
        if(size>0) {
            body.put(SIZE, size);
        }
        if(from>0) {
            body.put(FROM, from);
        }

        if(StringUtils.isNoneBlank(commaSeparatedFields)) {
            query.setParam(FIELDS, List.of(commaSeparatedFields.split(",")));
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
