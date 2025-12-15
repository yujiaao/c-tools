package com.bixuebihui.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * 16进制数转成36进制，从15字符可以压缩至12字符，压缩率只有20%
 * 16进制转成64进进制，理论上可以6位压至4位，即压缩率有33%
 *
 * @author xwx
 */
public class CompressHexString {

    private static final int RADIX = 36;
    private static final int HEX = 16;

    public static String compress16_36(String hexString) {

        String chunk;
        Long intChunk;
        StringBuffer b62 = new StringBuffer();

        // 14 is the length of 0x20000000000000 (2^53 in base 16)
        hexString = hexString.toLowerCase();
        int max = 15;
        for (int i = 0; i < hexString.length(); i += max) {
            int end = Math.min(i + max, hexString.length());
            chunk = hexString.substring(i, end);

            intChunk = Long.parseLong(chunk, HEX);

            b62.append(StringUtils.leftPad(Long.toString(intChunk, RADIX), 12, '0'));
        }
        return b62.toString();
    }

    // 11 is the length of 2gosa7pa2gv (2^53 in base 36)

    /**
     * 只能解由上面的压缩函数compress16_36产生的字符串
     *
     * @param b36
     * @return 36进制转16进制后的字符串
     */
    public static String decompress36_16(String b36) {
        StringBuffer b16 = new StringBuffer();
        int max = 12;
        for (int i = 0; i < b36.length(); i += max) {
            int end = i + max > b36.length() ? b36.length() : i + max;

            String chunk = b36.substring(i, end);
            Long intChunk = Long.parseLong(chunk, RADIX);

            b16.append(Long.toString(intChunk, HEX));
        }
        return b16.toString().toUpperCase();
    }


    //byte数组转换为64进制char数组
    public static String encode64Digit(byte[] bytes) {
        return Base64.encodeBase64URLSafeString(bytes);
    }

    //64进制String数组转换为byte数组
    public static byte[] decode64Digit(String base64String) {
        return Base64.decodeBase64(base64String);
    }

    public static void main(String[] args) {
        System.out.println(RADIX); ///36

        System.out.println(Long.toString(Long.MAX_VALUE, RADIX)); //1y2p0ij32e8e7 36进制最大整数 只有小写字母和数字,似乎适合当密码
        System.out.println(Long.toString(Long.MAX_VALUE, HEX));  ///7fffffffffffffff  16进制最大整数
        System.out.println(Long.toString(Long.MAX_VALUE));       //9223372036854775807 10进制最大整数
        System.out.println(Long.toString(Long.MIN_VALUE, RADIX)); //-1y2p0ij32e8e8
        System.out.println(Long.toString(Long.MIN_VALUE, HEX));    //-8000000000000000
        System.out.println(Long.toString(Long.MIN_VALUE));   //-9223372036854775808
        long max15 = 0xfffffffffffffffL;

        System.out.println(Long.toString(max15, RADIX));
        System.out.println(Long.toString(max15, HEX));
        System.out.println(Long.toString(max15)); //max15->radix36->length = 12


        String hexString = "1369C2C2187B930C2AABEE80F395B64403848438C700E4A623E8DB2485CE8FE010A45B5E385B7ED0649D0A50FBCF05A12EF106749F9196D15FE9C1B3FA4BB5F7F40C853731157B80EDA1DFDF6E071FC3";
        //1369C2C2187B931B6002D8A2240B10CC55BE697FB812518859ECBED0D10AFE8D98E571C29ED990D0649D0A50FBCF05A12EF106749F9196D15FE9C1B3FA4BB5F7F40C853731157B80EDA1DFDF6E071FC3


        String res = compress16_36(hexString); //0nwuz7z2884w6nscyp44d9km2bs0nbxx2yha2jmhpn2tmx1q4x81f95534p16ak1y95zwd177308f6a5ja6h8iu4davarpv78kb6bzrjhp3n3xqlbm33gfkv0000c8uf5kub
        System.out.println(res);
        res = decompress36_16(res);
        System.out.println(res);
        if (res.equals(hexString)) {
            System.out.println("sucessfull");
        } else {
            System.out.println(hexString);
            System.err.println("error!");
        }


        res = new String(EncryptUtil.parseHexStr2Byte(hexString.toLowerCase()));
        System.out.println(res);
        res = new String(decode64Digit(res));
        System.out.println(res);
        if (res.equals(hexString)) {
            System.out.println("sucessfull");
        } else {
            System.out.println(hexString);
            System.err.println("error!");
        }


    }
}

