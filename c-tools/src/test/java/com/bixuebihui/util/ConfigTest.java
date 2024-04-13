package com.bixuebihui.util;


import junit.framework.TestCase;
import org.apache.commons.lang3.time.StopWatch;

public class ConfigTest extends TestCase {
	public void testDbConfig()
	{
		Config.setProperty("top_back_color", "#006600");

		System.out.println("top_back_color="+Config.getProperty("top_back_color"));
		Config.setProperty("top_back_color", "#000066");
		assertTrue("#000066".equals(Config.getProperty("top_back_color")));
		System.out.println("top_back_color="+Config.getProperty("top_back_color"));
	}


	public void testZookeeper(){
		String res = Config.getProperty("beans.properties");
		System.out.println("beans.properties=\n"+res);

		res = Config.getProperty("abc");
		System.out.println("abc="+res);
	}

//	public void testGetRawData(){
//		//to avoid init time calculation
//		StopWatch sw=new StopWatch();
//
//		init(sw);
//		init(sw);
//
//		sw.start();
//		String path="/config/beans.properties";
//		String res = Config.getRawDataAsString(path);
//		sw.stop();
//
//		System.out.println("data="+res+"\n\n use time:"+sw.getTime());
//
//
//	}

	public void testGetPropertyChange() throws InterruptedException{
		//to avoid init time calculation
		StopWatch sw=new StopWatch();

		init(sw);
		init(sw);
			String path="myprop_zoo";
			Config.setProperty(path, "123");
		sw.start();


		String res = Config.getProperty(path);
		assertEquals(res,"123");
		sw.stop();

		System.out.println("data="+res+"\n\n use time:"+sw.getTime());

		Thread.sleep(15000);
		sw.reset();

		sw.start();
		Config.setProperty(path, "1234");
		path="myprop_zoo";
		res = Config.getProperty(path);
		sw.stop();
		assertEquals(res,"1234");
		System.out.println("data="+res+"\n\n use time:"+sw.getTime());
	}


	private void init(StopWatch sw) {
		sw.start();
		String s = Config.getProperty("config.zookeeper");
		sw.stop();
		System.out.println("config.zookeeper="+s+"\n use time="+sw.getTime());
		sw.reset();
	}

}
