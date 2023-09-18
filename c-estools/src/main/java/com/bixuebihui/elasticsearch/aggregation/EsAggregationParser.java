package com.bixuebihui.elasticsearch.aggregation;

import com.bixuebihui.elasticsearch.EsRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html
 *
 * @author xwx
 */
public class EsAggregationParser {


    static  CountObj jsonNodeToCountObj(JsonNode node){
        CountObj obj = new CountObj();
        obj.key = node.get("key").asText();
        obj.doc_count = node.get("doc_count").asInt();
        obj.items = jsonNodeToCountObjList(node.get("items"));
        return obj;
    }

    static  CountValueObj jsonNodeToCountValueObj(JsonNode node, ObjectMapper jsonObjectMapper) throws JsonProcessingException {
        return jsonObjectMapper.treeToValue(node, CountValueObj.class);
    }

    private static  List<CountObj> jsonNodeToCountObjList(JsonNode buckets) {
        if(buckets == null){
            return null;
        }
        List<CountObj> list = new ArrayList<>();
        for(JsonNode node: buckets){
            list.add(jsonNodeToCountObj(node));
        }
        return list;
    }
    private static  List<CountValueObj> jsonNodeToCountValueObjList(JsonNode buckets, ObjectMapper objectMapper) throws JsonProcessingException {
        if(buckets == null){
            return null;
        }
        List<CountValueObj> list = new ArrayList<>();
        for(JsonNode node: buckets){
            list.add(jsonNodeToCountValueObj(node, objectMapper));
        }
        return list;
    }


    public static  List<CountObj> parse(String json, String firstAggName, String secondAggName) throws JsonProcessingException {
        JsonNode node = EsRequest.getAggTableJsonNode(json, firstAggName, secondAggName);
        return jsonNodeToCountObjList(node);
    }
    public static  List<CountValueObj> parseCountValue(String json, String firstAggName, String secondAggName, ObjectMapper objectMapper) throws JsonProcessingException {
        JsonNode node = EsRequest.getAggTableJsonNode(json, firstAggName, secondAggName);
        return jsonNodeToCountValueObjList(node, objectMapper);
    }




}
