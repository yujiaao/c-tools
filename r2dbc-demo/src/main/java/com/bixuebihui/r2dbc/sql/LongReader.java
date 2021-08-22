package com.bixuebihui.r2dbc.sql;

import java.util.List;

/**
 * <p>LongReader class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class LongReader extends RowMapperResultReader<Long> {

    /**
     * <p>Constructor for LongReader.</p>
     *
     * @param resultList a {@link List} object.
     */
    public LongReader(List<Long> resultList) {

        super((rs, index) -> rs.getLong(1),resultList);
    }


}
