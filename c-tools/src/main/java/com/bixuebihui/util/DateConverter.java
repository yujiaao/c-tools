package com.bixuebihui.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

/**
 * @author xwx
 */
public class DateConverter {

    public static final String DATE_SEP = "-";


    public static Date converter(String strDate) {
        return getDateStringContent(strDate);
    }


    public static String padYear(String year) {
        if (year.length() == 0 || "0".equals(year) || "00".equals(year)) {
            year = "1900";
        } else if (year.length() == 2 && year.compareTo("50") < 0) {
            year = "20" + year;
        } else if (year.length() == 2 && year.compareTo("50") >= 0) {
            year = "19" + year;
        }
        return year;
    }

    /**
     * 使用时间转换String转换成Date 年月日分隔型;
     */
    private static java.util.Date getDateStringContent(String strDate) {

        String year = "";
        String month = "";
        String day = "";

        if (strDate.contains(" ")) {
            strDate = strDate.replaceAll(" ", DATE_SEP);
        }
        if (strDate.contains(".")) {
            strDate = strDate.replaceAll("\\.", DATE_SEP);
        }
        if (strDate.contains("．")) {
            strDate = strDate.replaceAll("．", DATE_SEP);
        }
        if (strDate.contains("、")) {
            strDate = strDate.replaceAll("、", DATE_SEP);
        }
        if (strDate.contains("--")) {
            strDate = strDate.replaceAll("--", DATE_SEP);
        }

        if (isNumeric(strDate) && strDate.length() == 8) {
            //format yyyyMMdd
            strDate = strDate.substring(0, 4) + DATE_SEP + strDate.substring(4, 6) + DATE_SEP + strDate.substring(6, 8);
        }

        if (strDate.contains("年")) {
            year = strDate.substring(0, strDate.indexOf("年"));
            if (strDate.contains("月")) {
                month = strDate.substring(strDate.indexOf("年") + 1,
                        strDate.indexOf("月"));

                if (strDate.contains("日")) {
                    day = strDate.substring(strDate.indexOf("月") + 1,
                            strDate.indexOf("日"));
                }
            }
        } else if (strDate.contains(DATE_SEP)) {
            String[] str = strDate.split(DATE_SEP);
            if (str.length == 3) {
                year = str[0];
                month = str[1];
                day = str[2];
            }
        } else if (isNumeric(strDate)) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.clear();
            cal.set(1900, 0, 0);
            cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(strDate) - 1);
            return cal.getTime();
        }

        if ("".equals(year)) {
            year = "1900";
        }
        if ("".equals(month)) {
            month = "01";
        }
        if ("".equals(day)) {
            day = "01";
        }


        return makeDate(year, month, day);


    }

    public static java.util.Date makeDate(int year, int month, int day) {
        return makeDate(year + "", month + "", day + "");
    }

    public static java.util.Date makeDate(String year, String month, String day) {
       year = padYear(year);

        if ((month.length() == 2 && month.compareTo("12") > 0) ||
         (month.length() == 0 || "0".equals(month) || "00".equals(month))) {
            month = "01";
        }
        if ((day.length() == 2 && day.compareTo("31") > 0) ||
         (day.length() == 0 || "0".equals(day) || "00".equals(day))) {
            day = "01";
        }
        String postTime = year + DATE_SEP + month + DATE_SEP + day;
        java.sql.Date dt = null;
        try {
            SimpleDateFormat formatter = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd");
            java.util.Date kz_zdValueDate = formatter.parse(postTime);

            dt = new java.sql.Date(kz_zdValueDate.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dt;
    }


    public static boolean isNumeric(String str) {
        return StringUtils.isNumeric(str);
    }

}
