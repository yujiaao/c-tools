package com.bixuebihui.r2dbc.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>RowCallbackHandler interface.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public interface RowCallbackHandler
{
    /**
     * <p>processRow.</p>
     *
     * @param resultset a {@link ResultSet} object.
     * @throws SQLException if any.
     */
    void processRow(ResultSet resultset) throws SQLException;
}
