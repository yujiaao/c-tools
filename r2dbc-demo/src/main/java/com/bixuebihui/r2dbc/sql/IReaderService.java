package com.bixuebihui.r2dbc.sql;

import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.Collection;

/**
 * 配合使用完成分页读取数据库表
 *
 * @author xingwx
 * @param <T>  返回类型
 * @version $Id: $Id
 */
public interface IReaderService<T> {

    /**
     * <p>select.</p>
     *
     * @param whereClause a {@link String} object.
     * @param params parameters for prepared statement
     * @param orderBy     a {@link String} object.
     * @param rowStart    a int.
     * @param rowEnd      a int.
     * @return a {@link Collection} object.
     */
    Collection<T> select(String whereClause, Object[] params, String orderBy, int rowStart, int rowEnd);

    /**
     * <p>count.</p>
     *
     * @param whereClause a {@link String} object.
     * @return a int.
     */
    Mono<Integer> count(String whereClause);
    Mono<Integer> countWhere(String whereClause, Object[] params);


}
