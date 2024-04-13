package com.bixuebihui.session;

import com.bixuebihui.util.other.CMyException;


/**
 * @author xwx
 */
public interface ISessionManager {
    SimpleSession read(String s_id);

    boolean update(String s_id) throws CMyException;

    boolean write(SimpleSession ss) throws CMyException;

    boolean destroy(String s_id);

    int gc(long time);

    void open();

    void close();

    boolean isAutoGC();

    SimpleSession createUUIDSession();

    int getCount() throws CMyException;
}
