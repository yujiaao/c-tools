package com.bixuebihui.jdbc;
/*
 * @(#)SqlServer2000PageHepler.java 0.1 2009-12-30
 * Copyright 2009 www.goldjetty.com Inc. All rights reserved.
 */

import com.bixuebihui.DbException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL SERVER 2000 分页辅助类.
 *
 * @author xingwx
 * @version 0.1
 */
public class SqlServer2000PageHepler {

    SqlServer2000PageHepler() throws IllegalAccessException {
        throw new IllegalAccessException("util class");
    }


    private static final String DESC = " desc";

    /**
     * 得到查询总数的SQL
     *
     * @param querySelect 查询SQL
     * @return 得到总数的SQL
     */
    public static String getCountString(String querySelect)  {

        querySelect     = getLineText(querySelect);
        int orderIndex  = getLastOrderInsertPoint(querySelect);

        int formIndex   = getAfterFormInsertPoint(querySelect);
        String select   = querySelect.substring(0, formIndex);

        //如果SELECT 中包含 group by 或者 distinct 就在外层包含COUNT
        if (select.startsWith("select distinct") || isHaveGroupBy(querySelect)) {
            return "select count(1) '_count' from (" +
                    querySelect.substring(0, orderIndex) + " ) _t";

        }else {
            return "select count (1) '_count' " +
                    querySelect.substring(formIndex, orderIndex);
        }
    }

    /**
     * 得到分页的SQL <pre>
     * SELECT TOP 5 *
     * FROM (SELECT TOP 10 *
     *   FROM table1
     *   ORDER BY id DESC) _table1
     * ORDER BY id </pre>
     *
     * @param querySelect select语句
     * @param offset    偏移量
     * @param limit     限度
     * @return 分页SQL
     */
    public static String getLimitString(String querySelect, int offset, int limit) {
        String innerQuerySelect = getLineText(querySelect);

        if (offset > 0) {
            int orderbyPos = getLastOrderInsertPoint(innerQuerySelect);
            if (orderbyPos < 0) {
                throw new UnsupportedOperationException("query result offset is not supported without 'order by' subclause");
            } else {
                String orderBy = innerQuerySelect.substring(orderbyPos);
                String reverseOrderBy = "order by " + reversOrderbyClause(querySelect.substring(orderbyPos + "order by".length()));
                String coreSql = innerQuerySelect.substring(0, orderbyPos);
                StringBuilder sb = new StringBuilder(innerQuerySelect.length() * 2)
                        .append(coreSql)
                        .insert(getAfterSelectInsertPoint(coreSql), " top " + offset)
                        .append(reverseOrderBy);

                sb.insert(0, "select top " + limit + " * from (").append(") _table1 ").append(orderBy);

                return sb.toString();

            }
        }
        return new StringBuilder(innerQuerySelect.length() + 8)
                .append(innerQuerySelect)
                .insert(getAfterSelectInsertPoint(innerQuerySelect), " top " + limit)
                .toString();

    }


    /**
     * 认为order by 子句为select语句的最后子句
     * @param substring 颠倒排序字段
     * @return 正序倒序翻转后的sql
     */
	static String reversOrderbyClause(String substring) {
		String [] arr = substring.split("\\,");
		StringBuilder sb = new StringBuilder(substring.length()*2);
		for (int i = 0; i < arr.length; i++) {
			sb.append(toggleOrder(arr[i]));
			if(i != arr.length-1) {
                sb.append(",");
            }
		}
		return sb.toString();
	}

	private static String toggleOrder(String ascdesc){
		ascdesc = ascdesc.trim();
		if(ascdesc.endsWith(DESC)) {
            return ascdesc.substring(0, ascdesc.lastIndexOf(DESC));
        } else if(ascdesc.endsWith(" asc")){
			ascdesc = ascdesc.substring(0, ascdesc.lastIndexOf(" asc"));
		}
		return ascdesc+DESC;
	}

	private static int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf( "select" );
		final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
		return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
	}


    /**
     * 判断是否包含正确group by
     * @return 如果包含返回True否则返回false
     */
    private static boolean isHaveGroupBy(String querySelect){
        int groupIndex = querySelect.toLowerCase().lastIndexOf("group by");
        return (groupIndex != -1 && isBracketCanPartnership(querySelect.substring(groupIndex)));
    }


    /**
     * 得到最后一个order By的插入点位置
     * @return 返回最后一个order By插入点的位置
     * @throws DbException 如果不存在正确的order by
     */
    private static int getLastOrderInsertPoint(String querySelect) {
        int orderIndex = querySelect.toLowerCase().lastIndexOf("order by");
        if (orderIndex == -1
                || !isBracketCanPartnership(querySelect.substring(orderIndex))) {
            throw new DbException("SQL 2000 分页必须要有Order by 语句!");
        }
        return orderIndex;
    }


    /**
     * 将SQL语句变成一条不换行语句，并且每个单词的间隔都是1个空格
     * @param text 需要转化的文本
     * @return 如果sql是NULL返回空，否则返回转化后的SQL
     */
    static String getLineText(String text) {
        return text.replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ");
    }

    /**
     * 得到SQL第一个正确的FROM的的插入点
     * @param querySelect 完整的查询语句
     * @return  正确的FROM插入点
     */
    private static int getAfterFormInsertPoint(String querySelect) {
        String regex = "\\s+FROM\\s+";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(querySelect);
        while (matcher.find()) {
            int fromStartIndex = matcher.start(0);
            String text = querySelect.substring(0, fromStartIndex);
            if (isBracketCanPartnership(text)) {
                return fromStartIndex;
            }
        }
        return 0;
    }

    /**
     * 判断括号'()'是否匹配
     * @param text  要判断的文本
     * @return 如果匹配返回TRUE,否则返回FALSE
     */
    static boolean isBracketCanPartnership(String text) {
        return text != null
                && getIndexOfCount(text, '(') == getIndexOfCount(text, ')');
    }

    /**
     * 得到一个字符在另一个字符串中出现的次数
     * @param text  文本
     * @param ch    字符
     * @return 字符出现在文本的次数.
     */
    private static int getIndexOfCount(String text, char ch) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            count = (text.charAt(i) == ch) ? count + 1 : count;
        }
        return count;
    }
}
