package com.bixuebihui.jsp;

import com.bixuebihui.jdbc.ISqlConditionType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 年月日时间段的HTML控件，前后端及数据库的支持
 *
 * @author xingwx @ TODO 对时间选择对话框的集成，可以选择今天，上个月，上一周，去年等
 */
public class DateTimeSpan implements ISqlConditionType {

    public final static int MIN_BEGIN_YEAR = 1900;

    public final static int MAX_END_YEAR = 2100;

    private Date beginDate = null;
    private Date endDate = null;
    private String prefix = "";


    /**
     * 时间的提示文字 tooltips
     */
    private String title;
    private String iconPath = "images/calendar.gif";
    private String baseUrl = "../";


    public static DateTimeSpan build(String beginDate, String endDate) throws ParseException {
        DateTimeSpan ts = new DateTimeSpan();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date begin = sf.parse(beginDate);
        Calendar cb = Calendar.getInstance();
        cb.setTime(begin);

        Date end = sf.parse(endDate==null? MAX_END_YEAR+"-12-31": endDate);
        Calendar ce = Calendar.getInstance();
        ce.setTime(end);
        ts.init(ce, cb);
        return ts;
    }


    /**
     * 初始化日期，昨天和三个月前
     */
    public void init(Calendar curDate, Calendar agoDate) {

        beginDate = agoDate.getTime();
        endDate = curDate.getTime();

    }


    private boolean noEnd() {
        return endDate == null ;
    }

    private boolean noBegin() {
        return beginDate == null;
    }

    private boolean noBoth() {
        return noBegin() && noEnd();
    }




    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


    public String getIconPath() {
        return iconPath;
    }

    public void setIconpath(String iconpath) {
        this.iconPath = iconpath;
    }



    /**
     * 得到自定义类型的条件语句
     *
     * @param sqlFieldName 字段名
     * @param databaseType 采用BaseDao里的数据类型定义
     * @return 为and开头的条件子句
     */
    @Override
    public String getConditionSql(String sqlFieldName, int databaseType) {
//        switch (databaseType) {
//        //TODO ...
//        }
        return "";

    }



}
