package com.bixuebihui.jsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlUtil {
    private static final Logger LOG = LoggerFactory.getLogger(UrlUtil.class);

    /**
     * 增加url后面的get参数
     *
     * @param url   需要修改的 url
     * @param name  参数名
     * @param value 参数值
     * @return url及参数
     */
    public static String addOrReplaceParam(String url, String name, String value) {
        if (value == null || "".equals(value)) {
            return url;
        }

        try {
            value = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.warn("",e);
        }

        if (url.contains("&" + name + "=")
                || url.contains("?" + name + "=")) {
            if (url.contains("&" + name + "=")) {
                url = url.replaceFirst("&" + name + "=\\w+&", "&" + name
                        + "=" + value + "&");
                url = url.replaceFirst("&" + name + "=\\w+", "&" + name
                        + "=" + value);
            } else {
                url = url.replaceFirst("\\?" + name + "=\\w+&", "?" + name
                        + "=" + value + "&");
                url = url.replaceFirst("\\?" + name + "=\\w+", "?" + name
                        + "=" + value);
            }
            return url;
        } else {
            if (url.contains("?")) {
                return url + "&" + name + "=" + value;
            } else {
                return url + "?" + name + "=" + value;
            }
        }
    }


    //"[\\?&]" + name + "=([^&#]*)"
    public static String addOrReplaceParam(String url, String name, String value, boolean needEncodeValue) {
        if (value == null || "".equals(value)) {
            return url;
        }

        if(needEncodeValue) {
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOG.warn("", e);
            }
        }

        if (url.contains("&" + name + "=") ) {
            return url.replaceFirst("[\\?&]" + name + "=([^&#]*)", "&" + name  + "=" + value );

        }else if(url.contains("?" + name + "=")){
            return url.replaceFirst("[\\?&]" + name + "=([^&#]*)", "?" + name + "=" + value );

        } else {
            if (url.contains("?")) {
                return url + "&" + name + "=" + value;
            } else {
                return url + "?" + name + "=" + value;
            }
        }

    }
}
