// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2006-1-26 15:57:00
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   HtmlAttribute.java

package com.bixuebihui.util.html;

import com.bixuebihui.util.other.CMyException;

// Referenced classes of package com.bixuebihui.util.html:
//            HtmlElement, HtmlDocument

public class HtmlAttribute
        implements Cloneable {

    protected HtmlAttribute() {
        parent = null;
    }

    public HtmlAttribute(String _name, String _value) {
        parent = null;
        setName(_name);
        setValue(_value);
    }

    public HtmlAttribute(String _strSrc) {
        parent = null;
        fromString(_strSrc);
    }

    public HtmlElement getParent() {
        return parent;
    }

    public HtmlAttribute setParent(HtmlElement _parent) {
        parent = _parent;
        return this;
    }

    public String getName() {
        return name;
    }

    public HtmlAttribute setName(String _name) {
        name = _name.trim();
        return this;
    }

    public boolean nameIs(String _name) {
        return name.compareToIgnoreCase(_name) == 0;
    }

    public String getValue() {
        return value;
    }

    public HtmlAttribute setValue(String _value) {
        value = _value;
        return this;
    }

    public int getIntValue()
            throws CMyException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {

            throw new CMyException(2, "\u6570\u636E\u8F6C\u6362\u9519\u8BEF\uFF1A\u65E0\u6548\u7684int\u503C\uFF08HtmlAttribute.getIntValue\uFF09");
        }
    }

    public long getLongValue()
            throws CMyException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new CMyException(2, "\u6570\u636E\u8F6C\u6362\u9519\u8BEF\uFF1A\u65E0\u6548\u7684long\u503C\uFF08HtmlAttribute.getLongValue\uFF09");
        }
    }

    public float getFloatValue()
            throws CMyException {
        try {
            return Float.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new CMyException(2, "\u6570\u636E\u8F6C\u6362\u9519\u8BEF\uFF1A\u65E0\u6548\u7684float\u503C\uFF08HtmlAttribute.getFloatValue\uFF09");
        }

    }

    public double getDoubleValue()
            throws CMyException {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new CMyException(2, "\u6570\u636E\u8F6C\u6362\u9519\u8BEF\uFF1A\u65E0\u6548\u7684double\u503C\uFF08HtmlAttribute.getDoubleValue\uFF09");
        }

    }

    public boolean getBooleanValue()
            throws CMyException {
        if ("true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value) || "off".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value)) {
            return false;
        } else {
            throw new CMyException(2, "\u6570\u636E\u8F6C\u6362\u9519\u8BEF\uFF1A\u65E0\u6548\u7684boolean\u503C\uFF08HtmlAttribute.getBooleanValue\uFF09");
        }
    }

    public HtmlDocument getDocument() {
        if (parent != null) {
            return parent.getDocument();
        } else {
            return null;
        }
    }

    @Override
    public Object clone() {
        HtmlAttribute attribute = new HtmlAttribute(name, value);
        attribute.setParent(null);
        return attribute;
    }

    @Override
    public final String toString() {
        char chrQuote = '"';
        if (value != null && value.indexOf('"') >= 0) {
            chrQuote = '\'';
        }
        if (value != null && value.indexOf('\'') < 0) {
            chrQuote = '\'';
        }
        return name + '=' + chrQuote + value + chrQuote;
    }

    public boolean fromString(String _strSrc) {
        _strSrc = _strSrc.trim();
        int nPos = _strSrc.indexOf('=');
        if (nPos < 0) {
            return false;
        }
        name = _strSrc.substring(0, nPos).trim();
        int nEnd = _strSrc.length();
        boolean bDelHead = false;
        char chrLast = _strSrc.charAt(nEnd - 1);
        if (chrLast == '"' || chrLast == '\'') {
            nEnd--;
            bDelHead = true;
        }
        value = _strSrc.substring(nPos + 1, nEnd).trim();
        if (bDelHead && value.length() > 0 && value.charAt(0) == chrLast) {
            value = value.substring(1);
        }
        return true;
    }

    public static void main(String[] args) {
        String strSrc = "  a=\"12\" ";
        HtmlAttribute attribute = new HtmlAttribute(strSrc);
        System.out.println(attribute.toString());
    }

    protected HtmlElement parent;
    protected String name;
    protected String value;
}
