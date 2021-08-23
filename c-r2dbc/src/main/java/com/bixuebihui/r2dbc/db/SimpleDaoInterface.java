package com.bixuebihui.r2dbc.db;

import com.bixuebihui.r2dbc.sql.IDbHelper;
import reactor.core.publisher.Mono;

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
     * @return a {@link String} object.
     */
    String getTableName();

    /**
     * <p>insert.</p>
     *
     * @param o a T object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    Mono<Boolean> insert(T o) throws SQLException;

    /**
     * <p>updateByKey.</p>
     *
     * @param o a T object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    Mono<Boolean> updateByKey(T o) throws SQLException;

    /**
     * <p>deleteByKey.</p>
     *
     * @param o a ID object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    Mono<Boolean> deleteByKey(ID o) throws SQLException;

}
