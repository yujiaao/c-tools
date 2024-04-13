// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2006-1-26 15:56:55
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   CMyFilenameFilter.java

package com.bixuebihui.util.other;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author xwx
 */
public class CMyFilenameFilter
        implements FilenameFilter {

    public CMyFilenameFilter(String _extendName) {
        sExt = _extendName;
    }

    @Override
    public boolean accept(File _dir, String _name) {
        return _name.endsWith(sExt);
    }

    private String sExt;
}
