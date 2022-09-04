package com.bixuebihui.elasticsearch.aggregation;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html
 *
 * @author xwx
 */
public class EsAggregationParser {

    /**
     * request:
     *
     * GET /my-index-00001/_search
     * {
     *   "size": 0,
     *
     * "aggs": {
     *     "source-count": {
     *       "terms": {
     *         "field": "source"
     *       },
     *       "aggs": {
     *         "content_type-count": {
     *           "terms": {
     *             "field": "content_type"
     *           }
     *         }
     *       }
     *     }
     *   }
     * }
     *
     * response:
     *
     * {
     *   "took": 6,
     *   "timed_out": false,
     *   "_shards": {
     *     "total": 5,
     *     "successful": 5,
     *     "skipped": 0,
     *     "failed": 0
     *   },
     *   "hits": {
     *     "total": 33428,
     *     "max_score": 0,
     *     "hits": []
     *   },
     *   "aggregations": {
     *     "source-count": {
     *       "doc_count_error_upper_bound": 0,
     *       "sum_other_doc_count": 0,
     *       "buckets": [
     *         {
     *           "key": 1,
     *           "doc_count": 27868,
     *           "content_type-count": {
     *             "doc_count_error_upper_bound": 0,
     *             "sum_other_doc_count": 0,
     *             "buckets": [
     *               {
     *                 "key": 1,
     *                 "doc_count": 18732
     *               },
     *               {
     *                 "key": 3,
     *                 "doc_count": 5479
     *               },
     *               {
     *                 "key": 2,
     *                 "doc_count": 3657
     *               }
     *             ]
     *           }
     *         },
     *         {
     *           "key": 4,
     *           "doc_count": 3443,
     *           "content_type-count": {
     *             "doc_count_error_upper_bound": 0,
     *             "sum_other_doc_count": 0,
     *             "buckets": [
     *               {
     *                 "key": 4,
     *                 "doc_count": 3443
     *               }
     *             ]
     *           }
     *         },
     *         {
     *           "key": 7,
     *           "doc_count": 814,
     *           "content_type-count": {
     *             "doc_count_error_upper_bound": 0,
     *             "sum_other_doc_count": 0,
     *             "buckets": [
     *               {
     *                 "key": 3,
     *                 "doc_count": 814
     *               }
     *             ]
     *           }
     *         },
     *         {
     *           "key": 3,
     *           "doc_count": 767,
     *           "content_type-count": {
     *             "doc_count_error_upper_bound": 0,
     *             "sum_other_doc_count": 0,
     *             "buckets": [
     *               {
     *                 "key": 4,
     *                 "doc_count": 695
     *               },
     *               {
     *                 "key": 3,
     *                 "doc_count": 72
     *               }
     *             ]
     *           }
     *         },
     *         {
     *           "key": 2,
     *           "doc_count": 491,
     *           "content_type-count": {
     *             "doc_count_error_upper_bound": 0,
     *             "sum_other_doc_count": 0,
     *             "buckets": [
     *               {
     *                 "key": 1,
     *                 "doc_count": 491
     *               }
     *             ]
     *           }
     *         },
     *         {
     *           "key": 5,
     *           "doc_count": 45,
     *           "content_type-count": {
     *             "doc_count_error_upper_bound": 0,
     *             "sum_other_doc_count": 0,
     *             "buckets": [
     *               {
     *                 "key": 3,
     *                 "doc_count": 45
     *               }
     *             ]
     *           }
     *         }
     *       ]
     *     }
     *   }
     * }
     */
}
