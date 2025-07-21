package com.bixuebihui.elasticsearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class EsActiveRecordImplTest {
    String indexName = "my-index-000001";

    private EsActiveRecordImpl<Object, Long> ar = new EsActiveRecordImpl<>(
            new BaseEsOperator<>( new EsRequest("http://localhost:9200","","", indexName)) {
                @Override
                protected Class<Object> getType() {
                    return Object.class;
                }
            });

    @BeforeEach
    void setUp() {

    }


    @Test
    void esActiveRecordImpl() {
        new EsActiveRecordImpl<>();
    }


    @Test
    void getResultFields() {
    }

    @Test
    void getResultFieldsSet() {
    }

    @Test
    void in() {
        Object res = ar.in("abc", new Object[]{1, 2, 3}).find();
        assertTrue(res==null || res.toString().contains("abc"));
    }

    @Test
    void emptyStringAsNullCondition() {
    }

    @Test
    void testIn() {

    }

    @Test
    void like() {
    }

    @Test
    void startWith() {
    }

    @Test
    void eq() {
        List<Object> res = ar.eq("abc", 2).findAll();
        System.out.println(res);
    }

    @Test
    void between() {
    }

    @Test
    void notBetween() {
    }

    @Test
    void ne() {
    }

    @Test
    void testEq() {
    }

    @Test
    void greaterThan() {
    }

    @Test
    void smallerThan() {
    }

    @Test
    void isNull() {
    }

    @Test
    void isNotNull() {
    }

    @Test
    void greaterOrEqualThan() {
    }

    @Test
    void smallerOrEqualThan() {
    }

    @Test
    void order() {
    }

    @Test
    void orderByIn() {
    }

    @Test
    void limit() {
    }

    @Test
    void findAll() {
    }

    @Test
    void getParams() {
    }

    @Test
    void testFindAll() {
    }

    @Test
    void formWhereClause() {
    }

    @Test
    void find() {
    }

    @Test
    void delete() {
    }

    @Test
    void get() {
    }

    @Test
    void getVector() {
    }

    @Test
    void getLongVector() {
    }

    @Test
    void parseLimitSql() {
    }

    @Test
    void getSql() {
    }

    @Test
    void parseOrder() {
    }

    @Test
    void parseGroupBy() {
    }

    @Test
    void asc() {
    }

    @Test
    void desc() {

        List<Object> res = ar.smallerOrEqualThan("abc",5).desc("abc").findAll();
        System.out.println(res);

        assertEquals("[{product=chocolate, price=[20, 4], abc=5}, {product=chocolate, price=[20, 4], abc=5}, {product=chocolate, price=[20, 4], abc=4}, {product=chocolate, price=[20, 4], abc=3}, {product=chocolate, price=[20, 4], abc=2, name=12312312321}]", res.toString());

    }

    @Test
    void last() {
    }

    @Test
    void getCondStack() {
    }

    @Test
    void or() {
    }

    @Test
    void alias() {
    }

    @Test
    void fields() {
        String _id="12345";
        String field = "abc";
        String value ="123";
        List<Object> res = ar.eq("abc", 2).fields("abc").findAll();
        System.out.println(res);

        assertEquals("[{abc=2}]", res.toString());

        List<Object> res1 = ar.eq("abc", 2).excludeFields(Set.of("abc")).findAll();
        System.out.println(res1);

        assertEquals("[{product=chocolate, price=[20, 4], name=12312312321}]", res1.toString());
    }

    @Test
    void join() {
    }

    @Test
    void groupBy() {
    }

    @Test
    void having() {
    }

    @Test
    void count() {
    }

    @Test
    void countValue() {
    }

    @Test
    void exists() {
    }

    @Test
    void update() {
        String _id="3";
    }

    /**
     * 测试批量更新文档
     */
    @Test
    void updateBatch() {
        // 准备测试数据
        List<Object> documents = List.of(
            Map.of("id", "1", "name", "doc1", "value", "value1"),
            Map.of("id", "2", "name", "doc2", "value", "value2")
        );

        // 测试基本批量更新
        boolean result = ar.updateBatch(indexName, documents, doc -> (String)((Map)doc).get("id"));
        assertTrue(result, "基本批量更新应该成功");

        // 测试带刷新的批量更新
        result = ar.updateBatch(indexName, documents, doc -> (String)((Map)doc).get("id"), true);
        assertTrue(result, "带刷新的批量更新应该成功");

        // 测试带mixin的批量更新
        result = ar.updateBatch(indexName, documents,
                doc -> (String)((Map)doc).get("id"), true, TestDocumentMixin.class,
                true);
        assertTrue(result, "带mixin的批量更新应该成功");
    }

    /**
     * 测试批量更新空列表
     */
    @Test
    void updateBatchEmptyList() {
        List<Object> emptyList = List.of();
        boolean result = ar.updateBatch(indexName, emptyList, doc -> (String)((Map)doc).get("id"));
        assertTrue(result, "空列表的批量更新应该成功");
    }

    /**
     * 测试批量更新时ID为null的情况
     */
    @Test
    void updateBatchWithNullId() {
        List<Object> documents = List.of(
            Map.of("id", null, "name", "doc1", "value", "value1"),
            Map.of("id", "2", "name", "doc2", "value", "value2")
        );
        boolean result = ar.updateBatch(indexName, documents, doc -> (String)((Map)doc).get("id"));
        assertTrue(result, "包含null ID的批量更新应该成功");
    }

    // 测试用的Mixin类
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"value"})
    private abstract static class TestDocumentMixin {
        @com.fasterxml.jackson.annotation.JsonProperty("document_name")
        String name;
    }

    @Test
    void updateByQuery() {
        String field = "name";
        long value = System.currentTimeMillis();
        int res = ar.eq("abc", "123").update(field, value+"");
        System.out.println(res);

        assertEquals(1, res);
    }
}
