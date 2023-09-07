package com.bixuebihui.jdbc.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * <p>DbHelperAroundAdvice class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class DbHelperAroundAdvice implements MethodInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(DbHelperAroundAdvice.class);
    private static final int MAX_SHOW = 100;
    private static final Map<String, SqlStat> SQL_STAT_MAP = new LRUMap(1000);
    double totalTime = 0;
    private long before;
    private long count;

    /**
     * <p>Getter for the field <code>sqlStatStore</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, SqlStat> getSqlStatMap() {
        return Collections.unmodifiableMap(SQL_STAT_MAP);
    }

    protected static boolean isDebug() {
        return LOG.isDebugEnabled();
    }

    public static String toString(Object o) {
        if (o == null) return "null";

        if (o instanceof byte[]) {
            return toString((byte[]) o);
        }

        return o.toString();
    }

    static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    public static String toString(byte[] byteArray) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (int i = 0; i < byteArray.length && i < MAX_SHOW; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        hexStringBuffer.insert(0, "0x");
        if (byteArray.length > MAX_SHOW) {
            hexStringBuffer.append("... ");
        }
        return hexStringBuffer.toString();
    }

    private void beforeCall(Method method, Object[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("[AOP] call ").append(method.getName()).append("\n");
        for (Object arg : args) {
            if (arg instanceof Object[]) {
                Object[] s = (Object[]) arg;
                for (int j = 0; j < s.length && j < MAX_SHOW; j++) {
                    if (s[j] == null) {
                        sb.append("\t [").append(j).append("] null").append("\n");
                    } else {
                        sb.append("\t  [").append(j).append("]")
                                .append(s[j].getClass()).append(": ")
                                .append(toString(s[j]))
                                .append("\n");
                    }
                }
                if (s.length >= MAX_SHOW) {
                    sb.append("\t  ").append(s.length - MAX_SHOW).append("... omitted ").append("\n");
                }
            } else if (arg instanceof String
                    || arg instanceof Integer
                    || arg instanceof Long
                    || arg instanceof Date
                    || arg instanceof Byte) {
                sb.append("\t ").append(arg).append("\n");
            }
        }
        LOG.debug(sb.toString());
        before = System.currentTimeMillis();
    }

    private void afterReturning(Method method, Object[] args
    ) {

        long after = System.currentTimeMillis();
        double thisTime = ((after - before) / 1000.0);
        if (!"getConnection".equals(method.getName())) {
            count++;
            totalTime += thisTime;
        }
        if (LOG.isDebugEnabled()) {
            if (ArrayUtils.isNotEmpty(args) && args[0] instanceof String) {
                String sql = (String) args[0];
                SqlStat ss;
                synchronized (SQL_STAT_MAP) {
                    if (SQL_STAT_MAP.containsKey(sql)) {
                        ss = SQL_STAT_MAP.get(sql);
                    } else {
                        ss = new SqlStat();
                        SQL_STAT_MAP.put(sql, ss);
                    }
                }
                ss.setCount(ss.getCount() + 1);
                ss.setTotalTime(ss.getTotalTime() + thisTime);
                if (thisTime > SqlStat.VERY_SLOW_QUERY) {
                    LOG.error("[AOP] Query is VERY SLOW: " + thisTime + " sql=" + sql);
                } else if (thisTime > SqlStat.SLOW_QUERY) {
                    LOG.warn("[AOP] Query is SLOW: " + thisTime + " sql=" + sql);
                }
            }

            // time 保留3位小数
            LOG.debug("[AOP] Current call time: " + thisTime + "s, Counts: "
                    + count + " Total sql time: " + totalTime + "s, on average: "
                    + (count > 0 ? Math.round(totalTime / count * 1000) / 1000.0 : 0));
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method m = invocation.getMethod();
        Object[] args = invocation.getArguments();
        beforeCall(m, args);
        Object o = invocation.proceed();
        afterReturning(m, args);
        return o;
    }

}
