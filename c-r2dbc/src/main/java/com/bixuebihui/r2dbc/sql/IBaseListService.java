package com.bixuebihui.r2dbc.sql;

import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 数据表基础操作接口，为与jmesa配合使用
 *
 * @author xingwx
 * @param <T> 表类型
 * @param <V> 主键类型
 * @version $Id: $Id
 */
public interface IBaseListService<T, V> extends IReaderService<T> {
    /**
     * <p>selectByIds.</p>
     *
     * @param uniquePropertyName   a {@link String} object.
     * @param uniquePropertyValues a {@link List} object.
     * @return a {@link Map} object.
     * @throws SQLException if any.
     */
    @NotNull Mono<Map<String, T>> selectByIds(String uniquePropertyName, List<String> uniquePropertyValues) throws SQLException;

    /**
     * <p>updateByKey.</p>
     *
     * @param info a T object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    Mono<Boolean> updateByKey(T info) throws SQLException;

    /**
     * <p>insertDummy.</p>
     *
     * @return a boolean.
     * @throws SQLException if any.
     */
    Mono<Boolean> insertDummy() throws SQLException;

    /**
     * <p>deleteByKey.</p>
     *
     * @param key a V object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    Mono<Boolean> deleteByKey(V key) throws SQLException;

    /**
     * <p>insertAutoNewKey.</p>
     *
     * @param info a T object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    Mono<Boolean> insertAutoNewKey(T info) throws SQLException;

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
     * <p>create.</p>
     *
     * @return a T object.
     */
    T create();

    /**
     * <p>getNextKey.</p>
     *
     * @return a V object.
     */
    Mono<V> getNextKey();

}
