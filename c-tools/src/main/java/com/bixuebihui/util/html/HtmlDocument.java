package com.bixuebihui.util.html;

import com.bixuebihui.util.other.CMyException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class HtmlDocument {

    public HtmlDocument() {
        content = new ArrayList(INITIAL_ARRAY_SIZE);
    }

    public HtmlDocument(HtmlElement rootElement) {
        content = new ArrayList(INITIAL_ARRAY_SIZE);
        setRootElement(rootElement);
    }

    public List getContent() {
        return content;
    }

    public HtmlDocument setContent(List newContent)
            throws CMyException {
        if (newContent == null) {
            return this;
        }
        boolean bDidRoot = false;
        Iterator itr = newContent.iterator();
        content = new ArrayList(INITIAL_ARRAY_SIZE);
        while (itr.hasNext()) {
            Object obj = itr.next();
            if (obj instanceof HtmlElement) {
                if (!bDidRoot) {
                    setRootElement((HtmlElement) obj);
                    bDidRoot = true;
                } else {
                    throw new CMyException(1, "文当中只能存在一个根元素(HtmlDocument.setContent)");
                }
            } else if (obj instanceof HtmlComment) {
                addContent((HtmlComment) obj);
            } else {
                throw new CMyException(1, "无效的文档内容(HtmlDocument.setContent)");
            }
        }
        if (!bDidRoot) {
            throw new CMyException(1, "文档缺少根元素（HtmlDocument.setContent）");
        } else {
            return this;
        }
    }

    public HtmlElement getRootElement() {
        for (Iterator itr = content.iterator(); itr.hasNext(); ) {
            Object obj = itr.next();
            if (obj instanceof HtmlElement) {
                return (HtmlElement) obj;
            }
        }

        return null;
    }

    public HtmlDocument setRootElement(HtmlElement _rootElement) {
        if (_rootElement == null) {
            return this;
        }
        boolean bHadRoot = false;
        ListIterator itr;
        for (itr = content.listIterator(); itr.hasNext(); ) {
            Object obj = itr.next();
            if (obj instanceof HtmlElement) {
                HtmlElement departingRoot = (HtmlElement) obj;
                departingRoot.setDocument(null);
                itr.set(_rootElement);
                bHadRoot = true;
                break;
            }
        }

        if (!bHadRoot) {
            itr.add(_rootElement);
        }
        _rootElement.setDocument(null);
        return this;
    }

    public HtmlDocument addContent(HtmlComment comment)
            throws CMyException {
        if (comment == null) {
            return this;
        }
        if (comment.getParent() != null || comment.getDocument() != null) {
            throw new CMyException(1, "添加的内容已经存在Parent（HtmlDocument.addContent）");
        } else {
            content.add(comment);
            return this;
        }
    }

    public List getElementsByName(String _name) {
        HtmlElement root = getRootElement();
        if (root == null) {
            return new ArrayList(1);
        } else {
            return root.getElementsByName(_name);
        }
    }

    @Override
    public String toString() {
        if (content == null || content.size() < 1) {
            return "";
        }
        StringBuilder textContent = new StringBuilder();
        for (Iterator itr = content.iterator(); itr.hasNext(); textContent.append("\n")) {
            Object obj = itr.next();
            if (obj instanceof HtmlComment) {
                textContent.append(obj);
            } else if (obj instanceof HtmlElement) {
                textContent.append(obj);
            }
        }

        return textContent.toString();
    }

    public boolean fromString(String _strSrc)
            throws CMyException {
        if (content != null) {
            content.clear();
        }
        _strSrc = _strSrc.trim();
        if (_strSrc.length() < 1) {
            return false;
        }
        HtmlElement docElement = new HtmlElement();
        if (docElement.fromString("<_DOM>" + _strSrc + "</_DOM>") < 1) {
            return false;
        }
        List lstContent = docElement.getContent();
        if (lstContent == null) {
            return false;
        }
        for (Iterator itr = lstContent.iterator(); itr.hasNext(); ) {
            Object obj = itr.next();
            if (obj != null) {
                if (obj instanceof HtmlComment) {
                    ((HtmlComment) obj).setParent(null);
                    addContent((HtmlComment) obj);
                } else if (obj instanceof HtmlElement) {
                    ((HtmlElement) obj).setParent(null);
                    setRootElement((HtmlElement) obj);
                }
            }
        }

        return true;
    }

    public boolean loadFromFile(String _sFilePathName)
            throws CMyException, IOException {
        String strDoc = IOUtils.resourceToString(_sFilePathName, Charset.defaultCharset());
        return fromString(strDoc);
    }

    public void saveToFile(String _sFilePathName)
            throws IOException {
        String strDoc = toString();
        FileUtils.write(new File(_sFilePathName), strDoc, Charset.defaultCharset());
    }

    public static void main(String[] args) {
        HtmlDocument htmlDoc = new HtmlDocument();
        HtmlElement element;
        List elements;
        try {
            String sFile = "d:\\temp\\test1.htm";
            if (!htmlDoc.loadFromFile(sFile)) {
                System.out.println("Failed to analyze Html Document.");
            } else {
                System.out.println("OK to analyze Html Document.");
                System.out.println(htmlDoc.toString());
                System.out.println("\nTest for getElementsByName...");
                elements = htmlDoc.getElementsByName("FORM");
                for (int i = 0; i < elements.size(); i++) {
                    element = (HtmlElement) elements.get(i);
                    System.out.println("\n" + element.toString());
                }

                elements.clear();
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    private static final int INITIAL_ARRAY_SIZE = 5;
    protected List content;
}
