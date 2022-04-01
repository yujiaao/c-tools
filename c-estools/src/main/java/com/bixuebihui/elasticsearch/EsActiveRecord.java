package com.bixuebihui.elasticsearch;

import com.bixuebihui.DbException;
import com.bixuebihui.db.ActiveRecord;

/**
 * @author xwx
 */
public interface EsActiveRecord<T> extends ActiveRecord<T> {

    int update(String _id, String field, Object value) throws DbException;

    int update(String _id, String[] fields, Object[] values) throws DbException;
}
