package com.bixuebihui.jsongen;

import org.jsonschema2pojo.AbstractRuleLogger;

class MyRuleLogger extends AbstractRuleLogger {
    @Override
    protected void doDebug(String msg) {
        GenEs.logger.debug(msg);
    }

    @Override
    protected void doError(String msg, Throwable e) {
        GenEs.logger.error(msg, e);
    }

    @Override
    protected void doInfo(String msg) {
        GenEs.logger.info(msg);
    }

    @Override
    protected void doTrace(String msg) {
        GenEs.logger.trace(msg);
    }

    @Override
    protected void doWarn(String msg, Throwable e) {
        GenEs.logger.warn(msg, e);
    }

    @Override
    public boolean isDebugEnabled() {
        return GenEs.logger.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return GenEs.logger.isErrorEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return GenEs.logger.isInfoEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return GenEs.logger.isTraceEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return GenEs.logger.isWarnEnabled();
    }
}
