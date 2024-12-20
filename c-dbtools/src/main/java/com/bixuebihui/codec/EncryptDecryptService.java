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

    /**
     * encrypt a list of strings
     * @param list the list of strings to be encrypted
     * @return the list of encrypted strings
     */
    List<String> encrypt(List<String> list);

    /**
     * decrypt a string
     * @param str the string to be decrypted
     * @return the decrypted string
     */
    String decrypt(String str);

    /**
     * decrypt a list of strings
     * @param list the list of strings to be decrypted
     * @return the list of decrypted strings
     */
    List<String> decrypt(List<String> list);
}
