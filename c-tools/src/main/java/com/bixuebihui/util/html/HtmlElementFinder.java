package com.bixuebihui.util.html;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author xwx
 */
public class HtmlElementFinder {

    public HtmlElementFinder() {
        srcBuffer = null;
        nCurrPos = 0;
        conBuffer = null;
    }

    public HtmlElementFinder(String _src) {
        srcBuffer = null;
        nCurrPos = 0;
        conBuffer = null;
        setHtmlSrc(_src);
    }

    private char[] srcBuffer;

    public HtmlElementFinder(char[] _src) {
        srcBuffer = null;
        nCurrPos = 0;
        conBuffer = null;
        setHtmlSrc(_src);
    }

    public static void main(String[] args) {
        try {
            String sFileName = "D:\\test.html";
            String strSrc = FileUtils.readFileToString(new File(sFileName), Charset.defaultCharset());
            System.out
                    .println("\n\n*****************  Result *********************");
            String[] arTagName = new String[7];
            arTagName[0] = "TR";
            String[] arTagSrcName = new String[7];
            arTagSrcName[0] = "SRC";
            arTagSrcName[1] = "BACKGROUND";
            arTagSrcName[2] = "BACKGROUND";
            arTagSrcName[3] = "BACKGROUND";
            arTagSrcName[4] = "SRC";
            arTagSrcName[5] = "HREF";
            arTagSrcName[6] = "SRC";
            for (int i = 0; i < arTagName.length; i++) {
                if (arTagName[i] != null) {
                    HtmlElementFinder imgReader = new HtmlElementFinder();
                    imgReader.setHtmlSrc(strSrc);
                    HtmlElement element = imgReader.findNextElement(
                            arTagName[i], false);
                    int nCount = 0;
                    for (; element != null; element = imgReader
                            .findNextElement(arTagName[i], false)) {
                        System.out.println("[FOUND]" + element.toString());
                        String sHtml = "\n<INPUT type='hidden' value='<%=request.getParameter(\"ChnlId\")%>' name='DOCCHANNEL'>\n";
                        sHtml = sHtml
                                + "<INPUT type='hidden' value='10' name='DOCTYPE'>\n";
                        sHtml = sHtml
                                + "<INPUT type='hidden' value='<%=request.getParameter(\"Tree\")%>' name='Tree'>\n";
                        sHtml = sHtml
                                + "<INPUT type='hidden' value='10' name='FormId'>\n";
                        sHtml = "";
                        imgReader
                                .putHTML(element.toString(true, false) + sHtml);
                        nCount++;
                    }

                    strSrc = imgReader.getContent();
                    System.out.println("\u5904\u7406\u8282\u70B9["
                            + arTagName[i] + "]\u53D1\u73B0[" + nCount
                            + "]\u4E2A");
                }
            }

            System.out
                    .println("\n\n*****************  Result *********************");
            FileUtils.write(new File("d:\\result.html"), strSrc, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public HtmlElementFinder setHtmlSrc(String _src) {
        if (_src == null) {
            _src = "";
        }
        return setHtmlSrc(_src.toCharArray());
    }

    public HtmlElement findNextElement(String _sName) {
        return findNextElement(_sName, false);
    }

    public String getContent() {
        if (conBuffer == null) {
            return "";
        } else {
            return conBuffer.toString();
        }
    }

    public HtmlElement findNextElement(String string, boolean i) {
        string = "<" + string.trim().toLowerCase();
        char[] jArr = string.toCharArray();
        int k = srcBuffer.length;
        for (; nCurrPos < k; ) {
            char l = srcBuffer[nCurrPos];
            if (l == '<') {
                int m = 1;
                int o;
                HtmlElement htmlelement;
                for (; m < jArr.length && nCurrPos + m < k; ) {
                    if (Character.toLowerCase(srcBuffer[nCurrPos + m]) != jArr[m]) {
                        break;
                    }

                    m++;
                }
                if (m == jArr.length) {
                    m = m + nCurrPos;
                    if (m >= k && Character.isWhitespace(srcBuffer[m]) || srcBuffer[m] == '>') {
                        htmlelement = new HtmlElement();
                        htmlelement.ONLY_SEARCH_SELF = i;
                        try {
                            o = htmlelement.fromString(srcBuffer, nCurrPos);
                            if (o > 0) {
                                nCurrPos = o;
                                return (htmlelement);
                            }
                        } catch (Exception exception) {
                        }
                    }
                }
            }
            conBuffer.append(l);
            nCurrPos = nCurrPos + 1;
        }
        return (null);
    }

    public HtmlElementFinder putElement(HtmlElement _element) {
        if (_element != null) {
            conBuffer.append(_element.toString(true, false));
        }
        return this;
    }

    public HtmlElementFinder putHTML(String _sHTML) {
        if (_sHTML != null) {
            conBuffer.append(_sHTML);
        }
        return this;
    }

    public HtmlElementFinder setHtmlSrc(char[] _src) {
        srcBuffer = _src;
        conBuffer = new StringBuffer();
        return this;
    }

    private int nCurrPos;

    public StringBuffer conBuffer;
}
