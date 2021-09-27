package com.bixuebihui.jdbc;

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
     * @param uniquePropertyName   a {@link java.lang.String} object.
     * @param uniquePropertyValues a {@link java.util.List} object.
     * @return a {@link java.util.Map} object.
     */
    Map<String, T> selectByIds(String uniquePropertyName, List<String> uniquePropertyValues);

    /**
     * <p>updateByKey.</p>
     *
     * @param info a T object.
     * @return a boolean.
     */
    boolean updateByKey(T info);

    /**
     * <p>insertDummy.</p>
     *
     * @return a boolean.
     */
    boolean insertDummy();

    /**
     * <p>deleteByKey.</p>
     *
     * @param key a V object.
     * @return a boolean.
     */
    boolean deleteByKey(V key);

    /**
     * <p>insertAutoNewKey.</p>
     *
     * @param info a T object.
     * @return a boolean.
     */
    boolean insertAutoNewKey(T info);

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
     * @return a {@link java.lang.String} object.
     */
    String getTableName();

    /**
     * <p>insert.</p>
     *
     * @param o a T object.
     * @return a boolean.
     */
    boolean insert(T o);

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
    V getNextKey();

}
