package com.bixuebihui.algorithm;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class LRULinkedHashMapTest  {


	@Test
	public void testRemoveObject() {
		int cap=5;
	Map<String, String> map = new  LRULinkedHashMap<String, String>(cap, new RemoveActionImpl());

		map.put("1", "1231");
		map.put("2", "1232");
		map.put("3", "1233");
		map.put("4", "1234");
		map.put("5", "1235");
		map.put("6", "1236");
		map.put("7", "1237");
		map.put("8", "1238");
		assertEquals(cap,map.size());
		map.remove("4");
		assertEquals(cap-1,map.size());
		map.clear();
		assertEquals(0,map.size());
	}

	public void testPutKV() {
		int cap =6;
		Map<String, String> map = new  LRULinkedHashMap<String, String>(cap, new RemoveActionImpl());

		map.put("1", "123");
		map.put("2", "123");
		map.put("3", "123");
		map.put("4", "123");
		map.put("5", "123");
		map.put("6", "123");
		map.put("7", "123");
		map.put("8", "123");
		assertEquals(cap,map.size());
	}

}
