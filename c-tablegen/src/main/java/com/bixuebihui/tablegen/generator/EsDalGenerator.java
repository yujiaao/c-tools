package com.bixuebihui.tablegen.generator;

import com.bixuebihui.dbcon.DatabaseConfig;
import com.bixuebihui.tablegen.GenException;
import com.bixuebihui.tablegen.ProjectConfig;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.EsKeyInfo;
import com.bixuebihui.tablegen.entry.KeyInfo;
import com.bixuebihui.tablegen.entry.TableSetInfo;

import java.util.*;

/**
 * @author xwx
 */
public class EsDalGenerator extends BaseGenerator {
    protected final static String CLASS_SUFFIX = "List";
    public static final String INDEX_ALIAS_SEPARATOR = ":";

    List<String> fields = new ArrayList<>();

    KeyInfo keyInfo;
    private List<String> primaryKeyFields;
    private int keyIndex;

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

    String getFirstKeyType() {
        if(keyIndex>=0 && keyIndex < primaryKeyFields.size()) {
            String field = primaryKeyFields.get(keyIndex);
            if(field != null &&  keyInfo.getKeyCache().containsKey(field)) {
                return keyInfo.getKeyCache().get(field).get(keyIndex);
            }
        }
        return null;
    }


    @Override
    protected Map<String, Object> getContextMap(String tableName) {
        Map<String, Object> v = super.getContextMap(tableName);

        v.put("keys", keyInfo.getTableKeys(tableName));

        String keyType = this.getFirstKeyType();

        if(keyType!=null ) {
            v.put("hasKey", true);
            v.put("firstKeyType", keyType);
            v.put("firstKeyName", this.getFirstKeyName());
        }

        String aliasName = getAliasName(tableName);

        v.put("tableName", aliasName);
        v.put("pojoClassName", this.getPojoClassName(aliasName));
        v.put("className", this.getClassName(aliasName));
        v.put("interface", this.getInterface(aliasName));
        v.put("extends", this.getExtendsClasses(aliasName));

        v.put("fields", this.fields);
        return v;
    }

    private String getFirstKeyName() {
        return primaryKeyFields.get(keyIndex);
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


    public void init(ProjectConfig config, DatabaseConfig dbConfig, Properties esConfig) {
        super.init(config, dbConfig, new TableSetInfo(),  esConfig);
    }

    public void setKeyInfo(KeyInfo keyInfo){
        this.keyInfo = keyInfo;
    }

    public void setPrimaryKeyInfo(List<String> primaryKeyFields, int index) {
        this.primaryKeyFields = primaryKeyFields;
        this.keyIndex = index;
    }
}
