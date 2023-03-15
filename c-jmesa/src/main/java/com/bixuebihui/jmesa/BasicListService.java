package com.bixuebihui.jmesa;

import com.bixuebihui.DbException;
import com.bixuebihui.jdbc.AbstractBaseDao;
import com.bixuebihui.jdbc.BaseDao;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.jdbc.SqlObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bixuebihui.db.Dialect.DERBY;

/**
 * @author xwx
 */
public class BasicListService extends AbstractBaseDao {

    private SqlObject sqlObj = new SqlObject();


    public BasicListService(IDbHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    public String getTableName() {
        return "(" + sqlObj.getSqlString() + ") basiclistservice_table_alias";
    }

    @Override
    public int count(String where) throws NumberFormatException {
        if (this.getSqlParams() == null || this.getSqlParams().length == 0) {
            return super.count(where);
        }
        String sql = "select count(*) from " + this.getTableName() + "   " + where;
        return Integer.parseInt(this.getDbHelper().executeScalar(sql, sqlObj.getParameters()).toString());
    }

    @Override
    public int countWhere(String where, Object[] params) {
        return super.countWhere(where, merge(sqlObj.getParameters(), params));
    }

    private Object[] merge(Object[] parameters, Object[] params) {
        List<Object> res = new ArrayList<>();
        if(parameters!=null&& parameters.length>0) {
            res.addAll(List.of(parameters));
        }
        if(params != null && params.length >0) {
            res.addAll(List.of(params));
        }
        return res.toArray();
    }


    @Override
    public List<Object> select(String whereClause, Object[] params, String orderBy, int rowStart, int rowEnd) {

        Object[] allParam = merge(sqlObj.getParameters(), params);

        boolean needOrderBy = true;
        String tableName = getTableName();
        if (this.getDbType() == DERBY){
            // || tableName.toUpperCase().indexOf("ORDER BY") > 0) {
            needOrderBy = false;
        }

        String selectSql = "select * from " + tableName + " " + whereClause + " "
                + (needOrderBy ? orderBy : "");

        List<Map<String, Object>> v = getDbHelper().executeQuery(this.getPagingSql(selectSql, rowStart, rowEnd), allParam);
        ArrayList<Object> list = new ArrayList<>();
        list.addAll(v);
        return list;

    }


    @Override
    public Map<String, Object> selectByIds(String arg0, List<String> arg1) {
        throw new DbException("not implement!");
    }

    @Override
    public boolean updateByKey(Object arg0) {
        throw new DbException("not implement!");
    }

    public void setCoreSql(String coreSql) {
        sqlObj.setSqlString(coreSql);
    }

    public Object[] getSqlParams() {
        return sqlObj.getParameters();
    }

    public void setSqlParams(Object[] sqlParameters) {
        sqlObj.setParameters(sqlParameters);
    }


}
