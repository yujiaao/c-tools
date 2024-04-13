package com.bixuebihui.util.db;

import com.bixuebihui.BeanFactory;
import com.bixuebihui.jdbc.IDbHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Hashtable;
import java.util.List;

/**
 * 用于同步相同结构的两个数据库中数据
 *
 * @author xwx
 */
public class SyncData {
    public IDbHelper dhsrc;

    public IDbHelper dhdes;

    public SyncData(String srcDbHelperName, String desDbHelperName) {
        dhsrc = (IDbHelper) BeanFactory.createObjectById(srcDbHelperName);
        dhdes = (IDbHelper) BeanFactory.createObjectById(desDbHelperName);
    }

    public SyncData(IDbHelper srcDbHelperName, IDbHelper desDbHelperName) {
        dhsrc = srcDbHelperName;
        dhdes = desDbHelperName;
    }

    /**
     * 用表名和字段名形成select语句
     *
     * @param tableName   表名
     * @param fields      字段名
     * @param whereClause 条件语句
     * @return 生成select用的sql语句
     */
    public String formSelectSql(String tableName, String[] fields,
                                String whereClause) {
        StringBuffer sql = new StringBuffer(" select ");
        for (int i = 0; i < fields.length - 1; i++) {
            sql.append(fields[i]).append(",");
        }
        sql.append(fields[fields.length - 1]).append(" from ")
                .append(tableName).append(" ");

        sql.append(whereClause);

        return sql.toString();
    }

    /**
     * 用表名和字段名形成update语句
     *
     * @param tableName   表名
     * @param fields      字段名
     * @param whereClause 条件语句
     * @return 生成update用的sql语句
     */
    public String formUpdateSql(String tableName, String[] fields,
                                String whereClause) {
        StringBuffer sql = new StringBuffer(" update ");
        sql.append(tableName).append(" set ");
        for (int i = 0; i < fields.length - 1; i++) {
            sql.append(fields[i]).append("=?,");
        }
        sql.append(fields[fields.length - 1]).append("=? ");

        sql.append(whereClause);

        return sql.toString();
    }

    public void setParam(PreparedStatement p, Object[] objs)
            throws SQLException {
        for (int i = 1; i <= objs.length; i++) {
            Object o = objs[i - 1];
            if (o == null) {
                p.setNull(i, Types.INTEGER);
            } else if (o.getClass() == Integer.class) {
                p.setInt(i, (Integer) o);
            } else if (o.getClass() == Double.class) {
                p.setDouble(i, (Double) o);
            } else if (o.getClass() == String.class) {
                p.setString(i, o.toString());
            } else if (o.getClass() == java.sql.Timestamp.class) {
                p.setString(i, o.toString());
                p.setDate(i, new java.sql.Date(((java.sql.Timestamp) o)
                        .getTime()));
            } else {
                throw new SQLException("Unknown type, to be added:" + (i) + "="
                        + o.getClass());
            }
        }
    }

    /**
     * 同步两个库中同一个表的数据
     *
     * @param tableName   表名
     * @param fields      字段名
     * @param whereClause 条件语句
     * @param primaryKey  主键
     * @return 同步的记录数
     * @throws SQLException 数据库异常
     */
    public int synUpdateTable(String tableName, String[] fields,
                              String whereClause, String primaryKey) throws SQLException {

        String sql = formSelectSql(tableName, fields, whereClause);
        List<?> rssrc = dhsrc.exeQuery(sql);
        Connection cn = dhdes.getConnection();

        sql = formUpdateSql(tableName, fields, " where " + primaryKey + "=?");
        PreparedStatement p = cn.prepareStatement(sql);

        int res = 0;
        System.out.println("共获得" + rssrc.size() + "记录！");

        try {
            for (int j = 0; j < rssrc.size(); j++) {
                Hashtable<?, ?> ht = (Hashtable<?, ?>) rssrc.get(j);

                Object[] objs = new Object[fields.length + 1];

                for (int i = 0; i < fields.length; i++) {
                    objs[i] = ht.get(fields[i]);
                }
                objs[fields.length] = ht.get(primaryKey);

                setParam(p, objs);

                res += p.executeUpdate();
                System.out.println("已同步" + res + "记录！");
            }
        } finally {
            p.close();
            cn.close();
        }
        return res;
    }

}
