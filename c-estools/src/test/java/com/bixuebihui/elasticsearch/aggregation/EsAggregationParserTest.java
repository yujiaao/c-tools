package com.bixuebihui.elasticsearch.aggregation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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

    @Test
    void jsonNodeToCountValueObj() throws IOException {
        String s = IOUtils.resourceToString("/node-addition.json", StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        CountValueObj res = EsAggregationParser.jsonNodeToCountValueObj(objectMapper.readTree(s), objectMapper);
        assertEquals(28, res.doc_count);
        assertEquals(6, res.getAdditionalProperties().size());
        assertEquals(15772.0,
                ((Map)res.getAdditionalProperties().get("sum_sight_show_pv")).get("value"));

    }


    @Test
    void parseCountValue() throws IOException {
        String s = IOUtils.resourceToString("/agg-query-result-sum.json", StandardCharsets.UTF_8);
        List<CountValueObj> res = EsAggregationParser.parseCountValue(s, "group_by_biztype_id_object_id", null, new ObjectMapper());

        assertEquals(6, res.size());
        assertEquals(28, res.get(0).doc_count);
        assertEquals(6, res.get(0).getAdditionalProperties().size());
        assertEquals(15772.0,
                ((Map)res.get(0).getAdditionalProperties().get("sum_sight_show_pv")).get("value"));


    }
}
