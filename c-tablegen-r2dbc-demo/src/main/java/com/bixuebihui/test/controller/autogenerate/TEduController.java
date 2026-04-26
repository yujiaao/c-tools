package com.bixuebihui.test.controller.autogenerate;


import com.bixuebihui.test.cmd.TEduDelCmd;

import com.bixuebihui.test.dto.TEduDto;
import com.bixuebihui.test.cmd.TEduSearchCmd;
import com.bixuebihui.test.service.autogenerate.TEduService;
import org.apache.commons.lang3.tuple.Pair;

import com.bixuebihui.query.Paging;
import com.bixuebihui.BaseModel;
import com.bixuebihui.PageModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;



import org.apache.commons.lang3.time.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.RequestBody;

import com.sdadas.spring2ts.annotations.SharedService;

import java.util.List;

/**
 * @author generated
 */
@SharedService
@RestController
@RequestMapping("/t-edu")

@Tag(name = "TEduController", description = "教育程度")

public class  TEduController {
    private final TEduService tEduService;

    public TEduController(TEduService tEduService) {
        this.tEduService = tEduService;
    }

    @Operation(summary ="取t_edu列表")
    @PostMapping(value = "/list", produces = {"application/json;charset=UTF-8"})
    public BaseModel<PageModel<TEduDto>> list(
    @Parameter(name ="maxRows", description = "一页大小")
        @RequestParam(value = "maxRows", defaultValue = "10") Long maxRows,
    @Parameter(name ="page", description = "页码")
        @RequestParam(value = "page", defaultValue = "1") Long page,
    @Parameter(name ="sortBy", description = "排序 sortBy=abc:asc,def:desc")
        @RequestParam(value = "sortBy", defaultValue = "id:desc") String sortBy,
        @Validated
    @Parameter(required = true, description = "查询条件")
        TEduSearchCmd  searchCmd) {

        Paging<TEduSearchCmd> searchBean = new Paging<>();
        searchBean.setSearchBean(searchCmd);
        searchBean.setStart(maxRows*(page-1));
        searchBean.setCount(maxRows);
        searchBean.parseSort(sortBy);

        StopWatch sw = new StopWatch();
        sw.start();
        Pair<Integer, List<TEduDto>> p= tEduService.queryList(searchBean);

        PageModel<TEduDto> result = PageModel
                .builder(p.getRight())
                .total(p.getLeft())
                .build();
        sw.stop();
        result.setTook(sw.getTime());

        BaseModel<PageModel<TEduDto>> res = new BaseModel<>();
        res.setResult(result);
        res.setReturncode(0);
        res.setMessage("success");
        return res;
    }

    @Operation(summary ="按主键删除t_edu记录")
    @PostMapping(value = "/del", produces = {"application/json;charset=UTF-8"})
    public BaseModel<Boolean> del(
        @Validated
    @Parameter(required = true, description = "主键")
        TEduDelCmd  delCmd) {

        BaseModel<Boolean> res = new BaseModel<>();
            res.setResult( tEduService.del(delCmd));
            res.setReturncode(0);
            res.setMessage("success");
        return res;
    }

    @Operation(summary ="保存t_edu记录")
    @PostMapping(value = "/save", produces = {"application/json;charset=UTF-8"})
    public BaseModel<TEduDto> save(
        @Validated
        @Parameter(required = true, description = "t_edu 数据对象")
            @RequestBody
            TEduDto  dto) {

        BaseModel<TEduDto> res = new BaseModel<>();
            res.setResult( tEduService.save(dto));
            res.setReturncode(0);
            res.setMessage("success");
        return res;
    }

}
