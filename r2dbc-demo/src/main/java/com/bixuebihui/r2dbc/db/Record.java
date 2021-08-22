package com.bixuebihui.r2dbc.db;


import com.bixuebihui.DbException;
import com.bixuebihui.db.GroupFunction;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * <p>Record interface.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public interface Record<T> {
	/**
	 * <p>getVector.</p> 获得单个字段
	 *
	 * @param field a {@link String} object.
	 * @return a {@link List} object.
	 * @throws DbException if any.
	 */
	List<Object> getVector(String field) throws DbException;


	/**
	 * <p>findAll.</p>
	 * 查询多条
	 *
	 * @return a {@link List} object.
	 * @throws DbException if any.
	 */
	List<T> findAll() throws DbException;

	/**
	 * <p>findAll.</p>
	 *
	 * @param clz the custom return type, for example a entity type that exclude return for blob/clob fields,
	 *            used in combination with the .fields() method.
	 * @return a {@link List} object.
	 * @throws DbException if any.
	 */
	<K> List<K> findAll(Class<K> clz) throws DbException;


	/**
	 * select one record from db
	 * @return a T object.
	 * @throws DbException if any.
	 */
	T find() throws DbException;


	/**
	 * <p>delete.</p>
	 * 删除
	 * @return a boolean.
	 * @throws DbException if any.
	 */
	Mono<Boolean> delete() throws DbException;


	/**
	 * <p>get.</p>
	 * 获得单个字段
	 * @param field a {@link String} object.
	 * @return a {@link Object} object.
	 * @throws DbException if any.
	 */
	Object get(String field) throws DbException;

	/**
	 * <p>count.</p> 获得数量
	 *
	 * @return a int.
	 * @throws DbException if any.
	 */
	Mono<Integer> count() throws DbException;

	/**
	 * <p>countValue.</p> 获得数量
	 *
	 * @param field a {@link String} object.
	 * @param fun   a {@link GroupFunction} object.
	 * @return a {@link CountValue} object.
	 * @throws DbException if any.
	 */
	CountValue countValue(String field, GroupFunction fun) throws DbException;

	/**
	 * <p>countObject.</p>
	 *
	 * @param field      a {@link String} object.
	 * @param fun        a {@link GroupFunction} object.
	 * @param objectType a {@link Class} object.
	 * @param <K>        a K object.
	 * @return a {@link CountObject} object.
	 * @throws DbException if any.
	 */
	<K> CountObject<K> countObject(String field, GroupFunction fun, Class<K> objectType) throws DbException;

	/**
	 * <p>countSum.</p> 获得数量
	 *
	 * @param field a {@link String} object.
	 * @return a {@link CountValue} object.
	 * @throws DbException if any.
	 */
	CountValue countSum(String field) throws DbException;

	/**
	 * <p>getSql.</p> 获得生产的sql
	 *
	 * @return a {@link SqlPocket} object.
	 * @throws DbException if any.
	 */
	SqlPocket getSql() throws DbException;


	/**
	 * 是否存在至少一条符合条件的记录
	 *
	 * @return true 存在
	 * @throws DbException 数据库异常
	 */
	Mono<Boolean> exists() throws DbException;

	/**
	 * <p>getStringVector.</p> 获得单个字段
	 *
	 * @param field a {@link String} object.
	 * @return a {@link List} object.
	 * @throws DbException if any.
	 */
	List<String> getStringVector(String field) throws DbException;

	/**
	 * 获取字段field的长整型列表
	 *
	 * @param field 字段名
	 * @return List&lt;Long&gt; 长整形列表
	 * @throws DbException 数据库执行出错
	 */
	List<Long> getLongVector(String field) throws DbException;

	/**
	 * field = field +1
	 *
	 * @param field 字段名
	 * @return 影响的行数
	 * @throws DbException 数据库异常
	 */
	Mono<Integer> inc(String field) throws DbException;

	/**
	 * 更新字段fields为values指定值
	 *
	 * @param fields 表字段名
	 * @param values 字段值， 如果values是SqlString 类型，将不参与形成预编译语句的占位符，而是原样输出
	 * @return 影响的行数
	 * @throws DbException  数据库异常
	 */
	Mono<Integer> update(String[] fields, Object[] values) throws DbException;

	/**
	 * <p>update.</p>
	 *
	 * @param fields a {@link String} object.
	 * @param values a {@link Object} object.
	 * @return a int.
	 * @throws DbException if any.
	 */
	Mono<Integer> update(String fields, Object values)throws DbException;

}
