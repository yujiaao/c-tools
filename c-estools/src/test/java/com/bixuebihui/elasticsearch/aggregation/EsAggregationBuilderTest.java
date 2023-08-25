package com.bixuebihui.elasticsearch.aggregation;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EsAggregationBuilderTest {


    @org.junit.jupiter.api.Test
    void test(){
        DateHistogram res = EsAggregationBuilder.buildDateHistogram("group_by_grabTime", "@timestamp",
                "5m", "yyyy-MM-dd HH",  "1533556800000", "1533806520000");

        assertEquals("group_by_grabTime", res.getName());
        assertEquals("@timestamp", res.getParams().get("field"));
        assertEquals("5m", res.getParams().get("fixed_interval"));
        assertEquals("yyyy-MM-dd HH", res.getParams().get("format"));
        assertEquals("+08:00", res.getParams().get("time_zone"));
    }

}
