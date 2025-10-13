package com.bixuebihui.jdbc.aop;

import com.bixuebihui.jdbc.DbHelper;
import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;

public class DbHelperAroundAdviceTest {


    @Test
    public void invoke() throws Throwable {

        DbHelperAroundAdvice advice = new DbHelperAroundAdvice();
        MethodInvocation methodInvocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(methodInvocation.getMethod()).andReturn(DbHelper.class.getMethod("executeNoQuery", String.class));
        EasyMock.expect(methodInvocation.getArguments()).andReturn(new Object[]{"select 1+2"});
        Object a = new Object();
        EasyMock.expect(methodInvocation.proceed()).andReturn(a);
        EasyMock.replay(methodInvocation);
        Object b = advice.invoke(methodInvocation);
        assertEquals(a, b);
        if(advice.isDebug())
            assertEquals(1, DbHelperAroundAdvice.getSqlStatMap().size());
        else
            assertEquals(0, DbHelperAroundAdvice.getSqlStatMap().size());
    }

    @Test
    public void invokeWithByteArray() throws Throwable {

        DbHelperAroundAdvice advice = new DbHelperAroundAdvice();
        MethodInvocation methodInvocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(methodInvocation.getMethod()).andReturn(DbHelper.class.getMethod("executeNoQuery", String.class,Object[].class, Connection.class));
        Object[] args = new Object[]{"select ?, ?", new Object[]{new byte[]{1, 2, 3, 4, 5, 6, 7, 8}, "test"}};
        EasyMock.expect(methodInvocation.getArguments()).andReturn(args);
        Object a = new Object();
        EasyMock.expect(methodInvocation.proceed()).andReturn(a);
        EasyMock.replay(methodInvocation);

        Object b = advice.invoke(methodInvocation);
        assertEquals(a, b);
        if(advice.isDebug())
            assertEquals(1, DbHelperAroundAdvice.getSqlStatMap().size());
        else
            assertEquals(0, DbHelperAroundAdvice.getSqlStatMap().size());
    }

    @Test
    public void testToString(){
        byte[] x = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        Object[] args = new Object[]{"select ?, ?", new Object[]{x, "test"}};
        System.out.println(DbHelperAroundAdvice.toString(x));
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            System.out.println(DbHelperAroundAdvice.toString(args[i]));
        }
    }


    @Test
    public void testReduceNormalSql() {
        DbHelperAroundAdvice advice = new DbHelperAroundAdvice();
        String originalSql = "SELECT * FROM users WHERE id = ? AND name = ?";
        String resultSql = advice.reduce(originalSql);
        // 正常SQL不应该被修改
        assertEquals(originalSql, resultSql);
    }

    @Test
    public void testReduceLongSqlWithoutManyQuestionMarks() {
        DbHelperAroundAdvice advice = new DbHelperAroundAdvice();
        // 创建一个长SQL但问号数量不超过50个
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM users WHERE ");
        for (int i = 0; i < 40; i++) {
            if (i > 0) sb.append(" AND ");
            sb.append("col").append(i).append(" = ?");
        }
        String originalSql = sb.toString();
        String resultSql = advice.reduce(originalSql);
        // 虽然SQL很长，但问号数量不超过50个，不应该被修改
        assertEquals(originalSql, resultSql);
    }

    @Test
    public void testReduceWithConsecutiveQuestionMarks() {
        DbHelperAroundAdvice advice = new DbHelperAroundAdvice();
        // 创建一个包含连续60个问号的SQL
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO users VALUES(?");
        sb.append(",?".repeat(59));
        sb.append(")");
        String originalSql = sb.toString();
        String resultSql = advice.reduce(originalSql);

        // 验证结果应该包含省略标记
        assertTrue("Result should contain omit marker", resultSql.contains("...omit"));
        // 验证结果应该保留第一个和最后一个问号
        assertTrue("Result should start with INSERT INTO users VALUES(?", resultSql.startsWith("INSERT INTO users VALUES(?)"));
        assertTrue("Result should end with ?)", resultSql.endsWith(",?)") || resultSql.endsWith(")"));
        // 验证省略的数量应该是58个（60-2）
        assertTrue("Result should omit 58 question marks", resultSql.contains("...omit 58"));
    }

    @Test
    public void testReduceWithMultipleConsecutiveBlocks() {
        DbHelperAroundAdvice advice = new DbHelperAroundAdvice();
        // 创建一个包含多个问号块的SQL，其中只有一个块超过50个问号
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM users WHERE (id IN (?");
        sb.append(",?".repeat(54));
        sb.append(") OR name IN (?");
        sb.append(",?".repeat(9));
        sb.append(")");
        String originalSql = sb.toString();
        String resultSql = advice.reduce(originalSql);

        // 验证第一个块被省略，第二个块保持不变
        assertTrue("First block should be omitted", resultSql.contains("...omit 53"));
        assertTrue("Second block should remain unchanged", resultSql.contains("name IN (?,?"));
    }

    private void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private void assertTrue(String message, String content, int index) {
        if (index == -1) {
            throw new AssertionError(message);
        }
        if (!content.contains("?")) {
            throw new AssertionError(message);
        }
    }

    @Test
    public void testReducePerformance() {
        DbHelperAroundAdvice advice = new DbHelperAroundAdvice();
        
        // 测试不同规模的SQL性能
        int[] testSizes = {100, 500, 1000, 2000, 5000, 10000};
        
        for (int size : testSizes) {
            // 创建包含指定数量问号的SQL
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO users VALUES(?");
            sb.append(",?".repeat(size - 1));
            sb.append(")");
            String sql = sb.toString();
            
            // 预热
            for (int i = 0; i < 10; i++) {
                advice.reduce(sql);
            }
            
            // 性能测试
            int iterations = 1000;
            long startTime = System.nanoTime();
            
            for (int i = 0; i < iterations; i++) {
                advice.reduce(sql);
            }
            
            long endTime = System.nanoTime();
            long totalTime = endTime - startTime;
            double avgTimeMs = (totalTime / 1_000_000.0) / iterations;
            
            System.out.printf("SQL with %d question marks: %.3f ms per operation%n", 
                            size, avgTimeMs);
            
            // 验证结果正确性
            String result = advice.reduce(sql);
            if (size > 50) {
                assertTrue("Should contain omit marker for large SQL", result.contains("...omit"));
            }
        }
    }

    @Test
    public void testReducePerformanceWithLongSql() {
        DbHelperAroundAdvice advice = new DbHelperAroundAdvice();
        
        // 创建一个很长的SQL，包含大量问号
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO users (");
        
        // 添加1000个列
        for (int i = 0; i < 1000; i++) {
            if (i > 0) sb.append(",");
            sb.append("col").append(i);
        }
        sb.append(") VALUES(?");
        sb.append(",?".repeat(999));
        sb.append(")");
        
        String longSql = sb.toString();
        System.out.println("Long SQL length: " + longSql.length() + " characters");
        
        // 预热
        for (int i = 0; i < 10; i++) {
            advice.reduce(longSql);
        }
        
        // 性能测试
        int iterations = 100;
        long startTime = System.nanoTime();
        
        for (int i = 0; i < iterations; i++) {
            advice.reduce(longSql);
        }
        
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        double avgTimeMs = (totalTime / 1_000_000.0) / iterations;
        
        System.out.printf("Long SQL performance: %.3f ms per operation%n", avgTimeMs);
        
        // 验证结果
        String result = advice.reduce(longSql);
        assertTrue("Should contain omit marker", result.contains("...omit"));
        
        // 性能断言：每次操作应该小于1毫秒
        assertTrue("Performance should be acceptable", avgTimeMs < 1.0);
    }

}
