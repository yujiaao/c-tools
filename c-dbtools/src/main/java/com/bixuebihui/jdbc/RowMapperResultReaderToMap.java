package com.bixuebihui.jdbc;

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
     * @param keyName a {@link java.lang.String} object.
     */
    public RowMapperResultReaderToMap(RowMapper<T> handle, String keyName){
        this.handle = handle;
        this.keyName = keyName;
    }

    /**
     * <p>Constructor for RowMapperResultReaderToMap.</p>
     *
     * @param handle a {@link RowMapper} object.
     * @param keyName a {@link java.lang.String} object.
     * @param resultMap a {@link java.util.Map} object.
     */
    public RowMapperResultReaderToMap(RowMapper<T> handle, String keyName, Map<K,T> resultMap){
        this.handle = handle;
        this.keyName= keyName;
        this.res = resultMap;
    }


    /**
     * <p>processResultSet.</p>
     *
     * @param rs a {@link java.sql.ResultSet} object.
     * @return a {@link java.util.Map} object.
     * @throws java.sql.SQLException if any.
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
            res.put((K)rs.getObject(keyName), handle.mapRow(rs, index++, null));
        }
        return res;
    }
}
