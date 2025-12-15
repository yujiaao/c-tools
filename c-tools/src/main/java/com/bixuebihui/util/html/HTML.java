
package com.bixuebihui.util.html;

import com.bixuebihui.util.other.CMyBitsValue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

public class HTML {
    public static final String NULL_ATTRIBUTE_VALUE = "#DEFAULT";
    private static final Hashtable tagHashtable;
    private static final Hashtable attHashtable;

    static {
        tagHashtable = new Hashtable(73);
        for (int i = 0; i < Tag.allTags.length; i++) {
            tagHashtable.put(Tag.allTags[i].toString(), Tag.allTags[i]);
        }

        attHashtable = new Hashtable(77);
        for (int i = 0; i < Attribute.allAttributes.length; i++) {
            attHashtable.put(Attribute.allAttributes[i].toString(), Attribute.allAttributes[i]);
        }

    }

    public static Attribute getAttributeKey(String attName) {
        Object a = attHashtable.get(attName);
        if (a == null) {
            return null;
        } else {
            return (Attribute) a;
        }
    }

    public static void main(String[] args) {
        Tag tag = getTag("TR");
        if (tag == null) {
            System.out.println("Tag not found!");
        } else {
            System.out.println(tag.isBlock());
        }
    }


    public HTML() {
    }

    public static Tag[] getAllTags() {
        Tag[] tags = new Tag[Tag.allTags.length];
        System.arraycopy(Tag.allTags, 0, tags, 0, Tag.allTags.length);
        return tags;
    }

    public static Tag getTag(String tagName) {
        if (tagName == null) {
            return null;
        } else {
            tagName = tagName.trim().toLowerCase();
            Object t = tagHashtable.get(tagName);
            return t != null ? (Tag) t : null;
        }
    }

    public static Attribute[] getAllAttributeKeys() {
        Attribute[] attributes = new Attribute[Attribute.allAttributes.length];
        System.arraycopy(Attribute.allAttributes, 0, attributes, 0, Attribute.allAttributes.length);
        return attributes;
    }

    public static final class Attribute {

        @Override
        public String toString() {
            return name;
        }

        private String name;
        public static final Attribute SIZE;
        public static final Attribute COLOR;
        public static final Attribute CLEAR;
        public static final Attribute BACKGROUND;
        public static final Attribute BGCOLOR;
        public static final Attribute TEXT;
        public static final Attribute LINK;
        public static final Attribute VLINK;
        public static final Attribute ALINK;
        public static final Attribute WIDTH;
        public static final Attribute HEIGHT;
        public static final Attribute ALIGN;
        public static final Attribute NAME;
        public static final Attribute HREF;
        public static final Attribute REL;
        public static final Attribute REV;
        public static final Attribute TITLE;
        public static final Attribute TARGET;
        public static final Attribute SHAPE;
        public static final Attribute COORDS;
        public static final Attribute ISMAP;
        public static final Attribute NOHREF;
        public static final Attribute ALT;
        public static final Attribute ID;
        public static final Attribute SRC;
        public static final Attribute HSPACE;
        public static final Attribute VSPACE;
        public static final Attribute USEMAP;
        public static final Attribute LOWSRC;
        public static final Attribute CODEBASE;
        public static final Attribute CODE;
        public static final Attribute ARCHIVE;
        public static final Attribute VALUE;
        public static final Attribute VALUETYPE;
        public static final Attribute TYPE;
        public static final Attribute CLASS;
        public static final Attribute STYLE;
        public static final Attribute LANG;
        public static final Attribute FACE;
        public static final Attribute DIR;
        public static final Attribute DECLARE;
        public static final Attribute CLASSID;
        public static final Attribute DATA;
        public static final Attribute CODETYPE;
        public static final Attribute STANDBY;
        public static final Attribute BORDER;
        public static final Attribute SHAPES;
        public static final Attribute NOSHADE;
        public static final Attribute COMPACT;
        public static final Attribute START;
        public static final Attribute ACTION;
        public static final Attribute METHOD;
        public static final Attribute ENCTYPE;
        public static final Attribute CHECKED;
        public static final Attribute MAXLENGTH;
        public static final Attribute MULTIPLE;
        public static final Attribute SELECTED;
        public static final Attribute ROWS;
        public static final Attribute COLS;
        public static final Attribute DUMMY;
        public static final Attribute CELLSPACING;
        public static final Attribute CELLPADDING;
        public static final Attribute VALIGN;
        public static final Attribute HALIGN;
        public static final Attribute NOWRAP;
        public static final Attribute ROWSPAN;
        public static final Attribute COLSPAN;
        public static final Attribute PROMPT;
        public static final Attribute HTTPEQUIV;
        public static final Attribute CONTENT;
        public static final Attribute LANGUAGE;
        public static final Attribute VERSION;
        public static final Attribute N;
        public static final Attribute FRAMEBORDER;
        public static final Attribute MARGINWIDTH;
        public static final Attribute MARGINHEIGHT;
        public static final Attribute SCROLLING;
        public static final Attribute NORESIZE;
        public static final Attribute ENDTAG;
        public static final Attribute COMMENT;
        static final Attribute MEDIA;
        static final Attribute[] allAttributes;

        static {
            SIZE = new Attribute("size");
            COLOR = new Attribute("color");
            CLEAR = new Attribute("clear");
            BACKGROUND = new Attribute("background");
            BGCOLOR = new Attribute("bgcolor");
            TEXT = new Attribute("text");
            LINK = new Attribute("link");
            VLINK = new Attribute("vlink");
            ALINK = new Attribute("alink");
            WIDTH = new Attribute("width");
            HEIGHT = new Attribute("height");
            ALIGN = new Attribute("align");
            NAME = new Attribute("name");
            HREF = new Attribute("href");
            REL = new Attribute("rel");
            REV = new Attribute("rev");
            TITLE = new Attribute("title");
            TARGET = new Attribute("target");
            SHAPE = new Attribute("shape");
            COORDS = new Attribute("coords");
            ISMAP = new Attribute("ismap");
            NOHREF = new Attribute("nohref");
            ALT = new Attribute("alt");
            ID = new Attribute("id");
            SRC = new Attribute("src");
            HSPACE = new Attribute("hspace");
            VSPACE = new Attribute("vspace");
            USEMAP = new Attribute("usemap");
            LOWSRC = new Attribute("lowsrc");
            CODEBASE = new Attribute("codebase");
            CODE = new Attribute("code");
            ARCHIVE = new Attribute("archive");
            VALUE = new Attribute("value");
            VALUETYPE = new Attribute("valuetype");
            TYPE = new Attribute("type");
            CLASS = new Attribute("class");
            STYLE = new Attribute("style");
            LANG = new Attribute("lang");
            FACE = new Attribute("face");
            DIR = new Attribute("dir");
            DECLARE = new Attribute("declare");
            CLASSID = new Attribute("classid");
            DATA = new Attribute("data");
            CODETYPE = new Attribute("codetype");
            STANDBY = new Attribute("standby");
            BORDER = new Attribute("border");
            SHAPES = new Attribute("shapes");
            NOSHADE = new Attribute("noshade");
            COMPACT = new Attribute("compact");
            START = new Attribute("start");
            ACTION = new Attribute("action");
            METHOD = new Attribute("method");
            ENCTYPE = new Attribute("enctype");
            CHECKED = new Attribute("checked");
            MAXLENGTH = new Attribute("maxlength");
            MULTIPLE = new Attribute("multiple");
            SELECTED = new Attribute("selected");
            ROWS = new Attribute("rows");
            COLS = new Attribute("cols");
            DUMMY = new Attribute("dummy");
            CELLSPACING = new Attribute("cellspacing");
            CELLPADDING = new Attribute("cellpadding");
            VALIGN = new Attribute("valign");
            HALIGN = new Attribute("halign");
            NOWRAP = new Attribute("nowrap");
            ROWSPAN = new Attribute("rowspan");
            COLSPAN = new Attribute("colspan");
            PROMPT = new Attribute("prompt");
            HTTPEQUIV = new Attribute("http-equiv");
            CONTENT = new Attribute("content");
            LANGUAGE = new Attribute("language");
            VERSION = new Attribute("version");
            N = new Attribute("n");
            FRAMEBORDER = new Attribute("frameborder");
            MARGINWIDTH = new Attribute("marginwidth");
            MARGINHEIGHT = new Attribute("marginheight");
            SCROLLING = new Attribute("scrolling");
            NORESIZE = new Attribute("noresize");
            ENDTAG = new Attribute("endtag");
            COMMENT = new Attribute("comment");
            MEDIA = new Attribute("media");
            allAttributes = (new Attribute[]{
                    FACE, COMMENT, SIZE, COLOR, CLEAR, BACKGROUND, BGCOLOR, TEXT, LINK, VLINK,
                    ALINK, WIDTH, HEIGHT, ALIGN, NAME, HREF, REL, REV, TITLE, TARGET,
                    SHAPE, COORDS, ISMAP, NOHREF, ALT, ID, SRC, HSPACE, VSPACE, USEMAP,
                    LOWSRC, CODEBASE, CODE, ARCHIVE, VALUE, VALUETYPE, TYPE, CLASS, STYLE, LANG,
                    DIR, DECLARE, CLASSID, DATA, CODETYPE, STANDBY, BORDER, SHAPES, NOSHADE, COMPACT,
                    START, ACTION, METHOD, ENCTYPE, CHECKED, MAXLENGTH, MULTIPLE, SELECTED, ROWS, COLS,
                    DUMMY, CELLSPACING, CELLPADDING, VALIGN, HALIGN, NOWRAP, ROWSPAN, COLSPAN, PROMPT, HTTPEQUIV,
                    CONTENT, LANGUAGE, VERSION, N, FRAMEBORDER, MARGINWIDTH, MARGINHEIGHT, SCROLLING, NORESIZE, MEDIA,
                    ENDTAG
            });
        }

        Attribute(String id) {
            name = id;
        }
    }

    public static class UnknownTag extends Tag
            implements Serializable {

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof UnknownTag) {
                return toString().equals(obj.toString());
            } else {
                return false;
            }
        }

        private void writeObject(ObjectOutputStream s)
                throws IOException {
            s.defaultWriteObject();
            s.writeBoolean(blockTag);
            s.writeBoolean(breakTag);
            s.writeBoolean(unknown);
            s.writeObject(name);
        }

        private void readObject(ObjectInputStream s)
                throws ClassNotFoundException, IOException {
            s.defaultReadObject();
            blockTag = s.readBoolean();
            breakTag = s.readBoolean();
            unknown = s.readBoolean();
            name = (String) s.readObject();
        }

        public UnknownTag(String id) {
            super(id);
        }
    }



    public static class Tag {

        public boolean isBlock() {
            return blockTag;
        }

        public boolean breaksFlow() {
            return breakTag;
        }

        public boolean isPreformatted() {
            return this == PRE || this == TEXTAREA || this == SCRIPT;
        }

        public boolean breakBeforeBegin() {
            return CMyBitsValue.getBit(formatFlag, 0);
        }

        public boolean breakBeforeEnd() {
            return CMyBitsValue.getBit(formatFlag, 1);
        }

        @Override
        public String toString() {
            return name;
        }

        boolean blockTag;
        boolean breakTag;
        String name;
        boolean unknown;
        int formatFlag;
        public static final Tag A;
        public static final Tag ADDRESS;
        public static final Tag APPLET;
        public static final Tag AREA;
        public static final Tag B;
        public static final Tag BASE;
        public static final Tag BASEFONT;
        public static final Tag BIG;
        public static final Tag BLOCKQUOTE;
        public static final Tag BODY;
        public static final Tag BR;
        public static final Tag CAPTION;
        public static final Tag CENTER;
        public static final Tag CITE;
        public static final Tag CODE;
        public static final Tag DD;
        public static final Tag DFN;
        public static final Tag DIR;
        public static final Tag DIV;
        public static final Tag DL;
        public static final Tag DT;
        public static final Tag EM;
        public static final Tag FONT;
        public static final Tag FORM;
        public static final Tag FRAME;
        public static final Tag FRAMESET;
        public static final Tag H1;
        public static final Tag H2;
        public static final Tag H3;
        public static final Tag H4;
        public static final Tag H5;
        public static final Tag H6;
        public static final Tag HEAD;
        public static final Tag HR;
        public static final Tag HTML;
        public static final Tag I;
        public static final Tag IMG;
        public static final Tag INPUT;
        public static final Tag ISINDEX;
        public static final Tag KBD;
        public static final Tag LI;
        public static final Tag LINK;
        public static final Tag MAP;
        public static final Tag MENU;
        public static final Tag META;
        static final Tag NOBR;
        public static final Tag NOFRAMES;
        public static final Tag OBJECT;
        public static final Tag OL;
        public static final Tag OPTION;
        public static final Tag P;
        public static final Tag PARAM;
        public static final Tag PRE;
        public static final Tag SAMP;
        public static final Tag SCRIPT;
        public static final Tag SELECT;
        public static final Tag SMALL;
        public static final Tag SPAN;
        public static final Tag STRIKE;
        public static final Tag S;
        public static final Tag STRONG;
        public static final Tag STYLE;
        public static final Tag SUB;
        public static final Tag SUP;
        public static final Tag TABLE;
        public static final Tag TBODY;
        public static final Tag TR;
        public static final Tag TD;
        public static final Tag TEXTAREA;
        public static final Tag TH;
        public static final Tag TITLE;
        public static final Tag TT;
        public static final Tag U;
        public static final Tag UL;
        public static final Tag VAR;
        public static final Tag IMPLIED = new Tag("p-implied");
        public static final Tag CONTENT = new Tag("content");
        public static final Tag COMMENT = new Tag("comment");
        static final Tag[] allTags;

        static {
            A = new Tag("a");
            ADDRESS = new Tag("address");
            APPLET = new Tag("applet", 3);
            AREA = new Tag("area");
            B = new Tag("b");
            BASE = new Tag("base");
            BASEFONT = new Tag("basefont");
            BIG = new Tag("big");
            BLOCKQUOTE = new Tag("blockquote", true, true, 1);
            BODY = new Tag("body", true, true, 3);
            BR = new Tag("br", true, false, 0);
            CAPTION = new Tag("caption");
            CENTER = new Tag("center", true, false, 3);
            CITE = new Tag("cite");
            CODE = new Tag("code");
            DD = new Tag("dd", true, true, 1);
            DFN = new Tag("dfn");
            DIR = new Tag("dir", true, true, 1);
            DIV = new Tag("div", true, true, 3);
            DL = new Tag("dl", true, true, 1);
            DT = new Tag("dt", true, true, 1);
            EM = new Tag("em");
            FONT = new Tag("font");
            FORM = new Tag("form", true, false, 3);
            FRAME = new Tag("frame", 3);
            FRAMESET = new Tag("frameset", 3);
            H1 = new Tag("h1", true, true, 1);
            H2 = new Tag("h2", true, true, 1);
            H3 = new Tag("h3", true, true, 1);
            H4 = new Tag("h4", true, true, 1);
            H5 = new Tag("h5", true, true, 1);
            H6 = new Tag("h6", true, true, 1);
            HEAD = new Tag("head", true, true, 3);
            HR = new Tag("hr", true, false, 1);
            HTML = new Tag("html", true, false, 3);
            I = new Tag("i");
            IMG = new Tag("img");
            INPUT = new Tag("input");
            ISINDEX = new Tag("isindex", true, false, 1);
            KBD = new Tag("kbd");
            LI = new Tag("li", true, true, 1);
            LINK = new Tag("link");
            MAP = new Tag("map");
            MENU = new Tag("menu", true, true, 1);
            META = new Tag("meta", 1);
            NOBR = new Tag("nobr");
            NOFRAMES = new Tag("noframes", true, true, 1);
            OBJECT = new Tag("object", 3);
            OL = new Tag("ol", true, true, 1);
            OPTION = new Tag("option");
            P = new Tag("p", true, true, 1);
            PARAM = new Tag("param", 1);
            PRE = new Tag("pre", true, true, 0);
            SAMP = new Tag("samp");
            SCRIPT = new Tag("script", 3);
            SELECT = new Tag("select");
            SMALL = new Tag("small");
            SPAN = new Tag("span");
            STRIKE = new Tag("strike");
            S = new Tag("s");
            STRONG = new Tag("strong");
            STYLE = new Tag("style", 3);
            SUB = new Tag("sub");
            SUP = new Tag("sup");
            TABLE = new Tag("table", false, true, 3);
            TBODY = new Tag("tbody", false, true, 3);
            TR = new Tag("tr", false, true, 3);
            TD = new Tag("td", true, true, 1);
            TEXTAREA = new Tag("textarea");
            TH = new Tag("th", true, true, 1);
            TITLE = new Tag("title", true, true, 1);
            TT = new Tag("tt");
            U = new Tag("u");
            UL = new Tag("ul", true, true, 1);
            VAR = new Tag("var");
            allTags = (new Tag[]{
                    A, ADDRESS, APPLET, AREA, B, BASE, BASEFONT, BIG, BLOCKQUOTE, BODY,
                    BR, CAPTION, CENTER, CITE, CODE, DD, DFN, DIR, DIV, DL,
                    DT, EM, FONT, FORM, FRAME, FRAMESET, H1, H2, H3, H4,
                    H5, H6, HEAD, HR, HTML, I, IMG, INPUT, ISINDEX, KBD,
                    LI, LINK, MAP, MENU, META, NOBR, NOFRAMES, OBJECT, OL, OPTION,
                    P, PARAM, PRE, SAMP, SCRIPT, SELECT, SMALL, SPAN, STRIKE, S,
                    STRONG, STYLE, SUB, SUP, TABLE, TD, TEXTAREA, TH, TITLE, TR,
                    TT, U, UL, VAR, TBODY
            });
            getTag("html");
        }

        public Tag() {
        }

        protected Tag(String id) {
            this(id, 0);
        }

        protected Tag(String id, int _formatFlag) {
            this(id, false, false, _formatFlag);
        }

        protected Tag(String id, boolean causesBreak, boolean isBlock, int _formatFlag) {
            name = id;
            breakTag = causesBreak;
            blockTag = isBlock;
            formatFlag = _formatFlag;
        }
    }
}
