package com.bixuebihui.jsp;

import junit.framework.TestCase;

public class UrlUtilTest extends TestCase {


	public void testAddParam() {
		String value="check";
		String name="q";
		String url="http://www.dev.bixuebihui.com/zichangl/desk/adminfrm_desk.jsp?ad=120&sf=123";
		assertEquals(url+"&"+name+"="+value,	UrlUtil.addOrReplaceParam(url, name, value));

		url = "http://b.a.s?q=true";
		assertEquals("http://b.a.s?q=check",	UrlUtil.addOrReplaceParam(url, name, value));

		url = "http://b.a.s?a=b&q=true";
		assertEquals("http://b.a.s?a=b&q=check",	UrlUtil.addOrReplaceParam(url, name, value));

		url = "http://b.a.s?q=b&q1=true";
		assertEquals("http://b.a.s?q=check&q1=true",	UrlUtil.addOrReplaceParam(url, name, value));

		url = "http://b.a.s?q=a,b&q1=true";
		value="a,b,c";
		assertEquals("http://b.a.s?q=a,b,c&q1=true",	UrlUtil.addOrReplaceParam(url, name, value, false));
	}

	public String operation(String a, String b) {
		a = b;
		return a;
	}

	public  void testStringFunc(){
		String a = "";
		String b = "test";
		assertEquals(b,this.operation(a, b));
		assertEquals("", a);
	}
}
