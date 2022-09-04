package com.bixuebihui.cache;

import com.bixuebihui.BeanFactory;
import com.bixuebihui.DbException;
import com.bixuebihui.util.html.FormControl;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 字典表一般认为是记录较少，一般不超过5000条，多数情况下仅为几十条，字段很少， 一般仅三、四个，同时改动很少，查询却很多的数据表
 * 标准字典表的定义为：表名－DMS_XXXXXX, 字段名
 * MS_ID，MS_VALUE，MS_SORT，缓存在内存中的只有MS_ID，和MS_VALUE
 *
 * @author xwx
 */
public class DictionaryCacheCaffeine {
    public static final String KEY_SEPARATOR = ".";
    public static final String CONDITION_SEPARATOR = "@";
    static Logger LOG = LoggerFactory.getLogger(DictionaryCacheCaffeine.class);
    LoadingCache<String, Object> byId;
    LoadingCache<String, Object> byValue;

    Map<String, DictionaryDefine> definitions;


    public static DictionaryCacheCaffeine newBuilder() {
        DictionaryCacheCaffeine caffeine = new DictionaryCacheCaffeine();
        return caffeine;
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

    @SuppressWarnings({"unchecked"})
    static Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, (Comparator) (o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue())
                .compareTo(((Map.Entry) (o2)).getValue()));
        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
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

    private static String getStoreKey(Dictionary dict) {
        String storekey;
        if (dict.condition != null) {
            storekey = dict.tableName + CONDITION_SEPARATOR + dict.condition;
        } else {
            storekey = dict.tableName;
        }
        return storekey;
    }

    public DictionaryCacheCaffeine registerDictionaryDefinitions(DictionaryDefine define) {
        if (definitions == null) {
            definitions = new HashMap<>();
        }
        definitions.put(define.getTableName(), define);
        return this;
    }

    public DictionaryCacheCaffeine build() {

        if (byId == null) {
            LOG.info("create new cache administrator");
            byId = Caffeine.newBuilder()
                    .maximumSize(10_000)
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .refreshAfterWrite(1, TimeUnit.MINUTES)
                    .build(key -> getFromDb(key, true));
            byValue = Caffeine.newBuilder()
                    .maximumSize(10_000)
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .refreshAfterWrite(1, TimeUnit.MINUTES)
                    .build(key -> getFromDb(key, false));

        }
        return this;
    }

    /**
     * 字典表数据在缓存内分为二级存储 一级为整个表，二级为单条记录，皆为Map数据结构（LinkedHashMap） key的定义为
     * “，如果仅是表名，则返回整个表，否则只返回表的一条记录
     *
     * @param key 表名.键值
     * @return “表名.键值”，如果仅是表名，则返回整个表，否则只返回表的一条记录
     */
    @SuppressWarnings("unchecked")
    public DictionaryItem getItemById(String key) {
        return (DictionaryItem) getFromCache(key, true);
    }

    public DictionaryItem getItemByValue(String key) {
        return (DictionaryItem) getFromCache(key, false);
    }

    public DictionaryItem getItemByValue(String key, boolean doRefreshIfNotFindInCache) {
        return (DictionaryItem) getFromCache(key, false, doRefreshIfNotFindInCache);
    }

    public Map<String, DictionaryItem> getDict(String tableBeanName) {
        return (Map<String, DictionaryItem>) getFromCache(tableBeanName, true);
    }

    /**
     * 得到 "&lt;option&gt;&lt;/option&gt;"
     *
     * @param tableBeanName      table name
     * @param strDefaultSelect   default selected item,if no condition item is selected.
     * @param strConditionSelect condition selected item
     * @return HTML option tag 字符串
     */
    public String getOptionList(String tableBeanName, String strDefaultSelect,
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

    private Object getFromCache(String key, boolean isById) {
        return getFromCache(key, isById, true);
    }

    private Object getFromCache(String key, boolean isById, boolean doRefreshIfNotFind) {
        Object myValue;

        Dictionary dict = parseKey(key);
        String storekey = getStoreKey(dict);

        // Get from the cache]
        myValue = (isById ? byId : byValue).get(storekey);
        if (dict.keyName == null) {
            // LinkedHashMap
            return myValue;
        } else {
            // DictionaryItem
            Object o = ((Map<String, DictionaryItem>) myValue).get(dict.keyName);

            return o;
        }

    }

    Object getFromDb(String key, boolean isById) {
        // Get the value (probably from the database)
        // myValue = "This is the content retrieved.";
        // Store in the cache
        Dictionary dict = parseKey(key);


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
            Map<String, DictionaryItem> mmById = new LinkedHashMap<>(li.size());
            Map<String, DictionaryItem> mmByValue = new LinkedHashMap<>(li.size());

            for (int i = 0; i < li.size(); i++) {
                mmById.put(li.get(i).getId() + "",
                        li.get(i));
                mmByValue.put(li.get(i).getValue()
                        + "", li.get(i));
            }

            byId.put(getStoreKey(dict), mmById);
            byValue.put(getStoreKey(dict), mmByValue);

            if (dict.keyName == null) {
                return isById ? mmById : mmByValue;
            } else {
                // DictionaryItem
                return (isById ? mmById : mmByValue).get(dict.keyName);
            }
        } catch (DbException e) {
            LOG.error("查询" + dict.tableName + "时数据库出错：");
            e.printStackTrace();
            return null;
        }
    }

    static class Dictionary {
        String tableName; // 表名
        String keyName; // 字段值
        String condition; // 附加条件
    }

}
