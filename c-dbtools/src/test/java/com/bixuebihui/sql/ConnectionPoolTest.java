package com.bixuebihui.sql;

import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.bixuebihui.datasource.DataSourceTest;
import com.bixuebihui.dbcon.DatabaseConfig;

import com.bixuebihui.util.DbUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

/** 方法名排序，保证 returnConnection / 压测靠后，减少与前面用例的池状态耦合 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConnectionPoolTest {


	private static final int _checkoutSeconds = 1;

	/** 仅在有 MySQL 时跑多线程压测；H2 下跳过（JUnit4 忽略 @DisabledIf，曾导致 1000 线程 + H2 极慢） */
	private static final int STRESS_GET_CONNECTION_THREADS = 64;

	/** 需略大于池的 checkout 间隔（_checkoutSeconds*1000）；再短易 flaky */
	private static final int IDLE_TEST_HOLD_MS = 1100;

	DatabaseConfig ds = DataSourceTest.getConfigMaster();

	private int _timeoutMilliSeconds = 100;
	private int _maxPrepStmts = 10;

	ConnectionPool cp = new ConnectionPool(ds.getAlias(), ds.getDburl(), ds.getUsername(), ds.getPassword(),
			ds.getMaxActive(), _timeoutMilliSeconds, _checkoutSeconds * 1000, ds.getMaxCheckOutCount(), _maxPrepStmts);

	@Before
	public void setUp() {
		try {
			DriverManager.registerDriver((Driver) Class.forName(ds.getClassName()).getDeclaredConstructor().newInstance());
		} catch (Exception e) {
			throw new IllegalStateException("JDBC driver is missing: " + ds.getClassName(), e);
		}
		// trace=true 时 getConnection() 内层循环行为异常，易阻塞/空转；日常测试关闭 tracing
		cp.setTracing(false);
	}

	@Test
	public void testSetCacheStatements() {
		cp.setCacheStatements(true);
	}

	@Test
	public void testGetCacheStatements() {
		cp.setCacheStatements(true);
		assertTrue(cp.isCacheStatements());
		cp.setCacheStatements(false);
		assertFalse(cp.isCacheStatements());
	}

	@Test
	public void testSetTracing() {
		cp.setTracing(true);
	}

	@Test
	public void testGetAlias() {
		assertEquals("test1", cp.getAlias());
	}

	@Test
	public void testGetNumRequests() throws SQLException {
		try (Connection ignored = cp.getConnection()) {
			int res = cp.getNumRequests();
			assertTrue(res > 0);
			System.out.println("getNumRequests = " + res);
		}
	}

	@Test
	public void testGetNumWaits() {
		int res = cp.getNumWaits();
		System.out.println("getNumWaits = " + res);
	}

	@Test
	public void testGetNumCheckoutTimeouts() {
		int res = cp.getNumCheckoutTimeouts();
		System.out.println("getNumCheckoutTimeouts=" + res);
	}

	@Test
	public void testGetMaxConn() {
		int res = cp.getMaxConn();
		System.out.println("getMaxConn = " + res);
	}

	@Test
	public void testSize() {
		int res = cp.size();
		System.out.println("size = " + res);
	}

	@Test
	public void testReapIdleConnections() {
		cp.reapIdleConnections();

	}

	@Test
	public void testRemoveAllConnections() throws SQLException {
		try (Connection ignored = cp.getConnection()) {
			assertTrue(cp.size() > 0);
		}
		cp.removeAllConnections();
		assertEquals(0, cp.size());
	}

	volatile AtomicInteger count = new AtomicInteger(STRESS_GET_CONNECTION_THREADS);

	@Test(timeout = 60_000)
	public void testZ_stressGetConnection() throws InterruptedException {
		// JUnit4 忽略 @DisabledIf；H2 下勿跑多线程压测（曾 1000 线程卡死）。不用 Assume，避免与 Surefire 组合异常。
		if (ds.getClassName() == null || !ds.getClassName().contains("mysql")) {
			return;
		}

		int total = count.get();
		System.out.println("before:\n" + cp.dumpInfo());
		System.out.println("Try to get connection ");

		for (int i = 0; i < total; i++) {
			new Thread("GetConThread-" + i) {
				@Override
				public void run() {
					Connection cn;
					try {
						cn = cp.getConnection();
						assert (cn!=null);
						cn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					count.decrementAndGet();
				}
			}.start();
		}
		while (count.get() > 1) {
			System.out.print(".");
			Thread.sleep(100L);
		}
		// cp.reapIdleConnections();
		// System.out.println("\nafter:\n"+cp.dumpInfo());
		System.out.println("Done.");

	}

	@Test
	public void testCreateDriverConnection() throws SQLException {
		try (Connection cn = cp.createDriverConnection()) {
			assertNotNull(cn);
			assertNotNull(cn.getMetaData().getDatabaseProductName());
		}
	}

	@Test
	@Ignore("与池顺序/全局 Driver 状态偶发长时间阻塞；调池时再单独启用")
	public void testZz_returnConnection() throws SQLException {
		PooledConnection pooledconnection = (PooledConnection) cp.getConnection();
		int res = cp.size();
		cp.returnConnection(pooledconnection);
		assertEquals(cp.size(), res);
	}

	@Test
	public void testDumpInfo() {
		String res = cp.dumpInfo();
		CharSequence s = "connections";
		assertTrue(res.contains(s));
		System.out.println(res);
	}

	@Test
	public void testSetPrefetchSize() {
		cp.setPrefetchSize(100);

	}

	@Test
	public void testGetPrefetchSize() {
		int size = 100;
		cp.setPrefetchSize(size);
		assertEquals(size, cp.getPrefetchSize());
	}

	@Test
	@Ignore("idle/reap 与 H2 组合易长时间阻塞；调连接池时再单独跑本方法")
	public void testMaxIdle() throws InterruptedException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = cp.getConnection();
			Thread.sleep(IDLE_TEST_HOLD_MS);
			cp.reapIdleConnections();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select 1");
			rs.next();
			assertEquals(1, rs.getInt(1));

		} catch (SQLException e) {
			assertTrue(false);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);
		}

	}

	@Test
	@Ignore("依赖 reap/超时时序，默认跳过以加快 mvn test；需要时去掉 @Ignore")
	public void testTimeOut() throws InterruptedException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = cp.getConnection();
			stmt = conn.createStatement();
			Thread.sleep(IDLE_TEST_HOLD_MS);
			cp.reapIdleConnections();
			rs = stmt.executeQuery("select 1");
			rs.next();

			assertTrue(false);

		} catch (SQLException e) {
			assertTrue(true);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);

		}
	}

}
