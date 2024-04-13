package com.bixuebihui.util;

import com.bixuebihui.util.other.CMyString;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class Util {

    public static String replaceChar(String pStr, char pC, String rep)
            throws Exception {
        java.text.StringCharacterIterator sciter = new java.text.StringCharacterIterator(
                pStr);
        String rt = "";
        for (char c = sciter.first(); c != java.text.StringCharacterIterator.DONE; c = sciter
                .next()) {
            if (c == pC) {
                rt += rep;
            } else {
                rt += c;
            }
        }
        return rt;
    }

    /**
     * @param out 输出通道
     * @param msg 信息,不能有双引号
     */
    public static void showHtmlAlertHistory(Writer out, String msg) throws IOException {
        msg = CMyString.filterForJs(msg);
        writeHead(out);
        writeAlert(out, msg);
        out.write("history.back();\n");
        writeTail(out);
    }

    private static void writeTail(Writer out) throws IOException {
        out.write("\n//-->");
        out.write("</script>");
        out.write("</body>");
        out.write("</html>");
    }

    /**
     * @param out 输出通道
     * @param msg 信息,不能有双引号
     * @throws java.lang.Exception
     */
    public static void showHtmlAlert(Writer out, String msg) throws Exception {
        msg = CMyString.filterForJs(msg);
        writeHead(out);
        writeAlert(out, msg);
        out.write("if(window.opener) window.opener.location.reload();");
        out.write("window.close();");
        writeTail(out);
    }

    /**
     * 只弹出提示信息;
     *
     * @param out 输出通道
     * @param msg 信息,不能有双引号
     */
    public static void showHtmlAlert_noReturn(Writer out, String msg) throws IOException {
        msg = CMyString.filterForJs(msg);
        writeHead(out);
        writeAlert(out, msg);
        writeTail(out);
    }

    private static void writeAlert(Writer out, String msg) throws IOException {
        out.write("alert(\"" + msg + "\");\n");
    }

    /**
     * @param out 输出通道
     * @param msg 信息,不能有双引号
     */
    public static void showHtmlAlertClose(Writer out, String msg) throws IOException {
        msg = CMyString.filterForJs(msg);
        writeHead(out);
        writeAlert(out, msg);
        out.write("window.self.close();");
        writeTail(out);
    }

    /**
     * @param out 输出通道
     * @param msg 信息,不能有双引号
     * @param doAfterStr
     */
    public static void showHtmlAlert3(Writer out, String msg, String doAfterStr) throws IOException {
        msg = CMyString.filterForJs(msg);
        writeHead(out);
        writeAlert(out, msg);
        out.write("if(window.opener) window.opener.close();");
        writeJump(out, doAfterStr);
        writeTail(out);
    }

    private static void writeJump(Writer out, String doAfterStr) throws IOException {
        out.write("window.location=\"" + doAfterStr + "\"");
    }

    /**
     * @param out 输出通道
     * @param msg 信息,不能有双引号
     * @param doAfterStr
     */
    public static void showHtmlAlert1(Writer out, String msg, String doAfterStr) throws IOException {
        msg = CMyString.filterForJs(msg);
        writeHead(out);
        writeAlert(out, msg);
        if (doAfterStr != null && !"".equals(doAfterStr)) {
            writeJump(out, doAfterStr);
        }
        writeTail(out);

    }

    /**
     * 没有提示，直接链到指定页面;
     * @param out 输出通道
     * @param doAfterStr
     */
    public static void showHtmlAlert1(Writer out, String doAfterStr) throws IOException {
        writeHead(out);
        writeJump(out, doAfterStr);
        writeTail(out);

    }

    /**
     * 用在有frame的页面上，是给出提示后，并整体frame页面刷新;
     *
     */
    public static void showHtmlAlertParent(Writer out, String msg,
                                           String doAfterStr) throws IOException {
        msg = CMyString.filterForJs(msg);
        writeHead(out);
        writeAlert(out, msg);
        writeParentJump(out, doAfterStr);
        writeTail(out);
    }

    private static void writeParentJump(Writer out, String doAfterStr) throws IOException {
        out.write("window.parent.parent.location=\"" + doAfterStr + "\"");
    }

    public static void goUrlParent(Writer out, String url) throws IOException {
        writeHead(out);
        writeParentJump(out, url);
        writeTail(out);
    }

    /**
     * 用在有frame的页面上，是整体frame页面刷新;
     */
    public static void showHtmlAlertParent(Writer out, String doAfterStr) throws IOException {
        writeHead(out);
        writeParentJump(out, doAfterStr);
        writeTail(out);
    }

    /**
     * @throws IOException
     */
    public static void showHtmlAlert1(Writer out, String msg,
                                      String doAfterStr, String widthHeight) throws IOException {
        msg = CMyString.filterForJs(msg);
        writeHead(out);
        writeAlert(out, msg);
        out.write("window.open('" + doAfterStr + "','login','" + widthHeight
                + "');");
        writeTail(out);
    }

    /**
     * Refrsh the window of the opener
     */
    public static void showHtmlAlertOpener(Writer out, String msg,
                                           String doAfterStr) throws IOException {
        msg = CMyString.filterForJs(msg);
        writeHead(out);
        writeAlert(out, msg);
        out.write("window.opener.location=\"" + doAfterStr + "\"");
        writeTail(out);
    }

    /**
     * 在弹出的页面上处理，处理后，弹出alert对话框，处理后自动关闭弹出页面,刷新原页面;
     */
    public static void showAndcloseHtmlAlertOpener(Writer out, String msg, String doAfterStr) throws IOException {
        msg = CMyString.filterForJs(msg);
        writeHead(out);
        writeAlert(out, msg);
        out.write("window.parent.opener.location =\"" + doAfterStr + "\"");
        writeTail(out);
    }

    /**
     * 在弹出的页面上处理，处理后，弹出alert对话框，处理后自动关闭弹出页面;
     */
    public static void showandCloseHtmlAlertOpener(Writer out, String msg)
            throws IOException {
        msg = CMyString.filterForJs(msg);
        writeHead(out);
        writeAlert(out, msg);
        out.write("window.parent.window.close();");
        writeTail(out);
    }

    /**
     * 在弹出的页面上处理，不需要alert对话框，处理后自动关闭弹出页面,刷新原页面;
     */
    public static void showAndcloseHtmlAlertOpener(Writer out, String doAfterStr)
            throws IOException {
        writeHead(out);
        out.write("window.parent.window.close();");
        out.write("window.parent.opener.location =\"" + doAfterStr + "\"");
        writeTail(out);
    }

    private static void writeHead(Writer out) throws IOException {
        out.write("<html>");
        out.write("<head><title>系统信息提示：：：：：：：：：：：：：：：：：：</title>");
        out.write("<base target='self'>");
        out.write("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />");
        out.write("</head>");
        out.write("<body>");
        out.write("<script language='javascript'>");
        out.write("<!--\n");
    }

    private static String disableHtmlContent(String contentStr) {
            return convertHtmlChar(contentStr);
    }

    private static String convertHtmlChar(String pStr){
            if (pStr == null) {
                pStr = "";
            }
            if ("".equals(pStr)) {
                return "";
            }
            java.text.StringCharacterIterator sciter = new java.text.StringCharacterIterator(
                    pStr);
            String rt = "";
            for (char c = sciter.first(); c != java.text.StringCharacterIterator.DONE; c = sciter
                    .next()) {
                if (c == '\"') {
                    rt += "&quot;";
                } else if (c == '>') {
                    rt += "&gt;";
                } else if (c == '<') {
                    rt += "&lt;";
                } else {
                    rt += c;
                }
            }
            return rt;
    }

    /**
     * 给字符串加单引号，用于数据库
     *
     * @param str
     * @return 加单引号的字符串
     */
    public static String makeQuotedStr(String str) {
        return "'" + CMyString.filterForSQL(str) + "'";
    }

    /**
     * 去掉字符串的单引号
     *
     * @param src 原字符串
     * @return 去掉外层单引号的字符串
     */
    public static String tripQuotedString(String src) {
        String des = src.trim();
        if (des.startsWith("'")) {
            des = des.substring(1);
        }
        if (des.endsWith("'")) {
            des = des.substring(0, des.length() - 1);
        }
        return des;
    }

    /**
     * 合并字符串数组为一个字符串
     *
     * @param s 原字符串数组
     * @return 合并后的字符串
     */
    public static String mergeStrArray(Object ... s) {
        if (s == null || s.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length - 1; i++) {
            sb.append(s[i]).append(",");
        }
        sb.append(s[s.length - 1]);
        return sb.toString();
    }

    /**
     * 合并整型数组为一个字符串
     *
     * @param s 原整型数组
     * @return 合并后的字符串
     */
    public static String mergeIntArray(int[] s) {
      return mergeStrArray(s);
    }

    /**
     * 判断字符串包含关系
     *
     * @param src src格式为1,4,7,8,9,10,11,12,13
     * @param sub 格式为 1
     * @return =true 包含
     */
    public static boolean isInString(String src, String sub) {
        return src.equals(sub)
                // 头
                || src.startsWith(sub + ",")
                // 中间
                || src.indexOf("," + sub + ",") > 0
                // 尾
                || src.endsWith("," + sub);

    }

    public static String getInStringCondition(String name, String value) {
        return name + "='" + value + "' or instr(" + name + ",'" + value
                + ",')=1 or instr(" + name + ",'," + value + ",')>1 or (instr("
                + name + ",'," + value + "')=(length(" + name + ")-length(',"
                + value + "'))+1 and instr(" + name + ",'," + value + "')>0)";
    }

    public static String convertValue2Name(Map ht, String values) {
        if (values == null || ht == null) {
            return "";
        }
        String[] vs = values.split(",");
        String[] ns = new String[vs.length];

        for (int i = 0; i < vs.length; i++) {
            ns[i] = (String) ht.get(vs[i]);
        }
        vs = null;
        return mergeStrArray(ns);
    }

    public static Long[] str2LongArray(String src, String splitter) {
        String[] users = src.split(splitter);
        Long[] data = new Long[users.length];
        for (int i = 0; i < users.length; i++) {
            data[i] = Long.parseLong(users[i]);
        }
        return data;
    }

    public static Long[] str2LongArray(String src) {
        return str2LongArray(src, ",");
    }

}
