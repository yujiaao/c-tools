// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2006-1-26 15:56:59
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   SiteFileFetch.java

package com.bixuebihui.util.downfile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// Referenced classes of package com.bixuebihui.util.downfile:
//            FileSplitterFetch, SiteInfoBean, Utility

public class SiteFileFetch extends Thread {

    public SiteFileFetch(SiteInfoBean bean)
            throws IOException {
        siteInfoBean = null;
        bFirst = true;
        bStop = false;
        siteInfoBean = bean;
        tmpFile = new File(bean.getSFilePath() + File.separator + bean.getSFileName() + ".info");
        if (tmpFile.exists()) {
            bFirst = false;
            read_nPos();
        } else {
            nStartPos = new long[bean.getNSplitter()];
            nEndPos = new long[bean.getNSplitter()];
        }
    }

    @Override
    public void run() {
        try {
            if (bFirst) {
                nFileLength = getFileSize();
                if (nFileLength == -1L) {
                    System.err.println("File Length is not known!");
                } else if (nFileLength == -2L) {
                    System.err.println("File is not access!");
                } else {
                    for (int i = 0; i < nStartPos.length; i++) {
                        nStartPos[i] = (long) i * (nFileLength / (long) nStartPos.length);
                    }

                    for (int i = 0; i < nEndPos.length - 1; i++) {
                        nEndPos[i] = nStartPos[i + 1];
                    }

                    nEndPos[nEndPos.length - 1] = nFileLength;
                }
            }
            fileSplitterFetch = new FileSplitterFetch[nStartPos.length];
            for (int i = 0; i < nStartPos.length; i++) {
                fileSplitterFetch[i] = new FileSplitterFetch(siteInfoBean.getSSiteURL(), siteInfoBean.getSFilePath() + File.separator + siteInfoBean.getSFileName(), nStartPos[i], nEndPos[i], i);
                Utility.log("Thread " + i + " , nStartPos = " + nStartPos[i] + ", nEndPos = " + nEndPos[i]);
                fileSplitterFetch[i].start();
            }

            boolean breakWhile = false;
            while (!bStop) {
                write_nPos();
                Utility.sleep(500);
                breakWhile = true;
                for (int i = 0; i < nStartPos.length; i++) {
                    if (fileSplitterFetch[i].bDownOver) {
                        continue;
                    }
                    breakWhile = false;
                    break;
                }

                if (breakWhile) {
                    break;
                }
            }
            System.err.println("文件下载结束！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getFileSize() throws MalformedURLException {
        int nFileLength = -1;
        HttpURLConnection httpConnection = null;
        int responseCode = 0;
        URL url = new URL(siteInfoBean.getSSiteURL());
        try {
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "NetFox");
            responseCode = httpConnection.getResponseCode();

            if (responseCode > 400)//if(responseCode < 400)
            {
                //   break MISSING_BLOCK_LABEL_57;
                processErrorCode(responseCode);
                return -2L;
            }

            int i = 1;
            do {
                String sHeader = httpConnection.getHeaderFieldKey(i);
                if (sHeader == null) {
                    break;
                }
                if ("Content-Length".equals(sHeader)) {
                    nFileLength = Integer.parseInt(httpConnection.getHeaderField(sHeader));
                    break;
                }
                i++;
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utility.log("\u6587\u4EF6\u5927\u5C0F\uFF1A" + nFileLength);
        return (long) nFileLength;
    }

    private void write_nPos() {
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(tmpFile))) {
            output.writeInt(nStartPos.length);
            for (int i = 0; i < nStartPos.length; i++) {
                output.writeLong(fileSplitterFetch[i].nStartPos);
                output.writeLong(fileSplitterFetch[i].nEndPos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read_nPos() {
        try (DataInputStream input = new DataInputStream(new FileInputStream(tmpFile))) {
            int nCount = input.readInt();
            nStartPos = new long[nCount];
            nEndPos = new long[nCount];
            for (int i = 0; i < nStartPos.length; i++) {
                nStartPos[i] = input.readLong();
                nEndPos[i] = input.readLong();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processErrorCode(int nErrorCode) {
        System.err.println("Error Code : " + nErrorCode);
    }

    public void siteStop() {
        bStop = true;
        for (int i = 0; i < nStartPos.length; i++) {
            fileSplitterFetch[i].splitterStop();
        }

    }

    SiteInfoBean siteInfoBean;
    long[] nStartPos;
    long[] nEndPos;
    FileSplitterFetch[] fileSplitterFetch;
    long nFileLength;
    boolean bFirst;
    boolean bStop;
    File tmpFile;
    DataOutputStream output;
}
