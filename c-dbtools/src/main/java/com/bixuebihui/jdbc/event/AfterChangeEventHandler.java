package com.bixuebihui.jdbc.event;

import java.util.List;

/**
 * @author xwx
 */
public interface AfterChangeEventHandler<T, ID> {


    void afterChange(T info, ChangeType changeType);

    void afterChange(T info, ChangeType changeType, String shardTableName);

    void afterChange(T[] infos, ChangeType changeType);
    void afterChange(List<T> infos, ChangeType changeType);
    void afterChange(String query, Object[] param, ChangeType changeType, int influenceRecordCount);

    void afterChange(String query, Object[] param, ChangeType changeType, String shardTableName, int influenceRecordCount);

    void afterChangeByKey(ID id, ChangeType changeType);

    void afterChangeByKey(ID[] id, ChangeType changeType);
}
