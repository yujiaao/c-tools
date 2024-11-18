package com.bixuebihui.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import com.bixuebihui.dbcon.DatabaseConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bixuebihui.ConnectionManager;
import junit.framework.TestCase;
import org.junit.jupiter.api.condition.DisabledIf;

import static org.junit.Assert.assertSame;

public class BitmechanicDataSourceTest  {

	private static final Logger LOG = LoggerFactory.getLogger(BitmechanicDataSourceTest.class);

	@Test
	@EnabledIf("com.bixuebihui.datasource.DataSourceTest#isMysqlAvailable")
	public void testGetConnection() throws SQLException {
		BitmechanicDataSource ds = new BitmechanicDataSource();

		ds.setAlias("test1");

		DataSourceTest.dataSourceTest(ds);
	}

	@Test
	@EnabledIf("com.bixuebihui.datasource.DataSourceTest#isMysqlAvailable")
	public void testGetConnection1() throws SQLException {
		BitmechanicDataSource ds = new BitmechanicDataSource();
		ds.setAlias("test1");
		DatabaseConfig cfg = new DatabaseConfig();
		cfg.setClassName("com.mysql.cj.jdbc.Driver");
		cfg.setUsername("test");
		cfg.setPassword("test123");
		cfg.setMaxCheckOutCount(10);
		cfg.setDburl(
				"jdbc:mysql://localhost:3306/test?autoReconnect=true" +
						"&useUnicode=true&" +
						"characterEncoding=utf-8&" +
						"mysqlEncoding=utf8&" +
						"useSSL=false&" +
						"connectTimeout=200" +
						"&socketTimeout=1000&allowMultiQueries=true");

		ds.setDatabaseConfig(cfg);
		ds.getDatabaseConfig().setMaxActive(1);

		Connection cn = ds.getConnection();
		Assertions.assertNotNull(cn);
		Assertions.assertTrue(cn.isValid(1));
		Connection cn2 = ds.getConnection();
		Assertions.assertNotNull(cn2);
		Assertions.assertTrue(cn2.isValid(1));
		Assertions.assertNotSame(cn, cn2);

		cn.close();
		for(int i=0; i< cfg.getMaxCheckOutCount()-1; i++) {
			Connection cn3 = ds.getConnection();
			Assertions.assertNotNull(cn3);
			Assertions.assertTrue(cn3.isValid(1));
			System.out.println("i="+i+"\ncn3="+cn3);
			Assertions.assertSame(cn, cn3);
			cn3.close();
		}
	}



	@Test
	@EnabledIf("com.bixuebihui.datasource.DataSourceTest#isMysqlAvailable")
	public void testSetDatabaseConfig() throws SQLException {
		BitmechanicDataSource ds = new BitmechanicDataSource();

		ds.setDatabaseConfig(DataSourceTest.getConfig());

		DataSourceTest.dataSourceTest(ds);
	}

	static class CursorThread extends Thread {
		int num = 0;

		public CursorThread(int num) {
			this.num = num;
		}

		public void run() {
			LOG.info("start " + num);
			synchronized (this) {
				BitmechanicDataSource ds = new BitmechanicDataSource();

				ds.setDatabaseConfig(DataSourceTest.getConfig());

				try {
					DataSourceTest.dataSourceTestCursor2(ds);

					ConnectionManager.getInstance().dumpInfo();

				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					this.wait(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			LOG.info("end " + num);
		}
	}

	@Test
	public void testMaxCursor() throws Exception {
		ConnectionManager.reset();

		int startCount = dumpUserThread();

		synchronized (this) {
			for (int i = 1; i < 20; i++) {
				(new CursorThread(i)).start();
			}

			int k = 1000;
			while (k-- > 0) {
				int numThreads = dumpUserThread();
				if (numThreads > startCount - 20 + 1) {
					wait(1);
				} else
					break;
			}

		}
	}

	public int dumpAllThread() {

		// Find the root thread group
		ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
		while (root.getParent() != null) {
			root = root.getParent();
		}
		// Visit each thread group
		return visit(root, 0, 0);
	}

	public int dumpUserThread() {

		// Find the root thread group
		ThreadGroup root = Thread.currentThread().getThreadGroup();// .getParent();
		// if (root.getParent() != null) {
		// root = root.getParent();
		// }
		// Visit each thread group
		return visit(root, 0, 0);
	}

	// This method recursively visits all thread groups under `group'.
	public static int visit(ThreadGroup group, int level, int sum) {
		// Get threads in `group'
		int numThreads = group.activeCount();
		Thread[] threads = new Thread[numThreads * 2];
		numThreads = group.enumerate(threads, false);
		// Enumerate each thread in `group'
		for (int i = 0; i < numThreads; i++) {
			// Get thread
			Thread thread = threads[i];
			if (thread.isAlive())
				sum++;
		} // Get thread subgroups of `group'
		int numGroups = group.activeGroupCount();
		ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
		numGroups = group.enumerate(groups, false);
		// Recursively visit each subgroup

		for (int i = 0; i < numGroups; i++) {
			sum = visit(groups[i], level + 1, sum);
		}
		return sum;
	}

}
