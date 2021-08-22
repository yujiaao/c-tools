package com.bixuebihui.r2dbc.sql;

import com.bixuebihui.DbException;
import io.r2dbc.spi.*;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>DbHelper class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class DbHelper implements IDbHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DbHelper.class);
    private Mono<Connection> connection = null;
    private ConnectionFactory dataSource = null;

    /**
     * <p>Constructor for DbHelper.</p>
     */
    public DbHelper() {
        // use this constructor
    }

    /**
     * for test only, not use in real application
     *
     * @param cn db connection
     */
    public DbHelper(Mono<Connection> cn) {
        connection = cn;
    }

    /**
     * <p>freeConnection.</p>
     *
     * @param cn a {@link Connection} object.
     */
    public static void freeConnection(Mono<Connection> cn) {
        cn.flatMapMany(Connection::close).subscribe();
    }


    /**
     * <p>close.</p>
     *
     * @param conn a {@link Connection} object.
     */
    public static void close(Mono<Connection> conn) {
        if (conn != null) {
            freeConnection(conn);
        }
    }

    private static void dumpSql(Exception ex, String sql, Object[] params) {
        LOG.warn(ex.getMessage());
        LOG.warn("===" + sql);
        if (params != null) {
            for (Object o : params) {
                LOG.warn("===" + o);
            }
        }
    }

    private static void dumpSql(Exception ex, String[] sql) {
        LOG.warn(ex.getMessage());
        for (String s : sql) {
            LOG.warn("==" + s);
        }
    }

    public static String clob2Str(final Clob source) {
        return Mono.from(source.stream()).map(CharSequence::toString).block();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionFactory getDataSource() {
        return dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataSource(ConnectionFactory dataSource) {
        this.dataSource = dataSource;
    }

    // 用于事务处理

    /**
     * <p>Getter for the field <code>connection</code>.</p>
     *
     * @return a {@link Connection} object.
     */
    @Override
    public Mono<Connection> getConnection() throws DbException {
        return connection != null ? connection : Mono.from(dataSource.create());
    }

    // 用于事务处理

    /**
     * {@inheritDoc}
     * <p>
     * this method for subclass override
     *
     * @return
     */
    @Override
    public Mono<Connection> getConnection(boolean readOnly) {
        return getConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (connection != null) {
            freeConnection(connection);
        }
    }

    /**
     * do not use this if you don't know what to do...
     * because it leading to resource leaking
     *
     * @param sql sql
     * @param cn  connection
     * @return Flux<Result>
     */
    @Deprecated
    public Flux<Result> executeQuery(String sql, @NotNull Connection cn) {
        return Flux.from(cn.createStatement(sql).execute());
    }

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql a {@link String} object.
     * @return a int.
     */
    @Override
    public Mono<Integer> executeNoQuery(String sql) {
        Mono<Connection> cn = this.getConnection(false);

        Flux<Integer> v = cn
                .flatMapMany(connection ->
                        Flux.from(connection.createStatement(sql).execute())
                                .doFinally(signalType -> connection.close()))
                .flatMap(Result::getRowsUpdated);

        return Mono.from(v);
    }

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql a {@link String} object.
     * @param cn  a {@link Connection} object.
     * @return a int.
     */
    public Mono<Integer> executeNoQuery(String sql, @NotNull Connection cn) {
        Flux<Integer> v = Flux.from(cn.createStatement(sql).execute())
                .flatMap(Result::getRowsUpdated);
        return Mono.from(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Integer> executeNoQueryBatch(String strSql, Iterable<Object[]> params) {
        return executeNoQueryBatch(strSql, params, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Integer> executeNoQueryBatch(String strSql, int total, ParamsIterator.CurrentParameters cur, Mono<Connection> cn) {
        return executeNoQueryBatch(strSql, new ParamsIterator(total, cur), cn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Object> executeScalar(String strSql) {
        return executeScalarSession(strSql, Mono.empty());
    }

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql an array of {@link String} objects.
     * @return a count of influenced records.
     */
    @Override
    public Mono<Integer> executeNoQuery(String[] sql) {
        Mono<Connection> cn = this.getConnection(false);

        return Mono.from(cn.flatMapMany(connection -> {
            connection.beginTransaction();
            Batch batch = connection.createBatch();

            for (String s : sql) {
                if (StringUtils.isNotBlank(s)) {
                    batch.add(s);
                }
            }

            Mono<Integer> v = Flux.from(batch.execute()).flatMap(Result::getRowsUpdated).reduceWith(() -> 0, Integer::sum)
                    .doFinally(signalType -> {
                        connection.commitTransaction();
                        connection.close();
                    });
            return v;
        }));
    }

    /**
     * 批量在事条里执行SQL语句数组
     *
     * @param sqls 批量在事条里执行SQL语句数组
     * @return 每一个SQL执行结果的数组
     */
    public int[] executeUpdate(String[] sqls) {

        if (sqls == null || sqls.length < 1) {
            return new int[0];
        }

        Mono<Connection> cn = getConnection(false);
        return cn.flatMapMany(connection -> {

            int[] j = new int[sqls.length];
            int step = -1;

            connection.setAutoCommit(false);
                    for (int i = 0; i < sqls.length; i++) {
                        Statement oStmt = cn.createStatement(sqls[i]);
                        j[i] = oStmt.execute();
                        step = i;
                    }
            connection.commit();


            return j;
        })
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull
    Flux<Map<String, Object>> exeQuery(String sql) {
        Mono<Connection> cn = getConnection(true);
        return cn.flatMapMany(connection -> {
                         Statement stmt = connection.createStatement(sql);
                        Result rset = stmt.execute()){
                        return resultSet2Vector(rset);
                    });
    }

    /**
     * <p>executeNoQuery.</p>
     *
     * @param strSql an array of {@link String} objects.
     * @param cn     a {@link Connection} object.
     * @return a int.
     */
    @Override
    public Mono<Integer> executeNoQuery(String[] strSql, @NotNull Mono<Connection> cn) {
        int n = 0;
        try (Statement stmt = cn.createStatement()) {
            for (String s : strSql) {
                if (!StringUtils.isNotBlank(s)) {
                    continue;
                }
                n += stmt.executeUpdate(s);
            }
        } catch (SQLException ex) {
            dumpSql(ex, strSql);
            throw new DbException(ex);
        }
        return n;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Integer> executeNoQuery(String sql, Object[] params, Mono<Connection> cn) {
        return Mono.from(cn.flatMapMany(connection -> {
            Statement p = connection.createStatement(sql);
            this.fillParameter(p, params, null);
            return Flux.from(p.execute()).map(result -> result.getRowsUpdated());
        }));
    }

    /**
     * {@inheritDoc}
     * <p>
     * 批处里数组, 用于Insert, delete或update
     */
    @Override
    public Mono<Integer> executeNoQueryBatch(String sql, Iterable<Object[]> params,
                                             Mono<Connection> cn) {

        boolean needRelease = false;
        if (cn == null) {
            cn = this.getConnection(false);
            needRelease = true;
        }
        int n;
        try (Statement p = cn.prepareStatement(sql)) {
            for (Object[] param : params) {
                this.fillParameter(p, param, null);
                p.addBatch();
            }
            int[] updateCounts = p.executeBatch();
            n = updateCounts.length;
        } catch (SQLException ex) {
            for (Object[] ps : params) {
                dumpSql(ex, sql, ps);
            }
            throw new DbException(ex);
        } finally {
            if (needRelease) {
                close(cn);
            }
        }
        return n;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> executeQuery(String sql, RowCallbackHandler handle) {
        Mono<Connection> cn = this.getConnection(true);
        return Mono.from(cn.flatMapMany(connection -> {
            Statement stmt = connection.createStatement(sql);
            return Flux.from(stmt.execute()).map(result -> {
                handle.processRow(result);
                return Mono.empty();
            });
        })).then();
    }

    /**
     * (non-Javadoc)
     *
     * @param cn  connection
     * @param sql sql语句
     * @return single object, may be integer or string
     * @see IDbHelper#executeScalar(String)
     */
    public Mono<Object> executeScalarSession(String sql, Mono<Connection> cn) {
        boolean localCn = cn.map(connection -> connection == null).block();
        cn = localCn ? this.getConnection(true) : cn;
        Flux<Object> v = cn.flatMapMany(connection ->
                Flux.from(connection.createStatement(sql).execute())
                        .doFinally(signalType -> {
                            if (localCn) {
                                connection.close();
                            }
                        }).map(result -> result.map((row, meta) -> row.get(0))));

        return Mono.from(v);
    }

    /**
     * <p>resultSet2Vector.</p>
     *
     * @param rs a {@link Result} object.
     * @return a {@link List} object.
     */
    public Flux<Map<String, Object>> resultSet2Vector(Result rs) {
        return Flux.from(rs.map((row, rowMetadata) -> {
            int iColumnNumber = rowMetadata.getColumnNames().size();

            Map<String, Object> mapColumn = new HashMap<>(32);
            for (int i = 1; i <= iColumnNumber; i++) {

                ColumnMetadata colMeta = rowMetadata.getColumnMetadata(i);

                String colName = colMeta.getName();

                if (mapColumn.containsKey(colName) && row.get(i) == null) {
                    //同一列名的已存在且不为空，则保留当前值，用于多表联合查询，但后表无记录的情况
                    continue;
                }

                mapColumn.put(colName, row.get(i));//, colMeta.getJavaType()));
            }
            return mapColumn;

        }));
    }


    /**
     * <p>executeScalar.</p>
     *
     * @param sql    a {@link String} object.
     * @param params an array of {@link Object} objects.
     * @return a {@link Object} object.
     */
    @Override
    public Mono<Object> executeScalar(String sql, Object[] params) {
        return executeScalar(sql, params, null);
    }

    /**
     * <p>fillParameter.</p>
     *
     * @param p      a {@link Statement} object.
     * @param params an array of {@link Object} objects.
     */
    public void fillParameter(Statement p, Object[] params) {
        fillParameter(p, params, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Object> executeScalar(String sql, Object[] params, Mono<Connection> cn) {
        if (params == null || params.length == 0) {
            return executeScalarSession(sql, cn);
        }

        boolean localCn = cn == null;
        cn = localCn ? this.getConnection(true) : cn;
        Flux<Object> v = cn.flatMapMany(connection -> {
            Statement p = connection.createStatement(sql);
            fillParameter(p, params, null);
            return Flux.from(p.execute())
                    .doFinally(signalType -> {
                        if (localCn) {
                            connection.close();
                        }
                    });
        }).map(result -> result.map((row, meta) -> row.get(0)));

        return Mono.from(v);
    }

    /**
     * <p>fillParameter.</p>
     *
     * @param p              a {@link Statement} object.
     * @param params         an array of {@link Object} objects.
     * @param targetSqlTypes an array of int objects.
     */
    public void fillParameter(Statement p, Object[] params,
                              Class[] targetSqlTypes) {

        if (ArrayUtils.isEmpty(params)) {
            return;
        }

        if (ArrayUtils.isNotEmpty(targetSqlTypes) && targetSqlTypes.length != params.length) {
            throw new DbException(
                    "Intenal error: The params and the targetSqlTypes must have same length!");
        }


        if (ArrayUtils.isNotEmpty(targetSqlTypes)) {
            for (int i = 1; i <= params.length; i++) {
                if (params[i - 1] == null) {
                    p.bindNull(i, targetSqlTypes[i - 1]);
                } else {
                    p.bind(i, params[i - 1]);
                }
            }
        } else {
            for (int i = 1; i <= params.length; i++) {
                Object pa = params[i - 1];
                if (pa == null) {
                    p.bindNull(i, String.class);
                    LOG.debug("不支持空数据(参数["
                            + i
                            + "])! Null is not support in DbHelper.executeNoQuery(String sql, Object[] params), there may be error!");
                } else if (pa instanceof Integer) {
                    p.bind(i, pa);
                } else if (pa instanceof Long) {
                    p.bind(i, pa);
                } else if (pa instanceof Timestamp) {
                    p.bind(i, pa);
                } else if (pa instanceof Date) {
                    p.bind(i, pa);
                } else if (pa instanceof java.util.Date) {
                    p.bind(i, new Date(((java.util.Date) pa).getTime()));
                } else if (pa instanceof Double) {
                    p.bind(i, pa);
                } else if (pa instanceof String) {
                    p.bind(i, pa);
                } else if (pa instanceof ClobString) {
                    p.bind(i, pa.toString());
                } else {
                    p.bind(i, pa);
                }
            }
        }
    }

    /**
     * 用于执行带参数的非查询语句
     *
     * @param sql    参数化sql语句
     * @param params 参数
     * @return (字段)) 结果集
     */
    @Override
    public Mono<List<Map<String, Object>>> executeQuery(String sql, Object[] params) {
        Mono<Connection> cn = getConnection(true);
        return Mono.from(cn.flatMapMany(connection -> {
            Statement p = connection.createStatement(sql);
            if (params != null && params.length > 0) {
                fillParameter(p, params);

            }
            return p.execute();
        }).map((row, meta) -> this.resultSet2Vector(row)));
    }

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql            a {@link String} object.
     * @param params         an array of {@link Object} objects.
     * @param targetSqlTypes an array of int objects.
     * @return a int.
     */
    @Override
    public int executeNoQuery(String sql, Object[] params, Class[] targetSqlTypes) {
        return executeNoQuery(sql, params, targetSqlTypes, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeNoQuery(String sql, Object[] params,
                              Class[] targetSqlTypes, Mono<Connection> conn) {
        boolean localCn = conn == null;
        Connection cn = localCn ? getConnection(false) : conn;
        try (Statement p = ArrayUtils.isNotEmpty(params) ? cn.prepareStatement(sql) : cn.createStatement()) {
            if (p instanceof Statement) {
                fillParameter((Statement) p, params, targetSqlTypes);
                ((Statement) p).execute();
            } else {
                p.execute(sql);
            }
            return p.getUpdateCount();
        } catch (SQLException ex) {
            dumpSql(ex, sql, params);
            throw new DbException(ex);
        } finally {
            if (localCn) {
                close(cn);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long insertAndFetchLastId(String sql, Object[] params,
                                     Class[] targetSqlTypes, Mono<Connection> conn) {
        boolean localCn = conn == null;
        Connection cn = localCn ? getConnection(false).block() : conn.block();
        try (Statement p = cn.createStatement(sql) {

            if (ArrayUtils.isNotEmpty(params)) {
                fillParameter(p, params, targetSqlTypes);
            }

            p.execute();

            Flux.from(p.executeReturnGeneratedKeys()).map(rs -> {
                    return Long.parseLong(rs.get(0).toString());
            });
        }
    }

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql    a {@link String} object.
     * @param params an array of {@link Object} objects.
     * @return a int.
     */
    @Override
    public Mono<Integer> executeNoQuery(String sql, Object[] params) {
        return executeNoQuery(sql, params, new int[0]);
    }

    /**
     * <p>executeQuery.</p>
     *
     * @param sql    a {@link String} object.
     * @param params an array of {@link Object} objects.
     * @param handle a {@link RowCallbackHandler} object.
     */
    @Override
    public Mono<Void> executeQuery(String sql, Object[] params,
                             RowCallbackHandler handle) {
        Connection cn = getConnection(true).block();
        Statement p = cn.createStatement(sql) ;
            if(params!=null && params.length>0)P
                fillParameter( p, params, null);
            }
            return Flux.from (p.execute()).map(rs ->
                    handle.processRow(rs))).then();


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull <T> Flux<T> executeQuery(String sql, Object[] params,
                                             @NotNull RowMapperResultReader<T> handle) {
        Mono<Connection> cn = getConnection(true);
        return cn.flatMapMany(connection -> {
            Statement p = connection.create(sql);
            if (params != null && params.length > 0) {
                fillParameter((Statement) p, params, null);
            }
            return Flux.from(p.execute()).map(rs -> handle.processResult(rs));
        }
    }

    @Override
    public @NotNull <T> List<T> executeQuery(String sql, Object[] params,
                                             @NotNull RowMapper<T> handle) {
        return executeQuery(sql, params,
                new RowMapperResultReader<>(handle));
    }

}
