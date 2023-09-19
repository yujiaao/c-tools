package com.bixuebihui.elasticsearch;

import com.bixuebihui.elasticsearch.aggregation.AbstractAggregation;

import com.bixuebihui.elasticsearch.query.Query;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
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

    static final String ACTION_UPDATE_BY_QUERY = "_update_by_query";
    static final String ACTION_UPDATE = "_update";
    public static final String UPDATE_DOC = "doc";

    static final String QUERY_ALL_FIELDS = "q=?";
    static final String SIZE = "size";
    static final String FROM = "from";
    static final String SOURCE = "_source";
    static final String QUERY ="query";
    static final String AGGS ="aggs";
    static final String COUNT ="count";
    static final String INCLUDES ="includes";
    static final String EXCLUDES ="excludes";

    static final String SCRIPT ="script";
    public static final String SCRIPT_SOURCE = "source";
    public static final String SCRIPT_PARAMS = "params";
    public static final String SORT = "sort";

    public static final String SEARCH_AFTER = "search_after";



    static ObjectMapper objectMapper = new ObjectMapper();


    static  boolean isNotEmpty(Set<String> fields){
       return   fields != null &&  !fields.isEmpty();
    }


    /**
     * SELECT * FROM crash-2022-06-14 where proviceid>userid
     * {
     * 	"from": 0,
     * 	"size": 200,
     * 	"query": {
     * 		"bool": {
     * 			"filter": [
     *                                {
     * 					"bool": {
     * 						"must": [
     *                            {
     * 								"script": {
     * 									"script": {
     * 										"source": "doc['proviceid'].value > doc['userid'].value",
     * 										"lang": "painless"
     *                                    },
     * 									"boost": 1
     *                                }
     *                            }
     * 						],
     * 						"adjust_pure_negative": true,
     * 						"boost": 1
     *                    }
     *                }
     * 			],
     * 			"adjust_pure_negative": true,
     * 			"boost": 1
     * 		}
     * 	}
     * }
     *
     * @param query
     * @param from
     * @param size
     * @param commaSeparatedIncludeFields
     * @param excludesFields
     * @param sort
     * @return
     */
    public static String build(Query query, int from, int size,
                               Set<String> commaSeparatedIncludeFields,
                               Set<String> excludesFields,
                               LinkedHashMap<String, String> sort,
                               String scriptSource,
                               Map<String,Object> scriptParams,
                               AbstractAggregation aggs, List<Object> searchAfter)  {
        Map queryConditions = query.toArray();

        HashMap<Object, Object> body = Maps.newHashMap();
        body.put(QUERY, queryConditions);

        if(aggs!=null){
            body.put(AGGS, Map.of(aggs.getName(), aggs.toArray()));
        }

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
            body.put(SORT, sort);
        }
        if(searchAfter!=null && !searchAfter.isEmpty()){
            if (sort == null || searchAfter.size() != sort.size()) {
                logger.error("search_after value size must equals sort field counts!");
            }
            body.put(SEARCH_AFTER, searchAfter);
        }

        if(scriptSource!=null){
            body.put(SCRIPT_SOURCE, Map.of(SCRIPT_SOURCE,scriptSource, SCRIPT_PARAMS, scriptParams, "lang", "painless"));
        }

        return toJsonString(body);
    }

    public static String buildCount(Query query){
        Map queryConditions = query.toArray();

        HashMap<Object, Object> body = Maps.newHashMap();
        body.put(QUERY, queryConditions);

        // 大于10000时，下面的返回总数，relation 字段返回  eq, 否则返回  10000， relation 字段返回  gte
        body.put("track_total_hits", true);

        // 不返回内容本身，只返回聚合数据
        body.put(SIZE, 0);

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


    /**
     * POST /all-content-info/_update_by_query
     * {
     *   "query":{
     *     "bool":{
     *       "must":[
     *     {"terms":{"global_id":[1961991758749401088,1985042401953087488,1987523321226362880]}}
     *     ]
     *     }
     *   },
     *
     *   "script": {
     *     "source": "ctx._source['carseries_status'] = 1;ctx._source['is_recommend=1'];"
     *   }
     * }
     * @param updateFieldsValues field's values to update to
     * @return
     */
    public static String buildUpdateByQuery(Query query, Map<String, Object> updateFieldsValues)  {
        Map queryConditions = query.toArray();

        HashMap<Object, Object> body = Maps.newHashMap();
        body.put(QUERY, queryConditions);

        HashMap<Object, Object> source = Maps.newHashMap();

        StringBuilder sb = new StringBuilder();
        HashMap<String, Object> valueMap = new HashMap<>(32);

        updateFieldsValues.forEach((key, value) -> {
            sb.append("ctx._source['").append(key).append("'] ="+SCRIPT_PARAMS+"['").append(key).append("'];");
            valueMap.put(key, value);
        });

        if(sb.length()!=0) {
            source.put(SCRIPT_SOURCE, sb.toString());
            source.put(SCRIPT_PARAMS, valueMap);
            body.put(SCRIPT, source);
        }


        return toJsonString(body);
    }




}
