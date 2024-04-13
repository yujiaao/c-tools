//package com.bixuebihui.util.log4j;
//
//import com.bixuebihui.sql.SQLUtil;
//import com.bixuebihui.util.Config;
//import org.apache.log4j.PatternLayout;
//import org.apache.log4j.spi.ErrorCode;
//import org.apache.log4j.spi.LoggingEvent;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Iterator;
//
///**
// * for log4j,将日志写到数据库里，效率较差，慎用
// *
// * @author xingwx
// */
//public class JDBCPoolAppender extends org.apache.log4j.jdbc.JDBCAppender {
//
//    DataSource ds;
//
//    public JDBCPoolAppender() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//        String dbsource = Config.getProperty("config.datasource");
//
//        if (!"".equals(dbsource) && dbsource != null) {
//            Class<?> cls = Class.forName(dbsource);
//            ds = (DataSource) (cls.newInstance());
//        }
//    }
//
//    /**
//     * ArrayList holding the buffer of Logging Events.
//     */
//    @Override
//    public void append(LoggingEvent event) {
//        buffer.add(event);
//        if (buffer.size() >= bufferSize) {
//            flushBuffer();
//        }
//    }
//
//    /**
//     * By default getLogStatement sends the event to the required Layout object.
//     * The layout will format the given pattern into a workable SQL string.
//     * <p>
//     * Overriding this provides direct access to the LoggingEvent when
//     * constructing the logging statement.
//     */
//    @Override
//    protected String getLogStatement(LoggingEvent event) {
//        return SQLUtil.escapeString(getLayout().format(event)).replaceAll("@@", "'");
//    }
//
//    /**
//     * Override this to provide an alertnate method of getting connections (such
//     * as caching). One method to fix this is to open connections at the start
//     * of flushBuffer() and close them at the end. I use a connection pool
//     * outside of JDBCAppender which is accessed in an override of this method.
//     */
//    @Override
//    protected void execute(String sql) throws SQLException {
//        Connection con;
//        Statement stmt = null;
//        try {
//            con = getConnection();
//            stmt = con.createStatement();
//            // System.out.println(sql);
//            stmt.executeUpdate(sql);
//        } catch (SQLException e) {
//            if (stmt != null) {
//                stmt.close();
//            }
//            throw e;
//        }
//        stmt.close();
//        closeConnection(con);
//    }
//
//    /**
//     * Override this to return the connection to a pool, or to clean up the
//     * resource.
//     * <p>
//     * The default behavior holds a single connection open until the appender is
//     * closed (typically when garbage collected).
//     */
//    @Override
//    protected void closeConnection(Connection con) {
//        try {
//            if (connection != null && !connection.isClosed()) {
//                connection.close();
//            }
//        } catch (SQLException e) {
//            errorHandler.error("Error closing connection", e, ErrorCode.GENERIC_FAILURE);
//        }
//
//    }
//
//    /**
//     *
//     */
//    @Override
//    protected Connection getConnection() throws SQLException {
//        connection = ds.getConnection();
//        return connection;
//    }
//
//    /**
//     * Closes the appender, flushing the buffer first then closing the default
//     * connection if it is open.
//     */
//    @Override
//    public void close() {
//        flushBuffer();
//
//        try {
//            if (connection != null && !connection.isClosed()) {
//                connection.close();
//            }
//        } catch (SQLException e) {
//            errorHandler.error("Error closing connection", e, ErrorCode.GENERIC_FAILURE);
//        }
//        this.closed = true;
//    }
//
//    /**
//     * loops through the buffer of LoggingEvents, gets a sql string from
//     * getLogStatement() and sends it to execute(). Errors are sent to the
//     * errorHandler.
//     * <p>
//     * If a statement fails the LoggingEvent stays in the buffer!
//     */
//    @Override
//    public void flushBuffer() {
//        // Do the actual logging
//        removes.ensureCapacity(buffer.size());
//        for (Iterator<LoggingEvent> i = buffer.iterator(); i.hasNext(); ) {
//            try {
//                LoggingEvent logEvent = i.next();
//                String sql = getLogStatement(logEvent);
//                execute(sql);
//                removes.add(logEvent);
//            } catch (SQLException e) {
//                errorHandler.error("Failed to excute sql", e, ErrorCode.FLUSH_FAILURE);
//            }
//        }
//
//        // remove from the buffer any events that were reported
//        buffer.removeAll(removes);
//
//        // clear the buffer of reported events
//        removes.clear();
//    }
//
//    /**
//     * closes the appender before disposal
//     */
//    @Override
//    public void finalize() {
//        close();
//        super.finalize();
//    }
//
//    /**
//     * JDBCAppender requires a layout.
//     */
//    @Override
//    public boolean requiresLayout() {
//        return true;
//    }
//
//    /**
//     *
//     */
//    @Override
//    public void setSql(String s) {
//        sqlStatement = s;
//        if (getLayout() == null) {
//            this.setLayout(new PatternLayout(s));
//        } else {
//            ((PatternLayout) getLayout()).setConversionPattern(s);
//        }
//    }
//
//    /**
//     * Returns pre-formated statement eg: insert into LogTable (msg) values
//     * ("%m")
//     */
//    @Override
//    public String getSql() {
//        return sqlStatement;
//    }
//
//
//    @Override
//    public void setBufferSize(int newBufferSize) {
//        bufferSize = newBufferSize;
//        buffer.ensureCapacity(bufferSize);
//        removes.ensureCapacity(bufferSize);
//    }
//
//    @Override
//    public int getBufferSize() {
//        return bufferSize;
//    }
//}
