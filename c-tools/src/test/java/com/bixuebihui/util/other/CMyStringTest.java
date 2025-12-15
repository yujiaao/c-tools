package com.bixuebihui.util.other;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CMyStringTest  {

	@Test
	public void testFilterForSQL() {
		String _sContent="a'a; dd";
		assertEquals("a''a dd", CMyString.filterForSQL(_sContent));
	}

    @Test
    public void getUTF8String() {
		byte[] b={(byte)0xFF-64, (byte)(0xB0 - 64), 0xC0|(byte)128, 0xC0|(byte)128,0xC0|(byte)128,};

		int i=0;
		//0xc0= 1100 0000b, 128= 1000 0000b
		if ((b[i++] & 0xc0) != 128 || (b[i++] & 0xc0) != 128)
		{
			System.out.println(i);
		}

		assertEquals(2, i);

		 i=0;
		//0xc0= 1100 0000b, 128= 1000 0000b
		if ((b[i++] & 0xc0) != 128)
		{
			System.out.println(i);
		}

		if ((b[i++] & 0xc0) != 128)
		{
			System.out.println(i);
		}

		assertEquals(2, i);
    }
}
