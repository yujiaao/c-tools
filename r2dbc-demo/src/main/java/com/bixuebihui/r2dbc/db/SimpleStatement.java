package com.bixuebihui.r2dbc.db;

import java.sql.SQLException;
import java.util.List;

/**
 * Date: 13-4-18
 * Time: 下午6:04
 * 简单的数据库查询
 * @author xingwx
 * @version $Id: $Id
 */
public interface SimpleStatement<T,V> {

    /**
     * <p>insert.</p>
     * 插入单个
     * @param entity a T object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    boolean insert(T entity)throws SQLException;

    /**
     * <p>insertAndReferences.</p>
     * 插入并插入依赖
     * @param entity a T object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    boolean insertAndReferences(T entity)throws SQLException;

    //插入多个

    /**
     * <p>insert.</p>
     *
     * @param entities a {@link List} object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    boolean insert(List<T> entities)throws SQLException;

    //插入或更新

    /**
     * <p>insertOrUpdate.</p>
     *
     * @param entity a T object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    boolean insertOrUpdate(T entity) throws SQLException;

    //插入或更新多个

    /**
     * <p>insertOrUpdate.</p>
     *
     * @param entity a {@link List} object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    boolean insertOrUpdate(List<T> entity)throws SQLException;

    //通过主键获得

    /**
     * <p>findByKey.</p>
     *
     * @param id a V object.
     * @return a T object.
     * @throws SQLException if any.
     */
    T findByKey(V id)throws SQLException;

    //通过多个主键获得

    /**
     * <p>findAllByKeys.</p>
     *
     * @param id an array of V[] objects.
     * @return a {@link List} object.
     * @throws SQLException if any.
     */
    List<T> findAllByKeys(V[] id)throws SQLException;

    //更新

    /**
     * <p>update.</p>
     *
     * @param entity a T object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    boolean update(T entity) throws SQLException;


    /**
     * <p>updateAndReferences.</p>
     * 更新并更新依赖
     * @param entity a T object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    boolean updateAndReferences(T entity)throws SQLException;


    /**
     * <p>update.</p>
     * 批量更新
     * @param entities a {@link List} object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    boolean update(List<T> entities)throws SQLException;

    /**
     * <p>delete.</p>
     * 删除单个
     * @param entity a T object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    boolean delete(T entity)throws SQLException;


    /**
     * <p>deleteByKey.</p>
     * 删除单个
     * @param id a V object.
     * @return a boolean.
     * @throws SQLException if any.
     */
    boolean deleteByKey(V id)throws SQLException;

    //批量删除

    /**
     * <p>deleteByKeys.</p>
     *
     * @param id an array of V[] objects.
     * @return a boolean.
     * @throws SQLException if any.
     */
    boolean deleteByKeys(V[] id)throws SQLException;

    //获得所有

    /**
     * <p>findAll.</p>
     *
     * @return a {@link List} object.
     * @throws IllegalAccessException if any.
     * @throws SQLException            if any.
     * @throws InstantiationException if any.
     */
    List<T> findAll() throws IllegalAccessException, SQLException, InstantiationException;

    //获得总数

    /**
     * <p>countAll.</p>
     *
     * @return a int.
     * @throws SQLException if any.
     */
    int countAll() throws SQLException;
}
