// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2006-1-26 15:57:00
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   HtmlElement.java

package com.bixuebihui.util.html;

import com.bixuebihui.util.other.CMyException;
import com.bixuebihui.util.other.CMyString;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.bixuebihui.util.html:
//            HtmlDocument, HtmlAttribute, HtmlComment, HTML

public class HtmlElement
        implements Cloneable {

    public HtmlElement() {
        ONLY_SEARCH_SELF = false;
        parent = null;
        attributes = null;
        content = null;
    }

    public HtmlElement(String _name) {
        ONLY_SEARCH_SELF = false;
        parent = null;
        attributes = null;
        content = null;
        name = _name;
    }

    public static boolean isAloneTag(String _name) {
        if (_name == null) {
            return false;
        } else {
            return " AREA BR HR INPUT LI LINK IMG META PARAM UL ".indexOf(" " + _name.toUpperCase() + " ") >= 0;
        }
    }

    public String getName() {
        return name;
    }

    public boolean nameIs(String _name) {
        return name.compareToIgnoreCase(_name) == 0;
    }

    public HtmlElement setName(String _name) {
        name = _name;
        return this;
    }

    public HTML.Tag getTag() {
        return HTML.getTag(name);
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

    public HtmlElement setDocument(HtmlDocument _document) {
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

    public HtmlElement setParent(HtmlElement _parent) {
        parent = _parent;
        return this;
    }

    public List getAttributes() {
        return attributes;
    }

    public List getContent() {
        return content;
    }

    public HtmlAttribute getAttribute(String _name) {
        if (attributes == null) {
            return null;
        }
        for (Iterator itr = attributes.iterator(); itr.hasNext(); ) {
            HtmlAttribute att = (HtmlAttribute) itr.next();
            if (att.getName().compareToIgnoreCase(_name) == 0) {
                return att;
            }
        }

        return null;
    }

    public String getAttributeValue(String _name) {
        HtmlAttribute attribute = getAttribute(_name);
        return attribute != null ? attribute.getValue() : null;
    }

    public HtmlElement setAttributes(List _attributes) {
        if (_attributes == null) {
            attributes = null;
            return this;
        }
        if (attributes == null) {
            attributes = new ArrayList(5);
        }
        for (Iterator itr = _attributes.iterator(); itr.hasNext(); ) {
            HtmlAttribute att = (HtmlAttribute) itr.next();
            if (att != null) {
                if (att.getParent() != this) {
                    att.setParent(this);
                }
                attributes.add(att);
            }
        }

        return this;
    }

    public HtmlElement setAttribute(String _name, String _value) {
        return setAttribute(new HtmlAttribute(_name, _value));
    }

    public HtmlElement setAttribute(HtmlAttribute _attribute) {
        if (_attribute == null) {
            return this;
        }
        if (attributes == null) {
            attributes = new ArrayList(5);
            attributes.add(_attribute);
            return this;
        }
        for (Iterator itr = attributes.iterator(); itr.hasNext(); ) {
            HtmlAttribute att = (HtmlAttribute) itr.next();
            if (att.nameIs(_attribute.getName())) {
                att.setValue(_attribute.getValue());
                return this;
            }
        }

        attributes.add(_attribute);
        if (_attribute.getParent() != this) {
            _attribute.setParent(this);
        }
        return this;
    }

    public boolean removeAttribute(String _name) {
        if (attributes == null) {
            return false;
        }
        for (Iterator itr = attributes.iterator(); itr.hasNext(); ) {
            HtmlAttribute att = (HtmlAttribute) itr.next();
            if (att.nameIs(_name)) {
                itr.remove();
                att.setParent(null);
                return true;
            }
        }

        return false;
    }

    public HtmlElement addContent(String _text) {
        if (content == null) {
            content = new ArrayList(5);
        }
        _text = _text.trim();
        int size = content.size();
        if (size > 0) {
            Object obj = content.get(size - 1);
            if (obj instanceof String) {
                _text = (String) obj + _text;
                content.remove(size - 1);
            }
        }
        content.add(_text);
        return this;
    }

    public HtmlElement addContent(HtmlElement _element)
            throws CMyException {
        if (_element == null) {
            return this;
        }
        if (_element.getParent() != null) {
            throw new CMyException(1, "\u5143\u7D20\u5DF2\u5B58\u5728Parent\uFF08HtmlElement.addContent\uFF09");
        }
        if (_element == this) {
            throw new CMyException(1, "\u5143\u7D20\u4E0D\u80FD\u6DFB\u52A0\u5230\u81EA\u8EAB\uFF08HtmlElement.addContent\uFF09");
        }
        if (_element.isAncestorOf(this)) {
            throw new CMyException(1, "\u8981\u6DFB\u52A0\u7684\u5143\u7D20\u662F\u5F53\u524D\u5143\u7D20\u7684\u7956\u5148\uFF08HtmlElement.addContent\uFF09");
        }
        if (content == null) {
            content = new ArrayList(5);
        }
        _element.setParent(this);
        content.add(_element);
        return this;
    }

    public HtmlElement addContent(HtmlComment _comment)
            throws CMyException {
        if (_comment.getParent() != null || _comment.getDocument() != null) {
            throw new CMyException(1, "\u5143\u7D20\u5DF2\u5B58\u5728Parent\uFF08HtmlElement.addContent\uFF09");
        }
        if (content == null) {
            content = new ArrayList(5);
        }
        _comment.setParent(this);
        content.add(_comment);
        return this;
    }

    public boolean removeContent(HtmlComment _comment) {
        if (_comment == null || content == null) {
            return false;
        }
        if (content.remove(_comment)) {
            _comment.setParent(null);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeContent(HtmlElement _element) {
        if (_element == null || content == null) {
            return false;
        }
        if (content.remove(_element)) {
            _element.setParent(null);
            return true;
        } else {
            return false;
        }
    }

    public boolean hasChildren() {
        if (content == null || content.size() == 0) {
            return false;
        }
        for (Iterator itr = content.iterator(); itr.hasNext(); ) {
            if (itr.next() instanceof HtmlElement) {
                return true;
            }
        }

        return false;
    }

    public List getChildren() {
        return getChildren(null);
    }

    public List getChildren(String _name) {
        List children = null;
        if (content == null) {
            children = new ArrayList(1);
        } else {
            children = new ArrayList(content.size());
            for (Iterator itr = content.iterator(); itr.hasNext(); ) {
                Object obj = itr.next();
                if ((obj instanceof HtmlElement) && (_name == null || ((HtmlElement) obj).nameIs(_name))) {
                    children.add(obj);
                }
            }

        }
        return children;
    }

    public HtmlElement getChild(String _name) {
        if (content == null) {
            return null;
        }
        for (Iterator itr = content.iterator(); itr.hasNext(); ) {
            Object obj = itr.next();
            if (obj instanceof HtmlElement) {
                HtmlElement element = (HtmlElement) obj;
                if (element.nameIs(_name)) {
                    return element;
                }
            }
        }

        return null;
    }

    public String getText() {
        if (content == null || content.size() < 1) {
            return "";
        }
        if (content.size() == 1 && (content.get(0) instanceof String)) {
            return (String) content.get(0);
        }
        StringBuffer textContent = new StringBuffer();
        boolean hasText = false;
        for (Iterator itr = content.iterator(); itr.hasNext(); ) {
            Object obj = itr.next();
            if (obj instanceof String) {
                textContent.append((String) obj);
                hasText = true;
            } else if (obj instanceof HtmlElement) {
                String sText = ((HtmlElement) obj).getText();
                if (sText.length() > 0) {
                    textContent.append(sText);
                    hasText = true;
                }
            }
        }

        return hasText ? textContent.toString() : "";
    }

    public boolean isRootElement() {
        return parent instanceof HtmlDocument;
    }

    public int getLevel() {
        int nLevel = 0;
        for (Object p = parent; p != null && (p instanceof HtmlElement); p = ((HtmlElement) p).getParent()) {
            nLevel++;
        }

        return nLevel;
    }

    public boolean isAncestorOf(HtmlElement _element) {
        if (_element == null) {
            return false;
        }
        for (Object p = _element.getParent(); p != null; p = ((HtmlElement) p).getParent()) {
            if (p == this) {
                return true;
            }
        }

        return false;
    }

    public List getElementsByName(String _name) {
        List elements = null;
        if (content == null) {
            elements = new ArrayList(1);
        } else {
            elements = new ArrayList(5);
            _name = _name.trim();
            for (Iterator itr = content.iterator(); itr.hasNext(); ) {
                Object obj = itr.next();
                if (obj != null && (obj instanceof HtmlElement)) {
                    List grandChildren = ((HtmlElement) obj).getElementsByName(_name);
                    if (_name == null || ((HtmlElement) obj).nameIs(_name)) {
                        elements.add(obj);
                    }
                    for (int i = 0; i < grandChildren.size(); i++) {
                        elements.add(grandChildren.get(i));
                    }

                }
            }

        }
        return elements;
    }

    @Override
    public String toString() {
        return toString(true, false);
    }

    public String toString(boolean _bIncludingTag, boolean _bFormat) {
        StringBuffer textContent = new StringBuffer();
        String sBlanks = null;
        if (_bIncludingTag) {
            textContent.append("<").append(name);
            if (attributes != null && attributes.size() >= 1) {
                for (Iterator itr = attributes.iterator(); itr.hasNext(); ) {
                    Object obj = itr.next();
                    if (obj != null && (obj instanceof HtmlAttribute)) {
                        textContent.append(" ").append(((HtmlAttribute) obj).toString());
                    }
                }

            }
            textContent.append(">");
            HTML.Tag tag = getTag();
            if (tag == HTML.Tag.SCRIPT || tag == HTML.Tag.STYLE) {
                textContent.append("\n");
            }
        }
        if (content != null) {
            int nSize = content.size();
            if (nSize == 1 && (content.get(0) instanceof String)) {
                textContent.append((String) content.get(0));
            } else if (nSize >= 1) {
                for (Iterator itr = content.iterator(); itr.hasNext(); ) {
                    Object obj = itr.next();
                    if (obj != null) {
                        if (obj instanceof String) {
                            textContent.append((String) obj);
                        } else if (obj instanceof HtmlComment) {
                            textContent.append(((HtmlComment) obj).toString());
                        } else if (obj instanceof HtmlElement) {
                            HTML.Tag tag = ((HtmlElement) obj).getTag();
                            if (tag != null && tag.breakBeforeBegin()) {
                                textContent.append("\n");
                                if (_bFormat) {
                                    if (sBlanks == null) {
                                        sBlanks = CMyString.makeBlanks((getLevel() + 1) * 2);
                                    }
                                    textContent.append(sBlanks);
                                }
                            }
                            textContent.append(((HtmlElement) obj).toString(true, _bFormat));
                        }
                    }
                }

            }
        }
        if (_bIncludingTag && !isAloneTag(name)) {
            if (_bFormat) {
                HTML.Tag tag = getTag();
                if (tag != null && tag.breakBeforeEnd()) {
                    textContent.append("\n");
                    textContent.append(CMyString.makeBlanks(getLevel() * 2));
                }
            }
            if (!ONLY_SEARCH_SELF) {
                textContent.append("</").append(name).append(">");
            }
        }
        return textContent.toString();
    }

    public static void main(String[] args) {
        HtmlElement element = new HtmlElement();
        try {
            String strSrc = IOUtils.resourceToString("d:\\test\\sina.htm", Charset.defaultCharset());
            element.fromString("<BODY>" + strSrc + "</BODY>");
            System.out.println(element.toString(false, true));
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public int fromString(String _strSrc)
            throws CMyException {
        if (attributes != null) {
            attributes.clear();
        }
        if (content != null) {
            content.clear();
        }
        int nLen = _strSrc.length();
        if (nLen < 1) {
            return 0;
        } else {
            char[] srcBuffer = new char[nLen + 1];
            _strSrc.getChars(0, nLen, srcBuffer, 0);
            return fromString(srcBuffer, 0);
        }
    }

    public int fromString(char[] _srcBuffer, int _nStart)
            throws CMyException {
        if (_nStart < 0) {
            return -1;
        }
        int nLen = _srcBuffer.length;
        int nPos = _nStart;
        StringBuffer conBuffer = null;
        for (; nPos < nLen && _srcBuffer[nPos] != '<'; nPos++) {
            ;
        }
        nPos++;
        conBuffer = new StringBuffer();
        for (; nPos < nLen; nPos++) {
            char aChar = _srcBuffer[nPos];
            if (Character.isWhitespace(aChar) || aChar == '>') {
                break;
            }
            conBuffer.append(aChar);
        }

        name = conBuffer.toString();
        nPos = readAttributes(_srcBuffer, nPos);
        if (ONLY_SEARCH_SELF) {
            return nPos;
        }
        boolean bOver = isAloneTag(name);
        HTML.Tag tag = getTag();
        if (!bOver && tag != null && tag.isPreformatted()) {
            conBuffer = new StringBuffer();
            boolean bCanEnd = true;
            for (; nPos < nLen; nPos++) {
                char aChar = _srcBuffer[nPos];
                if (aChar == '"') {
                    bCanEnd = !bCanEnd;
                } else if (aChar == '<' && bCanEnd) {
                    int nNextPos = checkAtEndTag(_srcBuffer, nPos);
                    if (nNextPos > 0) {
                        nPos = nNextPos;
                        break;
                    }
                }
                conBuffer.append(aChar);
            }

            addContent(conBuffer.toString());
            bOver = true;
        }
        while (!bOver && nPos < nLen) {
            while (nPos < nLen && Character.isWhitespace(_srcBuffer[nPos])) {
                nPos++;
            }
            if (nPos >= nLen) {
                break;
            }
            if (_srcBuffer[nPos] != '<') {
                conBuffer = new StringBuffer();
                boolean bCanEnd = true;
                for (; nPos < nLen; nPos++) {
                    char aChar = _srcBuffer[nPos];
                    if (aChar == '"') {
                        bCanEnd = !bCanEnd;
                    } else if (aChar == '<' && bCanEnd) {
                        break;
                    }
                    conBuffer.append(aChar);
                }

                addContent(conBuffer.toString());
            } else if (_srcBuffer[nPos + 1] == '!') {
                HtmlComment comment = new HtmlComment();
                conBuffer = new StringBuffer();
                while (nPos < nLen) {
                    char aChar = _srcBuffer[nPos++];
                    conBuffer.append(aChar);
                    if (aChar == '-' && _srcBuffer[nPos] == '-' && _srcBuffer[nPos + 1] == '>') {
                        conBuffer.append("->");
                        nPos += 2;
                        break;
                    }
                }
                if (comment.fromString(conBuffer.toString())) {
                    addContent(comment);
                }
            } else if (_srcBuffer[nPos + 1] == '/') {
                bOver = true;
                for (nPos += 2; nPos < nLen && _srcBuffer[nPos++] != '>'; ) {
                    ;
                }
            } else {
                HtmlElement element = new HtmlElement();
                int nNextPos = element.fromString(_srcBuffer, nPos);
                String sNextTagName = element.getName();
                if (sNextTagName != null) {
                    addContent(element);
                }
                nPos = nNextPos;
            }
        }
        return nPos;
    }

    private int readAttributes(char[] _srcBuffer, int _nStart) {
        int nPos = _nStart;
        int nLen = _srcBuffer.length;
        StringBuffer conBuffer = null;
        char aChar = ' ';
        char chrQuote = ' ';
        boolean bCanEnd = true;
        while (nPos < nLen) {
            while (nPos < nLen && Character.isWhitespace(_srcBuffer[nPos])) {
                nPos++;
            }
            if (nPos >= nLen || _srcBuffer[nPos] == '>' && bCanEnd) {
                break;
            }
            HtmlAttribute attribute = new HtmlAttribute();
            conBuffer = new StringBuffer();
            for (; nPos < nLen; nPos++) {
                aChar = _srcBuffer[nPos];
                if ((aChar == '"' || aChar == '\'') && _srcBuffer[nPos - 1] != '\\') {
                    if (chrQuote == ' ') {
                        chrQuote = aChar;
                        bCanEnd = false;
                    } else if (aChar == chrQuote) {
                        bCanEnd = !bCanEnd;
                        chrQuote = ' ';
                    }
                }
                if (bCanEnd && (Character.isWhitespace(aChar) || aChar == '>')) {
                    break;
                }
                conBuffer.append(aChar);
            }

            if (attribute.fromString(conBuffer.toString())) {
                setAttribute(attribute);
            }
        }
        return ++nPos;
    }

    private int checkAtEndTag(char[] _srcBuffer, int _nPos) {
        char[] tagName = ("</" + name + ">").toLowerCase().toCharArray();
        int nPos = _nPos;
        for (int i = 0; i < tagName.length && nPos < _srcBuffer.length; i++) {
            if (Character.toLowerCase(_srcBuffer[nPos++]) != tagName[i]) {
                return -1;
            }
        }

        return nPos;
    }

    protected static final boolean ISDEBUG = false;
    protected static final int FORMAT_BLANKS = 2;
    protected static final int INITIAL_ARRAY_SIZE = 5;
    protected static final String TAG_ALONE = " AREA BR HR INPUT LI LINK IMG META PARAM UL ";
    protected static final String TAG_SHORT = " BR LI ";
    public boolean ONLY_SEARCH_SELF;
    public static final int CON_COMMENT = 1;
    public static final int CON_ELEMENT = 2;
    public static final int CON_TEXT = 3;
    protected String name;
    protected Object parent;
    protected List attributes;
    protected List content;
}
