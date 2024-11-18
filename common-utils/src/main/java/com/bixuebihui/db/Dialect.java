package com.bixuebihui.db;

/**
 * @author xwx
 */
public class Dialect {

    /** Constant <code>UNKNOWN=0</code> */
    public static final int UNKNOWN = 0;
    /** Constant <code>ORACLE=1</code> */
    public static final int ORACLE = 1;
    /** Constant <code>DERBY=2</code> */
    public static final int DERBY = 2;
    /** Constant <code>MYSQL=3</code> */
    public static final int MYSQL = 3;
    /** Constant <code>SQLSERVER=4</code> */
    public static final int SQLSERVER = 4;
    /** Constant <code>SQLSERVER_2005_AND_UP=7</code> */
    public static final int SQLSERVER_2005_AND_UP = 7;
    public static final int SQLSERVER_2010_AND_UP = 9;
    /** Constant <code>POSTGRESQL=5</code> // POSTGRESQL NATIVE DRIVER*/
    public static final int POSTGRESQL = 5;
    /** Constant <code>ACCESS=6</code> */
    public static final int ACCESS = 6;
    /** Constant <code>H2=8</code> */
    public static final int H2 = 8;


    /**
     * <p>detectDbType.</p>
     *
     * @param driverName a {@link java.lang.String} object.
     * @return a int.
     */
    public static int detectDbType(String driverName) {
        int res = UNKNOWN;
        String name = driverName.toUpperCase();
        if (name.contains("ORACLE")) {
            res = ORACLE;
        } else if (name.contains("DERBY")) {
            res = DERBY;
        } else if (name.contains("MYSQL")) {
            res = MYSQL;
        } else if (name.contains("SQLSERVER")) {
            // sqlserver 2000
            res = SQLSERVER;
            //} else if (name.contains("SQL SERVER 201") || name.contains("SQL SERVER 202")) {

        } else if (name.contains("SQL SERVER")) {
            res = SQLSERVER_2010_AND_UP; //2010 ~ 2029
            // sql server 2005
            //  res = SQLSERVER_2005_AND_UP;
        } else if (name.contains("POSTGRESQL")) {
            res = POSTGRESQL;
        } else if (name.contains("H2")) {
            res = H2;
        } else if (name.contains("ACCESS")) {
            res = ACCESS;
        }
        return res;
    }


}
