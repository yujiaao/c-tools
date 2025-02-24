package com.bixuebihui.codec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EncryptDecryptServiceAESImpl implements EncryptDecryptService {
    public static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String ALGORITHM = "AES";
    private static final Logger LOG = LoggerFactory.getLogger(EncryptDecryptServiceAESImpl.class);
    private final String sKey;

    public EncryptDecryptServiceAESImpl(String sKey) {
        if (StringUtils.isBlank(sKey)) {
            LOG.error("sKey must not be blank");
            throw new RuntimeException("sKey must not be blank");
        }
        if (sKey.length() != 16) {
            LOG.error("sKey must be 16 characters");
            throw new RuntimeException("sKey must be 16 characters");
        }
        this.sKey = sKey;
    }

    // 将十六进制字符串转换为字节数组
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    // 将字节数组转换为十六进制字符串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    // 加密
    @Override
    public String encrypt(String sSrc) {
        if (StringUtils.isEmpty(sSrc)) {
            return sSrc;
        }
        try {
            Cipher cipher = getDefaultCipher();
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encrypted);
        } catch (Exception e) {
            LOG.error("encrypt failed:{},{}", e, sSrc);
        }
        return null;
    }

    private Cipher getDefaultCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        return getCipher(Cipher.ENCRYPT_MODE);
    }

    @Override
    public List<String> encrypt(List<String> list) {
        try {
            Cipher cipher = getDefaultCipher();
            return list.stream().map(sSrc -> {
                byte[] encrypted;
                try {
                    encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    throw new RuntimeException(e);
                }
                return bytesToHex(encrypted);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("failed to encrypt:{},{}", e, list);
            return null;

        }
    }

    private Cipher getCipher(int encryptMode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, EncryptDecryptServiceAESImpl.ALGORITHM);
        //"算法/模式/补码方式"
        Cipher cipher = Cipher.getInstance(EncryptDecryptServiceAESImpl.TRANSFORMATION);
        cipher.init(encryptMode, skeySpec);
        return cipher;
    }

    // 解密
    public String decrypt(String sSrc) {
        if (StringUtils.isEmpty(sSrc)) {
            return null;
        }
        try {
            Cipher cipher = getDefaultDescryptCipher();
            byte[] encrypted1 = hexToBytes(sSrc);

            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, StandardCharsets.UTF_8);

        } catch (Exception ex) {
            LOG.error("decrypt:{},{}", ex, sSrc);
            return null;
        }
    }

    private Cipher getDefaultDescryptCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        return getCipher(Cipher.DECRYPT_MODE);
    }

    @Override
    public List<String> decrypt(List<String> list) {
        try {
            Cipher cipher = getDefaultDescryptCipher();
            return transList(list, cipher);
        } catch (Exception ex) {
            LOG.error("decrypt list failed:{},{}", ex, list);
            return null;
        }
    }

    private static List<String> transList(List<String> list, Cipher cipher) {
        return list.stream().map(sSrc -> {
            byte[] encrypted1 = hexToBytes(sSrc);
            byte[] original;
            try {
                original = cipher.doFinal(encrypted1);
                return new String(original, StandardCharsets.UTF_8);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
