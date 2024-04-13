/*
 * 创建日期 2005-2-23
 *
 */
package com.bixuebihui.util;

import com.bixuebihui.util.other.CMyException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

/**
 * @author wk
 */
public class ParameterUtils {

    private static final String SIG = "sig";
    public static final String ENC = "UTF-8";

    private static final Logger log = LoggerFactory.getLogger(ParameterUtils.class.getName());

    private ParameterUtils(){
        throw new IllegalAccessError("this is a util class");
    }

    public static final String UPLOADEDFILENAME_ATTRIBUTE = "UPLOADEDFILENAME_ATTRIBUTE";
    public static final String UPLOADEDFILEPATH = "/upload";

    private static String encryptKey = Config.getProperty("parameter_encrypt_key16");

    public static final String TIMESTAMP = "_time";

    /**
     * 与getEncryptedParam相对，进行参数解密，以配置文件中的parameter_encrypt_key16为解密密码
     *
     * @param request HttpServletRequest
     * @param paramName 参数名
     * @return 解密的map键值对
     * @throws CMyException
     */
    public static Map<String, String> decryptParam(HttpServletRequest request, String paramName) throws CMyException {
        return decryptParam(request, paramName, encryptKey, 60 * 30);
    }

    public static Map<String, String> decryptParam(HttpServletRequest request, String paramName, int timOutSeconds)
            throws CMyException {
        return decryptParam(request, paramName, encryptKey, timOutSeconds);
    }

    /**
     * 对传入数据进行加密，注意不要传入以sig和_time为键的值，这两个值有专门用途
     *
     * @param params 参数
     * @return 解密的map键值对
     * @throws CMyException
     */
    public static String encryptParam(Map<String, String> params) throws CMyException {
        return encryptParam(params, encryptKey);
    }

    /**
     * 与getEncryptedParam相对，进行参数解密
     *
     * @param request
     * @param paramName
     * @param encryptKey
     * @return 解密的map键值对
     * @throws CMyException
     */
    public static Map<String, String> decryptParam(HttpServletRequest request, String paramName, String encryptKey,
                                                   int timeOutSeconds) throws CMyException {
        String str = getString(request, paramName);
        return decryptString(str, encryptKey, timeOutSeconds);
    }

    protected static Map<String, String> decryptString(String encryptedString, String encryptKey, int timeOutSeconds)
            throws CMyException {
        String str = EncryptUtil.decrypt64(encryptedString, encryptKey);
        Map<String, String> res = Collections.emptyMap();

        if (StringUtils.isBlank(str)) {
            return res;
        }

        res = splitQuery(str);
        if (verifySignedParams(res)) {
            long time = NumberUtils.toLong(res.get(TIMESTAMP));
            if (time <= 0) {
                throw new CMyException(-13, "无法得到时间戳: time=" + time + " row=" + res);
            }
            long curSec = System.currentTimeMillis() / 1000;

            if (Math.abs(time - curSec) > timeOutSeconds) {
                throw new CMyException(-10,
                        "参数已过期，请刷新页面: time=" + time + " currentTime=" + curSec + " timeOutSeconds=" + timeOutSeconds);
            }
        } else {
            throw new CMyException(-11,
                    "verifySignedParams fails, 如果有中文，请用URLEncoder.encode(param1, \"UTF-8\")转化后加密，否则解密时会出错! sig="
                            + res.get(SIG) + " currentTime=" + (System.currentTimeMillis() / 1000) + " timeOutSeconds="
                            + timeOutSeconds);
        }

        return res;
    }

    public static String encryptParam(Map<String, String> params, String encryptKey) throws CMyException {
        params.put(TIMESTAMP, System.currentTimeMillis() / 1000 + "");
        String str = signParams(params);
        return EncryptUtil.encrypt64(str, encryptKey);
    }

    protected static String map2ParameterStringExceptSIG(Map<String, String> paramMap) {
        ArrayList<String> list = new ArrayList<>(paramMap.keySet());
        Collections.sort(list);
        StringBuilder res = new StringBuilder("");
        Iterator<String> localIterator = list.iterator();
        while (localIterator.hasNext()) {
            String value = localIterator.next();
            if (!SIG.equals(value)) {
                res.append(value);
                res.append("=");
                try {
                    res.append(URLEncoder.encode(paramMap.get(value), ENC));
                } catch (UnsupportedEncodingException e) {
                    log.error("",e);
                }
                if (!(localIterator.hasNext())) {
                    break;
                }
                res.append("&");
            }
        }

        if (res.charAt(res.length() - 1) == '&') {
            res.deleteCharAt(res.length() - 1);
        }

        return res.toString();
    }

    /**
     * 对参数进行签名，用于完整性检查，返回queryString,同时在paramMap中增加sig参数
     *
     * @param paramMap 需传递的参数
     * @return 返回queryString
     * @throws CMyException
     */
    protected static String signParams(Map<String, String> paramMap) throws CMyException {
        String s = map2ParameterStringExceptSIG(paramMap);

        paramMap.put(SIG, md5(s));
        s += ("&" + SIG + "=" + paramMap.get(SIG));
        return s;
    }

    protected static boolean verifySignedParams(Map<String, String> paramMap) throws CMyException {

        String s = map2ParameterStringExceptSIG(paramMap);

        boolean res = md5(s).equals(paramMap.get(SIG));
        if (!res) {
            log.debug("str=" + s + " sig=" + paramMap.get(SIG) + " md5="
                    + md5(s));
        }

        return res;
    }

    static String md5(String source) throws CMyException {
        char[] hexchars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] sourcebin = source.getBytes("utf-8");
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(sourcebin);
            byte[] res = digest.digest();
            int i = res.length;
            char[] ascii = new char[i * 2];
            int j = 0;
            for (byte l : res) {
                ascii[(j++)] = hexchars[(l >>> 4 & 0xF)];
                ascii[(j++)] = hexchars[(l & 0xF)];
            }
            return new String(ascii);
        } catch (Exception localException) {
            throw new CMyException(-3, "签名失败：" + localException.getMessage());
        }
    }

    protected static Map<String, String> splitQuery(String queryString) {
        Map<String, String> queryPairs = new LinkedHashMap<>();

        if (StringUtils.isBlank(queryString)) {
            return queryPairs;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx < 0) {
                throw new IllegalArgumentException(
                        "Bad input string format( must use URLEncode.encode):" + queryString);
            }
            try {
                queryPairs.put(URLDecoder.decode(pair.substring(0, idx), ENC),
                        URLDecoder.decode(pair.substring(idx + 1), ENC));
            } catch (UnsupportedEncodingException e) {
                queryPairs.put(pair.substring(0, idx), pair.substring(idx + 1));
            }
        }
        return queryPairs;
    }

    public static void setCharacterEncoding(HttpServletRequest httpservletrequest) throws UnsupportedEncodingException {
        httpservletrequest.setCharacterEncoding(ENC);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, List<String>> getFieldMap(HttpServletRequest request)
            throws UnsupportedEncodingException {

		/*
         * MULTIPART_FORM_DATA是基于流的，只能获取一次，重复获取将为空
		 */
        Map<String, List<String>> fieldsMap;
        fieldsMap = (Map<String, List<String>>) request.getAttribute(ServletFileUpload.MULTIPART_FORM_DATA);
        if (fieldsMap == null) {
            fieldsMap = new HashMap<>(16);
            request.setAttribute(ServletFileUpload.MULTIPART_FORM_DATA, fieldsMap);
        } else {
            return fieldsMap;
        }

        ServletFileUpload fu = new ServletFileUpload(new DiskFileItemFactory());
        List<FileItem> li = null;
        //try {
            // li = fu.parseRequest(request);
            new RuntimeException("commons-fileupload not implemented jakarta interface yet!");
        //} catch (FileUploadException e) {
        //    log.error("",e);
        //}
        int m = 0;
        if (li != null) {
            for (FileItem fi : li) {
                if (fi.isFormField()) {
                    List<String> vec = new ArrayList<>();

                    // 这里取得字段名称
                    String fieldName = fi.getFieldName();
                    // 如果已经存在此字段信息（多选列表或多个同名的隐藏域等）
                    if (fieldsMap.get(fieldName) != null) {
                        vec = fieldsMap.get(fieldName);
                        // 很重要!需要进行编码哦！！！
                        vec.add(fi.getString(request.getCharacterEncoding()));
                    }
                    // 第一次取到这个名的字段
                    else {
                        vec.add(fi.getString(request.getCharacterEncoding()));
                        fieldsMap.put(fieldName, vec);
                    }
                } else {

                    List<String> fileNames = (List<String>) request.getAttribute(UPLOADEDFILENAME_ATTRIBUTE);
                    if (fileNames == null) {
                        fileNames = new ArrayList<>();
                        request.setAttribute(UPLOADEDFILENAME_ATTRIBUTE, fileNames);
                    }

                    String storePath = request.getSession().getServletContext().getRealPath(UPLOADEDFILEPATH);
                    try {
                        //UploadFile up = new UploadFile();
                        //up.saveFileItem(fileNames, m++, storePath, fi);
                    } catch (Exception e) {
                        log.error("",e);
                    }
                }
            }
        }

        return fieldsMap;
    }

    public static String getString(HttpServletRequest request, String s) {
        return getString(request, s, "");
    }

    public static final boolean isMultipartContent(HttpServletRequest request) {
        String contentType = request.getContentType();

        return !"POST".equalsIgnoreCase(request.getMethod()) ? false : isMultipartContent(contentType);
    }

    public static final boolean isMultipartContent(String contentType) {
        if (contentType == null) {
            return false;
        } else {
            return contentType.toLowerCase(Locale.ENGLISH).startsWith("multipart/");
        }
    }

    public static String getString(HttpServletRequest request, String s, String defaultValue) {
        boolean isMultipart = isMultipartContent(request);

        String res = null;
        if (isMultipart) {
            try {
                Map<String, List<String>> m = getFieldMap(request);

                if (m.containsKey(s)) {
                    List<String> v = m.get(s);
                    res = v == null ? null : v.get(0);
                } else {
                    log.debug("'" + s + "\' not find in request! m=" + m.toString() + " m.size=" + m.size()
                            + ", i am try get it from url...request.getParameter(s)=" + request.getParameter(s));

                    res = request.getParameter(s);

                }
            } catch (UnsupportedEncodingException e) {
               log.error("",e);
            }

        } else {
            res = request.getParameter(s);
        }

        // 形如 http://sample.com/a.html?b=abcd#xxx#ddd的链接,应返回abcd
        if ("GET".equals(request.getMethod()) && res != null && res.contains("#")) {
                res = res.substring(0, res.indexOf('#'));

        }
        return res != null ? res.trim() : defaultValue;
    }

    public static byte getBytes(HttpServletRequest request, String s) {
        String bt = getString(request, s);
        return NumberUtils.toByte(bt);
    }

    public static int getInt(HttpServletRequest request, String name) {
        return getInt(request, name, 0);
    }

    // 把全角数字转为半角数字
    public static String getAlabNum(String src, String defaultValue) {
        if (StringUtils.isBlank(src)) {
            return defaultValue;
        }

        String[] nums = {"０", "１", "２", "３", "４", "５", "６", "７", "８", "９"};
        String fnums = "0123456789";
        for (int i = 0; i <= 9; i++) {
            src = src.replaceAll(nums[i], fnums.charAt(i) + ""); // OsPHP.COM.CN
        }
        src = src.replaceAll("[^\\-0-9.]|^0{1,}", "").trim();
        if (StringUtils.isBlank(src)) {
            src = defaultValue;
        }
        return src;
    }

    public static int getInt(HttpServletRequest request, String name, int defaultValue) {
        String rs = getAlabNum(getString(request, name), defaultValue + "");
        return NumberUtils.toInt(rs);
    }

    public static Date getDate(HttpServletRequest httpservletrequest, String s) {
        String date = getString(httpservletrequest, s);

        return strToDate(date);
    }

    public static Date strToDate(String date) {
        if (StringUtils.trimToNull(date) == null) {
            return null;
        }

        int m = date.indexOf('-');
        int k = date.lastIndexOf('-');
        if (m > 0 && k > 0) {
            int yyyy = Integer.parseInt(date.substring(0, m));
            int mm = Integer.parseInt(date.substring(m + 1, k));
            String day = date.substring(k + 1, date.length());
            if (day.contains(" ")) {
                day = day.substring(0, day.indexOf(' '));
            }

            int dd = Integer.parseInt(day);

            return new GregorianCalendar(yyyy, mm - 1, dd).getTime();
        } else {// bad format
            return null;
        }
    }

    public static long getLong(HttpServletRequest request, String name) {
        String rs = getAlabNum(getString(request, name), "0");
        return NumberUtils.toLong(rs);
    }

    public static long getLong(HttpServletRequest request, String name, long defaultValue) {
        String rs = getAlabNum(getString(request, name), defaultValue + "");
        return NumberUtils.toLong(rs);
    }

    public static short getShort(HttpServletRequest request, String name) {
        String rs = getAlabNum(getString(request, name), "0");
        return NumberUtils.toShort(rs);

    }

    public static String[] getArray(HttpServletRequest request, String s) {
        boolean isMultipart = isMultipartContent(request.getContentType());
        String[] res = null;
        if (isMultipart) {
            try {
                Map<String, List<String>> m = getFieldMap(request);

                if (m.containsKey(s)) {
                    List<String> v = m.get(s);
                    res = v.toArray(new String[0]);
                } else {
                    log.info(s + " not find in request! m=" + m.toString() + " m.size=" + m.size());

                }
            } catch (UnsupportedEncodingException e) {
                log.error("",e);
            }

        } else {
            res = request.getParameterValues(s);
        }
        return res;
    }

    public static int[] getArrayInt(HttpServletRequest httpservletrequest, String s) {

        String[] chkIds = getArray(httpservletrequest, s);
        if (chkIds != null && chkIds.length > 0) {
            int[] ids = new int[chkIds.length];
            for (int i = 0; i <  ids.length; i++) {
                 ids[i] = Integer.parseInt(chkIds[i]);
            }
            return  ids;
        }
        return new int[0];
    }

    public static Long[] getArrayLong(HttpServletRequest httpservletrequest, String s) {

        String[] chkIds = getArray(httpservletrequest, s);
        if (chkIds != null && chkIds.length > 0) {
            Long[] ids = new Long[chkIds.length];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = Long.parseLong(chkIds[i]);
            }
            return ids;
        }
        return new Long[0];
    }

    public static boolean isLocalOrUnknown(String ipaddress) {
        return ipaddress == null || ipaddress.length() == 0 || "unknown".equalsIgnoreCase(ipaddress)
                || ipaddress.startsWith("127.0") || ipaddress.startsWith("192.168.") || ipaddress.startsWith("172.16.")
                || ipaddress.startsWith("172.17.") || ipaddress.startsWith("172.18.") || ipaddress.startsWith("172.19.")
                || ipaddress.startsWith("10.")
                //ipv4-mapped ipv6
                || (ipaddress.toLowerCase().startsWith("::ffff:") ? isLocalOrUnknown(ipaddress.substring(7)): false)

                // the prefix of a site-local address is FEC0::/10.
                // Note:  Site-local addresses have been deprecated, but existing implementations can still continue use them
                || ipaddress.toLowerCase().startsWith("fec0:")

                //local-link address
                || ipaddress.toLowerCase().startsWith("fe80:")

                //loopback
                || ipaddress.toLowerCase().startsWith("::");
    }

    private static boolean isUnknown(String ipaddress) {
        return ipaddress == null || ipaddress.length() == 0 || "unknown".equalsIgnoreCase(ipaddress)
                || "127.0.0.1".equals(ipaddress);
    }

    public static String getClientIP(HttpServletRequest request) {
        String ipAddress = null;

        String[] ipHeaders = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP",
                "X-Originating-IP", "HTTP_REMOTE_ADDR", "REMOTE_ADDR"};

        for (String header : ipHeaders) {
            if (isLocalOrUnknown(ipAddress)) {
                ipAddress = request.getHeader(header);
                if (ipAddress == null) {
                    ipAddress = request.getHeader(header.toLowerCase());
                }
                if (ipAddress == null) {
                    ipAddress = request.getHeader(header.toUpperCase());
                }
                if (ipAddress != null && ipAddress.contains(",")) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(','));
                }
            } else {
                break;
            }
        }
        if (isUnknown(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public static String getCookie(HttpServletRequest request, String cookieName) {
        return getCookie(request, cookieName, "");
    }

    public static String getCookie(HttpServletRequest request, String cookieName, String defualtValue) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cooky : cookies) {
                String sname = cooky.getName();

                if (sname != null && sname.equals(cookieName)) {
                    return cooky.getValue();
                }
            }
        }
        return defualtValue;
    }

    public static int getCookieAndRequestInt(HttpServletRequest request, String cookieName, int defaultValue) {
        int res = getInt(request, cookieName);
        if (res == 0) {
            res = getCookieInt(request, cookieName);
        }

        if (res == 0) {
            res = defaultValue;
        }

        return res;
    }

    public static int getCookieInt(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cooky : cookies) {
                String sname = cooky.getName();

                if (sname != null && sname.equals(cookieName)) {
                    String value = cooky.getValue();
                    if (StringUtils.isNotEmpty(value) && StringUtils.isNumeric(value)) {
                        return Integer.parseInt(value);
                    }
                }
            }
        }
        return 0;
    }

    public static long getCookieLong(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (int c = 0; c < cookies.length; c++) {
                String sname = cookies[c].getName();

                if (sname != null && sname.equals(cookieName)) {
                    String value = cookies[c].getValue();
                    if (StringUtils.isNotEmpty(value) && StringUtils.isNumeric(value)) {
                        return Long.parseLong(value);
                    }
                }
            }
        }
        return 0;
    }

    public static Date getCookieAndRequestDate(HttpServletRequest request, String dateName, Date defaultValue) {
        Date res = new Date(0);
        if (res.getTime() == 0) {
            res = getDate(request, dateName);
        }
        if (res == null || res.getTime() == 0) {
            res = getCookieDate(request, dateName);
        }
        if (res == null || res.getTime() == 0) {
            res = defaultValue;
        }
        return res;
    }

    private static Date getCookieDate(HttpServletRequest request, String dateName) {
        return strToDate(getCookie(request, dateName));
    }
}
