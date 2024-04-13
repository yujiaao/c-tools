package com.bixuebihui.session;

import com.bixuebihui.util.other.CMyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

/**
 * @author xwx
 */
public abstract class BaseSessionManager implements ISessionManager {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseSessionManager.class);


    abstract boolean insert(SimpleSession ss) throws CMyException;

    /**
     * SINGLE_LOGIN只允许每个用户一点登陆，即同一用户ID不能在两台以上的机器上同时用
     * 或同时开两个浏览器窗口，缺省为 false, SINGLE_LOGIN=true时，建在 session表上加唯一键
     */
    public static boolean SINGLE_LOGIN = false;

    public BaseSessionManager() {
        super();
    }

    @Override
    public boolean isAutoGC() {
        return true;
    }

    @Override
    public void close() {

    }

    @Override
    public void open() {
        gc((new Date()).getTime());
    }

    @Override
    public SimpleSession createUUIDSession() {
        SimpleSession ss = new SimpleSession();
        ss.setS_id(UUID.randomUUID().toString());
        ss.setS_fp(" ");
        ss.setS_start(new Date());
        ss.setS_expire((new Date()).getTime() + SimpleSession.SESSION_LIFE);
        return ss;
    }

    @Override
    public boolean write(SimpleSession ss) throws CMyException {

        if (!update(ss.getS_id())) {
            if (SINGLE_LOGIN) {
                destroy(ss.getS_id());
            }
            return insert(ss);
        }

        return true;
    }

}
