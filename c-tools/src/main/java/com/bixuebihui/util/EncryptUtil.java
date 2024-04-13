package com.bixuebihui.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

/**
 * 近些年DES使用越来越少，原因就在于其使用56位密钥，比较容易被破解，近些年来逐渐被AES替代，
 * AES已经变成目前对称加密中最流行算法之一；AES可以使用128、192、和256位密钥，并且用128位分组加密和解密数据。
 * 本文就简单介绍如何通过JAVA实现AES加密
 * http://blog.csdn.net/hbcui1984/article/details/5201247#
 *
 * @author xingwx
 */
public class EncryptUtil {
    private final static Logger LOG = LoggerFactory.getLogger(EncryptUtil.class);

    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return AES加密后的内容
     */
    private static byte[] encrypt0(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");

            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES");
            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);

            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // 加密
            return cipher.doFinal(byteContent);
        } catch (GeneralSecurityException e) {
            LOG.warn("", e);
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return AES解密后的字节数组
     */
    private static byte[] decrypt20(byte[] content, String password) {

        try {

            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (GeneralSecurityException e) {
            LOG.warn("", e);
        }
        return null;
    }


    /**
     * 加密
     * 还有一种加密方式，大家可以参考如下：
     * 这种加密方式有两种限制
     * <p>
     * 密钥必须是16位的
     * 待加密内容的长度必须是16的倍数，如果不是16的倍数，就会出如下异常：
     * 要解决如上异常，可以通过补全传入加密内容等方式进行避免。
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return 16位补齐过的加密字节数组
     */
    private static byte[] encrypt20(String content, String password) {
        try {
            // 判断Key是否正确
            if (password == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (password.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }

            SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");

            //Sonar http://cwe.mitre.org/data/definitions/327.html
            //The Advanced Encryption Standard (AES) encryption algorithm can be used with various modes. Some combinations are not secured:

            //Electronic Codebook (ECB) mode: Under a given key, any given plaintext block always gets encrypted to the same ciphertext block. Thus, it does not hide data patterns well. In some senses, it doesn't provide serious message confidentiality, and it is not recommended for use in cryptographic protocols at all.
            //Cipher Block Chaining (CBC) with PKCS#5 padding (or PKCS#7) is susceptible to padding oracle attacks.
            //In both cases, Galois/Counter Mode (GCM) with no padding should be preferred.

            //This rule raises an issue when a Cipher instance is created with either ECB or CBC/PKCS5Padding mode.

            //// Noncompliant   //Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

            //Compliant Solution
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            LOG.warn("", e);
        }
        return null;
    }

    public static byte[] decrypt(byte[] sSrc, String sKey) {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = sSrc;
            return cipher.doFinal(encrypted1);
        } catch (Exception e) {
            LOG.warn("", e);
        }
        return null;
    }

    // 判断Key是否正确
    public static byte[] encrypt(byte[] sSrc, String sKey) {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw;
        Cipher cipher;
        byte[] encrypted;
        try {
            raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encrypted = cipher.doFinal(sSrc);
            return encrypted;
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            LOG.warn("", e);
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return 字节转16进制后的字符串
     */
    public static String parseByte2HexStr(byte[] buf) {
        if(buf==null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return 16进制转二进制后的字节数组
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static String encrypt16(String src, String key16) {
        return parseByte2HexStr(encrypt(src.getBytes(), key16));
    }

    public static String decrypt16(String src, String key16) {
        return StringUtils.newStringUtf8(decrypt(parseHexStr2Byte(src), key16));
    }

    public static String encrypt64(String src, String key16) {
        return Base64.encodeBase64URLSafeString(encrypt(src.getBytes(), key16));
    }

    public static String decrypt64(String src, String key16) {
        return StringUtils.newStringUtf8(decrypt(Base64.decodeBase64(src), key16));
    }


}
