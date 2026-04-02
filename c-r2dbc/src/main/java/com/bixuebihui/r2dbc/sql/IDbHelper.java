package com.bixuebihui.r2dbc.sql;

import com.bixuebihui.jdbc.ParamsIterator;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * <p>IDbHelper interface.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public interface IDbHelper {


	/**
     * Example: final List names = new LinkedList();
     * dbHelper.executeQuery("SELECT user_mc FROM jiao_user", new
     * RowCallbackHandler() { public void processRow(ResultSet rs) throws
     * SQLException { names.add(rs.getString(1)); };
     *
     * @param sql
     *            SQL语句
     * @param handle
     *            处理接口
     */
    Mono<Void> executeQuery(String sql, RowCallbackHandler handle);

    /**
     * <p>executeQuery.</p>
     *
     * @param sql    a {@link String} object.
     * @param params an array of {@link Object} objects.
     * @param handle a {@link RowCallbackHandler} object.
     */
    <T> Mono<List<T>> executeQuery(String sql, Object[] params, RowCallbackHandler<T> handle);

    /**
     * must change the return value to {@code List<Map<String,Object>> }
     *
     * @param sql    SQL 语句
     * @param params SQL 参数
     * @return List of Map
     */
    Mono<List<Map<String, Object>>>  executeQuery(String sql, Object[] params);

    /**
     * <p>executeScalar.</p>
     *
     * @param strSql a {@link String} object.
     * @return a {@link Object} object.
     */
    Mono<Object> executeScalar(String strSql);

    /**
     * <p>executeScalar.</p>
     *
     * @param sql    a {@link String} object.
     * @param params an array of {@link Object} objects.
     * @return a {@link Object} object.
     */
    Mono<Object> executeScalar(String sql, Object[] params);

    /**
     * <p>executeScalar.</p>
     *
     * @param sql a {@link String} object.
     * @param params an array of {@link Object} objects.
     * @param cn a {@link Connection} object.
     * @return a {@link Object} object.
     */
    Mono<Object> executeScalar(String sql, Object[] params, Mono<Connection> cn);

    /**
     * must change the return value to {@code List<Map<String,Object>> }
     *
     * @param strSql SQL 语句
     * @return List of Map
     */
    Flux<Map<String, Object>> exeQuery(String strSql);

    /**
     * <p>getConnection.</p>
     *
     * @return a {@link Connection} object.
     */
    Publisher<? extends io.r2dbc.spi.Connection> getConnection();

    /**
     * <p>executeQuery.</p>
     *
     * @param sql    a {@link String} object.
     * @param params an array of {@link Object} objects.
     * @param handle a {@link RowMapperResultReader} object.
     * @param <T>    a T object.
     * @return a {@link List} object.
     */
    @NotNull <T> Mono<List<T>> executeQuery(String sql, Object[] params, RowMapperResultReader<T> handle);
    @NotNull <T> Mono<List<T>> executeQuery(String sql, Object[] params, RowMapper<T> handle);


    /**
     * <p>executeNoQuery.</p>
     *
     * @param strSql a {@link String} object.
     * @param params an array of {@link Object} objects.
     * @param cn     a {@link Connection} object.
     * @return a int.
     */
    Mono<Integer> executeNoQuery(String strSql, Object[] params, Connection cn);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql            a {@link String} object.
     * @param params         an array of {@link Object} objects.
     * @param targetSqlTypes an array of int.
     * @return a int.
     */
    Mono<Integer> executeNoQuery(String sql, Object[] params, Class[] targetSqlTypes);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql            a {@link String} object.
     * @param params         an array of {@link Object} objects.
     * @param targetSqlTypes an array of int.
     * @param conn           a {@link Connection} object.
     * @return a int.
     */
    Mono<Integer> executeNoQuery(String sql, Object[] params, Class[] targetSqlTypes, Mono<Connection> conn);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param strSql a {@link String} object.
     * @return a int.
     */
    Mono<Integer> executeNoQuery(String strSql);

    /**
     * return auto_increment id value
     * @param sql insert sql
     * @param params params
     * @param targetSqlTypes parameter's types
     * @param conn a {@link Connection }
     * @return
     */
    Mono<Long> insertAndFetchLastId(String sql, Object[] params,
                              Class[] targetSqlTypes, Mono<Connection> conn);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql    a {@link String} object.
     * @param params an array of {@link Object} objects.
     * @return a int.
     */
    Mono<Integer> executeNoQuery(String sql, Object[] params);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param strSql an array of {@link String} objects.
     * @return a int.
     */
    Mono<Integer> executeNoQuery(String[] strSql);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param strSql an array of {@link String} objects.
     * @param cn     a {@link Connection} object.
     * @return a int.
     */
    Mono<Integer> executeNoQuery(String[] strSql, Mono<Connection> cn);


    /**
     * 批处里数组, 用于Insert, delete或update
     *
     * @param strSql 参数化SQL语句
     * @param params 参数数组列表
     * @param cn     数据库连接,如为空,则自动获得
     * @return 成功执行数量
     */
    Mono<Integer> executeNoQueryBatch(String strSql, Iterable<Object[]> params, Mono<Connection> cn);

    /**
     * <p>executeNoQueryBatch.</p>
     *
     * @param strSql a {@link String} object.
     * @param params a {@link Iterable} object.
     * @return a int.
     */
    Mono<Integer> executeNoQueryBatch(String strSql, Iterable<Object[]> params);

    /**
     * <p>executeNoQueryBatch.</p>
     *
     * @param strSql a {@link String} object.
     * @param total  a int.
     * @param cur    a {@link ParamsIterator.CurrentParameters} object.
     * @param cn     a {@link Connection} object.
     * @return a int.
     */
    Mono<Integer> executeNoQueryBatch(String strSql, int total, ParamsIterator.CurrentParameters cur, Mono<Connection> cn);

    /**
     * 读写分离的连接获取，readOnly=true for select, readOnly=false for
     * update,insert,delete,create, alter etc.
     *
     * @param readOnly
     *            readOnly=true for select, readOnly=false for
	 *            update,insert,delete,create, alter etc
	 * @return 返回只读链接
	 */
	Publisher<? extends io.r2dbc.spi.Connection> getConnection(boolean readOnly) ;

    ConnectionFactory getDataSource();

    /**
	 * <p>setDataSource.</p>
     *
     * @param connManager a {@link ConnectionFactory} object.
	 */
	void setDataSource(ConnectionFactory connManager);

	/**
	 * <p>close.</p>
	 *
	 */
	void close() ;
}
