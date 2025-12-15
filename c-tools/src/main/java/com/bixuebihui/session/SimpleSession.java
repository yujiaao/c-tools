package com.bixuebihui.session;

import java.util.Date;

public class SimpleSession {


    public static int SESSION_LIFE = 1000 * 60 * 30; //30分钟
    public static int GC_LIMIT = 1000;//每1000次INSERT, 一次DELETE
    private String appName = "DEFAULT_APP";
    private String serverNodeName = "DEFAULT_NODE";

    private String s_id;
    private int user_id;
    private Date s_start;
    private long s_expire;
    private String s_fp;//foot print

    public String getS_fp() {
        return s_fp;
    }

    public void setS_fp(String s_fp) {
        this.s_fp = s_fp;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public Date getS_start() {
        return s_start;
    }

    public void setS_start(Date s_start) {
        this.s_start = s_start;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getServerNodeName() {
        return serverNodeName;
    }

    public void setServerNodeName(String serverNodeName) {
        this.serverNodeName = serverNodeName;
    }

    public long getS_expire() {
        return s_expire;
    }

    public void setS_expire(long s_expire) {
        this.s_expire = s_expire;
    }

    public String toXml() {
        StringBuffer s = new StringBuffer();
        String ln = System.getProperty("line.separator");
        s.append("<SIMPLESESSION S_ID=\"").append(this.getS_id()).append("\" ");

        s.append("USER_ID=\"").append(this.getUser_id()).append("\" ");
        s.append("S_START=\"").append(this.getS_start()).append("\" ");
        s.append("S_EXPIRE=\"").append(this.getS_expire()).append("\" ");
        s.append("S_FP=\"").append(this.getS_fp()).append("\" ");
        s.append("SESSION_LIFE=\"").append(SESSION_LIFE).append("\" ");
        s.append("APPNAME=\"").append(appName).append("\" ");
        s.append("SERVERNODENAME=\"").append(serverNodeName).append("\" ");

        s.append("/>").append(ln);
        return s.toString();
    }


}
