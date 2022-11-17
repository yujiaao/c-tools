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

    public ChangeEvent(String databaseName, String tableName, String keyName, List<V> id, ChangeType changeType) {
        this.dbName = databaseName;
        this.tableName = tableName;
        this.keyName =  keyName;
        this.changeType = changeType;
        this.key = id;
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
                '}';
    }
}
