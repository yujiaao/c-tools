// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2006-1-26 15:56:55
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   CMyString.java

package com.bixuebihui.util.other;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;


public class CMyString {

    public CMyString() {
    }

    public static boolean isEmpty(String _string) {
        return _string == null || _string.trim().length() == 0;
    }

    /**
     * @deprecated Method isEmptyStr is deprecated
     */

    @Deprecated
    public static boolean isEmptyStr(String _string) {
        return _string == null || _string.trim().length() == 0;
    }

    public static String showObjNull(Object p_sValue) {
        return showObjNull(p_sValue, "");
    }

    public static String showObjNull(Object _sValue, String _sReplaceIfNull) {
        if (_sValue == null) {
            return _sReplaceIfNull;
        } else {
            return _sValue.toString();
        }
    }

    public static String showNull(String p_sValue) {
        return showNull(p_sValue, "");
    }

    public static String showNull(String _sValue, String _sReplaceIfNull) {
        return _sValue != null ? _sValue : _sReplaceIfNull;
    }

    public static String expandStr(String _string, int _length, char _chrFill, boolean _bFillOnLeft) {
        int nLen = _string.length();
        if (_length <= nLen) {
            return _string;
        }
        String sRet = _string;
        for (int i = 0; i < _length - nLen; i++) {
            sRet = _bFillOnLeft ? _chrFill + sRet : sRet + _chrFill;
        }

        return sRet;
    }

    public static String setStrEndWith(String _string, char _chrEnd) {
        if (_string == null) {
            return null;
        }
        if (_string.charAt(_string.length() - 1) != _chrEnd) {
            return _string + _chrEnd;
        } else {
            return _string;
        }
    }

    public static String makeBlanks(int _length) {
        if (_length < 1) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(_length);
        for (int i = 0; i < _length; i++) {
            buffer.append(' ');
        }

        return buffer.toString();
    }

    public static String replaceStr(String _strSrc, String _strOld, String _strNew) {
        if (_strSrc == null) {
            return null;
        }
        char[] srcBuff = _strSrc.toCharArray();
        int nSrcLen = srcBuff.length;
        if (nSrcLen == 0) {
            return "";
        }
        char[] oldStrBuff = _strOld.toCharArray();
        int nOldStrLen = oldStrBuff.length;
        if (nOldStrLen == 0 || nOldStrLen > nSrcLen) {
            return _strSrc;
        }
        StringBuilder retBuff = new StringBuilder(nSrcLen * (1 + _strNew.length() / nOldStrLen));
        boolean bIsFound;
        for (int i = 0; i < nSrcLen; ) {
            bIsFound = false;
            if (srcBuff[i] == oldStrBuff[0]) {
                int j;
                for (j = 1; j < nOldStrLen; j++) {
                    if (i + j >= nSrcLen || srcBuff[i + j] != oldStrBuff[j]) {
                        break;
                    }
                }

                bIsFound = j == nOldStrLen;
            }
            if (bIsFound) {
                retBuff.append(_strNew);
                i += nOldStrLen;
            } else {
                int nSkipTo;
                if (i + nOldStrLen >= nSrcLen) {
                    nSkipTo = nSrcLen - 1;
                } else {
                    nSkipTo = i;
                }
                for (; i <= nSkipTo; i++) {
                    retBuff.append(srcBuff[i]);
                }

            }
        }

        return retBuff.toString();
    }

    public static String replaceStr(StringBuffer _strSrc, String _strOld, String _strNew) {
        if (_strSrc == null) {
            return null;
        }
        int nSrcLen = _strSrc.length();
        if (nSrcLen == 0) {
            return "";
        }
        char[] oldStrBuff = _strOld.toCharArray();
        int nOldStrLen = oldStrBuff.length;
        if (nOldStrLen == 0 || nOldStrLen > nSrcLen) {
            return _strSrc.toString();
        }
        StringBuilder retBuff = new StringBuilder(nSrcLen * (1 + _strNew.length() / nOldStrLen));
        boolean bIsFound;
        for (int i = 0; i < nSrcLen; ) {
            bIsFound = false;
            if (_strSrc.charAt(i) == oldStrBuff[0]) {
                int j;
                for (j = 1; j < nOldStrLen; j++) {
                    if (i + j >= nSrcLen || _strSrc.charAt(i + j) != oldStrBuff[j]) {
                        break;
                    }
                }

                bIsFound = j == nOldStrLen;
            }
            if (bIsFound) {
                retBuff.append(_strNew);
                i += nOldStrLen;
            } else {
                int nSkipTo;
                if (i + nOldStrLen >= nSrcLen) {
                    nSkipTo = nSrcLen - 1;
                } else {
                    nSkipTo = i;
                }
                for (; i <= nSkipTo; i++) {
                    retBuff.append(_strSrc.charAt(i));
                }

            }
        }

        return retBuff.toString();
    }

    public static String getStr(String _strSrc) {
        return getStr(_strSrc, ENCODING_DEFAULT);
    }

    public static String getStr(String _strSrc, boolean _bPostMethod) {
        return getStr(_strSrc, _bPostMethod ? ENCODING_DEFAULT : GET_ENCODING_DEFAULT);
    }

    public static String getStr(String _strSrc, String _encoding) {
        if (_encoding == null || _encoding.length() == 0) {
            return _strSrc;
        }
        byte[] byteStr;
        try {
            byteStr = _strSrc.getBytes();
            return new String(byteStr, _encoding);
        } catch (Exception ex) {
        }
        return _strSrc;
    }

    public static String toISO_8859(String _strSrc) {
        if (_strSrc == null) {
            return null;
        }
        try {
            return new String(_strSrc.getBytes(), StandardCharsets.ISO_8859_1);
        } catch (Exception ex) {
        }
        return _strSrc;
    }

    /**
     * @deprecated Method toUnicode is deprecated
     */
    @Deprecated
    public static String toUnicode(String _strSrc) {
        return toISO_8859(_strSrc);
    }

    public static byte[] getUTF8Bytes(String _string) {
        char[] c = _string.toCharArray();
        int len = c.length;
        int count = 0;
        for (char ch : c) {
            if (ch <= 127) {
                count++;
            } else if (ch <= 2047) {
                count += 2;
            } else {
                count += 3;
            }
        }

        byte[] b = new byte[count];
        int off = 0;
        for (int i = 0; i < len; i++) {
            int ch = c[i];
            if (ch <= 127) {
                b[off++] = (byte) ch;
            } else if (ch <= 2047) {
                b[off++] = (byte) (ch >> 6 | 0xc0);
                b[off++] = (byte) (ch & 0x3f | 0x80);
            } else {
                b[off++] = (byte) (ch >> 12 | 0xe0);
                b[off++] = (byte) (ch >> 6 & 0x3f | 0x80);
                b[off++] = (byte) (ch & 0x3f | 0x80);
            }
        }

        return b;
    }

    public static String getUTF8String(byte[] b) {
        return getUTF8String(b, 0, b.length);
    }

    public static String getUTF8String(byte[] b, int off, int len) {
        int count = 0;
        int max = off + len;
        int i;
        for (i = off; i < max; ) {
            int c = b[i++] & 0xff;
            switch (c >> 4) {
                case 0: // '\0'
                case 1: // '\001'
                case 2: // '\002'
                case 3: // '\003'
                case 4: // '\004'
                case 5: // '\005'
                case 6: // '\006'
                case 7: // '\007'
                    count++;
                    break;

                case 12: // '\f'
                case 13: // '\r'
                    if ((b[i++] & 0xc0) != 128) {
                        throw new IllegalArgumentException();
                    }
                    count++;
                    break;

                case 14: // '\016'
                    if ((b[i++] & 0xc0) != 128) {
                        throw new IllegalArgumentException();
                    }
                    if ((b[i++] & 0xc0) != 128) {
                        throw new IllegalArgumentException();
                    }
                    count++;
                    break;

                case 8: // '\b'
                case 9: // '\t'
                case 10: // '\n'
                case 11: // '\013'
                default:
                    throw new IllegalArgumentException();
            }
        }

        if (i != max) {
            throw new IllegalArgumentException();
        }
        char[] cs = new char[count];
        i = 0;
        while (off < max) {
            int c = b[off++] & 0xff;
            switch (c >> 4) {
                case 0: // '\0'
                case 1: // '\001'
                case 2: // '\002'
                case 3: // '\003'
                case 4: // '\004'
                case 5: // '\005'
                case 6: // '\006'
                case 7: // '\007'
                    cs[i++] = (char) c;
                    break;

                case 12: // '\f'
                case 13: // '\r'
                    cs[i++] = (char) ((c & 0x1f) << 6 | b[off++] & 0x3f);
                    break;

                case 14: // '\016'
                    int t = (b[off++] & 0x3f) << 6;
                    cs[i++] = (char) ((c & 0xf) << 12 | t | b[off++] & 0x3f);
                    break;

                case 8: // '\b'
                case 9: // '\t'
                case 10: // '\n'
                case 11: // '\013'
                default:
                    throw new IllegalArgumentException();
            }
        }
        return new String(cs, 0, count);
    }

    public static String byteToHexString(byte[] _bytes) {
        return byteToHexString(_bytes, ',');
    }

    public static String byteToHexString(byte[] bytes, char delim) {
        StringBuilder sRet = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
                sRet.append(delim);
            }
            sRet.append(Integer.toHexString(bytes[i]));
        }

        return sRet.toString();
    }

    public static String byteToString(byte[] _bytes, char delim, int _radix) {
        StringBuilder sRet = new StringBuilder();
        for (int i = 0; i < _bytes.length; i++) {
            if (i > 0) {
                sRet.append(delim);
            }
            sRet.append( Integer.toString(_bytes[i], _radix));
        }

        return sRet.toString();
    }

    public static String transDisplay(String _sContent) {
        return transDisplay(_sContent, true);
    }

    public static String transDisplay(String _sContent, boolean _bChangeBlank) {
        if (_sContent == null) {
            return "";
        }
        char[] srcBuff = _sContent.toCharArray();
        int nSrcLen = srcBuff.length;
        StringBuffer retBuff = new StringBuffer(nSrcLen * 2);
        for (int i = 0; i < nSrcLen; i++) {
            char cTemp = srcBuff[i];
            switch (cTemp) {
                case 32: // ' '
                    retBuff.append(_bChangeBlank ? "&nbsp;" : " ");
                    break;

                case 60: // '<'
                    retBuff.append("&lt;");
                    break;

                case 62: // '>'
                    retBuff.append("&gt;");
                    break;

                case 10: // '\n'
                    retBuff.append("<br>");
                    break;

                case 34: // '"'
                    retBuff.append("&quot;");
                    break;

                case 38: // '&'
                    boolean bUnicode = false;
                    for (int j = i + 1; j < nSrcLen && !bUnicode; j++) {
                        cTemp = srcBuff[j];
                        if (cTemp == '#' || cTemp == ';') {
                            retBuff.append("&");
                            bUnicode = true;
                        }
                    }

                    if (!bUnicode) {
                        retBuff.append("&amp;");
                    }
                    break;

                case 9: // '\t'
                    retBuff.append(_bChangeBlank ? "&nbsp;&nbsp;&nbsp;&nbsp;" : "    ");
                    break;

                default:
                    retBuff.append(cTemp);
                    break;
            }
        }

        return retBuff.toString();
    }

    public static String transDisplay_bbs(String _sContent, String p_sQuoteColor) {
        return transDisplay_bbs(_sContent, p_sQuoteColor, true);
    }

    public static String transDisplay_bbs(String _sContent, String p_sQuoteColor, boolean _bChangeBlank) {
        if (_sContent == null) {
            return "";
        }
        boolean bIsQuote = false;
        boolean bIsNewLine = true;
        char[] srcBuff = _sContent.toCharArray();
        int nSrcLen = srcBuff.length;
        StringBuffer retBuff = new StringBuffer((int) ((double) nSrcLen * 1.8D));
        for (int i = 0; i < nSrcLen; i++) {
            char cTemp = srcBuff[i];
            switch (cTemp) {
                case 58: // ':'
                    if (bIsNewLine) {
                        bIsQuote = true;
                        retBuff.append("<font color=" + p_sQuoteColor + ">:");
                    } else {
                        retBuff.append(":");
                    }
                    bIsNewLine = false;
                    break;

                case 32: // ' '
                    retBuff.append(_bChangeBlank ? "&nbsp;" : " ");
                    bIsNewLine = false;
                    break;

                case 60: // '<'
                    retBuff.append("&lt;");
                    bIsNewLine = false;
                    break;

                case 62: // '>'
                    retBuff.append("&gt;");
                    bIsNewLine = false;
                    break;

                case 34: // '"'
                    retBuff.append("&quot;");
                    bIsNewLine = false;
                    break;

                case 38: // '&'
                    retBuff.append("&amp;");
                    bIsNewLine = false;
                    break;

                case 9: // '\t'
                    retBuff.append(_bChangeBlank ? "&nbsp;&nbsp;&nbsp;&nbsp;" : "    ");
                    bIsNewLine = false;
                    break;

                case 10: // '\n'
                    if (bIsQuote) {
                        bIsQuote = false;
                        retBuff.append("</font>");
                    }
                    retBuff.append("<br>");
                    bIsNewLine = true;
                    break;

                default:
                    retBuff.append(cTemp);
                    bIsNewLine = false;
                    break;
            }
        }

        if (bIsQuote) {
            retBuff.append("</font>");
        }
        return retBuff.toString();
    }

    public static String transJsDisplay(String _sContent) {
        if (_sContent == null) {
            return "";
        }
        char[] srcBuff = _sContent.toCharArray();
        int nSrcLen = srcBuff.length;
        StringBuffer retBuff = new StringBuffer((int) ((double) nSrcLen * 1.5D));
        for (int i = 0; i < nSrcLen; i++) {
            char cTemp = srcBuff[i];
            switch (cTemp) {
                case 60: // '<'
                    retBuff.append("&lt;");
                    break;

                case 62: // '>'
                    retBuff.append("&gt;");
                    break;

                case 34: // '"'
                    retBuff.append("&quot;");
                    break;

                default:
                    retBuff.append(cTemp);
                    break;
            }
        }

        return retBuff.toString();
    }

    public static String transDisplayMark(String _strSrc, char p_chrMark) {
        if (_strSrc == null) {
            return "";
        }
        char[] buff = new char[_strSrc.length()];
        for (int i = 0; i < buff.length; i++) {
            buff[i] = p_chrMark;
        }

        return new String(buff);
    }

    public static String filterForSQL(String _sContent) {
        if (_sContent == null) {
            return "";
        }
        int nLen = _sContent.length();
        if (nLen == 0) {
            return "";
        }
        char[] srcBuff = _sContent.toCharArray();
        StringBuffer retBuff = new StringBuffer((int) ((double) nLen * 1.5D));
        for (int i = 0; i < nLen; i++) {
            char cTemp = srcBuff[i];
            switch (cTemp) {
                case 39: // '\''
                    retBuff.append("''");
                    break;

                case 59: // ';'
                    boolean bSkip = false;
                    for (int j = i + 1; j < nLen && !bSkip; j++) {
                        char cTemp2 = srcBuff[j];
                        if (cTemp2 != ' ') {
                            if (cTemp2 == '&') {
                                retBuff.append(';');
                            }
                            bSkip = true;
                        }
                    }

                    if (!bSkip) {
                        retBuff.append(';');
                    }
                    break;

                default:
                    retBuff.append(cTemp);
                    break;
            }
        }

        return retBuff.toString();
    }

    public static String filterForXML(String _sContent) {
        if (_sContent == null) {
            return "";
        }
        char[] srcBuff = _sContent.toCharArray();
        int nLen = srcBuff.length;
        if (nLen == 0) {
            return "";
        }
        StringBuffer retBuff = new StringBuffer((int) ((double) nLen * 1.8D));
        for (int i = 0; i < nLen; i++) {
            char cTemp = srcBuff[i];
            switch (cTemp) {
                case 38: // '&'
                    retBuff.append("&amp;");
                    break;

                case 60: // '<'
                    retBuff.append("&lt;");
                    break;

                case 62: // '>'
                    retBuff.append("&gt;");
                    break;

                case 34: // '"'
                    retBuff.append("&quot;");
                    break;

                case 39: // '\''
                    retBuff.append("&apos;");
                    break;

                default:
                    retBuff.append(cTemp);
                    break;
            }
        }

        return retBuff.toString();
    }

    public static String filterForHTMLValue(String _sContent) {
        if (_sContent == null) {
            return "";
        }
        char[] srcBuff = _sContent.toCharArray();
        int nLen = srcBuff.length;
        if (nLen == 0) {
            return "";
        }
        StringBuffer retBuff = new StringBuffer((int) ((double) nLen * 1.8D));
        for (int i = 0; i < nLen; i++) {
            char cTemp = srcBuff[i];
            switch (cTemp) {
                case 38: // '&'
                    if (i + 1 < nLen) {
                        cTemp = srcBuff[i + 1];
                        if (cTemp == '#') {
                            retBuff.append("&");
                        } else {
                            retBuff.append("&amp;");
                        }
                    } else {
                        retBuff.append("&amp;");
                    }
                    break;

                case 60: // '<'
                    retBuff.append("&lt;");
                    break;

                case 62: // '>'
                    retBuff.append("&gt;");
                    break;

                case 34: // '"'
                    retBuff.append("&quot;");
                    break;

                default:
                    retBuff.append(cTemp);
                    break;
            }
        }

        return retBuff.toString();
    }

    public static String filterForUrl(String _sContent) {
        if (_sContent == null) {
            return "";
        }
        char[] srcBuff = _sContent.toCharArray();
        int nLen = srcBuff.length;
        if (nLen == 0) {
            return "";
        }
        StringBuffer retBuff = new StringBuffer((int) ((double) nLen * 1.8D));
        for (int i = 0; i < nLen; i++) {
            char cTemp = srcBuff[i];
            switch (cTemp) {
                case 37: // '%'
                    retBuff.append("%25");
                    break;

                case 63: // '?'
                    retBuff.append("%3F");
                    break;

                case 35: // '#'
                    retBuff.append("%23");
                    break;

                case 38: // '&'
                    retBuff.append("%26");
                    break;

                case 32: // ' '
                    retBuff.append("%20");
                    break;

                default:
                    retBuff.append(cTemp);
                    break;
            }
        }

        return retBuff.toString();
    }

    public static String filterForJs(String _sContent) {
        if (_sContent == null) {
            return "";
        }
        char[] srcBuff = _sContent.toCharArray();
        int nLen = srcBuff.length;
        if (nLen == 0) {
            return "";
        }
        StringBuffer retBuff = new StringBuffer((int) ((double) nLen * 1.8D));
        for (int i = 0; i < nLen; i++) {
            char cTemp = srcBuff[i];
            switch (cTemp) {
                case 34: // '"'
                    retBuff.append("\\\"");
                    break;

                case 92: // '\\'
                    retBuff.append("\\\\");
                    break;

                case 10: // '\n'
                    retBuff.append("\\n");
                    break;

                case 13: // '\r'
                    retBuff.append("\\r");
                    break;

                case 12: // '\f'
                    retBuff.append("\\f");
                    break;

                case 9: // '\t'
                    retBuff.append("\\t");
                    break;

                case 47: // '/'
                    retBuff.append("\\/");
                    break;

                default:
                    retBuff.append(cTemp);
                    break;
            }
        }

        return retBuff.toString();
    }

    public static String numberToStr(int _nValue) {
        return numberToStr(_nValue, 0);
    }

    public static String numberToStr(int _nValue, int _length) {
        return numberToStr(_nValue, _length, '0');
    }

    public static String numberToStr(int _nValue, int _length, char _chrFill) {
        String sValue = String.valueOf(_nValue);
        return expandStr(sValue, _length, _chrFill, true);
    }

    public static String numberToStr(long _lValue) {
        return numberToStr(_lValue, 0);
    }

    public static String numberToStr(long _lValue, int _length) {
        return numberToStr(_lValue, _length, '0');
    }

    public static String numberToStr(long _lValue, int _length, char _chrFill) {
        String sValue = String.valueOf(_lValue);
        return expandStr(sValue, _length, _chrFill, true);
    }

    public static String circleStr(String _strSrc) {
        if (_strSrc == null) {
            return null;
        }
        String sResult = "";
        int nLength = _strSrc.length();
        for (int i = nLength - 1; i >= 0; i--) {
            sResult = sResult + _strSrc.charAt(i);
        }

        return sResult;
    }

    public static String truncateStr(String _string, int _maxLength) {
        return truncateStr(_string, _maxLength, "..");
    }

    public static String truncateStr(String _string, int _maxLength, String _sExt) {
        if (_string == null) {
            return null;
        }
        String sExt = "..";
        if (_sExt != null) {
            sExt = _sExt;
        }
        int nExtLen = getBytesLength(sExt);
        if (nExtLen >= _maxLength) {
            return _string;
        }
        int nMaxLen = (_maxLength - nExtLen) + 1;
        char[] srcBuff = _string.toCharArray();
        int nLen = srcBuff.length;
        StringBuilder dstBuff = new StringBuilder(nLen + 2);
        int nGet = 0;
        for (int i = 0; i < nLen; i++) {
            char aChar = srcBuff[i];
            boolean bUnicode = false;
            int j = 0;
            if (aChar == '&') {
                for (j = i + 1; j < nLen && j < i + 9 && !bUnicode; j++) {
                    char cTemp = srcBuff[j];
                    if (cTemp != ';') {
                        continue;
                    }
                    if (j == i + 5) {
                        bUnicode = false;
                        j = 0;
                        break;
                    }
                    bUnicode = true;
                }

                nGet++;
            } else {
                nGet += aChar > '\177' ? 2 : 1;
            }
            if (nGet >= nMaxLen) {
                if (nGet == _maxLength && i == nLen - 1) {
                    dstBuff.append(aChar);
                    for (; i < j - 1; i++) {
                        dstBuff.append(srcBuff[i + 1]);
                    }

                } else {
                    dstBuff.append(sExt);
                }
                break;
            }
            dstBuff.append(aChar);
            for (; i < j - 1; i++) {
                dstBuff.append(srcBuff[i + 1]);
            }

        }

        return dstBuff.toString();
    }

    public static String filterForJDOM(String _string) {
        if (_string == null) {
            return null;
        }
        char[] srcBuff = _string.toCharArray();
        int nLen = srcBuff.length;
        StringBuilder dstBuff = new StringBuilder(nLen);
        for (int i = 0; i < nLen; i++) {
            char aChar = srcBuff[i];
            if (aChar >= ' ' || i != nLen - 1) {
                dstBuff.append(aChar);
            }
        }

        return dstBuff.toString();
    }

    public static int getBytesLength(String _string) {
        if (_string == null) {
            return 0;
        }
        char[] srcBuff = _string.toCharArray();
        int nGet = 0;
        for (int i = 0; i < srcBuff.length; i++) {
            char aChar = srcBuff[i];
            nGet += aChar > '\177' ? 2 : 1;
        }

        return nGet;
    }

    /**
     * @deprecated Method cutStr is deprecated
     */
    @Deprecated
    public static String cutStr(String _string, int _length) {
        return truncateStr(_string, _length);
    }

    public static String URLEncode(String s) {
        try {
            return URLEncoder.encode(s,CMyString.ENCODING_DEFAULT);
        } catch (Exception ex) {

        }

        return s;
    }

    public static String[] split(String _str, String _sDelim)
            throws Exception {
        StringTokenizer stTemp = new StringTokenizer(_str, _sDelim);
        int nSize = stTemp.countTokens();
        if (nSize == 0) {
            return new String[0];
        }
        String[] str = new String[nSize];
        for (int i = 0; stTemp.hasMoreElements(); i++) {
            str[i] = stTemp.nextToken();
        }

        return str;
    }

    public static void main(String[] args) {
        try {
            split("5,dfsfda,fdaffasf", ",");
            split("", ",");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String str = "&#1575;&#1604;&#1605;&#1580;&#1604;&#1587; &#1575;&#1604;&#1608;&#1591;&#1606;&#1609; &#1604;&#1606;&#1608;&#1575;&#1576; &#1575;&#1604;&#1588;&#1593;&#1576;";
        System.out.println("============" + truncateStr(str, 6).length() + "=====================");
        System.out.println("============" + truncateStr(str, 6) + "=====================");
        StringBuffer strBuffer = new StringBuffer(100);
        System.out.println("===================test for replaceStr===================================");
        System.out.println(CMyString.replaceStr("a11b22c11", "11", "55"));
        System.out.println(CMyString.replaceStr("\u4E2D\u6587\u6D4B\u8BD5\u6D4B\u8BD5\u4E2D\u6587", "\u4E2D\u6587", "\u6C49\u5B57"));
        System.out.println(CMyString.replaceStr("a1\u6DF7\u5408\u6D4B\u8BD51b2\u6D4B\u8BD5\u6DF7\u54082c1\u6DF7\u5408\u6D4B\u8BD51", "1\u6DF7\u5408\u6D4B\u8BD51", "5\u6210\u529F5"));
        System.out.println(CMyString.replaceStr("a11", "1122", "55"));
        System.out.println(CMyString.replaceStr(strBuffer.append("a11b22c11"), "11", "55"));
        strBuffer = new StringBuffer(100);
        System.out.println(CMyString.replaceStr(strBuffer.append("\u4E2D\u6587\u6D4B\u8BD5\u6D4B\u8BD5\u4E2D\u6587"), "\u4E2D\u6587", "\u6C49\u5B57"));
        strBuffer = new StringBuffer(100);
        System.out.println(CMyString.replaceStr(strBuffer.append("a1\u6DF7\u5408\u6D4B\u8BD51b2\u6D4B\u8BD5\u6DF7\u54082c1\u6DF7\u5408\u6D4B\u8BD51"), "1\u6DF7\u5408\u6D4B\u8BD51", "5\u6210\u529F5"));
        strBuffer = new StringBuffer(100);
        System.out.println(CMyString.replaceStr(strBuffer.append("a11"), "1122", "55"));
        System.out.println(CMyString.transDisplay("this is a<test>for \ntransplay.\""));
        System.out.println("[Password=]" + CMyString.transDisplayMark("must be stars", '*'));
        System.out.println(CMyString.filterForSQL("a'bcd*e&f#"));
        System.out.println(numberToStr(123, 5, '0'));
        System.out.println(setStrEndWith("d:\\test", '\\'));
        System.out.println(setStrEndWith("d:\\test\\", '\\'));
        System.out.println(filterForJs("a>'1'\""));
        try {
            String sValue = "123\u4E2D1\u6587a";
            System.out.println("truncateStr=" + truncateStr(sValue, 7));
            sValue = "1231\u4E2D1\u6587a";
            System.out.println("truncateStr=" + truncateStr(sValue, 7));
            sValue = "123\u4E2D\u6587a";
            System.out.println("truncateStr=" + truncateStr(sValue, 7));
            sValue = "12345678";
            System.out.println("truncateStr=" + truncateStr(sValue, 7));
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        System.out.println("Integer.toString(2)=" + Integer.toString(10, 2));
        System.out.println("Integer.toString(8)=" + Integer.toString(10, 8));
        System.out.println("Integer.toString(16)=" + Integer.toString(10, 16));
        try {
            String sValue = "\u4F60";
            byte[] b = getUTF8Bytes(sValue);
            System.out.println("UTF8Bytes=" + byteToHexString(b));
            System.out.println("UTF8   -->" + byteToHexString(sValue.getBytes("UTF8")));
            System.out.println("default-->" + byteToHexString(sValue.getBytes()));
            System.out.println("gb2312 -->" + byteToHexString(sValue.getBytes("gb2312")));
            System.out.println("GBK    -->" + byteToHexString(sValue.getBytes("GBK")));
            System.out.println("ISO8859-->" + byteToHexString(sValue.getBytes("ISO-8859-1")));
            char[] c = new char[sValue.length()];
            sValue.getChars(0, sValue.length(), c, 0);
            sValue = getUTF8String(b, 0, b.length);
            System.out.println("UTF8String=" + sValue);
            System.out.println("        -->" + new String(b, "UTF8"));
            sValue = "====================value:$$HTMLCONTENT$$end====================";
            String sExcel = "123455";
            String sTRS = "$$HTMLCONTENT$$";
            int nPose = sValue.indexOf(sTRS);
            if (nPose >= 0) {
                System.out.println(sValue.substring(0, nPose) + sExcel + sValue.substring(nPose + sTRS.length()));
            } else {
                System.out.println("no found");
            }
            sValue = new String(sValue.getBytes("UTF8"), "UTF8");
            System.out.println("     GBK-->" + sValue);
            System.out.println(byteToHexString(sValue.getBytes("GBK")));
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public static String ENCODING_DEFAULT = "UTF-8";
    public static String GET_ENCODING_DEFAULT = "UTF-8";
    public static String FILE_WRITING_ENCODING = "GBK";


}
