package com.bixuebihui.elasticsearch.aggregation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;

public class EsAggregationBuilder {

    /**
     // 按时间聚合
     "aggs": {
     // 自己取的聚合名字
     "group_by_grabTime": {
     // es提供的时间处理函数
     "date_histogram": {
     // 需要聚合分组的字段名称, 类型需要为date, 格式没有要求
     "field": "@timestamp",
     // 按什么时间段聚合, 这里是5分钟, 可用的interval在上面给出
     "interval": "5m",
     // 设置时区, 这样就相当于东八区的时间
     "time_zone":"+08:00",
     // 返回值格式化，HH大写，不然不能区分上午、下午
     "format": "yyyy-MM-dd HH",
     // 为空的话则填充0
     "min_doc_count": 0,
     // 需要填充0的范围
     "extended_bounds": {
     "min": 1533556800000,
     "max": 1533806520000
     }
     },
     // 聚合
     "aggs": {
     // 自己取的名称
     "group_by_status": {
     // es提供
     "terms": {
     // 聚合字段名
     "field": "LowStatusOfPrice"
     }
     }
     }
     }
     */
    //beijing time
    public static final String timezone= "+08:00";
    public static DateHistogram buildDateHistogram(String name, String field, String interval,
                                                   String format,  String min, String max) {

        DateHistogram res = new DateHistogram(name, field, interval);
        //int factor =1;
        //res.setFactor(factor);
        //res.setOffset(offset);
        res.setFormat(format);
        res.setExtendedBounds(min, max);
        res.setTimezone(timezone);


        return  res;
    }

    public static DateHistogram buildDateHistogram(String field, String interval, String termField, String aggFunction) {

        DateHistogram res = new DateHistogram("date_hist_by_"+field, field, interval);

        res.setTimezone(timezone);
        if(termField!=null && !termField.isEmpty()){
            res.addAggregation(new Terms("group_by_"+termField)
                    .setField( termField));
        }
        if(aggFunction!=null && !aggFunction.isEmpty()){
            addAggFun(res, termField, aggFunction, false);
        }


        return  res;
    }

    public static DateHistogram buildDateHistogramMulti(String dateField, String interval,
                                                   List<String> groupByField,
                                                   Map<String,String> termFieldAggFunctionMap, String esVersion) {

        DateHistogram res = new DateHistogram("date_hist_by_"+dateField, dateField, interval, esVersion);

        res.setTimezone(timezone);
        return (DateHistogram) formAggregationQuery(groupByField, termFieldAggFunctionMap, res, emptySet());
    }

    public static Terms buildTermsAggMulti(List<String> groupByField,
                                                        Map<String,String> termFieldAggFunctionMap,
                                           Set<String> textFieldsNeededConvertToDouble) {

        Terms res = formTermsAgg(groupByField);
        return (Terms) formAggregationQuery(Collections.emptyList(), termFieldAggFunctionMap, res, textFieldsNeededConvertToDouble);
    }

    static Terms formTermsAgg(List<String> fields){
        if(fields.size()==1) {
            return (Terms) new Terms("group_by_" + fields.get(0))
                    .setField(fields.get(0));
        }else if(fields.size()>1){
            return (Terms) new Terms("group_by_" + String.join("_", fields))
                    .setScript("doc['"+String.join("'].value+'_'+doc['", fields)+"'].value");
        }
        throw new IllegalArgumentException("fields list must not empty");
    }

    private static AbstractAggregation formAggregationQuery(List<String> groupByField,
                                                            Map<String, String> termFieldAggFunctionMap,
                                                            AbstractAggregation res, Set<String> textFieldsNeededConvertToDouble) {
        if(groupByField.size()==1) {
            res.addAggregation(new Terms("group_by_" + groupByField.get(0))
                    .setField(groupByField.get(0)));
        }else if(groupByField.size()>1){
            res.addAggregation(new Terms("group_by_" + String.join("_", groupByField))
                    .setScript("doc['"+String.join("'].value+'_'+doc['", groupByField)+"'].value"));
        }

        if(termFieldAggFunctionMap !=null && !termFieldAggFunctionMap.isEmpty()){
            for(Map.Entry<String,String> entry: termFieldAggFunctionMap.entrySet()){
                String termField = entry.getKey();
                String aggFunction = entry.getValue();
                if(termField!=null && !termField.isEmpty() && aggFunction!=null && !aggFunction.isEmpty()){
                    addAggFun(res, termField, aggFunction, textFieldsNeededConvertToDouble.contains(termField));
                }
            }
        }
        return res;
    }

    private static void addAggFun(AbstractAggregation res, String termField, String aggFunction, boolean textNeedConvertToDouble) {

        String alias = aggFunction + "_" + termField;
        AbstractAggregation agg;
        switch (aggFunction) {
            case "count":
                agg = new ValueCount(alias, termField);
                break;
            case "sum":
                agg = new Sum(alias);
                break;
            case "max":
                agg = new Max(alias);
                break;
            case "min":
                agg = new Min(alias);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + aggFunction);
        }
        if(agg instanceof AbstractSimpleAggregation) {
            setupAgg((AbstractSimpleAggregation)agg, termField, textNeedConvertToDouble);
        }
        res.addAggregation(agg);

    }

    private static void setupAgg( AbstractSimpleAggregation agg, String termField, boolean textNeedConvertToDouble) {
        if (textNeedConvertToDouble) {
            agg.setScript("String string = doc['"+termField+"'].value; return  (string==null || string.isEmpty()) ? 0.0 : Double.parseDouble(string)");
        } else {
            agg.setField(termField);
        }
    }


}
