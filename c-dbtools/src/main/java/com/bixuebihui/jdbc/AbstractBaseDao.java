package com.bixuebihui.jdbc;

import com.bixuebihui.DbException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import static com.bixuebihui.db.Dialect.ORACLE;

/**
 * <p>AbstractBaseDao class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class AbstractBaseDao extends BaseDao<Object, Long> {

    /**
     * <p>Constructor for AbstractBaseDao.</p>
     *
     * @param db a {@link IDbHelper} object.
     */
    public AbstractBaseDao(IDbHelper db) {
        this.dbHelper = db;
    }

    /** {@inheritDoc} */
    @Override
    public Object mapRow(ResultSet rs, int index, Set<String> set) throws SQLException {
        throw new DbException("not implement!");
    }


    /**
     * <p>insertDummy.</p>
     *
     * @return a boolean.
     * @throws DbException if any.
     */
    @Override
    public boolean insertDummy() {
        throw new DbException("not implement!");
    }

    /** {@inheritDoc} */
    @Override
    public Long getId(Object info) {
        return 0L;
    }

    /**
     * <p>setId.</p>
     *
     * @param info a {@link java.lang.Object} object.
     * @param id a {@link java.lang.Long} object.
     */
    @Override
    public void setId(Object info, Long id) {
        //must override if the table has a primary key!
    }


    /** {@inheritDoc} */
    @Override
    public String getTableName() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getKeyName() {
        return null;
    }

    @Override
    protected void setIdLong(Object info, long id) {

    }

    /**
     * <p>getDummySql.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDummySql()  {
        if(this.getDbType() == ORACLE) {
            return " from dual";
        }
        return "";
    }

}
