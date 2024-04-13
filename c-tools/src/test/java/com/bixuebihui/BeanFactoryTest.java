package com.bixuebihui;

import com.bixuebihui.dbcon.DatabaseConfig;
import com.bixuebihui.shardingjdbc.core.util.DataSourceUtil;
import junit.framework.TestCase;
import org.junit.jupiter.api.Disabled;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;

public class BeanFactoryTest extends TestCase {

	public void testBeanFactory() {

		String _sObjectId="dbConfig";
		DatabaseConfig obj = (DatabaseConfig) BeanFactory.createObjectById(_sObjectId);
		System.out.println(obj+obj.getDburl());
	}

	public void testCreateObjectById() {

		Object o = BeanFactory.createObjectById("dbConfig");
		assertNotNull(o);
		System.out.println(o);
	}


	@Disabled
	//TODO implement transaction manager
	public void testTx(){
		//DataSource ds= DataSourceUtil.getDataSource();
		//DataSourceUtils.getConnection(ds);
	}


}
