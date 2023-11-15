package com.bixuebihui.util.downfile;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * @author xwx
 */
public class DownloadFileNameEncoder {

    private static String encoding = "UTF-8";

    public static void responseHeaders(HttpServletResponse response, HttpServletRequest request,
                                       String fileName, String contextType) throws Exception {
        responseHeaders(response, fileName, getUserAgent(request), contextType);
    }

    /**
     * <mime-mapping>
     * <extension>xls</extension>
     * <mime-type>application/vnd.ms-excel</mime-type>
     * </mime-mapping>
     */
    public static void responseHeaders(HttpServletResponse response, String fileName, String userAgent, String contextType) throws Exception {
        if (contextType == null) {
            contextType = "application/octet-stream";
        }
        response.setContentType(contextType);

        // added by xwx
        fileName = codedFileName(userAgent, fileName, encoding);

        response.setHeader("Content-Disposition", "attachment;filename" + fileName);
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));
    }

    public static String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent;
    }

    public static String codedFileName(String userAgent, String filename, String encoding)
            throws UnsupportedEncodingException {

        String new_filename = URLEncoder.encode(filename, encoding);
        // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
        String rtn = "=\"" + new_filename + "\"";
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            // IE浏览器，只能采用URLEncoder编码
            if (userAgent.indexOf("msie") != -1) {
                rtn = "=\"" + new_filename + "\"";
            }
            // Opera浏览器只能采用filename*
            else if (userAgent.indexOf("opera") != -1) {
                rtn = "*=\"UTF-8''" + new_filename + "\"";
            }
            // Safari浏览器，只能采用ISO编码的中文输出
            else if (userAgent.indexOf("safari") != -1) {
                rtn = "=\""
                        + new String(filename.getBytes(encoding), "ISO8859-1")
                        + "\"";
            }
            // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
            else if (userAgent.indexOf("applewebkit") != -1) {
                new_filename = MimeUtility.encodeText(filename, "UTF8", "B");
                rtn = "=\"" + new_filename + "\"";
            }
            // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
            else if (userAgent.indexOf("mozilla") != -1) {
                rtn = "*=\"UTF-8''" + new_filename + "\"";
                ;
            }
        }

        return rtn;
    }


    public static void setEncoding(String encoding) {
        DownloadFileNameEncoder.encoding = encoding;
    }

}
