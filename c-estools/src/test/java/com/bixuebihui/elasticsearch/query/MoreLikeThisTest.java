package com.bixuebihui.elasticsearch.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * MoreLikeThis查询测试类
 */
class MoreLikeThisTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testMoreLikeThisBasic() throws Exception {
        // 创建MoreLikeThis查询
        MoreLikeThis mlt = Query.more_like_this();

        // 设置字段
        mlt.setFields(Arrays.asList("title", "content"));

        // 设置文档字段
        mlt.setDoc(List.of("示例标题"));

        // 设置其他参数
        mlt.setMinTermFreq(1)
           .setMinDocFreq(1)
           .setMinimumShouldMatch("30%")
           .setBoost(2.0f);

        // 转换为JSON并验证
        String json = objectMapper.writeValueAsString(mlt.toArray());
        System.out.println("MoreLikeThis JSON: " + json);

        // 验证JSON结构
        assertNotNull(json);
        assertEquals(true, json.contains("\"more_like_this\""));
        assertEquals(true, json.contains("\"fields\":[\"title\",\"content\"]"));
        assertEquals(true, json.contains("\"doc\":{\"title\":\"示例标题\"}"));
        assertEquals(true, json.contains("\"min_term_freq\":1"));
        assertEquals(true, json.contains("\"min_doc_freq\":1"));
        assertEquals(true, json.contains("\"minimum_should_match\":\"30%\""));
        assertEquals(true, json.contains("\"boost\":2.0"));
    }

    @Test
    void testMoreLikeThisWithMultipleFields() throws Exception {
        // 创建MoreLikeThis查询
        MoreLikeThis mlt = Query.more_like_this();

        // 设置字段
        mlt.setFields(Arrays.asList("title", "content", "tags"));

        // 设置多个字段
        Map<String, String> fieldTexts = new HashMap<>();
        fieldTexts.put("title", "示例标题");
        fieldTexts.put("content", "这是一个示例内容，用于测试MoreLikeThis查询");
        fieldTexts.put("tags", "测试,示例,Elasticsearch");
        mlt.setDoc(fieldTexts.values().stream().toList());

        // 设置其他参数
        mlt.setMinTermFreq(2)
           .setMinDocFreq(2)
           .setMaxDocFreq(100)
           .setMinWordLength(2)
           .setMaxWordLength(15)
           .setMinimumShouldMatch("50%")
           .setBoost(1.5f)
           .setAnalyzer("standard")
           .setIgnoreTf(true);

        // 转换为JSON并验证
        String json = objectMapper.writeValueAsString(mlt.toArray());
        System.out.println("MoreLikeThis with multiple fields JSON: " + json);

        // 验证JSON结构
        assertNotNull(json);
        assertEquals(true, json.contains("\"more_like_this\""));
        assertEquals(true, json.contains("\"fields\":[\"title\",\"content\",\"tags\"]"));
        assertEquals(true, json.contains("\"doc\":{\"title\":\"示例标题\",\"content\":\"这是一个示例内容，用于测试MoreLikeThis查询\",\"tags\":\"测试,示例,Elasticsearch\"}"));
        assertEquals(true, json.contains("\"min_term_freq\":2"));
        assertEquals(true, json.contains("\"min_doc_freq\":2"));
        assertEquals(true, json.contains("\"max_doc_freq\":100"));
        assertEquals(true, json.contains("\"min_word_length\":2"));
        assertEquals(true, json.contains("\"max_word_length\":15"));
        assertEquals(true, json.contains("\"minimum_should_match\":\"50%\""));
        assertEquals(true, json.contains("\"boost\":1.5"));
        assertEquals(true, json.contains("\"analyzer\":\"standard\""));
        assertEquals(true, json.contains("\"ignore_tf\":true"));
    }

    @Test
    void testMoreLikeThisWithLikeText() throws Exception {
        // 创建MoreLikeThis查询
        MoreLikeThis mlt = Query.more_like_this();

        // 设置字段
        mlt.setFields(Arrays.asList("title", "content"));

        // 设置like_text
        mlt.setLikeText("这是一个示例文本，用于测试MoreLikeThis查询的like_text功能");

        // 设置其他参数
        mlt.setMinTermFreq(1)
           .setMinDocFreq(1)
           .setMinimumShouldMatch("30%");

        // 转换为JSON并验证
        String json = objectMapper.writeValueAsString(mlt.toArray());
        System.out.println("MoreLikeThis with like_text JSON: " + json);

        // 验证JSON结构
        assertNotNull(json);
        assertEquals(true, json.contains("\"more_like_this\""));
        assertEquals(true, json.contains("\"fields\":[\"title\",\"content\"]"));
        assertEquals(true, json.contains("\"like_text\":\"这是一个示例文本，用于测试MoreLikeThis查询的like_text功能\""));
        assertEquals(true, json.contains("\"min_term_freq\":1"));
        assertEquals(true, json.contains("\"min_doc_freq\":1"));
        assertEquals(true, json.contains("\"minimum_should_match\":\"30%\""));
    }

    @Test
    void testMoreLikeThisWithIds() throws Exception {
        // 创建MoreLikeThis查询
        MoreLikeThis mlt = Query.more_like_this();

        // 设置字段
        mlt.setFields(Arrays.asList("title", "content"));

        // 设置文档ID列表
        List<String> ids = Arrays.asList("doc1", "doc2", "doc3");
        mlt.setIds(ids);

        // 设置其他参数
        mlt.setMinTermFreq(1)
           .setMinDocFreq(1)
           .setMinimumShouldMatch("30%");

        // 转换为JSON并验证
        String json = objectMapper.writeValueAsString(mlt.toArray());
        System.out.println("MoreLikeThis with ids JSON: " + json);

        // 验证JSON结构
        assertNotNull(json);
        assertEquals(true, json.contains("\"more_like_this\""));
        assertEquals(true, json.contains("\"fields\":[\"title\",\"content\"]"));
        assertEquals(true, json.contains("\"ids\":[\"doc1\",\"doc2\",\"doc3\"]"));
        assertEquals(true, json.contains("\"min_term_freq\":1"));
        assertEquals(true, json.contains("\"min_doc_freq\":1"));
        assertEquals(true, json.contains("\"minimum_should_match\":\"30%\""));
    }
} 