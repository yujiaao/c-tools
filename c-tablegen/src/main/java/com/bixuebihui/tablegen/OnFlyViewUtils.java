package com.bixuebihui.tablegen;

import com.bixuebihui.jdbc.BaseDao;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableInfo;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.ResultSetMetaData.columnNullable;
import static com.bixuebihui.db.Dialect.*;

/**
 * Suitable to use when view is not available, for example the DBA not allowed to create view for some reasons.
 * So we use plain sql for ORM
 * @author xwx
 */
public class OnFlyViewUtils {

    public static TableInfo getColumnData(ResultSetMetaData rs, String onFlyViewName, int dbType) throws SQLException {
        List<ColumnData> colData = new ArrayList<>();

        ColumnData cd;
        int colType;
        int colCols;
        int decimalDigits;
        boolean isNullable;
        boolean isAutoIncrement;
        String remarks;

        try {
            for (int i=1; i<=rs.getColumnCount(); i++) {

                String colName = rs.getColumnLabel(i);

                boolean existsCol = false;
                for (ColumnData dt : colData) {
                    if (dt.getName().equalsIgnoreCase(colName)) {
                        existsCol = true;
                        break;
                    }
                }
                if (existsCol) {
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
                        isAutoIncrement, decimalDigits, null, remarks);
                colData.add(cd);
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

        return tableInfo;

    }

}
