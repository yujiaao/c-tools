package com.bixuebihui.util.db;

/**
 * @author xwx
 * Date 2011-1-1
 */
public class OracleDbUtil {

    /**
     * 获得一个月份记录的条件
     *
     * @param field 筛选字段名
     * @param year  年份
     * @param month 月份
     * @return SQL字符串
     */
    public static String sqlFilterOneMonth(String field, int year, int month) {

        return sqlFilterMonth(field, year, month, 1);
    }

    // 当monthCount==1时，与上面的函数相同
    public static String sqlFilterMonth(String field, int year, int month,
                                        int monthCount) {
        int yearEnd = year + monthCount / 12;
        int monthEnd = month + monthCount % 12;
        if (month + monthCount % 12 > 12) {
            monthEnd = month + monthCount % 12 - 12;
            yearEnd = year + 1;
        }
        if (monthCount > 0) {
            return field + ">=to_date(\'" + year + "-" + month
                    + "\',\'YYYY-MM\') and " + field + "< to_date(\'" + yearEnd
                    + "-" + monthEnd + "\',\'YYYY-MM\') ";
        } else {
            return field + ">=to_date(\'" + yearEnd + "-" + monthEnd
                    + "\',\'YYYY-MM\') and " + field + "< to_date(\'" + year
                    + "-" + month + "\',\'YYYY-MM\') ";
        }
    }

    public static String sqlFilterYear(String field, int year, int countYear
    ) {
        int yearEnd = year + countYear;

        if (countYear > 0) {
            return field + ">=to_date(" + year
                    + ",\'YYYY\') and " + field + "< to_date(" + yearEnd
                    + ",\'YYYY\') ";
        } else {
            return field + ">=to_date(" + yearEnd
                    + ",\'YYYY\') and " + field + "< to_date(" + year
                    + ",\'YYYY\') ";
        }
    }
}
