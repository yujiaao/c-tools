package com.bixuebihui.jdbc;

import com.bixuebihui.DbException;
import com.bixuebihui.db.*;
import com.bixuebihui.jdbc.aop.DbHelperAroundAdvice;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;
import com.bixuebihui.sequence.SequenceUtils;
import com.bixuebihui.shardingjdbc.core.api.HintManager;
import com.bixuebihui.sql.SQLUtil;
import com.bixuebihui.util.DbUtils;
import org.apache.commons.beanutils.*;
import org.apache.commons.beanutils.converters.*;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.bixuebihui.db.Dialect.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

/**
 * <p>Abstract BaseDao class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public abstract class BaseDao<T, ID> implements RowMapper<T>, IBaseListService<T, ID>, SimpleDaoInterface<T, ID> {

    private static final String SELECT_COUNT_FROM = "select count(*) from ";
    /**
     * Constant <code>SELECT_FROM="select * from "</code>
     */
    public static final String SELECT_FROM = "select * from ";
    /** Constant <code>WHERE=" where "</code> */
    protected static final String WHERE = " where ";
    protected IDbHelper dbHelper = null;
    /** Constant <code>LOG</code> */
    protected static final Logger LOG = LoggerFactory.getLogger(BaseDao.class);
    protected final PojoValidator<T> pojoValidator = new PojoValidator<>();


    static {
        ConvertUtils.register(new DateConverter(null), Date.class);
        ConvertUtils.register(new SqlDateConverter(null), java.sql.Date.class);
        ConvertUtils.register(new SqlTimestampConverter(null), Timestamp.class);
        ConvertUtils.register(new SqlTimeConverter(null), java.sql.Time.class);
        ConvertUtils.register(new BooleanConverter(null), Boolean.class);
    }

    /**
     * <p>beforeChange.</p>
     *
     * @param info a T object.
     */
    protected void beforeChange(T info) {
        pojoValidator.asureValid(info);
        if(info instanceof IModifiedTime){
            ((IModifiedTime)info).setModifiedTime(new Timestamp(
                    System.currentTimeMillis()
            ));
        }
    }
    private void beforeChange(T info, long time) {
        pojoValidator.asureValid(info);
        if(info instanceof IModifiedTime){
            ((IModifiedTime)info).setModifiedTime(new Timestamp(
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

    private static final Set<Class<?>> BUILT_IN_SET = new HashSet<>();

    /**
     * <p>Getter for the field <code>dbHelper</code>.</p>
     *
     * @return a {@link IDbHelper} object.
     */
    @Override
    public IDbHelper getDbHelper() {
        return dbHelper;
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
     * @param fields a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    protected static String[] fields(String ...fields){
        return fields;
    }

    /**
     * <p>params.</p>
     *
     * @param params a {@link java.lang.Object} object.
     * @return an array of {@link java.lang.Object} objects.
     */
    protected static Object[] params(Object ...params){
        return params;
    }

    /**
     * <p>Constructor for BaseDao.</p>
     *
     * @param dbHelper a {@link IDbHelper} object.
     */
    public BaseDao(IDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public BaseDao(DataSource ds) {
        this(ds,ds);
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
     * <p>create.</p>
     *
     * @return a T object.
     */
    @Override
    public T create() {
        return null;
    }

    private int dbtype = UNKNOWN;

    /**
     * MS SQL Server 2000
     *
     * @param selectSql 不带分页的基本语句
     * @param startNum  起始行，第一页从零开始
     * @param endNum    结束行，每页数+startNum
     * @return 完整分页SQL
     */
    private static String getPagingSqlSqlServer(String selectSql, int startNum, int endNum) {
        return SqlServer2000PageHepler.getLimitString(selectSql, startNum, endNum - startNum + 1);
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
        } else if (name.contains("SQL SERVER")) {
            // sql server 2005
            res = SQLSERVER_2005_AND_UP;
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
    private static String getPagingSqlSqlServer2005(String selectSql, int startNum, int endNum) {

        return SqlServer2005PageHepler.getLimitString(selectSql, startNum, endNum);

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

        if (dbtype == DERBY) {
            return getPagingSqlDerby(selectSql, startNum, endNum);
        } else if (dbtype == SQLSERVER) {
            return getPagingSqlSqlServer(selectSql, startNum, endNum);
        } else if (dbtype == SQLSERVER_2005_AND_UP) {
            return getPagingSqlSqlServer2005(selectSql, startNum, endNum);

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

        String orderBy = selectSql.indexOf("order by")>0 ?  selectSql.substring(selectSql.indexOf("order by")):"";
        selectSql = selectSql.indexOf("order by")>0 ? selectSql.substring(0, selectSql.indexOf("order by")): selectSql;

        return SELECT_FROM + "(select FR.*,ROW_NUMBER() OVER() AS RN from (" + selectSql
                + ")as FR "+orderBy+") as ttt where RN<=" + endNum + " and RN>" + startNum;

    }

    private String getDateTimeSql(String dt, boolean isTimestamp) {
        detectDbType();
        if (dbtype == DERBY) {
            if (isTimestamp) {
                return getTimestampSqlDerby(dt);
            }
            return getDateSqlDerby(dt);
        } else if (dbtype == SQLSERVER || dbtype == SQLSERVER_2005_AND_UP || dbtype == ACCESS) {
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
        } else if (dbtype == SQLSERVER || dbtype == SQLSERVER_2005_AND_UP || dbtype == ACCESS) {
            return getDateSqlServer(dt);
        } else if (dbtype == MYSQL) {
            return getDateSqlMySql(dt);
        } else if (dbtype == POSTGRESQL) {
            return getDateSqlPostgresql(dt);
        } else {
            return getDateSqlOracle(dt);
        }

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
    public int count(String sql, Object... params) {
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
    public boolean exists(String where, Object... params) {
        Object o = this.getDbHelper().executeScalar("select 1 from " + getTableName() + " " + where, params);
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
    public int countWhere(String where, Object... params) {
        String query = SELECT_COUNT_FROM + getTableName() + " " + where;
        Object o = this.getDbHelper().executeScalar(query, params);
        return o == null ? 0 : Integer.parseInt(o.toString());
    }

    /**
     * 通用更新函数
     *
     * @param fieldNames  字段名
     * @param whereClause 条件语句，可以加“?”形参数
     * @param params      参数对象数组，因无参数类型判断，不建议用空值
     * @param cn          用于事务处理的数据库连接
     * @return 更新记录数
     */
    public int update(String[] fieldNames, String whereClause, Object[] params, Connection cn) {
        String query = getUpdateSql(fieldNames, whereClause);
        if (cn == null) {
            return getDbHelper().executeNoQuery(query, params);
        } else {
            return getDbHelper().executeNoQuery(query, params, cn);
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
        return update(fieldNames, whereClause, params, null);
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
     * <p>getUpdateSql.</p>
     *
     * @param fieldNames  an array of {@link java.lang.String} objects.
     * @param whereClause a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    protected String getUpdateSql(String[] fieldNames, String whereClause) {
        if (fieldNames == null || fieldNames.length == 0) {
            throw new DbException("参数fieldNames不能为空");
        }

        String res = StringUtils.join(fieldNames, "=?, ") + "=? ";

        return "update " + this.getTableName() + " set " + res + whereClause;
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
     * @param h a {@link java.util.Map} object.
     * @param receiver a K object.
     * @param <K> a K object.
     * @return a K object.
     */
    protected @NotNull
    <K> K map2object(Map<String, Object> h, @NotNull K receiver) {
        BeanMap b = new BeanMap(receiver);

        Iterator<Entry<Object, Object>> iter = b.entryIterator();
        while (iter.hasNext()) {
            Entry<?, ?> e = iter.next();
            String hkey = e.getKey().toString();
            Object value;
            if (h.containsKey(hkey)) {
                value = h.get(hkey);
            }else {
                hkey = hkey.toUpperCase();
                if (h.containsKey(hkey)){
                    value = h.get(hkey);
                }else{
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
                } catch (SQLException throwables) {
                    throw new DbException(throwables);
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
     * <p>convertCaseInsensitive.</p>
     *
     * @param <K>      期待类型
     * @param h        源数据
     * @param receiver 目标对象
     * @return 目标对象
     * @throws java.lang.IllegalAccessException    无法拷贝属性
     * @throws java.lang.reflect.InvocationTargetException 无法拷贝属性
     * @throws java.lang.NoSuchMethodException     无法拷贝属性
     */
    protected @NotNull
    <K> K convertCaseSensitive(Map<String, Object> h, @NotNull K receiver)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // access properties as Map
        Map<String, String> properties = BeanUtils.describe(receiver);
        Map<String, Object> values = new HashMap<>(h.size()*2);
        for (Map.Entry<String, ?> e : properties.entrySet()) {
            Object o = h.get(e.getKey());
            values.put(e.getKey(), o);
        }
        BeanUtils.populate(receiver, values);
        return receiver;
    }

    /**
     * <p>convert.</p>
     *
     * @param <K>    期待类型
     * @param h  源数据
     * @param receiver 目标对象
     * @return 目标对象
     * @throws java.lang.IllegalAccessException 无法拷贝属性
     * @deprecated use convertCaseInsensitive
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
                if(dbtype==DERBY){
                    key = key.toLowerCase(Locale.ROOT);
                }
                b.copyProperty(receiver,  CaseUtils.toCamelCase(key, false, '_'), value);
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
     * @param select a {@link java.lang.String} object.
     * @param whereClause a {@link java.lang.String} object.
     * @param orderBy a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @param rowStart a int.
     * @param rowEnd a int.
     * @param clz a {@link java.lang.Class} object.
     * @param <K> a K object.
     * @return a {@link java.util.List} object.
     */
    public @NotNull <K> List<K> select(String select, String whereClause, String orderBy, Object[] params, int rowStart,
                                       int rowEnd, Class<K> clz) {

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
                    v1.add(convert(h, clz.getConstructor().newInstance()));
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                    throw new DbException(e);
                }
            }
        }

        return v1;
    }

    /**
     * <p>select.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @param whereClause a {@link java.lang.String} object.
     * @param orderBy a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @param rowStart a int.
     * @param rowEnd a int.
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
    List<T> select(String whereClause, Object[] params, String orderByClause, int beginNum, int endNum)
             {
        String query = getSelectAllFromTable() + whereClause;
        if (this.getDbType() != DERBY) {
            query += " "+orderByClause;
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
            Object o = rs.getObject(1);
            return (ID) o;
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
                           int beginNum, int endNum) {
        String query = "select " + fieldList + " from " + getTableName() + " " + whereClause;
        if (this.getDbType() != DERBY) {
            query += orderByClause;
        }
        query = getPagingSql(query, beginNum, endNum);
        return getDbHelper().executeQuery(query, params, new RowMapperResultReader<>(this), ActiveRecord.toFieldsSet(fieldList));
    }

    public @NotNull
    List<T> select(String whereClause, String orderbyClause, int beginNum, int endNum)
             {
        return select(whereClause, null, orderbyClause, beginNum, endNum);
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
            return Collections.emptyList();
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
            return Collections.emptyList();
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
     *       Deletes from the database for table
     */
    public boolean deleteByKey(ID id, Connection cn) {
        String query = getDeleteWhere() + getKeyName() + "=?";
        return 1 <= getDbHelper().executeNoQuery(query, new Object[]{id}, cn);
    }

    private String getDeleteWhere() {
        return "delete from " + getTableName() + WHERE;
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
        return 1 <= getDbHelper().executeNoQuery(query, new Object[]{id});
    }

    /**
     * <p>deleteByKeys.</p>
     *
     * @param id an array of V[] objects.
     * @return a boolean.
     */
    public boolean deleteByKeys(ID[] id) {
        String query = getDeleteWhere() + makeInPlaceHolder(getKeyName(), id.length);
        return 1 <= getDbHelper().executeNoQuery(query, id);
    }

    /**
     * <p>makeInPlaceHolder.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param size a int.
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
     * <p>filterForSQL.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String filterForSql(String sql) {
        if (sql == null || sql.length()==0) {
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
     *
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
     * @param where a {@link java.lang.String} object.
     * @param orderBy a {@link java.lang.String} object.
     * @param stepMax a int.
     * @param params an array of {@link java.lang.Object} objects.
     * @return a {@link java.util.List} object.
     */
    public @NotNull
    List<T> selectAllByStep(String where, String orderBy, int stepMax, Object[] params)
             {
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
     * @param orderBy            排序方法应为形如" order by id "的字符串，前后需加空格
     * @param stepMax            每次获取最多记录数，返回结果数据表记录可能{@code site()<=step_max }
     * @param handler             真正用于处理返回结果的接口
     * @param params              sql参数, 为 where 和 order_by 里的？点位符对应参数
     * @param alwaysFromBeginning 当处理结果不影响获取条件时， alwaysFromBeginning 应为 false,
     *                            否则会死循环，当处理结果使得最终会不再返回结果集时，才能为 true
     */
    public void processAllByStep(String where, String orderBy, int stepMax, ProcessHandler<T> handler,
                                 Object[] params, boolean alwaysFromBeginning) {
        List<T> list;
        int beginNum = 0;
        for (list = select(where, params, orderBy, beginNum, beginNum + stepMax);
             !list.isEmpty();
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
        } else if (dbtype == SQLSERVER || dbtype == SQLSERVER_2005_AND_UP || dbtype == ACCESS) {
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
     * @param sql a {@link java.lang.String} object.
     * @param where a {@link java.lang.String} object.
     * @param orderBy a {@link java.lang.String} object.
     * @param params an array of {@link java.lang.Object} objects.
     * @param start a int.
     * @param rowEnd a int.
     * @param clz an array of {@link java.lang.Class} objects.
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
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
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
        assert info!=null;
        return new Object[0];
    }

    /**
     * <p>getInsertObjs.</p>
     *
     * @param info a T object.
     * @return an array of {@link java.lang.Object} objects.
     */
    protected Object[] getInsertObjs(T info) {
        assert info!=null;
        throw new IllegalArgumentException("this method must override!");
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

        return 1 == getDbHelper().executeNoQuery(getUpdateSql(), getUpdateObjs(info));
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

    /**
     * Inserts the info object values into the database.
     *
     * @param info pojo
     * @return true if success
     */
    @Override
    public boolean insert(T info) {
        beforeChange(info);
        return 1 == getDbHelper().executeNoQuery(getInsertSql(), getInsertObjs(info));
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
        return 1 == getDbHelper().executeNoQuery(getInsertSql(), getInsertObjs(info), cn);
    }

    /**
     * <p>insertBatch.</p>
     *
     * @param infos an array of T[] objects.
     * @return a boolean.
     */
    public boolean insertBatch(T[] infos) {
        beforeChange(infos);

        return insertBatch(infos, null);
    }

    /**
     * <p>updateBatch.</p>
     *
     * @param infos an array of T[] objects.
     * @return a boolean.
     */
    public boolean updateBatch(T[] infos) {
        beforeChange(infos);
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

        List<Object[]> a = new ArrayList<>();
        for (T info : infos) {
            a.add(getInsertObjs(info));
        }
        return infos.length == getDbHelper().executeNoQueryBatch(getInsertSql(), a, cn);
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

        List<Object[]> a = new ArrayList<>();
        for (T info : infos) {
            a.add(getUpdateObjs(info));
        }
        return infos.length == getDbHelper().executeNoQueryBatch(getUpdateSql(), a, cn);
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

        if (field == null) {
            throw new DbException("field must not null");
        }

        String keyName = StringUtils.isEmpty(this.getKeyName()) ? field : this.getKeyName();
        final boolean noGroup = (groupBy == null || groupBy.length == 0);

        String groupKeys = noGroup ? "" : ", concat(" + StringUtils.join(groupBy, ",") + ") gv_key";

        String groupByClause = noGroup ? "" : " group by " + groupKeys;

        String query = "select count(" + keyName + ") gv_cnt, " + groupFunction + "(" + field + ") gv_sm " + groupKeys
                + " from " + getTableName() + " " + where + groupByClause + (having == null ? "" : having);
        return  getDbHelper().executeQuery(query, params,
                new RowMapperResultReader<>((rs, index, set) -> {
                    CountValue cs = new CountValue();
                    cs.setCount(rs.getLong(1));
                    cs.setValue(rs.getDouble(2));
                    if (!noGroup) {
                        cs.setKey(rs.getString(3));
                    }
                    return cs;
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

        if (field == null) {
            throw new DbException("field must not null");
        }

        String keyName = StringUtils.isEmpty(this.getKeyName()) ? field : this.getKeyName();
        final boolean noGroup = (groupBy == null || groupBy.length == 0);

        String groupKeys = noGroup ? "" : "concat(" + StringUtils.join(groupBy, ",") + ") gv_key";

        String groupByClause = noGroup ? "" : " group by " + StringUtils.join(groupBy, ",");

        String query = "select count(" + keyName + ") gv_cnt, " + groupFunction + "(" + field + ") gv_sm " +(noGroup? "": ","+groupKeys)
                + " from " + getTableName() + " " + where + groupByClause + (having == null ? "" : having);
        return getDbHelper().executeQuery(query, params,
                new RowMapperResultReader<>((rs, index, set) -> {
                    CountObject<K> cs = new CountObject<>();
                    cs.setCount(rs.getLong(1));
                    cs.setValue(rs.getObject(2, type));
                    if (!noGroup) {
                        cs.setKey(rs.getString(3));
                    }
                    return cs;
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

    /** {@inheritDoc} */
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

    protected void setIdLong(T info, long id) {
    }

    protected boolean insertAutoIncrement(T info) {
        beforeChange(info);
        setIdLong(info, getDbHelper().insertAndFetchLastId(this.getInsertSql(),
                getInsertObjs(info), null, null));
        return true;
    }

    protected String getInsertSqlAutoIncrement() {
        String res = this.getInsertSql();
        res += " ;" + getLastInsertIdSql() ;
        return res;
    }



    /**
     * Select from the database for table "Agent_form"
     *
     * @return list of pojo
     */
    public @NotNull
    List<T> selectBy(ActiveRecord<T> ar) {
        SqlPocket build = ar.getCondStack().build();

        String sql = this.getCoreSql()+build.getCondition();
        if(ar instanceof ActiveRecordImpl){
            ActiveRecordImpl arr =  (( ActiveRecordImpl) ar);
            sql += arr.parseOrder() + arr.parseLimitSql();

        }
        return this.dbHelper.executeQuery(sql,
                build.getParams().toArray(new Object[0]), this, ar.getResultFieldsSet());
    }

    protected String getCoreSql(){
        return SELECT_FROM+getTableName()+" ";
    }


    /**
     * <p>mapRow.</p>
     *
     * @param rs    a {@link java.sql.ResultSet} object.
     * @param index  row index from 1;
     * @return a T object.
     * @throws java.sql.SQLException if any.
     */
//    protected T mapRow(ResultSet rs, int index)  throws SQLException {
//        return mapRow(rs, index, Collections.emptySet());
//    };


    /**
     * contain field check
     * @param field
     * @param fields
     * @return true if fields is empty or contains field, ignore
     */
    protected static boolean containsIgnoreCase(String field, Set<String> fields){
        return fields==null ||  fields.isEmpty() || fields.contains(field.toLowerCase(Locale.ROOT));
    }
}
