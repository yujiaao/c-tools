package com.bixuebihui.query;

import java.sql.Timestamp;

public class User {
    Integer id;
    String name;

    //int age;

    Timestamp begin_ModifiedTime;
    Timestamp end_ModifiedTime;
    java.sql.Date begin_AuditDate;
    java.sql.Date end_AuditDate;
    Integer begin_age;
    Integer end_age;

    public java.sql.Date getBegin_AuditDate() {
        return begin_AuditDate;
    }

    public void setBegin_AuditDate(java.sql.Date begin_AuditDate) {
        this.begin_AuditDate = begin_AuditDate;
    }

    public java.sql.Date getEnd_AuditDate() {
        return end_AuditDate;
    }

    public void setEnd_AuditDate(java.sql.Date end_AuditDate) {
        this.end_AuditDate = end_AuditDate;
    }

    public String getBegin_name() {
        return begin_name;
    }

    public void setBegin_name(String begin_name) {
        this.begin_name = begin_name;
    }

    public String getEnd_name() {
        return end_name;
    }

    public void setEnd_name(String end_name) {
        this.end_name = end_name;
    }

    String begin_name;
    String end_name;

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

    public Integer getBegin_age() {
        return begin_age;
    }

    public void setBegin_age(Integer begin_age) {
        this.begin_age = begin_age;
    }

    public Integer getEnd_age() {
        return end_age;
    }

    public void setEnd_age(Integer end_age) {
        this.end_age = end_age;
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
