package com.bixuebihui.tablegen;

import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableInfo;
import com.bixuebihui.tablegen.entry.TableSetInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import static java.sql.ResultSetMetaData.columnNullable;
import static com.bixuebihui.db.Dialect.*;
import static org.springframework.data.util.Pair.toMap;

/**
 * Suitable to use when view is not available, for example the DBA not allowed to create view for some reasons.
 * So we use plain sql for ORM
 * @author xwx
 */
public class OnFlyViewUtils {

    public static TableInfo getColumnData(ResultSetMetaData rs, String onFlyViewName, int dbType, ProjectConfig config, Connection cn, IDbHelper dbHelper, DatabaseMetaData metaData) throws SQLException {
        List<ColumnData> colData = new ArrayList<>();

        ColumnData cd;
        int colType;
        int colCols;
        int decimalDigits;
        boolean isNullable;
        boolean isAutoIncrement;
        String remarks;

        Set<String> existsFields = new HashSet<>();
        Map<String, String> colFromTable = new HashMap<>();


        try {
            for (int i=1; i<=rs.getColumnCount(); i++) {

                String colName = rs.getColumnLabel(i);

                if(existsFields.contains(colName.toLowerCase())){
                    continue;
                }

                // 17
                isNullable = rs.isNullable(i) == columnNullable;

                //Access driver have not a column named IS_AUTOINCREMENT
                // 23
                isAutoIncrement = (dbType != ACCESS) && rs.isAutoIncrement(i);

                //.getString("DECIMAL_DIGITS");
                decimalDigits = rs.getPrecision(i);

                // REMARKS String => 描述列的注释（可为 null）
                //co.getString("REMARKS");
                remarks = rs.getColumnLabel(i) ;

                //COLUMN_DEF String => 该列的默认值，当值在单引号内时应被解释为一个字符串（可为 null）

                // column type (XOPEN values)
                colType = rs.getColumnType(i);
                // size e.g. varchar(20)
                colCols = rs.getColumnDisplaySize(i);
                cd = new ColumnData(colName, colType, colCols, isNullable,
                        isAutoIncrement, decimalDigits, null, remarks, config.getR2dc());
                colData.add(cd);

                existsFields.add(cd.getName().toLowerCase());
                colFromTable.put(colName, rs.getTableName(i));
            }
        } catch (SQLException e) {
            System.err.println("Table name:" );

            for (int i = 1; i <= rs.getColumnCount(); i++) {
                System.err.println(i + " : " +rs.getSchemaName(i) + rs.getTableName(i) + rs.getColumnName(i) + " -> " + rs.getColumnLabel(i));
            }
            throw e;
        }
        TableInfo tableInfo = new TableInfo(onFlyViewName);
        tableInfo.setFields(colData);
        tableInfo.setView(true);
        fillComment(tableInfo, colFromTable, config, cn, dbHelper, metaData);

        return tableInfo;

    }

    private static void fillComment(TableInfo tableInfo, Map<String, String> colFromTable,
                                    ProjectConfig config, Connection cn, IDbHelper dbHelper, DatabaseMetaData metaData) throws SQLException {
        if(colFromTable.isEmpty()){
            return;
        }
        Map<String, TableInfo> tableInfoMap = colFromTable.values().stream()
                .distinct()
                .filter(tableName -> !StringUtils.isBlank(tableName))
                .map(tableName -> {
                    try {
                        return Pair.of(tableName, TableSetInfo.getTableInfoWithComment(
                                dbHelper, metaData,
                                tableName, config.getCatalog(), config.getSchema(), config.getR2dc()));

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(toMap());

        for(ColumnData cd : tableInfo.getFields()){
            String tableName = colFromTable.get(cd.getName());
            if(StringUtils.isEmpty(tableName)){
                continue;
            }

            TableInfo plainTableInfo = tableInfoMap.get(tableName);
            if(plainTableInfo!=null){
                cd.setRemarks(plainTableInfo.getFields().stream()
                        .filter(c -> c.getName().equalsIgnoreCase(cd.getName()))
                        .map(ColumnData::getRemarks)
                        .findFirst()
                        .orElse(cd.getRemarks()));
            }
        }


    }


}
