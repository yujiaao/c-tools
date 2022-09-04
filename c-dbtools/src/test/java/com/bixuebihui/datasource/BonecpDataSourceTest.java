//package com.bixuebihui.datasource;
//
//import java.sql.SQLException;
//
//import com.bixuebihui.dbcon.DatabaseConfig;
//
//import junit.framework.TestCase;
//import org.junit.jupiter.api.condition.DisabledIf;
//
//
//public class BonecpDataSourceTest extends TestCase {
//
//	@DisabledIf("!com.bixuebihui.datasource.DataSourceTest.isMysqlAvailable()")
//	public void testGetConnection() throws SQLException {
//		BonecpDataSource ds = new BonecpDataSource();
//
//		DatabaseConfig cfg;
//		try {
//			cfg = DataSourceTest.getConfig();
//		}catch (RuntimeException e){
//			e.printStackTrace();
//			return;
//		}
//		ds.setDriverClass(cfg.getClassName());
//		ds.setJdbcUrl(cfg.getDburl());
//		ds.setUsername(cfg.getUsername());
//		ds.setPassword(cfg.getPassword());
//		DataSourceTest.dataSourceTest(ds);
//
//	}
//
//	public void testSetDatabaseConfig() throws SQLException {
//		BonecpDataSource ds = new BonecpDataSource();
//
//		ds.setDatabaseConfig(DataSourceTest.getConfig());
//
//		DataSourceTest.dataSourceTest(ds);
//	}
//}
