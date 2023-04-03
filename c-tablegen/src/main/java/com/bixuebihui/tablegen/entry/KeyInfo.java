package com.bixuebihui.tablegen.entry;

import java.util.List;
import java.util.Map;

/**
 * @author xwx
 */
public interface KeyInfo {
    Map<String, List<String>> getKeyCache();

    List<String> getTableKeys(String tableName);
}
