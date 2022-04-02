package com.bixuebihui.tablegen.generator;

import com.bixuebihui.jsongen.GenEs;
import com.bixuebihui.tablegen.NameUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.bixuebihui.jsongen.EsJsonschema2Pojo.getNodeName;

/**
 * @author xwx
 */
public class EsJsonSchemaGenerator extends BaseGenerator {
    protected final static String CLASS_SUFFIX = "Manager";

    private static final Logger LOG = LoggerFactory.getLogger(EsJsonSchemaGenerator.class);


    @Override
    public boolean wantToGenerate(String fileName) {
        return super.wantToGenerate(fileName) && ! new File(fileName).exists();
    }

    @Override
    String getClassSuffix(){return CLASS_SUFFIX;}

    @Override
    String getTemplateFileName() {
        return "business.java";
    }

    @Override
    public String getTargetFileName(String tableName) {
        return getTargetFileName("business", tableName);
    }


    @Override
    String getClassName(String tableName) {
        return getPojoClassName(tableName) + CLASS_SUFFIX;
    }

    @Override
    public void generateToFile(String tableName) throws IOException {

        String type = esConfig.getProperty("type", "elasticsearch_mapping");

        boolean isSchema = "elasticsearch_mapping".equalsIgnoreCase(esConfig.getProperty("type"));

        boolean isElasticSearch = type.contains("elasticsearch");


        String sourceDirectory = isSchema ? esConfig.getProperty("url") + "/" + tableName + "/_mapping"
                : ( isElasticSearch ? esConfig.getProperty("url") + "/" + tableName + "/_search?size=1"
                        : esConfig.getProperty("url")
        );
        String targetDirectory=this.config.getSrcDir();
        GenEs gen = new GenEs(isSchema, sourceDirectory, targetDirectory, esConfig.getProperty("package_name")+".pojo");

        EsDalGenerator dal = new EsDalGenerator();
        dal.init(config, dbConfig, setInfo, esConfig);

        Set<String> ignoreFields = Set.of(esConfig.getProperty("ignore_fields","").split(","));
        Set<String> collectionFields = Set.of(esConfig.getProperty("collection_fields","").split(","));
        gen.generate(stringPair -> dal.fields.addAll(Arrays.asList(stringPair.getRight())), ignoreFields, collectionFields);


        dal.generateToFile(tableName);


        if(isElasticSearch) {
            super.generateToFile(tableName);
        }
    }


}
