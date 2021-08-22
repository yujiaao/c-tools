package com.bixuebihui.r2dbc.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>RowMapperResultReaderToMap class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class RowMapperResultReaderToMap<K,T> {

    private RowMapper<T> handle;
    private String keyName;
    private Map<K,T> res;

    /**
     * <p>Constructor for RowMapperResultReaderToMap.</p>
     *
     * @param handle  a {@link RowMapper} object.
     * @param keyName a {@link String} object.
     */
    public RowMapperResultReaderToMap(RowMapper<T> handle, String keyName){
        this.handle = handle;
        this.keyName = keyName;
    }

    /**
     * <p>Constructor for RowMapperResultReaderToMap.</p>
     *
     * @param handle a {@link RowMapper} object.
     * @param keyName a {@link String} object.
     * @param resultMap a {@link Map} object.
     */
    public RowMapperResultReaderToMap(RowMapper<T> handle, String keyName, Map<K,T> resultMap){
        this.handle = handle;
        this.keyName= keyName;
        this.res = resultMap;
    }


    /**
     * <p>processResultSet.</p>
     *
     * @param rs a {@link ResultSet} object.
     * @return a {@link Map} object.
     * @throws SQLException if any.
     */
    @SuppressWarnings("unchecked")
    public Map<K,T> processResultSet(ResultSet rs) throws SQLException {
        if(res ==null) {
            res = new HashMap<>();
        }else{
            res.clear();
        }
        int index=1;
        while (rs.next()) {
            res.put((K)rs.getObject(keyName), handle.mapRow(rs, index++));
        }
        return res;
    }
}
