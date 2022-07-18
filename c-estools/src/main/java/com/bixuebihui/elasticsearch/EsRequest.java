package com.bixuebihui.elasticsearch;

import com.bixuebihui.elasticsearch.query.Query;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * common operation with elasticsearch use http request at 'low' level
 *
 * @author xwx
 */
public class EsRequest {
    static final Logger logger = LoggerFactory.getLogger(EsRequest.class);
    // This you have to fill in yourself, you're probably using Jackson's ObjectMapper
    // to load JSON data, and that should fit right in here.
    static ObjectMapper mapper = new ObjectMapper();
    /**
     * // The first thing you need is a runtime. These objects can compile expressions
     * // and they are specific to the kind of structure you want to search in.
     * // For most purposes you want the Jackson runtime, it can search in JsonNode
     * // structures created by Jackson.
     */
    static JmesPath<JsonNode> jmespath = new JacksonRuntime();
    /**
     * // Expressions need to be compiled before you can search. Compiled expressions
     * // are reusable and thread safe. Compile your expressions once, just like database
     * // prepared statements.
     */
    static Expression<JsonNode> exp = jmespath.compile(
            //table/index name
            "{caption:hits.hits[0]._index, " +
                    // field names
                    "titles:keys(hits.hits[0]._source)," +
                    // doc id
                    "_id: hits.hits[*]._id," +
                    // rows
                    "data:hits.hits[*]._source," +
                    // paging
                    "paging:{totalRows:hits.total.value}}");
    private final String username;
    private final String password;
    /**
     * index name to search
     * 需要检索的索引
     */
    protected String indexName;
    String host;
    Pattern pattern = Pattern.compile("open\\s+[\\.\\w|\\-|\\:+|\\w|\\-|\\:+]+");
    Pattern p = Pattern.compile("\\s+[\\.\\w\\-|\\:+|\\w\\-|\\:+]+");

    String version;


    public EsRequest(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }
    public EsRequest(String host, String username, String password, String indexName) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.indexName = indexName;
    }
    public EsRequest(String host, String username, String password, String indexName, String version) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.indexName = indexName;
        this.version = version;
    }

    public static <T> List<EsSearchHitResult<T>> json2result(String rawResult, JavaType type) throws JsonProcessingException {
        return mapper.readerFor(type).at("/hits/hits").readValue(rawResult);
    }

    /**
     * the elasticsearch  response json:
     * {
     * "took" : 17,
     * "timed_out" : false,
     * "_shards" : {
     * "total" : 1,
     * "successful" : 1,
     * "skipped" : 0,
     * "failed" : 0
     * },
     * "hits" : {
     * "total" : {
     * "value" : 2,
     * "relation" : "eq"
     * },
     * "max_score" : 1.0,
     * "hits" : [
     * {
     * "_index" : "my-index-000001",
     * "_type" : "_doc",
     * "_id" : "1",
     * "_score" : 1.0,
     * "_source" : {
     * "color" : [
     * "blue",
     * "green"
     * ]
     * }
     * },
     * {
     * "_index" : "my-index-000001",
     * "_type" : "_doc",
     * "_id" : "2",
     * "_score" : 1.0,
     * "_source" : {
     * "color" : "blue"
     * }
     * }
     * ]
     * }
     * }
     * <p>
     * and translate to my table query format:
     * ==>
     * <p>
     * {
     * "caption":"t_config",
     * "titles": [
     * "C _key",
     * "c_name",
     * "c_value"],
     * "data": [{
     * "c_key":"key1",
     * "c_name":"name1",
     * "c_value":"value1"}
     * ],
     * <p>
     * "paging":{"page":2,"maxRows":1,"rowEnd":2,"rowStart":1,"totalRows":3}}
     *
     * @param esJson
     * @return
     */
    public static String esJsonToTableJson(String esJson,
                                           int maxRows,
                                           int rowStart,
                                           int rowEnd,
                                           int page,
                                           int totalRows) throws JsonProcessingException {
        JsonNode resultTotal = getTableJsonNode(esJson,
                maxRows,
                rowEnd,
                rowStart,
                page,
                totalRows);
        if(resultTotal==null){
            return "{}";
        }
        return resultTotal.toPrettyString();
    }


    public static long getHits(  String esJson) {
        JsonNode input;
        try {
            input = mapper.readTree(esJson);
            if(input==null || (input.at("/hits/total") instanceof MissingNode)){
                return 0;
            }
            return input.at("/hits/total").asLong();
        } catch (JsonProcessingException e) {
           logger.error("查询结果json格式错误", e);
        }
        return 0;
    }

    public static JsonNode getTableJsonNode(String esJson,
                                            int maxRows,
                                            int rowEnd,
                                            int rowStart,
                                            int page,
                                            int totalRows
    ) throws JsonProcessingException {
        JsonNode input = mapper.readTree(esJson);
        // https://datatracker.ietf.org/doc/html/rfc6901
        if(input==null || (input.at("/hits/hits/0/_source") instanceof MissingNode)){
            return null;
        }
        // Finally this is how you search a structure. There's really not much more to it.
        JsonNode resultTotal = exp.search(input);
        JsonNode paging = resultTotal.path("paging");
        if (maxRows != 0) {
            ((ObjectNode) paging).put("maxRows", maxRows);
            ((ObjectNode) paging).put("rowEnd", rowEnd);
            ((ObjectNode) paging).put("rowStart", rowStart);
            ((ObjectNode) paging).put("page", page);
        }
        return resultTotal;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getHost() {
        return host;
    }

    public String createDoc(String indexName, List<Document> docs) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        HttpEntityEnclosingRequestBase post = new HttpPost(host + "/" + EsQueryBuilder.ACTION_BULK);
        StringBuilder sb = new StringBuilder();

        for (Document doc : docs) {
            Map<String, Object> map1 = Maps.newHashMap();
            Map<String, Object> map2 = Maps.newHashMap();
            map2.put("_index", indexName);
            map2.put("_id", doc.id);
            map2.put("_type", "_doc");
            map1.put("index", map2);
            sb.append(objectMapper.writeValueAsString(map1)).append("\n");

            sb.append(objectMapper.writeValueAsString(doc.values)).append("\n");
        }

        return httpJson(post, sb.toString());
    }

    public String createIndex(String indexName) {

        HttpEntityEnclosingRequestBase post = new HttpPut(host + "/" + indexName);

        return httpJson(post,"");
    }

    /**
     * http request
     *
     * @param requestBase
     * @return
     */
    private String httpJson(HttpEntityEnclosingRequestBase requestBase, String body) {
        logger.debug("====>>>>");
        logger.debug(requestBase.toString()+"\n"+body);

        if(StringUtils.isNotBlank(body) && requestBase.getEntity()==null){
            requestBase.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        }


        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            if (this.username != null && password != null) {
                Header header = new BasicHeader("Authorization", "Basic "
                        + Base64.getUrlEncoder().encodeToString((this.username + ":" + this.password).getBytes(StandardCharsets.UTF_8)));
                requestBase.addHeader(header);
            }

            HttpResponse response = httpClient.execute(requestBase);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode() +
                        " with message:" + getResponseString(response));
            }

            String res = getResponseString(response);
            logger.debug("<<<<====");
            logger.debug(StringUtils.abbreviate(res,1000));
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EsQueryBuilder.EMPTY_JSON_STRING;
    }

    public String query(Query query, int from, int size) {
        return query(query, from, size, null);
    }

    public String query(Query query, int from, int size, LinkedHashMap<String, String> sort) {
        try {
            String body = EsQueryBuilder.build(query, from, size, Collections.emptySet(), Collections.emptySet(), sort);

            HttpWithJsonEntity getRequest =
                    HttpWithJsonEntity.builder()
                            .withUrl(host + "/" + indexName + "/" + EsQueryBuilder.ACTION_SEARCH)
                            .withBodyEntry(body)
                            .build();

            return httpJson(getRequest,body);
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            e.printStackTrace();
        }
        return EsQueryBuilder.EMPTY_JSON_STRING;
    }

    public String search(String rawJson) {
        return query(rawJson, EsQueryBuilder.ACTION_SEARCH);
    }

    public int count(String rawJson) {
        String res = query(rawJson, EsQueryBuilder.ACTION_COUNT);

        try {
            Map<String, Object> map = mapper.readValue(res, new TypeReference<>() {
            });
            return map.containsKey(EsQueryBuilder.COUNT) ? Integer.parseInt(map.get(EsQueryBuilder.COUNT).toString()) : 0;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return 0;
        }

    }

    public String query(String rawJson, String action) {
        try {
            HttpWithJsonEntity getRequest =
                    HttpWithJsonEntity.builder()
                            .withUrl(host + "/" + indexName + "/" + action)
                            .withBodyEntry(rawJson)
                            .build();

            return httpJson(getRequest, rawJson);
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            e.printStackTrace();
        }
        return EsQueryBuilder.EMPTY_JSON_STRING;
    }

    public String post(String rawJson, String action, String _id) {
        try {
            HttpWithJsonEntity getRequest =
                    HttpWithJsonEntity.builder()
                            .withUrl(host + "/" + indexName + "/" + action + "/" + _id)
                            .withBodyEntry(rawJson)
                            .withMethod("POST")
                            .build();

            return httpJson(getRequest, rawJson);
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            e.printStackTrace();
        }
        return EsQueryBuilder.EMPTY_JSON_STRING;
    }

    /**
     * POST /all-content-info/_update_by_query
     * {
     *   "query":{
     *     "bool":{
     *       "must":[
     *     {"terms":{"global_id":[1961991758749401088,1985042401953087488,1987523321226362880]}}
     *     ]
     *     }
     *   },
     *
     *   "script": {
     *     "source": "ctx._source['carseries_status'] = 1"
     *   }
     * }
     * @return
     */
    public String post(String rawJson, String action) {
        try {
            HttpWithJsonEntity getRequest =
                    HttpWithJsonEntity.builder()
                            .withUrl(host + "/" + indexName + "/" + action)
                            .withBodyEntry(rawJson)
                            .withMethod("POST")
                            .build();

            return httpJson(getRequest, rawJson);
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            e.printStackTrace();
        }
        return EsQueryBuilder.EMPTY_JSON_STRING;
    }

    public List<String> getAllIndexList() {
        List<String> indexList = new ArrayList<>();
        try {
            HttpWithJsonEntity getRequest =
                    HttpWithJsonEntity.builder()
                            .withUrl(host + "/" + EsQueryBuilder.ACTION_CAT + "/" + EsQueryBuilder.ACTION_INDICES)
                            .withBodyEntry("")
                            .build();
            String res = httpJson(getRequest, "");
            Matcher matcher = pattern.matcher(res);
            while (matcher.find()) {
                String matchStr = matcher.group();
                Matcher m = p.matcher(matchStr);
                m.find();
                indexList.add(m.group().trim());
            }
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            e.printStackTrace();
        }
        return indexList;
    }

    private String getResponseString(HttpResponse response) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));

        String output;
        StringBuilder sb = new StringBuilder();
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        return sb.toString();
    }

    public int update(String _id, Map<String, Object> map) throws JsonProcessingException {
        String res;
        if(version!=null && version.startsWith("6")){
            //TODO routing problem
           res = post(EsQueryBuilder.buildUpdate(map),"_doc" , _id+"/"+EsQueryBuilder.ACTION_UPDATE);
        }else {
            res = post(EsQueryBuilder.buildUpdate(map), EsQueryBuilder.ACTION_UPDATE, _id);
        }
        /**
         * {
         *   "_index" : "my-index-000001",
         *   "_type" : "_doc",
         *   "_id" : "1",
         *   "_version" : 2,
         *   "result" : "updated",
         *   "_shards" : {
         *     "total" : 2,
         *     "successful" : 1,
         *     "failed" : 0
         *   },
         *   "_seq_no" : 1,
         *   "_primary_term" : 4
         * }
         */
        JsonNode result = mapper.readTree(res);
        return result.path("_shards").path("successful").asInt();
    }

    public int updateByQuery(Query query, Map<String,Object> keyValues, boolean conflictsProceed) throws JsonProcessingException {
        String q = EsQueryBuilder.buildUpdateByQuery(query, keyValues);

        String res = post(q, EsQueryBuilder.ACTION_UPDATE_BY_QUERY + (conflictsProceed? "?conflicts=proceed":""));

        /**
         {
         "took": 44,
         "timed_out": false,
         "total": 3,
         "updated": 3,
         "deleted": 0,
         "batches": 1,
         "version_conflicts": 0,
         "noops": 0,
         "retries": {
         "bulk": 0,
         "search": 0
         },
         "throttled_millis": 0,
         "requests_per_second": -1,
         "throttled_until_millis": 0,
         "failures": []
         }
         */
        JsonNode result = mapper.readTree(res);
        return result.path("updated").asInt();
    }

    static class Document {
        String id;
        Map<String, Object> values;

        public Document(String id, Map<String, Object> values) {
            this.id = id;
            this.values = values;
        }
    }


}
