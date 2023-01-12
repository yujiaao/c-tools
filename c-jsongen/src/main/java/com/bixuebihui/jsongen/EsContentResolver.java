package com.bixuebihui.jsongen;

import com.bixuebihui.elasticsearch.EsRequest;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsonschema2pojo.ContentResolver;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author xwx
 */
public class EsContentResolver extends ContentResolver {
    private static final Set<String> CLASSPATH_SCHEMES = new HashSet<>(Arrays.asList("classpath", "resource", "java"));
    private final ObjectMapper objectMapper;


    Map<String, String> additionalProperties;

    public EsContentResolver(JsonFactory jsonFactory, Map<String, String> additionalProperties) {
        super(null);
        this.additionalProperties = additionalProperties;
        this.objectMapper = (new ObjectMapper(jsonFactory)).enable(new JsonParser.Feature[]{JsonParser.Feature.ALLOW_COMMENTS}).enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

    }


    public JsonNode resolve(URI uri) {
        if(additionalProperties==null || additionalProperties.isEmpty()) {
            return super.resolve(uri);
        } else {

            String res = EsMapping2JsonschemaUtils.readFromUrl(uri.toString(),
                    additionalProperties.get(GenEs.USERNAME), additionalProperties.get(GenEs.PASSWORD));

            try {
                return objectMapper.readTree(res);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }

    }


}
