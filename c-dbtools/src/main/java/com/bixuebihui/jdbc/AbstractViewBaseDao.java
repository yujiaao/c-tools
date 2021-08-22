package com.bixuebihui.jdbc;

import com.bixuebihui.DbException;

import javax.sql.DataSource;

/**
 * @author xwx
 */
public abstract class AbstractViewBaseDao<T, ID> extends BaseDao<T, ID>{

    protected AbstractViewBaseDao(DataSource ds){
        super(ds);
    }

    @Override
    protected String getInsertSql(){
        return "";
    }


    @Override
    protected String getUpdateSql(){
        return "";
    }

    @Override
    protected Object[] getInsertObjs(T info){
        return new Object[]{ };
    }

    @Override
    protected Object[] getUpdateObjs(T  info){
        return new Object[]{};
    }

    @Override
    public void setId(T info, ID id) {
        throw new DbException("no key to set, don't use this method!");
    }

    @Override
    public void setIdLong(T info, long id) {
        throw new DbException("no key to set, don't use this method!");
    }

    @Override
    public boolean updateByKey(T info)  {
        throw new DbException("This operation is not supported, because table video_recommend_view not have a unique key!");
    }

    @Override
    public boolean deleteByKey(ID key)  {
        throw new DbException("This operation is not supported, because table video_recommend_view not have a unique key!");
    }

    @Override
    public boolean insertDummy(){
        throw new DbException("This operation is not supported, because table video_recommend_view not have a unique key!");
    }
}
