package com.bixuebihui.elasticsearch;

import com.bixuebihui.DbException;
import com.bixuebihui.db.ActiveRecord;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author xwx
 */
public interface EsActiveRecord<T> extends ActiveRecord<T> {

    int update(String _id, String field, Object value) throws DbException;

    int update(String _id, String[] fields, Object[] values) throws DbException;

    EsActiveRecord<T> excludeFields(Set<String> excludeFieldsSet);


    int updateByScript(String script, Map<String, Object> params) throws DbException;

    boolean insert(String indexName, T info);

    boolean insert(String indexName, T info, Function<T, String> getId);
    boolean insertBatch(String indexName, List<T> infos, Function<T, String> getId);
}
