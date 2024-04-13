
package com.bixuebihui.util.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MacAddressHelper {

    public MacAddressHelper() {
    }

    public static long getMyMacAsLong() {
        String sMac = getMyMac();
        return getMyMacAsLong(sMac);
    }

    public static long getMyMacAsLong(String mac) {
        if (mac == null || mac.length() <= 0) {
            return 0L;
        }
        String sMac = mac;
        sMac = CMyString.replaceStr(sMac, "-", "");
        long nMac = Long.parseLong(sMac, 16);
        sMac = "" + nMac;
        if (sMac.length() > 12) {
            for (sMac = sMac.substring(0, 12);   sMac.length() > 0 && sMac.charAt(0) == '0'; sMac = sMac.substring(1)) {
                ;
            }
            nMac = Long.parseLong(sMac);
        }
        return nMac;
    }

    private static String getMacOnWindow() {
        String s = "";
        try {
            String s1 = "ipconfig /all";
            Process process = Runtime.getRuntime().exec(s1);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String nextLine;
            for (String line = bufferedreader.readLine(); line != null; line = nextLine) {
                nextLine = bufferedreader.readLine();
                if (line.indexOf("Physical Address") <= 0) {
                    continue;
                }
                int i = line.indexOf("Physical Address") + 36;
                s = line.substring(i);
                break;
            }

            bufferedreader.close();
            process.waitFor();
        } catch (Exception exception) {
            s = "";
        }
        return s.trim();
    }

    private static String getMacOnLinux() {
        String s = "";
        try {
            String s1 = "/sbin/ifconfig -a";
            Process process = Runtime.getRuntime().exec(s1);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String nextLine;
            String res = bufferedreader.readLine();
            String line = res != null ? res.toUpperCase() : null;
            for (; line != null; line = nextLine.toUpperCase()) {
                nextLine = bufferedreader.readLine();
                if (line.indexOf("HWADDR") <= 0) {
                    continue;
                }
                int i = line.indexOf("HWADDR") + 7;
                s = line.substring(i);
                break;
            }

            bufferedreader.close();
            process.waitFor();
        } catch (Exception exception) {
            s = "";
        }
        return s.trim().replace(':', '-');
    }

    private static String getMacOnHP() {
        String s = "";
        try {
            String s1 = "/usr/sbin/lanscan";
            Process process = Runtime.getRuntime().exec(s1);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String nextLine;

            String res = bufferedreader.readLine();
            String line = res != null ? res.toUpperCase() : null;

            for (; line != null; line = nextLine.toUpperCase()) {
                nextLine = bufferedreader.readLine();
                int nPose = line.indexOf("0X");
                if (nPose <= 0) {
                    continue;
                }
                int nStart = nPose + 2;
                int nEnd = line.indexOf(" ", nStart);
                s = line.substring(nStart, nEnd);
                break;
            }

            bufferedreader.close();
            process.waitFor();
        } catch (Exception exception) {
            s = "";
        }
        return s.trim();
    }

    private static String getMacOnSolaris() throws IOException {
        String s = "";
        Process process;
        String s1 = "/usr/sbin/ifconfig -a";
        process = Runtime.getRuntime().exec(s1);
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String nextLine;
        String res = bufferedreader.readLine();
        String line = res != null ? res.toUpperCase() : null;
        for (; line != null; line = nextLine) {
            res = bufferedreader.readLine();
            nextLine = res != null ? res.toUpperCase() : "";
            if (line.indexOf("NEI0") <= 0) {
                continue;
            }
            int nStart = nextLine.indexOf("INET") + 5;
            if (nStart >= 5) {
                int nEnd = nextLine.indexOf(" ", nStart);
                if (nEnd > nStart) {
                    s = nextLine.substring(nStart, nEnd);
                }
            }
            break;
        }

        bufferedreader.close();
        if (s.length() <= 8) {
            return getMacOnSolaris2();
        }
        try {
            process.waitFor();
        } catch (Exception ex) {
            System.out.println("\u83B7\u53D6Sun Solaris\u64CD\u4F5C\u7CFB\u7EDF\u7684Mac\u5730\u5740\u5931\u8D25\uFF01");
            ex.printStackTrace();
            return getMacOnSolaris2();
        }
        return CMyString.replaceStr(s, ".", "");
    }

    private static String getMacOnSolaris2() {
        String s = "";
        try {
            String s1 = "/usr/sbin/ifconfig -a";
            Process process = Runtime.getRuntime().exec(s1);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String res = bufferedreader.readLine();
            String line = res != null ? res.toUpperCase() : null;
            for (; line != null; line = bufferedreader.readLine()) {
                line = line.toUpperCase();
                int nStart = line.indexOf("ETHER");
                if (nStart < 0) {
                    continue;
                }
                nStart += 6;
                int nEnd = line.indexOf(" ", nStart);
                if (nEnd <= 0) {
                    nEnd = line.length();
                }
                s = line.substring(nStart, nEnd).trim();
                break;
            }

            bufferedreader.close();
            process.waitFor();
        } catch (Exception ex) {
            System.out.println("\u83B7\u53D6Sun Solaris\u64CD\u4F5C\u7CFB\u7EDF\u7684Mac\u5730\u5740\u5931\u8D25\uFF01(getMacOnSolaris2)");
            ex.printStackTrace();
        }
        s = CMyString.replaceStr(s, ".", "");
        s = CMyString.replaceStr(s, ":", "");
        return s;
    }

    private static String getMacOnAIX() {
        String s = "";
        try {
            String s1 = "/usr/bin/uname -m";
            Process process = Runtime.getRuntime().exec(s1);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String res = bufferedreader.readLine();
            String line = res == null ? "" : res.toUpperCase();
            s = line;
            bufferedreader.close();
            process.waitFor();
        } catch (Exception exception) {
            s = "";
        }
        return s.trim();
    }

    public static String getMyMac() {
        String sOs = ((String) System.getProperties().get("os.name")).toUpperCase();
        if (sOs.contains("WINDOWS")) {
            return getMacOnWindow();
        }
        if (sOs.contains("HP")) {
            return getMacOnHP();
        }
        if (sOs.contains("LINUX")) {
            return getMacOnLinux();
        }
        if (sOs.contains("SOLARIS")) {
            try {
                return getMacOnSolaris();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (sOs.contains("AIX")) {
            return getMacOnAIX();
        } else {
            return "";
        }
    }

    public static void main(String[] args) {
        try {
            String s = "";
            String line = "        ether 0:3:ba:23:ec:7d";
            line = line.toUpperCase();
            int nStart = line.indexOf("ETHER");
            if (nStart >= 0) {
                nStart += 6;
                int nEnd = line.indexOf(" ", nStart);
                if (nEnd <= 0) {
                    nEnd = line.length();
                }
                s = line.substring(nStart, nEnd);
            }
            s = CMyString.replaceStr(s, ".", "");
            s = CMyString.replaceStr(s, ":", "");
            System.out.println(s);
            System.out.println(getMyMacAsLong(s));
        } catch (Exception ex) {
        }
    }
}
