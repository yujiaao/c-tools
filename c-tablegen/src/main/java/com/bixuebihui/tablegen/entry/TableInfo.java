package com.bixuebihui.tablegen.entry;

import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author xwx
 */
public class TableInfo {
   String name;
   String comment;

   String viewSql;

   List<ColumnData> fields;
    boolean isView=false;

    public TableInfo(String name) {
        this.name = name;
    }

    public boolean isView() {
        return isView;
    }

    public void setView(boolean view) {
        isView = view;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", fields=" + fields +
                ", isView=" + isView +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        if(StringUtils.isBlank(comment)){
            return name;
        }
        return org.apache.commons.lang3.StringUtils.abbreviate( comment.replace("\n"," "),200);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ColumnData> getFields() {
        return fields;
    }

    public void setFields(List<ColumnData> fields) {
        this.fields = fields;
    }

    public String getViewSql() {
        return viewSql;
    }

    public void setViewSql(String viewSql) {
        this.viewSql = viewSql;
    }
}
