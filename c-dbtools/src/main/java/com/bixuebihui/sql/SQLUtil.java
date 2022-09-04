

package com.bixuebihui.sql;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * <p>SQLUtil class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class SQLUtil
{

    private SQLUtil() throws IllegalAccessException {
        throw new IllegalAccessException("this is a util class");
    }

    /**
     * <p>escapeString.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String escapeString(String s) {
        return escapeString(s, 0xf423f);
    }

    /**
     * <p>escapeString.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @param i a int.
     * @return a {@link java.lang.String} object.
     */
    public static String escapeString(String s, int i) {
        if(s == null) {
            return EMPTY_STR;
        }
        if(s.length() > i) {
            s = s.substring(0, i);
        }
        StringTokenizer stringtokenizer = new StringTokenizer(s, "'");
        StringBuilder stringbuffer = null;
        for(; stringtokenizer.hasMoreTokens(); stringbuffer.append(stringtokenizer.nextToken())) {
            if(stringbuffer == null) {
                stringbuffer = new StringBuilder(s.length() + 20);
            } else {
                stringbuffer.append("''");
            }
        }

        if(stringbuffer == null) {
            return s;
        } else {
            return stringbuffer.toString();
        }
    }

    /**
     * <p>notNull.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String notNull(String s) {
        if(s == null) {
            return EMPTY_STR;
        } else {
            return s;
        }
    }

    /**
     * <p>formatMysqlDate.</p>
     *
     * @param date a {@link java.util.Date} object.
     * @return a {@link java.lang.String} object.
     */
    public static synchronized String formatMysqlDate(Date date) {
        if(date == null) {
            return "";
        } else {
            SimpleDateFormat mysqlFormat = new SimpleDateFormat("yyyy-MM-dd");
            return mysqlFormat.format(date);
        }
    }

    /**
     * <p>parseMysqlDate.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @return a {@link java.util.Date} object.
     */
    public static synchronized Date parseMysqlDate(String s) {
        if(s == null || "0000-00-00".equals(s)) {
            return null;
        }
        try {
            SimpleDateFormat mysqlFormat = new SimpleDateFormat("yyyy-MM-dd");
            return mysqlFormat.parse(s);
        } catch(Exception exception) {
            return null;
        }
    }

    /**
     * <p>formatHiResMysqlDate.</p>
     *
     * @param date a {@link java.util.Date} object.
     * @return a {@link java.lang.String} object.
     */
    public static synchronized String formatHiResMysqlDate(Date date) {
        if(date == null) {
            return "";
        } else {
            SimpleDateFormat mysqlFormatHiRes = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return mysqlFormatHiRes.format(date);
        }
    }

    /**
     * <p>booleanToString.</p>
     *
     * @param flag a boolean.
     * @return a {@link java.lang.String} object.
     */
    public static String booleanToString(boolean flag) {
        if(flag) {
            return "y";
        } else {
            return "n";
        }
    }

    /**
     * <p>stringToBoolean.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean stringToBoolean(String s) {
        return "y".equals(s);
    }


    private static final String EMPTY_STR = "";

}
