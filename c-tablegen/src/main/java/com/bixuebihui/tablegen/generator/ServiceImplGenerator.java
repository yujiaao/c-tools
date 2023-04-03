package com.bixuebihui.tablegen.generator;

import com.bixuebihui.tablegen.TableGen;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.github.jknack.handlebars.Handlebars;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author xwx
 */
public class ServiceImplGenerator extends BaseGenerator {
    protected final static String CLASS_SUFFIX = "ServiceImpl";


    @Override
    String getTemplateFileName() {
        return "service-impl.java";
    }

    protected String getSourceDir(){
        return config.getBaseServiceImplDir();
    }

    @Override
    public boolean wantToGenerate(String fileName) {
        return super.wantToGenerate(fileName) && ! new File(fileName).exists();
    }

    @Override
    protected Map<String, Object> getContextMap(String tableName) {
        Map<String, Object> v = super.getContextMap(tableName);
        List<ColumnData> cols = getColsInfo(tableName);
        List<String> keys = getKeysInfo(cols, tableName);

        v.put("hasVersionCol", containsColName(cols
                , config.getVersionColName()));


        v.put("firstKeyType", this.getFirstKeyType(tableName));
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
        return getTargetFileName("service"+File.separator+"autogenerate"+File.separator+"impl", tableName);
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

        // usage: {{where tableName}}
        handlebars.registerHelper("where", (tableName, options) ->
        {
            boolean like = options.param(0);
            return TableGen.createPreparedWhereClause(this.setInfo.getTableKeys((String) tableName), like, this.setInfo.getTableCols((String) tableName));
        });

    }

    String getFirstKeyName(String tableName) {
        List<String> keyData = getKeyData(tableName);
        if(!isNotEmpty(keyData)) {
            LOG.warn("Table '"+tableName+"' have not a  primary key, this may be a error");
        }
        return getFirstKeyName(keyData);
    }

}
