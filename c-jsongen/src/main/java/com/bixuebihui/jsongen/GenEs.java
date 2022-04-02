package com.bixuebihui.jsongen;

import org.apache.commons.lang3.tuple.Pair;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.SourceType;
import org.jsonschema2pojo.util.URLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;


/**
 * @author xwx
 */
public class GenEs extends DefaultGenerationConfig {
    static Logger logger = LoggerFactory.getLogger(GenEs.class);

    private  boolean isSchema;
    private String sourceDirectory;
    private String targetDirectory;


    private String targetPackage;

    public GenEs(boolean isSchema, String sourceDirectory, String targetDirectory, String targetPackage) {
        this.isSchema = isSchema;
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        this.targetPackage = targetPackage;
    }


    /**
     *  <configuration>
     *                     <sourceDirectory>${basedir}/src/test/resources/schemas</sourceDirectory>
     *                     <serializable>true</serializable>
     *                     <includeAdditionalProperties>false</includeAdditionalProperties>
     *                     <targetPackage>com.autohome.mobilevideo.car.dto</targetPackage>
     *                     <useLongIntegers>true</useLongIntegers>
     *                     <sourceType>json</sourceType>
     *                     <outputDirectory>${basedir}/src/main/java</outputDirectory>
     *                 </configuration>
     */
   public void generate(Consumer<Pair<String, String[]>> consumer, Set<String> ignoreFields, Set<String> collectionFields) throws  IOException {
       EsJsonschema2Pojo.generate(this,  new MyRuleLogger(), consumer, ignoreFields,  collectionFields);
   }

    @Override
    public File getTargetDirectory() {
        return new File(targetDirectory);
    }

    @Override
    public SourceType getSourceType() {
        return isSchema? SourceType.JSONSCHEMA:SourceType.JSON;
    }

    @Override
    public String getTargetPackage() {
        return targetPackage;
    }

    @Override
    public boolean isUseLongIntegers() {
        return true;
    }

    @Override
    public Iterator<URL> getSource() {
        return Collections.singleton( URLUtil.parseURL(sourceDirectory)).iterator();
    }

}
