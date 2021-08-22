package com.bixuebihui.jsp;

import com.bixuebihui.jdbc.ISqlConditionType;
import com.bixuebihui.util.ParameterUtils;
import com.bixuebihui.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.bixuebihui.db.Dialect.*;

/**
 * 年月日时间段的HTML控件，前后端及数据库的支持
 *
 * @author xingwx @ TODO 对时间选择对话框的集成，可以选择今天，上个月，上一周，去年等
 */
public class TimeSpan implements ISqlConditionType {

    public final static int MIN_BEGIN_YEAR = 1900;

    public final static int MAX_END_YEAR = 2100;
    private static final String BEGIN_YEAR = "byear";
    private static final String BMONTH = "bmonth";
    private static final String BDAY = "bday";
    private static final String END_YEAR = "eyear";
    private static final String EMONTH = "emonth";
    private static final String EDAY = "eday";
    private static final String BDATE = "bdate";
    private static final String EDATE = "edate";
    private java.util.Date beginDate = null;
    private java.util.Date endDate = null;
    private String prefix = "";
    private int beginYear;

    private int beginMonth;

    private int beginDay;

    private int endYear;

    private int endMonth;

    private int endDay;

    private boolean showChoiceDateDialog = true;
    private boolean useOneDateCell = true;
    /**
     * 时间的提示文字 tooltips
     */
    private String title;
    private String iconPath = "images/calendar.gif";
    private String baseUrl = "../";

    public TimeSpan() {
        beginYear = MIN_BEGIN_YEAR;
        beginMonth = 1;
        beginDay = 1;
        endYear = MAX_END_YEAR;
        endMonth = 1;
        endDay = 1;

    }

    /**
     * @param tildeSeparatedBeginInclusiveEndExclusive yyyy-MM-dd~yyyy-MM-dd
     * @return time span
     */
    public static TimeSpan build(String tildeSeparatedBeginInclusiveEndExclusive) throws ParseException {
        if (!isTimeSpan(tildeSeparatedBeginInclusiveEndExclusive)) {
            return new TimeSpan();
        }
        String[] dates = tildeSeparatedBeginInclusiveEndExclusive.split("~");
        return build(dates[0], dates.length<2? MAX_END_YEAR+"-12-31": dates[1]);
    }

    public static TimeSpan build(String beginDate, String endDate) throws ParseException {
        TimeSpan ts = new TimeSpan();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date begin = sf.parse(StringUtils.trimToNull(beginDate)==null? MIN_BEGIN_YEAR+"-01-01": beginDate);
        Calendar cb = Calendar.getInstance();
        cb.setTime(begin);

        Date end = sf.parse(StringUtils.trimToNull(endDate)==null? MAX_END_YEAR+"-12-31": endDate);
        Calendar ce = Calendar.getInstance();
        ce.setTime(end);
        ts.init(ce, cb);
        return ts;
    }

    public static boolean isTimeSpan(String tildeSeparatedDates) {
        String regex = "(\\d{4}-\\d{1,2}-\\d{1,2})?~(\\d{4}-\\d{1,2}-\\d{1,2})?";
        return tildeSeparatedDates != null &&
                tildeSeparatedDates.matches(regex);
    }

    public boolean isUseOneDateCell() {
        return useOneDateCell;
    }

    public void setUseOneDateCell(boolean useOneDateCell) {
        this.useOneDateCell = useOneDateCell;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 形成URL参数,for GET method
     *
     * @return url参数字符串
     */
    public String getUrlParamString() {
        return "&" + getBYearName() + "=" + beginYear + "&" + getBMonthName() + "="
                + beginMonth + "&" + getBDayName() + "=" + beginDay + "&" + getEYearName()
                + "=" + endYear + "&" + getEMonthName() + "=" + endMonth + "&"
                + getEDayName() + "=" + endDay;

    }

    public String addOrReplaceParam(String url) {
        url = UrlUtil.addOrReplaceParam(url, getBYearName(), beginYear + "");
        url = UrlUtil.addOrReplaceParam(url, getBMonthName(), beginMonth + "");
        url = UrlUtil.addOrReplaceParam(url, getBDayName(), beginDay + "");
        url = UrlUtil.addOrReplaceParam(url, getEYearName(), endYear + "");
        url = UrlUtil.addOrReplaceParam(url, getEMonthName(), endMonth + "");
        url = UrlUtil.addOrReplaceParam(url, getEDayName(), endDay + "");

        return url;
    }

    /**
     * 初始化日期，昨天和三个月前
     */
    public void init(Calendar curDate, Calendar agoDate) {

        beginYear = agoDate.get(GregorianCalendar.YEAR);
        beginMonth = agoDate.get(GregorianCalendar.MONTH) + 1;
        beginDay = agoDate.get(GregorianCalendar.DAY_OF_MONTH);

        endYear = curDate.get(GregorianCalendar.YEAR);
        endMonth = curDate.get(GregorianCalendar.MONTH) + 1;
        endDay = curDate.get(GregorianCalendar.DAY_OF_MONTH);

        beginDate = agoDate.getTime();
        endDate = curDate.getTime();

    }

    /**
     * 形成保存cookie的JavaScript脚本
     *
     * @param savefun 形成保存cookie的JavaScript函数名
     * @return JavaScript脚本
     */
    public String saveCookie(String savefun) {
        String byId = "', document.getElementById('";
        String vl = "').value, document.location);\n";
        return savefun + "('" + getBYearName() + byId
                + getBYearName() + vl + savefun
                + "('" + getBMonthName() + byId
                + getBMonthName() + vl + savefun
                + "('" + getBDayName() + byId + getBDayName()
                + vl + savefun + "('"
                + getEYearName() + byId + getEYearName()
                + vl + savefun + "('"
                + getEMonthName() + byId + getEMonthName()
                + vl + savefun + "('"
                + getEDayName() + byId + getEDayName()
                + vl;
    }

    /**
     * 从request中获得参数
     *
     * @param request
     */
    public void setDates(HttpServletRequest request) {
        if (this.useOneDateCell) {
            getSeparatedParams(request);
        } else {
            getParams(request);
        }

    }

    private void getSeparatedParams(HttpServletRequest request) {
        beginDate = ParameterUtils.getCookieAndRequestDate(request,
                getBDate(), beginDate);
        if (beginDate != null && beginDate.getTime() != 0) {
            Calendar begin_date = Calendar.getInstance();
            begin_date.setTime(beginDate);
            beginYear = begin_date.get(Calendar.YEAR);
            beginMonth = begin_date.get(Calendar.MONTH) + 1;
            beginDay = begin_date.get(Calendar.DAY_OF_MONTH);
        }

        endDate = ParameterUtils.getCookieAndRequestDate(request,
                getEDate(), endDate);
        if (endDate != null && endDate.getTime() != 0) {
            Calendar end_date = Calendar.getInstance();
            end_date.setTime(endDate);
            endYear = end_date.get(Calendar.YEAR);
            endMonth = end_date.get(Calendar.MONTH) + 1;
            endDay = end_date.get(Calendar.DAY_OF_MONTH);
        }
    }

    /**
     * 形成SQL条件语句, oracle语法 从零点到零点的闭区间
     *
     * @param sqlFieldName
     * @return sql SQL语句WHERE子句,类 and ...
     */
    public String getOracleSqlCondition(String sqlFieldName) {
        String a = "";
        if (noBoth()) {
            a = "";
        } else if (noBegin()) {
            a = " and " + sqlFieldName + "<= to_date('" + endYear + "-" + endMonth
                    + "-" + endDay + "','yyyy-mm-dd')";
        } else if (noEnd()) {
            a = " and " + sqlFieldName + ">= to_date('" + beginYear + "-" + beginMonth
                    + "-" + beginDay + "','yyyy-mm-dd')";
        } else {
            a = " and " + sqlFieldName + ">= to_date('" + beginYear + "-" + beginMonth
                    + "-" + beginDay + "','yyyy-mm-dd')" + " and " + sqlFieldName
                    + "<= to_date('" + endYear + "-" + endMonth + "-" + endDay
                    + "','yyyy-mm-dd')";
        }
        return a;
    }

    private boolean noEnd() {
        return endDate == null || endYear == MAX_END_YEAR;
    }

    private boolean noBegin() {
        return beginDate == null || beginYear == MIN_BEGIN_YEAR;
    }

    private boolean noBoth() {
        return noBegin() && noEnd();
    }

    /**
     * 形成SQL条件语句, Derby语法
     *
     * @param sqlFieldName
     * @return sql SQL语句WHERE子句,类 and ...
     */
    public String getDerbySqlCondition(String sqlFieldName) {
        String a = "";
        if (noBoth()) {
            a = "";
        } else if (this.noBegin()) {
            a = " and " + sqlFieldName + "<= date('" + endYear + "-" + endMonth
                    + "-" + endDay + "')";
        } else if (this.noEnd()) {
            a = " and " + sqlFieldName + ">= date('" + beginYear + "-" + beginMonth
                    + "-" + beginDay + "')";
        } else {
            a = " and " + sqlFieldName + ">= date('" + beginYear + "-" + beginMonth
                    + "-" + beginDay + "')" + " and " + sqlFieldName + "<= date('"
                    + endYear + "-" + endMonth + "-" + endDay + "')";
        }
        return a;
    }

    /**
     * 形成SQL条件语句, MySql语法
     *
     * @param sqlFieldName 字段名
     * @return sql SQL语句WHERE子句,类 and ...
     */
    public String getMysqlSqlCondition(String sqlFieldName) {
        String a;
        if (noBoth()) {
            a = "";
        } else if (this.noBegin()) {
            a = " and " + sqlFieldName + "<= str_to_date('" + endYear + "-"
                    + endMonth + "-" + endDay + "','%Y-%m-%d')";
        } else if (this.noEnd()) {
            a = " and " + sqlFieldName + ">= str_to_date('" + beginYear + "-"
                    + beginMonth + "-" + beginDay + "','%Y-%m-%d')";
        } else {
            a = " and " + sqlFieldName + ">= str_to_date('" + beginYear + "-"
                    + beginMonth + "-" + beginDay + "','%Y-%m-%d')" + " and "
                    + sqlFieldName + "<= str_to_date('" + endYear + "-" + endMonth
                    + "-" + endDay + "','%Y-%m-%d')";
        }
        return a;
    }

    public String getHTML() {
        return getCellHtml(this.useOneDateCell);
    }

    public String getBDate() {
        return prefix + BDATE;
    }

    public String getEDate() {
        return prefix + EDATE;
    }

    protected String getCellHtml(boolean useOneCell) {
        StringBuilder sb = new StringBuilder();

        String color1 = " class=\"modified span1\" ";
        String color2 = " class=\"modified span1\" ";

        if (this.beginDate == null) {
            color1 = "class=\"span1\"";
        }
        if (this.endDate == null) {
            color2 = "class=\"span1\"";
        }

        if (title != null) {
            sb.append("<span title=").append(Util.makeQuotedStr(title)).append(">");
        }

        fillCells(sb, color1, color2, useOneCell);

        if (isShowChoiceDateDialog()) {
            sb.append("       <img id=\"").append(prefix).append("imgDate1\" onmouseover=\"RaiseButton(this)\" style=\"cursor: pointer\" "
            ).append(" onmouseout=\"HideButton(this)\" height=\"20\" src=\"").append(baseUrl).append("/").append(iconPath).append("\" width=\"16\"\r\n").append(" border=\"0\" >\r\n");
        }

        if (title != null) {
            sb.append("</span>");
        }

        return sb.toString();
    }

    private void fillCells(StringBuilder sb, String color1, String color2,
                           boolean useOneDateCell) {
        if (useOneDateCell) {
            sb.append("从<input type=\"text\" name=\"").append(getBDate()).append("\" id=\"").append(getBDate()).append("\" value=\"");
            sb.append(beginYear).append("-").append(beginMonth).append("-")
                    .append(beginDay);
            sb.append("\" maxlength=\"12\" size=\"12\" ").append(color1).append(">\r\n");

            sb.append("\t\t\t\t\t 到 <input type=\"text\" name=\"").append(getEDate()).append("\" id=\"").append(getEDate()).append("\" value=\"");
            sb.append(endYear).append("-").append(endMonth).append("-")
                    .append(endDay);
            sb.append("\" maxlength=\"12\" size=\"12\" " + color2 + ">\r\n");
        } else {
            sb.append("从<input type=text name=" + getBYearName() + " id="
                    + getBYearName() + " value=\"");
            sb.append(beginYear);
            sb.append("\" maxlength=4 size=4 " + color1
                    + ">年<input type=text name=" + getBMonthName() + " id="
                    + getBMonthName() + " value=\"");
            sb.append(beginMonth);
            sb.append("\" maxlength=2 size=2 " + color1
                    + ">月<input type=text name=" + getBDayName() + " id="
                    + getBDayName() + " value=\"");
            sb.append(beginDay);
            sb.append("\" maxlength=2 size=2 " + color1 + ">日\r\n");

            sb.append("\t\t\t\t\t 到 <input type=text name=" + getEYearName()
                    + " id=" + getEYearName() + " value=\"");
            sb.append(endYear);
            sb.append("\" maxlength=4 size=4 " + color2
                    + ">年<input type=text name=" + getEMonthName() + " id="
                    + getEMonthName() + " value=\"");
            sb.append(endMonth);
            sb.append("\" maxlength=2 size=2 " + color2
                    + ">月<input type=text name=" + getEDayName() + " id="
                    + getEDayName() + " value=\"");
            sb.append(endDay);
            sb.append("\" maxlength=2 size=2 " + color2 + ">日\r\n");
        }
    }

    public java.util.Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(java.util.Date beginDate) {
        this.beginDate = beginDate;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    private String getBYearName() {
        return prefix + BEGIN_YEAR;
    }

    private String getBMonthName() {
        return prefix + BMONTH;
    }

    private String getBDayName() {
        return prefix + BDAY;
    }

    private String getEYearName() {
        return prefix + END_YEAR;
    }

    private String getEMonthName() {
        return prefix + EMONTH;
    }

    private String getEDayName() {
        return prefix + EDAY;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isShowChoiceDateDialog() {
        return showChoiceDateDialog;
    }

    public void setShowChoiceDateDialog(boolean showChoiceDateDialog) {
        this.showChoiceDateDialog = showChoiceDateDialog;
    }

    public int getBYear() {
        return beginYear;
    }

    public int getBMonth() {
        return beginMonth;
    }

    public int getBDay() {
        return beginDay;
    }

    public int getEYear() {
        return endYear;
    }

    public int getEMonth() {
        return endMonth;
    }

    public int getEDay() {
        return endDay;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconpath(String iconpath) {
        this.iconPath = iconpath;
    }

    private void getParams(HttpServletRequest request) {
        if (ParameterUtils.getCookieAndRequestInt(request, getBYearName(), 0) > 0) {

            beginYear = ParameterUtils.getCookieAndRequestInt(request,
                    getBYearName(), beginYear);
            beginMonth = ParameterUtils.getCookieAndRequestInt(request,
                    getBMonthName(), beginMonth);
            beginDay = ParameterUtils.getCookieAndRequestInt(request,
                    getBDayName(), beginDay);

            Calendar begin_date = new GregorianCalendar(beginYear,
                    beginMonth - 1, beginDay, 0, 0, 0);
            if (beginYear > MIN_BEGIN_YEAR) {
                beginDate = begin_date.getTime();
            }
        }

        if (ParameterUtils.getCookieAndRequestInt(request, getEYearName(), 0) > 0) {

            endYear = ParameterUtils.getCookieAndRequestInt(request,
                    getEYearName(), endYear);
            endMonth = ParameterUtils.getCookieAndRequestInt(request,
                    getEMonthName(), endMonth);
            endDay = ParameterUtils.getCookieAndRequestInt(request,
                    getEDayName(), endDay);

            Calendar end_date = new GregorianCalendar(endYear,
                    endMonth - 1, endDay, 0, 0, 0);

            if (endYear < MAX_END_YEAR) {
                endDate = end_date.getTime();
            }

        }
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
        switch (databaseType) {
            case DERBY:
                return this.getDerbySqlCondition(sqlFieldName);
            case MYSQL:
                return this.getMysqlSqlCondition(sqlFieldName);
            case ORACLE:
                return this.getOracleSqlCondition(sqlFieldName);
            default: {
                Logger LOG = LoggerFactory.getLogger(TimeSpan.class);
                LOG.warn("databaseType=" + databaseType + " not implement in  TimeSpan");
                return this.getMysqlSqlCondition(sqlFieldName);
            }
        }

    }

    @Override
    public String toString() {
        return "{" + beginYear + "-" + beginMonth + "-" + beginDay + "," + endYear + "-" + endMonth + "-" + endDay + "}";
    }


}
