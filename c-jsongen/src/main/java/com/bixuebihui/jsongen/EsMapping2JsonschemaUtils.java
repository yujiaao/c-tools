package com.bixuebihui.jsongen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
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

    static final Map<String, String> typeMap = Map.of(
            "keyword", "string",
            "text", "string",
            "date", "string",
            "long", "integer",
            "double", "number",
            "float", "number"
    );
    static Logger logger = LoggerFactory.getLogger(EsMapping2JsonschemaUtils.class);

    /**
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
        if (!properties.fields().hasNext()) {
            result = exp1.search(input);
            properties = result.path("properties");
        }

        if (!properties.fields().hasNext()) {
            logger.debug("It is not a valid ElasticSearch mapping format, by pass trans");
            return input;
        }


        for (String ignore : ignoreFields) {
            if (properties.has(ignore)) {
                ((ObjectNode) properties).remove(ignore);
            }
        }


        transFields(collectionFields, mapper, properties);
        return result;
    }

    /**
     * es fields mapping trans to jsonschema2pojo format
     *
     * @param collectionFields fields to generate List/Set
     * @param mapper           mapper object
     * @param properties       schema properties JsonNode
     * @throws JsonProcessingException
     * @link https://github.com/joelittlejohn/jsonschema2pojo/wiki/Reference#format
     */
    private static void transFields(Set<String> collectionFields, ObjectMapper mapper, JsonNode properties) throws JsonProcessingException {
        for (Iterator<Map.Entry<String, JsonNode>> iter = properties.fields(); iter.hasNext(); ) {
            Map.Entry<String, JsonNode> field = iter.next();

            String fieldName = field.getKey();
            String oldType = field.getValue().path("type").textValue();
            JsonNode nested = field.getValue().path("properties");
            if (!(nested instanceof MissingNode)) {
                transFields(collectionFields, mapper, nested);
                ((ObjectNode) field.getValue()).remove("properties");
            }

            if (fieldName.endsWith("_ids") || fieldName.endsWith("_names") || collectionFields.contains(fieldName)) {

                ((ObjectNode) field.getValue()).put("type", "array");
                if ((oldType == null || "nested".equals(oldType)) && !(nested instanceof MissingNode)) {
                    ((ObjectNode) field.getValue()).setAll((ObjectNode)
                            mapper.readTree("{\"items\" : { \"type\": \"object\", \"properties\": " + nested.toPrettyString() + "} }"));
                } else {
                    ((ObjectNode) field.getValue()).setAll((ObjectNode)
                            mapper.readTree("{\"items\" : { \"type\" : \"" + typeMapping(oldType) + "\"} }"));
                }
            } else {
                if (oldType == null && !(nested instanceof MissingNode)) {
                    ((ObjectNode) field.getValue()).setAll((ObjectNode)
                            mapper.readTree("{\"items\" : " + nested.toPrettyString() + " }"));
                } else {
                    ((ObjectNode) field.getValue()).put("type", typeMapping(oldType));
                }
                if ("date".equals(oldType)) {
                    JsonNode formatField = field.getValue().path("format");

                    if (formatField != null && !(formatField instanceof MissingNode)) {
                        String format = formatField.textValue();
                        if (format.contains("||")) {
                            format = format.substring(0, format.indexOf("||"));
                        }
                        ((ObjectNode) field.getValue()).put("customDateTimePattern", format);
                        ((ObjectNode) field.getValue()).put("format", "date-time");

                    } else {
                        ((ObjectNode) field.getValue()).put("format", "date-time");
                    }

                }
            }
        }
    }

    public static String typeMapping(String src) {
        return typeMap.getOrDefault(src, src);

    }
}
