package com.bixuebihui.jdbc.event;

import com.bixuebihui.jdbc.IPrimaryKeyedTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xwx
 */
public class LogAfterChangeEventHandler<T, ID> extends BaseAfterChangeEventHandler<T,ID> {

    Logger log = LoggerFactory.getLogger(LogAfterChangeEventHandler.class);

    public LogAfterChangeEventHandler(IPrimaryKeyedTable<T, ID> dbOperator) {
        super(dbOperator);
    }

    @Override
    public void sendEvent(ChangeEvent<ID> e) {
        log.info(String.valueOf(e));
    }
}
