package com.bixuebihui.jdbc;

import org.apache.commons.lang3.tuple.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>LongReader class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class StringKeyMapReader implements RowCallbackHandler {
    private final List<Pair<String, Object>> list;

    public StringKeyMapReader(List<Pair<String, Object>> list) {
        this.list = list;
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        String key = rs.getString(1);
        Object value = rs.getObject(2);
        if (!rs.wasNull()) {
            list.add(Pair.of(key, value));
        }
    }
}
