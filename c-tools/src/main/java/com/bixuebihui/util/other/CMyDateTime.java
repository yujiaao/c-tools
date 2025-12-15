package com.bixuebihui.util.other;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * @author xwx
 */
public class CMyDateTime
        implements Cloneable, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -410914354106815003L;

    public CMyDateTime() {
        date = null;
    }

    public CMyDateTime(long date) {
        this.date = new Date(date);
    }

    public static CMyDateTime now() {
        CMyDateTime mydtNow = new CMyDateTime();
        mydtNow.setDateTimeWithCurrentTime();
        return mydtNow;
    }

    public boolean isNull() {
        return date == null;
    }

    public long getTimeInMillis() {
        return date != null ? date.getTime() : 0L;
    }

    public static int getTimeZoneRawOffset() {
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone.getRawOffset();
    }

    public long compareTo(Date date) {
        long lMyTime = this.date != null ? this.date.getTime() : 0L;
        long lAnotherTime = date != null ? date.getTime() : 0L;
        return lMyTime - lAnotherTime;
    }

    public long compareTo(CMyDateTime dateTime) {
        return compareTo(dateTime.getDateTime());
    }

    public static String extractDateTimeFormat(String value) {
        char[] formatChar = {
                'y', 'M', 'd', 'H', 'm', 's'
        };
        return extractFormat(value, formatChar);
    }

    public static String extractDateFormat(String value) {
        char[] formatChar = {
                'y', 'M', 'd'
        };
        return extractFormat(value, formatChar);
    }

    public static String extractTimeFormat(String value) {
        char[] formatChar = {
                'H', 'm', 's'
        };
        return extractFormat(value, formatChar);
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    private static String extractFormat(String value, char[] formatChar) {
        if (value == null) {
            return null;
        }
        char[] buffValue = value.trim().toCharArray();
        if (buffValue.length == 0) {
            return null;
        }
        StringBuilder buffFormat = new StringBuilder(19);
        int nAt = 0;
        int nAtField = 0;
        while (nAt < buffValue.length) {
            char aChar = buffValue[nAt++];
            if (Character.isDigit(aChar)) {
                buffFormat.append(formatChar[nAtField]);
                continue;
            }
            buffFormat.append(aChar);
            if (++nAtField >= formatChar.length) {
                break;
            }
        }
        return buffFormat.toString();
    }

    public static String formatTimeUsed(long iMillis) {
        if (iMillis <= 0L) {
            return "";
        }
        int iSecond = 0;
        int iMinute = 0;
        StringBuffer sb = new StringBuffer(16);
        iSecond = (int) (iMillis / 1000L);
        iMillis %= 1000L;
        if (iSecond > 0) {
            iMinute = iSecond / 60;
            iSecond %= 60;
        }
        if (iMinute > 0) {
            sb.append(iMinute).append('\u5206');
            if (iSecond < 10) {
                sb.append('0');
            }
            sb.append(iSecond);
        } else {
            sb.append(iSecond).append('.');
            if (iMillis < 10L) {
                sb.append('0').append('0');
            } else if (iMillis < 100L) {
                sb.append('0');
            }
            sb.append(iMillis);
        }
        sb.append('\u79D2');
        return sb.toString();
    }

    public int getYear()
            throws CMyException {
        return get(1);
    }

    public int getMonth()
            throws CMyException {
        return get(2);
    }

    public int getDay()
            throws CMyException {
        return get(3);
    }

    public int getHour()
            throws CMyException {
        return get(4);
    }

    public int getMinute()
            throws CMyException {
        return get(5);
    }

    public int getSecond()
            throws CMyException {
        return get(6);
    }

    public int getDayOfWeek()
            throws CMyException {
        return get(12);
    }

    public static String getStr(Object dtTime, String format) {
        if (dtTime instanceof CMyDateTime) {
            return ((CMyDateTime) dtTime).toString(format);
        } else {
            return CMyString.showObjNull(dtTime);
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public static void main(String[] args) {
        CMyDateTime myDateTime = new CMyDateTime();
        try {
            CMyDateTime now = now();
            System.out.println("now:" + now.toString(DEF_DATETIME_FORMAT_PRG));
            CMyDateTime nowClone = (CMyDateTime) now.clone();
            CMyDateTime execStartTime = nowClone.dateAdd(3, -30);
            System.out.println("now-30:" + execStartTime.toString(DEF_DATETIME_FORMAT_PRG));
            System.out.println("now:" + now.toString(DEF_DATETIME_FORMAT_PRG));
            System.out.println("nowClone:" + nowClone.toString(DEF_DATETIME_FORMAT_PRG));
            now = now();
            execStartTime = now.dateAdd(3, -3);
            System.out.println("now-3:" + execStartTime.toString(DEF_DATETIME_FORMAT_PRG));
            now = now();
            execStartTime = now.dateAdd(4, -3);
            System.out.println("now-3:" + execStartTime.toString(DEF_DATETIME_FORMAT_PRG));
            execStartTime.setDateTimeWithString("2002.1.1 00:00:00", "yyyy.MM.dd HH:mm:ss");
            System.out.println("time:" + execStartTime.toString(DEF_DATETIME_FORMAT_PRG));
            execStartTime.setDateTimeWithString(execStartTime.toString(DEF_DATE_FORMAT_PRG) + " 23:00:00", DEF_DATETIME_FORMAT_PRG);
            CMyDateTime execEndTime = now();
            execEndTime.setDateTimeWithString(execEndTime.toString(DEF_DATE_FORMAT_PRG) + " 24:00:00", DEF_DATETIME_FORMAT_PRG);
            System.out.println("now:" + now.toString());
            System.out.println("execStartTime:" + execStartTime.toString());
            System.out.println("now.compareTo(execStartTime):" + now.compareTo(execStartTime));
            System.out.println("TimeZone = " + getTimeZoneRawOffset());
            myDateTime.setDateTimeWithCurrentTime();
            System.out.println("Start:" + myDateTime.getDateTimeAsString("yyyy/MM/dd HH:mm:ss"));
            long nTime = myDateTime.getTimeInMillis() % 0x36ee80L;
            System.out.print("\nTime=" + nTime);
            Time tempTime = new Time(nTime);
            System.out.print("  " + tempTime.toString());
            System.out.print("\n");
            myDateTime.setDateWithString("2001-04-15", 0);
            System.out.println(myDateTime.getDateTimeAsString("yyyy.MM.dd"));
            myDateTime.setDateWithString("000505", 2);
            System.out.println(myDateTime.getDateTimeAsString("yyyy.MM.dd"));
            myDateTime.setTimeWithString("12:01:02", 0);
            System.out.println(myDateTime.getDateTimeAsString(DEF_TIME_FORMAT_PRG));
            myDateTime.setTimeWithString("00:25", 2);
            System.out.println(myDateTime.getDateTimeAsString(DEF_DATETIME_FORMAT_PRG));
            java.sql.Date dDate = java.sql.Date.valueOf("1978-02-04");
            Time tTime = Time.valueOf("12:00:20");
            System.out.println(myDateTime.getDateTimeAsString("yyyy/MM/dd HH:mm:ss"));
            myDateTime.setDate(dDate);
            System.out.println(myDateTime.getDateTimeAsString("yyyy/MM/dd HH:mm:ss"));
            myDateTime.setTime(tTime);
            System.out.println(myDateTime.getDateTimeAsString("yyyy/MM/dd HH:mm:ss"));
            myDateTime.setDateTimeWithCurrentTime();
            System.out.println("End:" + myDateTime.getDateTimeAsString("yyyy/MM/dd HH:mm:ss"));
            CMyDateTime myDateTime2 = new CMyDateTime();
            int[] nFields = {
                    1, 2, 3, 4, 5, 6, 11, 12
            };
            myDateTime2.setDateTimeWithString("2001-02-07 14:34:00", DEF_DATETIME_FORMAT_PRG);
            myDateTime.setDateTimeWithString("2001-03-07 15:35:01", DEF_DATETIME_FORMAT_PRG);
            for (int i = 0; i < 8; i++) {
                long lDateDiff = myDateTime.dateDiff(nFields[i], myDateTime2.getDateTime());
                System.out.println("DateDiff(" + nFields[i] + ")=" + lDateDiff);
            }

            for (int i = 0; i < 6; i++) {
                System.out.println("get(" + nFields[i] + ")=" + myDateTime.get(nFields[i]));
            }

            System.out.println("getWeek=" + myDateTime.get(12));
            System.out.println("Test for dateAdd()");
            System.out.println("oldDateTime = " + myDateTime.toString());
            myDateTime.dateAdd(1, 12);
            System.out.println("dateAdd(YEAR,12) = " + myDateTime.toString());
            myDateTime.dateAdd(1, -12);
            System.out.println("dateAdd(YEAR,-12) = " + myDateTime.toString());
            myDateTime.dateAdd(2, -3);
            System.out.println("dateAdd(MONTH,-3) = " + myDateTime.toString());
            myDateTime.dateAdd(3, 10);
            System.out.println("dateAdd(DAY,10) = " + myDateTime.toString());
            myDateTime.setDateTimeWithCurrentTime();
            int nWeek = myDateTime.getDayOfWeek();
            myDateTime.dateAdd(3, -nWeek);
            System.out.println("Monday of this week is:" + myDateTime.toString(DEF_DATE_FORMAT_PRG));
            for (int j = 1; j < 7; j++) {
                myDateTime.dateAdd(3, 1);
                System.out.println((j + 1) + " of this week is:" + myDateTime.toString(DEF_DATE_FORMAT_PRG));
            }

            System.out.println("\n\n===== test for CMyDateTime.set() ====== ");
            String[] sValues = {
                    "2002.06.13 12:00:12", "1900.2.4 3:4:5", "1901-03-15 23:05:10", "1978-2-4 5:6:7", "2001/12/31 21:08:22", "1988/2/5 9:1:2", "1986.12.24", "0019.2.8", "2002-12-20", "1999-8-1",
                    "2001/12/21", "2000/1/5", "78.02.04", "89.2.6", "99-12-31", "22-3-6", "01/02/04", "02/5/8"
            };
            for (int j = 0; j < sValues.length; j++) {
                myDateTime.setDateTimeWithString(sValues[j]);
                System.out.println("[" + j + "]" + extractDateTimeFormat(sValues[j]) + "  " + myDateTime.toString());
            }

        } catch (CMyException ex) {
            ex.printStackTrace(System.out);
        }
    }

    public Date getDateTime() {
        return date;
    }

    public long dateDiff(int part, CMyDateTime dateTime)
            throws CMyException {
        if (dateTime == null) {
            throw new CMyException(10, "无效的日期时间对象参数(CMyDateTime.dateDiff(CMyDateTime))");
        } else {
            return dateDiff(part, dateTime.getDateTime());
        }
    }

    public long dateDiff(int part, Date another)
            throws CMyException {
        if (another == null) {
            throw new CMyException(10, "无效的日期时间参数（CMyDateTime.dateDiff(int,java.util.Date)）");
        }
        if (isNull()) {
            throw new CMyException(10, "日期时间为空（CMyDateTime.dateDiff(int,java.util.Date)）");
        }
        if (part == 1) {
            return dateDiffYear(another);
        }
        if (part == 2) {
            return dateDiffMonth(another);
        }
        long lMyTime = date != null ? date.getTime() : 0L;
        long lAnotherTime = another.getTime();
        long lDiffTime = (lMyTime - lAnotherTime) / 1000L;
        switch (part) {
            case 3: // '\003'
                return lDiffTime / 0x15180L;

            case 4: // '\004'
                return lDiffTime / 3600L;

            case 5: // '\005'
                return lDiffTime / 60L;

            case 6: // '\006'
                return lDiffTime;

            case 11: // '\013'
                return lDiffTime / 0x15180L / 91L;

            case 12: // '\f'
                return lDiffTime / 0x15180L / 7L;

            case 7: // '\007'
            case 8: // '\b'
            case 9: // '\t'
            case 10: // '\n'
            default:
                throw new CMyException(10, "参数无效(CMyDateTime.dateDiff(int,java.util.Date))");
        }
    }

    private long dateDiffYear(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(this.date);
        int nYear1 = cal.get(Calendar.YEAR);
        int nMonth1 = cal.get(Calendar.MONTH);
        cal.setTime(date);
        int nYear2 = cal.get(Calendar.YEAR);
        int nMonth2 = cal.get(Calendar.MONTH);
        if (nYear1 == nYear2) {
            return 0L;
        }
        if (nYear1 > nYear2) {
            return (nYear1 - nYear2) + (nMonth1 < nMonth2 ? -1 : 0);
        } else {
            return (nYear1 - nYear2) + (nMonth1 <= nMonth2 ? 0 : 1);
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public long dateDiffMonth(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(this.date);
        int nMonths1 = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH);
        int nDay1 = cal.get(Calendar.DATE);
        cal.setTime(date);
        int nMonths2 = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH);
        int nDay2 = cal.get(Calendar.DATE);
        if (nMonths1 == nMonths2) {
            return 0L;
        }
        if (nMonths1 > nMonths2) {
            return (nMonths1 - nMonths2) + (nDay1 >= nDay2 ? 0 : -1);
        } else {
            return (long) ((nMonths1 - nMonths2) + (nDay1 <= nDay2 ? 0 : 1));
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public int get(int field)
            throws CMyException {
        if (date == null) {
            throw new CMyException(20, "日期时间为空（CMyDateTime.get）");
        }
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(date);
        switch (field) {
            case 1: // '\001'
                return cal.get(Calendar.YEAR);

            case 2: // '\002'
                return cal.get(Calendar.MONTH) + 1;

            case 3: // '\003'
                return cal.get(Calendar.DATE);

            case 4: // '\004'
                return cal.get(Calendar.HOUR_OF_DAY);

            case 5: // '\005'
                return cal.get(Calendar.MINUTE);

            case 6: // '\006'
                return cal.get(Calendar.SECOND);

            case 12: // '\f'
                return cal.get(Calendar.DAY_OF_WEEK);

            case 13: // '\r'
                return cal.getActualMaximum(5);

            case 14: // '\016'
                return getWeekCountsOfMonth(true);

            case 15: // '\017'
                return cal.getActualMaximum(Calendar.DAY_OF_YEAR);

            case 16: // '\020'
                return cal.getActualMaximum(Calendar.WEEK_OF_YEAR);

            case 7: // '\007'
            case 8: // '\b'
            case 9: // '\t'
            case 10: // '\n'
            case 11: // '\013'
            default:
                throw new CMyException(10, "无效的日期时间域参数（CMyDateTime.get）");
        }
    }

    public void setDateTime(Date date) {
        this.date = date;
    }

    public boolean setDateTimeWithString(String value, String format)
            throws CMyException {
        try {
            SimpleDateFormat dtFormat = new SimpleDateFormat(format);
            date = dtFormat.parse(value);
            return true;
        } catch (Exception ex) {
            throw new CMyException(10, "日期时间字符串值和格式无效（CMyDateTime.setDateTimeWithString）", ex);
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public CMyDateTime dateAdd(int field, int and)
            throws CMyException {
        if (date == null) {
            throw new CMyException(20, "日期时间为空（CMyDateTime.dateAdd）");
        }
        int nCalField = 0;
        switch (field) {
            case 1:
                // '\001'
                nCalField = 1;
                break;

            case 2:
                // '\002'
                nCalField = 2;
                break;

            case 12:
                // '\f'
                nCalField = 5;
                and *= 7;
                break;

            case 3:
                // '\003'
                nCalField = 5;
                break;

            case 4:
                // '\004'
                nCalField = 10;
                break;

            case 5:
                // '\005'
                nCalField = 12;
                break;

            case 6:
                // '\006'
                nCalField = 13;
                break;

            case 7:
                // '\007'
            case 8:
                // '\b'
            case 9:
                // '\t'
            case 10:
                // '\n'
            case 11:
                // '\013'
            default:
                throw new CMyException(10, "无效的日期时间域参数（CMyDateTime.dateAdd");
        }
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(date);
        cal.set(nCalField, cal.get(nCalField) + and);
        date = cal.getTime();
        return this;
    }

    @Override
    public synchronized Object clone() {
        CMyDateTime newMyDateTime = new CMyDateTime();
        newMyDateTime.date = date != null ? (Date) date.clone() : null;
        return newMyDateTime;
    }

    public void setDateTimeWithRs(ResultSet resultSet, int fieldIndex)
            throws CMyException {
        try {
            Timestamp tsDateTime = resultSet.getTimestamp(fieldIndex);
            setDateTimeWithTimestamp(tsDateTime);
        } catch (SQLException ex) {
            throw new CMyException(40, "从记录集中读取时间字段时出错：CMyDateTime.setDateTimeWithRs()", ex);
        }
    }

    public void setDateTimeWithRs(ResultSet data, String fieldName)
            throws CMyException {
        try {
            Timestamp tsDateTime = data.getTimestamp(fieldName);
            setDateTimeWithTimestamp(tsDateTime);
        } catch (SQLException ex) {
            throw new CMyException(40, "从记录集中读取时间字段时出错：CMyDateTime.setDateTimeWithRs()", ex);
        }
    }

    @Override
    public String toString() {
        return toString(DEF_DATETIME_FORMAT_PRG);
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public String toString(String format) {
        if (date == null) {
            return null;
        }
        try {
            return getDateTimeAsString(format);
        } catch (CMyException ex) {
            return null;
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public java.sql.Date toDate() {
        if (date == null) {
            return null;
        } else {
            return new java.sql.Date(date.getTime());
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public Time toTime() {
        if (date == null) {
            return null;
        } else {
            return new Time(date.getTime());
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public Timestamp toTimestamp() {
        if (date == null) {
            return null;
        } else {
            return new Timestamp(date.getTime());
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public void setDateTimeWithCurrentTime() {
        if (date == null) {
            date = new Date(System.currentTimeMillis());
        } else {
            date.setTime(System.currentTimeMillis());
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public void setDateTimeWithTimestamp(Timestamp timestamp)
            throws CMyException {
        try {
            if (timestamp == null) {
                date = null;
            } else {
                if (date == null) {
                    date = new Date();
                }
                date.setTime(timestamp.getTime());
            }
        } catch (Exception ex) {
            throw new CMyException(0, "\u4F7F\u7528Timestamp\u5BF9\u8C61\u8BBE\u7F6E\u65E5\u671F\u548C\u65F6\u95F4\u51FA\u9519\uFF1ACMyDateTime.setDateTimeWithTimestamp()", ex);
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public boolean setDate(java.sql.Date date)
            throws CMyException {
        if (date == null) {
            return false;
        } else {
            return setDateWithString(date.toString(), 0);
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public boolean setTime(Time time)
            throws CMyException {
        if (time == null) {
            return false;
        } else {
            return setTimeWithString(time.toString(), 0);
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public boolean setDateWithString(String dateValue, int formatType)
            throws CMyException {
        boolean blHasSepChar;
        int nLen = dateValue.length();
        if (nLen < 6) {
            throw new CMyException(10, "\u65E5\u671F\u5B57\u7B26\u4E32\u65E0\u6548\uFF08CMyDateTime.setDateWithString\uFF09");
        }
        try {
            String sDateValue;
            switch (formatType) {
                case 1:
                    // '\001'
                    blHasSepChar = nLen >= 10;
                    sDateValue = dateValue.substring(0, 4) + "-" + dateValue.substring(blHasSepChar ? 5 : 4, blHasSepChar ? 7 : 6) + "-" + dateValue.substring(blHasSepChar ? 8 : 6, blHasSepChar ? 10 : 8);
                    break;

                case 2:
                    // '\002'
                    sDateValue = dateValue.charAt(0) >= '5' ? "19" : "20";
                    blHasSepChar = nLen >= 8;
                    sDateValue = sDateValue + dateValue.substring(0, 2) + "-" + dateValue.substring(blHasSepChar ? 3 : 2, blHasSepChar ? 5 : 4) + "-" + dateValue.substring(blHasSepChar ? 6 : 4, blHasSepChar ? 8 : 6);
                    break;

                default:
                    sDateValue = dateValue;
                    break;
            }
            if (date == null) {
                return setDateTimeWithString(sDateValue, DEF_DATE_FORMAT_PRG);
            } else {
                String sTimeValue = getDateTimeAsString(DEF_TIME_FORMAT_PRG);
                return setDateTimeWithString(sDateValue + " " + sTimeValue, DEF_DATETIME_FORMAT_PRG);
            }
        } catch (Exception ex) {
            throw new CMyException(10, "\u65E0\u6548\u7684\u65E5\u671F\u5B57\u7B26\u4E32\uFF08CMyException.setDateWithString\uFF09", ex);
        }
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public boolean setTimeWithString(String timeValue, int formatType)
            throws CMyException {
        boolean blHasSepChar;
        int nLen = timeValue.length();
        if (nLen < 4) {
            throw new CMyException(10, "时间字符串格式无效（）");
        }
        try {
            String sTimeValue;
            switch (formatType) {
                case 1:
                    // '\001'
                    blHasSepChar = nLen >= 8;
                    sTimeValue = timeValue.substring(0, 2) + ":" + timeValue.substring(blHasSepChar ? 3 : 2, blHasSepChar ? 5 : 4) + ":" + timeValue.substring(blHasSepChar ? 6 : 4, blHasSepChar ? 8 : 6);
                    break;

                case 2:
                    // '\002'
                    blHasSepChar = nLen >= 5;
                    sTimeValue = timeValue.substring(0, 2) + ":" + timeValue.substring(blHasSepChar ? 3 : 2, blHasSepChar ? 5 : 4) + ":00";
                    break;

                default:
                    sTimeValue = timeValue;
                    break;
            }
            if (date == null) {
                return setDateTimeWithString(sTimeValue, DEF_TIME_FORMAT_PRG);
            } else {
                String sDateValue = getDateTimeAsString(DEF_DATE_FORMAT_PRG);
                return setDateTimeWithString(sDateValue + " " + sTimeValue, DEF_DATETIME_FORMAT_PRG);
            }
        } catch (Exception ex) {
            throw new CMyException(10, "无效的时间字符串（CMyException.setTimeWithString）", ex);
        }
    }

    public String getDateTimeAsString(String format)
            throws CMyException {
        if (date == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).format(date);
        } catch (Exception ex) {
            throw new CMyException(10, "指定的日期时间格式有错（CMyDateTime.getDateTimeAsString）", ex);
        }
    }

    public String getDateTimeAsString()
            throws CMyException {
        if (date == null ) {
            return null;
        }
        try {
            return new SimpleDateFormat(DEF_DATETIME_FORMAT_PRG).format(date);
        } catch (Exception ex) {
            throw new CMyException(0, "格式化日期时间字符串出错（CMyDateTime.getDateTimeAsString()）", ex);
        }
    }

    public boolean setDateTimeWithString(String value)
            throws CMyException {
        String sFormat = extractDateTimeFormat(value);
        if (value == null) {
            return false;
        } else {
            return setDateTimeWithString(value, sFormat);
        }
    }

    public boolean isLeapYear()
            throws CMyException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.isLeapYear(getYear());
    }

    public boolean isToday() {
        CMyDateTime today = now();
        return toString(DEF_DATE_FORMAT_PRG).equals(today.toString(DEF_DATE_FORMAT_PRG));
    }

    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public int getWeekCountsOfMonth(boolean sundayStart)
            throws CMyException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int nWeekCounts = calendar.getActualMaximum(4);
        if (sundayStart) {
            return nWeekCounts;
        }
        CMyDateTime firstDay = new CMyDateTime();
        firstDay.setDateTime(date);
        firstDay.setDateTimeWithString(firstDay.getYear() + "-" + firstDay.getMonth() + "-1");
        if (firstDay.getDayOfWeek() == 6) {
            nWeekCounts++;
        }
        return nWeekCounts;
    }

    @Override
    public boolean equals(Object another) {
        return another != null && (another instanceof CMyDateTime) && ((CMyDateTime) another).getTimeInMillis() == getTimeInMillis();
    }

    @Override
    public int hashCode() {
        return (int) this.getTimeInMillis();
    }

    private Date date;
    public static final int FORMAT_DEFAULT = 0;
    public static final int FORMAT_LONG = 1;
    public static final int FORMAT_SHORT = 2;
    public static final String DEF_DATE_FORMAT_PRG = "yyyy-MM-dd";
    public static final String DEF_TIME_FORMAT_PRG = "HH:mm:ss";
    public static final String DEF_DATETIME_FORMAT_PRG = "yyyy-MM-dd HH:mm:ss";
    public static final String DEF_DATETIME_FORMAT_DB = "YYYY-MM-DD HH24:MI:SS";
    public static final int YEAR = 1;
    public static final int MONTH = 2;
    public static final int DAY = 3;
    public static final int HOUR = 4;
    public static final int MINUTE = 5;
    public static final int SECOND = 6;
    public static final int QUATER = 11;
    public static final int WEEK = 12;
    public static final int DAY_OF_MONTH = 13;
    public static final int WEEK_OF_MONTH = 14;
    public static final int DAY_OF_YEAR = 15;
    public static final int WEEK_OF_YEAR = 16;
    public static final long ADAY_MILLIS = 0x5265c00L;
}
