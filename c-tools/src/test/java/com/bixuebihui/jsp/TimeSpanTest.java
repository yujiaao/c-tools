package com.bixuebihui.jsp;


import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Calendar;

public class TimeSpanTest extends TestCase {



	public void testGetMysqlSqlCondition() {
		TimeSpan tsSpan = new TimeSpan();
		String res = tsSpan.getMysqlSqlCondition("createDate");
		assertEquals("",res);

		Calendar curDate = Calendar.getInstance();
		Calendar agoDate = Calendar.getInstance();
				agoDate.add(Calendar.MONTH, -2);
		tsSpan.init(curDate, agoDate);

		res = tsSpan.getMysqlSqlCondition("createdDate");
		System.out.println(res);



		TimeSpan auditTimespan = new TimeSpan();
		auditTimespan.setPrefix("adt");
		res = auditTimespan.getMysqlSqlCondition("abc");
		assertEquals("",res);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("adtbdate", "2012-4-26");

		auditTimespan.setDates(request);
		res = auditTimespan.getMysqlSqlCondition("abc");
		assertEquals(" and abc>= str_to_date('2012-4-26','%Y-%m-%d')",res);

		request.addParameter("adtedate", "2012-6-26");
		auditTimespan.setDates(request);
		res = auditTimespan.getMysqlSqlCondition("abc");
		assertEquals(" and abc>= str_to_date('2012-4-26','%Y-%m-%d') and abc<= str_to_date('2012-6-26','%Y-%m-%d')",res);
	}

	public void testGetHTML() {
		TimeSpan tsSpan = new TimeSpan();
		String res = tsSpan.getMysqlSqlCondition("createDate");
		assertEquals("",res);

		Calendar curDate = Calendar.getInstance();
		Calendar agoDate = Calendar.getInstance();
				agoDate.add(Calendar.MONTH, -2);
		tsSpan.init(curDate, agoDate);

		System.out.println(tsSpan.getHTML());
	}



	public void testSetDates(){

		TimeSpan auditTimespan = new TimeSpan();
		auditTimespan.setPrefix("adt");

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("adtbdate", "2012-4-26");
		request.addParameter("adtedate", "2012-6-26");

		auditTimespan.setDates(request);

		assertEquals(auditTimespan.getBYear(),2012);
		assertEquals(auditTimespan.getBMonth(),4);
		assertEquals(auditTimespan.getBDay(),26);

		assertEquals(auditTimespan.getEYear(),2012);
		assertEquals(auditTimespan.getEMonth(),6);
		assertEquals(auditTimespan.getEDay(),26);



	}


    public void testIsTimeSpan() {
		assertTrue(TimeSpan.isTimeSpan("2014-12-23~"));
		assertTrue(TimeSpan.isTimeSpan("~2014-12-23"));
		assertTrue(TimeSpan.isTimeSpan("2014-12-23~2020-11-11"));
    }
}
