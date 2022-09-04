package com.bixuebihui.util;


import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DateConverterTest {

	@Test
	public void testConverter() {
		String str = "19890520";
		Date res = DateConverter.converter(str);

		assertEquals(res, DateConverter.makeDate(1989, 5, 20));

		String str1 = "1989-12-20";
		Date res1 = DateConverter.converter(str1);
		assertEquals(res1, DateConverter.makeDate(1989, 12, 20));

		assertEquals(DateConverter.converter("1989-05-20"), DateConverter.converter("19890520"));
		assertEquals(DateConverter.converter("1989-05-20"), DateConverter.converter("1989年05月20日"));
	}


	@Test
	public void testPadYear(){
		assertEquals("1981", DateConverter.padYear("81"));
		assertEquals("2019", DateConverter.padYear("19"));
		assertEquals("2019", DateConverter.padYear("2019"));
		assertEquals("1900", DateConverter.padYear("0"));
		assertEquals("1900", DateConverter.padYear(""));
	}




}
