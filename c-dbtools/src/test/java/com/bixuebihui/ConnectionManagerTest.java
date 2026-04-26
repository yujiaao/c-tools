package com.bixuebihui;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;

import com.bixuebihui.util.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bixuebihui.sql.ConnectionPool;

import junit.framework.TestCase;
import org.junit.jupiter.api.condition.DisabledIf;

import static org.hamcrest.MatcherAssert.assertThat;

public class ConnectionManagerTest extends TestCase {
	private static final Logger LOG = LoggerFactory.getLogger(ConnectionManagerTest.class);

	public void testGetInstance() throws Exception {
		LOG.info(ConnectionManager.getInstance().toString());
	}

	public void testGetConnection() throws Exception {
		Collection<ConnectionPool> e = ConnectionManager.getInstance().getPools();
		if (!e.isEmpty()) {
			ConnectionPool o =  e.iterator().next();
			LOG.info(o.toString());
		} else {
			LOG.info("no pool exists!");
		}
	}

	@DisabledIf("!com.bixuebihui.datasource.DataSourceTest.isMysqlAvailable()")
	public void testGetConnectionString() throws SQLException {
		ConnectionManager.getInstance().setTracing(true);
		LOG.info(ConnectionManager.getInstance().toString());
		String alias = "test1";
		LOG.info(ConnectionManager.getConnection(alias).toString());
	}

	volatile boolean finish=false;

	@DisabledIf("!com.bixuebihui.datasource.DataSourceTest.isMysqlAvailable()")
	public void testFreeConnection() throws SQLException, InterruptedException {
		if(!com.bixuebihui.datasource.DataSourceTest.isMysqlAvailable()){
			LOG.warn("MySQL is not available!");
			return;
		}
		LOG.info(ConnectionManager.getInstance().toString());

		final String alias = "test1";
		final Connection cn = ConnectionManager.getConnection(alias);
		LOG.info(cn.toString());
		String dbProduct = cn.getMetaData().getDatabaseProductName();
		if (dbProduct == null || !dbProduct.toLowerCase().contains("mysql")) {
			LOG.info("Skip lock-table test on non-MySQL database: {}", dbProduct);
			DbUtils.closeQuietly(cn);
			return;
		}

		Statement s = cn.createStatement();
		boolean res = s.execute("lock table t_log write");

		/**
		 * <code>true</code> if the first result is a <code>ResultSet</code>
		 * object; <code>false</code> if it is an update count or there are no results
		 */
		assertFalse(res);

		new Thread(() -> {
			Connection cn1 = null;
			try {

				cn1 = ConnectionManager.getConnection(alias);

				Statement s1 = cn1.createStatement();
				s1.setQueryTimeout(2);

				LOG.info("While table is locked and\n\t I get another connection\n\t TRY select form LOCKED table");

				boolean res1 = s1.execute("select count(*) from t_log");
				assert(res1);
				LOG.info("You can see this, because lock write, not read");

				s1.execute("delete from t_log where lid=1");
				s1.execute("insert into t_log (lid,uid,content)values(1,1,'test must fail')");
				s1.execute("delete from t_log where lid=1");

				LOG.error("YOU CAN NOT SEE THIS");
				fail("An exception is expected when writing to a locked table");
			} catch (SQLException e) {
				LOG.info(e.getSQLState());
				//assertEquals("08S01", e.getSQLState());
			}finally{
				DbUtils.closeQuietly(cn1);
			}

			 finish=true;
		}).start();

		while(!finish)
			Thread.sleep(10);

		//https://github.com/awaitility/awaitility/wiki/Getting_started
		//Awaitility.await().atMost(20, Duration.SECONDS).until(didTheThing()); // Compliant

		s.execute("unlock tables");
		s.close();
		cn.close();

	}

	public void testState() throws Exception {
		Collection<ConnectionPool> pools = ConnectionManager.getInstance().getPools();
		assertThat("", pools.size()>0);
		LOG.info(ConnectionManager.state());
	}

	public void testReset() throws Exception {
		ConnectionManager.getInstance();
		ConnectionManager.reset();
	}

	public void testAddAliasString() {
		ConnectionManager.reset();
		LOG.info(Arrays.toString(ConnectionManager.getAliases()));
		ConnectionManager.addAliasString("test1");
		LOG.info(Arrays.toString(ConnectionManager.getAliases()));
		ConnectionManager.addAliasString("test2");
		LOG.info(Arrays.toString(ConnectionManager.getAliases()));
		ConnectionManager.addAliasString("test3");
		LOG.info(Arrays.toString(ConnectionManager.getAliases()));
		ConnectionManager.addAliasString("test4");
		LOG.info(Arrays.toString(ConnectionManager.getAliases()));
	}

}
