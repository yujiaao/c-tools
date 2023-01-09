package com.bixuebihui.tablegen.generator;

import com.bixuebihui.tablegen.TableGen;
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
public class EsDalGenerator extends BaseGenerator {
    protected final static String CLASS_SUFFIX = "List";
    public static final String INDEX_ALIAS_SEPARATOR = ":";

    List<String> fields = new ArrayList<>();

    @Override
    String getTemplateFileName() {
        return "dal.java";
    }

    public static String getIndexName(String tableName){
        if(tableName.contains(INDEX_ALIAS_SEPARATOR)) {
           return tableName.substring(0, tableName.indexOf(INDEX_ALIAS_SEPARATOR));
        }
        return tableName;
    }

    public  static  String getAliasName(String tableName){
        if(tableName.contains(INDEX_ALIAS_SEPARATOR)) {
           return tableName.substring(tableName.indexOf(INDEX_ALIAS_SEPARATOR)+1);
        }
        return tableName;
    }


    @Override
    protected Map<String, Object> getContextMap(String tableName) {
        Map<String, Object> v = super.getContextMap(tableName);

        String aliasName = getAliasName(tableName);

        v.put("tableName", aliasName);
        v.put("pojoClassName", this.getPojoClassName(aliasName));
        v.put("className", this.getClassName(aliasName));
        v.put("interface", this.getInterface(aliasName));
        v.put("extends", this.getExtendsClasses(aliasName));

        v.put("fields", this.fields);
        return v;
    }

    @Override
    String getClassSuffix() {
        return CLASS_SUFFIX;
    }

    @Override
    public String getTargetFileName(String tableName) {
        return getTargetFileName("dal", getAliasName(tableName));
    }

    @Override
    String getClassName(String tableName) {
        return getPojoClassName(getAliasName(tableName)) + CLASS_SUFFIX;
    }


}
