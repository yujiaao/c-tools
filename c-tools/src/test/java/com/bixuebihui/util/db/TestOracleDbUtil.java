package com.bixuebihui.util.db;

import junit.framework.TestCase;

public class TestOracleDbUtil extends TestCase {

	public void testSqlFilterOneMonth()
	{
		System.out.println(OracleDbUtil.sqlFilterMonth("dj_rq", 2006, 10, 0));
		System.out.println(OracleDbUtil.sqlFilterMonth("dj_rq", 2006, 12, 0));
		System.out.println(OracleDbUtil.sqlFilterMonth("dj_rq", 2006, 12, 1));
		System.out.println(OracleDbUtil.sqlFilterMonth("dj_rq", 2006, 4, 14));
		System.out.println(OracleDbUtil.sqlFilterMonth("dj_rq", 2006, 4, -2));
		System.out.println(OracleDbUtil.sqlFilterMonth("dj_rq", 2006, 4, -13));
		System.out.println(OracleDbUtil.sqlFilterMonth("dj_rq", 2006, 4, 5));

		System.out.println(OracleDbUtil.sqlFilterOneMonth("dj_rq00", 2006, 12));

		System.out.println(OracleDbUtil.sqlFilterYear("dj_rq", 2006, 1));

	}

}
