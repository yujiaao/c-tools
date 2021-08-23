package com.bixuebihui.test.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Permission;

/**
 * @author xwx
 */
@RestController
@RequestMapping("/datasource")
@Tag(name = "开发人员工具-数据源")
//@Authorize(permission = "datasource", description = "数据源管理")
public class TestController {

////    @Autowired
////    private DynamicDataSourceConfigRepository<? extends DynamicDataSourceConfig> repository;
//
//    @GetMapping
////    @Authorize(action = Permission.ACTION_QUERY)
//    @ApiOperation("获取全部数据源信息")
//    public ResponseMessage<List<? extends DynamicDataSourceConfig>> getAllConfig() {
//        return ResponseMessage.ok(repository.findAll());
//    }

}
