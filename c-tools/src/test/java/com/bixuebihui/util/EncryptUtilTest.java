package com.bixuebihui.util;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import static com.bixuebihui.util.EncryptUtil.*;

public class EncryptUtilTest {

    @Test
    public void test1(){
        String content = "test";
        String password = "1234567890ABCDEF";
        //加密
        System.out.println("加密前：" + content);
        byte[] encryptResult = encrypt(content.getBytes(), password);
        String encryptResultStr = parseByte2HexStr(encryptResult);
        System.out.println("加密后：" + encryptResultStr);
        //解密
        byte[] decryptFrom = parseHexStr2Byte(encryptResultStr);
        byte[] decryptResult = decrypt(decryptFrom,password);
        System.out.println("解密后：" + new String(decryptResult));
    }

    @Test
    public void test0(){
        String content = "test";
        String password = "1234567812345678";
        //加密
        System.out.println("加密前：" + content);
        byte[] encryptResult = encrypt(content.getBytes(), password);
        //解密
        byte[] decryptResult = decrypt(encryptResult,password);
        System.out.println("解密后：" + new String(decryptResult));
    }

    @Test
    public void testE(){
        String content = "mytopsecret";
        String password = "1234567890abcDEF";
        //加密
        System.out.println("加密前：" + content);
        String encryptResult = encrypt16(content, password);
        System.out.println("加密后：" + encryptResult); //0B388DEAB7FE9A7C47984497D30B0222
        //解密
        String decryptResult = decrypt16(encryptResult,password);
        System.out.println("解密后：" + decryptResult);

        //加密
        System.out.println("加密前：" + content);
        String encryptResult64 = encrypt64(content, password);
        System.out.println("加密后：" + encryptResult64); //0B388DEAB7FE9A7C47984497D30B0222
        //解密
        String decryptResult64 = decrypt64(encryptResult64,password);
        System.out.println("解密后：" + decryptResult64);


        byte[] bs = Base64.decodeBase64(encryptResult64);
        System.out.println(parseByte2HexStr(bs));
        System.out.println(new String(bs));
        System.out.println(new String(parseHexStr2Byte(parseByte2HexStr(bs))));
        System.out.println(bs.length);
        System.out.println(parseHexStr2Byte(parseByte2HexStr(bs)).length);
    }

}
