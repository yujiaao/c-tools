package com.bixuebihui.r2dbc.sequence;

import com.bixuebihui.r2dbc.sql.IDbHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.<br>
 * <b>User</b>: leizhimin<br>
 * <b>Date</b>: 2008-4-2 15:21:30<br>
 * <b>Note</b>: Java实现的Sequence工具
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class SequenceUtils {
	private static SequenceUtils INSTANCE = new SequenceUtils();
	private Map<String, KeyInfo> keyMap = new ConcurrentHashMap<>(20); // Sequence载体容器
	private static final int POOL_SIZE = 10; // Sequence值缓存大小
	private static final Logger LOG = LoggerFactory.getLogger(SequenceUtils.class);

	/**
	 * 禁止外部实例化
	 */
	private SequenceUtils() {
	}

	/**
	 * 获取SequenceUtils的单例对象
     *
     * @return SequenceUtils的单例对象
	 */
	public static SequenceUtils getInstance() {
		return INSTANCE;
	}


	/**
	 * 获取下一个Sequence键值
     *
     * @param keyName
	 *            Sequence名称
	 * @param dbHelper 数据库连接
	 * @return 下一个Sequence键值
	 */
	public synchronized Mono<Long> getNextKeyValue(String keyName, IDbHelper dbHelper) {
		try {
			return getOrCreateKeyInfo(keyName, dbHelper).getNextKey();
		} catch (SQLException e) {
			LOG.error("",e);
		}
		return Mono.just(0L);
	}

    /**
     * <p>moveKeyValueToCurrent.</p>
     *
     * @param keyName  a {@link String} object.
     * @param max      a {@link Long} object.
     * @param dbHelper a {@link IDbHelper} object.
     */
    public synchronized void moveKeyValueToCurrent(String keyName, Long max, IDbHelper dbHelper) {
        try {
            getOrCreateKeyInfo(keyName, dbHelper).moveTo(max);
        } catch (SQLException e) {
            LOG.error("",e);
        }

    }

	private KeyInfo getOrCreateKeyInfo(String keyName, IDbHelper dbHelper) throws SQLException {
		KeyInfo keyInfo;
		if (keyMap.containsKey(keyName)) {
			keyInfo = keyMap.get(keyName);
		} else {
			keyInfo = new KeyInfo(keyName, POOL_SIZE, dbHelper);
			keyMap.put(keyName, keyInfo);
		}
		return keyInfo;
	}
}
