package com.bixuebihui.tablegen.generator;

import java.io.File;

/**
 * @author xwx
 */
public class BaseListGenerator extends BaseGenerator{

    @Override
    public String getTargetFileName(String tableName) {
        return getBaseDir() + File.separator + "BaseList.java";
    }


    @Override
    String getTemplateFileName(String tableName) {
        return "baselist.java";
    }

    @Override
    public String getClassName(String tableName) {
        return "BaseList";
    }
}
