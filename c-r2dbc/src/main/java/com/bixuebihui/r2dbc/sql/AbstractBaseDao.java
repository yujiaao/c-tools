package com.bixuebihui.r2dbc.sql;

import com.bixuebihui.DbException;
import io.r2dbc.spi.Row;
import reactor.core.publisher.Mono;

import java.sql.SQLException;

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
    public Object mapRow(Row rs)  {
        throw new DbException("not implement!");
    }


    /**
     * <p>insertDummy.</p>
     *
     * @return a boolean.
     * @throws SQLException if any.
     */
    @Override
    public Mono<Boolean> insertDummy() throws SQLException {
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
    protected Mono<Void> setIdLong(Object info, Mono<Long> id) {
        return Mono.empty();
    }

    /**
     * <p>getDummySql.</p>
     *
     * @return a {@link String} object.
     */
    public Mono<String> getDummySql() {
       return this.getDbType().flatMap(type -> {
            if ( type == ORACLE){
                return Mono.just(" from dual");
            }
            return Mono.just("");
        });
    }

}
