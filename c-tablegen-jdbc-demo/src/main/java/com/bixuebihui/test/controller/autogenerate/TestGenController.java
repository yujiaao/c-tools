package com.bixuebihui.test.controller.autogenerate;


import com.bixuebihui.test.cmd.TestGenDelCmd;

import com.bixuebihui.test.dto.TestGenDto;
import com.bixuebihui.test.cmd.TestGenSearchCmd;
import com.bixuebihui.test.service.autogenerate.TestGenService;
import org.apache.commons.lang3.tuple.Pair;

import com.bixuebihui.query.Paging;
import com.bixuebihui.BaseModel;
import com.bixuebihui.PageModel;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import com.sdadas.spring2ts.annotations.SharedService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author generated
 */
@SharedService
@RestController
@RequestMapping("/test-gen")
@Api(tags = "测试表")
public class  TestGenController {
    @Autowired
    TestGenService testGenService;

    @ApiOperation("取test_gen列表")
    @PostMapping(value = "/list", produces = {"application/json;charset=UTF-8"})
    public BaseModel<PageModel<TestGenDto>> list(
    @ApiParam(name = "maxRows", value = "一页大小")
    @RequestParam(value = "maxRows", defaultValue = "10") Long maxRows,
    @ApiParam(name = "page", value = "页码")
    @RequestParam(value = "page", defaultValue = "1") Long page,
    @ApiParam(name = "sortBy", value = "排序 sortBy=abc:asc,def:desc")
    @RequestParam(value = "sortBy", defaultValue = "id:desc") String sortBy,
        @Validated
    @ApiParam(value = "查询条件", required = true)
    TestGenSearchCmd  searchCmd,
        HttpServletRequest request) {

        Paging<TestGenSearchCmd> searchBean = new Paging<>();
        searchBean.setSearchBean(searchCmd);
        searchBean.setStart(maxRows*(page-1));
        searchBean.setCount(maxRows);
        searchBean.parseSort(sortBy);

        StopWatch sw = new StopWatch();
        sw.start();
        Pair<Integer, List<TestGenDto>> p= testGenService.queryList(searchBean);

        PageModel<TestGenDto> result = PageModel
                .builder(p.getRight())
                .total(p.getLeft())
                .build();
        sw.stop();
        result.setTook(sw.getTime());

        BaseModel<PageModel<TestGenDto>> res = new BaseModel<>();
        res.setResult(result);
        res.setReturncode(0);
        res.setMessage("success");
        return res;
    }

    @ApiOperation("按主键删除test_gen记录")
    @PostMapping(value = "/del", produces = {"application/json;charset=UTF-8"})
    public BaseModel<Boolean> del(
        @Validated
    @ApiParam(value = "主键", required = true)
    TestGenDelCmd  delCmd,
        HttpServletRequest request) {

        BaseModel<Boolean> res = new BaseModel<>();
            res.setResult( testGenService.del(delCmd));
            res.setReturncode(0);
            res.setMessage("success");
        return res;
    }

    @ApiOperation("保存test_gen记录")
    @PostMapping(value = "/save", produces = {"application/json;charset=UTF-8"})
    public BaseModel<TestGenDto> save(
        @Validated
        @ApiParam(value = "test_gen 数据对象", required = true)
        @RequestBody
            TestGenDto  dto, HttpServletRequest request) {

        BaseModel<TestGenDto> res = new BaseModel<>();
            res.setResult( testGenService.save(dto));
            res.setReturncode(0);
            res.setMessage("success");
        return res;
    }

}
