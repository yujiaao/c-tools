package com.bixuebihui.elasticsearch.aggregation;

import java.util.List;

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
 * "buckets": [
 * {
 * "key": 1,
 * "doc_count": 27868,
 * "content_type-count": {
 * "doc_count_error_upper_bound": 0,
 * "sum_other_doc_count": 0,
 * "buckets": [
 * {
 * "key": 1,
 * "doc_count": 18732
 * },
 * {
 * "key": 3,
 * "doc_count": 5479
 * },
 * {
 * "key": 2,
 * "doc_count": 3657
 * }
 * ]
 * }
 * },
 * {
 * "key": 4,
 * "doc_count": 3443,
 * "content_type-count": {
 * "doc_count_error_upper_bound": 0,
 * "sum_other_doc_count": 0,
 * "buckets": [
 * {
 * "key": 4,
 * "doc_count": 3443
 * }
 * ]
 * }
 * },
 * {
 * "key": 7,
 * "doc_count": 814,
 * "content_type-count": {
 * "doc_count_error_upper_bound": 0,
 * "sum_other_doc_count": 0,
 * "buckets": [
 * {
 * "key": 3,
 * "doc_count": 814
 * }
 * ]
 * }
 * },
 * {
 * "key": 3,
 * "doc_count": 767,
 * "content_type-count": {
 * "doc_count_error_upper_bound": 0,
 * "sum_other_doc_count": 0,
 * "buckets": [
 * {
 * "key": 4,
 * "doc_count": 695
 * },
 * {
 * "key": 3,
 * "doc_count": 72
 * }
 * ]
 * }
 * },
 * {
 * "key": 2,
 * "doc_count": 491,
 * "content_type-count": {
 * "doc_count_error_upper_bound": 0,
 * "sum_other_doc_count": 0,
 * "buckets": [
 * {
 * "key": 1,
 * "doc_count": 491
 * }
 * ]
 * }
 * },
 * {
 * "key": 5,
 * "doc_count": 45,
 * "content_type-count": {
 * "doc_count_error_upper_bound": 0,
 * "sum_other_doc_count": 0,
 * "buckets": [
 * {
 * "key": 3,
 * "doc_count": 45
 * }
 * ]
 * }
 * }
 * ]
 * }
 * }
 * }
 */

public class CountObj {
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getDoc_count() {
        return doc_count;
    }

    public void setDoc_count(int doc_count) {
        this.doc_count = doc_count;
    }

    public List<CountObj> getItems() {
        return items;
    }

    public void setItems(List<CountObj> items) {
        this.items = items;
    }

    String key;
    int doc_count;
    List<CountObj> items;
}
