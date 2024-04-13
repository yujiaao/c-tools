package com.bixuebihui.util;

import junit.framework.TestCase;

public class TestUtil extends TestCase {

	public void testMakeQuotedStr()
	{
		System.out.println("trip '台式机' ="+Util.makeQuotedStr("'台式机'"));
		assertTrue("", Util.makeQuotedStr("'台式机'").equals("'''台式机'''"));
	}
	public void testTripQuotedString()
	{
		System.out.println("trip '台式机' ="+Util.tripQuotedString("'台式机'"));
		assertTrue("", Util.tripQuotedString("'台式机'").equals("台式机"));
	}

	public static void main(String[] args) {
		TestUtil t = new TestUtil();
		t.testMakeQuotedStr();
		t.testTripQuotedString();
	}
}
