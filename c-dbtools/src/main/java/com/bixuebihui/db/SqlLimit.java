package com.bixuebihui.db;

/**
 * <p>SqlLimit class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class SqlLimit {
    /**
     * Constant <code>LIMIT_ONE</code>
     */
    public static final SqlLimit LIMIT_ONE= new SqlLimit(0, 1);
    /** Constant <code>LIMIT_MAX</code> */
    public static final SqlLimit LIMIT_MAX= new SqlLimit(0, 10000);

    /**
     * <p>Constructor for SqlLimit.</p>
     *
     * @param begin offset from begin, default 0.
     * @param num  count of record to return.
     */
    public SqlLimit(int begin, int num) {
        this.begin = begin;
        this.num = num;
    }


    /**
     * <p>Constructor for SqlLimit.</p>
     *
     * @param src a {@link SqlLimit} object.
     */
    public SqlLimit(SqlLimit src) {
        this.begin = src.begin;
        this.num = src.num;
    }


    private int begin;
    private int num;

    /**
     * <p>Getter for the field <code>begin</code>.</p>
     *
     * @return an int.
     */
    public int getBegin() {
        return begin;
    }

    /**
     * <p>Setter for the field <code>begin</code>.</p>
     *
     * @param begin an int.
     */
    public void setBegin(int begin) {
        this.begin = begin;
    }

    /**
     * <p>Getter for the field <code>num</code>.</p>
     *
     * @return an int.
     */
    public int getNum() {
        return num;
    }

    /**
     * <p>getEnd.</p>
     *
     * @return an int.
     */
    public int getEnd(){
        return begin + num;
    }

    /**
     * <p>Setter for the field <code>num</code>.</p>
     *
     * @param num an int.
     */
    public void setNum(int num) {
        this.num = num;
    }

    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String toString() {
        return " limit " + begin + "," + num;
    }

}
