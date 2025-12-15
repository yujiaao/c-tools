package com.bixuebihui.codec;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;

public class EncryptDecryptServiceAESImplTest {


    @Test
    public void testEncrypt() {
        EncryptDecryptServiceAESImpl service;

        // 正常情况测试
        List<String> list = Arrays.asList("test1", "test2");

        // 异常情况测试
        // 模拟获取 Cipher 时抛出异常
        String key ="1234567890123456";
        service = new EncryptDecryptServiceAESImpl(key);
        List<String> encrypted = service.encrypt(list);
        Assertions.assertNotNull(encrypted);
        List<String> res = service.decrypt(encrypted);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(list, res);

    }

    @Test
    public void testDecrypt() {
        String cKey = "1234567890123456";
        EncryptDecryptServiceAESImpl tool = new EncryptDecryptServiceAESImpl(cKey);

        // 需要加密的字串
        String cSrc = "so long time no see, 好久没面了";
        String en = tool.encrypt(cSrc);
        String de = tool.decrypt(en);
        System.out.println("加密：" + en);
        System.out.println("解密:" + de);
        Assertions.assertEquals(cSrc, de);

    }
}
