package com.bixuebihui.db;

import com.bixuebihui.DbException;
import com.bixuebihui.jdbc.BaseDao;
import com.bixuebihui.query.Paging;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.StrReplace;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;

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
public interface ActiveRecord<T> extends Record<T>, Customizer<T> {

    /**
     * Constant <code>ORDER_ASC=0</code>
     */
    int ORDER_ASC = 0;
    /**
     * Constant <code>ORDER_DESC=1</code>
     */
    int ORDER_DESC = 1;

    /**
     * the fields that query will return
     *
     * @return a set of DB fields
     */

    Set<String> getResultFieldsSet();

    /**
     * <p>in</p>
     * condition
     *
     * @param field  a {@link java.lang.String} object.
     * @param values an array of {@link java.lang.Object} objects.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> in(String field, Object[] values);

    ActiveRecord<T> in(String field, List<?> values);


    /**
     * <p>not in</p>
     * condition
     *
     * @param field  a {@link java.lang.String} object.
     * @param values an array of {@link java.lang.Object} objects.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> notIn(String field, Object[] values);

    ActiveRecord<T> notIn(String field, List<?> values);

    /**
     * Call this method will reject empty string as where condition value
     *
     * @return this
     */
    ActiveRecord<T> emptyStringAsNullCondition();

    /**
     * By defautl, the rs.getInt(fieldName) return primitive type, so well turn null to zero,
     * if this is not desired, then call keepNullForNumberField to return null using the trick below:
     * ResultSet rs = new ResultSet();
     * <p>
     * resultVO.setTaskId(rs.getLong("task_id"));
     * if(rs.wasNull()){
     * resultVO.setTaskId(null);
     * }
     * <p>
     * or
     * <p>
     * ResultSet rs = statement.executeQuery("select Id from table");
     * Integer id = 0;
     * if (rs.getObject("Id") != null) {
     * id = rs.getInt("Id");
     * } else {
     * id = null;
     * }
     * JDBC ResultSet 的getInt, getLong, getDouble, getFloat等方法，不论数据库中是否存储为null，都返回0，或者0.0。
     * 注意：这个wasNull判断一定要紧跟在你想要判断非空的那个字段取动作之后，否则它判断的就不是你所期望的那个column值了。
     *
     * @return
     */
    ActiveRecord<T> keepNullForNumberField();


    /**
     * <p>in.</p>
     *
     * @param field  a {@link java.lang.String} object.
     * @param values a {@link java.lang.Object} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> in(String field, Object values);

    /**
     * <p>like.</p>
     *
     * @param field a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> like(String field, String value);

    /**
     * <p>startWith.</p>
     *
     * @param field a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> startWith(String field, String value);

    /**
     * <p>eq.</p>
     *
     * @param field a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> eq(String field, Object value);

    /**
     * <p>between.</p>
     *
     * @param field      a {@link java.lang.String} object.
     * @param leftValue  a {@link java.lang.Object} object.
     * @param rightValue a {@link java.lang.Object} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> between(String field, Object leftValue, Object rightValue);

    /**
     * <p>notBetween.</p>
     *
     * @param field      a {@link java.lang.String} object.
     * @param leftValue  a {@link java.lang.Object} object.
     * @param rightValue a {@link java.lang.Object} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> notBetween(String field, Object leftValue, Object rightValue);

    /**
     * <p>ne.</p>
     *
     * @param field a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> ne(String field, Object value);

    /**
     * <p>eq.</p>
     *
     * @param fields an array of {@link java.lang.String} objects.
     * @param value  an array of {@link java.lang.Object} objects.
     * @return a {@link ActiveRecord} object.
     * @throws SQLException if any.
     */
    ActiveRecord<T> eq(String[] fields, Object[] value) throws SQLException;

    /**
     * <p>greaterThan.</p>
     *
     * @param field a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> greaterThan(String field, Object value);

    /**
     * <p>smallerThan.</p>
     *
     * @param field a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> smallerThan(String field, Object value);

    ActiveRecord<T> isNull(String field);

    ActiveRecord<T> isNotNull(String field);


    /**
     * <p>asc.</p>
     *
     * @param field a {@link java.lang.String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> asc(String field);


    ActiveRecord<T> and(SqlHelper andStack);

    ActiveRecord<T> not(SqlHelper notStack);

    /**
     * Sort by given order
     * e.g. select FirstName from DemoTable where Id IN(4,5,6) order by field(Id,4,5,6);
     * +----+-----------+
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
     *
     * @param field field name
     * @param in    fields value in IN clause
     * @return this
     */
    ActiveRecord<T> orderByIn(String field, Object[] in);

    /**
     * <p>desc.</p>
     *
     * @param field a {@link java.lang.String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> desc(String field);

    /**
     * <p>limit.</p>
     *
     * @param begin a int.
     * @param num   a int.
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
     * @param field db field
     * @param value value to compare
     * @return this
     */
    ActiveRecord<T> greaterOrEqualThan(String field, Object value);

    /**
     * @param field db field
     * @param value value to compare
     * @return this
     */
    ActiveRecord<T> smallerOrEqualThan(String field, Object value);

    /**
     * @param field db field
     * @param value value to compare
    */
    ActiveRecord<T> wildcard(String field, String[] value);
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

    default ActiveRecord<T> or(ActiveRecord<T> orStack){
        return or(orStack.getCondStack());
    }
    default ActiveRecord<T> and(ActiveRecord<T> andStack){
        return and(andStack.getCondStack());
    }
    default ActiveRecord<T> not(ActiveRecord<T> notStack){
        return not(notStack.getCondStack());
    }

    /**
     * <p>alias.</p>
     *
     * @param shortName a {@link java.lang.String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> alias(String shortName);

    /**
     * Override table name for partition or other porpoise, otherwise you can omit to call this
     *
     * @param partitionTableName
     * @return
     */
    ActiveRecord<T> tableName(String partitionTableName);

    /**
     * <p>fields.</p>
     *
     * @param resultFields a {@link java.lang.String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> fields(String resultFields);

    /**
     * <p>join.</p>
     *
     * @param joinClause a {@link java.lang.String} object.
     * @return a {@link ActiveRecord} object.
     */
    ActiveRecord<T> join(String joinClause);

    /**
     * sql group by clause
     *
     * @param fields
     * @return
     */
    ActiveRecord<T> groupBy(String... fields);

    ActiveRecord<T> having(HavingConditon fieldProcessor);


    static Set<String> toFieldsSet(String commaSeparatedFields) {
        if (StringUtils.isBlank(commaSeparatedFields) || commaSeparatedFields.contains("*")) {
            return emptySet();
        }
        return Stream.of(commaSeparatedFields.split(",")).map(x -> BaseDao.getFieldAlias(x.toLowerCase(Locale.ROOT))).collect(Collectors.toSet());
    }

    /**
     * use 'search bean' value to query result:
     * when search bean's property is primitive, this is equals `eq`,
     * when property is String, this is like 'like',
     * when property is Date/Time, this is between operation
     *
     * @param searchBean
     * @param fields     valid table/view fields
     * @param <S>
     * @return
     */
    default <S> ActiveRecord<T> query(Paging<S> searchBean, Set<String> fields) {
        return searchBean.toActiveRecordParams(this, fields);
    }

    <S> ActiveRecord<T> query(Paging<S> searchBean);


    default String findAllToCsv(String titles) throws DbException {
        return findAllToCsv(titles, null);
    }
    /**
     * export to CSV format with Excel preferences
     * @param titles  i18n titles
     * @param onlyFields field names of POJO type T
     * @return csv string
     * @throws DbException if any.
     */
    default String findAllToCsv(String titles, String onlyFields) throws DbException {
         return findAllToCsv(titles, onlyFields, false, false);
    }

    default String findAllToCsv(String titles, String onlyFields,  boolean withBom, boolean forceStringForDateTime) throws DbException {
        List<T> list =  findAll();
        return findAllToCsv(list, titles, onlyFields, withBom, forceStringForDateTime);
    }



    class MyCustomCellProcessor extends CellProcessorAdaptor {
        // 匹配日期时间格式，如 2020-01-01 12:00:00.000 或 2020-01-01 12:00:00
        static String regex = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(\\.\\d+)?$";
        static String replacement = "=\"$0\"";
        public MyCustomCellProcessor() {
            super(new Optional(new StrReplace(regex, replacement)));
        }

        public MyCustomCellProcessor(StringCellProcessor next) {
            super(new Optional(new StrReplace(regex, replacement, next)));
        }

        @Override
        public <T> T execute(Object value, CsvContext context) {
            return next.execute(value, context);
        }
    }

    static <T> String findAllToCsv(List<T> list, String titles, String onlyFields) throws DbException {
        return findAllToCsv(list, titles, onlyFields, false, false);
    }
    static <T> String findAllToCsv(List<T> list, String titles, String onlyFields, boolean withBom, boolean forceStringForDateTime) throws DbException {

        if(list.isEmpty()) return "";

        String[] fields = onlyFields!=null ? onlyFields.split(",")
                : Arrays.stream(list.get(0).getClass().getDeclaredFields())
                .map(Field::getName).filter(name -> !name.contains("$")).toArray(String[]::new);

        StringWriter sb = new StringWriter(10_000);

        // add bom header to StringBuilder
        if(withBom) {
            sb.write("\uFEFF");
        }

        ICsvBeanWriter beanWriter = new CsvBeanWriter(sb,
                CsvPreference.EXCEL_PREFERENCE);

        String[] header = titles.split(",");

        try {
            beanWriter.writeHeader(header);
            if(forceStringForDateTime) {
                CellProcessor[] processors = new CellProcessor[fields.length];

                for (int i = 0; i < fields.length; i++) {
                    processors[i] = new MyCustomCellProcessor();
                }
                for (T pojo : list) {
                    beanWriter.write(pojo, fields, processors);
                }
            }else{
                for (T pojo : list) {
                    beanWriter.write(pojo, fields);
                }
            }
            beanWriter.close();
        } catch (IOException e) {
            throw new DbException(e);
        }
        return sb.toString();
    }
}

