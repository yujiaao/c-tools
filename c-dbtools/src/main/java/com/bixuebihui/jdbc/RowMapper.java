package com.bixuebihui.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * <p>RowMapper interface.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public interface RowMapper<T> {

   /**
    * map fields in set <b>fields</b>, ignoring case.
    *
    * @param rs
    * @param index row index from 1;
    * @param fields if empty, then map all fields
    * @return Data object
    * @throws SQLException
    */
   T mapRow(ResultSet rs, int index, Set<String> fields) throws SQLException ;

  // T mapRow(ResultSet rs, int index, Set<String> fields, boolean keepNullForNumberField) throws SQLException ;

}
