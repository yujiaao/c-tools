// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2006-1-26 15:56:59
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   FileAccessI.java

package com.bixuebihui.util.downfile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class FileAccessI
        implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -9179776536385077260L;

    public FileAccessI()
            throws IOException {
        this("", 0L);
    }

    public FileAccessI(String sName, long nPos)
            throws IOException {
        oSavedFile = new RandomAccessFile(sName, "rw");
        this.nPos = nPos;
        oSavedFile.seek(nPos);
    }

    public synchronized int write(byte[] b, int nStart, int nLen) {
        int n = -1;
        try {
            oSavedFile.write(b, nStart, nLen);
            n = nLen;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return n;
    }

    transient RandomAccessFile oSavedFile;
    long nPos;
}
