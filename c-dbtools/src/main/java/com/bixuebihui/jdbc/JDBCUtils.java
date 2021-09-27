package com.bixuebihui.jdbc;

import com.bixuebihui.util.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * 读取数据库表或视图，处理oracle大字段
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class JDBCUtils {

    /**
     * Constant <code>mLog</code>
     */
    protected static final Logger LOG = LoggerFactory.getLogger(JDBCUtils.class);

    private JDBCUtils(){
        throw new IllegalAccessError("this is a utils class");
    }

    /**
     * <p>tableOrViewExists.</p>
     *
     * @param catalog a {@link java.lang.String} object.
     * @param schemaPattern a {@link java.lang.String} object.
     * @param tableName a {@link java.lang.String} object.
     * @param conn a {@link java.sql.Connection} object.
     * @return a boolean.
     * @throws java.sql.SQLException if any.
     */
    public static boolean tableOrViewExists(String catalog,
                                            String schemaPattern, String tableName, Connection conn)
            throws SQLException {
        String[] tableTypes = { "TABLE", "VIEW" };
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet tables = null;
        boolean res;
        try {
            tables = metaData.getTables(catalog, schemaPattern, tableName,
                    tableTypes);
            res = tables.next();

        } finally {
            close(tables);
        }
        return res;
    }

    /**
     * <p>columnOfTableExists.</p>
     *
     * @param catalog a {@link java.lang.String} object.
     * @param schemaPattern a {@link java.lang.String} object.
     * @param tableName a {@link java.lang.String} object.
     * @param columnName a {@link java.lang.String} object.
     * @param conn a {@link java.sql.Connection} object.
     * @return a boolean.
     * @throws java.sql.SQLException if any.
     */
    public static boolean columnOfTableExists(String catalog,
                                              String schemaPattern, String tableName, String columnName,
                                              Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet column = null;
        boolean res;
        try {
            column = metaData.getColumns(catalog, schemaPattern, tableName,
                    columnName);
            res = column.next();

        } finally {
            close(column);
        }
        return res;
    }

    /**
     * <p>close.</p>
     *
     * @param stmt a {@link java.sql.Statement} object.
     */
    public static void close(Statement stmt) {
        DbUtils.closeQuietly(stmt);
    }

    /**
     * <p>close.</p>
     *
     * @param rs a {@link java.sql.ResultSet} object.
     */
    public static void close(ResultSet rs) {
        DbUtils.closeQuietly(rs);
    }

    /**
     * <p>close.</p>
     *
     * @param conn a {@link java.sql.Connection} object.
     */
    public static void close(Connection conn) {
        DbUtils.closeQuietly(conn);
    }





}
