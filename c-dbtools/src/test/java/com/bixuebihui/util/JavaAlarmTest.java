package com.bixuebihui.util;

import org.junit.Test;

public class JavaAlarmTest {

    volatile boolean stop = false;
    volatile long count = 0;

    @Test
    public void testNotify() throws TimeoutException, InterruptedException {
        System.out.println(count);
        new Thread() {
            public void run() {
                System.out.println(count);

                try {
                    sleep(10);
                    synchronized (this) {
                        notifyAll();
                    }
                    sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                System.out.println(count);
                stop = true;

                System.out.println(count);
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(count);
            }
        }.start();

        JavaAlarm ja = new JavaAlarm(() -> {
            while (!stop)
                count++;
        }, 100);

        synchronized (ja) {
            ja.notifyAll();
        }


    }
}
