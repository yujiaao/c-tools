// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2006-1-26 15:56:55
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   CMyFile.java

package com.bixuebihui.util.other;

import org.apache.poi.util.IOUtils;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;


/**
 * @author xwx
 */
public class CMyFile {

    public CMyFile() {
    }

    public static String mapResouceFullPath(String _resource)
            throws CMyException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(
                _resource);
        if (url == null) {
            throw new CMyException(55, "文件[" + _resource + "]没有找到！");
        }
        String sPath = getUrlFileString(url);
        return sPath;
    }

    private static String getUrlFileString(URL url) throws CMyException {
        String sPath = null;
        try {
            sPath = url.getFile();
            if (sPath.indexOf('%') >= 0) {
                sPath = URLDecoder.decode(url.getFile(), "UTF-8");
            }
        } catch (Exception ex) {
            throw new CMyException(55, "文件[" + url.getFile() + "]转换失败！", ex);
        }
        return sPath;
    }

    public static String mapResouceFullPath(String _resource, Class _currClass)
            throws CMyException {
        URL url = _currClass.getResource(_resource);
        if (url == null) {
            throw new CMyException(55, "文件[" + _resource + "]没有找到！");
        }
        String sPath = getUrlFileString(url);
        return sPath;
    }

    public static boolean fileExists(String _sPathFileName) {
        File file = new File(_sPathFileName);
        return file.exists();
    }

    public static boolean pathExists(String _sPathFileName) {
        String sPath = extractFilePath(_sPathFileName);
        return fileExists(sPath);
    }

    public static String extractFileName(String _sFilePathName) {
        int nPos = _sFilePathName.lastIndexOf(File.separatorChar);
        return _sFilePathName.substring(nPos + 1);
    }

    public static String extractHttpFileName(String _sFilePathName) {
        int nPos = _sFilePathName.lastIndexOf("/");
        return _sFilePathName.substring(nPos + 1);
    }

    public static String extractFileExt(String _sFilePathName) {
        int nPos = _sFilePathName.lastIndexOf('.');
        return nPos < 0 ? "" : _sFilePathName.substring(nPos + 1);
    }

    public static String extractFilePath(String _sFilePathName) {
        int nPos = _sFilePathName.lastIndexOf(File.separatorChar);
        return nPos < 0 ? "" : _sFilePathName.substring(0, nPos + 1);
    }

    public static String toAbsolutePathName(String _sFilePathName) {
        File file = new File(_sFilePathName);
        return file.getAbsolutePath();
    }

    public static String extractFileDrive(String _sFilePathName) {
        int nLen = _sFilePathName.length();
        if (nLen > 2 && _sFilePathName.charAt(1) == ':') {
            return _sFilePathName.substring(0, 2);
        }
        if (nLen > 2 && _sFilePathName.charAt(0) == File.separatorChar
                && _sFilePathName.charAt(1) == File.separatorChar) {
            int nPos = _sFilePathName.indexOf(File.separatorChar, 2);
            if (nPos >= 0) {
                nPos = _sFilePathName.indexOf(File.separatorChar, nPos + 1);
            }
            return nPos < 0 ? _sFilePathName : _sFilePathName
                    .substring(0, nPos);
        } else {
            return "";
        }
    }

    public static boolean deleteFile(String _sFilePathName) {
        File file = new File(_sFilePathName);
        return file.delete();
    }

    public static boolean makeDir(String _sDir, boolean _bCreateParentDir) {
        File file = new File(_sDir);
        if (_bCreateParentDir) {
            return file.mkdirs();
        } else {
            return file.mkdir();
        }
    }

    public static boolean deleteDir(String _sDir) {
        return deleteDir(_sDir, false);
    }

    public static boolean deleteDir(String _sDir, boolean _bDeleteChildren) {
        File file = new File(_sDir);
        if (!file.exists()) {
            return false;
        }
        if (_bDeleteChildren) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDir(files[i].getAbsolutePath(), _bDeleteChildren);
                    } else {
                        files[i].delete();
                    }
                }
            }

        }
        return file.delete();
    }

    public static String readFile(String _sFileName) throws CMyException {
        StringBuffer buffContent = null;
        String s1;
        try (FileInputStream fis = new FileInputStream(_sFileName);
             BufferedReader buffReader = new BufferedReader(
                     new InputStreamReader(fis, CMyString.FILE_WRITING_ENCODING));
        ) {
            String s;
            while ((s = buffReader.readLine()) != null) {
                if (buffContent == null) {
                    buffContent = new StringBuffer();
                } else {
                    buffContent.append("\n");
                }
                buffContent.append(s);
            }
            s1 = buffContent != null ? buffContent.toString() : "";
        } catch (FileNotFoundException ex) {
            throw new CMyException(
                    55,
                    "\u8981\u8BFB\u53D6\u5F97\u6587\u4EF6\u6CA1\u6709\u627E\u5230(CMyFile.readFile)",
                    ex);
        } catch (IOException ex) {
            throw new CMyException(53,
                    "\u8BFB\u6587\u4EF6\u65F6\u9519\u8BEF(CMyFile.readFile)",
                    ex);
        }
        return s1;

    }

    public static boolean copyFile(String _sSrcFile, String _sDstFile,
                                   boolean _bMakeDirIfNotExists) throws CMyException {
        FileOutputStream fos =null;
        try ( FileInputStream fis = new FileInputStream(_sSrcFile) ) {
            try {
                fos = new FileOutputStream(_sDstFile);
            } catch (FileNotFoundException ex) {
                if (_bMakeDirIfNotExists) {
                    if (!makeDir(extractFilePath(_sDstFile), true)) {
                        throw new CMyException(
                                50,
                                "\u4E3A\u76EE\u6807\u6587\u4EF6["
                                        + _sDstFile
                                        + "]\u521B\u5EFA\u76EE\u5F55\u5931\u8D25\uFF01");
                    }
                    fos = new FileOutputStream(_sDstFile);
                } else {
                    throw new CMyException(
                            50,
                            "\u6307\u5B9A\u76EE\u6807\u6587\u4EF6["
                                    + _sDstFile
                                    + "]\u6240\u5728\u76EE\u5F55\u4E0D\u5B58\u5728\uFF01",
                            ex);
                }
            }
            byte[] buffer = new byte[4096];
            int i;
            while ((i = fis.read(buffer, 0, 4096)) > 0) {
                fos.write(buffer, 0, i);
            }
        } catch (FileNotFoundException ex) {
            throw new CMyException(
                    55,
                    "\u8981\u590D\u5236\u7684\u539F\u6587\u4EF6\u6CA1\u6709\u53D1\u73B0(CMyFile.copyFile)",
                    ex);
        } catch (IOException ex) {
            throw new CMyException(
                    50,
                    "\u590D\u5236\u6587\u4EF6\u65F6\u53D1\u751F\u5F02\u5E38(CMyFile.copyFile)",
                    ex);
        } finally {
            IOUtils.closeQuietly(fos);

        }

        return true;
    }

    public static boolean writeFile(String _sFileName, String _sFileContent)
            throws CMyException {
        try (
                FileOutputStream fos = new FileOutputStream(_sFileName);
                Writer outWriter = new OutputStreamWriter(fos,
                        CMyString.FILE_WRITING_ENCODING);) {
            outWriter.write(_sFileContent);
            outWriter.flush();
            return true;
        } catch (Exception ex) {
            throw new CMyException(54,
                    "\u5199\u6587\u4EF6\u9519\u8BEF(CMyFile.writeFile)", ex);
        }
    }

    public static boolean writeFile(String _sFileName, String _sFileContent,
                                    String _encoding) throws CMyException {
        int BUFFER = 2048;
        try (
                FileOutputStream fos = new FileOutputStream(_sFileName);
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                Writer outWriter = new OutputStreamWriter(dest, _encoding);
        ) {
            outWriter.write(_sFileContent);
            outWriter.flush();
            return true;
        } catch (Exception ex) {
            throw new CMyException(54,
                    "\u5199\u6587\u4EF6\u9519\u8BEF(CMyFile.writeFile)", ex);
        }
    }

    public static boolean appendFile(String _sFileName, String _sAddContent)
            throws CMyException {
        try (
                RandomAccessFile raf = new RandomAccessFile(_sFileName, "rw");
        ) {
            raf.seek(raf.length());
            raf.writeBytes(_sAddContent);
            return true;
        } catch (Exception ex) {
            throw new CMyException(
                    50,
                    "\u5411\u6587\u4EF6\u8FFD\u52A0\u5185\u5BB9\u65F6\u53D1\u751F\u5F02\u5E38(CMyFile.appendFile)",
                    ex);
        }
    }

    public static boolean copyFile(String _sSrcFile, String _sDstFile)
            throws CMyException {
        return copyFile(_sSrcFile, _sDstFile, true);
    }

    public static void main(String[] args) {
        try {
            String sSrcFile = "d:\\temp\\InfoRadar.pdf";
            String sDstFile = "d:\\temp\\sub\\InfoRadar_old.pdf";
            long lStartTime = System.currentTimeMillis();
            copyFile(sSrcFile, sDstFile);
            long lEndTime = System.currentTimeMillis();
            System.out.println("==============\u6240\u7528\u65F6\u95F4\uFF1A"
                    + (lEndTime - lStartTime) + "ms ==============");
        } catch (CMyException ex) {
            ex.printStackTrace(System.out);
        }
    }

    public File[] listFiles(String _dir, String _extendName) {
        File fDir = new File(_dir);
        if (_extendName.charAt(0) != '.') {
            _extendName = "." + _extendName;
        }
        File[] Files = fDir.listFiles(new CMyFilenameFilter(_extendName));
        return Files;
    }
}
