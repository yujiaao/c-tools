package com.bixuebihui.tablegen.generator;

import com.bixuebihui.tablegen.TableGen;
import com.bixuebihui.tablegen.TableUtils;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.github.jknack.handlebars.Handlebars;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.bixuebihui.tablegen.NameUtils.firstUp;

/**
 * @author xwx
 */
public class DtoGenerator extends BaseGenerator {
    protected final static String CLASS_SUFFIX = "Dto";


    @Override
    String getTemplateFileName() {
        return "dto.java";
    }

    @Override
    public boolean wantToGenerate(String fileName) {
        return super.wantToGenerate(fileName) && ! new File(fileName).exists();
    }


    protected String getSourceDir(){
        return config.getBaseDtoDir();
    }

    public static String createKeyObjects(List<String> params) {
        StringBuilder objs = new StringBuilder();
        if (params != null) {
            String key;
            for (Iterator<String> e = params.iterator(); e.hasNext(); ) {
                key = e.next();

                objs.append("info.get").append(firstUp(key)).append("()");

                if (e.hasNext()) {
                    objs.append(" , ");
                }
            }

        } else {
            objs = null;
        }

        return objs == null ? null : objs.toString();
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
        v.put("keyObjects", createKeyObjects(keys));

        v.put("isView", isView());

        return v;
    }

    @Override
    String getClassSuffix() {
        return CLASS_SUFFIX;
    }

    @Override
    public String getTargetFileName(String tableName) {
        return getTargetFileName("dto", tableName);
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


        handlebars.registerHelper("columnAnnotation", (col, options) ->
                TableUtils.getColumnAnnotation(config, setInfo, options.param(0),(ColumnData) col, false));

        handlebars.registerHelper("columnDescription", (col, options) -> TableUtils.getColumnDescription(config,
                setInfo.getColumnsExtInfo(options.param(0)), options.param(0), (ColumnData)col));

    }

    String getFirstKeyName(String tableName) {
        List<String> keyData = getKeyData(tableName);
        if(!isNotEmpty(keyData)) {
            LOG.warn("Table '"+tableName+"' have not a  primary key, this may be a error");
        }
        return getFirstKeyName(keyData);
    }

}
