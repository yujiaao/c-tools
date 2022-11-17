package com.bixuebihui.jdbc;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xwx
 */
public interface IPrimaryKeyedTable<T, V> {


    /**
     * <p>getId.</p>
     *
     * @param info a T object.
     * @return a V object.
     */
    V getId(T info);

    /**
     * <p>setId.</p>
     *
     * @param info a T object.
     * @param id a V object.
     */
    void setId(T info, V id);


    /**
     * <p>getTableName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getTableName();

    /**
     * Database name
     * @return db name
     */
    String getDatabaseName();

    /**
     * primary key field name, if exists such, or null
     *
     * @return primary key field name, if exists such, or null
     */
    String getKeyName();

    /**
     *  return table name or alias or sharded table name
     * @param tableName
     * @return table name
     */
    default String getTableName(String tableName) {
        return (StringUtils.isBlank(tableName) ? getTableName() : tableName);
    }

}
