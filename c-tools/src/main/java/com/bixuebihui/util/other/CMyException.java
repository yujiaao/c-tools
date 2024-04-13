package com.bixuebihui.util.other;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author xwx
 */
public class CMyException extends Exception {
    public CMyException(String msg, Throwable rootCause) {
        super(msg);
        errNo = 0;
        this.rootCause = null;
        this.rootCause = rootCause;
    }

    public CMyException(int errNo) {
        this.errNo = 0;
        rootCause = null;
        this.errNo = errNo;
    }

    public CMyException(int errNo, String msg) {
        super(msg);
        this.errNo = 0;
        rootCause = null;
        this.errNo = errNo;
    }

    public CMyException(int errNo, String msg, Throwable rootCause) {
        super(msg);
        this.errNo = 0;
        this.rootCause = null;
        this.errNo = errNo;
        this.rootCause = rootCause;
    }

    public CMyException(String string) {
        super(string);
    }

    public int getErrNo() {
        return errNo;
    }

    public Throwable getRootCause() {
        return rootCause;
    }

    public String getErrNoMsg() {
        switch (errNo) {
            case ERR_MYEXCEPTION:
                // '\001'
                return "ERR_MYEXCEPTION";

            case ERR_DATACONVERT:
                // '\002'
                return "ERR_DATACONVERT";

            case ERR_PARAM_INVALID:
                // '\n'
                return "ERR_PARAM_INVALID";

            case ERR_OBJ_NULL:
                // '\024'
                return "ERR_OBJ_NULL";

            case ERR_NUMOP_FAIL:
                // '\036'
                return "ERR_NUMOP_FAIL";

            case ERR_DBOP_FAIL:
                // '('
                return "ERR_DBOP_FAIL";

            case ERR_CONNECTION_GETFAIL:
                // ')'
                return "ERR_CONNECTION_GETFAIL";

            case ERR_FILEOP_FAIL:
                // '2'
                return "ERR_FILEOP_FAIL";

            case ERR_FILEOP_OPEN:
                // '3'
                return "ERR_FILEOP_OPEN";

            case ERR_FILEOP_CLOSE:
                // '4'
                return "ERR_FILEOP_CLOSE";

            case ERR_FILEOP_READ:
                // '5'
                return "ERR_FILEOP_READ";

            case ERR_FILEOP_WRITE:
                // '6'
                return "ERR_FILEOP_WRITE";

            case ERR_FILE_NOTFOUND:
                // '7'
                return "ERR_FILE_NOTFOUND";

            case ERR_URL_MALFORMED:
                // 'n'
                return "ERR_URL_MALFORMED";

            case ERR_NET_OPENSTREAM:
                // 'o'
                return "ERR_NET_OPENSTREAM";
            default:
                return "ERR_UNKNOWN";
        }
    }

    public String getMyMessage() {
        return super.getMessage();
    }

    @Override
    public String getMessage() {
        if (rootCause == null) {
            return "[" + errNo + "]" + super.getMessage();
        } else {
            return "[" + errNo + "]" + super.getMessage() + ";\r\n <-- " + rootCause.toString();
        }
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    @Override
    public void printStackTrace(final PrintStream _ps) {
        if (rootCause == null) {
            super.printStackTrace(_ps);
        } else {
            synchronized (this) {
                _ps.println(this);
                rootCause.printStackTrace(_ps);
            }
        }
    }

    @Override
    public void printStackTrace(final PrintWriter _pw) {
        if (rootCause == null) {
            super.printStackTrace(_pw);
        } else {
            synchronized (this) {
                _pw.println(this);
                rootCause.printStackTrace(_pw);
            }
        }
    }

    public static String getStackTraceText(Throwable _ex) {
        try (StringWriter strWriter = new StringWriter(); PrintWriter prtWriter= new PrintWriter(strWriter)){
            _ex.printStackTrace(prtWriter);
            prtWriter.flush();
            return strWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected int errNo;
    protected Throwable rootCause;
    public static final int ERR_UNKNOWN = 0;
    public static final int ERR_MYEXCEPTION = 1;
    public static final int ERR_DATACONVERT = 2;
    public static final int ERR_PARAM_INVALID = 10;
    public static final int ERR_OBJ_NULL = 20;
    public static final int ERR_NUMOP_FAIL = 30;
    public static final int ERR_DBOP_FAIL = 40;
    public static final int ERR_CONNECTION_GETFAIL = 41;
    public static final int ERR_FILEOP_FAIL = 50;
    public static final int ERR_FILEOP_OPEN = 51;
    public static final int ERR_FILEOP_CLOSE = 52;
    public static final int ERR_FILEOP_READ = 53;
    public static final int ERR_FILEOP_WRITE = 54;
    public static final int ERR_FILE_NOTFOUND = 55;
    public static final int ERR_URL_MALFORMED = 110;
    public static final int ERR_NET_OPENSTREAM = 111;

}
