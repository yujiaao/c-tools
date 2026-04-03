package com.bixuebihui.jdbc.aop;

import org.slf4j.MDC;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;


/**
 * 对于向es等日志库的输出工具准备，MDC的值可以通过  LayoutPattern 中的 %X{key} 打印
 *
 */
public  class LogContextAdapter {
    private static final String ServerIp;

    static {
        ServerIp = getServerIp();
    }

    private static String getServerIp() {
        String SERVER_IP = null;
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (netInterfaces.hasMoreElements() && SERVER_IP == null) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                        SERVER_IP = ip.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            SERVER_IP = "127.127.127.127";
        }
        if (SERVER_IP == null || SERVER_IP.isEmpty()) {
            SERVER_IP = "127.127.127.127";
        }
        return SERVER_IP;
    }

    static void prepareArgs(Object[] arguments) {
        MDC.put("ServerIP", ServerIp);

        if (arguments != null && arguments.length > 0) {
            StringBuilder build = new StringBuilder();
            build.append("Arguments:");
            for (Object item : arguments) {
                if (item != null) {
                    build.append("[arg]:").append(item);
                } else {
                    build.append("[arg]:");
                }
            }
            MDC.put("Arguments", escape(build.toString()));
        } else {
            //清理线程池中可能出现的问题
            MDC.put("Arguments", "");
        }
    }

    private static String escape(String str) {
        //需要编码
        try {
            if (str != null && !str.isEmpty()) {
                return URLEncoder.encode(str, StandardCharsets.UTF_8);
            }
            return str;
        } catch (Exception ex) {
            return str;
        }
    }
}
