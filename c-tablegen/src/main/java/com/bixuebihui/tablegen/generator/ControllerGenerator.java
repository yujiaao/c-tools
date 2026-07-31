package com.bixuebihui.tablegen.generator;

import com.bixuebihui.tablegen.TableGen;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.github.jknack.handlebars.Handlebars;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.bixuebihui.tablegen.NameUtils.toSnakeCase;

/**
 * @author xwx
 */
public class ControllerGenerator extends BaseGenerator {
    protected final static String CLASS_SUFFIX = "Controller";


    @Override
    String getTemplateFileName(String tableName) {
        return "manager-controller.java";
    }

    protected String getSourceDir(){
        return config.getBaseControllerDir();
    }

    @Override
    public boolean wantToGenerate(String fileName) {
        // 在 gen-with-merge (enableMerge=true) 模式下，controller 类不覆盖已存在的文件，保留用户修改
        if (config.isEnableMerge()) {
            return !new File(fileName).exists();
        }
        return super.wantToGenerate(fileName);
    }

    @Override
    protected Map<String, Object> getContextMap(String tableName) {
        Map<String, Object> v = super.getContextMap(tableName);
        List<ColumnData> cols = getColsInfo(tableName);
        List<String> keys = getKeysInfo(cols, tableName);

        v.put("hasVersionCol", containsColName(cols
                , config.getVersionColName()));


        v.put("firstKeyType", this.getPrimaryKeyType(tableName));
        v.put("firstKeyName", this.getFirstKeyName(tableName));

        v.put("whereNoLike", TableGen.createPreparedWhereClause(keys,
                false, cols));

        v.put("isView", isView());
        return v;
    }

    @Override
    String getClassSuffix() {
        return CLASS_SUFFIX;
    }

    @Override
    public String getTargetFileName(String tableName) {
        return getTargetFileName("controller"+File.separator+"autogenerate", tableName);
    }

    @Override
    String getClassName(String tableName) {
        return getPojoClassName(tableName) + CLASS_SUFFIX;
    }

    @Override
    protected void additionalSetting(Handlebars handlebars) {
        super.additionalSetting(handlebars);
        // usage: {{type tableName colName}}
        handlebars.registerHelper("colType", (tableName, options) -> getColType(options.param(0), this.setInfo.getTableCols((String) tableName)));
        handlebars.registerHelper("isKey", (tableName, options) -> this.setInfo.getTableKeys((String) tableName).contains(options.param(0)));
        handlebars.registerHelper("snakeCase", (tableName, options) -> toSnakeCase((String) tableName));

        // usage: {{where tableName}}
        handlebars.registerHelper("where", (tableName, options) ->
        {
            boolean like = options.param(0);
            return TableGen.createPreparedWhereClause(this.setInfo.getTableKeys((String) tableName), like, this.setInfo.getTableCols((String) tableName));
        });

    }


}
