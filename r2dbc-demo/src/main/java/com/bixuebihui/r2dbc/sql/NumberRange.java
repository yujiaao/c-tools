package com.bixuebihui.r2dbc.sql;

import java.math.BigDecimal;
import java.text.ParseException;

/**
 * @author xwx
 */
public class NumberRange implements ISqlConditionType {
    public static final String SEPARATOR = "~";
    public static final String PERIOD = ".";
    BigDecimal begin;
    BigDecimal end;

    public NumberRange(BigDecimal begin, BigDecimal end) {
        this.begin = begin;
        this.end = end;
    }

    public static boolean isNumberRange(String tildeSeparatedNumbers){
        String regex = "([+-]?([0-9]*[.])?[0-9]+)?~([+-]?([0-9]*[.])?[0-9]+)?";
        return tildeSeparatedNumbers != null && tildeSeparatedNumbers.length()>1 &&
                tildeSeparatedNumbers.matches(regex);
    }

    /**
     * @param tildeSeparatedBeginInclusiveEndExclusive yyyy-MM-dd~yyyy-MM-dd
     * @return time span
     * @throws ParseException if any.
     */
    public static NumberRange build(String tildeSeparatedBeginInclusiveEndExclusive) throws ParseException {
        NumberRange range = new NumberRange(null, null);
        if (!isNumberRange(tildeSeparatedBeginInclusiveEndExclusive)) {
            return range;
        }
        if(!tildeSeparatedBeginInclusiveEndExclusive.contains(SEPARATOR)){
            return range;
        }

        String[] data = tildeSeparatedBeginInclusiveEndExclusive.split(SEPARATOR);
        range.begin = data[0].length()==0? null: parseNumber(data[0]);
        if(data.length>1) {
            range.end = data[1].length() == 0 ? null : parseNumber(data[1]);
        }

        return range;
    }

    public static NumberRange build(String beginNumber, String endNumber){
        NumberRange range = new NumberRange(null, null);

        range.begin = beginNumber.length()== 0 ? null: parseNumber(beginNumber);
        if(endNumber!=null) {
            range.end = endNumber.length() == 0 ? null : parseNumber(endNumber);
        }

        return range;
    }

    private static BigDecimal parseNumber(String src){
       if(src.contains(PERIOD)){
           return BigDecimal.valueOf(Double.parseDouble(src));
       }
        return BigDecimal.valueOf(Long.parseLong(src));
    }

    public BigDecimal getBegin() {
        return begin;
    }

    public BigDecimal getEnd() {
        return end;
    }

    @Override
    public String getConditionSql(String sqlFieldName, int databaseType) {
        String a;
        if (noBoth()) {
            a = "";
        } else if (this.noBegin()) {
            a = " and " + sqlFieldName + "<" + end;
        } else if (this.noEnd()) {
            a = " and " + sqlFieldName + ">= " + begin;
        } else {
            a = " and " + sqlFieldName + ">= " + begin + " and "
                    + sqlFieldName + "<" + end;
        }
        return a;
    }

    private boolean noEnd() {
        return end == null;
    }

    private boolean noBegin() {
        return begin == null;
    }

    private boolean noBoth() {
        return noBegin() && noEnd();
    }
}
