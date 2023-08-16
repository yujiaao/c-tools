package com.bixuebihui.elasticsearch;

import com.bixuebihui.elasticsearch.processor.Sort;
import com.bixuebihui.elasticsearch.query.Query;
import com.bixuebihui.jdbc.SqlFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        filter.addFilter("age", SqlFilter.Comparison.BETWEEN, new Integer[]{10, 15});
        filter.addFilter("height", SqlFilter.Comparison.GT, new Integer[]{100});

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(), emptySet(), null, null, emptyMap());

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"must\":[{\"range\":{\"age\":{\"from\":10,\"to\":15}}},{\"range\":{\"height\":{\"gt\":100}}}]}}}", res);

    }

    @Test
    void toEsObjectMatch() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        filter.addFilter("title", SqlFilter.Comparison.CONTAIN, new String[]{"abc 中国"});
        filter.addFilter("height", SqlFilter.Comparison.GT, new Integer[]{100});

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(), emptySet(), null, null, emptyMap());

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"must\":[{\"match\":{\"title\":\"abc 中国\"}},{\"range\":{\"height\":{\"gt\":100}}}]}}}", res);

    }

    @Test
    void toEsObjectWildcard() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        filter.addFilter("title", SqlFilter.Comparison.WILDCARD, new String[]{"中国", "北京"});
        filter.addFilter("height", SqlFilter.Comparison.GT, new Integer[]{100});

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(), emptySet(), null, null, emptyMap());

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"should\":[{\"wildcard\":{\"title\":" +
                "{\"boost\":1.0,\"value\":\"中国*\"}}},{\"wildcard\":{\"title\":{\"boost\":1.0,\"value\":\"北京*\"}}}]," +
                "\"must\":[{\"range\":{\"height\":{\"gt\":100}}}]}}}", res);

    }


    @Test
    void toQuery() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        filter.addFilter("title", SqlFilter.Comparison.WILDCARD, new String[]{"中国", "北京"});
        filter.addFilter("height", SqlFilter.Comparison.GT, new Integer[]{100});

        SqlFilter f1 = new SqlFilter();
        f1.addFilter("a1",
                SqlFilter.Comparison.LT, new Integer[]{100});
        f1.addFilter("a2",
                SqlFilter.Comparison.LT, new Integer[]{100});

        SqlFilter f2 = new SqlFilter();
        f2.addFilter("b1",
                SqlFilter.Comparison.GTE, new Integer[]{100});
        f2.addFilter("b2",
                SqlFilter.Comparison.GTE, new Integer[]{100});
        f2.setSubGroupsJoinOperator(SqlFilter.Operator.OR);

        filter.addSubGroupCondition(SqlFilter.Operator.AND, f1, f2);


        Query query = filter.toQuery();
        assertEquals(2, query.getParams().size());

        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(), null, null, emptyMap());

        System.out.println(res);
    }

    @Test
    void toQueryOr() {
        ElasticSearchFilter filter = new ElasticSearchFilter();

        ElasticSearchFilter f1 = new ElasticSearchFilter();
        f1.addFilter("a1",
                SqlFilter.Comparison.LT, new Integer[]{100});
        f1.addFilter("b1",
                SqlFilter.Comparison.GTE, new Integer[]{100});
        filter.addSubGroupCondition(SqlFilter.Operator.OR, f1);


        Query query = filter.toQuery();
        assertEquals(1, query.getParams().size());

        System.out.println(query.toArray());

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.valueToTree(query));

        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(), null, null, emptyMap());

        System.out.println(res);
    }


    @Test
    void toQuerySubOr() {
        ElasticSearchFilter filter = new ElasticSearchFilter();

        SqlFilter f2 = new SqlFilter();
        f2.addFilter("b1",
                SqlFilter.Comparison.GTE, new Integer[]{100});
        f2.addFilter("b2",
                SqlFilter.Comparison.GTE, new Integer[]{100});
        f2.setSubGroupsJoinOperator(SqlFilter.Operator.OR);

        filter.addSubGroupCondition(SqlFilter.Operator.AND, f2);


        Query query = filter.toQuery();
        assertEquals(1, query.getParams().size());

        System.out.println(query.toArray());

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.valueToTree(query));

        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(), null, null, emptyMap());

        System.out.println(res);
    }


    @Test
    void toQuerySubOr2() {
        ElasticSearchFilter filter = new ElasticSearchFilter();

        SqlFilter f2 = new SqlFilter();
        f2.addFilter("b1",
                SqlFilter.Comparison.GTE, new Integer[]{100});
        f2.addFilter("b2",
                SqlFilter.Comparison.GTE, new Integer[]{100});
        f2.setSubGroupsJoinOperator(SqlFilter.Operator.OR);

        SqlFilter f3 = new SqlFilter();
        f3.addFilter("c1",
                SqlFilter.Comparison.IS, new Double[]{0.9});
        f2.addSubGroupCondition(SqlFilter.Operator.AND, f3);

        filter.addSubGroupCondition(SqlFilter.Operator.OR, f2);

        Query query = filter.toQuery();
        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(), null, null, emptyMap());

        System.out.println(res);
    }


}
