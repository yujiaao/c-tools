package com.bixuebihui.jdbc;


import java.sql.SQLException;

/**
 * SQL SERVER 2005 分页辅助类.
 *
 * @author 黄明杰
 * @version 0.2
 * <p>注意：</p>
 *
 * 不支持"UNION" 和“UNION ALL” 这样的结果合并语句，例如：
 * Sql代码
 *
 * 1. select * from dbo.[user]
 * 2. union
 * 3. select top 1 * from dbo.[user] order by age
 *
 * select * from dbo.[user]
 * union
 * select top 1 * from dbo.[user] order by age
 *
 * 主要原因是 UNION  这样的语句没有共同的order by 条件，所以不想支持，如果你需要得到正确的结果你需要这样写，如下：
 * Sql代码
 *
 * 1. select * from (
 * 2.     select * from dbo.[user]
 * 3.     union
 * 4.     select top 1 * from dbo.[user] order by age
 * 5. ) Q order by id
 *
 * select * from (
 * select * from dbo.[user]
 * union
 * select top 1 * from dbo.[user] order by age
 * ) Q order by id
 *
 * SELECT DepartmentID, Name, GroupName
 * FROM HumanResources.Department
 * ORDER BY DepartmentID ASC
 *     OFFSET @StartingRowNumber - 1 ROWS
 *     FETCH NEXT @RowCountPerPage ROWS ONLY;
 *
 */
public class SqlServer2010PageHelper extends SqlServer2000PageHelper {

    private SqlServer2010PageHelper() throws IllegalAccessException {
        super();
    }


    /**
     * 得到分页的SQL
     *
     * @param limit       count of records to return
     * @param offset      offset from start
     * @param querySelect select clause
     * @return select clause with pagination
     * {@link SqlServer2000PageHelper#getLimitString }
     */
    public static String getLimitString(String querySelect, int offset, int limit) {
        querySelect = getLineText(querySelect);

        try {
            getLastOrderInsertPoint(querySelect);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return querySelect + " OFFSET " + offset + " ROWS " +
                "FETCH NEXT " + Math.max(limit,1) + " ROWS ONLY";
    }

    /**
     * 得到最后一个order By的插入点位置
     * @param querySelect  sql语句
     * @return 返回最后一个order By插入点的位置
     * @throws SQLException 如果不存在正确的order by
     */
    private static int getLastOrderInsertPoint(String querySelect) throws SQLException {
        int orderIndex = querySelect.toLowerCase().lastIndexOf("order by");
        if (orderIndex == -1
                || !isBracketCanPartnership(querySelect.substring(orderIndex))) {
            throw new SQLException("SQL 2005 分页必须要有Order by 语句!");
        }
        return orderIndex;
    }


}
