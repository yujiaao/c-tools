package com.bixuebihui.test.view.cmd;

import java.sql.*;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;

/*
 *  MyTestViewSearchCmd
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */
/**
 * @author xwx
 * https://stackoverflow.com/questions/26612404/spring-map-get-request-parameters-to-pojo-automatically/26612778#26612778
 */
public class MyTestViewSearchCmd {

    /**
     * edu_id 教育程度
     *  change type of Integer to List<Integer> will generate 'in' sql condition.
     */
    @ApiModelProperty("教育程度")
    protected Integer eduId;

    public void setEduId(Integer eduId) {
        this.eduId = eduId;
    }

    public Integer getEduId() {
        return this.eduId;
    }
}
