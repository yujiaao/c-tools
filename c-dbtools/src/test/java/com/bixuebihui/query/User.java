package com.bixuebihui.query;

import java.sql.Timestamp;

public class User {
    Integer id;
    String name;

    public Timestamp getBegin_ModifiedTime() {
        return begin_ModifiedTime;
    }

    public void setBegin_ModifiedTime(Timestamp begin_ModifiedTime) {
        this.begin_ModifiedTime = begin_ModifiedTime;
    }

    public Timestamp getEnd_ModifiedTime() {
        return end_ModifiedTime;
    }

    public void setEnd_ModifiedTime(Timestamp end_ModifiedTime) {
        this.end_ModifiedTime = end_ModifiedTime;
    }

    Timestamp begin_ModifiedTime;
    Timestamp end_ModifiedTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
