package com.bixuebihui.excel;

import junit.framework.TestCase;

public class BaseExcelInputTest extends TestCase {

	public void testFilterOutSpace() {
		String res = "郭  兵 ";
		res = BaseExcelInput.filterOutSpace(res);
		System.out.println(res);
		assertEquals(res,"郭兵");


		res = " KENENISA LELISA HIRPHA";
		res = BaseExcelInput.filterOutSpace(res);
		assertEquals(res, "KENENISA LELISA HIRPHA");
	}

}
