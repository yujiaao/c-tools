package com.bixuebihui.query;

import java.sql.Timestamp;

public class User {
    Integer id;
    String name;
    Timestamp bModifiedTime;
    Timestamp eModifiedTime;

    public Timestamp getbModifiedTime() {
        return bModifiedTime;
    }

    public void setbModifiedTime(Timestamp bModifiedTime) {
        this.bModifiedTime = bModifiedTime;
    }

    public Timestamp geteModifiedTime() {
        return eModifiedTime;
    }

    public void seteModifiedTime(Timestamp eModifiedTime) {
        this.eModifiedTime = eModifiedTime;
    }

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
