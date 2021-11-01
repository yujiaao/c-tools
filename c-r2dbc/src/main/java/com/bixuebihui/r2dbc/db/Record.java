package com.bixuebihui.r2dbc.db;


import com.bixuebihui.DbException;
import com.bixuebihui.db.GroupFunction;
import com.bixuebihui.db.SqlPocket;
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
	Mono<List<Object>>  getVector(String field) throws DbException;


	/**
	 * <p>findAll.</p>
	 * 查询多条
	 *
	 * @return a {@link List} object.
	 * @throws DbException if any.
	 */
	Mono<List<T>> findAll() throws DbException;

	/**
	 * <p>findAll.</p>
	 *
	 * @param clz the custom return type, for example a entity type that exclude return for blob/clob fields,
	 *            used in combination with the .fields() method.
	 * @return a {@link List} object.
	 * @throws DbException if any.
	 */
	<K> Mono<List<K>> findAll(Class<K> clz) throws DbException;


	/**
	 * select one record from db
	 * @return a T object.
	 * @throws DbException if any.
	 */
	Mono<T> find() throws DbException;


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
	 */
	Mono<Object> get(String field) ;

	/**
	 * <p>count.</p> 获得数量
	 *
	 * @return a int.
	 */
	Mono<Integer> count() ;

	/**
	 * <p>countValue.</p> 获得数量
	 *
	 * @param field a {@link String} object.
	 * @param fun   a {@link GroupFunction} object.
	 * @return a {@link CountValue} object.
	 */
	Mono<CountValue> countValue(String field, GroupFunction fun);

	/**
	 * <p>countObject.</p>
	 *
	 * @param field      a {@link String} object.
	 * @param fun        a {@link GroupFunction} object.
	 * @param objectType a {@link Class} object.
	 * @param <K>        a K object.
	 * @return a {@link CountObject} object.
	 */
	<K> Mono<CountObject<K>> countObject(String field, GroupFunction fun, Class<K> objectType);

	/**
	 * <p>countSum.</p> 获得数量
	 *
	 * @param field a {@link String} object.
	 * @return a {@link CountValue} object.
	 * @throws DbException if any.
	 */
	Mono<CountValue> countSum(String field) throws DbException;

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
	Mono<List<String>> getStringVector(String field) throws DbException;

	/**
	 * 获取字段field的长整型列表
	 *
	 * @param field 字段名
	 * @return List&lt;Long&gt; 长整形列表
	 */
	Mono<List<Long>>  getLongVector(String field);

	/**
	 * field = field +1
	 *
	 * @param field 字段名
	 * @return 影响的行数
	 */
	Mono<Integer> inc(String field);

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
