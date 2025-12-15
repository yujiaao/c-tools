package com.bixuebihui.session;

import com.bixuebihui.util.other.CMyException;
import junit.framework.TestCase;

import java.util.Date;
import java.util.Hashtable;

public class MemoSessionManagerTest extends TestCase {

	public void testRead() throws CMyException {
		System.out.println("session bench begin>>>");
		MemoSessionManager sm = new MemoSessionManager(new Hashtable());
		SimpleSession ss = null;
		Date start = new Date();
		for (int i = 0; i < 1000; i++) {
			ss = sm.createUUIDSession();
			sm.write(ss);
			ss = sm.read(ss.getS_id());
		}
		Date end = new Date();
		System.out.println(ss.toXml());
		System.out.println("create & read 1000 sessions used time: "
				+ (end.getTime() - start.getTime()) / 1000.0 + "s");
		System.out.println("collected session: "
				+ sm.gc((new Date()).getTime() + SimpleSession.SESSION_LIFE));
		System.out.println("session bench done.");
	}

	public void testInsert() throws CMyException {

		MemoSessionManager sm = new MemoSessionManager(new Hashtable());
		SimpleSession ss = null;
		Date start = new Date();
		int count=1000;
		for (int i = 0; i < count; i++) {
			ss = sm.createUUIDSession();
			sm.insert(ss);
			ss = sm.read(ss.getS_id());
		}
		assertEquals(count, sm.getCount());

		Date end = new Date();
		//System.out.println(ss.toXml());
		System.out.println("insert & read 1000 sessions used time: "
				+ (end.getTime() - start.getTime()) / 1000.0 + "s");
		System.out.println("collected session: "
				+ sm.gc((new Date()).getTime() + SimpleSession.SESSION_LIFE));
		System.out.println("session bench done.");
	}



	public void testWrite() throws CMyException {
		MemoSessionManager sm = new MemoSessionManager();
		SimpleSession ss = sm.createUUIDSession();
		 sm.write(ss);
		System.out.println(ss.toXml());
	}

}
