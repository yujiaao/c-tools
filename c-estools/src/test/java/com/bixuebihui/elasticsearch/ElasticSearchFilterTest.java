package com.bixuebihui.elasticsearch;

import com.bixuebihui.jdbc.SqlFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.*;

class ElasticSearchFilterTest {

    @BeforeEach
    void setUp() {
    }


    @Test
    public void testSimple() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
    }


    @Test
    void toEsObject() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        filter.addFilter("age", SqlFilter.Comparison.BETWEEN, new Integer[]{10,15});
        filter.addFilter("height", SqlFilter.Comparison.GT, new Integer[]{100});

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(),emptySet(),null, null, emptyMap());

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"must\":[{\"range\":{\"age\":{\"from\":10,\"to\":15}}},{\"range\":{\"height\":{\"gt\":100}}}]}}}", res);

    }

    @Test
    void toEsObjectMatch() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        filter.addFilter("title", SqlFilter.Comparison.CONTAIN, new String[]{"abc 中国"});
        filter.addFilter("height", SqlFilter.Comparison.GT, new Integer[]{100});

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(),emptySet(),null, null, emptyMap());

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"must\":[{\"match\":{\"title\":\"abc 中国\"}},{\"range\":{\"height\":{\"gt\":100}}}]}}}", res);

    }

    @Test
    void toEsObjectWildcard() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        filter.addFilter("title", SqlFilter.Comparison.WILDCARD, new String[]{"中国","北京"});
        filter.addFilter("height", SqlFilter.Comparison.GT, new Integer[]{100});

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(),emptySet(),null, null, emptyMap());

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"should\":[{\"wildcard\":{\"title\":" +
                "{\"boost\":1.0,\"value\":\"中国*\"}}},{\"wildcard\":{\"title\":{\"boost\":1.0,\"value\":\"北京*\"}}}]," +
                "\"must\":[{\"range\":{\"height\":{\"gt\":100}}}]}}}", res);

    }
}
