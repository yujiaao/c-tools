package com.bixuebihui.jdbc.event;

import com.bixuebihui.jdbc.IPrimaryKeyedTable;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
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

    void formEvent(ChangeType changeType, String tableName, T[] infos){
        sendEvent( new ChangeEvent<>(dbOperator.getDatabaseName(),
                dbOperator.getTableName(tableName),
                dbOperator.getKeyName(),
                infos==null? emptyList(): Arrays.stream(infos).map(info -> dbOperator.getId(info)).collect(Collectors.toList()),
                changeType));
    }
    void formEvent(ChangeType changeType, String tableName, T info){
        sendEvent( new ChangeEvent<>(dbOperator.getDatabaseName(),
                dbOperator.getTableName(tableName),
                dbOperator.getKeyName(),
                List.of(dbOperator.getId(info)),
                changeType));
    }

    void formEvent(ChangeType changeType, String tableName, int influnceRecordCount, String query, Object[] params){
        sendEvent( new ChangeEvent<>(dbOperator.getDatabaseName(),
                dbOperator.getTableName(tableName),
                dbOperator.getKeyName(),
                changeType, influnceRecordCount, query + summary(params)));
    }

    protected String summary(Object[] params) {
        return Arrays.stream(params).map(p -> StringUtils.abbreviate(p==null?"": p.toString(), 250)).collect(Collectors.joining(","));
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
    public void afterChange(String query, Object[] param, ChangeType changeType, int influenceRecordCount) {
        formEvent(changeType, null, influenceRecordCount, query, param);
    }

    @Override
    public void afterChange(String query, Object[] param, ChangeType changeType, String shardTableName, int influenceRecordCount) {
        formEvent(changeType, shardTableName, influenceRecordCount, query, param);
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
