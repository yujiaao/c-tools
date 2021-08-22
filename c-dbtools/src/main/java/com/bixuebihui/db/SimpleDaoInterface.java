package com.bixuebihui.db;

import com.bixuebihui.jdbc.IDbHelper;

import java.sql.SQLException;

/**
 * 抽象数据访问接口，方便数据访问的复用
 *
 * @author luax
 * @version $Id: $Id
 */
public interface  SimpleDaoInterface<T, ID> {
    /**
     * <p>getDbHelper.</p>
     *
     * @return a {@link IDbHelper} object.
     */
    IDbHelper getDbHelper();

    /**
     * <p>getTableName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getTableName();

    /**
     * <p>insert.</p>
     *
     * @param o a T object.
     * @return a boolean.
     * @throws java.sql.SQLException if any.
     */
    boolean insert(T o) throws SQLException;

    /**
     * <p>updateByKey.</p>
     *
     * @param o a T object.
     * @return a boolean.
     * @throws java.sql.SQLException if any.
     */
    boolean updateByKey(T o) throws SQLException;

    /**
     * <p>deleteByKey.</p>
     *
     * @param o a ID object.
     * @return a boolean.
     * @throws java.sql.SQLException if any.
     */
    boolean deleteByKey(ID o) throws SQLException;

}
