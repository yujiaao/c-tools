package com.bixuebihui.elasticsearch.aggregation;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * request:
 * <p>
 * GET /my-index-00001/_search
 * {
 * "size": 0,
 * <p>
 * "aggs": {
 * "source-count": {
 * "terms": {
 * "field": "source"
 * },
 * "aggs": {
 * "content_type-count": {
 * "terms": {
 * "field": "content_type"
 * }
 * }
 * }
 * }
 * }
 * }
 * <p>
 * response:
 * <p>
 * {
 * "took": 6,
 * "timed_out": false,
 * "_shards": {
 * "total": 5,
 * "successful": 5,
 * "skipped": 0,
 * "failed": 0
 * },
 * "hits": {
 * "total": 33428,
 * "max_score": 0,
 * "hits": []
 * },
 * "aggregations": {
 * "source-count": {
 * "doc_count_error_upper_bound": 0,
 * "sum_other_doc_count": 0,
 "buckets": [
 {
 "key": "13_13063043",
 "doc_count": 3,
 "sum_sight_show_pv": {
 "value": 275.0
 },
 "sum_click_pv": {
 "value": 41.0
 }
 }
 ]
 * }
 * }
 * }
 */

public class CountValueObj {
    String key;
    int doc_count;
    List<CountValueObj> items;
    @JsonIgnore
    final private Map<String, Object> additionalProperties = new HashMap<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getDoc_count() {
        return doc_count;
    }

    @Override
    public String toString() {
        return "{" +
                "key='" + key + '\'' +
                ", doc_count=" + doc_count +
                ", items=" + items +
                ", additionalProperties=" + additionalProperties +
                '}';
    }

    public void setDoc_count(int doc_count) {
        this.doc_count = doc_count;
    }

    public List<CountValueObj> getItems() {
        return items;
    }

    public void setItems(List<CountValueObj> items) {
        this.items = items;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Double getValue(String field){
        return (Double) ((Map)additionalProperties.get(field)).get("value");
    }
}
