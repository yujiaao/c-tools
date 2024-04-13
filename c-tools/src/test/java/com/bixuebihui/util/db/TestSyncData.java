package com.bixuebihui.util.db;

import com.bixuebihui.jdbc.IDbHelper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * 这个例子可以演示EasyMock的基本使用方式
 * EasyMock是解剖式的测试, 测重于执行流程, 如果代码流程发生改变则需改写测试
 * 与之相对比的是Mockito,主要测重于结果的测试,对中间执行过程并不关心.
 *
 * @author xingwx
 *
 */
public class TestSyncData extends EasyMockSupport {

	private static final String update = " update shebeitaizhang set SB_ID=?,BEIZHU=?,TM=?  where sb_id in(1000,2000)";
	private static final String select = " select SB_ID,BEIZHU,TM from shebeitaizhang  where sb_id in(1000,2000)";
	private String whereClause = " where sb_id in(1000,2000)";
	String[] fields={"SB_ID","BEIZHU","TM"};

	IDbHelper dbHelper ;
	IDbHelper  dbHelper0 ;
	Connection conn;
	PreparedStatement p;
	private String 	tableName = "shebeitaizhang";
;

	@Before
	public void setUp() {
		dbHelper = mock(IDbHelper.class);
		dbHelper0 = mock(IDbHelper.class);
		conn = mock(Connection.class);
		p = niceMock(PreparedStatement.class);
	}


	@Test
	public void testSyncTable() throws SQLException{
		List<Map<String, Object>> v = new Vector();
		Map h = niceMock(Hashtable.class);
		v.add(h);

		expect(dbHelper.exeQuery(select)).andReturn(v);
	    expect(dbHelper0.getConnection()).andReturn(conn);
	    expect(conn.prepareStatement(anyString())).andReturn(p);
	    expect(p.executeUpdate()).andReturn(2);

	    conn.close();

	    replayAll(); // replay all mocks at once

		SyncData sd = new SyncData(dbHelper,dbHelper0);



		assertEquals("must be 2?",2,sd.synUpdateTable(tableName, fields, whereClause,"SB_ID"));

	    verifyAll(); // verify all mocks at once

	}

	@Test
	public void testFormSelectSql()
	{
		SyncData sd = new SyncData(dbHelper,dbHelper);

		String res = sd.formSelectSql(tableName, fields, whereClause);
		assertEquals(select, res);
		res = (sd.formUpdateSql(tableName, fields, whereClause));
		assertEquals(update, res);

	}

}
