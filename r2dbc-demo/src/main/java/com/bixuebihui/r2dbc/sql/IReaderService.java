package com.bixuebihui.r2dbc.sql;

import java.sql.SQLException;
import java.util.Collection;

/**
 * 配合使用完成分页读取数据库表
 *
 * @author xingwx
 * @param <T>  返回类型
 * @version $Id: $Id
 */
public interface IReaderService<T> {

    /**
     * <p>select.</p>
     *
     * @param whereClause a {@link String} object.
     * @param params parameters for prepared statement
     * @param orderBy     a {@link String} object.
     * @param rowStart    a int.
     * @param rowEnd      a int.
     * @return a {@link Collection} object.
     * @throws SQLException if any.
     */
    Collection<T> select(String whereClause, Object[] params, String orderBy, int rowStart, int rowEnd) throws SQLException;

    /**
     * <p>count.</p>
     *
     * @param whereClause a {@link String} object.
     * @return a int.
     * @throws SQLException if any.
     */
    int count(String whereClause) throws SQLException;
    int countWhere(String whereClause, Object[] params) throws SQLException;


}
