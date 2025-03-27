package com.bixuebihui.tablegen.generator;

import com.bixuebihui.jsongen.GenEs;
import com.bixuebihui.tablegen.entry.EsKeyInfo;
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
    String getTemplateFileName(String tableName) {
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
    public void generateToFile(String tableName, int index) throws IOException {

        String type = esConfig.getProperty("type", "elasticsearch_mapping");

        boolean isSchema = "elasticsearch_mapping".equalsIgnoreCase(esConfig.getProperty("type"));

        boolean isElasticSearch = type.contains("elasticsearch");

        String indexName= EsDalGenerator.getIndexName(tableName);
        String aliasName = EsDalGenerator.getAliasName(tableName);

        String sourceDirectory = isSchema ? esConfig.getProperty("url") + "/" + indexName + "/_mapping"
                : ( isElasticSearch ? esConfig.getProperty("url") + "/" + indexName + "/_search?size=1"
                        : esConfig.getProperty("url")
        );


        String targetDirectory=this.config.getSrcDir();
        GenEs gen = new GenEs(isSchema, sourceDirectory, targetDirectory,
                esConfig.getProperty("package_name")+".pojo",
                esConfig.getProperty("username"), esConfig.getProperty("password"), aliasName);

        EsDalGenerator dal = new EsDalGenerator();

        dal.init(config, dbConfig, esConfig);

        Set<String> ignoreFields = Set.of(esConfig.getProperty("ignore_fields","").split(","));
        Set<String> collectionFields = Set.of(esConfig.getProperty("collection_fields","").split(","));

        List<String> primaryKeyFields = Arrays.asList(esConfig.getProperty("primary_key_fields","").split(","));


        Map<String, String> keyNameTypeMap = gen.generate(indexFieldsPair -> dal.fields.addAll(Arrays.asList(indexFieldsPair.getRight())),
                ignoreFields, collectionFields, primaryKeyFields);

        EsKeyInfo esKeyInfo = new EsKeyInfo();

        esKeyInfo.setKeyCache(keyNameTypeMap);

        dal.setKeyInfo(esKeyInfo);
        dal.setPrimaryKeyInfo(primaryKeyFields, index);
        dal.generateToFile(aliasName, index);


        if(isElasticSearch) {
            super.generateToFile(aliasName,index);
        }
    }


}
