package com.bixuebihui.jdbc;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xwx
 */
public class SqlGroup {
    List<String> groupByFieldOrSqlString;
    public SqlGroup(){
        groupByFieldOrSqlString = new ArrayList<>();
    }

    public SqlGroup(SqlGroup groupByClause) {
        assert this.groupByFieldOrSqlString != null;
        this.groupByFieldOrSqlString.addAll(groupByClause.groupByFieldOrSqlString);
    }

    public void add(String... fieldsOrSqlString){
        groupByFieldOrSqlString.addAll(Arrays.asList(fieldsOrSqlString));
    }

    public void clear() {
        groupByFieldOrSqlString.clear();
    }

    @Override
    public String toString() {
        return groupByFieldOrSqlString.size()>0 ? " group by " +
                StringUtils.join(groupByFieldOrSqlString, ",") : "";
    }

    public String[] toArray(){
        return groupByFieldOrSqlString.toArray(new String[groupByFieldOrSqlString.size()]);
    }
}
