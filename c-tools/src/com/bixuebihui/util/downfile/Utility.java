// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2006-1-26 15:56:59
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Utility.java

package com.bixuebihui.util.downfile;


/**
 * @author xwx
 */
public class Utility {

    public Utility() {
    }

    public static void sleep(int nSecond) {
        try {
            Thread.sleep(nSecond);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void log(String sMsg) {
        System.err.println(sMsg);
    }

    public static void log(int sMsg) {
        System.err.println(sMsg);
    }
}
