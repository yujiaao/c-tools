package com.bixuebihui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class LogTest {
	static final Logger LOG = LoggerFactory.getLogger(LogTest.class);

	Object o = new Object() {
		public String toString() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "this is slow! " + System.currentTimeMillis();
		}
	};

	@Test
	public void testGoFirstArriveFirst() throws InterruptedException {

			new Thread() {
				public void run() {
					LOG.info(o.toString());
				}
			}.start();

			Thread.sleep(100);

			new Thread() {
				public void run() {
					LOG.info("this is fast! " + System.currentTimeMillis());
				}
			}.start();

			Thread.sleep(2000);

	}


	@Test
	public void testGoFirstArriveLast() throws InterruptedException {

			new Thread() {
				public void run() {
					LOG.info(o.toString());
				}
			}.start();

			Thread.sleep(100);

			new Thread() {
				public void run() {
					LOG.info("this is fast! " + System.currentTimeMillis());
				}
			}.start();

			Thread.sleep(2000);

	}

}
