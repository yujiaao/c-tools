package com.bixuebihui.r2dbc.sequence;

import com.bixuebihui.r2dbc.sql.IDbHelper;
import io.r2dbc.spi.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;


/**
 * Created by IntelliJ IDEA.<br>
 * <b>User</b>: leizhimin<br>
 * <b>Date</b>: 2008-4-2 15:24:52<br>
 * <b>Note</b>: Sequence载体 DROP TABLE IF EXISTS t_sequence; CREATE TABLE
 * t_sequence( KEYNAME varchar(24) NOT NULL ,--COMMENT 'Sequence名称', KEYVALUE
 * numeric(20) DEFAULT 10000,-- COMMENT 'Sequence最大值', PRIMARY KEY (KEYNAME) ) ;
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class KeyInfo {
    /**
     * Constant <code>SEQUENCE_TABLE="t_sequence"</code>
     */
    public static final String SEQUENCE_TABLE = "t_sequence";
    /** Constant <code>SEQUENCE_DBHELPER_NAME="sequenceDbHelper"</code> */
    public static final String SEQUENCE_DBHELPER_NAME = "sequenceDbHelper";
    /** 当前Sequence载体的最大值 */
	private long maxKey;
    /** 当前Sequence载体的最小值 */
	private long minKey;
    /** 下一个Sequence值 */
	private long nextKey;
    /** Sequence值缓存大小 */
	private int poolSize;
    /** Sequence的名称 */
	private String keyName;
	private static final String SQL_UPDATE = "UPDATE " + SEQUENCE_TABLE
			+ " SET KEYVALUE = KEYVALUE + ? WHERE KEYNAME = ? and KEYVALUE=?";
	private static final String MOVE_TO_MAX = "UPDATE " + SEQUENCE_TABLE
			+ " SET KEYVALUE =  ? WHERE KEYNAME = ? ";
	private static final String SQL_QUERY = "SELECT KEYVALUE FROM "
			+ SEQUENCE_TABLE + " WHERE KEYNAME = ?";
	static final String[] INSTALL ={
			"CREATE TABLE  IF NOT EXISTS  t_sequence ( KEYNAME varchar(24) NOT NULL PRIMARY KEY ,"+
							" KEYVALUE INT default 10000"+
								"  );"
	};

	private IDbHelper dbHelper;

	protected Logger LOG = LoggerFactory.getLogger(KeyInfo.class);

    private void init(String keyName, int poolSize, IDbHelper dbHelper) {
        this.dbHelper = dbHelper;

        this.poolSize = poolSize;
        this.keyName = keyName;

        retrieveFromDB();
    }

    /**
     * <p>Constructor for KeyInfo.</p>
     *
     * @param keyName a {@link String} object.
     * @param poolSize a int.
     * @param dbHelper a {@link IDbHelper} object.
     */
    public KeyInfo(String keyName, int poolSize, IDbHelper dbHelper)  {
        init(keyName, poolSize, dbHelper);
    }


    /**
     * <p>Getter for the field <code>keyName</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * <p>Getter for the field <code>maxKey</code>.</p>
     *
     * @return a long.
     */
    public long getMaxKey() {
        return maxKey;
    }

    /**
     * <p>Getter for the field <code>minKey</code>.</p>
     *
     * @return a long.
     */
    public long getMinKey() {
        return minKey;
    }

    /**
     * <p>Getter for the field <code>poolSize</code>.</p>
     *
     * @return a int.
     */
    public int getPoolSize() {
        return poolSize;
    }

	/**
	 * 获取下一个Sequence值
     *
	 * @return 下一个Sequence值
     */
	public long getNextKey()  {
		if (nextKey > maxKey) {
			int maxTries=10;
			int i=0;
			int res=0;
			while(i<maxTries && res!=1){
			 res = retrieveFromDB().block();
				if(res!=1){
					LOG.error("数据库主键维护出错,更新时返回结果不对: res="+res+" keyName="+keyName+" nextKey="+nextKey);
				}
				i++;
			}
		}
		return nextKey++;
	}

	/**
	 * 执行Sequence表信息初始化和更新工作
	 *
	 */
    private Mono<Integer> retrieveFromDB()  {

        Connection cn = dbHelper.getConnection().block();
        cn.setAutoCommit(false);
        try {
            // 查询数据库
            // 更新数据库
            Mono<Integer> res = Mono.just(0);
            int i = 0;
            while (i < 100 && res.block() <= 0) {
                Object o = dbHelper.executeScalar(SQL_QUERY,
                        new Object[] { keyName }, Mono.just(cn));

                if (o != null) {
                    Long value = Long.valueOf(o.toString());
                    LOG.debug(keyName + "=" + value+"~"+(value+poolSize));

                    maxKey = value + poolSize;
                    minKey = maxKey - poolSize + 1;
                    nextKey = minKey;

                    LOG.debug("更新Sequence最大值！");
                    res= dbHelper.executeNoQuery(SQL_UPDATE, new Object[] {
                            (long) poolSize, keyName, value }, cn);

                } else {
                    LOG.debug("执行Sequence数据库初始化工作！");
                    String initSql = "INSERT INTO " + SEQUENCE_TABLE
                            + "(KEYNAME,KEYVALUE) VALUES('" + keyName
                            + "',10000 + " + poolSize + ")";
                    res = dbHelper.executeNoQuery(initSql, new Object[0],cn);
                    maxKey = 10000L + poolSize;
                    minKey = maxKey - poolSize + 1;
                    nextKey = minKey;
                }
                i++;
            }
            cn.commitTransaction();

            return res;
        } catch (Exception e) {
            cn.rollbackTransaction();
            dbHelper.executeNoQuery(INSTALL);
            throw e;
        } finally {
            cn.setAutoCommit(true);
            cn.close();
        }

    }

    /**
     * <p>moveTo.</p>
     *
     * @param max a {@link Long} object.
     */
    public void moveTo(Long max)  {
        dbHelper.executeNoQuery(MOVE_TO_MAX, new Object[] {
                max, keyName });
        nextKey = max;
    }
}
