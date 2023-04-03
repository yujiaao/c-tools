package com.bixuebihui.r2dbc.sql;

import io.r2dbc.spi.Result;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>RowMapperResultReader class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class RowMapperResultReader<T> {

	private RowMapper<T> handle;

    /**
     * <p>Constructor for RowMapperResultReader.</p>
     *
     * @param handle a {@link RowMapper} object.
     */
    public RowMapperResultReader(RowMapper<T> handle){
        this.handle = handle;
    }

    /**
     * <p>processResultSet.</p>
     *
     * @param rs a {@link ResultSet} object.
     * @return a {@link List} object.
     */
    public @NotNull Mono<List<T>> processResult(Result rs) {
        return Mono.from(Flux.from(rs.map((row, meta) -> handle.mapRow(row))).collectList());
    }
}
