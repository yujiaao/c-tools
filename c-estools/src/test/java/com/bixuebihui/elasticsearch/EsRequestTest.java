package com.bixuebihui.elasticsearch;

import com.bixuebihui.elasticsearch.query.Query;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EsRequestTest {
    public static String host= "http://localhost:9200";
    public static String username = "elastic";
    public static String password = "changeme";

    EsRequest esRequest= new EsRequest(host, username, password);


    @Test
    public  void testQuery() {

        esRequest.indexName = "test*";

        String s0 = esRequest.query(Query.match_all(), 0, 10, null, null, Collections.emptyList());
        System.out.println(s0);

        System.out.println("===========");


        String s = esRequest.query(Query
                .match(null, null).setFieldQuery("child_endpoint_name",
                        "{GET}/index/sysGenerator"), 0, 10, null, null, Collections.emptyList());
        System.out.println(s);

    }

    @Test
    public void testMatchAll(){
        esRequest.indexName = "test*";
        String s0 = esRequest.query(Query.match_all(), 0, 10, null, null, Collections.emptyList());
        System.out.println(s0);
    }

    @Test
    @Disabled("run once only")
    void getIndex() {
        String s0 = esRequest.createIndex("test");
        System.out.println(s0);
    }

    @Test
    void createDoc() throws JsonProcessingException {
        esRequest.indexName = "test";

        List<EsRequest.Document> list  = Lists.newArrayList();
        for(int i=0; i<10; i++) {
            EsRequest.Document doc = new EsRequest.Document(i+"", ImmutableMap.<String, Object>builder()
                    .put("my_id", "abc " + i)
                    .put("my_value", i * i)
                    .put("age", i +10)
                    .put("height", i +10+ new Random().nextInt(100))
                    .build());
            list.add(doc);
        }
        String s0 = esRequest.createDoc(esRequest.indexName, list);
        System.out.println(s0);
    }

    @Test
    public void testRange(){
        esRequest.indexName = "test";

        String s0 = esRequest.query(Query.range("age", ImmutableMap.<String, Object>builder()
                .put("from", 11)
                .put("to",18).build()), 0, 10, Collections.emptyList());
        System.out.println(s0);
    }


    @Test
    public void  esJsonToTableJson() throws IOException {
       String  json = "{ " +
                "     \"took\" : 17, " +
                "     \"timed_out\" : false, " +
                "     \"_shards\" : { " +
                "     \"total\" : 1, " +
                "     \"successful\" : 1, " +
                "     \"skipped\" : 0, " +
                "     \"failed\" : 0 " +
                "     }, " +
                "     \"hits\" : { " +
                "     \"total\" : { " +
                "     \"value\" : 2, " +
                "     \"relation\" : \"eq\" " +
                "     }, " +
                "     \"max_score\" : 1.0, " +
                "     \"hits\" : [ " +
                "     { " +
                "     \"_index\" : \"my-index-000001\", " +
                "     \"_type\" : \"_doc\", " +
                "     \"_id\" : \"1\", " +
                "     \"_score\" : 1.0, " +
                "     \"_source\" : { " +
                "     \"color\" : [ " +
                "     \"blue\", " +
                "     \"green\" " +
                "     ] " +
                "     } " +
                "     }, " +
                "     { " +
                "     \"_index\" : \"my-index-000001\", " +
                "     \"_type\" : \"_doc\", " +
                "     \"_id\" : \"2\", " +
                "     \"_score\" : 1.0, " +
                "     \"_source\" : { " +
                "     \"color\" : \"blue\" " +
                "     } " +
                "     } " +
                "     ] " +
                "     } " +
                "     }";

        /**
         *
         {
         "caption":"t_config",
         "titles": [
         "C _key",
         "c_name",
         "c_value"],
         "data": [{
         "c_key":"key1",
         "c_name":"name1",
         "c_value":"value1"}
         ],

         "paging":{"page":2,"maxRows":1,"rowEnd":2,"rowStart":1,"totalRows":3}}
         */

        Object result = new EsRequest("", "","").esJsonToTableJson(json, 10000, 0,50,1,2);


        System.out.println(result);
        assertEquals("{\n" +
                "  \"caption\" : \"my-index-000001\",\n" +
                "  \"titles\" : [ \"color\" ],\n" +
                "  \"_id\" : [ \"1\", \"2\" ],\n" +
                "  \"items\" : [ {\n" +
                "    \"color\" : [ \"blue\", \"green\" ]\n" +
                "  }, {\n" +
                "    \"color\" : \"blue\"\n" +
                "  } ],\n" +
                "  \"paging\" : {\n" +
                "    \"totalRows\" : {\n" +
                "      \"value\" : 2,\n" +
                "      \"relation\" : \"eq\"\n" +
                "    },\n" +
                "    \"maxRows\" : 10000,\n" +
                "    \"rowEnd\" : 50,\n" +
                "    \"rowStart\" : 0,\n" +
                "    \"page\" : 1\n" +
                "  },\n" +
                "  \"total\" : 2\n" +
                "}", result);

    }

    @Test
    public void queryAllIndex(){
        List<String> indexList = esRequest.getAllIndexList();
        System.out.println(StringUtils.join(indexList,","));
    }

    @Test
    public void queryData(){
        esRequest.indexName = "library*";
        String json = esRequest.query(Query.match_all(), 0, 2, null);
        System.out.println(json);
    }



    @ParameterizedTest
    @CsvSource({
            "/es_result_paging_es7.json,7.0",
            "/es_result_paging_es6.json,6.0"
    })
    void getTableJsonNode(String fileName, String version) throws IOException, URISyntaxException {
        String json = IOUtils.toString(this.getClass().getResource(fileName).toURI(), StandardCharsets.UTF_8);

        EsRequest esRequest1 = new EsRequest("", "", "");

        esRequest1.version =version;

        JsonNode res = esRequest1.getTableJsonNode(json, 10, 10, 10, 1, 100);

        if(version.startsWith("7")){
        assertEquals("{\"caption\":\"all-content-info\",\"titles\":[\"author_level\",\"pv\",\"author_img\",\"year_ids\",\"source\",\"community_status\",\"qa_author_img\",\"koubei_label_names\",\"publish_time\",\"price\",\"bbs_name\",\"brand_ids\",\"media_id\",\"verify_status\",\"pc_url\",\"height\",\"like_count\",\"label_ids\",\"qa_author_id\",\"bbs_image\",\"factory_ids\",\"global_id\",\"expire_time\",\"reply_count\",\"content_level\",\"city_ids\",\"attribute_ids\",\"m_url\"],\"_id\":[\"1998367890176217088\"]," +
                        "\"items\":[{\"author_level\":null,\"pv\":174439,\"author_img\":\"https://test.com/abc.jpg\",\"year_ids\":null,\"source\":2,\"community_status\":null,\"qa_author_img\":null,\"koubei_label_names\":null,\"publish_time\":\"2022/07/07 19:00:00\",\"price\":null,\"bbs_name\":null,\"brand_ids\":[331],\"media_id\":null,\"verify_status\":null,\"pc_url\":\"http://localhost/\",\"height\":null,\"like_count\":19,\"label_ids\":null,\"qa_author_id\":null,\"bbs_image\":null,\"factory_ids\":[4],\"global_id\":1998367890176217088,\"expire_time\":null,\"reply_count\":2095,\"content_level\":null,\"city_ids\":[],\"attribute_ids\":null,\"m_url\":\"http://m.url.com/\"}]," +
                        "\"paging\":{\"totalRows\":{\"value\":1,\"relation\":\"eq\"},\"maxRows\":10,\"rowEnd\":10,\"rowStart\":10,\"page\":1},"+
                        "\"total\":1}",
                res.toString());
        }else {
            assertEquals("{\"caption\":\"all-content-info\",\"titles\":[\"author_level\",\"pv\",\"author_img\",\"year_ids\",\"source\",\"community_status\",\"qa_author_img\",\"koubei_label_names\",\"publish_time\",\"price\",\"bbs_name\",\"brand_ids\",\"media_id\",\"verify_status\",\"pc_url\",\"height\",\"like_count\",\"label_ids\",\"qa_author_id\",\"bbs_image\",\"factory_ids\",\"global_id\",\"expire_time\",\"reply_count\",\"content_level\",\"city_ids\",\"attribute_ids\",\"m_url\"],\"_id\":[\"1998367890176217088\"]," +
                            "\"items\":[{\"author_level\":null,\"pv\":174439,\"author_img\":\"https://test.com/abc.jpg\",\"year_ids\":null,\"source\":2,\"community_status\":null,\"qa_author_img\":null,\"koubei_label_names\":null,\"publish_time\":\"2022/07/07 19:00:00\",\"price\":null,\"bbs_name\":null,\"brand_ids\":[331],\"media_id\":null,\"verify_status\":null,\"pc_url\":\"http://localhost/\",\"height\":null,\"like_count\":19,\"label_ids\":null,\"qa_author_id\":null,\"bbs_image\":null,\"factory_ids\":[4],\"global_id\":1998367890176217088,\"expire_time\":null,\"reply_count\":2095,\"content_level\":null,\"city_ids\":[],\"attribute_ids\":null,\"m_url\":\"http://m.url.com/\"}]," +
                            "\"paging\":{\"totalRows\":1,\"maxRows\":10,\"rowEnd\":10,\"rowStart\":10,\"page\":1}," +
                            "\"total\":1}",

                    res.toString());
        }
    }
}
