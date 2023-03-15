package com.bixuebihui.jdbc.event;

import com.bixuebihui.jdbc.IPrimaryKeyedTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

/**
 * @author xwx
 */
public class LogAfterChangeEventHandler<T, ID> extends BaseAfterChangeEventHandler<T,ID> {

    static  final Logger log = LoggerFactory.getLogger(LogAfterChangeEventHandler.class);
    ChangeEventConsumer<ID> handler = (ChangeEvent<ID> e) -> {
        log.info(String.valueOf(e));
    };

    public LogAfterChangeEventHandler(IPrimaryKeyedTable<T, ID> dbOperator) {
        super(dbOperator);
    }

    @Override
    public void sendEvent(ChangeEvent<ID> e) {
        handler.accept(e);
    }


    @Autowired(required = false)
    public void setHandler(ChangeEventConsumer<ID> handler) {
        this.handler = handler;
    }
}
