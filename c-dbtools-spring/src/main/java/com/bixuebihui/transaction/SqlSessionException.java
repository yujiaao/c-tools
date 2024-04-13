package com.bixuebihui.transaction;

import com.bixuebihui.DbException;

public class SqlSessionException extends DbException {
    public SqlSessionException(Exception e) {
        super(e);
    }

    public SqlSessionException(String s) {
        super(s);
    }
}
