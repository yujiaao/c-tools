package com.bixuebihui.util.downfile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class FileSplitterFetch extends Thread {

    public FileSplitterFetch(String sURL, String sName, long nStart, long nEnd, int id)
            throws IOException {
        bDownOver = false;
        bStop = false;
        fileAccessI = null;
        this.sURL = sURL;
        nStartPos = nStart;
        nEndPos = nEnd;
        nThreadID = id;
        fileAccessI = new FileAccessI(sName, nStartPos);
    }

    @Override
    public void run() {
        while (nStartPos < nEndPos && !bStop) {
            try {
                URL url = new URL(sURL);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestProperty("User-Agent", "NetFox");
                String sProperty = "bytes=" + nStartPos + "-";
                httpConnection.setRequestProperty("RANGE", sProperty);
                Utility.log(sProperty);
                InputStream input = httpConnection.getInputStream();
                byte[] b = new byte[1024];
                int i;
                for (; (i = input.read(b, 0, 1024)) > 0 && nStartPos < nEndPos && !bStop; nStartPos += fileAccessI.write(b, 0, i)) {
                    ;
                }
                Utility.log("Thread " + nThreadID + " is over!");
                bDownOver = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void logResponseHead(HttpURLConnection con) {
        int i = 1;
        do {
            String header = con.getHeaderFieldKey(i);
            if (header != null) {
                Utility.log(header + " : " + con.getHeaderField(header));
                i++;
            } else {
                return;
            }
        } while (true);
    }

    public void splitterStop() {
        bStop = true;
    }

    String sURL;
    long nStartPos;
    long nEndPos;
    int nThreadID;
    boolean bDownOver;
    boolean bStop;
    FileAccessI fileAccessI;
}
