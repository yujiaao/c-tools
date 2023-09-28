package com.bixuebihui.dbcon;

import com.bixuebihui.jdbc.IDbHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>DatabaseTools class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class DatabaseTools extends BaseOperator
{
    private  static  final Logger log = LoggerFactory.getLogger(DatabaseTools.class);
    /**
     * <p>getMaxvalue.</p>
     *
     * @param tblname   a {@link java.lang.String} object.
     * @param colname   a {@link java.lang.String} object.
     * @param condition a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getMaxvalue(String tblname, String colname, String condition) {
        String sqlstr = "select MAX("+colname+") from "+tblname;
        if(condition != null && condition.length()>3) {
            sqlstr = sqlstr + " where "+condition;
        }
        try {
            getResultSet(sqlstr);
            if(rst.next()) {
                return rst.getString(1);
            }
        } catch(SQLException sqle) {
            System.out.println("SQL Error@DT.getMaxvalue:"+sqle.getMessage());
        } finally {
            close();
        }

        return null;
    }

    /**
     * <p>getMaxnumber.</p>
     *
     * @param tblname a {@link java.lang.String} object.
     * @param colname a {@link java.lang.String} object.
     * @param condition a {@link java.lang.String} object.
     * @return a int.
     */
    public int getMaxnumber(String tblname, String colname, String condition) {
        String value = getMaxvalue(tblname, colname, condition);
        try {
            if(value!=null) {
                return Integer.parseInt(value);
            }
        } catch(NumberFormatException nfe) {
            //
        }
        return -0;
    }

    /**
     * 通过数据库Sequence获得唯一值
     *
     * @param seq_name		传入的SEQUENCE值
     * @return Sequence获得唯一值
     */
    public int getNextSequence(String seq_name) {
        //CURRVAL
        String seqsql = "select "+seq_name+".NEXTVAL from DUAL";
        try {
            getResultSet(seqsql);
            if(rst.next()) {
                return rst.getInt(1);
            }
        } catch(SQLException sqle) {
            System.out.println("SQL Error@DT.getNextSequence:"+sqle.getMessage());
        } finally {
            close();
        }

        return -1;
    }

    public static long importTsvData(String tsvFileName, String tableName, IDbHelper dbHelper, int batchSize) {
        String encoding = "UTF-8";
        int maxLines = batchSize>0 ? batchSize : 1000;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tsvFileName), encoding))) {

            String firstLine = reader.readLine();
            String[] columns = firstLine.split("\t");
            String insertSql = "insert into " + tableName+ "(" + String.join(",", columns)
                    + ") values (" + String.join(",", "?".repeat(columns.length).split("")) + ")";
            List<Object[]> values = new ArrayList<>();

            int count = 0;
            for (String line; (line = reader.readLine()) != null; ) {
                Object[] fields = line.split("\t");
                values.add(fields);
                count++;
                if (count % maxLines == 0) {
                    dbHelper.executeNoQueryBatch(insertSql, values);
                    values.clear();
                    log.info("imported " + count + " lines to "+tableName);
                }
            }
            if (!values.isEmpty()) {
                dbHelper.executeNoQueryBatch(insertSql, values);
            }
            return count;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
