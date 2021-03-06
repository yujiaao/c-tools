package com.bixuebihui.tablegen.generator;

import java.io.File;

/**
 * @author xwx
 */
public class BaseListGenerator extends BaseGenerator{

    @Override
    public String getTargetFileName(String tableName) {
        String baseDir = config.getBaseSrcDir();

        return baseDir + File.separator + "BaseList.java";

    }

    @Override
    String getTemplateFileName() {
        return "baselist.java";
    }

    @Override
    public String getClassName(String tableName) {
        return "BaseList";
    }
}
