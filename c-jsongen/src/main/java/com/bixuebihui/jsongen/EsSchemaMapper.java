package com.bixuebihui.jsongen;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SourceType;
import org.jsonschema2pojo.rules.RuleFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Set;

/**
 * @author xwx
 */
public class EsSchemaMapper {
    private static final JsonNodeFactory NODE_FACTORY = JsonNodeFactory.instance;

    private final RuleFactory ruleFactory;
    private final SchemaGenerator schemaGenerator;


    /**
     * Create a schema mapper with the given {@link RuleFactory}.
     *
     * @param ruleFactory     A factory used by this mapper to create Java type generation
     *                        rules.
     * @param schemaGenerator the generator that this mapper will use if the config dictates
     *                        that the input documents are plain json (not json schema)
     */
    public EsSchemaMapper(RuleFactory ruleFactory, SchemaGenerator schemaGenerator) {
        this.ruleFactory = ruleFactory;
        this.schemaGenerator = schemaGenerator;
    }

    /**
     * Create a schema mapper with the default {@link RuleFactory}
     * implementation.
     *
     * @see RuleFactory
     */
    public EsSchemaMapper() {
        this(new RuleFactory(), new SchemaGenerator());
    }

    /**
     * Reads a schema and adds generated types to the given code model.
     *
     * @param codeModel   the java code-generation context that should be used to
     *                    generated new types
     * @param className   the name of the parent class the represented by this schema
     * @param packageName the target package that should be used for generated types
     * @param schemaUrl   location of the schema to be used as input
     * @return The top-most type generated from the given file
     * @throws IOException if the schema content cannot be read
     */
    public JType generate(JCodeModel codeModel, String className,
                          String packageName, URL schemaUrl,
                          Set<String> ignoreFields, Set<String> collectionFields,
                          String username, String password) throws IOException {

        JPackage jpackage = codeModel._package(packageName);

        JsonNode node;
        String url = schemaUrl.toString();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            String s = EsMapping2JsonschemaUtils.readFromUrl(url, username, password);
            node = objectMapper().readTree(s);
        } else {
            node = objectMapper().readTree(schemaUrl);
        }

        JsonNode json = EsMapping2JsonschemaUtils.trans(node, ignoreFields, collectionFields);

        return generate(codeModel, className, packageName, json);


    }

    private ObjectNode readSchema(URL schemaUrl) {

        switch (ruleFactory.getGenerationConfig().getSourceType()) {
            case JSONSCHEMA:
            case YAMLSCHEMA:
                ObjectNode schemaNode = NODE_FACTORY.objectNode();
                schemaNode.put("$ref", schemaUrl.toString());
                return schemaNode;
            case JSON:
            case YAML:
                return schemaGenerator.schemaFromExample(schemaUrl);
            default:
                throw new IllegalArgumentException("Unrecognised source type: " + ruleFactory.getGenerationConfig().getSourceType());
        }

    }

    public JType generate(JCodeModel codeModel, String className, String packageName, String json,
                          URI schemaLocation) throws IOException {

        JPackage jpackage = codeModel._package(packageName);

        JsonNode schemaNode = objectMapper().readTree(json);

        return ruleFactory.getSchemaRule().apply(className, schemaNode, null, jpackage,
                new Schema(schemaLocation, schemaNode, null));
    }

    public JType generate(JCodeModel codeModel, String className, String packageName, String json) throws IOException {

        JPackage jpackage = codeModel._package(packageName);

        JsonNode schemaNode;
        if (ruleFactory.getGenerationConfig().getSourceType() == SourceType.JSON) {
            JsonNode jsonNode = objectMapper().readTree(json);
            schemaNode = schemaGenerator.schemaFromExample(jsonNode);
        } else {
            schemaNode = objectMapper().readTree(json);
        }

        return ruleFactory.getSchemaRule().apply(className, schemaNode, null, jpackage, new Schema(null, schemaNode, null));
    }


    public JType generate(JCodeModel codeModel, String className, String packageName, JsonNode jsonNode) throws IOException {

        JPackage jpackage = codeModel._package(packageName);

        JsonNode schemaNode;
        if (ruleFactory.getGenerationConfig().getSourceType() == SourceType.JSON) {
            schemaNode = schemaGenerator.schemaFromExample(jsonNode);
        } else {
            schemaNode = jsonNode;
        }

        return ruleFactory.getSchemaRule().apply(className, schemaNode, null, jpackage, new Schema(null, schemaNode, null));
    }

    private ObjectMapper objectMapper() {
        return new ObjectMapper()
                .enable(JsonParser.Feature.ALLOW_COMMENTS)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    }
}
