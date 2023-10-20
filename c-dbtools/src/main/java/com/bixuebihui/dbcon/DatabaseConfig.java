package com.bixuebihui.dbcon;

import com.bixuebihui.filter.IFilter;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * 数据库(链接池)的基本参数
 *
 * @author yexf
 * @version $Id: $Id
 */
public class DatabaseConfig
{
	private String alias;
	private String className;
	private String dburl;
	private String username;
	private String password;
	private int maxIdle;
	private int maxActive;
	/** 最大等待时间,毫秒 */
	private long maxWaitTime;
	/** 每个连接最大的使用次数，超出后自动回收 //bitmechanicDatasource */
	private int maxCheckOutCount;
	/** DbcpDataSource & bitmechanicDatasource */
	private int maxOpenPreparedStatements;
	private int sleepIntervalInSeconds;
	/** for DruidDataSource */
	private boolean removeAbandoned;

    /**
     * <p>Constructor for DatabaseConfig.</p>
     */
    public DatabaseConfig(){
        alias = "";
        className = "";
        dburl = "";
        username = "";
        maxIdle = 2;
        maxActive = 300;

        /**
         *  最大等待时间,毫秒
         */
        maxWaitTime = 300000;

        /** 每个连接最大的使用次数，超出后自动回收 bitmechanicDatasource */
        setMaxCheckOutCount(5000);

        /** DbcpDataSource & bitmechanicDatasource */
        maxOpenPreparedStatements=200;
        sleepIntervalInSeconds = 30;
        setRemoveAbandoned(false);
    }

	private IFilter filter= null;

    /**
     * <p>Getter for the field <code>alias</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * <p>Setter for the field <code>alias</code>.</p>
     *
     * @param alias a {@link java.lang.String} object.
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * <p>Getter for the field <code>className</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getClassName() {
        return className;
    }

    /**
     * <p>Setter for the field <code>className</code>.</p>
     *
     * @param className a {@link java.lang.String} object.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * <p>Getter for the field <code>dburl</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDburl() {
        if(filter!=null){
            return filter.filter(dburl);
        }
        return dburl;
    }

    /**
     * <p>Setter for the field <code>dburl</code>.</p>
     *
     * @param dburl a {@link java.lang.String} object.
     */
    public void setDburl(String dburl) {
        this.dburl = dburl;
    }

    /**
     * <p>Getter for the field <code>maxActive</code>.</p>
     *
     * @return a int.
     */
    public int getMaxActive() {
        return maxActive;
    }

    /**
     * <p>Setter for the field <code>maxActive</code>.</p>
     *
     * @param maxActive a int.
     */
    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    /**
     * <p>Getter for the field <code>maxIdle</code>.</p>
     *
     * @return a int.
     */
    public int getMaxIdle() {
        return maxIdle;
    }

    /**
     * <p>Setter for the field <code>maxIdle</code>.</p>
     *
     * @param maxIdle a int.
     */
    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    /**
     * <p>Getter for the field <code>maxWaitTime</code>.</p>
     *
     * @return a long.
     */
    public long getMaxWaitTime() {
        return maxWaitTime;
    }

    /**
     * <p>Setter for the field <code>maxWaitTime</code>.</p>
     *
     * @param maxWaitTime a long.
     */
    public void setMaxWaitTime(long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    /**
     * <p>Getter for the field <code>password</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPassword() {
        return password;
    }

    /**
     * <p>Setter for the field <code>password</code>.</p>
     *
     * @param password a {@link java.lang.String} object.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * <p>Getter for the field <code>username</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUsername() {
        return username;
    }

    /**
     * <p>Setter for the field <code>username</code>.</p>
     *
     * @param username a {@link java.lang.String} object.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * <p>Getter for the field <code>filter</code>.</p>
     *
     * @return a {@link IFilter} object.
     */
    public IFilter getFilter() {
        return filter;
    }

    /**
     * <p>Setter for the field <code>filter</code>.</p>
     *
     * @param filter a {@link IFilter} object.
     */
    public void setFilter(IFilter filter) {
        this.filter = filter;
    }

    /**
     * <p>Getter for the field <code>maxOpenPreparedStatements</code>.</p>
     *
     * @return a int.
     */
    public int getMaxOpenPreparedStatements() {
        return maxOpenPreparedStatements;
    }

    /**
     * <p>Setter for the field <code>maxOpenPreparedStatements</code>.</p>
     *
     * @param maxOpenPreparedStatements a int.
     */
    public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
    }

    /**
     * <p>Getter for the field <code>sleepIntervalInSeconds</code>.</p>
     *
     * @return a int.
     */
    public int getSleepIntervalInSeconds() {
        return sleepIntervalInSeconds;
    }

    /**
     * <p>Setter for the field <code>sleepIntervalInSeconds</code>.</p>
     *
     * @param sleepIntervalInSeconds a int.
     */
    public void setSleepIntervalInSeconds(int sleepIntervalInSeconds) {
        this.sleepIntervalInSeconds = sleepIntervalInSeconds;
    }

    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String toString(){
        return this.getClass()+"(alias = "+alias+
                ",className = "+className+
                ",dburl = "+dburl+
                ",username = "+username+
                ",password = ******"+
                ",maxIdle = "+maxIdle+
                ",maxActive = "+maxActive+
                ",maxWaitTime = "+maxWaitTime+ //最大等待时间,毫秒
                ",maxCheckOutCount="+getMaxCheckOutCount()+ //每个连接最大的使用次数，超出后自动回收 //bitmechanicDatasource
                ",maxOpenPreparedStatements="+maxOpenPreparedStatements+   //DbcpDataSource & bitmechanicDatasource
                ",removeAbandoned="+isRemoveAbandoned()+
                ",sleepIntervalInSeconds = "+sleepIntervalInSeconds+")";
    }

    /**
     * <p>Getter for the field <code>maxCheckOutCount</code>.</p>
     *
     * @return a int.
     */
    public int getMaxCheckOutCount() {
        return maxCheckOutCount;
    }

    /**
     * <p>Setter for the field <code>maxCheckOutCount</code>.</p>
     *
     * @param maxCheckOutCount a int.
     */
    public void setMaxCheckOutCount(int maxCheckOutCount) {
        this.maxCheckOutCount = maxCheckOutCount;
    }

    /**
     * <p>isRemoveAbandoned.</p>
     *
     * @return a boolean.
     */
    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    /**
     * <p>Setter for the field <code>removeAbandoned</code>.</p>
     *
     * @param removeAbandoned a boolean.
     */
    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public static DatabaseConfig newInstance(Properties props) {
        DatabaseConfig config = new DatabaseConfig();
        config.setAlias(props.getProperty("alias"));
        config.setClassName(props.getProperty("className"));
        config.setDburl(props.getProperty("dburl"));
        config.setUsername(props.getProperty("username"));
        config.setPassword(props.getProperty("password"));
        if (StringUtils.trimToNull(config.getAlias()) == null) {
            config.setAlias("defaultdbcpalias");
        }
        return config;
    }

}
