package com.bixuebihui.elasticsearch.aggregation;

import com.bixuebihui.elasticsearch.EsRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

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

    public static  List<CountObj> parse(String json, String firstAggName, String secondAggName) throws JsonProcessingException {
        JsonNode node = EsRequest.getAggTableJsonNode(json, firstAggName, secondAggName);
        return jsonNodeToCountObjList(node);
    }




}
