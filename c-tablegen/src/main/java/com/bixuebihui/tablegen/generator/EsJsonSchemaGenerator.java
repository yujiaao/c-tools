package com.bixuebihui.tablegen.generator;

import com.bixuebihui.jsongen.GenEs;
import com.bixuebihui.tablegen.NameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        String targetDirectory=this.getBaseDir();
        String type = esConfig.getProperty("type", "elasticsearch_mapping");

        boolean isSchema = "elasticsearch_mapping".equalsIgnoreCase(esConfig.getProperty("type"));

        boolean isElasticSearch = type.contains("elasticsearch");


        String sourceDirectory = isSchema ? esConfig.getProperty("url") + "/" + tableName + "/_mapping"
                : ( isElasticSearch ? esConfig.getProperty("url") + "/" + tableName + "/_search?size=1"
                        : esConfig.getProperty("url")
        );
        GenEs gen = new GenEs(isSchema, sourceDirectory, targetDirectory, esConfig.getProperty("package_name"));
        gen.generate();
        //String className = getNodeName(tableName, gen);
        if(isElasticSearch) {
            super.generateToFile(NameUtils.tableNameToClassName(tableName));
        }
    }


}
