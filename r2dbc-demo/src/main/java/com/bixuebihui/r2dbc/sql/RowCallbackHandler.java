package com.bixuebihui.r2dbc.sql;

import io.r2dbc.spi.Result;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * <p>RowCallbackHandler interface.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public interface RowCallbackHandler<T>
{
    /**
     * <p>processRow.</p>
     *
     * @param result a {@link Result} object.
     */
    Mono<List<T>> processRow(Result result);
}
