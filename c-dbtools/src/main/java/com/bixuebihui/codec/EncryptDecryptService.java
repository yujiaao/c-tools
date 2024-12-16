package com.bixuebihui.codec;

import java.util.List;

/**
 * support one or multiple strings encryption
 */
public interface EncryptDecryptService {
    /**
     * encrypt a string
     * @param str the string to be encrypted
     * @return the encrypted string
     */
    String encrypt(String str);

    List<String> encrypt(List<String> list);

    /**
     * decrypt a string
     * @param str the string to be decrypted
     * @return the decrypted string
     */
    String decrypt(String str);

    List<String> decrypt(List<String> list);
}
