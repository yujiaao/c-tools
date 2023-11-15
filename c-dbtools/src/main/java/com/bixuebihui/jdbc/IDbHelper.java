package com.bixuebihui.jdbc;

import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.tuple.Pair;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    void executeQuery(String sql, RowCallbackHandler handle);

    /**
     * <p>executeQuery.</p>
     *
     * @param sql    a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @param handle a {@link RowCallbackHandler} object.
     */
    void executeQuery(String sql, Object[] params, RowCallbackHandler handle);

    /**
     * must change the return value to {@code List<Map<String,Object>> }
     *
     * @param sql    SQL 语句
     * @param params SQL 参数
     * @return List of Map
     */
    List<Map<String, Object>> executeQuery(String sql, Object[] params);

    /**
     * <p>executeScalar.</p>
     *
     * @param strSql a {@link java.lang.String} object.
     * @return a {@link java.lang.Object} object.
     */
    Object executeScalar(String strSql);

    /**
     * <p>executeScalar.</p>
     *
     * @param sql    a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @return a {@link java.lang.Object} object.
     */
    Object executeScalar(String sql, Object[] params);

    /**
     * <p>executeScalar.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @param cn a {@link java.sql.Connection} object.
     * @return a {@link java.lang.Object} object.
     */
    Object executeScalar(String sql, Object[] params, Connection cn);

    /**
     * must change the return value to {@code List<Map<String,Object>> }
     *
     * @param strSql SQL 语句
     * @return List of Map
     */
    List<Map<String, Object>> exeQuery(String strSql);

    /**
     * <p>getConnection.</p>
     *
     * @return a {@link java.sql.Connection} object.
     */
    Connection getConnection();

    /**
     * <p>executeQuery.</p>
     *
     * @param sql    a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @param handle a {@link RowMapperResultReader} object.
     * @param <T>    a T object.
     * @return a {@link java.util.List} object.
     */
    @NotNull <T> List<T> executeQuery(String sql, Object[] params, RowMapperResultReader<T> handle);

    @NotNull <T> List<T> executeQuery(String sql, Object[] params,
                                      @NotNull RowMapperResultReader<T> handle, Set<String> returnFields);

    @NotNull <T> List<T> executeQuery(String sql, Object[] params, RowMapper<T> handle);
    @NotNull <T> List<T> executeQuery(String sql, Object[] params, RowMapper<T> handle, Set<String> returnFields);

    /**
     * sql must in the form of "select count(*) from table where ...; select * from table where ... limit 0,10"
     * attention: allowMultiQueries=true must be set in jdbc url
     * @param sql  sql must in the form of "select count(*) from table where ...; select * from table where ... limit 0,10"
     * @param params params for count sql and select sql
     * @param handle handle for select sql
     * @param returnFields return fields for select sql
     * @return Pair of count and list
     * @param <T> a T object.
     */
    @NotNull <T> Pair<Long,List<T>> pageWithCount(String sql, Object[] params, RowMapper<T> handle, Set<String> returnFields);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param strSql a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @param cn a {@link java.sql.Connection} object.
     * @return a int.
     */
    int executeNoQuery(String strSql, Object[] params, Connection cn);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @param targetSqlTypes an array of int.
     * @return a int.
     */
    int executeNoQuery(String sql, Object[] params, int[] targetSqlTypes);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @param targetSqlTypes an array of int.
     * @param conn a {@link java.sql.Connection} object.
     * @return a int.
     */
    int executeNoQuery(String sql, Object[] params, int[] targetSqlTypes, Connection conn);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param strSql a {@link java.lang.String} object.
     * @return a int.
     */
    int executeNoQuery(String strSql);

    long insertAndFetchLastId(String sql, Object[] params,
                              int[] targetSqlTypes, Connection conn);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @return a int.
     */
    int executeNoQuery(String sql, Object[] params);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param strSql an array of {@link java.lang.String} objects.
     * @return a int.
     */
    int executeNoQuery(String[] strSql);

    /**
     * <p>executeNoQuery.</p>
     *
     * @param strSql an array of {@link java.lang.String} objects.
     * @param cn a {@link java.sql.Connection} object.
     * @return a int.
     */
    int executeNoQuery(String[] strSql, Connection cn);


    /**
     * 批处里数组, 用于Insert, delete或update
     *  also note parameter rewriteBatchedStatements=true
     *  in jdbc url
     *  @see <a href="https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html">mysql connector-j</a>
     * @param strSql
     *            参数化SQL语句
     * @param params
     *            参数数组列表
     * @param cn
     *            数据库连接,如为空,则自动获得
     * @return 成功执行数量
     */
    int executeNoQueryBatch(String strSql, Iterable<Object[]> params, Connection cn);

    /**
     * <p>executeNoQueryBatch.</p>
     *
     * @param strSql a {@link java.lang.String} object.
     * @param params a {@link java.lang.Iterable} object.
     * @return a int.
     */
    int executeNoQueryBatch(String strSql, Iterable<Object[]> params);

    /**
     * <p>executeNoQueryBatch.</p>
     *
     * @param strSql a {@link java.lang.String} object.
     * @param total  a int.
     * @param cur    a {@link ParamsIterator.CurrentParameters} object.
     * @param cn     a {@link java.sql.Connection} object.
     * @return a int.
     */
    int executeNoQueryBatch(String strSql, int total, ParamsIterator.CurrentParameters cur, Connection cn);

    /**
     * 读写分离的连接获取，readOnly=true for select, readOnly=false for
     * update,insert,delete,create, alter etc.
     *
     * @param readOnly
     *            readOnly=true for select, readOnly=false for
	 *            update,insert,delete,create, alter etc
	 * @return 返回只读链接
	 */
	Connection getConnection(boolean readOnly) ;

    DataSource getDataSource();

    /**
	 * <p>setDataSource.</p>
     *
     * @param connManager a {@link javax.sql.DataSource} object.
	 */
	void setDataSource(DataSource connManager);

	/**
	 * <p>close.</p>
	 *
	 */
	void close() ;

}
