package com.bixuebihui.util;

import com.bixuebihui.util.gb.Shenfenzheng;
import junit.framework.TestCase;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ShenfenzhengTest extends TestCase {

	public void testValidCardId() {
		assertTrue(Shenfenzheng.isValidCardID("232301197311123155"));
		assertFalse(Shenfenzheng.isValidCardID("23230119731112315X"));
		assertFalse(Shenfenzheng.isValidCardID("23230119731112315A"));

	}

	public void testGetDateFromCardId() throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.set(1974 , 6-1, 30,0,0,0);
		cal.set(Calendar.MILLISECOND,0);
		Date dt =cal.getTime();
		//new Date(1974 - 1900, 6 - 1, 30);
		assertEquals(dt.toString(), Shenfenzheng.getDateFromCardID("110111740630031").toString());

		//Date dt1 = new Date(1973 - 1900, 11 - 1, 12);
		Calendar cal1 = Calendar.getInstance();
		cal1.set(1973 , 11 - 1, 12,0,0,0);
		cal.set(Calendar.MILLISECOND,0);

		Date dt1 =cal1.getTime();
		assertEquals(dt1.toString(), Shenfenzheng.getDateFromCardID("232301197311123155").toString());
	}
}
