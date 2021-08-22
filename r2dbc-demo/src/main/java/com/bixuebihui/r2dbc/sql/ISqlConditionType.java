package com.bixuebihui.r2dbc.sql;

/**
 * <p>ISqlConditionType interface.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public interface ISqlConditionType {
    /**
     * 得到自定义类型的条件语句
     *
     * @param sqlFieldName 字段名
     * @param databaseType 采用BaseDao里的数据类型定义
     * @return 为and开头的条件子句
     */
    String getConditionSql(String sqlFieldName, int databaseType);
}
