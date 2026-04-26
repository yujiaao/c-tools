package com.bixuebihui.test.dto;

/*
 *  TestGenDto
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */
import java.sql.*;
import java.io.Serializable;
import org.apache.commons.text.StringEscapeUtils;
import com.sdadas.spring2ts.annotations.SharedModel;
import io.swagger.v3.oas.annotations.media.Schema;

@SharedModel("")
@Schema(description = "测试表")
public class TestGenDto implements Serializable {

    public TestGenDto() {
        id = 0;
        name = "";
        age = 0;
        birth = new Timestamp(System.currentTimeMillis());
        eduId = 0;
        gender = "";
        sex = Boolean.FALSE;
        child = Boolean.FALSE;
    }

    /**
     * id 这里是id!
     */
    @Schema(description = "id 这里是id! ")
    protected Integer id;

    /**
     * name 这里是名称！
     */
    @Schema(description = "name 这里是名称！ ")
    protected String name;

    /**
     * age 这里是年龄
     */
    @Schema(description = "age 这里是年龄 ")
    protected Short age;

    /**
     * birth 这里是日期！
     */
    @Schema(description = "birth 这里是日期！ ")
    protected Timestamp birth;

    /**
     * edu_id 教育程度
     */
    @Schema(description = "edu_id 教育程度 ")
    protected Integer eduId;

    /**
     * gender 性别
     */
    @Schema(description = "gender 性别 ")
    protected String gender;

    /**
     * sex 性别
     */
    @Schema(description = "sex 性别 ")
    protected Boolean sex;

    /**
     * child 是否有孩子
     */
    @Schema(description = "child 是否有孩子 ")
    protected Boolean child;

    /**
     * id 这里是id!
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * id 这里是id!
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * name 这里是名称！
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * name 这里是名称！
     */
    public String getName() {
        return this.name;
    }

    /**
     * age 这里是年龄
     */
    public void setAge(Short age) {
        this.age = age;
    }

    /**
     * age 这里是年龄
     */
    public Short getAge() {
        return this.age;
    }

    /**
     * birth 这里是日期！
     */
    public void setBirth(Timestamp birth) {
        this.birth = birth;
    }

    /**
     * birth 这里是日期！
     */
    public Timestamp getBirth() {
        return this.birth;
    }

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

    /**
     * gender 性别
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * gender 性别
     */
    public String getGender() {
        return this.gender;
    }

    /**
     * sex 性别
     */
    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    /**
     * sex 性别
     */
    public Boolean getSex() {
        return this.sex;
    }

    /**
     * child 是否有孩子
     */
    public void setChild(Boolean child) {
        this.child = child;
    }

    /**
     * child 是否有孩子
     */
    public Boolean getChild() {
        return this.child;
    }

    public String toXml() {
        StringBuilder s = new StringBuilder();
        String ln = System.getProperty("line.separator");
        s.append("<test_gen ");
        s.append(" id=\"").append(this.getId()).append("\"");
        s.append(" name=\"").append(StringEscapeUtils.escapeXml11(this.getName())).append("\"");
        s.append(" age=\"").append(this.getAge()).append("\"");
        s.append(" birth=\"").append(this.getBirth()).append("\"");
        s.append(" eduId=\"").append(this.getEduId()).append("\"");
        s.append(" gender=\"").append(StringEscapeUtils.escapeXml11(this.getGender())).append("\"");
        s.append(" sex=\"").append(this.getSex()).append("\"");
        s.append(" child=\"").append(this.getChild()).append("\"");
        s.append(" />");
        s.append(ln);
        return s.toString();
    }
}
