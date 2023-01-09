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
import java.util.*;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String username;
    private String password;

    private String alias;



    public GenEs(boolean isSchema, String sourceDirectory,
                 String targetDirectory, String targetPackage,
                 String username, String password,
                 String alias) {
        this.isSchema = isSchema;
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        this.targetPackage = targetPackage;
        this.username = username;
        this.password = password;

        this.alias = alias;
    }


    /**
     *  <configuration>
     *                     <sourceDirectory>${basedir}/src/test/resources/schemas</sourceDirectory>
     *                     <serializable>true</serializable>
     *                     <includeAdditionalProperties>false</includeAdditionalProperties>
     *                     <targetPackage>com.bixuebihui.dto</targetPackage>
     *                     <useLongIntegers>true</useLongIntegers>
     *                     <sourceType>json</sourceType>
     *                     <outputDirectory>${basedir}/src/main/java</outputDirectory>
     *                 </configuration>
     */
   public void generate(Consumer<Pair<String, String[]>> consumer, Set<String> ignoreFields, Set<String> collectionFields) throws  IOException {
       Map<String, String> additionalProperties = new HashMap<>();
       if(username!=null) {
           additionalProperties.put("username", username);
           additionalProperties.put("password", password);
       }
       if(alias!=null){
           additionalProperties.put("alias", alias);
       }
       EsJsonschema2Pojo.generate(this,  new MyRuleLogger(), consumer, ignoreFields,  collectionFields, additionalProperties);
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
