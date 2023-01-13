package com.bixuebihui.tablegen.generator;

import com.bixuebihui.tablegen.TableGen;
import com.bixuebihui.tablegen.TableUtils;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.github.jknack.handlebars.Handlebars;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.bixuebihui.tablegen.NameUtils.columnNameToConstantName;
import static com.bixuebihui.tablegen.NameUtils.firstUp;
import static com.bixuebihui.tablegen.TableGen.INDENT;
import static java.util.Collections.emptyList;

/**
 * @author xwx
 */
public class SearchCmdGenerator extends BaseGenerator {
    protected final static String CLASS_SUFFIX = "SearchCmd";


    @Override
    String getTemplateFileName() {
        return "search-cmd.java";
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

        handlebars.registerHelper("columnAnnotation", (col, options) -> TableUtils.getColumnAnnotation(config, setInfo, options.param(0),(ColumnData) col));

        handlebars.registerHelper("columnDescription", (col, options) -> TableUtils.getColumnDescription(config,
                setInfo.getColumnsExtInfo(options.param(0)), options.param(0), (ColumnData)col));

    }

}
