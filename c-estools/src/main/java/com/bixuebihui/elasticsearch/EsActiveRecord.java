package com.bixuebihui.elasticsearch;

import com.bixuebihui.DbException;
import com.bixuebihui.db.ActiveRecord;
import com.bixuebihui.db.GroupFunction;
import com.bixuebihui.elasticsearch.aggregation.CountObj;
import com.bixuebihui.elasticsearch.aggregation.CountValueObj;
import com.bixuebihui.jdbc.entity.CountValue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author xwx
 */
public interface EsActiveRecord<T> extends ActiveRecord<T> {

    List<CountValueObj> aggregationList(List<String> groupByFields,
                                        Map<String, GroupFunction> termFieldAggFunctionMap, Set<String> textFieldsNeededConvertToDouble);

    List<CountValueObj> aggregationList(List<String> groupByFields, Map<String, GroupFunction> termFieldAggFunctionMap);

    CountValue countSum(String field, boolean needConvertToDouble) throws DbException;

    int update(String _id, String field, Object value) throws DbException;

    int update(String _id, String[] fields, Object[] values) throws DbException;

    EsActiveRecord<T> excludeFields(Set<String> excludeFieldsSet);


    int updateByScript(String script, Map<String, Object> params) throws DbException;

    boolean insert(String indexName, T info);

    boolean insert(String indexName, T info, Function<T, String> getId);
    boolean insertBatch(String indexName, List<T> infos, Function<T, String> getId);

    EsActiveRecord<T> setFilter(ElasticSearchFilter filter);

    EsActiveRecord<T> searchAfter(List<Object> lastOrderFieldsValue);

    List<CountObj> dateHistCountList(String dateField, String interval, String[] groupByFields, GroupFunction fun);

}
