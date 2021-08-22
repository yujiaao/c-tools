package com.bixuebihui.r2dbc.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 读取 SQL 脚本并执行
 *
 * @author Unmi
 * @version $Id: $Id
 */
public class SqlFileExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(SqlFileExecutor.class);

	private boolean batchExecute = false;
	private String defaultEncoding="UTF-8";

	public SqlFileExecutor setAllowMultiQueries(boolean allowMultiQueries) {
		this.allowMultiQueries = allowMultiQueries;
		return this;
	}

	private boolean allowMultiQueries = true;


	/**
	 * 读取 SQL 文件，获取 SQL 语句
	 *
	 * @param sqlFile
	 *            SQL 脚本文件
	 * @return List<sql> 返回所有 SQL 语句的 List
	 * @throws IOException 文件加载出错
	 */
	private List<String> loadSql(String sqlFile) throws IOException {
		try (InputStream sqlFileIn = new FileInputStream(sqlFile)) {
			return loadSqlFromStream(sqlFileIn);
		}
	}


	/**
	 * 读取 SQL 文件，获取 SQL 语句
	 *
	 * @param sqlFile
	 *            SQL 脚本文件
	 * @return List<sql> 返回所有 SQL 语句的 List
	 */
	private List<String> loadSqlFromStream(InputStream sqlFile) throws IOException {
		List<String> sqlList = new ArrayList<>();

		StringBuilder buffer = new StringBuilder();
		try (
				InputStreamReader isr = new InputStreamReader(sqlFile, defaultEncoding);
				Reader in = new BufferedReader(isr)
		) {

			int ch;
			while ((ch = in.read()) > -1) {
				buffer.append((char)ch);
			}

			// Windows 下换行是 \r\n, Linux 下是 \n
			if(allowMultiQueries){
				sqlList.add(buffer.toString());
			}else {
				// without a collect sql grammar parser the below replace maybe buggy. use it only by fortune!
				String[] sqlArr = buffer.toString()
						.split("(;\\s*\\r\\n)|(;\\s*\\n)");
				for (int i = 0; i < sqlArr.length; i++) {
					String sql = sqlArr[i].replaceAll("--\\s+.*", "").trim();
					if (!"".equals(sql)) {
						sqlList.add(sql);
					}
				}
			}
			return sqlList;
		}
	}

	/**
	 * 传入连接来执行 SQL 脚本文件，这样可与其外的数据库操作同处一个事物中
     *
     * @param conn
	 *            传入数据库连接
	 * @param sqlFile
	 *            SQL 脚本文件
     * @throws IOException 加载文件出错
     * @throws SQLException 数据库出错
     */
	public void execute(Connection conn, String sqlFile) throws IOException, SQLException {
		List<String> sqlList = loadSql(sqlFile);
		execute(conn, sqlList);
	}

    /**
     * <p>execute.</p>
     *
     * @param conn    a {@link Connection} object.
     * @param sqlFile a {@link InputStream} object.
     * @throws IOException   if any.
     * @throws SQLException if any.
     */
    public void execute(Connection conn, InputStream sqlFile) throws IOException, SQLException {
        List<String> sqlList = loadSqlFromStream(sqlFile);
        execute(conn, sqlList);
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     * @throws Exception if any.
     */
    public static void main(String[] args) throws Exception {
        List<String> sqlList = new SqlFileExecutor().loadSql(args[0]);
        LOG.info("size:" + sqlList.size());
        for (String sql : sqlList) {
            LOG.info(sql);
        }
    }

	private void execute(Connection conn, List<String> sqlList)
			throws SQLException {

		try (Statement stmt = conn.createStatement()) {
			if (this.batchExecute) {
				for (int i = 0; i < sqlList.size(); i++) {
					String sql = sqlList.get(i);
					stmt.addBatch(sql);
				}
				int[] rows = stmt.executeBatch();
				LOG.info("Row count:" + Arrays.toString(rows));
			} else {
				for (int i = 0; i < sqlList.size(); i++) {
					String sql = sqlList.get(i);
					try {
						if (stmt.execute(sql)) {
							LOG.info("RETURN RESULT SET IGNORED: " + sql);
						} else {
							LOG.info("RETURN UPDATE COUNT: "+stmt.getUpdateCount()+" " + sql);
						}
					} catch (SQLException e) {
						LOG.error("",e);
					}
				}

			}
		}
	}

	/**
	 * 传入连接来执行 SQL 脚本文件，这样可与其外的数据库操作同处一个事物中
     *
	 * @param conn
	 *            传入数据库连接
	 * @param sqlFile
	 *            SQL 脚本文件
	 * @param testTableName
	 *            用来检测表或视图是否存在的名称，如存在则不执行文件
     * @throws IOException 加载文件出错
     * @throws SQLException 数据库出错
     */
	public void execute(Connection conn, String sqlFile, String testTableName)
            throws SQLException, IOException {
        if (!JDBCUtils.tableOrViewExists(null, null, testTableName, conn)) {
            execute(conn, sqlFile);
        } else {
            LOG.warn("Table [" + testTableName + "] already exists!");
        }
    }

    /**
     * <p>isBatchExecute.</p>
     *
     * @return a boolean.
     */
    public boolean isBatchExecute() {
        return batchExecute;
    }

    /**
     * <p>Setter for the field <code>batchExecute</code>.</p>
     *
     * @param batchExecute a boolean.
     */
    public void setBatchExecute(boolean batchExecute) {
        this.batchExecute = batchExecute;
    }

    /**
     * <p>getEncoding.</p>
     *
     * @return a {@link String} object.
     */
    public String getEncoding() {
        return defaultEncoding;
    }

    /**
     * <p>settEncoding.</p>
     *
     * @param encoding a {@link String} object.
     */
    public void settEncoding(String encoding) {
        this.defaultEncoding = encoding;
    }
}
