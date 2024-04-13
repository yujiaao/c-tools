// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2006-1-26 15:57:00
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   HtmlComment.java

package com.bixuebihui.util.html;


// Referenced classes of package com.bixuebihui.util.html:
//            HtmlDocument, HtmlElement

public class HtmlComment
        implements Cloneable {

    protected HtmlComment() {
        parent = null;
    }

    public HtmlComment(String _text) {
        parent = null;
        setText(_text);
    }

    public HtmlDocument getDocument() {
        if (parent == null) {
            return null;
        }
        if (parent instanceof HtmlDocument) {
            return (HtmlDocument) parent;
        }
        if (parent instanceof HtmlElement) {
            return ((HtmlElement) parent).getDocument();
        } else {
            return null;
        }
    }

    public HtmlComment setDocument(HtmlDocument _document) {
        parent = _document;
        return this;
    }

    public HtmlElement getParent() {
        if (parent instanceof HtmlElement) {
            return (HtmlElement) parent;
        } else {
            return null;
        }
    }

    public HtmlComment setParent(HtmlElement _parent) {
        parent = _parent;
        return this;
    }

    public String getText() {
        return text;
    }

    public HtmlComment setText(String _text) {
        text = _text;
        return this;
    }

    public boolean isRootComment() {
        return parent instanceof HtmlDocument;
    }

    @Override
    public Object clone() {
        HtmlComment comment = new HtmlComment(text);
        comment.setParent(null);
        return comment;
    }

    @Override
    public String toString() {
        return "<!--" + text + "-->";
    }

    public boolean fromString(String _strSrc) {
        _strSrc = _strSrc.trim();
        if (!_strSrc.startsWith("<!--") || !_strSrc.endsWith("-->")) {
            return false;
        } else {
            text = _strSrc.substring(4, _strSrc.length() - 3);
            return true;
        }
    }

    public static void main(String[] args) {
        String strSrc = " <!-- this is a comment -->  ";
        HtmlComment comment = new HtmlComment();
        comment.fromString(strSrc);
        System.out.println(comment.toString());
    }

    public static final String TAG_START = "<!--";
    public static final String TAG_END = "-->";
    protected String text;
    protected Object parent;
}
