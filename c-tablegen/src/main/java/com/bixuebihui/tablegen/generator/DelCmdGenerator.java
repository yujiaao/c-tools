package com.bixuebihui.tablegen.generator;

import com.bixuebihui.tablegen.TableUtils;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.github.jknack.handlebars.Handlebars;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author xwx
 */
public class DelCmdGenerator extends BaseGenerator {
    protected final static String CLASS_SUFFIX = "DelCmd";

    @Override
    public boolean wantToGenerate(String fileName) {
        return super.wantToGenerate(fileName) && ! new File(fileName).exists();
    }

    @Override
    String getTemplateFileName(String tableName) {
        return "del-cmd.java";
    }

    protected String getSourceDir(){
        return config.getBaseCmdDir();
    }

    @Override
    protected Map<String, Object> getContextMap(String tableName) {
        Map<String, Object> v = super.getContextMap(tableName);
        List<ColumnData> cols = getColsInfo(tableName);
        List<String> keys = getKeysInfo(cols, tableName);

        v.put("firstKeyType", this.getPrimaryKeyType(tableName));
        v.put("firstKeyName", getPrimaryKeyName(keys));
        v.put("firstKeyFirstColumnName", this.getFirstKeyFirstColumnName(tableName));
        v.put("firstKeyFirstColumnType", this.getPrimaryKeyFirstColumnType(tableName));

        return v;
    }

    @Override
    String getClassSuffix() {
        return CLASS_SUFFIX;
    }

    @Override
    public String getTargetFileName(String tableName) {
        return getTargetFileName("cmd", tableName);
    }

    @Override
    String getClassName(String tableName) {
        return getPojoClassName(tableName) + CLASS_SUFFIX;
    }

    @Override
    protected void additionalSetting(Handlebars handlebars) {
        super.additionalSetting(handlebars);


        handlebars.registerHelper("columnAnnotation", (col, options) ->
                TableUtils.getColumnAnnotation(config, setInfo, options.param(0),(ColumnData) col, false));

    }

}
