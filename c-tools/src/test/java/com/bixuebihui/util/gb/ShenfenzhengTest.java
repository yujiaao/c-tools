package com.bixuebihui.util.gb;

import junit.framework.TestCase;

import java.text.ParseException;
import java.util.Date;

public class ShenfenzhengTest extends TestCase {

	public void testIsValidCardID() {
		assertTrue(Shenfenzheng.isValidCardID("232301197311123155"));
		assertFalse(Shenfenzheng.isValidCardID("232301197312123155"));
		assertFalse(Shenfenzheng.isValidCardID("23230119731212315X"));
	}

	public void testGetSexFromCardID() {
		assertEquals(1, Shenfenzheng.getSexFromCardID("232301197311123155"));
	}

	@SuppressWarnings("deprecation")
	public void testGetDateFromCardID() throws ParseException {
		assertEquals(new Date(1973-1900,10,12), Shenfenzheng.getDateFromCardID("232301197311123155"));
	}


	public void testGetRandomCardID() {
		Shenfenzheng sfz = new Shenfenzheng();
		assertTrue( Shenfenzheng.isValidCardID(sfz.getRandomCardID()));
	}
}
