package com.bixuebihui.r2dbc.sql;

import io.r2dbc.spi.Result;


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
     * @param result a {@link Result} object.
     */
    void processRow(Result result);
}
