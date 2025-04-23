package com.bixuebihui.elasticsearch;

import com.bixuebihui.elasticsearch.aggregation.DateHistogram;
import com.bixuebihui.elasticsearch.aggregation.EsAggregationBuilder;
import com.bixuebihui.elasticsearch.aggregation.Terms;
import com.bixuebihui.elasticsearch.query.Query;
import com.bixuebihui.jdbc.SqlFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;

class ElasticSearchFilterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

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

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), null, emptyList());

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"must\":[{\"range\":{\"age\":{\"from\":10,\"to\":15}}},{\"range\":{\"height\":{\"gt\":100}}}]}}}", res);

    }

    @Test
    void matchPhrase() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        filter.addFilter("title", SqlFilter.Comparison.MATCH_PHRASE, new String[]{ "rock climbing"});

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), null, emptyList());

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"must\":[{\"match_phrase\":{\"title\":\"rock climbing\"}}]}}}", res);

    }

    @Test
    void toEsObjectMatch() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        filter.addFilter("title", SqlFilter.Comparison.CONTAIN, new String[]{"abc 中国"});
        filter.addFilter("height", SqlFilter.Comparison.GT, new Integer[]{100});

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), null, emptyList());

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"must\":[{\"match\":{\"title\":\"abc 中国\"}},{\"range\":{\"height\":{\"gt\":100}}}]}}}", res);

    }

    @Test
    void toEsObjectWildcard() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        filter.addFilter("title", SqlFilter.Comparison.WILDCARD, new String[]{"中国", "北京"});
        filter.addFilter("height", SqlFilter.Comparison.GT, new Integer[]{100});

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), null, emptyList());

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
        f2.setJoinOperator(SqlFilter.Operator.OR);

        filter.addSubGroupCondition(SqlFilter.Operator.AND, f1, f2);


        Query query = filter.toQuery();
        assertEquals(2, query.getParams().size());

        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), null, emptyList());

        System.out.println(res);
    }

    @Test
    void toQueryOrDirect() {
        ElasticSearchFilter filter = new ElasticSearchFilter();


        filter.addFilter("a1",
                SqlFilter.Comparison.LT, new Integer[]{100});
        filter.addFilter("b1",
                SqlFilter.Comparison.GTE, new Integer[]{100});
        filter.setJoinOperator(SqlFilter.Operator.OR);

        Query query = filter.toQuery();
        assertEquals(2, query.getParams().size());

        System.out.println(query.toArray());

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.valueToTree(query));

        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), null, emptyList());

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"should\":[{\"range\":{\"a1\":{\"lt\":100}}},{\"range\":{\"b1\":{\"gte\":100}}}],\"minimum_should_match\":1}}}"
        ,res);
        System.out.println(res);
    }

    @Test
    void toQueryInnerOr() {
        ElasticSearchFilter filter = new ElasticSearchFilter();

        ElasticSearchFilter f1 = new ElasticSearchFilter();
        f1.addFilter("a1",
                SqlFilter.Comparison.LT, new Integer[]{100});
        f1.addFilter("b1",
                SqlFilter.Comparison.GTE, new Integer[]{100});
        f1.setJoinOperator(SqlFilter.Operator.OR);

        filter.addSubGroupCondition(SqlFilter.Operator.AND, f1);


        Query query = filter.toQuery();
        assertEquals(2, query.getParams().size());

        System.out.println(query.toArray());

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.valueToTree(query));

        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), null, emptyList());

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"should\":[{\"range\":{\"a1\":{\"lt\":100}}},{\"range\":{\"b1\":{\"gte\":100}}}],\"minimum_should_match\":1}}}"
                ,res);
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
        f2.setJoinOperator(SqlFilter.Operator.OR);

        filter.addSubGroupCondition(SqlFilter.Operator.AND, f2);


        Query query = filter.toQuery();
        assertEquals(2, query.getParams().size());

        System.out.println(query.toArray());

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.valueToTree(query));

        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(), null, null, emptyMap(), null, emptyList());

        System.out.println(res);
    }


    @Test
    void toQuerySubOr2() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        filter.addFilter("a1",
                SqlFilter.Comparison.GTE, new Integer[]{99});
        SqlFilter f2 = new SqlFilter();
        f2.addFilter("b1",
                SqlFilter.Comparison.GTE, new Integer[]{100});
        f2.addFilter("b2",
                SqlFilter.Comparison.GTE, new Integer[]{101});
//        f2.setJoinOperator(SqlFilter.Operator.OR);

        SqlFilter f3 = new SqlFilter();
        f3.addFilter("c1",
                SqlFilter.Comparison.IS, new Double[]{0.9});
        f2.addSubGroupCondition(SqlFilter.Operator.OR, f3);

        filter.addSubGroupCondition(SqlFilter.Operator.OR, f2);

        Query query = filter.toQuery();

        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(), null, null, emptyMap(), null);

        System.out.println(res);
        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"should\":[{\"bool\":{\"must\":[{\"term\":{\"c1\":{\"boost\":1.0,\"value\":0.9}}}]}},{\"range\":{\"b1\":{\"gte\":100}}},{\"range\":{\"b2\":{\"gte\":101}}},{\"range\":{\"a1\":{\"gte\":99}}}],\"minimum_should_match\":1}}}"
        ,res);
    }

    @Test
    void toQuerySubOrAnd() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        SqlFilter f1 = new SqlFilter();
        f1.addFilter("a1", SqlFilter.Comparison.GTE, new Integer[]{99});
        f1.addFilter("a2", SqlFilter.Comparison.GTE, new Integer[]{99});

        SqlFilter f2 = new SqlFilter();
        f2.addFilter("b1", SqlFilter.Comparison.GTE, new Integer[]{100});
        f2.addFilter("b2", SqlFilter.Comparison.GTE, new Integer[]{101});

        filter.addSubGroupCondition(SqlFilter.Operator.OR, f1);
        filter.addSubGroupCondition(SqlFilter.Operator.OR, f2);


        Query query = filter.toQuery();

        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(), null, null, emptyMap(), null);

        System.out.println(res);
        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"should\":[{\"bool\":{\"must\":[{\"range\":{\"a1\":{\"gte\":99}}},{\"range\":{\"a2\":{\"gte\":99}}}]}},{\"bool\":{\"must\":[{\"range\":{\"b1\":{\"gte\":100}}},{\"range\":{\"b2\":{\"gte\":101}}}]}}],\"minimum_should_match\":1}}}"
                ,res);
    }

    @Test
    void testWithDateHist(){
        ElasticSearchFilter filter = new ElasticSearchFilter();

        filter.addFilter("age", SqlFilter.Comparison.BETWEEN, new Integer[]{10, 15});

        DateHistogram  agg = EsAggregationBuilder.buildDateHistogram("create_time", "10m", "type", null);

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), agg);

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"must\":[{\"range\":{\"age\":{\"from\":10,\"to\":15}}}]}},\"aggs\":{\"date_hist_by_create_time\":{\"date_histogram\":{\"fixed_interval\":\"10m\",\"field\":\"create_time\",\"time_zone\":\"+08:00\"},\"aggs\":{\"group_by_type\":{\"terms\":{\"field\":\"type\"}}}}}}", res);


    }

    @Test
    void testWithDateAgg(){
        ElasticSearchFilter filter = new ElasticSearchFilter();

        filter.addFilter("age", SqlFilter.Comparison.BETWEEN, new Integer[]{10, 15});

        DateHistogram  agg = EsAggregationBuilder.buildDateHistogramMulti(
                "create_time", "30d",
                List.of("biz_type","biz_id"),
                Map.of(
                        "click_pv","sum",
                        "show_pv","sum"
                ), "6.8.0");

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), agg);

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"must\":[{\"range\":{\"age\":{\"from\":10,\"to\":15}}}]}},\"aggs\":{\"date_hist_by_create_time\":{\"date_histogram\":{\"field\":\"create_time\",\"interval\":\"30d\",\"time_zone\":\"+08:00\"},\"aggs\":{\"sum_click_pv\":{\"sum\":{\"field\":\"click_pv\"}},\"group_by_biz_type_biz_id\":{\"terms\":{\"script\":\"doc['biz_type'].value+'_'+doc['biz_id'].value\"}},\"sum_show_pv\":{\"sum\":{\"field\":\"show_pv\"}}}}}}"
                , res);


    }

    @Test
    void testWithTermsAgg(){
        ElasticSearchFilter filter = new ElasticSearchFilter();

        filter.addFilter("dt", SqlFilter.Comparison.BETWEEN, new String[]{"now-7d/d", "now/d"});

        Terms agg = EsAggregationBuilder.buildTermsAggMulti(
                List.of("biztype_id", "object_id"),
                Map.of(
                        "click_pv","sum",
                        "sight_show_pv","sum"
                ), emptySet());

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), agg);

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"must\":[{\"range\":{\"dt\":{\"from\":\"now-7d/d\",\"to\":\"now/d\"}}}]}},\"aggs\":{\"group_by_biztype_id_object_id\":{\"terms\":{\"script\":\"doc['biztype_id'].value+'_'+doc['object_id'].value\"},\"aggs\":{\"sum_sight_show_pv\":{\"sum\":{\"field\":\"sight_show_pv\"}},\"sum_click_pv\":{\"sum\":{\"field\":\"click_pv\"}}}}}}"
                , res);


    }

    @Test
    void toQuerySearchAfter() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        SqlFilter f1 = new SqlFilter();
        f1.addFilter("a1", SqlFilter.Comparison.GTE, new Integer[]{99});
        filter.addSubGroupCondition(SqlFilter.Operator.AND, f1);

        Query query = filter.toQuery();

        List<Object> searchAfter= List.of(123, "abc");
        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(), null, null, emptyMap(), null, searchAfter);

        System.out.println(res);
        assertEquals("{\"search_after\":[123,\"abc\"],\"size\":10,\"query\":{\"bool\":{\"must\":[{\"range\":{\"a1\":{\"gte\":99}}}]}}}"
                ,res);
    }

    @Test
    void testMoreLikeThisQuery() throws Exception {
        // 创建ElasticSearchFilter
        ElasticSearchFilter filter = new ElasticSearchFilter();
        
        // 使用addFilter方法添加MORE_LIKE_THIS过滤器
        filter.addFilter("content", SqlFilter.Comparison.MORE_LIKE_THIS, new Object[]{"示例文本内容"});
        
        // 转换为Query对象
        Query query = filter.toQuery();
        
        // 验证Query对象
        assertNotNull(query);
        
        // 转换为JSON并验证
        String json = objectMapper.writeValueAsString(query.toArray());
        System.out.println("MoreLikeThis Query JSON: " + json);
        
        // 验证JSON结构
        assertTrue(json.contains("\"more_like_this\""));
        assertTrue(json.contains("\"fields\":[\"content\"]"));
        assertTrue(json.contains("\"doc\":{\"content\":\"示例文本内容\"}"));
        assertTrue(json.contains("\"min_term_freq\":1"));
        assertTrue(json.contains("\"min_doc_freq\":1"));
        assertTrue(json.contains("\"minimum_should_match\":\"90%\""));
    }
    
    @Test
    void testMoreLikeThisWithMultipleFields() throws Exception {
        // 创建ElasticSearchFilter
        ElasticSearchFilter filter = new ElasticSearchFilter();
        
        // 使用addFilter方法添加MORE_LIKE_THIS过滤器，使用多个字段，每个字段对应一个值
        filter.addFilter("title,content", SqlFilter.Comparison.MORE_LIKE_THIS, new Object[]{"标题示例", "内容示例"});
        
        // 转换为Query对象
        Query query = filter.toQuery();
        
        // 验证Query对象
        assertNotNull(query);
        
        // 转换为JSON并验证
        String json = objectMapper.writeValueAsString(query.toArray());
        System.out.println("MoreLikeThis with multiple fields JSON: " + json);
        
        // 验证JSON结构
        assertTrue(json.contains("\"more_like_this\""));
        assertTrue(json.contains("\"fields\":[\"title\",\"content\"]"));
        assertTrue(json.contains("\"doc\":{\"title\":\"标题示例\",\"content\":\"内容示例\"}"));
        assertTrue(json.contains("\"min_term_freq\":1"));
        assertTrue(json.contains("\"min_doc_freq\":1"));
        assertTrue(json.contains("\"minimum_should_match\":\"90%\""));
    }
    
    @Test
    void testMoreLikeThisWithBoolQuery() throws Exception {
        // 创建ElasticSearchFilter
        ElasticSearchFilter filter = new ElasticSearchFilter();
        
        // 使用addFilter方法添加MORE_LIKE_THIS过滤器
        filter.addFilter("content", SqlFilter.Comparison.MORE_LIKE_THIS, new Object[]{"示例文本内容"});
        
        // 添加另一个过滤器
        filter.addFilter("status", SqlFilter.Comparison.IS, new Object[]{"published"});
        
        // 转换为Query对象
        Query query = filter.toQuery();
        
        // 验证Query对象
        assertNotNull(query);
        
        // 转换为JSON并验证
        String json = objectMapper.writeValueAsString(query.toArray());
        System.out.println("MoreLikeThis with bool query JSON: " + json);
        
        // 验证JSON结构
        assertTrue(json.contains("\"bool\""));
        assertTrue(json.contains("\"must\""));
        assertTrue(json.contains("\"more_like_this\""));
        assertTrue(json.contains("\"term\""));
    }
    
    @Test
    void testMoreLikeThisWithOrOperator() throws Exception {
        // 创建ElasticSearchFilter
        ElasticSearchFilter filter = new ElasticSearchFilter();
        
        // 使用addFilter方法添加MORE_LIKE_THIS过滤器
        filter.addFilter("content", SqlFilter.Comparison.MORE_LIKE_THIS, new Object[]{"示例文本内容"});
        
        // 添加另一个过滤器
        filter.addFilter("status", SqlFilter.Comparison.IS, new Object[]{"published"});
        
        // 设置连接操作符为OR
        filter.setJoinOperator(SqlFilter.Operator.OR);
        
        // 转换为Query对象
        Query query = filter.toQuery();
        
        // 验证Query对象
        assertNotNull(query);
        
        // 转换为JSON并验证
        String json = objectMapper.writeValueAsString(query.toArray());
        System.out.println("MoreLikeThis with OR operator JSON: " + json);
        
        // 验证JSON结构
        assertTrue(json.contains("\"bool\""));
        assertTrue(json.contains("\"should\""));
        assertTrue(json.contains("\"more_like_this\""));
        assertTrue(json.contains("\"term\""));
        assertTrue(json.contains("\"minimum_should_match\":1"));
    }
}
