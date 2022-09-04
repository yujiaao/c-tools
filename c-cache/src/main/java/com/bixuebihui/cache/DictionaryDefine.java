package com.bixuebihui.cache;

import com.bixuebihui.jdbc.IBaseListService;
import com.bixuebihui.util.other.CMyString;

/**
 * @author xwx
 */
public class DictionaryDefine {

    private String addCondition = "";

    private String conditionValue;

    private String idFieldName = "MS_ID";
    private String valueFieldName = "MS_VALUE";
    private String sortFieldName = "MS_SORT";
    private IBaseListService<DictionaryItem, String> serviceClass;
    private int maxCapacity = 5000;
    private String tableName;

    public DictionaryDefine(String tableName, String idFieldName, String valueName, String sortName, String addCondition) {
        this.tableName = tableName;
        this.idFieldName = idFieldName;
        this.valueFieldName = valueName;
        this.sortFieldName = sortName;
        this.addCondition = addCondition;
    }

    public DictionaryDefine(String tableName) {
        this.tableName = tableName;
    }

    public DictionaryDefine() {
    }

    public String getIdFieldName() {
        return idFieldName;
    }

    public void setIdFieldName(String idName) {
        this.idFieldName = idName;
    }

    public String getValueFieldName() {
        return valueFieldName;
    }

    public void setValueFieldName(String valueName) {
        this.valueFieldName = valueName;
    }

    public String getSortFieldName() {
        return sortFieldName;
    }

    public void setSortFieldName(String sortFieldName) {
        this.sortFieldName = sortFieldName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    protected String getAddCondition() {
        return addCondition;
    }

    public void setAddCondition(String addCondition) {
        this.addCondition = addCondition;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public String getSqlCondition() {
        if (this.conditionValue != null) {
            return CMyString.replaceStr(this.getAddCondition(), "{0}", this.conditionValue);
        }
        return this.getAddCondition();
    }

    public IBaseListService<DictionaryItem, String> getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(IBaseListService<DictionaryItem, String> serviceClass) {
        this.serviceClass = serviceClass;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
