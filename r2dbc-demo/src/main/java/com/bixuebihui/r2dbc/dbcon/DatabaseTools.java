package com.bixuebihui.r2dbc.dbcon;

import java.sql.SQLException;

/**
 * <p>DatabaseTools class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class DatabaseTools extends BaseOperator
{
    /**
     * <p>getMaxvalue.</p>
     *
     * @param tblname   a {@link String} object.
     * @param colname   a {@link String} object.
     * @param condition a {@link String} object.
     * @return a {@link String} object.
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
     * @param tblname a {@link String} object.
     * @param colname a {@link String} object.
     * @param condition a {@link String} object.
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
}
