package com.bixuebihui.util;

import com.bixuebihui.util.other.CMyException;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.bixuebihui.util.ParameterUtils.isMultipartContent;


public class ParameterUtilsTest extends TestCase {

	public void testGetDate() {

		MockHttpServletRequest mock_request = new MockHttpServletRequest();
		String str = "1973-11-12";
		String paramName = "dt";
		// 搞一个mock
		mock_request.addParameter(paramName, str);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = ParameterUtils.getDate(mock_request, paramName);

		System.out.println(dt);
		Calendar car  = GregorianCalendar.getInstance();
		car.setTime(dt);
		assertEquals(str, format.format(dt));
		assertEquals(1973, car.get(Calendar.YEAR));
		assertEquals(11, car.get(Calendar.MONTH) + 1);
		assertEquals(12, car.get(Calendar.DAY_OF_MONTH));
	}

	public void testStrToDate() {

		String str = "1973-11-12 12:01:00";
		// 搞一个mock
		Date dt = ParameterUtils.strToDate(str);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar car  = GregorianCalendar.getInstance();
		car.setTime(dt);
		assertEquals("1973-11-12", format.format(dt));
		assertEquals(1973, car.get(Calendar.YEAR));
		assertEquals(11, car.get(Calendar.MONTH) + 1);
		assertEquals(12, car.get(Calendar.DAY_OF_MONTH));
	}

	public void testMap2List() {
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		for (int i = 0; i < 100; i++) {
			query_pairs.put("key" + i, Math.sin(i) + "");
		}
		List<String> list = new ArrayList<String>();
		list.addAll(query_pairs.keySet());

		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
			assertEquals("key" + i, list.get(i));
		}

	}

	public void testGetString() {

		MockHttpServletRequest mock_request = new MockHttpServletRequest();

		String paramName = "b";
		// String queryString="http://sample.com/a.html?b=abcd#xxx#ddd";
		String queryString = "abcd#xxx#ddd";
		// 搞一个mock
		mock_request.setParameter(paramName, queryString);
		mock_request.setMethod("GET");

		String res = ParameterUtils.getString(mock_request, paramName);

		assertEquals("abcd", res);

		paramName = "cssleft";
		queryString = "-600";
		mock_request.setParameter(paramName, queryString);

		long longRes = ParameterUtils.getLong(mock_request, paramName);

		assertEquals(-600, longRes);

	}

	public void testGetInt() {

		MockHttpServletRequest mock_request = new MockHttpServletRequest();

		String paramName = "b";
		// String queryString="http://sample.com/a.html?b=abcd#xxx#ddd";
		String queryString = "123";
		// 搞一个mock
		mock_request.setParameter(paramName, queryString);

		int res = ParameterUtils.getInt(mock_request, paramName);

		assertEquals(res, 123);

		queryString = "";
		mock_request.setParameter(paramName, queryString);
		res = ParameterUtils.getInt(mock_request, paramName);
		assertEquals(res, 0);

		queryString = null;
		mock_request.setParameter(paramName, queryString);
		res = ParameterUtils.getInt(mock_request, paramName);
		assertEquals(res, 0);

		queryString = "abc";
		mock_request.setParameter(paramName, queryString);
		res = ParameterUtils.getInt(mock_request, paramName);
		assertEquals(res, 0);

		queryString = "１２３";
		mock_request.setParameter(paramName, queryString);
		res = ParameterUtils.getInt(mock_request, paramName);
		assertEquals(res, 123);

	}

	public void testGetArabNum() {

		String src = "";
		String res = ParameterUtils.getAlabNum(src, 0 + "");

		assertEquals(res, "0");

		src = "123";
		res = ParameterUtils.getAlabNum(src, 0 + "");
		assertEquals(res, "123");

		src = "123.wer";
		res = ParameterUtils.getAlabNum(src, 0 + "");
		assertEquals(res, "123.");

		src = "１２３123.wer";
		res = ParameterUtils.getAlabNum(src, 0 + "");
		assertEquals(res, "123123.");

		src = "123１２３123.wer";
		res = ParameterUtils.getAlabNum(src, 0 + "");
		assertEquals(res, "123123123.");

		src = "123１２３123.wer0";
		res = ParameterUtils.getAlabNum(src, 0 + "");
		assertEquals(res, "123123123.0");

		src = "-123";
		res = ParameterUtils.getAlabNum(src, 0 + "");
		assertEquals(res, "-123");
	}

	public void testDate() {
		System.out.println(new Date(0));
		assertTrue(new Date(0).getTime() == 0);

	}

	public void testMultiPart() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		assertFalse(isMultipartContent(request));
	}

	public void testEncryptParam() throws CMyException {
		Hashtable<String, String> map = new Hashtable<String, String>();
		map.put("abc", "123");
		map.put("xyz", "我是谁？");

		String res = ParameterUtils.encryptParam(map);
		System.out.println(res);

		assertTrue(ParameterUtils.verifySignedParams(map));

		MockHttpServletRequest request = new MockHttpServletRequest();

		String name = "myparam";
		request.setParameter(name, res);

		Map<String, String> o = ParameterUtils.decryptParam(request, name);
		assertEquals(map.get("abc"), o.get("abc"));
		assertEquals(map.get("xyz"), o.get("xyz"));
		System.out.println(o);

	}

	public void testMap2String() {
		Hashtable<String, String> map = new Hashtable<String, String>();

		map.put("xyz", "我是谁？");

		String res = ParameterUtils.map2ParameterStringExceptSIG(map);
		assertEquals("xyz=%E6%88%91%E6%98%AF%E8%B0%81%EF%BC%9F", res);

		map.put("abc", "123");

		res = ParameterUtils.map2ParameterStringExceptSIG(map);
		assertEquals("abc=123&xyz=%E6%88%91%E6%98%AF%E8%B0%81%EF%BC%9F", res);

		map.put("sig", "test");

		res = ParameterUtils.map2ParameterStringExceptSIG(map);
		assertEquals("abc=123&xyz=%E6%88%91%E6%98%AF%E8%B0%81%EF%BC%9F", res);

	}

	public void testEncryptParamOne() throws CMyException {
		Hashtable<String, String> map = new Hashtable<String, String>();

		map.put("xyz", "我是谁？");

		String res = ParameterUtils.encryptParam(map);
		System.out.println(res);

		assertTrue(ParameterUtils.verifySignedParams(map));

		MockHttpServletRequest request = new MockHttpServletRequest();

		String name = "param";
		request.setParameter(name, res);

		Map<String, String> o = ParameterUtils.decryptParam(request, name);

		assertEquals(map.get("xyz"), o.get("xyz"));
		System.out.println(o);

	}

	public void testTimeOutParam() {
		// String
		// enc="1369C2C2187B930C2AABEE80F395B64403848438C700E4A623E8DB2485CE8FE010A45B5E385B7ED0649D0A50FBCF05A12EF106749F9196D15FE9C1B3FA4BB5F7F40C853731157B80EDA1DFDF6E071FC3";
		String enc = "uG-isGlEk_w5lUzJ8AFxgeVvgLFN10WNIEhIpH-17Vfak9-LzuIbeO6NGdTUACpAmHKVvdUh_0Ztq5dff-FV5aV0stR5N0ZBGFx1SHq9HHwJJFXMeWWz6XCQRaHlY0qGlP990lW2aZGWsWn-jCVMew";
		try {
			String key = Config.getProperty("parameter_encrypt_key16");
			ParameterUtils.decryptString(enc, key, 60 * 15);
		} catch (CMyException e) {

			assertTrue(e.getMessage(), e.getMessage().indexOf("参数已过期") >= 0);

		}

	}

	public void testTimeOutParam1() {
		// String
		// enc="1369C2C2187B930C2AABEE80F395B64403848438C700E4A623E8DB2485CE8FE010A45B5E385B7ED0649D0A50FBCF05A12EF106749F9196D15FE9C1B3FA4BB5F7F40C853731157B80EDA1DFDF6E071FC3";
		String enc = "k7xq2AQsc1ZXEdKbuc77-cOq51HwuD-lo5K9iyzE3WY6rpqGfWpgmDA30TM1aHkPaxqWCkCw2wIpddsZGso_qGhRTAVAf1ozXYJzAlSr2h8";
		try {
			MockHttpServletRequest request = new MockHttpServletRequest();
			String name = "param";
			request.addParameter(name, enc);
			Map<String, String> res = ParameterUtils.decryptParam(request, name);

			System.out.println(res);
		} catch (CMyException e) {
			assertTrue(true);
			assertTrue(e.getMessage().indexOf("参数已过期") >= 0);
			// e.printStackTrace();
		}

	}

	public void testMd5() throws CMyException{
		String s="_time=1380330085&abc=123&xyz=%E6%88%91%E6%98%AF%E8%B0%81%EF%BC%9F";
		String res = ParameterUtils.md5(s);

		assertEquals("c5494a94144766abfa907d9dad2b6135", res);

		s="_time=1380330085&abc=123&xyz=我是谁？";
		res = ParameterUtils.md5(s);

		assertEquals("de1424bac56a40a6b1e6d6154c090334", res);

	}

	public void testTimeOutParamSig() {


		try {
			String source = "_time=1380330085&abc=123&xyz=%E6%88%91%E6%98%AF%E8%B0%81%EF%BC%9F&sig=c5494a94144766abfa907d9dad2b6135";


			String enc = "jB8et9gRJWCxwLGXeb0Ub0PvNPxF-MsM_3Adg3o_CUa2PaQ3FNPs3t5mwLrzWdr-dSRf0O7HD_sB6iMghwTJ30LnDa8do2GhU8e_F33D3Zv7NCM5Uq2d4d-TRfcXOQO8-icMjcG81L62LxfamyBmQQ";
			String e = EncryptUtil.encrypt64(source, Config.getProperty("parameter_encrypt_key16"));

			assertEquals(enc, e);

			MockHttpServletRequest request = new MockHttpServletRequest();
			String name = "param";
			request.addParameter(name, enc);
			Map<String, String> res = ParameterUtils.decryptParam(request, name);

			System.out.println(res);
		} catch (CMyException e) {
			assertTrue(e.getMessage(), e.getMessage().indexOf("参数已过期") >= 0);
		}

	}

	public void testParamBadString() {
		// String
		// enc="1369C2C2187B930C2AABEE80F395B64403848438C700E4A623E8DB2485CE8FE010A45B5E385B7ED0649D0A50FBCF05A12EF106749F9196D15FE9C1B3FA4BB5F7F40C853731157B80EDA1DFDF6E071FC3";
		String enc = "uG-isGlEk_w5lUzJ8AFxgeVvgLFN10WNIEhIpH-17Vfak9-LzuIbeO6NGdTUACpAmHKVvdUh_0Ztq5dff-FV5bHycZ5Ff6w7E3EWjPUrFe6-mhRAB5C_X5it9Eoqjj9ScOa7XCnjsFI5JwATUn8TzQ";
		try {
			String key = Config.getProperty("parameter_encrypt_key16");
			ParameterUtils.decryptString(enc, key, 60 * 15);
		} catch (CMyException e) {

			e.printStackTrace();
			assertTrue(e.getMessage().indexOf("encode") >= 0);

		}

	}

	public void testParamStringWithKey() {
		// String
		// enc="1369C2C2187B930C2AABEE80F395B64403848438C700E4A623E8DB2485CE8FE010A45B5E385B7ED0649D0A50FBCF05A12EF106749F9196D15FE9C1B3FA4BB5F7F40C853731157B80EDA1DFDF6E071FC3";
		String s="_time=1394527118&value=la<Lh~&*(asMB%s8.%*nP@*Hjkshf/?apiwuN_lny&sig=e8ed42afab7058fc9084e59c9e378627";
		String enc = "YErgSUSUQ1FDW-uZPCY1srYHWGIxH7y-dhXBzDA9CQsMQ6U3qHUK3shLmVJElrmd8540AL9VgOvoMHnEEWu3S2DEjy0MMZUOgIdRGGiQCPXMSjpFuma7Qy0sOSBP1DM1rIgVscRZM6xxwt4EWQSBag";

		try {
			Map<String, String> params =new HashMap<String,String>();
			params.put("_time", "1394527118");
			params.put("value", "la<Lh~&*(asMB%s8.%*nP@*Hjkshf/?apiwuN_lny");

			String encryptKey = "*Hx#10>LMB%s8./?";
			String e = ParameterUtils.encryptParam(params, encryptKey);

			Map<String, String> res = ParameterUtils.decryptString(e, encryptKey, 60 * 15);

			assertEquals(params.get("value"), res.get("value"));



			ParameterUtils.decryptString(enc, encryptKey, 60 * 15);
		} catch (IllegalArgumentException | CMyException e) {
			assertTrue(e.getMessage(), e.getMessage().indexOf("Bad input string format") >= 0);
		}

	}


	public  void testGetClientIP(){
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("X-Forwarded-For", "223.223.192.231,192.168.1.18");

		String ip  = ParameterUtils.getClientIP(request);

		assertEquals("223.223.192.231", ip);

		request = new MockHttpServletRequest();
		request.addHeader("HTTP_CLIENT_IP", "223.223.192.232");
		ip  = ParameterUtils.getClientIP(request);

		assertEquals("223.223.192.232", ip);

	}

}
