package com.bixuebihui.db;

/**
 * group by function
 * @author xwx
 */
public enum GroupFunction {
    /**
     * avg(column)	返回某列的平均值
     */
    AVG,
    //BINARY_CHECKSUM
    //CHECKSUM
    //CHECKSUM_AGG
    /**
     * count(column)	返回某列的行数（不包括NULL值）
     */
    COUNT,
    //COUNT(*)	返回被选行数
    //COUNT(DISTINCT column)	返回相异结果的数目
    /**
     * first(column)	返回在指定的域中第一个记录的值（SQLServer2000 不支持）
     */
    FIRST,
    /**
     * last(column)	返回在指定的域中最后一个记录的值（SQLServer2000 不支持）
     */
    LAST,

    /**
     * max(column)	返回某列的最高值
     */
    MAX,
    /**
     * min(column)	返回某列的最低值
     */
    MIN,
    /**
     * STDDEV_SAMP for MySql, MS SQL Server STDEV(column)
     */
    STDDEV_SAMP,
    /**
     * std(column)	STD for MySql , MS SQL Server STDEVP
     */
    STD,
    /**
     * sum(column)	返回某列的总和
     */
    SUM,
    /**
     * var(column) MySQL 不支持, 使用 variance
     */
    VAR,
    /**
     * VARIANCE(expr);  standard variance 标准方差
     */
    VARIANCE,
    /**
     * varp(column) MySQL 不支持,see var_pop or var_samp
     */
    VARP
}
