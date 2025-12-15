package com.bixuebihui.util.gb;

import junit.framework.TestCase;

public class HKShenfenzhengTest extends TestCase {


	public void testIsValid() {
		String id="com.bixuebihui.util.gb.HKShenfenzheng";

		assertFalse(HKShenfenzheng.isValid(id));
		id="Z12345678";

		assertFalse(HKShenfenzheng.isValid(id));

		id="Z1234567(8)";

		assertFalse(HKShenfenzheng.isValid(id));

		id="Z687485(2)";

		assertTrue(HKShenfenzheng.isValid(id));

	}

}
