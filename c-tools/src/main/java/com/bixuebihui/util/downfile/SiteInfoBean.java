// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2006-1-26 15:56:59
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   SiteInfoBean.java

package com.bixuebihui.util.downfile;


public class SiteInfoBean {

    public SiteInfoBean() {
        this("", "", "", 5);
    }

    public SiteInfoBean(String sURL, String sPath, String sName, int nSpiltter) {
        sSiteURL = sURL;
        sFilePath = sPath;
        sFileName = sName;
        nSplitter = nSpiltter;
    }

    public String getSSiteURL() {
        return sSiteURL;
    }

    public void setSSiteURL(String value) {
        sSiteURL = value;
    }

    public String getSFilePath() {
        return sFilePath;
    }

    public void setSFilePath(String value) {
        sFilePath = value;
    }

    public String getSFileName() {
        return sFileName;
    }

    public void setSFileName(String value) {
        sFileName = value;
    }

    public int getNSplitter() {
        return nSplitter;
    }

    public void setNSplitter(int nCount) {
        nSplitter = nCount;
    }

    private String sSiteURL;
    private String sFilePath;
    private String sFileName;
    private int nSplitter;
}
