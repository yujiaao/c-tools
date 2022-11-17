package com.bixuebihui.jdbc.event;

import com.bixuebihui.jdbc.IPrimaryKeyedTable;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * @author xwx
 */
public abstract class BaseAfterChangeEventHandler<T,V> implements AfterChangeEventHandler<T,V>{
    IPrimaryKeyedTable<T,V> dbOperator;

    public BaseAfterChangeEventHandler(IPrimaryKeyedTable<T, V> dbOperator) {
        this.dbOperator = dbOperator;
    }

    public abstract void sendEvent(ChangeEvent<V> e);

    void formEvent(ChangeType changeType, String tableName, T... infos){
        sendEvent( new ChangeEvent<>(dbOperator.getDatabaseName(),
                dbOperator.getTableName(tableName),
                dbOperator.getKeyName(),
                infos==null? emptyList(): Arrays.stream(infos).map(info -> dbOperator.getId(info)).collect(Collectors.toList()),
                changeType ));
    }
    void formEventByIds(ChangeType changeType, String tableName, V... ids){
        sendEvent( new ChangeEvent<>(dbOperator.getDatabaseName(),
                dbOperator.getTableName(tableName),
                dbOperator.getKeyName(),
                ids==null? emptyList() : Arrays.stream(ids).collect(Collectors.toList()),
                changeType ));
    }

    @Override
    public void afterChange(T info, ChangeType changeType) {
        formEvent(changeType, null, info);
    }

    @Override
    public void afterChange(T info, ChangeType changeType, String shardTableName) {
        formEvent(changeType, shardTableName, info);
    }

    @Override
    public void afterChange(T[] infos, ChangeType changeType) {
        formEvent(changeType, null, infos);
    }

    @Override
    public void afterChange(String query, Object[] param, ChangeType changeType) {
        formEvent(changeType,  null, null);
    }

    @Override
    public void afterChange(String query, Object[] param, ChangeType changeType, String shardTableName) {
        formEvent(changeType, shardTableName, null);
    }

    @Override
    public void afterChangeByKey(V v, ChangeType changeType) {
        formEventByIds(changeType, null, v);
    }

    @Override
    public void afterChangeByKey(V[] id, ChangeType changeType) {
        formEventByIds(changeType, null, id);
    }

}
