package com.bixuebihui.util.html.publish;

import com.bixuebihui.jsp.UrlUtil;
import com.bixuebihui.util.Config;
import junit.framework.TestCase;

import java.io.IOException;

public class TestUrl2html extends TestCase {


	public void writeToFile() throws IOException
	{
		Url2html uh = new Url2html();

		String url = "http://localhost:8080/zichangl/desk/admin_desk.jsp";
		String filename = "c:\\010101.htm";

		url = UrlUtil.addOrReplaceParam(url, "syscode",Config.getProperty("system.code"));

		uh.writeFile(url, filename);

	}
	public void testWriteToFile() throws IOException
	{
		//writeToFile();
	}
}
