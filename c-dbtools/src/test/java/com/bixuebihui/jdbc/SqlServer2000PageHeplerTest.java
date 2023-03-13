package com.bixuebihui.jdbc;

import junit.framework.TestCase;

import java.sql.SQLException;

public class SqlServer2000PageHeplerTest extends TestCase {


	public void testGetLimitString() throws SQLException {
		String querySelect="select * from table1 order by id, names";
		int offset=10;
		int limit=5;
		String sql = SqlServer2000PageHelper.getLimitString(querySelect, offset, limit);
		System.out.println(sql);

	}

	public void testReversOrderbyClause() {
		String substring = " a, b desc, c asc";
		String a= SqlServer2000PageHelper.reversOrderbyClause(substring);
		System.out.println(a);
		assertEquals("a desc,b,c desc", a);
	}

}
