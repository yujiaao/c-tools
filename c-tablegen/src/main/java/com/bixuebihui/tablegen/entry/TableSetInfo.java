package com.bixuebihui.tablegen.entry;

import com.bixuebihui.algorithm.LRULinkedHashMap;
import com.bixuebihui.db.Dialect;
import com.bixuebihui.generated.tablegen.business.T_metatableManager;
import com.bixuebihui.generated.tablegen.pojo.T_metacolumn;
import com.bixuebihui.generated.tablegen.pojo.T_metatable;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.jdbc.JDBCUtils;
import com.bixuebihui.jdbc.RowMapperResultReader;
import com.bixuebihui.jdbc.SqlFileExecutor;
import com.bixuebihui.tablegen.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static com.bixuebihui.db.Dialect.*;
import static com.bixuebihui.tablegen.TableUtils.isMysql;
import static com.bixuebihui.tablegen.TableUtils.isSQLServer;

/**
 * @author xwx
 */
public class TableSetInfo implements KeyInfo {
    public static final String DB_ERROR = "[GEN]Error setup database";
    private static final Logger LOG = LoggerFactory.getLogger(TableSetInfo.class);
    private final Map<String, List<ForeignKeyDefinition>> foreignKeyImCache = new LRULinkedHashMap<>(
            400);
    private final Map<String, List<String>> keyCache = new LRULinkedHashMap<>(500);

    /**
     * union keys and indexes store in multiline of metadata, currently only support single field index
     * so need use: Map<tableName, Map<indexName, List<columnNames>> indexCache
     */
    private final Map<String, Map<String, List<String>>> indexCache = new HashMap<>(200);


    private final Map<String, List<ForeignKeyDefinition>> foreignKeyExCache = new LRULinkedHashMap<>(
            400);
    private Map<String, T_metatable> tableDataExt;
    private LinkedHashMap<String, TableInfo> tableInfos;
    private LinkedHashMap<String, TableInfo> viewInfos;

    protected static int getPos(List<?> list, int desiredPos) {
        return Math.min(list.size(), desiredPos);
    }

    private static void fillSQLServerTableComment(IDbHelper dbHelper, TableInfo tableInfo) {
        String tableName = tableInfo.getName();
        Map<String, String> map = TableUtils.getTableCommentForSQLServer(dbHelper, List.of(tableName));
        tableInfo.setComment(map.get(tableName));
    }


    /**
     * this below two methods is valid only for MySQL
     *
     * @param conn      database connection
     * @param tableInfo table structure
     *                  tableInfo with comment
     */
    private static void fillMySQLComment(Connection conn, TableInfo tableInfo) {
        String tableName = tableInfo.getName();
        try (Statement showTableStatement = conn.createStatement();
             ResultSet showTableResultSet = showTableStatement.executeQuery("show create table " + tableName)
        ) {
            showTableResultSet.next();
            String createTableSql = showTableResultSet.getString(2);
            findFieldComment(tableName, tableInfo.getFields(), createTableSql);
            String comment = findTableComment(createTableSql);
            tableInfo.setComment(comment);
        } catch (SQLException e) {
            LOG.warn("fail to execute: show create table " + tableName);
            LOG.warn(e.getMessage(), e);
        }
    }


    protected static void findFieldComment(String tableName, List<ColumnData> fields, String tableSql) {
        String fieldSql = tableSql.substring(tableSql.indexOf("(") + 1, tableSql.lastIndexOf(")"));
        String[] fieldDesc = StringUtils.split(fieldSql, "\n");
        Map<String, ColumnData> commentMap = new HashMap<>();
        for (ColumnData fieldInfo : fields) {
            commentMap.put(fieldInfo.getName().toUpperCase(), fieldInfo);
        }
        for (String desc : fieldDesc) {
            String trim = StringUtils.trim(desc);
            String fieldName = StringUtils.split(trim, " ")[0].toUpperCase();
            fieldName = replaceForIdentifier(fieldName);
            String upper = desc.toUpperCase();
            if (upper.contains("AUTO_INCREMENT")) {
                if (Arrays.asList(StringUtils.split(upper, " ")).contains("AUTO_INCREMENT")) {
                    commentMap.get(fieldName).setAutoIncrement(true);
                }
            }
            if (!upper.contains("COMMENT")) {
                continue;
            }
            String[] splits = StringUtils.splitByWholeSeparator(trim, "COMMENT");
            String comment = splits[splits.length - 1];
            comment = replaceComment(comment);
            if (commentMap.containsKey(fieldName)) {
                commentMap.get(fieldName).setComment(comment);
            } else {
                LOG.info("table:" + tableName + ",fileName:" + desc);
            }
        }
    }

    private static String findTableComment(String tableSql) {
        if (!tableSql.contains("COMMENT=")) {
            return "";
        }
        String classCommentTmp = tableSql.substring(tableSql.lastIndexOf("COMMENT=") + 8).trim();
        classCommentTmp = replaceForIdentifier(classCommentTmp);
        classCommentTmp = StringUtils.trim(classCommentTmp);
        return classCommentTmp;
    }

    private static String replaceForIdentifier(String classCommentTmp) {
        classCommentTmp = StringUtils.split(classCommentTmp, " ")[0];
        classCommentTmp = StringUtils.replace(classCommentTmp, "'", "");
        classCommentTmp = StringUtils.replace(classCommentTmp, ";", "");
        classCommentTmp = StringUtils.replace(classCommentTmp, ",", "");
        classCommentTmp = StringUtils.replace(classCommentTmp, "`", "");
        classCommentTmp = StringUtils.replace(classCommentTmp, "\n", "");
        classCommentTmp = StringUtils.replace(classCommentTmp, "\t", "");
        classCommentTmp = StringUtils.trim(classCommentTmp);
        return classCommentTmp;
    }

    protected static String replaceComment(String classCommentTmp) {
        classCommentTmp = StringUtils.replace(classCommentTmp, "\n", " ");
        classCommentTmp = StringUtils.replace(classCommentTmp, "\t", " ");
        classCommentTmp = StringUtils.trim(classCommentTmp);

        if (classCommentTmp.endsWith(",")) {
            classCommentTmp = classCommentTmp.substring(0, classCommentTmp.length() - 1);
        }

        //classCommentTmp.stripLeading("'").stripTrailing("',");
        //StarRocks commend quoted in double quotes "\""
        classCommentTmp = StringUtils.strip(classCommentTmp, "'\"");


        return classCommentTmp;
    }

    static String insertTop1(String sql) {
        if (sql.contains(" top ") || sql.contains(" TOP ")) {
            return sql;
        }

        StringTokenizer tokenizer = new StringTokenizer(sql);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        while (tokenizer.hasMoreElements()) {
            String next = tokenizer.nextElement().toString();
            if (first && next.equalsIgnoreCase("select")) {
                sb.append("select top 1 ");
                first = false;
            } else {
                sb.append(next).append(" ");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public LinkedHashMap<String, TableInfo> getViewInfos() {
        return viewInfos;
    }

    public void setViewInfos(LinkedHashMap<String, TableInfo> viewInfos) {
        this.viewInfos = viewInfos;
    }

    public Map<String, T_metatable> getTableDataExt() {
        return tableDataExt;
    }

    public void setTableDataExt(Map<String, T_metatable> tableDataExt) {
        this.tableDataExt = tableDataExt;
    }

    public Map<String, List<ForeignKeyDefinition>> getForeignKeyImCache() {
        return foreignKeyImCache;
    }

    @Override
    public Map<String, List<String>> getKeyCache() {
        return keyCache;
    }

    public Map<String, List<ForeignKeyDefinition>> getForeignKeyExCache() {
        return foreignKeyExCache;
    }

    public LinkedHashMap<String, TableInfo> getTableInfos() {
        return tableInfos;
    }

    public void setTableInfos(LinkedHashMap<String, TableInfo> tableInfos) {
        this.tableInfos = tableInfos;
    }

    public List<ColumnData> getTableCols(String tableName) {
        if(this.getTableInfos()==null){
            LOG.error("tableInfos is null");
            return Collections.emptyList();
        }
        if(this.getTableInfos().get(tableName)==null){
            LOG.error("tableInfos.get("+tableName+") is null");
            return Collections.emptyList();
        }
        return this.getTableInfos().get(tableName).getFields();
    }

    public List<ColumnData> getViewCols(String tableName) {
        return this.getViewInfos().get(tableName).getFields();
    }

    public String tableName2ClassName(String tableName) {
        if (tableDataExt != null && tableDataExt.get(tableName) != null) {
            String alias = tableDataExt.get(tableName).getClassname();

            LOG.debug("Pojo class alias is: " + alias);

            if (StringUtils.isNotEmpty(alias)) {
                return alias.trim();
            }
        }
        return tableName;
    }

    private T_metatable getTableDetail(String tableName, ProjectConfig config) {
        T_metatable res;
        if (tableDataExt != null) {
            res = tableDataExt.get(tableName);
        } else {
            res = new T_metatable();
            boolean any = false;
            if (config.getPojoNodeInterfaceList().contains(tableName)) {
                res.setIsnode(true);
                any = true;
            }
            if (config.getPojoStateInterfaceList().contains(tableName)) {
                res.setIsstate(true);
                any = true;
            }
            if (config.getPojoVersionInterfaceList().contains(tableName)) {
                res.setIsversion(true);
                any = true;
            }
            if (config.getPojoModifyDateInterfaceList().contains(tableName)) {
                res.setIsmodifydate(true);
                any = true;
            }
            if (config.getPojoUuidInterfaceList().contains(tableName)) {
                res.setIsuuid(true);
                any = true;
            }
            if (!any) {
                res = null;
            }
        }
        return res;
    }

    public String getInterface(String tableName, ProjectConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append(" implements Serializable");

        T_metatable tab = getTableDetail(tableName, config);
        if (tab != null) {
            if (tab.getIsnode()) {
                sb.append(", ").append(config.getPojoNodeInterface());
            }
            if (tab.getIsstate()) {
                sb.append(", ").append(config.getPojoStateInterface());
            }
            if (tab.getIsversion()) {
                sb.append(", ").append(config.getPojoVersionInterface());
            }
            if (tab.getIsmodifydate()) {
                sb.append(", ").append(config.getPojoModifyDateInterface());
            }
            if (tab.getIsuuid()) {
                sb.append(", ").append(config.getPojoUuidInterface());
            }
            if (StringUtils.isNotBlank(tab.getExtrainterfaces())) {
                sb.append(", ").append(tab.getExtrainterfaces());
            }
        }
        return sb.toString();
    }

    public Map<String, T_metacolumn> getColumnsExtInfo(String tableName) {
        if (getTableDataExt() != null && getTableDataExt().get(tableName) != null) {
            return getTableDataExt().get(tableName).getColumns();
        }
        return Collections.emptyMap();
    }

    public Map<String, Map<String, List<String>>> getIndexCache() {
        return indexCache;
    }

    public Map<String, T_metatable> getTableDataExt(IDbHelper dbHelper, LinkedHashMap<String, TableInfo> tables)
            throws SQLException {
        HashMap<String, T_metatable> ht = new HashMap<>(tables.size() * 2);
        T_metatableManager daoMetaTable = new T_metatableManager(dbHelper.getDataSource());
        daoMetaTable.setDbHelper(dbHelper);

        setupMetaTable(dbHelper, daoMetaTable);
        List<String> names = new ArrayList<>(32);
        for (TableInfo table : tables.values()) {
            names.add(table.getName());
        }

        Collection<T_metatable> c = daoMetaTable.getTableDataExt(names);
        for (T_metatable t : c) {
            ht.put(t.getTname(), t);
        }
        return ht;
    }

    public boolean insertOrUpdateMetatable(T_metatable metatable, T_metatableManager daoMetaTable) {
        if (metatable.getTid() <= 0) {
            return daoMetaTable.insertAutoNewKey(metatable);
        } else {
            return daoMetaTable.updateByKey(metatable);
        }
    }

    public boolean initTableData(LinkedHashMap<String, TableInfo> tableNames, T_metatableManager daoMetaTable) {
        if (tableNames != null) {
            T_metatable[] infos = new T_metatable[tableNames.size()];
            int i = 0;
            for (TableInfo tableInfo : tableNames.values()) {
                T_metatable t = new T_metatable();
                t.setTname(tableInfo.getName());
                t.setDescription(tableInfo.getComment());
                t.setIsnode(false);
                t.setIsstate(false);
                t.setIsversion(false);
                t.setIsmodifydate(false);
                t.setIsuuid(false);
                t.setTid(daoMetaTable.getNextKey());

                infos[i] = t;
                i++;
            }
            return daoMetaTable.insertBatch(infos, null);
        } else {
            return false;
        }
    }

    public Map<String, T_metatable> getExtraTableDataFromXml(String extraSettingFileName,
                                                             Map<String, T_metatable> settingFromDb) throws IOException {
        File file = new File(extraSettingFileName);
        if (file.exists()) {
            LOG.info("Find extra setting file: " + extraSettingFileName);
            if (settingFromDb == null) {
                settingFromDb = new HashMap<>();
            }
            String xml = FileUtils.readFileToString(file, "UTF-8");

            Map<String, T_metatable> res = PojoPropertiesParser.parse(xml);

            return PojoPropertiesParser.mergeTableSetting(settingFromDb, res);
        } else {
            LOG.info("WARNING: File for value extra_setting not exists:" + extraSettingFileName);
            return settingFromDb;
        }
    }

    public boolean setupMetaTable(IDbHelper dbHelper, T_metatableManager daoMetaTable) {
        Connection conn = null;
        boolean res = false;
        try {
            LOG.info("[Auto] Setup Metadata Tables to DB...");
            conn = dbHelper.getConnection();
            if (!JDBCUtils.tableOrViewExists(null, null, daoMetaTable.getTableName(), conn)) {
                SqlFileExecutor ex = new SqlFileExecutor();
                String filename;

                if (daoMetaTable.getDbType() == POSTGRESQL) {
                    filename = "postgresql";
                } else {
                    filename = "mysql";
                }

                DefaultResourceLoader loader = new DefaultResourceLoader();
                String beanConfigFile = "classpath:dbscript/ext." + filename + ".sql";
                LOG.info(loader.getResource(beanConfigFile).getFilename());
                ex.execute(conn, loader.getResource(beanConfigFile).getInputStream());

                res = initTableData(getTableInfos(), daoMetaTable);
            }
            if (res) {
                LOG.info("[Auto] The metadata tables created!");
            } else {
                LOG.warn(DB_ERROR);
            }
        } catch (Exception e) {
            LOG.error(DB_ERROR, e);
        } finally {
            JDBCUtils.close(conn);
        }
        return res;
    }

    /**
     * 1. TABLE_CAT==test
     * 2. TABLE_SCHEM==null
     * 3. TABLE_NAME=  表名称
     * 4. NON_UNIQUE==false  索引值是否可以不惟一。TYPE 为 tableIndexStatistic 时索引值为 false
     * 5. INDEX_QUALIFIER==
     * 6. INDEX_NAME==PRIMARY   索引名称；TYPE 为 tableIndexStatistic 时索引名称为 null
     * 7. TYPE==3 ,short => 索引类型：
     * 0 tableIndexStatistic - 此标识与表的索引描述一起返回的表统计信息
     * 1 tableIndexClustered - 此为集群(聚簇？)索引
     * 2 tableIndexHashed - 此为散列索引
     * 3 tableIndexOther - 此为某种其他样式的索引
     * 8、 ORDINAL_POSITION==1   索引中的列序列号；TYPE 为 tableIndexStatistic 时该序列号为零
     * 9.  COLUMN_NAME String => 列名称；TYPE 为 tableIndexStatistic 时列名称为 null
     * 10. ASC_OR_DESC==A, string => 列排序序列，”A” => 升序，”D” => 降序，如果排序序列不受支持，可能为 null；TYPE 为 tableIndexStatistic 时排序序列为 null
     * 11. CARDINALITY==0,  int => TYPE 为 tableIndexStatistic 时，它是表中的行数；否则，它是索引中惟一值的数量。
     * 12. PAGES==0, int => TYPE 为 tableIndexStatisic 时，它是用于表的页数，否则它是用于当前索引的页数。
     * 13。 FILTER_CONDITION==null, String => 过滤器条件，如果有的话。（可能为 null）
     *
     * @param config    generator configuration
     * @param dbHelper  database helper
     * @param tableName table name
     */
    protected void initIndex(ProjectConfig config, DatabaseMetaData metaData, String tableName) {
        // use a hashmap to temporarily store the indexes as well
        // so we can avoid duplicate values.
        String primary = "PRIMARY";
        String indexColName, indexName;
        short indexType, pos;
        Map<String, List<String>> indexData = new HashMap<>();

        /*
         * metaData.getIndexInfo 参数说明：
         * catalog : 类别名称，因为存储在此数据库中，所以它必须匹配类别名称。该参数为 “” 则检索没有类别的描述，为 null 则表示该类别名称不应用于缩小搜索范围
         * schema : 模式名称，因为存储在此数据库中，所以它必须匹配模式名称。该参数为 “” 则检索那些没有模式的描述，为 null 则表示该模式名称不应用于缩小搜索范围
         * table : 表名称，因为存储在此数据库中，所以它必须匹配表名称
         * unique : 该参数为 true 时，仅返回惟一值的索引；该参数为 false 时，返回所有索引，不管它们是否惟一
         * approximate : 该参数为 true 时，允许结果是接近的数据值或这些数据值以外的值；该参数为 false 时，要求结果是精确结果
         */
        try (ResultSet r = metaData.getIndexInfo(config.getCatalog(), config.getSchema(), tableName, false, false)) {
            while (r.next()) {
                indexName = r.getString(6);
                //skip primary key, 主键不处理
                if (primary.equals(indexName)) {
                    continue;
                }
                indexType = r.getShort(7);
                indexColName = r.getString(9);
                pos = r.getShort(8);
                if (indexType != DatabaseMetaData.tableIndexStatistic) {
                    List<String> cols = indexData.getOrDefault(indexName, new ArrayList<>());
                    cols.add(getPos(cols, pos), indexColName);
                    indexData.putIfAbsent(indexName, cols);
                }
            }
        } catch (SQLException e) {
            LOG.warn("initIndex", e);
        }
        indexCache.put(tableName, indexData);
    }

    private void initExportKeys(ProjectConfig config, IDbHelper dbHelper, DatabaseMetaData metaData, String tableName) throws SQLException {


            if (isMysql(metaData)) {
                //针对mysql的优化, 一次加载全部外键
                if (getForeignKeyExCache().isEmpty()) {
                    getForeignKeyExCache().putAll(TableUtils.getAllMySQLExportKeys(dbHelper, config.getTableOwner()));
                }
                if (getForeignKeyExCache().containsKey(tableName)) {
                    LOG.info(tableName + " has  ExportKey in cache:" + getForeignKeyExCache().get(tableName));
                } else {
                    LOG.debug(tableName + " does not have a ExportKey in cache");
                }
                return;
            }
            List<ForeignKeyDefinition> foreignKeyData = TableUtils.getTableExportedKeys(metaData, config.getCatalog(), config.getTableOwner(), tableName);
            getForeignKeyExCache().put(tableName, foreignKeyData);

    }

    /**
     * Retrieves all the table data required.
     */
    public synchronized void getTableData(ProjectConfig config, IDbHelper dbHelper, DatabaseMetaData metaData) throws SQLException, IOException {

        LinkedHashMap<String, TableInfo> col = getTableInfos();

        if (CollectionUtils.isEmpty(col)) {

            String catalog = config.getCatalog();
            String schema = config.getSchema();
            boolean isR2dbc = config.getR2dc();
            //表名
            List<String> tableNames =
                    TableUtils.getTableData(metaData, catalog, schema,
                            config.getTableOwner(), config.getTablesList(), config.getExcludeTablesList());

            LOG.info("find tables:{}", tableNames);
            LinkedHashMap<String, TableInfo> tables = new LinkedHashMap<>();
            for (String name : tableNames) {
                TableInfo table = getTableInfoWithComment(dbHelper, metaData, name, catalog, schema, isR2dbc);

                tables.put(name, table);

                // Primary keys info
                List<String> keyData = TableUtils.getTableKeys(metaData, catalog, schema, name);
                getKeyCache().put(name, keyData);

                // foreign keys import info
                initImportKeys(config,  dbHelper, metaData, name);

                // foreign keys export info
                initExportKeys(config, dbHelper, metaData, name);

                // index info
                initIndex(config, metaData, name);


            }

            setTableInfos(tables);
        } else {
            LOG.info("tableNames already set, retrieve from db skipped.");
        }

        // Custom meta info from db table
        if (config.isUseCustomMetaTable()) {
            setTableDataExt(getTableDataExt(dbHelper, getTableInfos()));
        }

        // Custom meta info from xml
        if (StringUtils.isNotEmpty(config.getExtraSetting())) {
            setTableDataExt(getExtraTableDataFromXml(
                    config.getBaseDir() + config.getExtraSetting(), getTableDataExt()));
        }

    }

    public static TableInfo getTableInfoWithComment(IDbHelper dbHelper, DatabaseMetaData metaData, String name, String catalog, String schema, boolean isR2dbc) throws SQLException {
        LOG.info("extract info for table:{}", name);
        TableInfo table = new TableInfo(name);

        // Fill columns info
        TableInfo newInfo = TableUtils.getColumnData(dbHelper, metaData, catalog, schema, name, isR2dbc);
        table.setFields(newInfo.getFields());
        fillComment(dbHelper, metaData, table);
        return table;
    }

    private static void fillComment(IDbHelper dbHelper, DatabaseMetaData metaData, TableInfo table) throws SQLException {
        // table & column's  comment
        if (isMysql(metaData)) {
            try (Connection cn = dbHelper.getConnection()) {
                fillMySQLComment(cn, table);
            }
        } else if (isSQLServer(metaData)) {
            fillSQLServerTableComment(dbHelper, table);
        }
    }

    /**
     * Retrieves all the table data required.
     */
    public synchronized void getOnFlyViewData(ProjectConfig config, IDbHelper dbHelper, DatabaseMetaData metaData) throws SQLException, IOException {

        if (config.getViewList() == null) {
            return;
        }
        //虚视图
        LinkedHashMap<String, TableInfo> viewInfos = new LinkedHashMap<>();

        try (Connection cn = dbHelper.getConnection()) {
            int dbType = Dialect.detectDbType(cn.getMetaData().getDriverName());

            for (String name : config.getViewList().keySet()) {
                String originalSql = config.getViewList().get(name);
                String sql = addLimit(originalSql, dbType);

                dbHelper.executeQuery(sql,
                        new Object[0], new RowMapperResultReader<>((rs, index, set) -> null) {
                            @Override
                            public List<Object> processResultSet(ResultSet rs, Set<String> set) {

                                try {
                                    TableInfo info = OnFlyViewUtils.getColumnData(rs.getMetaData(),
                                            name, dbType, config, cn, dbHelper, metaData);
                                    info.setComment(name);
                                    info.setViewSql(originalSql);
                                    viewInfos.put(name, info);
                                } catch (SQLException e) {
                                    LOG.error("getOnFlyViewData", e);
                                }

                                return Collections.emptyList();
                            }
                        });

            }

            setViewInfos(viewInfos);

        }

    }

    private String addLimit(String sql, int dbType) {
        if (dbType == MYSQL) {
            sql = sql.contains("limit") ? sql : sql + " limit 1";
        } else if (dbType == SQLSERVER_2005_AND_UP) {
            sql =
                    insertTop1(sql);

            // sql  = getPagingSqlSqlServer2005(sql, 0,1);
        }

        return sql;
    }

    private void initImportKeys(ProjectConfig config, IDbHelper dbHelper, DatabaseMetaData metaData, String tableName) throws SQLException {

            if (isMysql(metaData)) {
                //针对mysql的优化, 一次加载全部外键
                if (getForeignKeyImCache().isEmpty()) {
                    getForeignKeyImCache().putAll(TableUtils.getAllMySQLImportKeys(dbHelper, config.getTableOwner()));
                }
                if (getForeignKeyImCache().containsKey(tableName)) {
                    LOG.info("{} has  ImportKey in cache:{}", tableName, getForeignKeyImCache().get(tableName));
                } else {
                    LOG.debug("{} have not a ImportKey in cache", tableName);
                }
                return;

            }
            List<ForeignKeyDefinition> foreignKeyData = TableUtils.getTableImportedKeys(metaData, config.getCatalog(), config.getTableOwner(), tableName);
            getForeignKeyImCache().put(tableName, foreignKeyData);

    }

    /**
     * Selects the primary keys for a particular table.
     */
    @Override
    public List<String> getTableKeys(String tableName) {
        return getKeyCache().getOrDefault(tableName, Collections.emptyList());
    }

    /**
     * Selects the Imported Keys defined for a particular table.
     */
    public List<ForeignKeyDefinition> getTableImportedKeys(String tableName) {
        return getForeignKeyImCache().getOrDefault(tableName, Collections.emptyList());
    }

    /**
     * Selects the Exported Keys defined for a particular table.
     */
    public List<ForeignKeyDefinition> getTableExportedKeys(String tableName) {
        return getForeignKeyExCache().getOrDefault(tableName, Collections.emptyList());
    }

    /**
     * Selects the indexes for a particular table.
     */
    public Map<String, List<String>> getTableIndexes(String tableName) {
        return getIndexCache().getOrDefault(tableName, Collections.emptyMap());
    }


}
