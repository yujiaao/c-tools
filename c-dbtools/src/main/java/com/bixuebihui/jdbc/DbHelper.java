package com.bixuebihui.jdbc;

import com.bixuebihui.DbException;
import com.bixuebihui.util.DbUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.*;
import java.sql.Date;
import java.util.*;


/**
 * <p>DbHelper class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class DbHelper implements IDbHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DbHelper.class);
    private Connection connection = null;
    private DataSource dataSource = null;

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
    public DbHelper(Connection cn) {
        connection = cn;
    }

    /**
     * <p>freeConnection.</p>
     *
     * @param cn a {@link java.sql.Connection} object.
     */
    public static void freeConnection(Connection cn) {
        try {
            cn.close();
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    /**
     * public java.sql.Result exeSql(String strSQl) {
     * <p>
     * return new Result(); }
     *
     * @param stmt statement to be closed
     */
    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }
    }

    /**
     * <p>close.</p>
     *
     * @param rs a {@link java.sql.ResultSet} object.
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }
    }

    /**
     * <p>close.</p>
     *
     * @param conn a {@link java.sql.Connection} object.
     */
    public static void close(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                freeConnection(conn);
            }
        } catch (SQLException e) {
            LOG.error("", e);
        }
    }

    private static void dumpSql(SQLException ex, String sql, Object[] params) {
        if(LOG.isWarnEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append(ex.getMessage()).append("\n");
            sb.append("===" + sql).append("\n");
            if (params != null) {
                for (Object o : params) {
                    sb.append("===" + o).append("\n");
                }
            }
            LOG.warn(sb.toString());
        }
    }

    private static void dumpSql(Exception ex, String[] sql) {
        if(LOG.isWarnEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append(ex.getMessage()).append("\n");
        for (String s : sql) {
            sb.append("==" + s).append("\n");
        }
            LOG.warn(sb.toString());
        }
    }

    public static String clob2Str(Clob clobObject) {
        InputStream in;
        try {
            in = clobObject.getAsciiStream();
            StringWriter w = new StringWriter();
            IOUtils.copy(in, w, Charset.defaultCharset());
            return w.toString();
        } catch (SQLException | IOException e) {
            throw new DbException(e);
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // 用于事务处理

    /**
     * <p>Getter for the field <code>connection</code>.</p>
     *
     * @return a {@link java.sql.Connection} object.
     */
    @Override
    public Connection getConnection() throws DbException {
        try {
            return connection != null ? connection : dataSource.getConnection();
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    // 用于事务处理

    /**
     * {@inheritDoc}
     * <p>
     * this method for subclass override
     */
    @Override
    public Connection getConnection(boolean readOnly) {
        return getConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * do not use this if you don't know what to do...
     * because it leading to resource leaking
     *
     * @param sql sql
     * @param cn  connection
     * @return ResultSet must closed after using
     * @deprecated ResultSet must closed, before close the connection, is not recommend to use this
     */
    @Deprecated
    public ResultSet executeQuery(String sql, @NotNull Connection cn) {
        ResultSet rs;
        try (Statement stmt = cn.createStatement()) {
            rs = stmt.executeQuery(sql);
        } catch (SQLException ex) {
            dumpSql(ex, sql, null);
            throw new DbException(ex);
        }
        return rs;
    }

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @return a int.
     */
    @Override
    public int executeNoQuery(String sql) {
        Connection cn = this.getConnection(false);
        try (Statement stmt = cn.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            dumpSql(ex, sql, null);

            throw new DbException(ex);
        } finally {
            close(cn);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bixuebihui.zichan.dal.IDbHelper#executeQuery(java.lang.String,
     * com.bixuebihui.util.db.RowCallbackHandler)
     */

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @param cn  a {@link java.sql.Connection} object.
     * @return a int.
     */
    public int executeNoQuery(String sql, @NotNull Connection cn) {
        try (Statement stmt = cn.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            dumpSql(ex, sql, null);
            throw new DbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeNoQueryBatch(String strSql, Iterable<Object[]> params) {
        return executeNoQueryBatch(strSql, params, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeNoQueryBatch(String strSql, int total, ParamsIterator.CurrentParameters cur, Connection cn) {
        return executeNoQueryBatch(strSql, new ParamsIterator(total, cur), cn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object executeScalar(String strSql) {
        return executeScalarSession(strSql, null);
    }

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql an array of {@link java.lang.String} objects.
     * @return a count of influenced records.
     */
    @Override
    public int executeNoQuery(String[] sql) {
        Connection cn = this.getConnection(false);
        try {
            cn.setAutoCommit(false);
        } catch (SQLException e) {
            LOG.warn("", e);
        }

        int n = 0;
        try (Statement stmt = cn.createStatement()) {
            for (String s : sql) {
                if (StringUtils.isNotBlank(s)) {
                    n += stmt.executeUpdate(s);
                }
            }
            cn.commit();
        } catch (SQLException ex) {
            try {
                cn.rollback();
            } catch (SQLException e) {
                LOG.warn("", e);
            }
            dumpSql(ex, sql);
            throw new DbException(ex);
        } finally {
            try {
                cn.setAutoCommit(true);
            } catch (SQLException e) {
                LOG.warn("", e);
            }
            close(cn);
        }
        return n;
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


        Connection cn = getConnection(false);

        int[] j = new int[sqls.length];
        int step = -1;
        try {
            cn.setAutoCommit(false);
            try (Statement oStmt = cn.createStatement()) {
                for (int i = 0; i < sqls.length; i++) {
                    j[i] = oStmt.executeUpdate(sqls[i]); //NOSONAR
                    step = i;
                }
                cn.commit();
            }
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            DbException ex = new DbException("执行SQL语句出错"
                    + (step < 0 ? "" : ":" + sqls[step])
                    + " DbHelper.ExecuteUpdate" + e.getMessage());
            dumpSql(ex, sqls);

            throw ex;

        } finally {
            close(cn);
        }
        return j;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull
    List<Map<String, Object>> exeQuery(String sql) {
        Connection cn = getConnection(true);
        try (Statement stmt = cn.createStatement();
             ResultSet rset = stmt.executeQuery(sql)) {
            return resultSet2Vector(rset);
        } catch (SQLException ex) {
            dumpSql(ex, sql, null);
            throw new DbException(ex);
        } finally {
            close(cn);
        }
    }

    /**
     * <p>executeNoQuery.</p>
     *
     * @param strSql an array of {@link java.lang.String} objects.
     * @param cn     a {@link java.sql.Connection} object.
     * @return a int.
     */
    @Override
    public int executeNoQuery(String[] strSql, @NotNull Connection cn) {
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
    public int executeNoQuery(String sql, Object[] params, @NotNull Connection cn) {

        try (Statement p = ArrayUtils.isEmpty(params) ? cn.createStatement() : cn.prepareStatement(sql)) {
            if (p instanceof PreparedStatement) {
                PreparedStatement pre = (PreparedStatement) p;
                this.fillParameter((PreparedStatement) p, params, null);
                return pre.executeUpdate();
            } else {
                return p.executeUpdate(sql);
            }
        } catch (SQLException ex) {
            dumpSql(ex, sql, params);
            throw new DbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 批处里数组, 用于Insert, delete或update
     */
    @Override
    public int executeNoQueryBatch(String sql, Iterable<Object[]> params,
                                   Connection cn) {

        boolean needRelease = false;
        if (cn == null) {
            cn = this.getConnection(false);
            needRelease = true;
        }
        int n;
        try (PreparedStatement p = cn.prepareStatement(sql)) {
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
    public void executeQuery(String sql, RowCallbackHandler handle) {
        Connection cn = this.getConnection(true);
        try (Statement stmt = cn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                handle.processRow(rs);
            }
        } catch (SQLException ex) {
            dumpSql(ex, sql, null);
            throw new DbException(ex);
        } finally {
            close(cn);
        }

    }

    /**
     * (non-Javadoc)
     *
     * @param cn  connection
     * @param sql sql语句
     * @return single object, may be integer or string
     * @see IDbHelper#executeScalar(java.lang.String)
     */
    public Object executeScalarSession(String sql, Connection cn) {

        boolean localCn = cn == null;

        cn = localCn ? this.getConnection(true) : cn;
        try (
                Statement stmt = cn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getObject(1);
            }
            return null;
        } catch (SQLException ex) {
            dumpSql(ex, sql, null);
            throw new DbException(ex);
        } finally {
            if (localCn) {
                close(cn);
            }
        }
    }

    /**
     * <p>resultSet2Vector.</p>
     *
     * @param rs a {@link java.sql.ResultSet} object.
     * @return a {@link java.util.List} object.
     */
    public @NotNull
    List<Map<String, Object>> resultSet2Vector(ResultSet rs) {
        List<Map<String, Object>> result = new ArrayList<>();
        //System.out.println("No data"); //http://stackoverflow.com/a/6813771/1484621
        try {
            if (!rs.isBeforeFirst()) {
                return result;
            }

            ResultSetMetaData metaData = rs.getMetaData();
            int iColumnNumber = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> mapColumn = new HashMap<>(32);
                for (int i = 1; i <= iColumnNumber; i++) {

                    String colTypeName = metaData.getColumnTypeName(i).toUpperCase();

                    String colName = metaData.getColumnName(i);

                    if (!StringUtils.isBlank(metaData.getColumnLabel(i))) {
                        colName = metaData.getColumnLabel(i);
                    }

                    if (mapColumn.containsKey(colName) && rs.getObject(i) == null) {
                        //同一列名的已存在且不为空，则保留当前值，用于多表联合查询，但后表无记录的情况
                        continue;
                    }

                    switch (colTypeName) {
                        case "NUMBER":
                        case "INT":
                        case "INTEGER":
                        case "TINYINT":
                        case "TINYINT UNSIGNED":
                        case "INTEGER IDENTITY":
                        case "COUNTER":
                        case "NUMERIC":
                        case "DECIMAL":
                        case "MEDIUMINT":
                        case "INT UNSIGNED":
                        case "MEDIUMINT UNSIGNED":

                            dealNumber(rs, metaData, mapColumn, i, colTypeName, colName);

                            break;
                        case "BIGINT":
                        case "BIGINT UNSIGNED":
                            mapColumn.put(colName,
                                    rs.getLong(i));
                            break;
                        case "SMALLINT":
                            mapColumn.put(colName,
                                    rs.getInt(i));
                            break;
                        case "VARCHAR2":
                        case "VARCHAR":
                        case "NVARCHAR2":
                        case "NVARCHAR":
                        case "TEXT":
                        case "MEDIUMTEXT":
                        case "LONGTEXT":
                        case "CHAR":
                        case "NTEXT":
                        case "NCHAR":
                        case "LONGVARCHAR":
                        case "LONGCHAR": {
                            String temp = rs.getString(i);
                            if (temp == null) {
                                temp = "";
                            }
                            mapColumn.put(colName, temp);
                            break;
                        }
                        case "DATETIME":
                        case "TIMESTAMP":
                            // 10.17
                            if (rs.getTimestamp(i) != null) {
                                mapColumn.put(colName, rs.getTimestamp(i));
                            } else {
                                // 1970 year
                                Timestamp d1970 = new Timestamp(0);
                                mapColumn.put(colName, d1970);
                            }
                            break;
                        case "DATE":
                            java.util.Date dt = rs.getDate(i);
                            if (dt != null) {
                                Timestamp tm = new Timestamp(
                                        dt.getTime());
                                mapColumn.put(colName, tm);
                            } else {
                                Timestamp d1970 = new Timestamp(0);
                                mapColumn.put(colName, d1970);
                            }

                            break;
                        case "LONG": {
                            String temp = "";
                            byte[] mybyte = rs.getBytes(i);
                            if (mybyte != null) {
                                temp = new String(mybyte, Charset.defaultCharset());
                            }
                            mapColumn.put(colName, temp);
                            break;
                        }
                        case "RAW":
                        case "VARCHAR () FOR BIT DATA":
                            byte[] bb = rs.getBytes(i);
                            mapColumn.put(colName, bb == null ? new byte[0]
                                    : bb);
                            break;
                        case "CLOB": {
                            String temp = clob2Str(rs.getClob(i));
                            mapColumn.put(colName, temp);
                            break;
                        }
                        case "BOOL":
                        case "BIT": {
                            Boolean temp = rs.getBoolean(i);
                            mapColumn.put(colName, temp);
                            break;
                        }
                        case "FLOAT": {
                            Float temp = rs.getFloat(i);
                            mapColumn.put(colName, temp);
                            break;
                        }
                        case "DOUBLE": {
                            Double temp = rs.getDouble(i);
                            mapColumn.put(colName, temp);
                            break;
                        }
                        default:
                            mapColumn.put(colName, rs.getObject(i));
                            String string = " -colName:";
                            LOG.warn(
                                    "Unknow data type, ask xwx@live.cn for adding this to DbHelper.resultSet2Vector colTypeName: "
                                            + colTypeName + string + colName);
                            break;
                    }
                }
                result.add(mapColumn);
            }
            return result;
        } catch (SQLException ex) {
            dumpSql(ex, "", null);
            throw new DbException(ex);
        }
    }

    private void dealNumber(ResultSet rset, ResultSetMetaData rsmdQuery, Map<String, Object> columnMap, int i, String colTypeName, String colName)
            throws SQLException {
        if ("TINYINT".equals(colTypeName) && rsmdQuery.getPrecision(i) == 1) {
            columnMap.put(colName, rset.getBoolean(i));
        } else if ("TINYINT".equals(colTypeName) || "TINYINT UNSIGNED".equals(colTypeName)) {
            columnMap.put(colName, rset.getByte(i));
        } else if (rsmdQuery.getScale(i) <= 0) {
            // BigDecimal or Integer
            if (rset.getObject(i) instanceof BigDecimal) {
                BigDecimal a = (BigDecimal) rset.getObject(i);
                if (a.scale() > 0
                        || a.compareTo(new BigDecimal(
                        Integer.MAX_VALUE)) > 0) {
                    columnMap.put(colName,
                            rset.getObject(i));
                } else {
                    columnMap.put(colName,
                            rset.getInt(i));
                }
            } else if (rsmdQuery.getPrecision(i) > 9) {
                columnMap.put(colName,
                        rset.getLong(i));
            } else {
                columnMap.put(colName,
                        rset.getInt(i));
            }
        } else {
            columnMap.put(colName,
                    rset.getDouble(i));

        }
    }

    /**
     * <p>executeScalar.</p>
     *
     * @param sql    a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @return a {@link java.lang.Object} object.
     */
    @Override
    public Object executeScalar(String sql, Object[] params) {
        return executeScalar(sql, params, null);
    }

    /**
     * <p>fillParameter.</p>
     *
     * @param p      a {@link java.sql.PreparedStatement} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @throws SQLException if any
     */
    public void fillParameter(PreparedStatement p, Object[] params) throws SQLException {
        fillParameter(p, params, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object executeScalar(String sql, Object[] params, Connection cn) {
        if (params == null || params.length == 0) {
            return executeScalarSession(sql, cn);
        }

        boolean localCn = cn == null;

        cn = localCn ? this.getConnection(true) : cn;

        try (PreparedStatement p = cn.prepareStatement(sql)) {
            fillParameter(p, params, null);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject(1);
                }
            }
        } catch (SQLException ex) {
            dumpSql(ex, sql, params);
            throw new DbException(ex);
        } finally {
            if (localCn) {
                close(cn);
            }
        }
        return null;
    }

    /**
     * <p>fillParameter.</p>
     *
     * @param p              a {@link java.sql.PreparedStatement} object.
     * @param params         an array of {@link java.lang.Object} objects.
     * @param targetSqlTypes an array of int objects.
     * @throws SQLException 数据库出错
     */
    public void fillParameter(PreparedStatement p, Object[] params,
                              int[] targetSqlTypes) throws SQLException {

        if (ArrayUtils.isEmpty(params)) {
            return;
        }

        if (ArrayUtils.isNotEmpty(targetSqlTypes) && targetSqlTypes.length != params.length) {
            throw new DbException(
                    "Intenal error: The params and the targetSqlTypes must have same length!");
        }


        if (ArrayUtils.isNotEmpty(targetSqlTypes)) {
            for (int i = 1; i <= params.length; i++) {
                p.setObject(i, params[i - 1], targetSqlTypes[i - 1]);
            }
        } else {
            for (int i = 1; i <= params.length; i++) {
                Object pa = params[i - 1];
                if (pa == null) {
                    p.setNull(i, java.sql.Types.VARCHAR, "String");
                    LOG.debug("不支持空数据(参数["
                            + i
                            + "])! Null is not support in DbHelper.executeNoQuery(String sql, Object[] params), there may be error!");
                } else if (pa instanceof Integer) {
                    p.setInt(i, (Integer) pa);
                } else if (pa instanceof Long) {
                    p.setLong(i, (Long) pa);
                } else if (pa instanceof java.sql.Timestamp) {
                    p.setTimestamp(i, (Timestamp) pa);
                } else if (pa instanceof java.sql.Date) {
                    p.setDate(i, (Date) pa);
                }else if (pa instanceof java.sql.Time) {
                    p.setTime(i, (Time) pa);
                } else if (pa instanceof java.util.Date) {
                    p.setTimestamp(i, new java.sql.Timestamp(((java.util.Date) pa).getTime()));
                } else if (pa instanceof Double) {
                    p.setDouble(i, (Double) pa);
                } else if (pa instanceof String) {
                    p.setString(i, ((String) pa));
                } else if (pa instanceof ClobString) {
                    p.setObject(i, pa.toString());
                } else {
                    p.setObject(i, pa);
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
    public List<Map<String, Object>> executeQuery(String sql, Object[] params) {
        Connection cn = getConnection(true);
        ResultSet rs = null;

        try (Statement p = ArrayUtils.isEmpty(params) ? cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY) : cn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY)
        ) {
            if (p instanceof PreparedStatement) {
                fillParameter((PreparedStatement) p, params);
                rs = ((PreparedStatement) p).executeQuery();
            } else {
                rs = p.executeQuery(sql);
            }
            return this.resultSet2Vector(rs);

        } catch (SQLException ex) {
            dumpSql(ex, sql, params);
            throw new DbException(ex);
        } finally {
            close(rs);
            close(cn);
        }

    }

    /**
     * <p>executeNoQuery.</p>
     *
     * @param sql            a {@link java.lang.String} object.
     * @param params         an array of {@link java.lang.Object} objects.
     * @param targetSqlTypes an array of int objects.
     * @return a int.
     */
    @Override
    public int executeNoQuery(String sql, Object[] params, int[] targetSqlTypes) {
        return executeNoQuery(sql, params, targetSqlTypes, null);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeNoQuery(String sql, Object[] params,
                              int[] targetSqlTypes, Connection conn) {
        boolean localCn = conn == null;
        Connection cn = localCn ? getConnection(false) : conn;
        try (Statement p = ArrayUtils.isNotEmpty(params) ? cn.prepareStatement(sql) : cn.createStatement()) {
            if (p instanceof PreparedStatement) {
                fillParameter((PreparedStatement) p, params, targetSqlTypes);
                ((PreparedStatement) p).execute();
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
                                     int[] targetSqlTypes, Connection conn) {
        boolean localCn = conn == null;
        Connection cn = localCn ? getConnection(false) : conn;
        try (PreparedStatement p = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (ArrayUtils.isNotEmpty(params)) {
                fillParameter(p, params, targetSqlTypes);
            }

            p.execute();

            ResultSet rs = p.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return Long.parseLong(rs.getObject(1).toString());
            }
            return 0;


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
     * <p>executeNoQuery.</p>
     *
     * @param sql    a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @return a int.
     */
    @Override
    public int executeNoQuery(String sql, Object[] params) {

        return executeNoQuery(sql, params, new int[0]);

    }

    /**
     * <p>executeQuery.</p>
     *
     * @param sql    a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @param handle a {@link RowCallbackHandler} object.
     */
    @Override
    public void executeQuery(String sql, Object[] params,
                             RowCallbackHandler handle) {
        Connection cn = getConnection(true);
        try (Statement p = ArrayUtils.isNotEmpty(params) ? cn.prepareStatement(sql) : cn.createStatement()) {
            if (p instanceof PreparedStatement) {
                fillParameter((PreparedStatement) p, params, null);
            }
            try (ResultSet rs = (p instanceof PreparedStatement) ? ((PreparedStatement) p).executeQuery() : p.executeQuery(sql)) {
                while (rs.next()) {
                    handle.processRow(rs);
                }
            }
        } catch (SQLException ex) {
            dumpSql(ex, sql, params);
            throw new DbException(ex);
        } finally {
            DbUtils.closeQuietly(cn);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull <T> List<T> executeQuery(String sql, Object[] params,
                                             @NotNull RowMapperResultReader<T> handle) {
        return executeQuery(sql, params, handle, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull <T> List<T> executeQuery(String sql, Object[] params,
                                             @NotNull RowMapperResultReader<T> handle, Set<String> returnFields) {
        Connection cn = getConnection(true);
        try (Statement p = ArrayUtils.isNotEmpty(params) ? cn.prepareStatement(sql) : cn.createStatement()) {
            if (p instanceof PreparedStatement) {
                fillParameter((PreparedStatement) p, params, null);
            }
            try (ResultSet rs = (p instanceof PreparedStatement) ? ((PreparedStatement) p).executeQuery() : p.executeQuery(sql)) {
                return handle.processResultSet(rs, returnFields);
            }
        } catch (SQLException ex) {
            //可能超时，输出sql和参数
            dumpSql(ex, sql, params);
            throw new DbException(ex);
        } finally {
            DbUtils.closeQuietly(cn);
        }

    }

    @Override
    public @NotNull <T> List<T> executeQuery(String sql, Object[] params,
                                             @NotNull RowMapper<T> handle) {
        return executeQuery( sql, params,
                new RowMapperResultReader<>(handle));
    }

    @Override
    public @NotNull <T> List<T> executeQuery(String sql, Object[] params,
                                             @NotNull RowMapper<T> handle, Set<String> returnFields) {
        return executeQuery( sql, params,
                new RowMapperResultReader<>(handle), returnFields);
    }
}
