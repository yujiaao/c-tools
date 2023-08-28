package com.bixuebihui.elasticsearch.aggregation;

import com.bixuebihui.elasticsearch.EsRequest;
import com.bixuebihui.elasticsearch.query.Query;
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
    void testRequestAgg(){
        DateHistogram res = EsAggregationBuilder.buildDateHistogram("created_stime", "10m", "status", "count");

        EsRequest esRequest= new EsRequest("http://localhost:9200", "elastic", "changeme");
        esRequest.setIndexName("test*");
        Query q=Query.match_all();

        q.addParam("aggs", res);

        String res2 = esRequest.query(q, 0, 10);
        System.out.println(res2);

    }

}
