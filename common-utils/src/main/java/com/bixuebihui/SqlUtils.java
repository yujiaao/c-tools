package com.bixuebihui;


import org.apache.commons.lang3.StringUtils;

public class SqlUtils {


    public static String formGroupQuery(String tableName,String keyName, String field, String groupFunction, String where, String having, String[] groupBy) {
        assert field!=null;

        String keyFieldName = StringUtils.isEmpty(keyName) ? field : keyName;
        final boolean noGroup = (groupBy == null || groupBy.length == 0);

        String groupKeys = noGroup ? "" : " concat(" + StringUtils.join(groupBy, ",") + ") ";

        String groupByClause = noGroup ? "" : "group by gv_key";

        String query = "select count(" + keyFieldName + ") gv_cnt, " + groupFunction + "(" + field + ") gv_sm " + (groupKeys.length()>0? ","+groupKeys+" gv_key":"")
                + " from " + tableName + " " + where  +" "+ groupByClause +" "+ (having == null ? "" : having);
        return query;
    }
}
