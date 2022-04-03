package com.bixuebihui.tablegen;

import com.bixuebihui.cache.DictionaryCache;
import com.bixuebihui.cache.DictionaryItem;
import com.bixuebihui.generated.tablegen.pojo.T_metacolumn;
import com.bixuebihui.jdbc.BaseDao;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.jdbc.RowMapperResultReader;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableInfo;
import com.bixuebihui.tablegen.entry.TableSetInfo;
import com.bixuebihui.util.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.bixuebihui.db.Dialect.*;

/**
 * @author xwx
 */
public class TableUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TableUtils.class);
    private static StopWatch stopWatch = new StopWatch();
    private static boolean first = true;

    private static void start(String taskName) {
        stopWatch.start(taskName);
    }

    private static void end() {
        stopWatch.stop();
        if (stopWatch.getLastTaskTimeMillis() > 100) {
            String info = stopWatch.getLastTaskName() + " took:" + stopWatch.getLastTaskTimeMillis() +
                    "\nYour DB metadata is slow, By using the tip given there : http://www.mysqlperformanceblog.com/2011/12/23/solving-information_schema-slowness/\n" +
                    "If you use MySQL, try set this in my.cnf :\n" +
                    "innodb_stats_on_metadata=0" +
                    "\n and  set " +
                    "useInformationSchema=true" +
                    "\n in db url \n";
            if (first) {
                first = false;
                LOG.warn(info);
            } else {
                LOG.warn(info);
            }
        } else {
            LOG.debug(stopWatch.getLastTaskName() + " use: " + stopWatch.getLastTaskTimeMillis()+"ms");
        }

    }

    /**
     * Selects the primary keys for a particular table.
     *
     * @throws SQLException if any
     */
    public static List<String> getTableKeys(DatabaseMetaData metaData,
                                            String catalog, String schema, String tableName)
            throws SQLException {
        start("getTableKeys");

        List<String> keyData = new ArrayList<>();
        ResultSet r = null;
        try {
            String key;
            r = metaData.getPrimaryKeys(catalog, schema, tableName);
            while (r.next()) {
                // COLUMN_NAME
                key = r.getString(4);

                boolean existsCol = false;
                for (String dt : keyData) {
                    if (dt.equalsIgnoreCase(key)) {
                        existsCol = true;
                        break;
                    }
                }
                if (existsCol) {
                    continue;
                }

                keyData.add(key);
            }

        } catch (SQLException e) {
            System.err.println("Error whilst getting keys for " + tableName);
            e.printStackTrace(System.err);
        } finally {
            end();
            DbUtils.close(r);
        }

        return keyData;
    }

    /**
     * Selects the Imported Keys defined for a particular table.
     *     1, "PKTABLE_CAT", Types.VARCHAR
     *     2, "PKTABLE_SCHEM", Types.VARCHAR
     *     3, "PKTABLE_NAME", Types.VARCHAR
     *     4, "PKCOLUMN_NAME", Types.VARCHAR
     *     5, "FKTABLE_CAT", Types.VARCHAR
     *     6, "FKTABLE_SCHEM", Types.VARCHAR
     *     7, "FKTABLE_NAME", Types.VARCHAR
     *     8, "FKCOLUMN_NAME", Types.VARCHAR
     *     9, "KEY_SEQ", Types.SMALLINT
     *     10, "UPDATE_RULE", Types.SMALLINT
     *     11, "DELETE_RULE", Types.SMALLINT
     *     12, "FK_NAME", Types.VARCHAR
     *     13, "PK_NAME", Types.VARCHAR
     *     14, "DEFERABILITY", Types.SMALLINT
     */
    public static List<ForeignKeyDefinition> getTableImportedKeys(
            DatabaseMetaData metaData, String catalog, String schema,
            String tableName) throws SQLException {
        String sFKName;
        String sFKColumn;
        String sPKColumn;
        String sFKTable;
        String sPKTable;
        short sequence;
        short oldSequence;
        boolean bMore;
        ForeignKeyDefinition f;
        start("getTableImportedKeys");

        List<ForeignKeyDefinition> foreignKeyData = new ArrayList<>();


        ResultSet r = metaData.getImportedKeys(catalog, schema, tableName);
        try {
            bMore = r.next();
            while (bMore) {
                sFKName = r.getString(12);
                //这里返回的表名是小写的(mysql 5.6.21-enterprise-commercial-advanced mac os),不知为什么?
                sFKTable = r.getString(7);
                sPKTable = r.getString(3);
                sequence = r.getShort(9);
                oldSequence = 0;

                boolean existsCol = false;
                for (ForeignKeyDefinition dt : foreignKeyData) {
                    if (dt.getFKColumnName().equalsIgnoreCase(sFKName)) {
                        existsCol = true;
                        break;
                    }
                }
                if (existsCol) {
                    continue;
                }

                f = new ForeignKeyDefinition(sPKTable, sFKTable, sFKName);
                while (bMore && (sequence > oldSequence)) {
                    sFKColumn = r.getString(8);
                    sPKColumn = r.getString(4);
                    f.addField(sFKColumn, sPKColumn);

                    oldSequence = sequence;
                    bMore = r.next();
                    if (bMore) {
                        sequence = r.getShort(9);
                    }
                }
                foreignKeyData.add(f);
            }
        } finally {
            end();
            DbUtils.close(r);
        }
        return foreignKeyData;
    }

    /**
     * Selects the Exported Keys defined for a particular table.
     */
    public static List<ForeignKeyDefinition> getTableExportedKeys(
            DatabaseMetaData metaData, String catalog, String schema,
            String tableName) throws SQLException {
        String sFKName;
        String sFKColumn;
        String sPKColumn;
        String sFKTable;
        String sPKTable;
        short sequence;
        short oldSequence;
        boolean bMore;
        ForeignKeyDefinition f;

        start("getTableExportedKeys");


        List<ForeignKeyDefinition> foreignKeyData = new ArrayList<>();

        ResultSet r = metaData.getExportedKeys(catalog, schema, tableName);
        try {
            bMore = r.next();

            while (bMore) {
                sFKName = r.getString(12);
                sFKTable = r.getString(7);
                sPKTable = r.getString(3);
                sequence = r.getShort(9);
                oldSequence = 0;
                f = new ForeignKeyDefinition(sPKTable, sFKTable, sFKName);
                while (bMore && (sequence > oldSequence)) {
                    sFKColumn = r.getString(8);
                    sPKColumn = r.getString(4);
                    f.addField(sFKColumn, sPKColumn);

                    oldSequence = sequence;
                    bMore = r.next();
                    if (bMore) {
                        sequence = r.getShort(9);
                    }
                }
                foreignKeyData.add(f);
            }
        } finally {
            end();
            DbUtils.close(r);
        }
        return foreignKeyData;
    }


    /**
     * when MySQL using InformationSchema, the sql will be :
     * SELECT
     * A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,
     * NULL AS PKTABLE_SCHEM,
     * A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,  -- 3*
     * A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME, -- 4*
     * A.TABLE_SCHEMA AS FKTABLE_CAT,   -- 5
     * NULL AS FKTABLE_SCHEM,
     * A.TABLE_NAME AS FKTABLE_NAME,    -- 7*
     * A.COLUMN_NAME AS FKCOLUMN_NAME,   -- 8*
     * A.ORDINAL_POSITION AS KEY_SEQ,   -- 9*
     * CASE
     * WHEN R.UPDATE_RULE = 'CASCADE' THEN 0
     * WHEN R.UPDATE_RULE = 'SET NULL' THEN 2
     * WHEN R.UPDATE_RULE = 'SET DEFAULT' THEN 4
     * WHEN R.UPDATE_RULE = 'RESTRICT' THEN 1
     * WHEN R.UPDATE_RULE = 'NO ACTION' THEN 3
     * ELSE 3
     * END AS UPDATE_RULE,   --10
     * CASE
     * WHEN R.DELETE_RULE = 'CASCADE' THEN 0
     * WHEN R.DELETE_RULE = 'SET NULL' THEN 2
     * WHEN R.DELETE_RULE = 'SET DEFAULT' THEN 4
     * WHEN R.DELETE_RULE = 'RESTRICT' THEN 1
     * WHEN R.DELETE_RULE = 'NO ACTION' THEN 3
     * ELSE 3
     * END AS DELETE_RULE,
     * A.CONSTRAINT_NAME AS FK_NAME,  --12 *
     * (SELECT
     * CONSTRAINT_NAME
     * FROM
     * INFORMATION_SCHEMA.TABLE_CONSTRAINTS
     * WHERE
     * TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA
     * AND TABLE_NAME = A.REFERENCED_TABLE_NAME
     * AND CONSTRAINT_TYPE IN ('UNIQUE' , 'PRIMARY KEY')
     * LIMIT 1) AS PK_NAME,  --13
     * 7 AS DEFERRABILITY
     * FROM
     * INFORMATION_SCHEMA.KEY_COLUMN_USAGE A
     * JOIN
     * INFORMATION_SCHEMA.TABLE_CONSTRAINTS B USING (TABLE_SCHEMA , TABLE_NAME , CONSTRAINT_NAME)
     * JOIN
     * INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS R ON (R.CONSTRAINT_NAME = B.CONSTRAINT_NAME
     * AND R.TABLE_NAME = B.TABLE_NAME
     * AND R.CONSTRAINT_SCHEMA = B.TABLE_SCHEMA)
     * WHERE
     * B.CONSTRAINT_TYPE = 'FOREIGN KEY'
     * AND A.REFERENCED_TABLE_SCHEMA like 'ssll'
     * AND A.REFERENCED_TABLE_NAME = 'Forum'
     * ORDER BY A.TABLE_SCHEMA , A.TABLE_NAME , A.ORDINAL_POSITION;
     *
     * @param dbHelper datasource helper
     * @param databaseName database name
     * @return result of schema
     * @throws SQLException if any
     */
    public static Map<String, List<ForeignKeyDefinition>> getAllMySQLExportKeys(IDbHelper dbHelper, String databaseName) throws SQLException {
        String sql = "SELECT\n" +
                "    A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,  -- 3*\n" +
                "    A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME, -- 4*\n" +
                "    A.TABLE_NAME AS FKTABLE_NAME,    -- 7* \n" +
                "    A.COLUMN_NAME AS FKCOLUMN_NAME,   -- 8*\n" +
                "    A.ORDINAL_POSITION AS KEY_SEQ,   -- 9*\n" +
                "    A.CONSTRAINT_NAME AS FK_NAME  -- 12 *\n" +
                " FROM\n" +
                "    INFORMATION_SCHEMA.KEY_COLUMN_USAGE A\n" +
                "       JOIN\n" +
                "    INFORMATION_SCHEMA.TABLE_CONSTRAINTS B USING (TABLE_SCHEMA , TABLE_NAME , CONSTRAINT_NAME)        \n" +
                " WHERE    \n" +
                "    B.TABLE_SCHEMA=?\n" +
                "    and B.CONSTRAINT_TYPE = 'FOREIGN KEY'        \n" +
                " ORDER BY A.TABLE_SCHEMA , A.TABLE_NAME , A.ORDINAL_POSITION";

        Map<String, List<ForeignKeyDefinition>> map = new HashMap<>();

        List<ForeignKeyDefinition> list = getFkDefinitions(dbHelper, databaseName, sql, map);

        dump(list);

        return map;
    }

    /**
     * SELECT
     * A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,
     * NULL AS PKTABLE_SCHEM,
     * A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,
     * A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME,
     * A.TABLE_SCHEMA AS FKTABLE_CAT,
     * NULL AS FKTABLE_SCHEM,
     * A.TABLE_NAME AS FKTABLE_NAME,
     * A.COLUMN_NAME AS FKCOLUMN_NAME,
     * A.ORDINAL_POSITION AS KEY_SEQ,
     * CASE
     * WHEN R.UPDATE_RULE = 'CASCADE' THEN 0
     * WHEN R.UPDATE_RULE = 'SET NULL' THEN 2
     * WHEN R.UPDATE_RULE = 'SET DEFAULT' THEN 4
     * WHEN R.UPDATE_RULE = 'RESTRICT' THEN 1
     * WHEN R.UPDATE_RULE = 'NO ACTION' THEN 3
     * ELSE 3
     * END AS UPDATE_RULE,
     * CASE
     * WHEN R.DELETE_RULE = 'CASCADE' THEN 0
     * WHEN R.DELETE_RULE = 'SET NULL' THEN 2
     * WHEN R.DELETE_RULE = 'SET DEFAULT' THEN 4
     * WHEN R.DELETE_RULE = 'RESTRICT' THEN 1
     * WHEN R.DELETE_RULE = 'NO ACTION' THEN 3
     * ELSE 3
     * END AS DELETE_RULE,
     * A.CONSTRAINT_NAME AS FK_NAME,
     * (SELECT
     * CONSTRAINT_NAME
     * FROM
     * INFORMATION_SCHEMA.TABLE_CONSTRAINTS
     * WHERE
     * TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA
     * AND TABLE_NAME = A.REFERENCED_TABLE_NAME
     * AND CONSTRAINT_TYPE IN ('UNIQUE' , 'PRIMARY KEY')
     * LIMIT 1) AS PK_NAME,
     * 7 AS DEFERRABILITY
     * FROM
     * INFORMATION_SCHEMA.KEY_COLUMN_USAGE A
     * JOIN
     * INFORMATION_SCHEMA.TABLE_CONSTRAINTS B USING (CONSTRAINT_NAME , TABLE_NAME)
     * JOIN
     * INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS R ON (R.CONSTRAINT_NAME = B.CONSTRAINT_NAME
     * AND R.TABLE_NAME = B.TABLE_NAME
     * AND R.CONSTRAINT_SCHEMA = B.TABLE_SCHEMA)
     * WHERE
     * B.CONSTRAINT_TYPE = 'FOREIGN KEY'
     * AND A.TABLE_SCHEMA LIKE ?
     * AND A.TABLE_NAME = ?
     * AND A.REFERENCED_TABLE_SCHEMA IS NOT NULL
     * ORDER BY A.REFERENCED_TABLE_SCHEMA , A.REFERENCED_TABLE_NAME , A.ORDINAL_POSITION
     */
    public static Map<String, List<ForeignKeyDefinition>> getAllMySQLImportKeys(IDbHelper dbHelper, String databaseName) throws SQLException {
        String sql = "SELECT \t\n" +
                "    A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,   -- 3 *\n" +
                "    A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME, -- 4 *\t\n" +
                "    A.TABLE_NAME AS FKTABLE_NAME, -- 7*\n" +
                "    A.COLUMN_NAME AS FKCOLUMN_NAME, -- 8* \n" +
                "    A.ORDINAL_POSITION AS KEY_SEQ,  -- 9 *\t \n" +
                "    A.CONSTRAINT_NAME AS FK_NAME  -- 12 *\n" +
                " FROM\n" +
                "    INFORMATION_SCHEMA.KEY_COLUMN_USAGE A\n" +
                "    JOIN\n" +
                "    INFORMATION_SCHEMA.TABLE_CONSTRAINTS B USING (CONSTRAINT_NAME , TABLE_NAME)\n" +
                " WHERE\n" +
                "    B.CONSTRAINT_TYPE = 'FOREIGN KEY'\n" +
                "    AND A.TABLE_SCHEMA = ? \t \n" +
                "    AND A.REFERENCED_TABLE_SCHEMA IS NOT NULL\n" +
                "    ORDER BY A.REFERENCED_TABLE_SCHEMA , A.REFERENCED_TABLE_NAME , A.ORDINAL_POSITION";

        Map<String, List<ForeignKeyDefinition>> map = new HashMap<>();

        List<ForeignKeyDefinition> list = getFkDefinitions(dbHelper, databaseName, sql, map);

        dump(list);

        return map;
    }

    private static List<ForeignKeyDefinition> getFkDefinitions(IDbHelper dbHelper, String databaseName, String sql, Map<String, List<ForeignKeyDefinition>> map) {
        return dbHelper.executeQuery(sql, new Object[]{databaseName}, new RowMapperResultReader<>((r, index, set) -> {

            String sFKName = r.getString("FK_NAME"); //12
            String sFKTable = r.getString("FKTABLE_NAME"); //7
            String sPKTable = r.getString("PKTABLE_NAME"); //3
            short sequence = r.getShort("KEY_SEQ"); //9

            List<ForeignKeyDefinition> foreignKeyData = map.computeIfAbsent(sFKTable, k -> new ArrayList<>());

            ForeignKeyDefinition fkDefinition = getFkDefinition(sFKName, foreignKeyData);
            if (fkDefinition == null) {
                fkDefinition = new ForeignKeyDefinition(sPKTable, sFKTable, sFKName);
                foreignKeyData.add(fkDefinition);
            }

            String sFKColumn = r.getString("FKCOLUMN_NAME"); //8
            String sPKColumn = r.getString("PKCOLUMN_NAME"); //4
            fkDefinition.addField(sFKColumn, sPKColumn);

            LOG.info("sequence=" + sequence + " FKName=" + sFKName + " FKTable=" + sFKTable + " PKTable=" + sPKTable + " FKColumn=" + sFKColumn + " sPKColumn=" + sPKColumn);
            return fkDefinition;
        }));
    }

    private static ForeignKeyDefinition getFkDefinition(String sFKName, List<ForeignKeyDefinition> foreignKeyData) {
        LOG.debug("dump all foreign keys:");
        dump(foreignKeyData);

        for (ForeignKeyDefinition dt : foreignKeyData) {
            if (dt.getForeignKeyName().equalsIgnoreCase(sFKName)) {
                LOG.debug("FKName:" + sFKName + " found!");
                return dt;
            }
        }
        LOG.debug("FKName:" + sFKName + " NOT found!");
        return null;
    }

    private static void dump(List<ForeignKeyDefinition> list) {
        for (ForeignKeyDefinition fk : list) {
            LOG.debug(fk.toString());
        }
    }


    /**
     * Retrieves all the table data required. find all table names not in
     * exclude list.
     * TABLE_CAT String => 表类别（可为 null）
     * TABLE_SCHEM String => 表模式（可为 null）
     * TABLE_NAME String => 表名称
     * TABLE_TYPE String => 表类型。典型的类型是 "TABLE"、"VIEW"、"SYSTEM TABLE"、"GLOBAL TEMPORARY"、"LOCAL TEMPORARY"、"ALIAS" 和 "SYNONYM"。
     * REMARKS String => 表的解释性注释
     * TYPE_CAT String => 类型的类别（可为 null）
     * TYPE_SCHEM String => 类型模式（可为 null）
     * TYPE_NAME String => 类型名称（可为 null）
     * SELF_REFERENCING_COL_NAME String => 有类型表的指定 "identifier" 列的名称（可为 null）
     * REF_GENERATION String => 指定在 SELF_REFERENCING_COL_NAME 中创建值的方式。这些值为 "SYSTEM"、"USER" 和 "DERIVED"。（可能为 null）
     *
     * 数据库中schema和catalog
     * ----------------------
     *  A：按照SQL标准的解释，在SQL环境下Catalog和Schema都属于抽象概念，从概念上说，一个数据库系统包含多个Catalog，每个Catalog又包含多个Schema，而每个Schema又包含多个数据库对象（表、视图、序列等），反过来讲一个数据库对象必然属于一个Schema，而该Schema又必然属于一个Catalog。
     *  B：作用：得到数据库对象的全限定名称，解决命名冲突问题
     *  C：用法：各种数据库系统对Catalog和Schema的支持和实现方式千差万别，针对具体问题需要参考具体的产品说明书，比较简单而常用的实现方式是使用数据库名作为Catalog名，使用用户名作为Schema名
     *
     *  数据库	Catalog	 Schema
     * -------|--------|--------
     *  Oracle	不支持	用户名(User Id)
     *  MySQL	不支持	数据库名
     *
     *  D：表删除后会放到数据库的回收站：数据库库删除一张表之后,会产生一个BIN开头的垃圾信息,使用下面sql语句可以查询出来
     *  select object_name
     *  from user_objects
     *  where object_type = upper('table');
     *  清除回收的表：purge recyclebin;
     *
     *  !!!
     *   mysql-connector-java 5.1.47 版与8.0.22版的区别
     *   当用户有多个库的权限时， metaData.getTables 不管schema是否指定库，
     *   8.0.22版返回所有用户可见的表，
     *   5.1.47版只返回 schema 指定的库里的表
     *  !!!
     */
    public static List<String> getTableData(DatabaseMetaData metaData,
                                            String catalog, String schema, String tableOwner,
                                            Map<String, String> includeTablesList,
                                            Map<String, String> excludeTablesList) throws SQLException {
        String[] tableTypes = {"TABLE", "VIEW"};
        List<String> tableNames;

        ResultSet tables = metaData.getTables(catalog, schema, null, tableTypes);
        start("getTableData");
        try {
            tableNames = new ArrayList<>();

            String tableName;

            while (tables.next()) {


                if (tableOwner != null
                        && tables.getString(2) != null
                        && !tableOwner.equalsIgnoreCase(
                        tables.getString(2))) {
                    continue;
                }

                tableName = tables.getString(3);

                // If we are using a table list
                // then check against that first.
                // If no list then we use everything.
                //
                if (!tableNames.contains(tableName)) {
                    if (matchTableName(includeTablesList, tableName)) {
                        if (!isExcluded(excludeTablesList, tableName)) {
                            tableNames.add(tableName);
                        }
                    }
                }
            }
        } finally {
            end();
            DbUtils.close(tables);
        }
        return tableNames;
    }

    public static boolean isExcluded(Map<String, String> excludeTablesList,
                                     String tableName) {
        if (!tableName.contains("$")) {
            if (excludeTablesList == null) {
                return false;
            }
            return (excludeTablesList.get(tableName) != null)
                    || matchTableName(excludeTablesList, tableName);
        } else {
            return true;
        }
    }

    public static boolean matchTableName(Map<String, String> includeTablesList,
                                         String tableName) {
        if (includeTablesList == null) {
            return true;
        }
        if (includeTablesList.get(tableName) != null) {
            return true;
        }

        Object[] st = includeTablesList.keySet().toArray();
        for (Object o : st) {
            if (tableName.matches(o.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the column data for a specified table.
     * 每个列描述都有以下列：
     * <p>
     * TABLE_CAT String => 表类别（可为 null）
     * TABLE_SCHEM String => 表模式（可为 null）
     * TABLE_NAME String => 表名称
     * COLUMN_NAME String => 列名称
     * DATA_TYPE int => 来自 java.sql.Types 的 SQL 类型
     * TYPE_NAME String => 数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的
     * COLUMN_SIZE int => 列的大小。
     * BUFFER_LENGTH 未被使用。
     * DECIMAL_DIGITS int => 小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null。
     * NUM_PREC_RADIX int => 基数（通常为 10 或 2）
     * NULLABLE int => 是否允许使用 NULL。
     * columnNoNulls - 可能不允许使用 NULL 值
     * columnNullable - 明确允许使用 NULL 值
     * columnNullableUnknown - 不知道是否可使用 null
     * REMARKS String => 描述列的注释（可为 null）
     * COLUMN_DEF String => 该列的默认值，当值在单引号内时应被解释为一个字符串（可为 null）
     * SQL_DATA_TYPE int => 未使用
     * SQL_DATETIME_SUB int => 未使用
     * CHAR_OCTET_LENGTH int => 对于 char 类型，该长度是列中的最大字节数
     * ORDINAL_POSITION int => 表中的列的索引（从 1 开始）
     * IS_NULLABLE String => ISO 规则用于确定列是否包括 null。
     * YES --- 如果参数可以包括 NULL
     * NO --- 如果参数不可以包括 NULL
     * 空字符串 --- 如果不知道参数是否可以包括 null
     * SCOPE_CATLOG String => 表的类别，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
     * SCOPE_SCHEMA String => 表的模式，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
     * SCOPE_TABLE String => 表名称，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
     * SOURCE_DATA_TYPE short => 不同类型或用户生成 Ref 类型、来自 java.sql.Types 的 SQL 类型的源类型（如果 DATA_TYPE 不是 DISTINCT 或用户生成的 REF，则为 null）
     * IS_AUTOINCREMENT String => 指示此列是否自动增加
     * YES --- 如果该列自动增加
     * NO --- 如果该列不自动增加
     * 空字符串 --- 如果不能确定该列是否是自动增加参数
     * COLUMN_SIZE 列表示给定列的指定列大小。对于数值数据，这是最大精度。对于字符数据，这是字符长度。对于日期时间数据类型，这是 String 表示形式的字符长度（假定允许的最大小数秒组件的精度）。对于二进制数据，这是字节长度。对于 ROWID 数据类型，这是字节长度。对于列大小不适用的数据类型，则返回 Null。
     *
     *
     * mysql-java-connector 8 metadata:
     * 0 columnName=TABLE_CAT,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     * 1 columnName=TABLE_SCHEM,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     * 2 columnName=TABLE_NAME,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     * 3 columnName=COLUMN_NAME,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     * 4 columnName=DATA_TYPE,originalColumnName=null,mysqlType=-1(FIELD_TYPE_INT),sqlType=4,flags=, charsetIndex=0, charsetName=US-ASCII]"
     * 5 columnName=TYPE_NAME,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     * 6 columnName=COLUMN_SIZE,originalColumnName=null,mysqlType=-1(FIELD_TYPE_INT),sqlType=4,flags=, charsetIndex=0, charsetName=US-ASCII]"
     * 7 columnName=BUFFER_LENGTH,originalColumnName=null,mysqlType=-1(FIELD_TYPE_INT),sqlType=4,flags=, charsetIndex=0, charsetName=US-ASCII]"
     * 8 columnName=DECIMAL_DIGITS,originalColumnName=null,mysqlType=-1(FIELD_TYPE_INT),sqlType=4,flags=, charsetIndex=0, charsetName=US-ASCII]"
     * 9 columnName=NUM_PREC_RADIX,originalColumnName=null,mysqlType=-1(FIELD_TYPE_INT),sqlType=4,flags=, charsetIndex=0, charsetName=US-ASCII]"
     * 10 columnName=NULLABLE,originalColumnName=null,mysqlType=-1(FIELD_TYPE_INT),sqlType=4,flags=, charsetIndex=0, charsetName=US-ASCII]"
     * 11 columnName=REMARKS,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     * 12 columnName=COLUMN_DEF,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     * 13 columnName=SQL_DATA_TYPE,originalColumnName=null,mysqlType=-1(FIELD_TYPE_INT),sqlType=4,flags=, charsetIndex=0, charsetName=US-ASCII]"
     * 14 columnName=SQL_DATETIME_SUB,originalColumnName=null,mysqlType=-1(FIELD_TYPE_INT),sqlType=4,flags=, charsetIndex=0, charsetName=US-ASCII]"
     * 15 columnName=CHAR_OCTET_LENGTH,originalColumnName=null,mysqlType=-1(FIELD_TYPE_INT),sqlType=4,flags=, charsetIndex=0, charsetName=US-ASCII]"
     * 16 columnName=ORDINAL_POSITION,originalColumnName=null,mysqlType=-1(FIELD_TYPE_INT),sqlType=4,flags=, charsetIndex=0, charsetName=US-ASCII]"
     * 17 columnName=IS_NULLABLE,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     * 18 columnName=SCOPE_CATALOG,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     * 19 columnName=SCOPE_SCHEMA,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     * 20 columnName=SCOPE_TABLE,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     * 21 columnName=SOURCE_DATA_TYPE,originalColumnName=null,mysqlType=-1(FIELD_TYPE_SMALLINT),sqlType=5,flags=, charsetIndex=0, charsetName=US-ASCII]"
     * 22 columnName=IS_AUTOINCREMENT,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     * 23 columnName=IS_GENERATEDCOLUMN,originalColumnName=null,mysqlType=-1(FIELD_TYPE_CHAR),sqlType=1,flags=, charsetIndex=255, charsetName=UTF-8]"
     *
     * <p>
     * <p>
     * 参数：
     * catalog - 类别名称；它必须与存储在数据库中的类别名称匹配；该参数为 "" 表示获取没有类别的那些描述；为 null 则表示该类别名称不应该用于缩小搜索范围
     * schemaPattern - 模式名称的模式；它必须与存储在数据库中的模式名称匹配；该参数为 "" 表示获取没有模式的那些描述；为 null 则表示该模式名称不应该用于缩小搜索范围
     * tableNamePattern - 表名称模式；它必须与存储在数据库中的表名称匹配
     * columnNamePattern - 列名称模式；它必须与存储在数据库中的列名称匹配
     */
    public static TableInfo getColumnData(DatabaseMetaData metaData,
                                                 String catalog, String schema, String tableName, boolean isR2dbc) throws SQLException {
        start("getColumnData");
        List<ColumnData> colData = new ArrayList<>();


        ResultSet rs = metaData.getColumns(catalog, schema, tableName, "%");

        int dbType = BaseDao.detectDbType(metaData.getDriverName());

        ColumnData cd;
        int colType;
        int colCols;
        int decimalDigits = 0;
        boolean isNullable;
        boolean isAutoIncrement;
        String defaultValue;
        String remarks;

       // int prevPos=0;
        try {
            while (rs.next()) {

                String colName = rs.getString(4);

                System.out.println("TABLE_SCHEM="+rs.getObject("TABLE_SCHEM")
                        +" TABLE_CAT="+rs.getObject("TABLE_CAT")
                        +" colName="+colName
                        +" IS_GENERATEDCOLUMN="+ rs.getObject("IS_GENERATEDCOLUMN")
                        +" ORDINAL_POSITION="+rs.getObject("ORDINAL_POSITION"));

               // int curColumnPos = rs.getInt("ORDINAL_POSITION");

                //mysql-connector-java 8.x.x return already deleted columns, so skip those columns!
//                if(curColumnPos<prevPos){
//                    break;
//                }else{
//                    prevPos = curColumnPos;
//                }

                //oracle system columns
                if (colName.contains("#"))
                {
                    continue;
                }
                if (isExistsCol(colData, colName)) {
                    break;
                }

                // 17
                isNullable = NameUtils.isYes(rs.getString("IS_NULLABLE"));

                //Access driver have not a column named IS_AUTOINCREMENT
                // 23
                isAutoIncrement = (dbType != ACCESS) && NameUtils.isYes(rs.getString("IS_AUTOINCREMENT"));

                String digits = rs.getString("DECIMAL_DIGITS");
                if (StringUtils.isNumeric(digits)) {
                    decimalDigits = Integer.parseInt(digits);
                }

                // REMARKS String => 描述列的注释（可为 null）
                remarks = rs.getString("REMARKS");

                //COLUMN_DEF String => 该列的默认值，当值在单引号内时应被解释为一个字符串（可为 null）
                defaultValue = rs.getString("COLUMN_DEF");

                // column type (XOPEN values)
                colType = rs.getInt(5);
                // size e.g. varchar(20)
                colCols = rs.getInt(7);
                cd = new ColumnData(colName, colType, colCols, isNullable,
                        isAutoIncrement, decimalDigits, defaultValue, remarks, isR2dbc);
                colData.add(cd);
            }
        } catch (SQLException e) {
            System.err.println("Driver name:" + metaData.getDriverName());

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                System.err.println(i + " : " + rs.getMetaData().getColumnName(i + 1) + " -> " + rs.getMetaData().getColumnLabel(i + 1));
            }
            throw e;
        } finally {
            end();
            DbUtils.close(rs);
        }
        TableInfo tableInfo = new TableInfo(tableName);
        tableInfo.setFields(colData);

        return tableInfo;

    }

    private static boolean isExistsCol(List<ColumnData> colData, String colName) {
        for (ColumnData dt : colData) {
            if (dt.getName().equalsIgnoreCase(colName)) {
                return true;
            }
        }
        return false;
    }

    public  static  String getColumnDescription(ProjectConfig config, Map<String, T_metacolumn> cols, String tableName, ColumnData col) {
        String res;
        String columnName = col.getName();
        if (config.useCustomMetaTable) {
            DictionaryItem item = null;
            try {
                item = DictionaryCache.byId(TableGenConfig.META_COLUMN_DICT + DictionaryCache.CONDITION_SEPARATOR
                        + getTableIdByName(config, tableName) + DictionaryCache.KEY_SEPARATOR + columnName.toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
            }
            res = item == null ? columnName : item.getValue();
        } else {
            res = columnName;
        }

        if (cols != null && cols.get(columnName) != null) {
            T_metacolumn colMeta = cols.get(columnName);
            res += colMeta == null ? "" : colMeta.getDescription();
        }

        if (col.getDefaultValue() != null) {
            if (!res.contains("NotNull")) {
                res += " * NotNull, but has default value :"+  col.getDefaultValue()+ "\n";
            }

        }
        return res;
    }

    private static int getTableIdByName(ProjectConfig config, String tableName) {
        if (config.useCustomMetaTable) {
            DictionaryItem item = null;
            try {
                item = DictionaryCache.byValue(
                        TableGenConfig.TABLE_NAME_DICT + DictionaryCache.KEY_SEPARATOR + tableName.toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return item == null ? 0 : Integer.parseInt(item.getValue());
        } else {
            return 0;
        }
    }


    public static String getColumnAnnotation(ProjectConfig config, TableSetInfo setInfo, String tableName, ColumnData cd) {
        StringBuilder sb = new StringBuilder();
        if (config.useAnnotation) {

            if (setInfo.getTableDataExt()  != null && setInfo.getTableDataExt().get(tableName) != null) {
                Map<String, T_metacolumn> cols = setInfo.getTableDataExt().get(tableName).getColumns();
                if (cols != null && cols.get(cd.getName()) != null) {
                    T_metacolumn col = cols.get(cd.getName());
                    sb.append(col.getAnnotation() == null ? "" : col.getAnnotation() + "\n");
                } else {
                    LOG.info("There NO settings for table columns:" + tableName);
                }
            }

            if ("String".equals(cd.getJavaType())) {
                //if columns is JSON type, there is no column size.
                if (sb.indexOf("@Size") < 0 && cd.getColumns()>0) {
                    sb.append("@Size(max=").append(cd.getColumns()).append(")\n");
                }
            }
            if (isNeedNotNullAnnotation(config, cd)) {
                if (sb.indexOf("@NotNull") < 0) {
                    if(sb.length()>0){
                        sb.append("    ");
                    }
                    sb.append("@NotNull\n");
                }
            }

            if(config.useSwagger && StringUtils.isNotBlank(cd.getComment())){
                if(sb.length()>0){
                    sb.append("    ");
                }
                sb.append("@Schema(title = \"").append(cd.getComment()).append("\")");
            }
        }

        return org.apache.commons.lang3.StringUtils.stripEnd(sb.toString(), "\n");
    }

    private static boolean isNeedNotNullAnnotation(ProjectConfig config, ColumnData cd) {
        return !cd.isNullable() && cd.getDefaultValue() == null && (!config.useAutoincrement || !cd.isAutoIncrement());
    }



}
