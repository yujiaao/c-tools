//package com.bixuebihui.util.upfile;
//
//import junit.framework.TestCase;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class UploadFileTest extends TestCase {
//
//	public void testExtractFileExt() {
//		assertEquals("docx", UploadFile.extractFileExt("c:/a/a.b.c.docx"));
//		assertEquals("c", UploadFile.extractFileExt("c:/a/a.b.c"));
//		assertEquals("", UploadFile.extractFileExt("c:/a/a.b.c/bcbin"));
//	}
//
//	public void testGetFileStorePath() throws IOException {
//
//		System.out.println("path: " + UploadFile.getFileStorePath("."));
//
//	}
//
//	public void testConstructor() throws IOException {
//		Map<String, String> m = new HashMap<String, String>();
//		String fileExtention = ".exe";
//		m.put(fileExtention, fileExtention);
//
//		UploadFile f = new UploadFile(m, 1024 * 1024);
//		assertEquals(f.maxSize, 1024 * 1024);
//		assertTrue(f.isInWhiteList(fileExtention));
//		assertFalse(f.isInWhiteList(".zip"));
//
//	}
//
//	public void testGetNewFileName() {
//		assertEquals("docx", UploadFile.extractFileExt((new UploadFile())
//				.getNewFileName("fackfile.docx", 1)));
//
//	}
//
//	public void testGetBaseFileName() {
//		assertEquals("hot.txt", UploadFile
//				.getBaseFileName("http://a.b.com/hot.txt"));
//	}
//
//
//}
