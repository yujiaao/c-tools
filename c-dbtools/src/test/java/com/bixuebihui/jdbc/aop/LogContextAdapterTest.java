package com.bixuebihui.jdbc.aop;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 * LogContextAdapter 单元测试
 */
public class LogContextAdapterTest {

    @Before
    public void setUp() {
        // 清理MDC，避免测试间相互影响
        MDC.clear();
    }

    @Test
    public void testPrepareArgsWithNull() {
        // 测试null参数
        LogContextAdapter.prepareArgs(null);

        // 验证ServerIP被设置
        String serverIp = MDC.get("ServerIP");
        assertNotNull("ServerIP should not be null", serverIp);
        assertFalse("ServerIP should not be empty", serverIp.isEmpty());

        // 验证Arguments被设置为空字符串
        String arguments = MDC.get("Arguments");
        assertEquals("Arguments should be empty string for null input", "", arguments);
    }

    @Test
    public void testPrepareArgsWithEmptyArray() {
        // 测试空数组参数
        LogContextAdapter.prepareArgs(new Object[]{});

        // 验证ServerIP被设置
        String serverIp = MDC.get("ServerIP");
        assertNotNull("ServerIP should not be null", serverIp);

        // 验证Arguments被设置为空字符串
        String arguments = MDC.get("Arguments");
        assertEquals("Arguments should be empty string for empty array", "", arguments);
    }

    @Test
    public void testPrepareArgsWithNormalArguments() {
        // 测试正常参数
        Object[] args = new Object[]{"param1", "param2", 123};
        LogContextAdapter.prepareArgs(args);

        // 验证ServerIP被设置
        String serverIp = MDC.get("ServerIP");
        assertNotNull("ServerIP should not be null", serverIp);

        // 验证Arguments被正确设置
        String arguments = MDC.get("Arguments");
        assertNotNull("Arguments should not be null", arguments);
        assertTrue("Arguments should contain param1", arguments.contains("param1"));
        assertTrue("Arguments should contain param2", arguments.contains("param2"));
        assertTrue("Arguments should contain 123", arguments.contains("123"));
    }

    @Test
    public void testPrepareArgsWithNullElement() {
        // 测试包含null元素的数组
        Object[] args = new Object[]{"valid", null, "another"};
        LogContextAdapter.prepareArgs(args);

        String arguments = MDC.get("Arguments");
        assertNotNull("Arguments should not be null", arguments);
        assertTrue("Arguments should contain valid", arguments.contains("valid"));
        assertTrue("Arguments should contain another", arguments.contains("another"));
    }

    @Test
    public void testPrepareArgsEncodesSpecialCharacters() {
        // 测试特殊字符编码
        String specialValue = "test=value&param=test";
        Object[] args = new Object[]{specialValue};
        LogContextAdapter.prepareArgs(args);

        String arguments = MDC.get("Arguments");
        assertNotNull(arguments);

        // 验证URL编码
        String encoded = URLEncoder.encode(specialValue, StandardCharsets.UTF_8);
        assertTrue("Arguments should be URL encoded", arguments.contains(encoded));
    }

    @Test
    public void testPrepareArgsWithChineseCharacters() {
        // 测试中文字符
        String chineseValue = "测试中文";
        Object[] args = new Object[]{chineseValue};
        LogContextAdapter.prepareArgs(args);

        String arguments = MDC.get("Arguments");
        assertNotNull(arguments);

        // 验证中文字符被URL编码
        String encoded = URLEncoder.encode(chineseValue, StandardCharsets.UTF_8);
        assertTrue("Chinese characters should be URL encoded", arguments.contains(encoded));
    }

    @Test
    public void testPrepareArgsWithMultipleCalls() {
        // 测试多次调用，确保MDC值正确更新
        Object[] args1 = new Object[]{"first"};
        LogContextAdapter.prepareArgs(args1);
        String firstArguments = MDC.get("Arguments");

        Object[] args2 = new Object[]{"second"};
        LogContextAdapter.prepareArgs(args2);
        String secondArguments = MDC.get("Arguments");

        assertNotEquals("Arguments should be updated on each call", firstArguments, secondArguments);
    }

    @Test
    public void testServerIpFormat() {
        // 验证ServerIP格式（应该是有效的IP地址或回退地址）
        LogContextAdapter.prepareArgs(new Object[]{"test"});

        String serverIp = MDC.get("ServerIP");
        assertNotNull(serverIp);

        // IP地址应该是数字和点组成，或者是回退地址127.127.127.127
        boolean isValidIpFormat = serverIp.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")
                || serverIp.equals("127.127.127.127");
        assertTrue("ServerIP should be valid IP format or fallback address", isValidIpFormat);
    }

    @Test
    public void testEscapeWithNull() {
        // 通过反射测试escape方法，或通过prepareArgs间接测试
        Object[] args = new Object[]{null};
        LogContextAdapter.prepareArgs(args);

        String arguments = MDC.get("Arguments");
        // null 元素在原文中为 [arg]:，整段经 URLEncoder 后方括号会被编码
        assertNotNull(arguments);
        String decoded = URLDecoder.decode(arguments, StandardCharsets.UTF_8);
        assertTrue(decoded.contains("[arg]:"));
    }

    @Test
    public void testEscapeWithEmptyString() {
        // 测试空字符串转义
        Object[] args = new Object[]{""};
        LogContextAdapter.prepareArgs(args);

        String arguments = MDC.get("Arguments");
        assertNotNull(arguments);
    }

    @Test
    public void testEscapeWithNewlineCharacters() {
        // 测试换行符转义
        String valueWithNewline = "line1\nline2\rline3";
        Object[] args = new Object[]{valueWithNewline};
        LogContextAdapter.prepareArgs(args);

        String arguments = MDC.get("Arguments");
        assertNotNull(arguments);
        
        // URL编码后换行符应该被编码
        String encoded = URLEncoder.encode(valueWithNewline, StandardCharsets.UTF_8);
        assertTrue(arguments.contains(encoded));
    }

    @Test
    public void testEscapeWithSpecialChars() {
        // 测试各种特殊字符的转义
        String[] specialValues = {
                "test<tag>",
                "test\"quote\"",
                "test'single quote",
                "test&and",
                "test#hash",
                "test%percent"
        };

        for (String value : specialValues) {
            Object[] args = new Object[]{value};
            LogContextAdapter.prepareArgs(args);

            String arguments = MDC.get("Arguments");
            String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
            assertTrue("Should encode: " + value, arguments.contains(encoded));
            
            // 清理为下一个测试做准备
            MDC.clear();
        }
    }

    @Test
    public void testMdcKeysAreCorrect() {
        // 验证MDC使用的key名称正确
        LogContextAdapter.prepareArgs(new Object[]{"test"});

        // 检查固定的key名称
        assertNotNull(MDC.get("ServerIP"));
        
        // ServerIP的key应该是"ServerIP"（注意大小写）
        assertTrue(MDC.getCopyOfContextMap().containsKey("ServerIP"));
        
        // Arguments的key应该是"Arguments"
        assertTrue(MDC.getCopyOfContextMap().containsKey("Arguments"));
    }

    @Test
    public void testConcurrentSafety() throws InterruptedException {
        // 简单的并发安全测试
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                Object[] args = new Object[]{"thread-" + index};
                LogContextAdapter.prepareArgs(args);
                
                // 每个线程应该有自己的MDC值
                String arguments = MDC.get("Arguments");
                assertNotNull(arguments);
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // 主线程验证自己的值不受影响
        Object[] mainArgs = new Object[]{"main"};
        LogContextAdapter.prepareArgs(mainArgs);
        
        String arguments = MDC.get("Arguments");
        assertTrue(arguments.contains("main"));
    }
}
