// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2006-1-26 15:57:00
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   ContentLinkReplacer.java

package com.bixuebihui.util.html;

import com.bixuebihui.util.other.CMyException;
import com.bixuebihui.util.other.CMyString;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.Hashtable;

public class ContentLinkReplacer {
    public static final int PRIORITY_MAX = 10;
    public static final int PRIORITY_NORM = 5;
    public static final int PRIORITY_MIN = 1;
    public static int KEY_LEN_LIMIT = 20;
    private static String[] TAGS_TOSKIP_ARRAY = {
            "A", "APPLET", "MAP", "OBJECT", "SELECT", "TEXTAREA", "SCRIPT", "OPTION", "INPUT", "STYLE"
    };
    private static Hashtable TAGS_TOSKIP;
    private Hashtable hLinkMap;
    private boolean[] haveKeysStartWith;
    private boolean[] haveKeysWithLen;
    private int nMaxKeyLen;

    public static void main(String[] args) {
        String sLinkMapFile = "d:\\test\\link\\link_table.ini";
        String srcFile = "d:\\test\\link\\news911.htm";
        String dstFile = "d:\\test\\link\\dst.htm";
        String sHtmlDst = null;
        try {
            ContentLinkReplacer replacer = new ContentLinkReplacer();
            replacer.loadLinkMap(sLinkMapFile);
            String sHtmlSrc = FileUtils.readFileToString(new File(srcFile), Charset.defaultCharset());
            for (int i = 0; i < 20; i++) {
                long lStartTime = System.currentTimeMillis();
                sHtmlDst = replacer.replaceLink(sHtmlSrc);
                long lEndTime = System.currentTimeMillis();
                System.out.println("[" + i + "] Used time: " + (lEndTime - lStartTime) + " ms");
            }

            FileUtils.write(new File(dstFile), sHtmlDst, Charset.defaultCharset());
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }


    static {
        TAGS_TOSKIP = new Hashtable();
        for (int i = 0; i < TAGS_TOSKIP_ARRAY.length; i++) {
            TAGS_TOSKIP.put(TAGS_TOSKIP_ARRAY[i].toUpperCase(), TAGS_TOSKIP_ARRAY[i]);
        }

    }

    public static final String[] getTagsToSkip() {
        return TAGS_TOSKIP_ARRAY;
    }

    public void clear() {
        hLinkMap.clear();
    }

    public ContentLinkReplacer() {
        hLinkMap = null;
        haveKeysStartWith = new boolean[65535];
        haveKeysWithLen = new boolean[KEY_LEN_LIMIT + 1];
        nMaxKeyLen = 0;
        hLinkMap = new Hashtable();
        for (int i = 0; i < haveKeysStartWith.length; i++) {
            haveKeysStartWith[i] = false;
        }

        for (int i = 0; i < haveKeysWithLen.length; i++) {
            haveKeysWithLen[i] = false;
        }

    }

    public boolean putLinkMap(String _sKey, String _sLink) {
        if (_sKey == null || _sLink == null) {
            return false;
        }
        _sKey = CMyString.transDisplay(_sKey.trim(), true);
        int nLen = _sKey.length();
        if (nLen == 0) {
            return false;
        }
        _sLink = _sLink.trim();
        if (_sLink.length() == 0) {
            return false;
        }
        hLinkMap.put(_sKey, _sLink);
        int index = nLen <= KEY_LEN_LIMIT ? nLen : 0;
        if (!haveKeysWithLen[index]) {
            haveKeysWithLen[index] = true;
        }
        if (nLen > nMaxKeyLen) {
            nMaxKeyLen = nLen;
        }
        index = _sKey.charAt(0);
        if (!haveKeysStartWith[index]) {
            haveKeysStartWith[index] = true;
        }
        return true;
    }

    public String removeLinkMap(String _sKey) {
        if (_sKey == null) {
            return null;
        } else {
            return (String) hLinkMap.remove(_sKey);
        }
    }

    public String replaceLink(String _srcContent)
            throws CMyException {
        return replaceLink(_srcContent, null);
    }

    public String getMappedLink(String _sKey) {
        if (_sKey == null) {
            return null;
        } else {
            return (String) hLinkMap.get(_sKey);
        }
    }

    public String replaceLink(String _srcContent, String _extraLinkAttributes)
            throws CMyException {
        char[] srcBuff;
        int nLen;
        StringBuffer retBuff;
        int nextPos;
        int currFlag;
        char[] sQuoteStack;
        int nQuoteDeep;
        int FLAG_NORM = 0;
        int FLAG_WAIT_TAG_GT = 1;
        int FLAG_WAIT_SKIPTAG_STARTGT = 2;
        int FLAG_WAIT_SKIPTAG_ENDTAG = 3;
        if (_srcContent == null) {
            return null;
        }
        srcBuff = _srcContent.toCharArray();
        nLen = srcBuff.length;
        if (nLen == 0) {
            return "";
        }
        retBuff = null;
        nextPos = 0;
        currFlag = FLAG_NORM;
        sQuoteStack = new char[20];
        nQuoteDeep = -1;
        boolean bToSkip = false;
        retBuff = new StringBuffer((int) ((double) nLen * 1.5D));
        try {
            while (nextPos < nLen) {
                char currChar = srcBuff[nextPos++];
                if (currChar == '\n' || currChar == '\r') {
                    retBuff.append(currChar);
                } else if (nQuoteDeep >= 0) {
                    if (currChar == sQuoteStack[nQuoteDeep] && srcBuff[nextPos - 1] != '\\') {
                        nQuoteDeep--;
                    }
                    retBuff.append(currChar);
                } else {
                    switch (currChar) {
                        case 34: // '"'
                        case 39: // '\''
                            if (currFlag == FLAG_WAIT_TAG_GT || currFlag == FLAG_WAIT_SKIPTAG_STARTGT) {
                                nQuoteDeep++;
                                sQuoteStack[nQuoteDeep] = currChar;
                                retBuff.append(currChar);
                                bToSkip = true;
                            }
                            break;

                        case 60: // '<'
                            retBuff.append('<');
                            bToSkip = true;
                            if (nextPos >= nLen) {
                                break;
                            }
                            currChar = srcBuff[nextPos++];
                            if (currChar == '/') {
                                retBuff.append('/');
                                if (currFlag != FLAG_WAIT_SKIPTAG_ENDTAG || nextPos >= nLen) {
                                    break;
                                }
                                currChar = srcBuff[nextPos++];
                            } else if (currFlag != FLAG_NORM || currChar == '!') {
                                retBuff.append(currChar);
                                break;
                            }
                            String sTagName = "";
                            for (; Character.isWhitespace(currChar); currChar = srcBuff[nextPos++]) {
                                retBuff.append(currChar);
                                if (nextPos >= nLen) {
                                    break;
                                }
                            }

                            for (; !Character.isWhitespace(currChar) && currChar != '>'; currChar = srcBuff[nextPos++]) {
                                sTagName = sTagName + currChar;
                                if (nextPos >= nLen) {
                                    break;
                                }
                            }

                            retBuff.append(sTagName);
                            if (nextPos < nLen) {
                                retBuff.append(currChar);
                            }
                            if (TAGS_TOSKIP.get(sTagName.toUpperCase()) != null) {
                                if (currFlag == FLAG_NORM) {
                                    currFlag = FLAG_WAIT_SKIPTAG_STARTGT;
                                } else {
                                    currFlag = currChar != '>' ? FLAG_WAIT_TAG_GT : FLAG_NORM;
                                }
                            }
                            break;

                        case 62: // '>'
                            retBuff.append('>');
                            bToSkip = true;
                            if (currFlag == FLAG_WAIT_TAG_GT) {
                                currFlag = FLAG_NORM;
                            } else if (currFlag == FLAG_WAIT_SKIPTAG_STARTGT) {
                                currFlag = FLAG_WAIT_SKIPTAG_ENDTAG;
                            }
                            break;

                        default:
                            if (currFlag != FLAG_NORM) {
                                retBuff.append(currChar);
                                bToSkip = true;
                            }
                            break;
                    }
                    if (!bToSkip) {
                        if (!haveKeysStartWith[currChar]) {
                            retBuff.append(currChar);
                        } else {
                            String sMatch = String.valueOf(currChar);
                            boolean bFound = false;
                            int nAt = nextPos;
                            for (int nKeyLen = 1; nKeyLen <= nMaxKeyLen; nKeyLen++) {
                                if (haveKeysWithLen[nKeyLen]) {
                                    String sLink = (String) hLinkMap.get(sMatch);
                                    if (sLink != null) {
                                        retBuff.append("<a href=").append(sLink);
                                        if (_extraLinkAttributes != null) {
                                            retBuff.append(' ').append(_extraLinkAttributes);
                                        }
                                        retBuff.append('>').append(sMatch).append("</a>");
                                        nextPos = nAt;
                                        bFound = true;
                                        break;
                                    }
                                }
                                if (nAt >= nLen) {
                                    break;
                                }
                                char nextChar = srcBuff[nAt++];
                                if (nextChar == '\n' || nextChar == '\r') {
                                    break;
                                }
                                sMatch = sMatch + nextChar;
                            }

                            if (!bFound) {
                                retBuff.append(currChar);
                            }
                        }
                    }
                }
            }
            return retBuff.toString();
        } catch (IndexOutOfBoundsException ex) {
            throw new CMyException(1, "\u66FF\u6362\u94FE\u63A5\u5931\u8D25\uFF08DocInnerLinkReplacer.replaceLink\uFF09", ex);
        }
    }

    private class LinkMapItem {

        char[] keywords;
        String link;
        int priority;
        LinkMapItem conjugate;
        LinkMapItem pre;
        LinkMapItem next;

        public LinkMapItem(String _keywords, String _link) {
            this(_keywords, _link, 5);
        }

        public LinkMapItem(String _keywords, String _link, int _priority) {
            conjugate = null;
            pre = null;
            next = null;
            keywords = _keywords.toCharArray();
            link = _link;
            priority = _priority;
        }
    }



    public boolean loadLinkMap(String _linkMapFile)
            throws CMyException {
        File file = new File(_linkMapFile);
        try( FileReader fileReader = new FileReader(file)){
            BufferedReader buffReader = new BufferedReader(fileReader);
            String s;
            while ((s = buffReader.readLine()) != null) {
                int nPos = s.indexOf('=');
                if (nPos >= 0) {
                    putLinkMap(s.substring(0, nPos), s.substring(nPos + 1));
                }
            }
            buffReader.close();
            return true;
        } catch (Exception ex) {
            throw new CMyException(1, "\u88C5\u8F7D\u94FE\u63A5\u6620\u5C04\u8868\u5931\u8D25(ContentLinkReplacer.loadLinkMap)", ex);
        }
    }
}
