package com.bixuebihui.r2dbc.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    public Object mapRow(ResultSet rs, int index) throws SQLException {
        throw new SQLException("not implement!");
    }


    /**
     * <p>insertDummy.</p>
     *
     * @return a boolean.
     * @throws SQLException if any.
     */
    @Override
    public boolean insertDummy() throws SQLException {
        throw new SQLException("not implement!");
    }

    /** {@inheritDoc} */
    @Override
    public Long getId(Object info) {
        return 0L;
    }

    /**
     * <p>setId.</p>
     *
     * @param info a {@link Object} object.
     * @param id a {@link Long} object.
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
     * @return a {@link String} object.
     * @throws SQLException if any.
     */
    public String getDummySql() throws SQLException {
        if(this.getDbType() == ORACLE) {
            return " from dual";
        }
        return "";
    }

}
