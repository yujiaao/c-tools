package com.bixuebihui.r2dbc.sequence;

import com.bixuebihui.r2dbc.sql.IDbHelper;
import io.r2dbc.spi.Connection;
import org.apache.commons.collections.Factory;
import org.junit.platform.engine.support.hierarchical.OpenTest4JAwareThrowableCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;


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
	static final String INSTALL =
			"CREATE TABLE  IF NOT EXISTS  t_sequence ( KEYNAME varchar(24) NOT NULL PRIMARY KEY ,"+
							" KEYVALUE INT default 10000"+
								"  );";

	private IDbHelper dbHelper;

	protected static Logger LOG = LoggerFactory.getLogger(KeyInfo.class);

    private void init(String keyName, int poolSize, IDbHelper dbHelper) {
        this.dbHelper = dbHelper;

        this.poolSize = poolSize;
        this.keyName = keyName;

        retrieveFromDb();
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
	public Mono<Long> getNextKey()  {
		if (nextKey > maxKey) {
			 return  retrieveFromDb();
		}
		return Mono.just(nextKey++);
	}

    Mono<Object> installAndGet(Connection cn){
        LOG.info("install sequence table:" + INSTALL);

        LOG.info("执行Sequence数据库初始化工作！");
        String initSql = "INSERT INTO " + SEQUENCE_TABLE
                + "(KEYNAME,KEYVALUE) VALUES('" + keyName
                + "',10000 + " + poolSize + ")";
        return dbHelper.executeNoQuery(new String[]{INSTALL, initSql}, Mono.just(cn)).flatMap(x -> {
            LOG.info("batch res:"+x);

            maxKey = 10000L + poolSize;
            minKey = maxKey - poolSize + 1;
            nextKey = minKey;
            // 更新数据库

           return Mono.empty();
        }).then(getValue(cn));
    }

    Mono<Object> getValue(Connection cn){
        return dbHelper.executeScalar(SQL_QUERY,
                new Object[]{keyName}, Mono.just(cn));
    }


	/**
	 * 执行Sequence表信息初始化和更新工作
	 *
	 */
    private Mono<Long> retrieveFromDb()  {

        AtomicBoolean got = new AtomicBoolean(false);
        AtomicLong value= new AtomicLong();

            return Mono.from(dbHelper.getConnection()).flatMap(cn -> {
                // 查询数据库
                return getValue(cn)
                        .switchIfEmpty(installAndGet(cn))
                        .single().flatMap(x -> {
                             value.set(Long.parseLong(x.toString()));
                             LOG.debug(keyName + "=" + value.get() + "~" + (value.get() + poolSize));

                             maxKey = value.get() + poolSize;
                             minKey = maxKey - poolSize + 1;
                             nextKey = minKey;
                             LOG.debug("更新Sequence最大值！");
                             got.set(true);
                             return dbHelper.executeNoQuery(SQL_UPDATE, new Object[]{(long) poolSize, keyName, value.get()}, cn);
                         })//.repeatWhenEmpty(Repeat.onlyIf(r -> got.get())
                     //   .fixedBackoff(Duration.ofSeconds(5))
                     //   .timeout(Duration.ofSeconds(30)))
                        .flatMap(x -> Mono.just(value.get()));
            });


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
