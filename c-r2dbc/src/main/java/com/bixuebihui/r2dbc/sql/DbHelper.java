package com.bixuebihui.r2dbc.sql;

import com.bixuebihui.DbException;
import com.bixuebihui.jdbc.ParamsIterator;
import io.r2dbc.spi.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * <p>DbHelper class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class DbHelper implements IDbHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DbHelper.class);
    private ConnectionFactory dataSource = null;

    /**
     * <p>Constructor for DbHelper.</p>
     */
    public DbHelper() {
        // use this constructor
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
    public Mono<Connection> getConnection() {
        return Mono.from(dataSource.create());
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
    public Mono<List<Integer>> executeUpdate(String[] sqls) {

        if (sqls == null || sqls.length < 1) {
            return Mono.just(Collections.emptyList());
        }

        Mono<Connection> cn = getConnection(false);
        return Mono.from(cn.flatMapMany(connection -> {

            Batch batch = connection.createBatch();
            for (int i = 0; i < sqls.length; i++) {
                batch.add(sqls[i]);
            }
            return Flux.from(batch.execute()).flatMap(result -> result.getRowsUpdated()).collectList();

        }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull
    Flux<Map<String, Object>> exeQuery(String sql) {
        return Flux.usingWhen(getConnection(true), cn ->
                        Flux.from(cn.createStatement(sql).execute())
                                .flatMap(DbHelper::resultSet2Vector),
                Connection::close);
    }

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sqls an array of {@link String} objects.
     * @param cn     a {@link Connection} object.
     * @return a int.
     */
    @Override
    public Mono<Integer> executeNoQuery(String[] sqls, @NotNull Mono<Connection> cn) {
        return Mono.from(cn.flatMapMany(connection -> {

            Batch batch = connection.createBatch();
            for (int i = 0; i < sqls.length; i++) {
                if (!StringUtils.isNotBlank(sqls[i])) {
                    batch.add(sqls[i]);
                }
            }
            return Flux.from(batch.execute()).flatMap(Result::getRowsUpdated).reduceWith(() -> 0, Integer::sum);
        }));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Integer> executeNoQuery(String sql, Object[] params, Connection cn) {
            Statement p = cn.createStatement(sql);
            fillParameter(p, params, null);
            return Mono.from(Flux.from(p.execute()).flatMap(Result::getRowsUpdated));
    }

    /**
     * {@inheritDoc}
     * <p>
     * 批处里数组, 用于Insert, delete或update
     */
    @Override
    public Mono<Integer> executeNoQueryBatch(String sql, Iterable<Object[]> params,
                                             Mono<Connection> cn) {

        return Mono.from(cn.flatMapMany(connection -> {
            Statement p = connection.createStatement(sql);
            params.forEach(it -> {
                fillParameter(p, it, null);
                p.add();
            });
            return Flux.from(p.execute()).flatMap(Result::getRowsUpdated).reduceWith(() -> 0, Integer::sum);
        }));
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
        AtomicBoolean localCn = new AtomicBoolean(true);
        return Mono.usingWhen(
                cn.flatMap(cn1 -> {
                    localCn.set(false);
                    return Mono.just(cn1);
                }).switchIfEmpty(getConnection(true)),

                c -> Flux.from(c.createStatement(sql).execute())
                        .log()
                        .map(result -> result.map((row, meta) -> row.get(0))
                        ).flatMap(f -> Mono.from(f)).next(), //.doOnNext(data -> LOG.info("value: {}", data)),
                c -> {
                    if (localCn.get()) {
                        c.close();
                    }
                    return Mono.empty();
                }
        );


    }

    /**
     * <p>resultSet2Vector.</p>
     *
     * @param rs a {@link Result} object.
     * @return a {@link List} object.
     */
    public static Flux<Map<String, Object>> resultSet2Vector(Result rs) {
        return Flux.from(rs.map((row, rowMetadata) -> {
            int iColumnNumber = rowMetadata.getColumnNames().size();

            Map<String, Object> mapColumn = new HashMap<>(32);
            for (int i = 0; i < iColumnNumber; i++) {

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
        return executeScalar(sql, params, Mono.empty());
    }

    /**
     * <p>fillParameter.</p>
     *
     * @param p      a {@link Statement} object.
     * @param params an array of {@link Object} objects.
     */
    public static void fillParameter(Statement p, Object[] params) {
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

        AtomicBoolean localCn= new AtomicBoolean(true);
        Mono<Connection>  conn = cn.flatMap(cn1 -> {localCn.set(false); return Mono.just(cn1);}).switchIfEmpty( getConnection(true));

        Flux<Object> v = cn.flatMapMany(connection -> {
            Statement p = connection.createStatement(sql);
            fillParameter(p, params, null);
            return Flux.from(p.execute())
                    .doFinally(signalType -> {
                        if (localCn.get()) {
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
    public static void fillParameter(Statement p, Object[] params,
                              Class[] targetSqlTypes) {

        if (ArrayUtils.isEmpty(params)) {
            return;
        }

        if (ArrayUtils.isNotEmpty(targetSqlTypes) && targetSqlTypes.length != params.length) {
            throw new DbException(
                    "Intenal error: The params and the targetSqlTypes must have same length!");
        }


        if (ArrayUtils.isNotEmpty(targetSqlTypes)) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] == null) {
                    p.bindNull(i, targetSqlTypes[i]);
                } else {
                    p.bind(i, params[i]);
                }
            }
        } else {
            for (int i = 0; i < params.length; i++) {
                Object pa = params[i];
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
                } else if (pa instanceof java.sql.Time) {
                    p.bind(i, pa);
                } else if (pa instanceof java.util.Date) {
                    p.bind(i, new java.sql.Timestamp(((java.util.Date) pa).getTime()));
                }  else if (pa instanceof Double) {
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

        return Mono.usingWhen(getConnection(true), cn -> {
            Statement p = cn.createStatement(sql);
            if (params != null && params.length > 0) {
                fillParameter(p, params);
            }
            return Mono.from(p.execute()).map(DbHelper::resultSet2Vector).flatMap(Flux::collectList);
        }, cn -> cn.close());
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
    public Mono<Integer> executeNoQuery(String sql, Object[] params, Class[] targetSqlTypes) {
        return executeNoQuery(sql, params, targetSqlTypes, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Integer> executeNoQuery(String sql, Object[] params,
                              Class[] targetSqlTypes, Mono<Connection> cn) {
        return Mono.from((cn!=null?cn: getConnection()).flatMapMany(connection -> {
            Statement p = connection.createStatement(sql);

                fillParameter(p, params, targetSqlTypes);
                p.add();

            return Flux.from(p.execute()).flatMap(Result::getRowsUpdated).reduceWith(() -> 0, Integer::sum);
        }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Long> insertAndFetchLastId(String sql, Object[] params,
                                           Class[] targetSqlTypes, Mono<Connection> conn) {
        boolean localCn = conn == null;
        Mono<Connection> cn = localCn ? getConnection(false) : conn;
        return Mono.from(cn.flatMapMany(connection -> {
            Statement p = connection.createStatement(sql);

            if (ArrayUtils.isNotEmpty(params)) {
                fillParameter(p, params, targetSqlTypes);
            }
            return Flux.from(p.returnGeneratedValues().execute()).flatMap(result ->
                    result.map((row, meta) -> Long.parseLong(Objects.requireNonNull(row.get(0)).toString()))
            );
        }));

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
        return executeNoQuery(sql, params, new Class[0]);
    }

    /**
     * <p>executeQuery.</p>
     *
     * @param sql    a {@link String} object.
     * @param params an array of {@link Object} objects.
     * @param handle a {@link RowCallbackHandler} object.
     */
    @Override
    public <T> Mono<List<T>> executeQuery(String sql, Object[] params,
                             RowCallbackHandler<T> handle) {
        return Mono.usingWhen(getConnection(true),
                cn -> {
                    Statement p = cn.createStatement(sql);
                    if (params != null && params.length > 0) {
                        fillParameter(p, params, null);
                    }
                    return Mono.from(p.execute()).flatMap(handle::processRow);
                },
                c -> c.close()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull <T> Mono<List<T>> executeQuery(String sql, Object[] params,
                                             @NotNull RowMapperResultReader<T> handle) {
        Mono<Connection> conn =  getConnection(true);
        return conn.flatMap( cn -> {

                    Statement p = cn.createStatement(sql);
                    if (ArrayUtils.isNotEmpty(params)) {
                        fillParameter(p, params, null);
                    }
                    return Mono.from(p.execute())
                            .flatMap(handle::processResult)
                            .doFinally(c -> cn.close());

                }
        );

    }

    @Override
    public @NotNull <T> Mono<List<T>> executeQuery(String sql, Object[] params,
                                             @NotNull RowMapper<T> handle) {
        return executeQuery(sql, params,
                new RowMapperResultReader<>(handle));
    }

}
