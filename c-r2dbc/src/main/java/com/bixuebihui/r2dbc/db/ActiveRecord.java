package com.bixuebihui.r2dbc.db;

import com.bixuebihui.db.SqlHelper;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-4-18
 * Time: 下午5:57
 * To change this template use File | Settings | File Templates.
 *
 * @author xingwx
 * @version $Id: $Id
 */
public interface ActiveRecord<T> extends Record<T> {

    /**
     * Constant <code>ORDER_ASC=0</code>
     */
    int ORDER_ASC = 0;
    /** Constant <code>ORDER_DESC=1</code> */
    int ORDER_DESC=1;

    /**
     * <p>in.</p>
     * 条件
     * @param field a {@link String} object.
     * @param values an array of {@link Object} objects.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> in(String field, Object[] values);

    /**
     * Call this method will reject empty string as where condition value
     * @return this
     */
    ActiveRecord<T> emptyStringAsNullCondition();

    /**
     * <p>in.</p>
     *
     * @param field a {@link String} object.
     * @param values a {@link Object} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> in(String field, Object values);

    /**
     * <p>like.</p>
     *
     * @param field a {@link String} object.
     * @param value a {@link String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> like(String field, String value);

    /**
     * <p>startWith.</p>
     *
     * @param field a {@link String} object.
     * @param value a {@link String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> startWith(String field, String value);

    /**
     * <p>eq.</p>
     *
     * @param field a {@link String} object.
     * @param value a {@link Object} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> eq(String field, Object value);

    /**
     * <p>ne.</p>
     *
     * @param field a {@link String} object.
     * @param value a {@link Object} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> ne(String field, Object value);

    /**
     * <p>eq.</p>
     *
     * @param fields an array of {@link String} objects.
     * @param value an array of {@link Object} objects.
     * @return a {@link ActiveRecord} object.
     * @throws SQLException if any.
     */
    ActiveRecord<T> eq(String[] fields, Object[] value) throws SQLException;

    /**
     * <p>greaterThan.</p>
     *
     * @param field a {@link String} object.
     * @param value a {@link Object} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> greaterThan(String field, Object value);

    /**
     * <p>smallerThan.</p>
     *
     * @param field a {@link String} object.
     * @param value a {@link Object} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> smallerThan(String field, Object value);

    ActiveRecord<T> isNull(String field);

    ActiveRecord<T> isNotNull(String field);
    /**
     * <p>asc.</p>
     *
     * @param field a {@link String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> asc(String field);


    /**
     * Sort by given order
     *  e.g. select FirstName from DemoTable where Id IN(4,5,6) order by field(Id,4,5,6);
     *  +----+-----------+
     * | Id | FirstName |
     * +----+-----------+
     * |  1 | Chris     |
     * |  2 | Robert    |
     * |  3 | Mike      |
     * |  4 | Sam       |
     * |  5 | Carol     |
     * |  6 | David     |
     * +----+-----------+
     * result
     * +-----------+
     * | FirstName |
     * +-----------+
     * | Sam       |
     * | Carol     |
     * | David     |
     * +-----------+
     * @param field field name
     * @param in fields value in IN clause
     * @return this
     */
    ActiveRecord<T> orderByIn(String field, Object[] in);

    /**
     * <p>desc.</p>
     *
     * @param field a {@link String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> desc(String field);

    /**
     * <p>limit.</p>
     *
     * @param begin a int.
     * @param num a int.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> limit(int begin, int num);


    /**
     * <p>last.</p>
     *
     * @return a {@link Record} object.
     */
    Record<T> last();

    /**
     * 大于或等于
     * @param field db field
     * @param value value to compare
     * @return this
     */
    ActiveRecord<T> greaterOrEqualThan(String field, Object value);

    /**
     * 小于或等于
     * @param field db field
     * @param value value to compare
     * @return this
     */
    ActiveRecord<T> smallerOrEqualThan(String field, Object value);

    /**
     * <p>getCondStack.</p>
     *
     * @return a {@link SqlHelper} object.
     */
    SqlHelper getCondStack();

    /**
     * <p>or.</p>
     *
     * @param andStack a {@link SqlHelper} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> or(SqlHelper andStack);

    /**
     * <p>alias.</p>
     *
     * @param shortName a {@link String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> alias(String shortName);

    /**
     * <p>fields.</p>
     *
     * @param resultFields a {@link String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> fields(String resultFields);

    /**
     * <p>join.</p>
     *
     * @param joinClause a {@link String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> join(String joinClause);
}
