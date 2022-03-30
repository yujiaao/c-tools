package com.bixuebihui.elasticsearch;


import com.bixuebihui.elasticsearch.query.DSL;

import java.util.Arrays;
import java.util.List;

/**
 * @author xwx
 */
public class EsVersion {


    protected List<String> queries = Arrays.asList(
            "match",
            "multi_match",
            "bool",
            "boosting",
            "common_terms",
            "constant_score",
            "dis_max",
            "function_score",
            "fuzzy",
            "geo_shape",
            "has_child",
            "has_parent",
            "ids",
            "match_all",
            "more_like_this",
            "nested",
            "parent_id",
            "prefix",
            "query_string",
            "simple_query_string",
            "range",
            "regexp",
            "span_first",
            "span_multi_term",
            "span_near",
            "span_not",
            "span_or",
            "span_term",
            "term",
            "terms",
            "wildcard",
            "geo_distance",
            "exists",
            "type",
            "percolate"
    );

    protected List<String> aggregations = Arrays.asList(
            "min",
            "max",
            "sum",
            "sum_bucket",
            "avg",
            "avg_bucket",
            "stats",
            "extended_stats",
            "value_count",
            "percentiles",
            "percentile_ranks",
            "cardinality",
            "geo_bounds",
            "top_hits",
            "scripted_metric",

            // original: global
            "global_agg",
            "filter",
            "filters",
            "missing",
            "nested",
            "reverse_nested",
            "children",
            "terms",
            "significant_terms",
            "range",
            "date_range",
            "ipv4_range",
            "histogram",
            "date_histogram",
            "geo_distance",
            "geohash_grid",
            "bucket_script",
            "serial_diff"
    );

    protected List<String> suggesters = Arrays.asList(
            "term",
            "phrase",
            "completion",
            "context"
    );


    /**
     * returns true if name is supported, false otherwise.
     *
     * @param name
     * @param type
     * @return bool
     */
    public boolean supports(String name, DSL type) {
        switch (type) {
            case TYPE_QUERY:
                return this.queries.contains(name);
            case TYPE_AGGREGATION:
                return this.aggregations.contains(name);
            case TYPE_SUGGEST:
                return this.suggesters.contains(name);
            default:
                throw new IllegalArgumentException("unknow type:"+type);
        }

        // disables version check in Facade for custom DSL objects
       // return true;
    }

    /**
     * @return string[]
     */
    public List<String> getAggregations() {
        return this.aggregations;
    }

    /**
     * @return string[]
     */
    public List<String> getQueries() {
        return this.queries;
    }

    /**
     * @return string[]
     */
    public List<String> getSuggesters() {
        return this.suggesters;
    }
}
