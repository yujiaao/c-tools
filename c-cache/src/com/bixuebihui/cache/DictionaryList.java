package com.bixuebihui.cache;


import com.bixuebihui.jdbc.RowMapperResultReader;
import com.bixuebihui.sequence.SequenceUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.bixuebihui.db.Dialect.DERBY;

/**
 * DictionaryItem
 * <p>
 * WARNING! Automatically generated file!
 * Do not edit the pojo and dal packages,use bixuebihui-smartable!
 * Code Generator by J.A.Carter
 * Modified by Xing Wanxiang 2008
 *
 * @author xwx
 */
public class DictionaryList extends BaseList<DictionaryItem, String> {

    private final DictionaryDefine def;

    public DictionaryList(DictionaryDefine define) {
        this.def = define;
    }

    DictionaryList() {//for test
        def = new DictionaryDefine();
    }

    /**
     * Updates the object from a selected ResultSet.
     */
    @Override
    public DictionaryItem mapRow(ResultSet r, int index) throws SQLException {
        DictionaryItem res = new DictionaryItem();
        res.setId(r.getString(def.getIdFieldName()));
        res.setValue(r.getString(def.getValueFieldName()));
        res.setSort(r.getString(def.getSortFieldName()));
        return res;
    }

    private String getSelectSql() {
        if (def.getIdFieldName().equalsIgnoreCase(def.getSortFieldName()) ||
                def.getValueFieldName().equalsIgnoreCase(def.getSortFieldName())) {
            return "select " + def.getIdFieldName() + ", " + def.getValueFieldName()
                    + " from ";
        } else {
            return "select " + def.getIdFieldName() + ", " + def.getValueFieldName()
                    + ", " + def.getSortFieldName() + " from ";
        }
    }

    /**
     * Select from the database for table "getTableName()"
     */
    @Override
    public List<DictionaryItem> select(String whereClause, String orderbyClause,
                                       int beginNum, int endNum)  {

        if (StringUtils.trimToNull(whereClause) == null) {
            whereClause = " where 1=1 ";
        }

        String query = getSelectSql() + getTableName() + " " + whereClause + " " + def.getSqlCondition();
        if (this.getDbType() != DERBY) {
            query += orderbyClause;
        }
        query = getPagingSql(query, beginNum, endNum);
        return dbHelper.executeQuery(query, null, new RowMapperResultReader<>(
                this));
    }


    /**
     * Select from the database for table "DictionaryItem"
     */
    @Override
    public List<DictionaryItem> selectAll()  {
        String query = getSelectSql() + getTableName() + " where 1=1 " + def.getSqlCondition();

        if (StringUtils.trimToNull(def.getSortFieldName()) != null) {
            query += " order by " + def.getSortFieldName();
        }
        return dbHelper.executeQuery(query, null, new RowMapperResultReader<>(
                this));
    }

    /**
     * Select from the database for table "dictionary"
     *
     * @param id 扩展表 id
     * @return DictionaryItem
     */
    public DictionaryItem selectByKey(long id) {
        String query = getSelectSql() + getTableName() + " where " + def.getIdFieldName() + "=?" + " " + def.getSqlCondition();
        List<DictionaryItem> info = dbHelper.executeQuery(query, new Object[]{id},
                new RowMapperResultReader<>(this));
        if (info != null && info.size() > 0) {
            return info.get(0);
        }
        return null;
    }

    /**
     * Select from the database for table "getTableName()"
     *
     * @param id id
     * @return List of DictionaryItem
     */
    public List<DictionaryItem> selectAllLikeKey(long id) {
        String query = getSelectSql() + getTableName() + " where " + def.getIdFieldName() + "=?" + " " + def.getSqlCondition();
        return dbHelper.executeQuery(query, new Object[]{id},
                new RowMapperResultReader<>(this));
    }

    /**
     * Updates the current object values into the database.
     *
     * @param info DictionaryItem
     * @return true success
     */
    @Override
    public boolean updateByKey(DictionaryItem info)  {
        String query = "update " + getTableName() + " set " + def.getIdFieldName() + "=?" + ","
                + def.getValueFieldName() + "=?" + "," + def.getSortFieldName() + "=?" + " where " + def.getIdFieldName() + "=?" + " " + def.getSqlCondition();
        return 1 == dbHelper.executeNoQuery(query, new Object[]{
                info.getId(), info.getValue(), info.getSort(),
                info.getId()});
    }


    /**
     * Deletes from the database for table "KUOZHANBIAOMING"
     *
     * @param id id
     * @return true if succcess
     */
    public boolean deleteByKey(long id) throws SQLException {
        String query = "delete from " + getTableName() + " where " + def.getIdFieldName() + "=?" + " " + def.getSqlCondition();
        return 1 <= dbHelper.executeNoQuery(query, new Object[]{id});
    }

    @Override
    public String getNextKey() {
        return SequenceUtils.getInstance().getNextKeyValue(this.getTableName() + "_ms_id", dbHelper) + "";
    }

    @Override
    protected void setIdLong(DictionaryItem info, long id) {
        setId(info, id+"");
    }


    /**
     * Counts the number of entries for this table in the database.
     *
     * @param id id
     * @return count
     */
    public int countByKey(long id) {
        String query = "select count(*) from " + getTableName()
                + " where " + def.getIdFieldName() + "=?" + " " + def.getSqlCondition();
        Object o = dbHelper.executeScalar(query, new Object[]{id});
        return o == null ? 0 : Integer.parseInt(o.toString());
    }

    /**
     * Counts the number of entries for this table in the database.
     *
     * @param id id
     * @return count
     */
    public int countLikeKey(long id) {
        String query = "select count(*) from " + getTableName()
                + " where " + def.getIdFieldName() + "=?" + " " + def.getSqlCondition();
        Object o = dbHelper.executeScalar(query, new Object[]{id});
        return o == null ? 0 : Integer.parseInt(o.toString());
    }


    /**
     * Inserts the DictionaryItem object values into the database.
     *
     * @param info DictionaryItem
     * @return true if success
     */
    @Override
    public boolean insert(DictionaryItem info)  {
        String query = "insert into " + getTableName()
                + " ( " + def.getIdFieldName() + "," + def.getValueFieldName() + "," + def.getSortFieldName() + " ) values ( ?,?,? )";
        return 1 == dbHelper.executeNoQuery(query, new Object[]{
                info.getId(), info.getValue(), info.getSort()});
    }

    /**
     * Inserts the dummy record of DictionaryItem object values into the
     * database.
     *
     * @return true if success
     */
    @Override
    public boolean insertDummy() throws SQLException {
        DictionaryItem info = new DictionaryItem();
        java.util.Random rnd = new java.util.Random();
        info.setValue(Integer.toString(Math.abs(rnd.nextInt(Integer.MAX_VALUE)), 36));
        info.setId(getNextKey() + "");
        return this.insert(info);
    }


    @Override
    public String getTableName() {
        return def.getTableName();
    }


    @Override
    public String getId(DictionaryItem info) {
        return info.getId();
    }

    @Override
    public void setId(DictionaryItem info, String id) {
        info.setId(id);
    }

    @Override
    public String getKeyName() {
        return def.getIdFieldName();
    }


}
