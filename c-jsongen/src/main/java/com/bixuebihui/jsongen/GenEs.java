package com.bixuebihui.jsongen;

import org.jsonschema2pojo.AbstractRuleLogger;
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


/**
 * @author xwx
 */
public class GenEs extends DefaultGenerationConfig {
    static Logger logger = LoggerFactory.getLogger(GenEs.class);

    private  boolean isSchema;
    private String sourceDirectory;
    private String targetDirectory;

    public GenEs(String sourceDirectory, String targetDirectory, boolean isSchema) {
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        this.isSchema = isSchema;
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
   public void generate() throws  IOException {
       EsJsonschema2Pojo.generate(this,  new MyRuleLogger());
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
        return "com.bixuebihui.hit";
    }

    @Override
    public boolean isUseLongIntegers() {
        return true;
    }

    @Override
    public Iterator<URL> getSource() {
        return Collections.singleton( URLUtil.parseURL(sourceDirectory)).iterator();
    }

    private static class MyRuleLogger extends AbstractRuleLogger {
        @Override
        protected void doDebug(String msg) {
            logger.debug(msg);
        }

        @Override
        protected void doError(String msg, Throwable e) {
            logger.error(msg, e);
        }

        @Override
        protected void doInfo(String msg) {
            logger.info(msg);
        }

        @Override
        protected void doTrace(String msg) {
            logger.trace(msg);
        }

        @Override
        protected void doWarn(String msg, Throwable e) {
            logger.warn(msg, e);
        }

        @Override
        public boolean isDebugEnabled() {
            return logger.isDebugEnabled();
        }

        @Override
        public boolean isErrorEnabled() {
            return logger.isErrorEnabled();
        }

        @Override
        public boolean isInfoEnabled() {
            return logger.isInfoEnabled();
        }

        @Override
        public boolean isTraceEnabled() {
            return logger.isTraceEnabled();
        }

        @Override
        public boolean isWarnEnabled() {
            return logger.isWarnEnabled();
        }
    }
}
