package com.bixuebihui.codec;

import java.util.List;
import java.util.stream.Collectors;

/**
 * support one or multiple strings encryption
 */
public class EncryptDecryptServiceBase64Impl {
    /**
     * encrypt a string
     * @param str the string to be encrypted
     * @return the encrypted string
     */
    String encrypt(String str){
        return java.util.Base64.getEncoder().encodeToString(str.getBytes());
    }

    List<String> encrypt(List<String> list) {
       return list.stream().map(this::encrypt).collect(Collectors.toList());
    }

    /**
     * decrypt a string
     * @param str the string to be decrypted
     * @return the decrypted string
     */
    String decrypt(String str){
        return new String(java.util.Base64.getDecoder().decode(str));
    }

    List<String> decrypt(List<String> list) {
        return list.stream().map(this::decrypt).collect(Collectors.toList());
    }
}
