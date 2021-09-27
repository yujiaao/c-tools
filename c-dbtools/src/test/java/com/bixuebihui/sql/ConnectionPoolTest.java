package com.bixuebihui.sql;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.bixuebihui.datasource.DataSourceTest;
import com.bixuebihui.dbcon.DatabaseConfig;

import com.bixuebihui.util.DbUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.condition.DisabledIf;

import static org.junit.Assert.*;

@DisabledIf("!com.bixuebihui.datasource.DataSourceTest.isMysqlAvailable()")
public class ConnectionPoolTest {

	private static final int _checkoutSeconds = 1;

	DatabaseConfig ds = DataSourceTest.getConfigMaster();

	private int _timeoutMilliSeconds = 100;
	private int _maxPrepStmts = 10;

	ConnectionPool cp = new ConnectionPool(ds.getAlias(), ds.getDburl(), ds.getUsername(), ds.getPassword(),
			ds.getMaxActive(), _timeoutMilliSeconds, _checkoutSeconds * 1000, ds.getMaxCheckOutCount(), _maxPrepStmts);

	@Before
	public void setUp() {
		cp.setTracing(true);
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
		cp.getConnection();
		int res = cp.getNumRequests();
		assertTrue(res > 0);
		System.out.println("getNumRequests = " + res);
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
	@DisabledIf("!com.bixuebihui.datasource.DataSourceTest.isMysqlAvailable()")
	public void testRemoveAllConnections() throws SQLException {
		cp.getConnection();
		int res = cp.size();
		assertTrue(res > 0);
		System.out.println("size before removeAllConnections = " + res);
		cp.removeAllConnections();
		res = cp.size();
		assertEquals(0, res);
		System.out.println("size after removeAllConnections = " + res);
	}

	volatile AtomicInteger count = new AtomicInteger(1000);

	@Test
	@DisabledIf("!com.bixuebihui.datasource.DataSourceTest.isMysqlAvailable()")
	public void testGetConnection() throws InterruptedException {
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
	@DisabledIf("!com.bixuebihui.datasource.DataSourceTest.isMysqlAvailable()")
	public void testCreateDriverConnection() throws SQLException {
		Connection cn = cp.createDriverConnection();
		assertNotNull(cn);
		System.out.println(cn.getClientInfo());
	}

	@Test
	@DisabledIf("!com.bixuebihui.datasource.DataSourceTest.isMysqlAvailable()")
	public void testReturnConnection() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		DriverManager.registerDriver((Driver) Class.forName(ds.getClassName()).getDeclaredConstructor().newInstance());

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
	@DisabledIf("!com.bixuebihui.datasource.DataSourceTest.isMysqlAvailable()")
	public void testMaxIdle() throws InterruptedException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = cp.getConnection();
			Thread.sleep(_checkoutSeconds * 2 * 1000);
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
	public void testTimeOut() throws InterruptedException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = cp.getConnection();
			stmt = conn.createStatement();
			Thread.sleep(_checkoutSeconds * 2 * 1000);
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
