package com.bixuebihui.r2dbc.sql;

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
    * @param rs    a {@link ResultSet} object.
    * @param index a int.
    * @return a T object.
    * @throws SQLException if any.
    */
   T mapRow(ResultSet rs, int index) throws SQLException;
}
