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

}
