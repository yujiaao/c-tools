package com.bixuebihui.test.view.dto;

/*
 *  MyTestViewDto
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */
import java.sql.*;
import java.io.Serializable;
import com.sdadas.spring2ts.annotations.SharedModel;

@SharedModel("")
public class MyTestViewDto implements Serializable {

    public MyTestViewDto() {
        eduId = 0;
    }

    /**
     * edu_id 教育程度
     */
    protected Integer eduId;

    /**
     * edu_id 教育程度
     */
    public void setEduId(Integer eduId) {
        this.eduId = eduId;
    }

    /**
     * edu_id 教育程度
     */
    public Integer getEduId() {
        return this.eduId;
    }

    public String toXml() {
        StringBuilder s = new StringBuilder();
        String ln = System.getProperty("line.separator");
        s.append("<my_test_view ");
        s.append(" eduId=\"").append(this.getEduId()).append("\"");
        s.append(" />");
        s.append(ln);
        return s.toString();
    }
}
