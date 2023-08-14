package com.bixuebihui.elasticsearch.aggregation;

import static org.junit.jupiter.api.Assertions.*;

class EsAggregationParserTest {
    /*
    测试环境：
10.60.12.23:8080
业务账号密码：
autotest
4R16vKlFMYZEltuZ4YaC

    GET cms-info-statistic/_search
{
  "size": 100,
  "query": {
    "bool": {
      "must": [
        {
          "terms": {
            "biz_type": [
              "1",
              "3",
              "700112"
            ]
          }
        },
        {
          "terms": {
            "dt": [
              "2023-08-03",
              "2023-08-02",
              "2023-08-01",
              "2023-07-31"
            ]
          }
        },
        {
          "range": {
            "sight_show_pv": {
              "gte": 1
            }
          }
        },
        {
          "range": {
            "ctr": {
              "from": "0",
              "to": "100"
            }
          }
        }
      ]
    }
  },
  "aggs": {
   "my-agg-name": {
      "terms": {
        "field": "biz_id"

      },
      "aggs": {
         "agg2": {
          "terms": {
            "field": "biz_type"
          }},
        "my-sub-agg-name": {
          "sum": {
            "field": "click_pv"
          }
        },
         "my-sub-agg-name2": {
          "sum": {
            "field": "sight_show_pv"
          }
        }
      }
   }
},
  "sort": {
    "ctr": "desc"
  }
}
     */
        @org.junit.jupiter.api.Test
        void parse() {
            String s = "{\n" +
                    "  \"size\": 100,\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": [\n" +
                    "        {\n" +
                    "          \"terms\": {\n" ;


        }

}
