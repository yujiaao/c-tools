package com.bixuebihui.cache;

import com.bixuebihui.BeanFactory;
import com.bixuebihui.DbException;
import com.bixuebihui.util.html.FormControl;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * 字典表一般认为是记录较少，一般不超过5000条，多数情况下仅为几十条，字段很少， 一般仅三、四个，同时改动很少，查询却很多的数据表
 * 标准字典表的定义为：表名－DMS_XXXXXX, 字段名
 * MS_ID，MS_VALUE，MS_SORT，缓存在内存中的只有MS_ID，和MS_VALUE
 *
 * @author xwx
 */
public class DictionaryCache {
    public static final String KEY_SEPARATOR = ".";
    public static final String CONDITION_SEPARATOR = "@";
    static Logger LOG;
    static GeneralCacheAdministrator admin;

    static {
        LOG = LoggerFactory.getLogger(DictionaryCache.class);
        if (admin == null) {
            LOG.info("create new cache administrator");
            admin = new GeneralCacheAdministrator();
        }
    }

    @SuppressWarnings({"unchecked"})
    protected static Map<String, Object> sortByValue(Map<String, Object> map) {
        List<Map.Entry<String, Object>> list = new LinkedList<>(map.entrySet());
        list.sort((Comparator) (o1, o2) -> ((Comparable) ((Map.Entry<String, Object>) (o1)).getValue())
                .compareTo(((Map.Entry<String, Object>) (o2)).getValue()));
        Map<String, Object> result = new LinkedHashMap<>();
        list.forEach( it -> result.put(it.getKey(),it.getValue()));
        return result;
    }

    public static String makeKey(String tableName, String condition,
                                 String idValue) {
        if (idValue == null && condition == null) {
            return tableName;
        } else if (idValue == null) {
            return tableName + CONDITION_SEPARATOR + condition;
        } else if (condition == null) {
            return tableName + KEY_SEPARATOR + idValue;
        } else {
            return tableName + CONDITION_SEPARATOR + condition + KEY_SEPARATOR
                    + idValue;
        }
    }

    private static Dictionary parseKey(String key) {
        Dictionary dict = new Dictionary();

        String[] keys = key.split("\\" + KEY_SEPARATOR);
        if (keys.length > 0) {
            String[] names = keys[0].split("\\" + CONDITION_SEPARATOR);
            dict.tableName = names[0];
            if (names.length > 1) {
                dict.condition = names[1];
            } else {
                dict.condition = null;
            }
        } else {
            LOG.error("No key find! " + key);
        }
        if (keys.length > 1) {
            dict.keyName = keys[1];
        }

        return dict;
    }

    /**
     * 字典表数据在缓存内分为二级存储 一级为整个表，二级为单条记录，皆为Map数据结构（LinkedHashMap） key的定义为
     * “，如果仅是表名，则返回整个表，否则只返回表的一条记录
     *
     * @param key 表名.键值
     * @return “表名.键值”，如果仅是表名，则返回整个表，否则只返回表的一条记录
     */
    @SuppressWarnings("unchecked")
    public static DictionaryItem byId(String key) {
        return (DictionaryItem) getFromCache(key, true);
    }

    public static DictionaryItem byValue(String value) {
        return (DictionaryItem) getFromCache(value, false);
    }

    public static Map<String, DictionaryItem> getDict(String tableBeanName) {
        return (Map<String, DictionaryItem>) getFromCache(tableBeanName, true);
    }

    /**
     * 得到 "&lt;option&gt;&lt;/option&gt;"
     *
     * @param tableBeanName      table name
     * @param strDefaultSelect   default selected item,if no condition item is selected.
     * @param strConditionSelect condition selected item
     * @return　HTML option tag 字符串
     */
    public static String getOptionList(String tableBeanName, String strDefaultSelect,
                                String strConditionSelect) {
        Map<String, DictionaryItem> map = getDict(tableBeanName);
        String[] ids = new String[map.size()];
        String[] values = new String[map.size()];
        Iterator<DictionaryItem> iter = map.values().iterator();
        for (int i = 0; i < map.size(); i++) {
            DictionaryItem item = iter.next();
            ids[i] = item.getId() + "";
            values[i] = item.getValue();
        }

        return FormControl.getOptionList(values, ids, strConditionSelect,
                strConditionSelect);
    }

    private static Object getFromCache(String key, boolean isById) {
        return getFromCache(key, isById, true);
    }

    @SuppressWarnings("unchecked")
    private static Object getFromCache(String key, boolean isById, boolean doRefreshIfNotFind) {
        Object[] myValue;

        Dictionary dict = parseKey(key);
        String storeKey;
        if (dict.condition != null) {
            storeKey = dict.tableName + CONDITION_SEPARATOR + dict.condition;
        } else {
            storeKey = dict.tableName;
        }

        try {
            // Get from the cache]
            myValue = (Map[]) admin.getFromCache(storeKey, (int) (CacheConfig
                    .getUpdateIntervalMilliseconds() / 1000));
            if (dict.keyName == null) {
                // LinkedHashMap
                return myValue[isById ? 0 : 1];
            } else {
                // DictionaryItem
                Object o = ((Map<String, DictionaryItem>) myValue[isById ? 0 : 1]).get(dict.keyName);

                if (o == null) {
                    throw new NeedsRefreshException("not find " + dict.keyName);
                } else {
                    return o;
                }
            }

        } catch (NeedsRefreshException nre) {
            try {
                if (!doRefreshIfNotFind) {
                    return null;
                }
                // Get the value (probably from the database)
                // myValue = "This is the content retrieved.";
                // Store in the cache

                DictionaryDefine def = (DictionaryDefine) BeanFactory
                        .createObjectById(dict.tableName);
                if (def == null) {
                    def = new DictionaryDefine(dict.tableName);
                }

                if (dict.condition != null) {
                    def.setConditionValue(dict.condition);
                }

                DictionaryList list = new DictionaryList(def);
                try {
                    List<DictionaryItem> li = list.selectAll();
                    Map<String, DictionaryItem>[] mm = putInCache(storeKey, li);

                    if (dict.keyName == null) {
                        return mm[isById ? 0 : 1];
                    } else {
                        // DictionaryItem
                        return mm[isById ? 0 : 1].get(dict.keyName);
                    }
                } catch (DbException e) {
                    LOG.error("查询" + dict.tableName + "时数据库出错：");
                    e.printStackTrace();
                    admin.cancelUpdate(key);
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
                admin.cancelUpdate(key);
                // We have the current content if we want fail-over.
                Object value = nre.getCacheContent();
                // It is essential that cancelUpdate is called if the
                // cached content is not rebuilt
                return value;
            }
        }
    }

    /**
     * method for fill cache
     * @param storeKey
     * @param li
     * @return
     */
    public static Map<String, DictionaryItem>[] putInCache(String storeKey, List<DictionaryItem> li) {
        LinkedHashMap<String,DictionaryItem>[] mm = new LinkedHashMap[] {
            new LinkedHashMap<>(li.size()),
            new LinkedHashMap<>(li.size())
        };

        for (DictionaryItem dictionaryItem : li) {
            mm[0].put(dictionaryItem.getId() + "", dictionaryItem);
            mm[1].put(dictionaryItem.getValue() + "", dictionaryItem);
        }

        admin.putInCache(storeKey, mm);
        return mm;
    }

    public DictionaryItem byValue(String key, boolean doRefreshIfNotFindInCache) {
        return (DictionaryItem) getFromCache(key, false, doRefreshIfNotFindInCache);
    }

    public void destroy() {
        if (admin != null) {
            admin.destroy();
        }
    }

    static class Dictionary {
        String tableName; // 表名
        String keyName; // 字段值
        String condition; // 附加条件
    }

}
