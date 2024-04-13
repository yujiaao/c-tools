package com.bixuebihui.util.other;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;


/**
 * @author xwx
 */
public class CMyEmail {
    private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    private static byte[] codes;
    private String sUsername;
    private String sPassword;
    private String sSmtpServer;
    private String sFrom;
    private String sReplyTo;
    private String sTo;
    private String sCc;
    private String sBcc;
    private String sSubject;
    private String sBody;
    private String sLogs;
    private String sAttachFilename;
    private int iMailFormat;


    public CMyEmail() {
        sUsername = "";
        sSmtpServer = "";
        sFrom = "";
        sReplyTo = "";
        sTo = "";
        sCc = "";
        sBcc = "";
        sSubject = "";
        sBody = "";
        sLogs = "CYC mailer Ver 1.0 logs:\n\n";
        sAttachFilename = "";
        iMailFormat = 1;
    }

    public void setSMTPServer(String smtpServer) {
        sSmtpServer = smtpServer;
    }

    public void setFrom(String from) {
        sFrom = from;
    }

    public void setReplyTo(String replyTo) {
        sReplyTo = replyTo;
    }

    public void setTo(String to) {
        sTo = to;
    }

    public void setCc(String cc) {
        sCc = cc;
    }

    public void setBcc(String bcc) {
        sBcc = bcc;
    }

    public void setSubject(String subject) {
        sSubject = subject;
    }

    public void setBody(String body) {
        sBody = body;
    }

    static {
        codes = new byte[256];
        for (int i = 0; i < 256; i++) {
            codes[i] = -1;
        }

        for (int i = 65; i <= 90; i++) {
            codes[i] = (byte) (i - 65);
        }

        for (int i = 97; i <= 122; i++) {
            codes[i] = (byte) ((26 + i) - 97);
        }

        for (int i = 48; i <= 57; i++) {
            codes[i] = (byte) ((52 + i) - 48);
        }

        codes[43] = 62;
        codes[47] = 63;
    }

    public void setMailFormat(int mailFormat){
        iMailFormat = mailFormat;
    }

    public static void main(String[] args) {
        try {
            CMyEmail sm = new CMyEmail();
            sm.setSMTPServer("localhost");
            sm.setFrom("admin@cashing.com");
            sm.setTo("cashing@cashing.com");
            sm.setReplyTo("admin@cashing.com");
            sm.setSubject("Test email of CYC mailer");
            sm.setBody("<h1><font color='RED'>This is a test email.</font></h1>");
            sm.setAttacheFile("d:\\test.jpg");
            sm.setMailFormat(0);
            if (sm.send()) {
                System.out.println("Send email Successful");
            } else {
                System.out.print("Send email Failure");
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public String getSendLogs() {
        return sLogs;
    }

    public void authSMTPServer(String username, String password) {
        if (username.length() > 0 && password.length() > 0) {
            sUsername = username;
            sPassword = password;
        }
    }

    private void sendBuffer(BufferedReader in, BufferedWriter out, String sRequest)
            throws CMyException {
        String sResponse = "";
        try {
            out.write(sRequest + "\r\n");
            out.flush();
            sLogs = sLogs + sRequest + "\r\n";
            sResponse = in.readLine();
            sLogs = sLogs + sResponse + "\r\n";
        } catch (IOException e) {
            throw new CMyException(0, "\u4ECE\u90AE\u4EF6\u670D\u52A1\u5668\u8BFB\u5199\u6570\u636E\u65F6\u53D1\u751F\u9519\u8BEF", e);
        }
    }

    private void sendBuffer(BufferedWriter out, String sRequest)
            throws CMyException {
        try {
            out.write(sRequest + "\r\n");
            out.flush();
            sLogs = sLogs + sRequest + "\r\n";
        } catch (IOException e) {
            throw new CMyException(0, "从邮件服务器读写数据时发生错误", e);
        }
    }

    private char[] encodeBase64(byte[] data) {
        char[] out = new char[((data.length + 2) / 3) * 4];
        int i = 0;
        for (int index = 0; i < data.length; index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = 0xff & data[i];
            val <<= 8;
            if (i + 1 < data.length) {
                val |= 0xff & data[i + 1];
                trip = true;
            }
            val <<= 8;
            if (i + 2 < data.length) {
                val |= 0xff & data[i + 2];
                quad = true;
            }
            out[index + 3] = alphabet[quad ? val & 0x3f : 64];
            val >>= 6;
            out[index + 2] = alphabet[trip ? val & 0x3f : 64];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3f];
            val >>= 6;
            out[index + 0] = alphabet[val & 0x3f];
            i += 3;
        }

        return out;
    }

    public void setAttacheFile(String filename)
            throws CMyException {
        if (filename != null && filename.length() > 0) {
            File attachFile = new File(filename);
            if (attachFile.exists()) {
                sAttachFilename = attachFile.getPath();
            } else {
                throw new CMyException(0, "附件文件不存在");
            }
        }
    }



    public boolean send() throws CMyException {
        boolean bStatus;

            try(
                    Socket socket = new Socket(sSmtpServer, 25);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GBK"));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "GBK"));
            ) {
                sendBuffer(in, out, "HELO " + sSmtpServer);
                if (sUsername.length() > 0 && sPassword.length() > 0) {
                    sendBuffer(in, out, "AUTH LOGIN");
                    sendBuffer(in, out, new String(encodeBase64(sUsername.getBytes())));
                    sendBuffer(in, out, new String(encodeBase64(sPassword.getBytes())));
                }
                sendBuffer(in, out, "MAIL FROM: " + sFrom);
                for (StringTokenizer stTo = new StringTokenizer(sTo, ";"); stTo.hasMoreTokens(); sendBuffer(in, out, "RCPT TO: " + stTo.nextToken())) {
                    ;
                }
                if (sCc.length() > 0) {
                    for (StringTokenizer stCc = new StringTokenizer(sCc, ";"); stCc.hasMoreTokens(); sendBuffer(in, out, "RCPT TO: " + stCc.nextToken())) {
                        ;
                    }
                }
                if (sBcc.length() > 0) {
                    for (StringTokenizer stBcc = new StringTokenizer(sBcc, ";"); stBcc.hasMoreTokens(); sendBuffer(in, out, "RCPT TO: " + stBcc.nextToken())) {
                        ;
                    }
                }
                sendBuffer(in, out, "DATA");
                sendBuffer(out, "From: " + sFrom);
                sendBuffer(out, "To: " + sTo);
                if (sCc.length() > 0) {
                    sendBuffer(out, "Cc: " + sCc);
                }
                if (sReplyTo.length() == 0) {
                    sendBuffer(out, "Reply-To: " + sFrom);
                } else {
                    sendBuffer(out, "Reply-To: " + sReplyTo);
                }
                sendBuffer(out, "Subject: " + sSubject);
                SimpleDateFormat sdfDataFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                String sDateTime = sdfDataFormat.format(new Date());
                sendBuffer(out, "Date: " + sDateTime);
                sendBuffer(out, "X-Mailer: CYC mailer Ver 1.0");
                sendBuffer(out, "MIME-Version: 1.0");
                if (sAttachFilename.length() > 0) {
                    File attachFile = new File(sAttachFilename);
                    String sFilename = attachFile.getName();
                    if (iMailFormat == 0) {
                        sendBuffer(out, "Content-Type: multipart/mixed; boundary=\"----CYC_Email_Maker\"");
                        sendBuffer(out, "");
                        sendBuffer(out, "------CYC_Email_Maker");
                        sendBuffer(out, "Content-Type: multipart/alternative; boundary=\"----CYC_Email_Html_Maker\"");
                        sendBuffer(out, "");
                        sendBuffer(out, "------CYC_Email_Html_Maker");
                        sendBuffer(out, "Content-Type: text/plain; charset=\"GB2312\"");
                        sendBuffer(out, "");
                        sendBuffer(out, "This is a HTML email.");
                        sendBuffer(out, "");
                        sendBuffer(out, "------CYC_Email_Html_Maker");
                        sendBuffer(out, "Content-Type: text/html; charset=\"GB2312\"");
                        sendBuffer(out, "");
                        sendBuffer(out, sBody);
                        sendBuffer(out, "");
                        sendBuffer(out, "------CYC_Email_Html_Maker--");
                        sendBuffer(out, "");
                        sendBuffer(out, "------CYC_Email_Maker");
                        sendBuffer(out, "Content-Type: application/octet-stream; name=\"" + sFilename + "\"");
                        sendBuffer(out, "Content-Disposition: attachment; filename=\"" + sFilename + "\"");
                        sendBuffer(out, "Content-Transfer-Encoding: base64");
                        sendBuffer(out, "");
                        try(
                            FileInputStream fis = new FileInputStream(attachFile);
                            DataInputStream dis = new DataInputStream(fis);
                        ){
                            int len = (int) attachFile.length();
                            byte[] buf = new byte[len];
                            dis.readFully(buf);
                            sendBuffer(out, new String(encodeBase64(buf)));
                        } catch (IOException e) {
                            throw new CMyException(0, "读取附件文件时发生错误", e);
                        }
                        sendBuffer(out, "");
                        sendBuffer(out, "------CYC_Email_Maker--");
                    } else {
                        sendBuffer(out, "Content-Type: multipart/mixed; boundary=\"----CYC_Email_Maker\"");
                        sendBuffer(out, "");
                        sendBuffer(out, "------CYC_Email_Maker");
                        sendBuffer(out, "Content-Type: text/plain;charset=\"GB2312\"");
                        sendBuffer(out, "");
                        sendBuffer(out, sBody);
                        sendBuffer(out, "");
                        sendBuffer(out, "------CYC_Email_Maker");
                        sendBuffer(out, "Content-Type: application/octet-stream; name=\"" + sFilename + "\"");
                        sendBuffer(out, "Content-Disposition: attachment; filename=\"" + sFilename + "\"");
                        sendBuffer(out, "Content-Transfer-Encoding: base64");
                        sendBuffer(out, "");
                        try(
                            FileInputStream fis = new FileInputStream(attachFile);
                            DataInputStream dis = new DataInputStream(fis);
                        ){
                            int len = (int) attachFile.length();
                            byte[] buf = new byte[len];
                            dis.readFully(buf);
                            sendBuffer(out, new String(encodeBase64(buf)));
                            //throw new CMyException(0, "\u8BFB\u53D6\u9644\u4EF6\u6587\u4EF6\u65F6\u53D1\u751F\u9519\u8BEF", e);
                        }
                        sendBuffer(out, "");
                        sendBuffer(out, "------CYC_Email_Maker--");
                    }
                } else if (iMailFormat == 0) {
                    sendBuffer(out, "Content-Type: multipart/alternative; boundary=\"----CYC_Email_Html_Maker\"");
                    sendBuffer(out, "");
                    sendBuffer(out, "------CYC_Email_Html_Maker");
                    sendBuffer(out, "Content-Type: text/plain;charset=\"GB2312\"");
                    sendBuffer(out, "");
                    sendBuffer(out, "This email is HTML format!");
                    sendBuffer(out, "");
                    sendBuffer(out, "------CYC_Email_Html_Maker");
                    sendBuffer(out, "Content-Type: text/html;charset=\"GB2312\"");
                    sendBuffer(out, "");
                    sendBuffer(out, sBody);
                    sendBuffer(out, "");
                    sendBuffer(out, "------CYC_Email_Html_Maker--");
                } else {
                    sendBuffer(out, "Content-Type: text/plain; charset=\"GB2312\"");
                    sendBuffer(out, "");
                    sendBuffer(out, sBody);
                }
                sendBuffer(out, ".");
                sendBuffer(in, out, "QUIT");
            bStatus = true;
        } catch (Exception e) {
            throw new CMyException(0, "发送邮件时发生错误", e);
        }
        return bStatus;
    }
}
