package com.bixuebihui.r2dbc.sql;

/**
 * <p>LongReader class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class LongReader extends RowMapperResultReader<Long> {

    /**
     * <p>Constructor for LongReader.</p>
     */
    public LongReader() {
        super((rs) -> rs.get(0, Long.class));
    }


}
