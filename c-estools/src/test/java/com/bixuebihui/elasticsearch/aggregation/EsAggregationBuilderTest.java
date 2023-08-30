package com.bixuebihui.elasticsearch.aggregation;

import com.bixuebihui.elasticsearch.EsRequest;
import com.bixuebihui.elasticsearch.query.Query;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EsAggregationBuilderTest {


    @Test
    void test(){
        DateHistogram res = EsAggregationBuilder.buildDateHistogram("group_by_grabTime", "@timestamp",
                "5m", "yyyy-MM-dd HH",  "1533556800000", "1533806520000");

        assertEquals("group_by_grabTime", res.getName());
        assertEquals("@timestamp", res.getParams().get("field"));
        assertEquals("5m", res.getParams().get("fixed_interval"));
        assertEquals("yyyy-MM-dd HH", res.getParams().get("format"));
        assertEquals("+08:00", res.getParams().get("time_zone"));
    }

    @Test
    void testDateHistWithTerms(){
        DateHistogram res = EsAggregationBuilder.buildDateHistogram("group_by_grabTime", "@timestamp",
                "5m", "yyyy-MM-dd HH",  "1533556800000", "1533806520000");
        res.addAggregation(new Terms("group_by_status")
                .setField( "status")
                .setName("group_by_status"));

        assertEquals("group_by_grabTime", res.getName());
        assertEquals("@timestamp", res.getParams().get("field"));
        assertEquals("5m", res.getParams().get("fixed_interval"));
        assertEquals("yyyy-MM-dd HH", res.getParams().get("format"));
        assertEquals("+08:00", res.getParams().get("time_zone"));
        assertEquals("group_by_status", res.getAggs().get("group_by_status").getName());
        assertEquals("status", res.getAggs().get("group_by_status").getParams().get("field"));
    }

    @Test
    void testRequestAgg() throws JsonProcessingException {
        DateHistogram res = EsAggregationBuilder.buildDateHistogram("created_stime", "10m", "status", null);

        EsRequest esRequest= new EsRequest("http://localhost:9200", "elastic", "changeme");
        esRequest.setIndexName("test*");
        Query q=Query.match_all();


        String res2 = esRequest.query(q, 0, 1, null,  res);
        JsonNode res3 = esRequest.getAggTableJsonNode(res2, res.getName(), res.getAggs().entrySet().stream().findFirst().get().getValue().getName());
        System.out.println(res3);

    }

}
