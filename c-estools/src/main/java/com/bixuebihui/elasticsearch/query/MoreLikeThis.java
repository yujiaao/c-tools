package com.bixuebihui.elasticsearch.query;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MoreLikeThis extends Query {

    /**
     * 构造函数
     */
    public MoreLikeThis() {
        super();
    }

    /**
     * 设置要查找相似文档的字段
     * 
     * @param fields 字段列表
     * @return this
     */
    public MoreLikeThis setFields(List<String> fields) {
        return (MoreLikeThis) this.setParam("fields", fields);
    }

    /**
     * 设置要查找相似文档的文本
     * 
     * @param likeText 文本内容
     * @return this
     */
    public MoreLikeThis setLikeText(String likeText) {
        return (MoreLikeThis) this.setParam("like_text", likeText);
    }

    /**
     * 设置最小词频
     * 
     * @param minTermFreq 最小词频
     * @return this
     */
    public MoreLikeThis setMinTermFreq(int minTermFreq) {
        return (MoreLikeThis) this.setParam("min_term_freq", minTermFreq);
    }

    /**
     * 设置最小文档频率
     * 
     * @param minDocFreq 最小文档频率
     * @return this
     */
    public MoreLikeThis setMinDocFreq(int minDocFreq) {
        return (MoreLikeThis) this.setParam("min_doc_freq", minDocFreq);
    }

    /**
     * 设置最大文档频率
     * 
     * @param maxDocFreq 最大文档频率
     * @return this
     */
    public MoreLikeThis setMaxDocFreq(int maxDocFreq) {
        return (MoreLikeThis) this.setParam("max_doc_freq", maxDocFreq);
    }

    /**
     * 设置最小词长度
     * 
     * @param minWordLength 最小词长度
     * @return this
     */
    public MoreLikeThis setMinWordLength(int minWordLength) {
        return (MoreLikeThis) this.setParam("min_word_length", minWordLength);
    }

    /**
     * 设置最大词长度
     * 
     * @param maxWordLength 最大词长度
     * @return this
     */
    public MoreLikeThis setMaxWordLength(int maxWordLength) {
        return (MoreLikeThis) this.setParam("max_word_length", maxWordLength);
    }

    /**
     * 设置最小应该匹配
     * 
     * @param minimumShouldMatch 最小应该匹配
     * @return this
     */
    public MoreLikeThis setMinimumShouldMatch(String minimumShouldMatch) {
        return (MoreLikeThis) this.setParam("minimum_should_match", minimumShouldMatch);
    }

    /**
     * 设置提升值
     * 
     * @param boost 提升值
     * @return this
     */
    public MoreLikeThis setBoost(float boost) {
        return (MoreLikeThis) this.setParam("boost", boost);
    }

    /**
     * 设置文档ID列表
     * 
     * @param ids 文档ID列表
     * @return this
     */
    public MoreLikeThis setIds(List<String> ids) {
        return (MoreLikeThis) this.setParam("ids", ids);
    }

    /**
     * 设置文档
     * 
     * @param docs 文档列表
     * @return this
     */
    public MoreLikeThis setDocs(List<Map<String, Object>> docs) {
        return (MoreLikeThis) this.setParam("docs", docs);
    }

    /**
     * 设置文档
     * 
     * @param doc 文档
     * @return this
     */
    public MoreLikeThis setDoc(Map<String, Object> doc) {
        return (MoreLikeThis) this.setParam("doc", doc);
    }

    /**
     * 设置文档字段
     * 
     * @param field 字段名
     * @param text 文本内容
     * @return this
     */
    public MoreLikeThis setDocField(String field, String text) {
        Map<String, Object> doc = new HashMap<>();
        doc.put(field, text);
        return this.setDoc(doc);
    }

    /**
     * 设置文档字段
     * 
     * @param fieldTexts 字段文本映射
     * @return this
     */
    public MoreLikeThis setDocFields(Map<String, String> fieldTexts) {
        return (MoreLikeThis) this.setParam("doc", fieldTexts);
    }

    /**
     * 设置包含字段
     * 
     * @param include 包含字段
     * @return this
     */
    public MoreLikeThis setInclude(boolean include) {
        return (MoreLikeThis) this.setParam("include", include);
    }

    /**
     * 设置分析器
     * 
     * @param analyzer 分析器
     * @return this
     */
    public MoreLikeThis setAnalyzer(String analyzer) {
        return (MoreLikeThis) this.setParam("analyzer", analyzer);
    }

    /**
     * 设置忽略TF
     * 
     * @param ignoreTf 忽略TF
     * @return this
     */
    public MoreLikeThis setIgnoreTf(boolean ignoreTf) {
        return (MoreLikeThis) this.setParam("ignore_tf", ignoreTf);
    }

    /**
     * 设置失败查询
     * 
     * @param failOnUnsupportedField 失败查询
     * @return this
     */
    public MoreLikeThis setFailOnUnsupportedField(boolean failOnUnsupportedField) {
        return (MoreLikeThis) this.setParam("fail_on_unsupported_field", failOnUnsupportedField);
    }

    /**
     * 设置查询名称
     * 
     * @param queryName 查询名称
     * @return this
     */
    public MoreLikeThis setQueryName(String queryName) {
        return (MoreLikeThis) this.setParam("_name", queryName);
    }

    /**
     * 设置最大查询词数
     * 
     * @param maxQueryTerms 最大查询词数
     * @return this
     */
    public MoreLikeThis setMaxQueryTerms(int maxQueryTerms) {
        return (MoreLikeThis) this.setParam("max_query_terms", maxQueryTerms);
    }

    public MoreLikeThis setBoostTerms(double boostTerms) {
        return (MoreLikeThis) this.setParam("boost_terms", boostTerms);
    }

}
