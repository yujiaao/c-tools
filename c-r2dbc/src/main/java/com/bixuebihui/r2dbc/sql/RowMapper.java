package com.bixuebihui.r2dbc.sql;

import io.r2dbc.spi.Row;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>RowMapper interface.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public interface RowMapper<T> {
   /**
    * <p>mapRow.</p>
    *
    * @param row    a {@link ResultSet} object.
    * @return a T object.
    */
   T mapRow(Row row) ;
}
