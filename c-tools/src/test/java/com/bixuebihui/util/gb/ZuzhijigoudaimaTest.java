package com.bixuebihui.util.gb;

import com.bixuebihui.util.other.CMyException;
import junit.framework.TestCase;

public class ZuzhijigoudaimaTest extends TestCase {

	public void testDwdmjym() throws CMyException {
	Zuzhijigoudaima dm = new Zuzhijigoudaima();

		assertEquals('8', dm.dwdmjym("12345678"));

	}

	public void testDwdmjy() throws CMyException {
		Zuzhijigoudaima dm = new Zuzhijigoudaima();
			assertTrue( dm.dwdmjy("123456788"));
			assertFalse( dm.dwdmjy("123456789"));
	}

}
