package com.bixuebihui.jdbc.event;

import java.util.List;

/**
 * @author xwx
 */
public class ChangeEvent<V> {
    ChangeType changeType;
    String dbName;
    String tableName;
    String keyName;
    List<V> key;
    int influenceRecordCount;
    String conditionSummary;

    public ChangeEvent(String databaseName, String tableName, String keyName, List<V> id, ChangeType changeType) {
        this.dbName = databaseName;
        this.tableName = tableName;
        this.keyName =  keyName;
        this.changeType = changeType;
        this.key = id;
        this.influenceRecordCount = id.size();
    }

    public ChangeEvent(String databaseName, String tableName, String keyName,ChangeType changeType, int influenceRecordCount, String conditionSummary) {
        this.dbName = databaseName;
        this.tableName = tableName;
        this.keyName =  keyName;
        this.changeType = changeType;
        this.influenceRecordCount = influenceRecordCount;
        this.conditionSummary = conditionSummary;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public int getInfluenceRecordCount() {
        return influenceRecordCount;
    }

    public void setInfluenceRecordCount(int influenceRecordCount) {
        this.influenceRecordCount = influenceRecordCount;
    }

    public String getConditionSummary() {
        return conditionSummary;
    }

    public void setConditionSummary(String conditionSummary) {
        this.conditionSummary = conditionSummary;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<V> getKey() {
        return key;
    }

    public void setKey(List<V> key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "{" +
                "changeType=" + changeType +
                ", dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", keyName='" + keyName + '\'' +
                ", key=" + key +
                ", influenceRecordCount=" + influenceRecordCount +
                ", conditionSummary='" + conditionSummary + '\'' +
                '}';
    }
}
