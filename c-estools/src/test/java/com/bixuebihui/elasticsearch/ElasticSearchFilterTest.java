package com.bixuebihui.elasticsearch;

import com.bixuebihui.db.SqlString;
import com.bixuebihui.elasticsearch.aggregation.DateHistogram;
import com.bixuebihui.elasticsearch.aggregation.EsAggregationBuilder;
import com.bixuebihui.elasticsearch.aggregation.Terms;
import com.bixuebihui.elasticsearch.query.Query;
import com.bixuebihui.jdbc.SqlFilter;
import com.bixuebihui.elasticsearch.EsActiveRecordImpl;
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
    void moreLikeThis() {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        filter.addFilter("age", SqlFilter.Comparison.BETWEEN, new Integer[]{10, 15});
        filter.addFilter("title,content", SqlFilter.Comparison.MORE_LIKE_THIS, new String[]{"this is a title", "this is the content of a beautiful text"});

        String res = filter.toEsObject(filter.toQuery(), 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), null, emptyList());

        assertEquals("{\"size\":10,\"query\":{\"bool\":{\"must\":[{\"range\":{\"age\":{\"from\":10,\"to\":15}}},{\"more_like_this\":{\"max_query_terms\":50,\"boost_terms\":1.5,\"min_doc_freq\":1,\"like\":[\"this is a title\",\"this is the content of a beautiful text\"],\"min_term_freq\":1,\"minimum_should_match\":\"90%\",\"fields\":[\"title\",\"content\"]}}]}}}", res);

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
        assertTrue(json.contains("\"like\":[\"示例文本内容\"]"));
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
        assertTrue(json.contains("\"like\":[\"标题示例\",\"内容示例\"]"));
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


    /**
     *  需求增加对 filter + script 的支持， 类似以下的写法
     * GET cms-content/_search
     *
     * {
     *     "size": 10000,
     *     "query": {
     *         "bool": {
     *             "must": [
     *                 {
     *                     "term": {
     *                         "is_del": {
     *                             "boost": 1,
     *                             "value": false
     *                         }
     *                     }
     *                 },
     *                 {
     *                     "term": {
     *                         "is_publish": {
     *                             "boost": 1,
     *                             "value": true
     *                         }
     *                     }
     *                 },
     *                 {
     *                     "term": {
     *                         "status": {
     *                             "boost": 1,
     *                             "value": 8
     *                         }
     *                     }
     *                 },
     *                 {
     *                     "term": {
     *                         "biz_type": {
     *                             "boost": 1,
     *                             "value": 0
     *                         }
     *                     }
     *                 },
     *                 {
     *                     "terms": {
     *                         "series_ids": [
     *                             8721
     *                         ]
     *                     }
     *                 },
     *                 {
     *                     "terms": {
     *                         "tag_ids": [
     *                             1
     *                         ]
     *                     }
     *                 },
     *                 {
     *                     "terms": {
     *                         "secondary_ids": [
     *                             101
     *                         ]
     *                     }
     *                 },
     *                 {
     *                     "terms": {
     *                         "new_car_label": [
     *                             "新车谍照",
     *                             "新车效果图",
     *                             "新车申报图/专利图",
     *                             "新车发布预告",
     *                             "新车发布",
     *                             "新车预售预告",
     *                             "新车预售",
     *                             "新车上市预告",
     *                             "上市前体验",
     *                             "新车上市",
     *                             "上市后体验",
     *                             "技术解析",
     *                             "购车分享"
     *                         ]
     *                     }
     *                 },
     *                 {
     *                     "range": {
     *                         "publish_time": {
     *                             "from": "2026-05-23 00:00:00",
     *                             "to": "2026-07-25 13:50:22"
     *                         }
     *                     }
     *                 }
     *             ],
     *             "filter": [
     *                 {
     *                     "script": {
     *                         "script": {
     *                             "source": "doc['new_car_series_names'].size() == 0"
     *                         }
     *                     }
     *                 }
     *             ]
     *         }
     *     },
     *     "sort": {
     *         "publish_time": "desc"
     *     }
     * }
      */
    @Test
    void testBoolMustWithFilterScript() throws Exception {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        filter.addFilter("is_del", SqlFilter.Comparison.IS, new Object[]{false});
        filter.addFilter("is_publish", SqlFilter.Comparison.IS, new Object[]{true});
        filter.addFilter("status", SqlFilter.Comparison.IS, new Object[]{8});
        filter.addFilter("biz_type", SqlFilter.Comparison.IS, new Object[]{0});
        filter.addFilter("series_ids", SqlFilter.Comparison.IN, new Object[]{8721});
        filter.addFilter("tag_ids", SqlFilter.Comparison.IN, new Object[]{1});
        filter.addFilter("secondary_ids", SqlFilter.Comparison.IN, new Object[]{101});
        filter.addFilter("new_car_label", SqlFilter.Comparison.IN, new String[]{
                "新车谍照", "新车效果图", "新车申报图/专利图", "新车发布预告", "新车发布",
                "新车预售预告", "新车预售", "新车上市预告", "上市前体验", "新车上市",
                "上市后体验", "技术解析", "购车分享"});
        filter.addFilter("publish_time", SqlFilter.Comparison.BETWEEN,
                new String[]{"2026-05-23 00:00:00", "2026-07-25 13:50:22"});
        filter.addFilter("new_car_series_names", SqlFilter.Comparison.SCRIPT,
                new Object[]{"doc['new_car_series_names'].size() == 0"});

        Query query = filter.toQuery();
        String res = filter.toEsObject(query, 0, 10000, emptySet(), emptySet(),
                null, null, emptyMap(), null, emptyList());

        System.out.println(res);

        assertTrue(res.contains("\"must\""));
        assertTrue(res.contains("\"filter\""));
        assertTrue(res.contains("\"script\""));
        assertTrue(res.contains("\"source\":\"doc['new_car_series_names'].size() == 0\""));
    }

    /**
     * 测试 EsActiveRecord#filterScript 方法能够生成 bool.filter 下的 script 查询
     */
    @Test
    void filterScript() throws Exception {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        EsActiveRecordImpl<Object, Long> record = new EsActiveRecordImpl<>();
        record.setFilter(filter);

        record.filterScript("doc['new_car_series_names'].size() == 0");

        Query query = filter.toQuery();
        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), null, emptyList());
        System.out.println(res);

        assertTrue(res.contains("\"filter\""));
        assertTrue(res.contains("\"script\""));
        assertTrue(res.contains("\"source\":\"doc['new_car_series_names'].size() == 0\""));
        // 未指定 lang 时默认 painless
        assertTrue(res.contains("\"lang\":\"painless\""));
    }

    /**
     * 测试 EsActiveRecord#filterScript 方法带 params 与 lang 的场景
     */
    @Test
    void filterScriptWithParams() throws Exception {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        EsActiveRecordImpl<Object, Long> record = new EsActiveRecordImpl<>();
        record.setFilter(filter);

        Map<String, Object> params = Map.of("min", 1, "max", 5);
        record.filterScript(
                "doc['price'].value >= params.min && doc['price'].value <= params.max",
                params, "painless");

        Query query = filter.toQuery();
        String res = filter.toEsObject(query, 0, 10, emptySet(), emptySet(),
                null, null, emptyMap(), null, emptyList());
        System.out.println(res);

        assertTrue(res.contains("\"filter\""));
        assertTrue(res.contains("\"source\":\"doc['price'].value >= params.min && doc['price'].value <= params.max\""));
        assertTrue(res.contains("\"params\":"));
        assertTrue(res.contains("\"min\":1"));
        assertTrue(res.contains("\"max\":5"));
        assertTrue(res.contains("\"lang\":\"painless\""));
    }

}
