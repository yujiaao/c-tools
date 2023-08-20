package com.bixuebihui.jdbc;

import com.bixuebihui.DbException;
import com.bixuebihui.SqlUtils;
import com.bixuebihui.db.*;
import com.bixuebihui.jdbc.aop.DbHelperAroundAdvice;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;
import com.bixuebihui.jdbc.event.AfterChangeEventHandler;
import com.bixuebihui.jdbc.event.ChangeEventConsumer;
import com.bixuebihui.jdbc.event.ChangeType;
import com.bixuebihui.jdbc.event.LogAfterChangeEventHandler;
import com.bixuebihui.sequence.SequenceUtils;
import com.bixuebihui.shardingjdbc.core.api.HintManager;
import com.bixuebihui.sql.SQLUtil;
import com.bixuebihui.util.DbUtils;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.beanutils.*;
import org.apache.commons.beanutils.converters.*;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.bixuebihui.db.Dialect.*;
import static java.util.Collections.emptyList;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;

/**
 * <p>Abstract BaseDao class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public abstract class BaseDao<T, ID> implements RowMapper<T>, IBaseListService<T, ID>,
        SimpleDaoInterface<T, ID> {

    /**
     * Constant <code>SELECT_FROM="select * from "</code>
     */
    public static final String SELECT_FROM = "select * from ";
    /**
     * Constant <code>WHERE=" where "</code>
     */
    protected static final String WHERE = " where ";
    /**
     * Constant <code>LOG</code>
     */
    protected static final Logger LOG = LoggerFactory.getLogger(BaseDao.class);
    private static final String SELECT_COUNT_FROM = "select count(*) from ";
    private static final Set<Class<?>> BUILT_IN_SET = new HashSet<>();

    static {
        ConvertUtils.register(new DateConverter(null), Date.class);
        ConvertUtils.register(new SqlDateConverter(null), java.sql.Date.class);
        ConvertUtils.register(new SqlTimestampConverter(null), Timestamp.class);
        ConvertUtils.register(new SqlTimeConverter(null), java.sql.Time.class);
        ConvertUtils.register(new BooleanConverter(null), Boolean.class);
    }

    static {
        BUILT_IN_SET.add(Integer.class);
        BUILT_IN_SET.add(Long.class);
        BUILT_IN_SET.add(Double.class);
        BUILT_IN_SET.add(Float.class);
        BUILT_IN_SET.add(Boolean.class);
        BUILT_IN_SET.add(Character.class);
        BUILT_IN_SET.add(Byte.class);
        BUILT_IN_SET.add(Short.class);
    }

    protected final PojoValidator<T> pojoValidator = new PojoValidator<>();
    protected IDbHelper dbHelper = null;
    AfterChangeEventHandler<T, ID> afterChangeEventHandler;
    private int dbtype = UNKNOWN;
    private String databaseName;

    /**
     * <p>Constructor for BaseDao.</p>
     *
     * @param dbHelper a {@link IDbHelper} object.
     */
    public BaseDao(IDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public BaseDao(DataSource ds) {
        this(ds, ds);
    }

    public BaseDao(DataSource master, DataSource slave) {
        MSDbHelper dbHelper0 = new MSDbHelper();
        dbHelper0.setMasterDatasource(master);
        dbHelper0.setDataSource(slave);

        if (LOG.isDebugEnabled()) {
            ProxyFactory obj = new ProxyFactory(dbHelper0);
            obj.addAdvice(new DbHelperAroundAdvice());
            dbHelper = (IDbHelper) obj.getProxy();
        } else {
            dbHelper = dbHelper0;
        }
    }

    /**
     * <p>Constructor for BaseDao.</p>
     */
    public BaseDao() {
    }

    /**
     * <p>sql.</p>
     *
     * @param src a {@link java.lang.String} object.
     * @return a {@link SqlString} object.
     */
    protected static SqlString sql(String src) {
        return new SqlString(src);
    }

    /**
     * <p>fields.</p>
     * Comma separated column names, used for select statement when you don't want return all fields,
     * in case to optimize the index usage.
     *
     * @param fields a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    protected static String[] fields(String... fields) {
        return fields;
    }

    /**
     * <p>params.</p>
     *
     * @param params a {@link java.lang.Object} object.
     * @return an array of {@link java.lang.Object} objects.
     */
    protected static Object[] params(Object... params) {
        return params;
    }

    /**
     * MS SQL Server 2000
     *
     * @param selectSql 不带分页的基本语句
     * @param startNum  起始行，第一页从零开始
     * @param endNum    结束行，每页数+startNum
     * @return 完整分页SQL
     */
    private static String getPagingSqlSqlServer(String selectSql, int startNum, int endNum) {
        return SqlServer2000PageHelper.getLimitString(selectSql, startNum, endNum - startNum + 1);
    }

    /**
     * <p>makeQuotedStr.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String makeQuotedStr(String s) {
        return "'" + SQLUtil.escapeString(s) + "'";
    }

    /**
     * <p>detectDbType.</p>
     *
     * @param driverName a {@link java.lang.String} object.
     * @return a int.
     */
    public static int detectDbType(String driverName) {
        int res = UNKNOWN;
        String name = driverName.toUpperCase();
        if (name.contains("ORACLE")) {
            res = ORACLE;
        } else if (name.contains("DERBY")) {
            res = DERBY;
        } else if (name.contains("MYSQL")) {
            res = MYSQL;
        } else if (name.contains("SQLSERVER")) {
            // sqlserver 2000
            res = SQLSERVER;
        //} else if (name.contains("SQL SERVER 201") || name.contains("SQL SERVER 202")) {

        } else if (name.contains("SQL SERVER")) {
            res = SQLSERVER_2010_AND_UP; //2010 ~ 2029
            // sql server 2005
          //  res = SQLSERVER_2005_AND_UP;
        } else if (name.contains("POSTGRESQL")) {
            res = POSTGRESQL;
        } else if (name.contains("H2")) {
            res = H2;
        } else if (name.contains("ACCESS")) {
            res = ACCESS;
        }
        return res;
    }

    /**
     * MS SQL Server 2005 and up
     *
     * @param selectSql 不带分页的基本语句
     * @param startNum  起始行，第一页从零开始
     * @param endNum    结束行，每页数+startNum
     * @return 完整分页SQL
     */
    public static String getPagingSqlSqlServer2005(String selectSql, int startNum, int endNum) {
        return SqlServer2005PageHelper.getLimitString(selectSql, startNum, endNum);
    }
    public static String getPagingSqlSqlServer2010(String selectSql, int startNum, int endNum) {
        return SqlServer2010PageHelper.getLimitString(selectSql, startNum, endNum-startNum);
    }

    /**
     * Paging with PostgreSQL is super easy! select * from table where (x=y)
     * offset 0 limit 30; Gives you the first 30 matches, then do select * from
     * table where (x=y) offset 30 limit 30; This will give the next 30, super
     * easy!
     *
     * @param selectSql 不带分页的基本语句
     * @param startNum  起始行，第一页从零开始
     * @param endNum    结束行，每页数+startNum
     * @return 完整分页SQL
     */
    private static String getPagingSqlPostgresql(String selectSql, int startNum, int endNum) {

        return selectSql + " offset " + startNum + " limit " + (endNum - startNum);

    }

    /**
     * Oracle
     *
     * @param selectSql 不带分页的基本语句
     * @param startNum  起始行，第一页从零开始
     * @param endNum    结束行，每页数+startNum
     * @return 完整分页SQL
     */
    private static String getPagingSqlOracle(String selectSql, int startNum, int endNum) {

        return SELECT_FROM + "(select FR.*,ROWNUM RN from (" + selectSql + ") FR where rownum<=" + endNum
                + ") where RN>" + startNum;

    }

    /**
     * sql2005
     * <p>
     * WITH, ROW_NUMBER （行数）and OVER
     * <p>
     * 这对SQL Server
     * 2005来说非常新鲜并且看上去非常有用。下面一个例子显示从一个结果集得到20至19条记录。刚开始有一点惊奇，但是浏览了查询器后发现它是如此简单。
     * <p>
     * <p>
     * With Customer AS ( SELECT CustomerID, CompanyName, ROW_NUMBER() OVER (order
     * by CompanyName) as RowNumber FROM Customers ) select * from Customer Where
     * RowNumber Between 20 and 30
     * <p>
     * SQL Server 2005的WITH指定了一个临时命名的结果，很像SQL
     * Server以前版本中的临时表。但是，输入部分是ROW_NUMBER和OVER声明
     * ，它根据公司的名称在每组中创建行数。这就像通过命令条文向临时表添加一个身份种子。
     * <p>
     * DB2 上用 FETCH FIRST n ROW ONLY 代替可行啦，即是
     * <p>
     * SELECT tRANGE.* FROM (SELECT tTOP.* FROM (SELECT * FROM t ORDER BY k ASC
     * FETCH FIRST endNum ROW ONLY) tTOP ORDER BY tTOP.k DESC FETCH FIRST
     * (endNum-startNum+1) ROW ONLY) tRANGE ORDER BY tRANGE.k ASC
     *
     * @param selectSql 不带分页的基本语句
     * @param startNum  起始行，第一页从零开始
     * @param endNum    结束行，每页数+startNum
     * @return 完整分页SQL
     */
    private static String getPagingSqlMySql(String selectSql, int startNum, int endNum) {

        return selectSql + " LIMIT " + startNum + "," + (endNum - startNum);

    }

    /**
     * SELECT * FROM ( SELECT ROW_NUMBER() OVER() AS rownum, myLargeTable.* FROM
     * myLargeTable ) AS tmp WHERE rownum > 200000 AND rownum <= 200005;
     *
     * @param selectSql 不带分页的基本语句
     * @param startNum  起始行，第一页从零开始
     * @param endNum    结束行，每页数+startNum
     * @return 完整分页SQL
     */
    private static String getPagingSqlDerby(String selectSql, int startNum, int endNum) {

        String orderBy = selectSql.indexOf("order by") > 0 ? selectSql.substring(selectSql.indexOf("order by")) : "";
        selectSql = selectSql.indexOf("order by") > 0 ? selectSql.substring(0, selectSql.indexOf("order by")) : selectSql;

        return SELECT_FROM + "(select FR.*,ROW_NUMBER() OVER() AS RN from (" + selectSql
                + ")as FR " + orderBy + ") as ttt where RN<=" + endNum + " and RN>" + startNum;

    }

    private static String getDateSqlDerby(String dt) {
        return "date('" + dt + "')";
    }

    private static String getTimestampSqlDerby(String dt) {
        return "TIMESTAMP('" + dt + "')";
    }

    private static String getDateSqlOracle(String dt) {
        return "to_date('" + dt + "','yyyy-mm-dd')";
    }

    private static String getDateSqlMySql(String dt) {
        return "'" + dt + "'";
    }

    private static String getDateSqlServer(String dt) {
        return "'" + dt + "'";
    }

    private static String getDateSqlDerby(java.util.Date dt) {
        return "date('" + formatDate(dt) + "')";
    }

    /**
     * 使用SimpleDateFormat需要同步才行 For more information on this see Sun Bug
     * #6231579 and Sun Bug #6178997.
     *
     * @param dt 日期
     * @return 格式化的日期字符串
     */
    private static String formatDate(Date dt) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        return fmt.format(dt);
    }

    private static String getDateSqlOracle(Date dt) {
        return "to_date('" + formatDate(dt) + "','yyyy-mm-dd')";
    }

    private static String getDateSqlPostgresql(Date dt) {
        return "to_date('" + formatDate(dt) + "','YYYY-MM-DD')";
    }

    private static String getDateSqlMySql(Date dt) {
        return "'" + formatDate(dt) + "'";
    }

    private static String getDateSqlServer(Date dt) {
        return "'" + formatDate(dt) + "'";
    }

    /**
     * <p>registerConverters.</p>
     *
     * @param convertors a {@link java.util.Map} object.
     */
    public static void registerConverters(Map<Class, Converter> convertors) {
        BeanUtilsBean b = BeanUtilsBean.getInstance();
        for (Entry<Class, Converter> entry : convertors.entrySet()) {
            b.getConvertUtils().register(entry.getValue(), entry.getKey());
        }
    }

    /**
     * <p>filterForSQL.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String filterForSql(String sql) {
        if (sql == null || sql.length() == 0) {
            return "";
        }
        int nLen = sql.length();

        char[] srcBuff = sql.toCharArray();
        StringBuilder retBuff = new StringBuilder((int) (nLen * 1.5D));
        for (int i = 0; i < nLen; ++i) {
            char cTemp = srcBuff[i];
            switch (cTemp) {
                case '\'':
                    retBuff.append("''");
                    break;
                case ';':
                    boolean bSkip = false;
                    for (int j = i + 1; (j < nLen) && (!(bSkip)); ++j) {
                        char cTemp2 = srcBuff[j];
                        if (cTemp2 == ' ') {
                            continue;
                        }
                        if (cTemp2 == '&') {
                            retBuff.append(';');
                        }
                        bSkip = true;
                    }

                    if (!(bSkip)) {
                        retBuff.append(';');
                    }
                    break;
                default:
                    retBuff.append(cTemp);
            }

        }

        return retBuff.toString();
    }

    /**
     * contain field check
     *
     * @param field
     * @param fields
     * @return true if fields is empty or contains field, ignore
     */
    protected static boolean containsIgnoreCase(String field, Set<String> fields) {
        return fields == null || fields.isEmpty() || fields.contains(getFieldAlias(field).toLowerCase(Locale.ROOT));
    }

    /**
     * deal with alias and `` quotes
     *
     * @param field
     * @return
     */
    public static String getFieldAlias(String field) {
        field = field.strip();

        if (!field.startsWith("`") && StringUtils.containsWhitespace(field)) {
            StringTokenizer tokenizer = new StringTokenizer(field);
            while (tokenizer.hasMoreTokens()) {
                field = tokenizer.nextToken();
            }
        } else if (field.startsWith("`")) {
            field = StringUtils.strip("`");
        }
        return field;
    }

    public static String getPagingSql(String driverName, String selectSql, int startNum, int endNum) {

        int dbtype = detectDbType(driverName);

        return getPagingSql(selectSql, startNum, endNum, dbtype);

    }

    private static String getPagingSql(String selectSql, int startNum, int endNum, int dbtype) {
        if (dbtype == DERBY) {
            return getPagingSqlDerby(selectSql, startNum, endNum);
        } else if (dbtype == SQLSERVER) {
            return getPagingSqlSqlServer(selectSql, startNum, endNum);
        } else if (dbtype == SQLSERVER_2005_AND_UP) {
            return getPagingSqlSqlServer2005(selectSql, startNum, endNum);
        } else if (dbtype == SQLSERVER_2010_AND_UP) {
            return getPagingSqlSqlServer2010(selectSql, startNum, endNum);
        } else if (dbtype == MYSQL || dbtype == H2) {
            return getPagingSqlMySql(selectSql, startNum, endNum);
        } else if (dbtype == POSTGRESQL) {
            return getPagingSqlPostgresql(selectSql, startNum, endNum);
        } else if (dbtype == ACCESS) {
            return getPagingSqlSqlServer(selectSql, startNum, endNum);
        } else {
            return getPagingSqlOracle(selectSql, startNum, endNum);
        }
    }

    public AfterChangeEventHandler<T, ID> getAfterChangeEventHandler() {
        return afterChangeEventHandler;
    }

    public void setAfterChangeEventHandler(AfterChangeEventHandler<T, ID> afterChangeEventHandler) {
        this.afterChangeEventHandler = afterChangeEventHandler;
    }

    @Autowired(required = false)
    public void setChangeEventConsumer(ChangeEventConsumer<ID> changeEvent) {
        LogAfterChangeEventHandler<T, ID> eventHandler = new LogAfterChangeEventHandler<>(this);
        eventHandler.setHandler(changeEvent);
        this.afterChangeEventHandler = eventHandler;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    public List<String> getAllFields() {
        return emptyList();
    }

    /**
     * <p>beforeChange.</p>
     *
     * @param info a T object.
     */
    protected void beforeChange(T info) {
        pojoValidator.asureValid(info);
        if (info instanceof IModifiedTime) {
            ((IModifiedTime) info).setModifiedTime(new Timestamp(
                    System.currentTimeMillis()
            ));
        }
    }

    private void beforeChange(T info, long time) {
        pojoValidator.asureValid(info);
        if (info instanceof IModifiedTime) {
            ((IModifiedTime) info).setModifiedTime(new Timestamp(
                    time
            ));
        }
    }

    private void beforeChange(T[] infos) {
        long time = System.currentTimeMillis();
        for (T info : infos) {
            beforeChange(info, time);
        }
    }
    private void beforeChange(List<T> infos) {
        long time = System.currentTimeMillis();
        for (T info : infos) {
            beforeChange(info, time);
        }
    }

    /**
     * <p>Getter for the field <code>dbHelper</code>.</p>
     *
     * @return a {@link IDbHelper} object.
     */
    @Override
    public IDbHelper getDbHelper() {
        return dbHelper;
    }

    /**
     * <p>create.</p>
     *
     * @return a T object.
     */
    @Override
    public T create() {
        return null;
    }

    /**
     * <p>getCount.</p>
     *
     * @param tableName   a {@link java.lang.String} object.
     * @param whereClause a {@link java.lang.String} object.
     * @return a int.
     */
    public int getCount(String tableName, String whereClause) {

        String strSql = SELECT_COUNT_FROM + addAlias(tableName) + " " + whereClause;
        if (this.getDbType() == ORACLE) {
            return ((BigDecimal) getDbHelper().executeScalar(strSql)).intValue();
        } else {
            return Integer.parseInt(getDbHelper().executeScalar(strSql).toString());
        }
    }

    /**
     * <p>getPagingSql.</p>
     *
     * @param selectSql a {@link java.lang.String} object.
     * @param startNum  a int.
     * @param endNum    a int.
     * @return a {@link java.lang.String} object.
     */
    public String getPagingSql(String selectSql, int startNum, int endNum) {

        detectDbType();

        return getPagingSql(selectSql, startNum, endNum, dbtype);

    }

    private String getDateTimeSql(String dt, boolean isTimestamp) {
        detectDbType();
        if (dbtype == DERBY) {
            if (isTimestamp) {
                return getTimestampSqlDerby(dt);
            }
            return getDateSqlDerby(dt);
        } else if (dbtype == SQLSERVER || dbtype == SQLSERVER_2005_AND_UP || dbtype == SQLSERVER_2010_AND_UP || dbtype == ACCESS) {
            return getDateSqlServer(dt);
        } else if (dbtype == MYSQL) {
            return getDateSqlMySql(dt);
        } else {
            return getDateSqlOracle(dt);
        }
    }

    /**
     * <p>getDateSql.</p>
     *
     * @param dt a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getDateSql(String dt) {
        return getDateTimeSql(dt, false);
    }

    /**
     * <p>getTimestampSql.</p>
     *
     * @param dt a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getTimestampSql(String dt) {
        return getDateTimeSql(dt, true);
    }

    /**
     * <p>getDateSql.</p>
     *
     * @param dt a {@link java.util.Date} object.
     * @return a {@link java.lang.String} object.
     */
    public String getDateSql(Date dt) {
        detectDbType();
        if (dbtype == DERBY) {
            return getDateSqlDerby(dt);
        } else if (dbtype == SQLSERVER || dbtype == SQLSERVER_2005_AND_UP|| dbtype == SQLSERVER_2010_AND_UP || dbtype == ACCESS) {
            return getDateSqlServer(dt);
        } else if (dbtype == MYSQL) {
            return getDateSqlMySql(dt);
        } else if (dbtype == POSTGRESQL) {
            return getDateSqlPostgresql(dt);
        } else {
            return getDateSqlOracle(dt);
        }

    }

    /**
     * <p>getDBTYPE.</p>
     *
     * @return a int.
     */
    public int getDbType() {
        detectDbType();
        return dbtype;
    }

    /**
     * <p>getKeyName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public abstract String getKeyName();

    /**
     * {@inheritDoc}
     * <p>
     * Counts the number of entries for this table in the database.
     */
    @Override
    public int count(String where) {
        return getCount(getTableName(), where);
    }

    /**
     * <p>count.</p>
     *
     * @return a int.
     */
    public int count() {
        return getCount(getTableName(), "");
    }

    /**
     * <p>count.</p>
     *
     * @param sql    a {@link java.lang.String} object.
     * @param params a {@link java.lang.Object} object.
     * @return a int.
     */
    public int count(String sql, Object[] params) {
        Object o = this.getDbHelper().executeScalar(sql, params);
        return o == null ? 0 : Integer.parseInt(o.toString());
    }

    /**
     * <p>exists.</p>
     *
     * @param where  a {@link java.lang.String} object.
     * @param params a {@link java.lang.Object} object.
     * @return a boolean.
     */
    public boolean exists(String where, Object[] params) {
        Object o = this.getDbHelper().executeScalar("select 1 from " + getTableName() + " " + where, params);
        return o != null && Integer.parseInt(o.toString()) == 1;
    }


    public boolean exists(String tableName, String where, Object[] params) {
        Object o = this.getDbHelper().executeScalar("select 1 from " + getTableName(tableName) + " " + where, params);
        return o != null && Integer.parseInt(o.toString()) == 1;
    }

    /**
     * <p>countWhere.</p>
     *
     * @param where  a {@link java.lang.String} object.
     * @param params a {@link java.lang.Object} object.
     * @return a int.
     */
    @Override
    public int countWhere(String where, Object[] params) {
        return countWhere(null, where, params);
    }

    public int countWhere(String tableName, String where, Object[] params) {
        String query = SELECT_COUNT_FROM + getTableName(tableName) + " " + where;
        Object o = this.getDbHelper().executeScalar(query, params);
        return o == null ? 0 : Integer.parseInt(o.toString());
    }

    /**
     * 通用更新函数
     *
     * @param fieldNames  字段名
     * @param values      参数对象数组，因无参数类型判断，不建议用空值
     * @param whereClause 条件语句，可以加“?”形参数
     * @param whereParams 条件语句中包含的参数
     * @param cn          用于事务处理的数据库连接
     * @return 更新记录数
     */
    public int update(String[] fieldNames, Object[] values, String whereClause, Object[] whereParams, Connection cn) {
        // 如何获取更新数据？
        // 可能需要二次查询，通过 where 查主键id?
        List<Object> allParams = new ArrayList<>();
        String query = getUpdateSql(fieldNames, values, whereClause, whereParams, allParams);
        int res;
        Object[] params = allParams.toArray();

        if (cn == null) {
            res = getDbHelper().executeNoQuery(query, params);
        } else {
            res = getDbHelper().executeNoQuery(query, params, cn);
        }
        afterUpdate(params, query, res);
        return res;
    }

    private void afterUpdate(Object[] params, String query, int res) {
        if (res > 0 && afterChangeEventHandler != null) {
            afterChangeEventHandler.afterChange(query, params, ChangeType.UPDATE, res);
        }
    }

    private void afterUpdate(T[] infos, boolean res) {
        if (res && afterChangeEventHandler != null) {
            afterChangeEventHandler.afterChange(infos, ChangeType.UPDATE);
        }
    }

    private void afterUpdate(List<T> infos, boolean res) {
        if (res && afterChangeEventHandler != null) {
            afterChangeEventHandler.afterChange(infos, ChangeType.UPDATE);
        }
    }

    private void afterUpdate(T infos, boolean res) {
        if (res && afterChangeEventHandler != null) {
            afterChangeEventHandler.afterChange(infos, ChangeType.UPDATE);
        }
    }


    /**
     * <p>update.</p>
     *
     * @param fieldNames  an array of {@link java.lang.String} objects.
     * @param whereClause a {@link java.lang.String} object.
     * @param params      an array of {@link java.lang.Object} objects.
     * @return a int.
     */
    public int update(String[] fieldNames, String whereClause, Object[] params) {
        return update(fieldNames, params, whereClause, new Object[]{}, null);
    }

    /**
     * <p>getUpdateSql.</p>
     *
     * @param fieldNames  an array of {@link java.lang.String} objects.
     * @param whereClause a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @deprecated Use {@link #getUpdateSql with params} instead
     */
    @Deprecated
    protected String getUpdateSql(String[] fieldNames, String whereClause) {
        if (fieldNames == null || fieldNames.length == 0) {
            throw new DbException("参数fieldNames不能为空");
        }

        String res = StringUtils.join(fieldNames, "=?, ") + "=? ";

        return "update " + this.getTableName() + " set " + res + whereClause;
    }

    protected String getUpdateSql(String[] fields, Object[] values, String whereClause, Object[] whereParams, List<Object> allParams) {
        if (fields == null || fields.length == 0) {
            throw new DbException("参数fieldNames不能为空");
        }

        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String field : fields) {
            Object val = values[i];
            if (val instanceof SqlString) {
                sb.append(field).append(" = ").append(val);
            } else {
                sb.append(field).append(" = ? ");
                params.add(val);
            }
            i++;
            if (i < fields.length) {
                sb.append(",");
            }
        }

        if (sb.lastIndexOf(",") == sb.length() - 1) {
            sb.deleteCharAt(sb.length() - 1);
        }

        allParams.addAll(params);
        Collections.addAll(allParams, whereParams);

        return "update " + this.getTableName() + " set " + sb + whereClause;
    }

    /**
     * 获取单个数据库对象, 并直接对 receiver的属性赋值
     *
     * @param <K>      期待类型
     * @param sql      sql语包
     * @param params   sql语句所需参数
     * @param receiver 要赋值的对象
     * @return receiver 承载查询结果的java bean
     */
    public @NotNull <K> K getSingleObject(String sql, Object[] params, @NotNull K receiver) {

        List<Map<String, Object>> v = this.getDbHelper().executeQuery(sql, params);
        if (v.isEmpty()) {
            return receiver;
        }
        Map<String, Object> s = v.get(0);
        return map2object(s, receiver);
    }

    /**
     * <p>selectToObjects.</p>
     *
     * @param sql       a {@link java.lang.String} object.
     * @param params    an array of {@link java.lang.Object} objects.
     * @param receivers an array of {@link java.lang.Object} objects.
     */
    public void selectToObjects(String sql, Object[] params, Object[] receivers) {
        List<Map<String, Object>> v = this.getDbHelper().executeQuery(sql, params);
        if (v.isEmpty()) {
            return;
        }
        Map<String, Object> s = v.get(0);
        for (Object target : receivers) {
            map2object(s, target);
        }
    }

    /**
     * <p>map2object.</p>
     *
     * @param h        a {@link java.util.Map} object.
     * @param receiver a K object.
     * @param <K>      a K object.
     * @return a K object.
     */
    protected @NotNull <K> K map2object(Map<String, Object> h, @NotNull K receiver) {
        BeanMap b = new BeanMap(receiver);

        Iterator<Entry<Object, Object>> iter = b.entryIterator();
        while (iter.hasNext()) {
            Entry<?, ?> e = iter.next();
            String hkey = e.getKey().toString();
            Object value;
            if (h.containsKey(hkey)) {
                value = h.get(hkey);
            } else {
                hkey = hkey.toUpperCase();
                if (h.containsKey(hkey)) {
                    value = h.get(hkey);
                } else {
                    continue;
                }
            }

            Class<?> type = b.getType(e.getKey().toString());
            value = reduceValueType(type, value);
            try {
                b.put(e.getKey(), value);
            } catch (IllegalArgumentException ex) {
                LOG.error("类型错误:key=" + e.getKey() + ", value=" + value + " instanceof " + value.getClass()
                        + ",but expected " + b.getType(e.getKey().toString()));
                throw ex;
            }
        }
        b.setBean(receiver);
        return receiver;
    }

    private Object reduceValueType(@SuppressWarnings("rawtypes") Class type, Object value) {
        // 各种日期型，整形认为是相同的

        if (value instanceof Integer && type == Long.class) {
            value = Long.valueOf((Integer) value);
        } else if (value instanceof Integer && type == Short.class) {
            value = ((Integer) value).shortValue();
        } else if (value instanceof java.util.Date && type == Timestamp.class) {
            value = new Timestamp(((java.util.Date) value).getTime());
        } else if (value instanceof Timestamp && type == java.util.Date.class) {
            value = new Date(((Timestamp) value).getTime());
        } else if (value instanceof Timestamp && type == java.sql.Date.class) {
            value = new java.sql.Date(((Timestamp) value).getTime());
        }
        return value;
    }

    /**
     * <p>addAlias.</p>
     *
     * @param tableName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    protected String addAlias(String tableName) {
        // hack the
        // " com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException:
        // Every derived table must have its own alias]"
        if (this.getDbType() == MYSQL && StringUtils.startsWithIgnoreCase(tableName.trim(), "select ")) {
            tableName = "(" + tableName + ") table_alias_in_base_dao";
        }
        return tableName;
    }

    private void detectDbType() {
        if (dbtype == UNKNOWN) {
            if (getDbHelper() != null) {
                try (Connection cn = getDbHelper().getConnection()) {

                    dbtype = detectDbType(cn.getMetaData().getDriverName());
                } catch (SQLException exception) {
                    throw new DbException(exception);
                }
            } else {
                throw new DbException("DbHelper not initialized!");
            }
        }
    }

    /**
     * <p>executeTransaction.</p>
     *
     * @param sqlObjs an array of {@link SqlObject} objects.
     * @return a boolean.
     */
    public boolean executeTransaction(SqlObject[] sqlObjs) {

        Connection cn = null;
        try {
            cn = getDbHelper().getConnection();
            cn.setAutoCommit(false);
            for (SqlObject sqlObj : sqlObjs) {
                int res = getDbHelper().executeNoQuery(sqlObj.getSqlString(), sqlObj.getParameters(), cn);

                if (sqlObj.getExpectedResult() > 0 && sqlObj.getExpectedResult() != res) {
                    cn.rollback();
                    return false;
                }
            }

            cn.commit();
            return true;
        } catch (SQLException sqlException) {
            try {
                cn.rollback();
            } catch (Exception e) {
                LOG.warn("rollback", e);
            }
            throw new DbException(sqlException);
        } finally {
            try {
                if (cn != null) {
                    cn.setAutoCommit(true);
                }
            } catch (Exception e) {
                LOG.warn("setAutoCommit", e);
            }

            DbUtils.closeQuietly(cn);
        }

    }

    /**
     * <p>convertCaseSensitive.</p>
     *
     * @param <K>      期待类型
     * @param h        源数据
     * @param receiver 目标对象
     * @return 目标对象
     * @throws java.lang.IllegalAccessException            无法拷贝属性
     * @throws java.lang.reflect.InvocationTargetException 无法拷贝属性
     * @throws java.lang.NoSuchMethodException             无法拷贝属性
     */
    protected @NotNull <K> K convertCaseSensitive(Map<String, Object> h, @NotNull K receiver)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // access properties as Map
        Map<String, String> properties = BeanUtils.describe(receiver);
        Map<String, Object> values = new HashMap<>(h.size() * 2);
        for (Map.Entry<String, ?> e : properties.entrySet()) {
            Object o = h.get(e.getKey());
            values.put(e.getKey(), o);
        }
        PropertyUtils.copyProperties(receiver, values);
        return receiver;
    }

    /**
     * <p>convert.</p>
     *
     * @param <K>      期待类型
     * @param h        源数据
     * @param receiver 目标对象
     * @return 目标对象
     * @throws java.lang.IllegalAccessException 无法拷贝属性
     * @deprecated use convertCaseSensitive
     */
    @Deprecated
    protected <K> K convert(Map<String, Object> h, K receiver) throws IllegalAccessException {
        BeanUtilsBean b = BeanUtilsBean.getInstance();

        // 因大小写的问题,不能直接传入全部属性

        for (Entry<String, Object> entry : h.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 生成对象都用LONG型,
            if (value instanceof Integer) {
                value = Long.valueOf((Integer) value);
            }

            try {
                if (dbtype == DERBY) {
                    key = key.toLowerCase(Locale.ROOT);
                }
                b.copyProperty(receiver, CaseUtils.toCamelCase(key, false, '_'), value);
            } catch (IllegalAccessException e) {
                LOG.error("类型错误:key=" + key + ", value=" + value + " instanceof " + value.getClass()
                        + ", ask xwx@live.cn to add a convertor for this type.");
                throw e;
            } catch (InvocationTargetException e) {
                LOG.error("类型错误:key=" + key + ", value=" + value + " instanceof " + value.getClass()
                        + ", ask xwx@live.cn to add a convertor for this type.");
                LOG.error("copyProperty", e);
            }
        }

        return receiver;
    }

    /**
     * <p>select.</p>
     *
     * @param select      a {@link java.lang.String} object.
     * @param whereClause a {@link java.lang.String} object.
     * @param orderBy     a {@link java.lang.String} object.
     * @param params      an array of {@link java.lang.Object} objects.
     * @param rowStart    a int.
     * @param rowEnd      a int.
     * @param clz         a {@link java.lang.Class} object.
     * @param <K>         a K object.
     * @return a {@link java.util.List} object.
     */
    public @NotNull <K> List<K> select(String select, String whereClause, String orderBy, Object[] params, int rowStart,
                                       int rowEnd, Class<K> clz) {
        return select(select, whereClause, orderBy, params, rowStart, rowEnd, clz, false);
    }

    public @NotNull <K> List<K> select(String select, String whereClause, String orderBy, Object[] params, int rowStart,
                                       int rowEnd, Class<K> clz, boolean keepCase) {

        String selectSql = select + " " + whereClause + " " + orderBy;

        List<Map<String, Object>> v = getDbHelper().executeQuery(this.getPagingSql(selectSql, rowStart, rowEnd),
                params);

        List<K> v1 = new ArrayList<>();
        if (BUILT_IN_SET.contains(clz)) {
            for (Map<String, Object> h : v) {
                v1.add(clz.cast(h.values().toArray()[0]));
            }
        } else {
            for (Map<String, Object> h : v) {
                try {
                    if (keepCase && getDbType()!= DERBY) { // Derby column name all BIG LETTERS
                        v1.add(convertCaseSensitive(h, clz.getConstructor().newInstance()));
                    } else {
                        v1.add(convert(h, clz.getConstructor().newInstance()));
                    }
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                         InstantiationException e) {
                    throw new DbException(e);
                }
            }
        }

        return v1;
    }

    /**
     * <p>select.</p>
     *
     * @param sql         a {@link java.lang.String} object.
     * @param whereClause a {@link java.lang.String} object.
     * @param orderBy     a {@link java.lang.String} object.
     * @param params      an array of {@link java.lang.Object} objects.
     * @param rowStart    a int.
     * @param rowEnd      a int.
     * @return a {@link java.util.List} object.
     */
    protected @NotNull
    List<Map<String, Object>> select(String sql, String whereClause, String orderBy,
                                     Object[] params, int rowStart, int rowEnd) {
        String selectSql = sql + " " + whereClause + " " + (this.getDbType() == DERBY ? "" : orderBy);
        return getDbHelper().executeQuery(this.getPagingSql(selectSql, rowStart, rowEnd), params);
    }

    /**
     * 查询返回通用类型
     *
     * @param whereClause   条件，如果是mysql数据库，会针对*优化, 如 "where id&gt;?"
     * @param params        参数，如 new Object[]{ 1000 }
     * @param orderByClause 排序条件, 如 "order by id desc"
     * @param beginNum      起始行,如 0
     * @param endNum        截止行，如 10000
     * @return 数据集
     */
    @Override
    public @NotNull
    List<T> select(String whereClause, Object[] params, String orderByClause, int beginNum, int endNum) {
        String query = getSelectAllFromTable() + whereClause;
        if (this.getDbType() != DERBY) {
            query += " " + orderByClause;
        }
        query = getPagingSql(query, beginNum, endNum);
        return getDbHelper().executeQuery(query, params, new RowMapperResultReader<>(this));
    }

    /**
     * 针对MySql的二步查询优化
     *
     * @param whereClause   whereClause
     * @param params        params
     * @param orderByClause orderbyClause
     * @param beginNum      bingNum
     * @param endNum        endNum
     * @return 结果集
     */
    protected @NotNull
    List<T> select2step(String whereClause, Object[] params, String orderByClause, int beginNum,
                        int endNum) {
        String query = "select " + getKeyName() + " from " + getTableName() + " " + whereClause;
        if (this.getDbType() != DERBY) {
            query += orderByClause;
        }
        query = getPagingSql(query, beginNum, endNum);
        List<ID> ids = getDbHelper().executeQuery(query, params, new RowMapperResultReader<>((rs, index, fieldsSet) -> {
            try {
                return (ID) rs.getObject(1);
            } catch (SQLException e) {
                throw new DbException(e);
            }
        }));
        return selectByKeys(ids, orderByClause);
    }

    /**
     * TODO fieldList该方法目前仅用于与其他表联表查询，传进来的数据没有经过检查
     *
     * @param fieldList     fieldList 返回的列必须与
     *                      RowMapper.mapRow里的列相对应，否则报错，该方法目前仅用于与其他表联表查询，fieldList可传入类似t2.*形式的字符串
     * @param whereClause   where语句，多表联查时，可以是 left join table2 t2 on t2.id=t1.id where
     *                      ....
     * @param params        参数列表，用于替换所有的？占位符
     * @param orderByClause 排序语句，可为空
     * @param beginNum      起始行
     * @param endNum        截止行
     * @return 结果集
     */
    public @NotNull
    List<T> selectWithJoin(String fieldList, String whereClause, Object[] params, String orderByClause,
                           int beginNum, int endNum, String tableName) {
        String tableNameOrSelect = useTablenameOrSelect(fieldList, tableName);

        String query =  tableNameOrSelect+ " " + whereClause;
        if (this.getDbType() != DERBY) {
            query += orderByClause;
        }
        query = getPagingSql(query, beginNum, endNum);
        return getDbHelper().executeQuery(query, params, new RowMapperResultReader<>(this), ActiveRecord.toFieldsSet(fieldList));

    }

    public String useTablenameOrSelect(String fieldList, String tableName) {
        String tableNameOrSelect = getTableName(tableName);
        if (!tableNameOrSelect.toUpperCase().startsWith("SELECT ")) {
            tableNameOrSelect = "select " + fieldList + " from " + tableNameOrSelect;
        }
        return tableNameOrSelect;
    }

    public @NotNull
    List<T> selectWithJoin(String fieldList, String whereClause, Object[] params, String orderByClause,
                           int beginNum, int endNum) {
        return selectWithJoin(fieldList, whereClause, params, orderByClause,
                beginNum, endNum, null);
    }

    public @NotNull
    List<T> select(String whereClause, String orderByClause, int beginNum, int endNum) {
        return select(whereClause, null, orderByClause, beginNum, endNum);
    }

    /**
     * Select from the database for table
     *
     * @param id 主键ID
     * @return 数据对象pojo
     */
    public T selectByKey(@NotNull ID id) {
        String query = SELECT_FROM + getTableName() + WHERE + getKeyName() + "=?";
        List<T> info = getDbHelper().executeQuery(query, new Object[]{id}, new RowMapperResultReader<>(this));
        if (!info.isEmpty()) {
            return info.get(0);
        }
        return null;
    }

    /**
     * Select from the database for table "Agent_form"
     *
     * @param ids 主键数组
     * @return List of pojo
     */
    public @NotNull
    List<T> selectByKeys(ID[] ids) {
        if (ids == null || ids.length <= 0) {
            return emptyList();
        }

        String query = getSelectAllWhere() + getKeyName() + " in ( "
                + StringUtils.repeat("?", ",", ids.length) + ")";
        return getDbHelper().executeQuery(query, ids, new RowMapperResultReader<>(this));
    }

    private String getSelectAllWhere() {
        return getSelectAllFromTable() + WHERE;
    }

    /**
     * <p>selectByKeys.</p>
     *
     * @param ids           a {@link java.util.List} object.
     * @param orderbyClause a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    protected @NotNull
    List<T> selectByKeys(List<ID> ids, String orderbyClause) {
        if (ids == null || ids.isEmpty()) {
            return emptyList();
        }

        String query = getSelectAllWhere() + getKeyName() + " in ( "
                + StringUtils.repeat("?", ",", ids.size()) + ") " + orderbyClause;
        return getDbHelper().executeQuery(query, ids.toArray(), new RowMapperResultReader<>(this));
    }

    /**
     * <p>deleteByKey.</p>
     *
     * @param id 主键
     * @param cn 数据库连接
     * @return true成功删除
     * Deletes from the database for table
     */
    public boolean deleteByKey(ID id, Connection cn) {
        String query = getDeleteWhere() + getKeyName() + "=?";
        boolean res = 1 <= getDbHelper().executeNoQuery(query, new Object[]{id}, cn);

        afterDelete(id, res);
        return res;
    }

    private void afterDelete(ID id, boolean res) {
        if (res && afterChangeEventHandler != null) {
            afterChangeEventHandler.afterChangeByKey(id, ChangeType.DELETE);
        }
    }

    private void afterDelete(ID[] id, boolean res) {
        if (res && afterChangeEventHandler != null) {
            afterChangeEventHandler.afterChangeByKey(id, ChangeType.DELETE);
        }
    }

    private void afterDelete(String query, Object[] params, int res) {
        if (res > 0 && afterChangeEventHandler != null) {
            afterChangeEventHandler.afterChange(query, params, ChangeType.DELETE, res);
        }
    }

    private String getDeleteWhere() {
        return "delete from " + getTableName() + WHERE;
    }

    private String getDeleteWhere(String where) {
        return "delete from " + getTableName() + " " + where;
    }


    public boolean delete(String where, Object[] whereParams, Connection cn) {
        String sql = getDeleteWhere(where);
        int res;
        if (cn == null) {
            res = getDbHelper().executeNoQuery(sql, whereParams);
        } else {
            res = getDbHelper().executeNoQuery(sql, whereParams, cn);
        }

        afterDelete(where, whereParams, res);
        return res > 0;
    }

    /**
     * <p>deleteByKey.</p>
     *
     * @param id 主键
     * @return true 成功删除
     * Deletes from the database for table
     */
    @Override
    public boolean deleteByKey(ID id) {
        String query = getDeleteWhere() + getKeyName() + "=?";
        boolean res = 1 <= getDbHelper().executeNoQuery(query, new Object[]{id});

        afterDelete(id, res);
        return res;
    }

    /**
     * <p>deleteByKeys.</p>
     *
     * @param id an array of V[] objects.
     * @return a boolean.
     */
    public boolean deleteByKeys(ID[] id) {
        String query = getDeleteWhere() + makeInPlaceHolder(getKeyName(), id.length);
        boolean res = 1 <= getDbHelper().executeNoQuery(query, id);
        afterDelete(id, res);
        return res;
    }

    /**
     * <p>makeInPlaceHolder.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param size      a int.
     * @return a {@link java.lang.String} object.
     */
    protected String makeInPlaceHolder(String fieldName, int size) {
        return fieldName + " in (" + StringUtils.repeat("?", ",", size) + ")";
    }

    /**
     * <p>countByKey.</p>
     *
     * @param id 主键
     *           <p>
     *           Counts the number of entries for this table in the database.
     * @return 总数
     */
    public int countByKey(ID id) {
        String query = SELECT_COUNT_FROM + getTableName() + WHERE + getKeyName() + "=?";
        Object o = getDbHelper().executeScalar(query, new Object[]{id});
        return o == null ? 0 : Integer.parseInt(o.toString());
    }

    /**
     * <p>countLikeKey.</p>
     *
     * @param id a V object.
     * @return a int.
     */
    public int countLikeKey(ID id) {
        return countByKey(id);
    }

    /**
     * <p>insertAutoNewKey.</p>
     *
     * @param info a T object.
     * @return a boolean.
     */
    @Override
    public boolean insertAutoNewKey(T info) {
        try {
            setId(info, getNextKey());
            return this.insert(info);
        } catch (DbException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                // Duplicate entry '10061' for key 'PRIMARY'
                String message = e.getMessage();

                if (message.contains("Duplicate") && message.contains("PRIMARY")) {
                    repairAndTry(info, 1);
                }
            }
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Select from the database for table "agent_form" by unique ids
     */
    @Override
    public @NotNull
    Map<String, T> selectByIds(String uniquePropertyName, List<String> uniquePropertyValues) {

        String ids = StringUtils.join(uniquePropertyValues, ",");

        java.util.Collection<T> infos = selectAllWhere(
                WHERE + uniquePropertyName + " in(" + filterForSql(ids) + ")");
        Map<String, T> map = new HashMap<>(16);
        for (T info : infos) {
            map.put("" + getId(info), info);
        }
        return map;
    }

    /**
     * <p>selectAllWhere.</p>
     *
     * @param where a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public @NotNull
    List<T> selectAllWhere(String where) {
        String query = getSelectAllFromTable() + where;
        return getDbHelper().executeQuery(query, null, new RowMapperResultReader<>(this));
    }

    /**
     * <p>selectAllWhere.</p>
     *
     * @param where   a {@link java.lang.String} object.
     * @param objects a {@link java.lang.Object} object.
     * @return a {@link java.util.List} object.
     */
    public @NotNull
    List<T> selectAllWhere(String where, Object... objects) {
        String query = getSelectAllFromTable() + where;
        return getDbHelper().executeQuery(query, objects, new RowMapperResultReader<>(this));
    }

    /**
     * <p>selectAllByStep.</p>
     *
     * @param where   a {@link java.lang.String} object.
     * @param orderBy a {@link java.lang.String} object.
     * @param stepMax a int.
     * @param params  an array of {@link java.lang.Object} objects.
     * @return a {@link java.util.List} object.
     */
    public @NotNull
    List<T> selectAllByStep(String where, String orderBy, int stepMax, Object[] params) {
        final List<T> result = new ArrayList<>();
        processAllByStep(where, orderBy, stepMax, result::addAll, params);
        return result;
    }

    /**
     * <p>processAllByStep.</p>
     *
     * @param where   a {@link java.lang.String} object.
     * @param orderBy a {@link java.lang.String} object.
     * @param stepMax a int.
     * @param handler a {@link ProcessHandler} object.
     * @param params  an array of {@link java.lang.Object} objects.
     */
    public void processAllByStep(String where, String orderBy, int stepMax, ProcessHandler<T> handler,
                                 Object[] params) {
        processAllByStep(where, orderBy, stepMax, handler, params, false);
    }

    /**
     * 批处理数据表记录, 当处理结果不影响获取条件时，alwaysFromBeginning应为false
     *
     * @param where               条件字符串，应为形如" where site_id=0 "的字符串，前后需加空格
     * @param orderBy             排序方法应为形如" order by id "的字符串，前后需加空格
     * @param stepMax             每次获取最多记录数，返回结果数据表记录可能{@code site()<=step_max }
     * @param handler             真正用于处理返回结果的接口
     * @param params              sql参数, 为 where 和 order_by 里的？点位符对应参数
     * @param alwaysFromBeginning 当处理结果不影响获取条件时， alwaysFromBeginning 应为 false,
     *                            否则会死循环，当处理结果使得最终会不再返回结果集时，才能为 true
     */
    public void processAllByStep(String where, String orderBy, int stepMax, ProcessHandler<T> handler,
                                 Object[] params, boolean alwaysFromBeginning) {
        processAllByStep(where, orderBy, stepMax, handler, params,  alwaysFromBeginning, List::isEmpty);
    }

    public void processAllByStep(String where, String orderBy, int stepMax, ProcessHandler<T> handler,
                                 Object[] params, boolean alwaysFromBeginning, Function<List<T>, Boolean> finished ) {
        List<T> list;
        int beginNum = 0;
        for (list = select(where, params, orderBy, beginNum, beginNum + stepMax);
             !finished.apply(list);
             list = select(where, params, orderBy, beginNum, beginNum + stepMax)) {
            if (!alwaysFromBeginning) {
                beginNum = beginNum + stepMax;
            }
            LOG.debug("the size of " + this.getTableName() + " selectAllWhereByStep:" + list.size());
            try {
                handler.process(list);
            } catch (SQLException e) {
                throw new DbException(e);
            }
        }
    }

    /**
     * Select from the database for table "Agent_form"
     *
     * @return list of pojo
     */
    public @NotNull
    List<T> selectAll() {
        String query = getSelectAllFromTable();
        return getDbHelper().executeQuery(query, null, new RowMapperResultReader<>(this));
    }

    protected String getSelectAllFromTable() {
        return SELECT_FROM + getTableName() + " ";
    }

    /**
     * <p>getLastInsertId.</p>
     *
     * @param cn a {@link java.sql.Connection} object.
     * @return a {@link java.lang.Long} object.
     */
    public Long getLastInsertId(Connection cn) {
        return Long.parseLong(this.getDbHelper().executeScalar(getLastInsertIdSql(), null, cn).toString());
    }

    private String getLastInsertIdSql() {
        detectDbType();
        if (dbtype == DERBY) {
            return "select last_insert_id()";
        } else if (dbtype == SQLSERVER || dbtype == SQLSERVER_2005_AND_UP || dbtype == SQLSERVER_2010_AND_UP || dbtype == ACCESS) {
            return "select scope_identity()";
        } else if (dbtype == MYSQL) {
            return "select last_insert_id()";
        } else {
            throw new DbException("getLastInsertId is not support for current dbtype=" + dbtype);
        }

    }

    /**
     * <p>select.</p>
     *
     * @param sql     a {@link java.lang.String} object.
     * @param where   a {@link java.lang.String} object.
     * @param orderBy a {@link java.lang.String} object.
     * @param params  an array of {@link java.lang.Object} objects.
     * @param start   a int.
     * @param rowEnd  a int.
     * @param clz     an array of {@link java.lang.Class} objects.
     * @return a {@link java.util.List} object.
     */
    protected @NotNull
    List<Object[]> select(String sql, String where, String orderBy, Object[] params, int start,
                          int rowEnd, Class[] clz) {
        List<Map<String, Object>> v = select(sql, where, orderBy, params, start, rowEnd);
        ArrayList<Object[]> res = new ArrayList<>();
        for (Map<String, Object> h : v) {
            Object[] rec = new Object[clz.length];
            for (int i = 0; i < clz.length; i++) {
                try {
                    rec[i] = clz[i].getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                         InvocationTargetException e) {
                    LOG.warn("newInstance", e);
                }
                this.map2object(h, rec[i]);
            }
            res.add(rec);
        }
        return res;
    }

    private boolean repairAndTry(T info, int tryCount) {
        if (tryCount++ > 3) {
            return false;
        }

        try {
            setId(info, getNextKey());
            return this.insert(info);
        } catch (DbException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {

                // Duplicate entry '10061' for key 'PRIMARY'
                String message = e.getMessage();

                if (message.contains("Duplicate") && message.contains("PRIMARY")) {

                    String key = getSequenceKeyName();
                    try {
                        long max = (long) this.ar().countValue(this.getKeyName(), GroupFunction.MAX).getValue();

                        SequenceUtils.getInstance().moveKeyValueToCurrent(key, max,
                                this.getDbHelper());
                        return repairAndTry(info, tryCount);
                    } catch (Exception ex) {
                        throw new DbException(ex);
                    }
                }
            }
        }
        return false;
    }

    /**
     * <p>getInsertSql.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected String getInsertSql() {
        throw new NotImplementedException();
    }

    /**
     * <p>getUpdateSql.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected String getUpdateSql() {
        throw new NotImplementedException();
    }

    /**
     * <p>getUpdateObjs.</p>
     *
     * @param info a T object.
     * @return an array of {@link java.lang.Object} objects.
     */
    protected Object[] getUpdateObjs(T info) {
        assert info != null;
        return new Object[0];
    }

    /**
     * <p>getInsertObjs.</p>
     *
     * @param info a T object.
     * @return an array of {@link java.lang.Object} objects.
     */
    protected Object[] getInsertObjs(T info) {
        assert info != null;
        throw new IllegalArgumentException("this method must override!");
    }

    public boolean insertKeepKey(T info){
        return insertKeepKey(info,"");
    }
    public boolean insertKeepKey(T info, String suffix) {

        boolean[]  has = new boolean[1];
        String insertSql = getInsertSqlWithId(has);
        List<Object> insertObjects = has[0] ? List.of(getInsertObjs(info)) : getInsertObjectsWithId(info);
        beforeChange(info);
        boolean res = 1 <= this.getDbHelper().executeNoQuery(insertSql+suffix, insertObjects.toArray());
        afterInsert(info, res);
        return res;
    }

    static  boolean matchField(String sql, String field){
        Pattern m = Pattern.compile("[(,]\\s*" + field + "\\s*[),]", CASE_INSENSITIVE|MULTILINE);
        return m.matcher(sql).find();
    }

    private String getInsertSqlWithId(boolean[] outHas) {

        String insertSql = this.getInsertSql();
        if(matchField(insertSql, getKeyName())) {
            return insertSql;
        }else{
            insertSql = insertSql.replace(") values (", ","
                    + this.getKeyName() + ") values (?,");
            outHas[0]  =true;
        }
        return insertSql;
    }

    private List<Object> getInsertObjectsWithId(T info) {
        List<Object> insertObjects = new ArrayList<>(Arrays.asList(this.getInsertObjs(info)));
        insertObjects.add(this.getId(info));
        return insertObjects;
    }

    /**
     * Updates the current object values into the database.
     *
     * @param info pojo
     * @return true if success
     */
    @Override
    public boolean updateByKey(T info) {
        beforeChange(info);

        boolean res = 1 == getDbHelper().executeNoQuery(getUpdateSql(), getUpdateObjs(info));
        afterUpdate(info, res);

        return res;
    }

    public boolean updateByKey(T info, String shardTableName) {
        beforeChange(info);

        return 1 == getDbHelper().executeNoQuery(shardNameStrategy(getUpdateSql(), shardTableName), getUpdateObjs(info));
    }

    /**
     * <p>updateByVersionAndKey.</p>
     *
     * @param info a T object.
     * @return a boolean.
     */
    public boolean updateByVersionAndKey(T info) {
        return updateByKey(info);
    }

    /**
     * Updates the current object values into the database.
     *
     * @param info pojo
     * @param cn   db connection
     * @return true if success
     */
    public boolean updateByKey(T info, Connection cn) {
        beforeChange(info);
        return 1 == getDbHelper().executeNoQuery(getUpdateSql(), getUpdateObjs(info), cn);
    }

    public boolean updateByKey(T info, Connection cn, String tableShardName) {
        beforeChange(info);
        return 1 == getDbHelper().executeNoQuery(shardNameStrategy(getUpdateSql(), tableShardName), getUpdateObjs(info), cn);
    }

    String shardNameStrategy(String sql, String shardTableName) {
        if(StringUtils.isEmpty(shardTableName)){
            return sql;
        }
        return sql.replaceAll(getTableName(), shardTableName);
    }

    /**
     * Inserts the info object values into the database.
     *
     * @param info pojo
     * @return true if success
     */
    @Override
    public boolean insert(T info) {
        beforeChange(info);
        boolean res = 1 == getDbHelper().executeNoQuery(getInsertSql(), getInsertObjs(info));

        afterInsert(info, res);
        return res;
    }

    private void afterInsert(T info, boolean res) {
        if (res && afterChangeEventHandler != null) {
            afterChangeEventHandler.afterChange(info, ChangeType.INSERT);
        }
    }

    private void afterInsert(T[] info, boolean res) {
        if (res && afterChangeEventHandler != null) {
            afterChangeEventHandler.afterChange(info, ChangeType.INSERT);
        }
    }

    private void afterInsert(T info, boolean res, String shardTableName) {
        if (res && afterChangeEventHandler != null) {
            afterChangeEventHandler.afterChange(info, ChangeType.INSERT, shardTableName);
        }
    }

    public boolean upsert(T info) {
        return insertKeepKey(info, getUpsertClause());
    }

    public boolean insert(T info, String tableShardName) {
        return insert(info, tableShardName,"");
    }

    public boolean insert(T info, String tableShardName, String upsertClause) {
        beforeChange(info);
        boolean res = 1 == getDbHelper().executeNoQuery(shardNameStrategy(getInsertSql(), tableShardName), getInsertObjs(info));

        afterInsert(info, res);
        return res;
    }

    /**
     * Inserts the info object values into the database.
     *
     * @param info pojo
     * @param cn   db connection
     * @return true if success
     */
    public boolean insert(T info, Connection cn) {
        beforeChange(info);
        boolean res = 1 == getDbHelper().executeNoQuery(getInsertSql(), getInsertObjs(info), cn);
        afterInsert(info, res);
        return res;
    }

    public boolean insert(T info, Connection cn, String shardTableName) {
        beforeChange(info);
        boolean res = 1 == getDbHelper().executeNoQuery(shardNameStrategy(getInsertSql(), shardTableName), getInsertObjs(info), cn);
        afterInsert(info, res);
        return res;
    }

    /**
     * <p>insertBatch.</p>
     *
     * @param infos an array of T[] objects.
     * @return a boolean.
     */
    public boolean insertBatch(T[] infos) {
        beforeChange(infos);

        boolean res = insertBatch(infos, null);
        afterInsert(infos, res);
        return res;
    }

    /**
     * <p>updateBatch.</p>
     *
     * @param infos an array of T[] objects.
     * @return a boolean.
     */
    public boolean updateBatch(T[] infos) {
        return updateBatch(infos, null);
    }
    public boolean updateBatch(List<T> infos) {
        return updateBatch(infos, null);
    }
    /**
     * Inserts the Agent object values into the database.
     *
     * @param infos pojos array
     * @param cn    db connection
     * @return true if all success
     */
    public boolean insertBatch(T[] infos, Connection cn) {
        beforeChange(infos);

        List<Object[]> a = Arrays.stream(infos).map(this::getInsertObjs).collect(Collectors.toList());

        boolean res = infos.length == getDbHelper().executeNoQueryBatch(getInsertSql(), a, cn);

        afterUpdate(infos, res);
        return res;
    }

    protected String getUpsertClause(){
        if(StringUtils.isEmpty(this.getKeyName())){
            throw new DbException("Table "+getTableName()+" has not a primary key for upsert!");
        }

        if(this.getDbType() == MYSQL ){
            String subquery = this.getUpdateSql();
            subquery = subquery.substring(subquery.indexOf(" set ")+4, subquery.indexOf(" where "));
           return " ON DUPLICATE KEY UPDATE "
                   +subquery.replaceAll("(\\w+)=(\\?)","$1=values($1)");
        }else if (this.getDbType() == POSTGRESQL){
           return "ON CONFLICT ("+this.getKeyName()+") DO UPDATE;";
        }else{
           throw new DbException(new SQLFeatureNotSupportedException("Current database not supported upsert natively:"+this.dbtype));
        }
    }
    public boolean upsertBatch(List<T> infos) {
        return insertBatchKeepKey(infos, "",true);
    }
    protected boolean upsertBatch(List<T> infos, String shardTableName) {
        return insertBatchKeepKey(infos, shardTableName,true);
    }

    public boolean insertBatch(List<T> infos) {
        return insertBatch(infos, null);
    }
    public boolean insertBatch(List<T> infos, Connection cn) {
        beforeChange(infos);
        List<Object[]> a = infos.stream().map(this::getInsertObjs).collect(Collectors.toList());
        boolean res = infos.size() == getDbHelper().executeNoQueryBatch(getInsertSql(), a, cn);
        afterUpdate(infos, res);
        return res;
    }


    public boolean insertBatchKeepKey(List<T> infos, String shardTableName, boolean useUpsert) {
        boolean[] res1 = new boolean[1];
        String insertSql = getInsertSqlWithId(res1);
        beforeChange(infos);
        boolean res = infos.size() <= this.getDbHelper().executeNoQueryBatch(shardNameStrategy(insertSql, shardTableName)
                + (useUpsert?getUpsertClause():""),
                infos.stream().map(info -> res1[0]?
                getInsertObjectsWithId(info).toArray(): getInsertObjs(info)).collect(Collectors.toList()));
        afterUpdate(infos, res);
        return res;
    }

    private boolean insertBatch(List<T> infos, Connection cn, String shardTableName) {
        beforeChange(infos);
        List<Object[]> a = infos.stream().map(this::getInsertObjs).collect(Collectors.toList());
        boolean res = infos.size() == getDbHelper().executeNoQueryBatch(shardNameStrategy(getInsertSql(), shardTableName), a, cn);
        afterUpdate(infos, res);
        return res;
    }
    /**
     * <p>updateBatch.</p>
     *
     * @param infos an array of T[] objects.
     * @param cn    a {@link java.sql.Connection} object.
     * @return a boolean.
     */
    public boolean updateBatch(T[] infos, Connection cn) {
        beforeChange(infos);

        List<Object[]> a = Arrays.stream(infos).map(this::getUpdateObjs).collect(Collectors.toList());

        boolean res = infos.length == getDbHelper().executeNoQueryBatch(getUpdateSql(), a, cn);

        afterUpdate(infos, res);
        return res;
    }
    public boolean updateBatch(List<T> infos, Connection cn) {
        beforeChange(infos);

        List<Object[]> a = infos.stream().map(this::getUpdateObjs).collect(Collectors.toList());

        boolean res = infos.size() == getDbHelper().executeNoQueryBatch(getUpdateSql(), a, cn);

        afterUpdate(infos, res);
        return res;
    }


    public boolean updateBatch(T[] infos, Connection cn, String shardTableName) {
        beforeChange(infos);

        List<Object[]> a = Arrays.stream(infos).map(this::getUpdateObjs).collect(Collectors.toList());

        return infos.length == getDbHelper().executeNoQueryBatch(shardNameStrategy(getUpdateSql(), shardTableName), a, cn);
    }

    /**
     * <p>ar.</p>
     *
     * @return a {@link ActiveRecord} object.
     */
    public ActiveRecord<T> ar() {
        return new ActiveRecordImpl<>(this);
    }

    /**
     * <p>ar.</p>
     *
     * @param cn a {@link java.sql.Connection} object.
     * @return a {@link ActiveRecord} object.
     */
    public ActiveRecord<T> ar(Connection cn) {
        return new ActiveRecordImpl<>(this, cn);
    }

    /**
     * <p>getIDs.</p>
     *
     * @param infos a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    public @NotNull
    List<ID> getIDs(List<T> infos) {
        List<ID> res = new ArrayList<>();
        for (T info : infos) {
            res.add(getId(info));
        }
        return res;
    }

    /**
     * <p>countGroupValue.</p>
     *
     * @param field         字段名
     * @param groupFunction 分组函数
     * @param where         条件语句
     * @param having        分组条件
     * @param groupBy       分组语句
     * @param params        参数
     * @return 返回统计的键值对列表
     * @deprecated use more generic  {@code <K> List<CountObject<K>> countGroupObject }
     */
    @Deprecated
    public @NotNull
    List<CountValue> countGroupValue(String field, String groupFunction, String where, String having,
                                     String[] groupBy, Object... params) {

        String query = SqlUtils.formGroupQuery(this.getTableName(), this.getKeyName(), field, groupFunction, where, having, groupBy);
        return getDbHelper().executeQuery(query, params,
                new RowMapperResultReader<>((rs, index, set) -> {
                    CountValue cs = new CountValue();
                    try {
                        cs.setCount(rs.getLong(1));
                        cs.setValue(rs.getDouble(2));
                        if (!(groupBy == null || groupBy.length == 0)) {
                            cs.setKey(rs.getString(3));
                        }
                        return cs;
                    } catch (SQLException e) {
                        throw new DbException(e);
                    }

                }));

    }

    /**
     * <p>countGroupObject.</p>
     *
     * @param field         字段名
     * @param groupFunction 分组函数
     * @param where         条件语句
     * @param having        分组条件
     * @param groupBy       分组语句
     * @param type          返回值类型
     * @param <K>           返回值类型
     * @param params        参数
     * @return 返回统计的键值对列表
     */
    public @NotNull <K> List<CountObject<K>> countGroupObject(String field, String groupFunction, String where, String having,
                                                              String[] groupBy, final Class<K> type, Object... params) {

        String query = SqlUtils.formGroupQuery(this.getTableName(), this.getKeyName(), field, groupFunction, where, having, groupBy);

        return getDbHelper().executeQuery(query, params,
                new RowMapperResultReader<>((rs, index, set) -> {
                    CountObject<K> cs = new CountObject<>();
                    try {
                        cs.setCount(rs.getLong(1));
                        cs.setValue(rs.getObject(2, type));
                        if (!(groupBy == null || groupBy.length == 0)) {
                            cs.setKey(rs.getString(3));
                        }
                        return cs;
                    } catch (SQLException e) {
                        throw new DbException(e);
                    }

                }));

    }

    /**
     * 获取主键最大的记录，仅用于方便测试
     *
     * @return 最近的表记录
     */
    public T getLast() {
        @SuppressWarnings("unchecked")
        ID id = (ID) this.ar().get("max(" + this.getKeyName() + ")");
        if (id == null) {
            return null;
        }
        return this.selectByKey(id);
    }

    /**
     * 获取主键最小的记录， 仅用于测试的目的
     *
     * @return 主键最小记录
     */
    public T getFirst() {
        @SuppressWarnings("unchecked")
        ID id = (ID) this.ar().get("min(" + this.getKeyName() + ")");
        if (id == null) {
            return null;
        }
        return this.selectByKey(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ID getNextKey() {
        return (ID) (Long.valueOf(SequenceUtils.getInstance()
                .getNextKeyValue(getSequenceKeyName(), this.getDbHelper())));

    }

    private String getSequenceKeyName() {
        return (this.getTableName() + "_" + this.getKeyName()).toLowerCase();
    }

    /**
     * 以下两个方法做为三明治用来做引导查询主库，使用场景为当用户刚更新过某个表，紧接查主库
     * <p>
     * beginForceMasterDB();
     * select()...
     * endForceMasterDB();
     */
    public void beginForceMasterDB() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
    }

    /**
     * <p>endForceMasterDB.</p>
     */
    public void endForceMasterDB() {
        try {
            this.getDbHelper().close();
        } catch (DbException e) {
            LOG.warn("endForceMasterDB", e);
        }
    }

    public boolean save(T info) {
        if (getId(info) != null && Integer.parseInt(getId(info).toString()) != 0) {
            return updateByKey(info);
        } else {
            return insertAutoIncrement(info);
        }
    }

    public boolean save(T info, String tableShardName) {
        if (getId(info) != null && Integer.parseInt(getId(info).toString()) != 0) {
            return updateByKey(info, null, tableShardName);
        } else {
            return insertAutoIncrement(info, tableShardName);
        }
    }

    protected void setIdLong(T info, long id) {
    }

    protected boolean insertAutoIncrement(T info) {
        beforeChange(info);
        setIdLong(info, getDbHelper().insertAndFetchLastId(this.getInsertSql(),
                getInsertObjs(info), null, null));

        afterInsert(info, getId(info) != null);
        return true;
    }

    protected boolean insertAutoIncrement(T info, String shardTableName) {
        beforeChange(info);
        setIdLong(info, getDbHelper().insertAndFetchLastId(shardNameStrategy(this.getInsertSql(), shardTableName),
                getInsertObjs(info), null, null));
        afterInsert(info, getId(info) != null, shardTableName);
        return true;
    }

    protected String getInsertSqlAutoIncrement() {
        String res = this.getInsertSql();
        res += " ;" + getLastInsertIdSql();
        return res;
    }


    /**
     * Select from the database for table "Agent_form"
     *
     * @return list of pojo
     */
    public @NotNull <V> List<T> selectBy(ActiveRecord<T> ar) {
        SqlPocket build = ar.getCondStack().build();

        String sql = this.getCoreSql() + build.getCondition();
        if (ar instanceof ActiveRecordImpl) {
            ActiveRecordImpl<T, V> arr = ((ActiveRecordImpl<T, V>) ar);
            sql += arr.parseOrder() + arr.parseLimitSql();

        }
        return this.dbHelper.executeQuery(sql,
                build.getParams().toArray(new Object[0]), this, ar.getResultFieldsSet());
    }

    protected String getCoreSql() {
        return SELECT_FROM + getTableName() + " ";
    }
}
