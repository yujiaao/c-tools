package com.bixuebihui.jsongen;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author xwx
 */
public class EsMapping2JsonschemaUtils {

    static Logger logger = LoggerFactory.getLogger(EsMapping2JsonschemaUtils.class);

    /**
     * {
     * "mobile-video-info": {
     * "mappings": {
     * "mobile-video-info-type": {
     * "properties": {
     * "attribute_ids": {
     * "type": "long"
     * },
     * "attribute_names": {
     * "type": "keyword"
     * },
     * "author_id": {
     * "type": "long"
     * },
     * "author_name": {
     * "type": "keyword"
     * },
     * "brand_ids": {
     * "type": "long"
     * },
     * "content_label_ids": {
     * "type": "long"
     * },
     * "content_label_names": {
     * "type": "text",
     * "fields": {
     * "keyword": {
     * "type": "keyword",
     * "ignore_above": 256
     * }
     * }
     * },
     * "cover_image": {
     * "type": "text"
     * },
     * "cover_image_2_3": {
     * "type": "text"
     * },
     * "created_stime": {
     * "type": "keyword"
     * },
     * "duaration": {
     * "type": "long"
     * },
     * "expire_date": {
     * "type": "keyword"
     * },
     * "factory_ids": {
     * "type": "long"
     * },
     * "first_frame": {
     * "type": "text"
     * },
     * "is_del": {
     * "type": "long"
     * },
     * "is_privacy": {
     * "type": "integer"
     * },
     * "is_public": {
     * "type": "long"
     * },
     * "is_recommend": {
     * "type": "integer"
     * },
     * "label_ids": {
     * "type": "long"
     * },
     * "label_names": {
     * "type": "keyword"
     * },
     * "labeled": {
     * "type": "integer"
     * },
     * "make_score": {
     * "type": "double"
     * },
     * "media_id": {
     * "type": "text"
     * },
     * "off_video": {
     * "type": "integer"
     * },
     * "play_url": {
     * "type": "text"
     * },
     * "series_ids": {
     * "type": "long"
     * },
     * "style_label_ids": {
     * "type": "long"
     * },
     * "style_label_names": {
     * "type": "text",
     * "fields": {
     * "keyword": {
     * "type": "keyword",
     * "ignore_above": 256
     * }
     * }
     * },
     * "stytle_score": {
     * "type": "double"
     * },
     * "title": {
     * "type": "keyword"
     * },
     * "title_score": {
     * "type": "double"
     * },
     * "total_score": {
     * "type": "double"
     * },
     * "verify_status": {
     * "type": "integer"
     * },
     * "video_id": {
     * "type": "long"
     * },
     * "video_source": {
     * "type": "long"
     * }
     * }
     * }
     * }
     * }
     * }
     * <p>
     * trip 3 layers, and make ids, names to array type like:
     * <p>
     * {
     * "type" : "object",
     * "properties" : {
     * "myArrayProperty" : {
     * "type" : "array",
     * "items" : {
     * "type" : "string"
     * }
     * }
     * }
     * }
     *
     * @param src
     * @return JsonNode properties jsonschema
     */
    public static JsonNode trans(String src, Set<String> ignoreFields, Set<String> collectionFields) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return trans(mapper.readTree(src), ignoreFields, collectionFields);
    }

    public static JsonNode trans(JsonNode input, Set<String> ignoreFields, Set<String> collectionFields) throws IOException {

        JmesPath<JsonNode> jmespath = new JacksonRuntime();
        ObjectMapper mapper = new ObjectMapper();

        Expression<JsonNode> exp = jmespath.compile(
                //table/index name
                "{properties:*.mappings.*.properties}| {properties:properties[0]}| {properties:properties[0]}");
        Expression<JsonNode> exp1 = jmespath.compile(
                //table/index name
                "{properties:*.mappings.properties}| {properties:properties[0]}");
        // Finally this is how you search a structure. There's really not much more to it.
        JsonNode result = exp.search(input);
        JsonNode properties = result.path("properties");
        if(!properties.fields().hasNext()){
            result = exp1.search(input);
            properties = result.path("properties");
        }

        if(!properties.fields().hasNext()){
            logger.debug("It is not a valid ElasticSearch mapping format, by pass trans");
            return input;
        }


        for(String ignore : ignoreFields){
            if(properties.has(ignore)){
                ((ObjectNode) properties).remove(ignore);
            }
        }


        for (Iterator<Map.Entry<String, JsonNode>> iter = properties.fields(); iter.hasNext(); ) {
            Map.Entry<String, JsonNode> field = iter.next();

            String fieldName = field.getKey();
            String oldType = field.getValue().path("type").textValue();
            if (fieldName.endsWith("_ids") || fieldName.endsWith("_names") || collectionFields.contains(fieldName)) {
                ((ObjectNode) field.getValue()).put("type", "array");
                ((ObjectNode) field.getValue()).setAll((ObjectNode)
                        mapper.readTree("{\"items\" : { \"type\" : \"" + typeMapping(oldType) + "\"} }"));
            } else {
                ((ObjectNode) field.getValue()).put("type", typeMapping(oldType));
            }
        }
        return result;
    }

    public static String typeMapping(String src) {
        if ("keyword".equals(src) || "text".equals(src)) {
            return "string";
        }
        if ("long".equals(src)) {
            return "integer";
        }
        if ("double".equals(src)) {
            return "number";
        }
        return src;
    }
}
