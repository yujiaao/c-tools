package com.bixuebihui.tablegen.entry;

import com.bixuebihui.algorithm.LRULinkedHashMap;

import java.util.List;
import java.util.Map;

/**
 * @author xwx
 */
public class EsKeyInfo implements KeyInfo {

    private final Map<String, List<String>> keyCache = new LRULinkedHashMap<>(500);

    @Override
    public Map<String, List<String>> getKeyCache() {
        return keyCache;
    }

    @Override
    public List<String> getTableKeys(String tableName) {
        return keyCache.get(tableName);
    }

    public void setKeyCache(Map<String, String> keyNameTypeMap) {
        keyNameTypeMap.entrySet().stream().forEach(entry ->{
           keyCache.put(entry.getKey(), List.of(entry.getValue()));
        });
    }
}
