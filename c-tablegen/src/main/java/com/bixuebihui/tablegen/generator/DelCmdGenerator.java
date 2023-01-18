package com.bixuebihui.tablegen.generator;

import com.bixuebihui.tablegen.TableUtils;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.github.jknack.handlebars.Handlebars;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xwx
 */
public class DelCmdGenerator extends BaseGenerator {
    protected final static String CLASS_SUFFIX = "DelCmd";


    @Override
    String getTemplateFileName() {
        return "del-cmd.java";
    }

    protected String getSourceDir(){
        return config.getBaseCmdDir();
    }

    @Override
    protected Map<String, Object> getContextMap(String tableName) {
        Map<String, Object> v = super.getContextMap(tableName);
        List<ColumnData> cols = isView ? this.setInfo.getViewCols(tableName) : this.setInfo.getTableCols(tableName);
        List<String> keys = getKeyData(tableName);
        if(keys.isEmpty() && isView){
            keys = new ArrayList<>();
            keys.add(cols.get(0).getName());
        }

        v.put("firstKeyType", this.getFirstKeyType(tableName));
        v.put("firstKeyName", this.getFirstKeyName(keys));

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

        handlebars.registerHelper("isRangeType", (typeName, options) ->
                List.of("Timestamp", "Date", "DateTime", "Time").contains((String)typeName));


        handlebars.registerHelper("columnAnnotation", (col, options) ->
                TableUtils.getColumnAnnotation(config, setInfo, options.param(0),(ColumnData) col, false));

        handlebars.registerHelper("columnDescription", (col, options) -> TableUtils.getColumnDescription(config,
                setInfo.getColumnsExtInfo(options.param(0)), options.param(0), (ColumnData)col));

    }

}
