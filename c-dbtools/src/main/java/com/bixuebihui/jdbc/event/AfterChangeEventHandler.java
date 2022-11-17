package com.bixuebihui.jdbc.event;

/**
 * @author xwx
 */
public interface AfterChangeEventHandler<T, ID> {


    void afterChange(T info, ChangeType changeType);

    void afterChange(T info, ChangeType changeType, String shardTableName);

    void afterChange(T[] infos, ChangeType changeType);

    void afterChange(String query, Object[] param, ChangeType changeType);

    void afterChange(String query, Object[] param, ChangeType changeType, String shardTableName);

    void afterChangeByKey(ID id, ChangeType changeType);

    void afterChangeByKey(ID[] id, ChangeType changeType);
}
