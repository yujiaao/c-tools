package com.bixuebihui.datasource;

import com.bixuebihui.dbcon.DatabaseConfig;
import com.bixuebihui.jdbc.AbstractBaseDao;
import com.bixuebihui.jdbc.DbHelper;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.sequence.SequenceUtils;
import com.bixuebihui.util.DbUtils;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DataSourceTest {

	private static final Logger LOG = LoggerFactory.getLogger(DataSourceTest.class);
	protected static DatabaseConfig cfg = new DatabaseConfig();
	static {
		/**
		 * Execute the following sql to create a test database and user:
		 *  create database test  default character set utf8mb4 collate utf8mb4_unicode_ci;
		 *  create user 'test' identified by 'test123';
		 *  grant all privileges on test.* to  'test'@'%' ;
		 */
		cfg.setAlias("test1");
		cfg.setClassName("com.mysql.cj.jdbc.Driver");
		cfg.setUsername("test");
		cfg.setPassword("test123");
		cfg.setDburl(
				"jdbc:mysql://localhost:3306/test?autoReconnect=true" +
						"&useUnicode=true&" +
						"characterEncoding=utf-8&" +
						"mysqlEncoding=utf8&" +
						"useSSL=false&" +
						"connectTimeout=200" +
						"&socketTimeout=1000&allowMultiQueries=true");

	}

	public static DatabaseConfig getConfig() {
		return getConfigMaster();
	}

	public static String getDummySql(Connection cn) {
		IDbHelper db = new DbHelper(cn);
		AbstractBaseDao mybd = new AbstractBaseDao(db);
		return mybd.getDummySql();
	}

	public static List<Long> getIds(DataSource ds, int count) {
		IDbHelper db = new DbHelper();
		db.setDataSource(ds);
		AbstractBaseDao mybd = new AbstractBaseDao(db) {
			@Override
			public String getTableName() {
				return "t_log";
			}
		};
		return mybd.ar().limit(0, count).getLongVector("lid");
	}

	static Connection getConnection(DataSource ds) throws SQLException {
		Connection cn  = ds.getConnection();
        assertNotNull(cn);
		if(cn.isClosed()){
			LOG.error("connection is closed");
			LOG.error("try get another");
			return ds.getConnection();
		}
		return cn;
	}

	public static  void dataSourceTest(DataSource ds) throws SQLException {
		Date dtStart = new Date();
		String sql="";
		try(Connection cn = getConnection(ds)) {
			sql = getDummySql(cn);
		}
		try(Connection cn = getConnection(ds)) {

			for (int i = 0; i < 20; i++) {
				ResultSet rs = null;
				Statement stmt = null;
				try {
					if(!cn.isClosed()) {
						stmt = cn.createStatement();
					}else{
						LOG.error("connection is closed in loop i="+i);
						return;
					}

					rs = stmt.executeQuery("select 1+" + i + " " + sql);

					if (rs.next()) {
						assertEquals(1 + i, rs.getInt(1));
					} else {
						LOG.info("dataSourceTest error select");
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw e;
				} finally {
					DbUtils.closeQuietly(stmt);
					DbUtils.closeQuietly(rs);
				}
			}
		}

		Date dtEnd = new Date();
		LOG.info(ds + " used time:" + (dtEnd.getTime() - dtStart.getTime()) / 1000.0);
	}




	public  static  void testDbHelper(DataSource ds){
		DbHelper dbHelper = new DbHelper();
		dbHelper.setDataSource(ds);

		String keyName="what_is_love";
		long v1 = SequenceUtils.getInstance().getNextKeyValue(keyName, dbHelper);
		long v2 = SequenceUtils.getInstance().getNextKeyValue(keyName, dbHelper);

		assertEquals(v1 + 1, v2);
	}

	public static void dataSourceTestCursor(DataSource ds) throws SQLException {
		Date dtStart = new Date();

		for (int i = 1; i < 2000; i++) {
			Connection cn = null;
			ResultSet rs = null;
			PreparedStatement stmt = null;
			try {
				cn = ds.getConnection();
				stmt = cn.prepareStatement("select lid, content from t_log where lid=?");
				stmt.setInt(1, i);

				rs = stmt.executeQuery();

				if (rs.next()) {
					assertEquals(i, rs.getInt(2));
				} else {
					LOG.info("dataSourceTestCursor error select region_id=" + i);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;

			} finally {

				DbUtils.closeQuietly(cn, stmt, rs);
			}
		}
		Date dtEnd = new Date();
		LOG.info(ds + " used time:" + (dtEnd.getTime() - dtStart.getTime()) / 1000.0);
	}

	public static void dataSourceTestCursor1(DataSource ds) throws SQLException {
		Date dtStart = new Date();
 		for (int i = 1; i < 2000; i++) {
			Connection cn = null;
			ResultSet rs = null;
			Statement stmt = null;
			try {
				cn = ds.getConnection();

				stmt = cn.createStatement();
				rs = stmt.executeQuery("select content, lid from t_log where lid=" + i);

				if (rs.next()) {
					assertEquals(i, rs.getInt(2));

				} else {
					LOG.info("dataSourceTestCursor1 error select");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;

			} finally {

				DbUtils.closeQuietly(cn, stmt, rs);
			}
			synchronized (dtStart) {
				try {
					dtStart.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		Date dtEnd = new Date();
		LOG.info(ds + " used time:" + (dtEnd.getTime() - dtStart.getTime()) / 1000.0);
	}



	public static void dataSourceTestCursor2(DataSource ds) throws SQLException {
		Date dtStart = new Date();

 		List<Long> ids = getIds(ds, 100);

		for (int i = 1; i < ids.size(); i++) {
			Connection cn = null;
			ResultSet rs = null;
			Statement stmt = null;
			try {
				cn = ds.getConnection();

				stmt = cn.createStatement();
				Random r = new Random();
				int k = r.nextInt();
				rs = stmt.executeQuery(
						"select content, lid+(" + k + ") from t_log where lid=" + ids.get(i));

				if (rs.next()) {
					assertEquals(ids.get(i) + k, rs.getInt(2));
				} else {
					LOG.info("dataSourceTestCursor2 error select");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;

			} finally {

				DbUtils.closeQuietly(cn, stmt, rs);
			}
			synchronized (dtStart) {
				try {
					dtStart.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		Date dtEnd = new Date();
		LOG.info(ds + " used time:" + (dtEnd.getTime() - dtStart.getTime()) / 1000.0);
	}

	public static DatabaseConfig getConfigDerby() {
	    String dbName = "/tmp/data/test";
		File file = new File(dbName);
		try {
			FileUtils.deleteDirectory(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		DatabaseConfig cfg = new DatabaseConfig();
		cfg.setAlias("derby");
		//cfg.setClassName("org.apache.derby.jdbc.EmbeddedDriver");
		// for java 9 derby 10.15.x
		cfg.setClassName("org.apache.derby.iapi.jdbc.AutoloadedDriver");
		cfg.setUsername("");
		cfg.setPassword("");
		cfg.setDburl("jdbc:derby:"+dbName+";create=true");

		return cfg;

	}

	public static DatabaseConfig getConfigWest() {

		DatabaseConfig cfg = new DatabaseConfig();
		cfg.setAlias("oracle");
		cfg.setClassName("oracle.jdbc.driver.OracleDriver");
		cfg.setUsername("oracle");
		cfg.setPassword("oracle123");
		cfg.setDburl(
				"jdbc:oracle:thin:@(description=(address=(host=localhost)(protocol=tcp)(port=1521))(connect_data=(sid=testdev)))");
		cfg.setMaxActive(60);

		cfg.setMaxCheckOutCount(200);
		cfg.setSleepIntervalInSeconds(2000);
		cfg.setMaxOpenPreparedStatements(500);

		cfg.setMaxIdle(10);

		return cfg;

	}

	public static DatabaseConfig getConfigH2() {

		DatabaseConfig cfg = new DatabaseConfig();
		cfg.setAlias("test1");
		cfg.setClassName("org.h2.Driver");
		cfg.setUsername("sa");
		cfg.setPassword("sa");
		cfg.setDburl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

		return cfg;

	}

	public static boolean isMysqlAvailable() {

		if(!cfg.getClassName().contains("mysql")){
			return false;
		}

		try {
			DriverManager.registerDriver((java.sql.Driver)(Class.forName(cfg.getClassName()).getDeclaredConstructor().newInstance()));
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | InvocationTargetException | SQLException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("MySQL Driver is not available!");
		}

		try(Connection con = DriverManager.getConnection(
				cfg.getDburl(), cfg.getUsername(), cfg.getPassword());
			Statement statement =  con.createStatement();
			ResultSet resultSet = statement.executeQuery("select 1+1");
		) {
			resultSet.next();
			return resultSet.getInt(1) == 2;
		} catch (Exception e) {
			return false;
		}
	}

	public static DatabaseConfig getConfigMaster() {
		if (!isMysqlAvailable()) {
			try {
				new H2DataSource().init();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return getConfigH2();
		}
		return cfg;
	}

	public static DatabaseConfig getConfigReadOnly() {

		DatabaseConfig c = getConfigMaster();
		DatabaseConfig cfg = new DatabaseConfig();
		cfg.setAlias("testRO");
		cfg.setClassName(c.getClassName());
		cfg.setUsername(c.getUsername());
		cfg.setPassword(c.getPassword());
		cfg.setDburl(c.getDburl());

		return cfg;

	}

}
