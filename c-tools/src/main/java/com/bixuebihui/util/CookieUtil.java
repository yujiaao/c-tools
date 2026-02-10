package com.bixuebihui.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xwx
 */
public class CookieUtil {
    /**
     * 项目当中用到cookie保存中文，但是会报如下错误：
     * <p>
     * Control character in cookie value, consider BASE64 encoding your value
     * <p>
     * 大概意思是保存到cookie当中的值存在控制字符，无法保存。但实际上数据是不存在这种问题的。再看后面的那句话，好像是将要保存的值进行了base64编码，可能是因为中文在编码时出现乱码导致一些控制字符的出现。
     * <p>
     * 解决方案：将要保存的值进行URLEncoder.encode(value,"utf-8")编码。
     * <p>
     * 在提取时，同样进行解码
     * <p>
     * <p>
     * 添加一个cookie值
     *
     * @param name     名称
     * @param value    值
     * @param time     cookie的有效期
     * @param response 保存cookie的对象
     */
    public static void setCookie(String name, String value, Integer time, HttpServletResponse response) {
        try {
            //关键点
            if (value != null) {
                value = java.net.URLEncoder.encode(value, "UTF-8");
            }
        } catch (java.io.UnsupportedEncodingException e) {
        }
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(time);
        response.addCookie(cookie);
    }

    /**
     * 根据name值,从cookie当中取值
     *
     * @param name    要获取的name
     * @param request cookie存在的对象
     * @return 与name对应的cookie值
     */
    public static String getCookie(String name, HttpServletRequest request) {
        Cookie[] cs = request.getCookies();
        String value = "";
        if (cs != null) {
            for (Cookie c : cs) {
                if (name.equals(c.getName())) {
                    try {

                        //关键点
                        value = c.getValue();
                        if (value != null) {
                            value = java.net.URLDecoder.decode(value, "UTF-8");
                        }


                    } catch (java.io.UnsupportedEncodingException e) {
                    }
                    return value;
                }
            }
        }
        return value;

    }

}
