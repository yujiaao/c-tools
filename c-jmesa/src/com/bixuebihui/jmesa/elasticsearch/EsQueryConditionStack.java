package com.bixuebihui.jmesa.elasticsearch;

import com.bixuebihui.db.SqlHelper;
import com.bixuebihui.jdbc.SqlFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * to make up the query condition
 * @author xwx
 */
public class EsQueryConditionStack {
    ElasticSearchFilter filters;
    List<EsQueryConditionStack> orCond = null;
    List<EsQueryConditionStack> notCond = null;

    public EsQueryConditionStack(EsQueryConditionStack src) {
        this.filters =  src.filters ;
        if (src.orCond != null) {
            this.orCond = new ArrayList<>();
            this.orCond.addAll(src.orCond);
        }
        if (src.notCond != null) {
            this.notCond = new ArrayList<>();
            this.notCond.addAll(src.notCond);
        }
    }

    public EsQueryConditionStack() {
        filters = new ElasticSearchFilter();
    }


    /**
     * 增加
     *
     * @param condition 条件
     * @param ignoreNulls 忽略null参数
     * @return 当前实例
     */
    public EsQueryConditionStack eq(Map<String, Object> condition, boolean ignoreNulls) {
        Set<Map.Entry<String, Object>> e = condition.entrySet();
        for (Map.Entry<String, Object> entry:e) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (ignoreNulls && value == null) {
                continue;
            }
            eq(key, value);
        }
        return this;
    }

    /**
     * <p>eq.</p>
     *
     * @param condition a {@link java.util.Map} object.
     * @return a {@link SqlHelper} object.
     */
    public EsQueryConditionStack eq(Map<String, Object> condition) {
        return eq(condition, true);
    }

    /**
     * <p>eq.</p>
     *
     * @param field a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     * @return a {@link SqlHelper} object.
     */
    public EsQueryConditionStack eq(String field, Object value) {
        filters.addFilter(field, SqlFilter.Comparison.IS, new Object[]{value});
        return this;
    }
}
