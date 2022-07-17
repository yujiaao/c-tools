package com.bixuebihui.tablegen;

import com.bixuebihui.datasource.BitmechanicDataSource;
import com.bixuebihui.dbcon.DatabaseConfig;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.tablegen.diffhandler.DiffHandler;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableInfo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @author xwx
 */
public class DbDiff {
    private static final Logger LOG = LoggerFactory.getLogger(DbDiff.class);

    Database db1;
    Database db2;

    private Map<String, List<ColumnData>> cachedTableData = null;


    private final HashMap<String, List<ColumnData>> tableData = new HashMap<>();
    /**
     * 数据库不一致的处理程序
     */
    private final List<DiffHandler> diffHandlerList = new ArrayList<>();


    public DbDiff(Map<String, List<ColumnData>> cachedTableData, Connection dsdstcfg, String catalog, String schema) throws SQLException {

        this.cachedTableData = cachedTableData;

        this.db2 = new Database(dsdstcfg.getMetaData());
        this.db2.catalog = catalog;
        this.db2.schema = schema;
    }


    public DbDiff() {

    }

    private void addTableData(String tableName, List<ColumnData> columnDatas) {
        tableData.put(tableName, columnDatas);
    }

    public void addDiffHandler(DiffHandler dh) {
        diffHandlerList.add(dh);
    }

    protected DataSource makeDataSource(DatabaseConfig dbconfig) {
        BitmechanicDataSource ds = new BitmechanicDataSource();
        ds.setDatabaseConfig(dbconfig);
        return ds;
    }

    List<String> getTableData(Database database) throws SQLException {
        return TableUtils.getTableData(database.metaData, database.catalog, database.schema,
                database.tableOwner, database.includeTablesList, database.excludeTablesList);
    }

    private List<String> getCachedTables() {

        Iterator<Map.Entry<String, List<ColumnData>>> i = cachedTableData.entrySet().iterator();

        List<String> v = new ArrayList<>();

        while (i.hasNext()) {
            v.add(i.next().getKey());
        }

        return v;
    }

    private List<ColumnData> getCachedColumns(String tableName) {
        return cachedTableData.get(tableName);
    }

    public void compareTables(boolean isR2dbc) throws SQLException, IOException {


        List<String> tab1;


        if (db1 == null) {
            tab1 = getCachedTables();

        } else {
            tab1 = getTableData(db1);
        }


        List<String> tab2 = getTableData(db2);

        storeTableData( tab2, isR2dbc);

        compareTableData(tab1, tab2, isR2dbc);
        for (DiffHandler dh : diffHandlerList) {
            dh.processEnd(tableData);
        }
    }

    private void storeTableData( List<String> tab2, boolean isR2dbc) throws SQLException {

        for (String t : tab2) {

            List<ColumnData> c = getColumns(db2, t, isR2dbc).getFields();

            addTableData(t, c);
        }

    }

    /**
     * 比对表数据
     */
    public void compareTableData(List<String> tab1, List<String> tab2,   boolean isR2dbc) throws SQLException, IOException {
        dumpDiffTabs(tab1, tab2);

        Collection intersection = CollectionUtils.intersection(tab1, tab2);
        compareTablesColumns(intersection, isR2dbc);

    }

    public void compareTablesColumns(Collection<String> tabs,boolean isR2dbc)
            throws SQLException, IOException {
        for (String tableName : tabs) {
            compareColumns(tableName, isR2dbc);
        }
    }

    protected TableInfo getColumns(Database database, String tableName, boolean isR2dbc)
            throws SQLException {
        return  TableUtils.getColumnData(null, database.metaData, database.catalog, database.schema, tableName, isR2dbc);
    }

    public void compareColumns(String tableName, boolean isR2dbc) throws SQLException, IOException {

        List<ColumnData> cols1;

        if (db1 == null) {

            cols1 = getCachedColumns(tableName);

            if (cols1 == null) {
                cols1 = new ArrayList<>();
            }

        } else {
            cols1 = getColumns(db1, tableName, isR2dbc).getFields();
        }


        List<ColumnData> cols2 = getColumns(db2, tableName,isR2dbc).getFields();


        if (dumpDiffCols(cols1, cols2)) {

            /*
             * 当表不一致时，通知所有handler处理表的不一致
             */
            for (DiffHandler dh : diffHandlerList) {
                dh.processTableDiff(tableName);
            }

            LOG.debug("====>" + tableName + " changed");
        }
    }

    /**
     * 比较两个集合
     * show difference to console
     *
     * @param tab1 previews table
     * @param tab2 now table
     */
    protected void dumpDiffTabs(List<String> tab1, List<String> tab2) throws IOException {
        Collection<String> res1 = CollectionUtils.subtract(tab1, tab2);
        Collection<String> res2 = CollectionUtils.subtract(tab2, tab1);

        if (!res1.isEmpty()) {
            LOG.debug("previous - current =");
            outputSortedList(res1);
        }
        if (!res2.isEmpty()) {
            LOG.debug("current - previous =");
            outputSortedList(res2);

            for (String tableName : res2) {
                /*
                 * 当原表没有生成时，进行处理
                 */
                for (DiffHandler dh : diffHandlerList) {
                    dh.processTableDiff(tableName);
                }
            }

        }
        Collection res3 = CollectionUtils.disjunction(tab1, tab2);
        if (!res3.isEmpty()) {
            LOG.debug("|previous-current|=");
            outputSortedList(res3);
        }


    }

    private void outputSortedList(Collection<String> res2) {
        ArrayList<String> list = new ArrayList<>(res2);
        Collections.sort(list);
        LOG.debug(list.toString());
    }

    /**
     * 比较两个集合
     *
     * @param cols1 columns of first table
     * @param cols2 columns of second table
     * @return true if identity
     */
    protected boolean dumpDiffCols(List<ColumnData> cols1,
                                   List<ColumnData> cols2) {
        Collection<ColumnData> res1 = CollectionUtils.subtract(cols1, cols2);
        Collection<ColumnData> res2 = CollectionUtils.subtract(cols2, cols1);
        boolean changed = false;
        if (!res1.isEmpty()) {
            LOG.debug("cols1 - cols2 =");
            LOG.debug(res1.toString());
            changed = true;
        }

        if (!res2.isEmpty()) {
            LOG.debug("cols2 - cols1 =");
            LOG.debug(res2.toString());
            changed = true;
        }

        return changed;

    }

    public static class Database {
        DatabaseMetaData metaData;
        String schema = null;
        String catalog = null;
        String tableOwner = "";
        Map<String, String> includeTablesList = null;
        Map<String, String> excludeTablesList = null;
        public Database(DatabaseMetaData metaData2) {
            this.metaData = metaData2;
        }
    }

}
