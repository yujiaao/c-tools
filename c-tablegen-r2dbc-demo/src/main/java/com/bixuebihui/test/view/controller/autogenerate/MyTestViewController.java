package com.bixuebihui.test.view.controller.autogenerate;


import com.bixuebihui.test.view.dto.MyTestViewDto;
import com.bixuebihui.test.view.cmd.MyTestViewSearchCmd;
import com.bixuebihui.test.view.service.autogenerate.MyTestViewService;
import org.apache.commons.lang3.tuple.Pair;

import com.bixuebihui.query.Paging;
import com.bixuebihui.BaseModel;
import com.bixuebihui.PageModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;



import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.validation.annotation.Validated;

import com.sdadas.spring2ts.annotations.SharedService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author generated
 */
@SharedService
@RestController
@RequestMapping("/my-test-view")

@Tag(name = "MyTestViewController", description = "my_test_view")

public class  MyTestViewController {
    @Autowired
    MyTestViewService myTestViewService;

    @Operation(summary ="取my_test_view列表")
    @PostMapping(value = "/list", produces = {"application/json;charset=UTF-8"})
    public BaseModel<PageModel<MyTestViewDto>> list(
    @Parameter(name ="maxRows", description = "一页大小")
        @RequestParam(value = "maxRows", defaultValue = "10") Long maxRows,
    @Parameter(name ="page", description = "页码")
        @RequestParam(value = "page", defaultValue = "1") Long page,
    @Parameter(name ="sortBy", description = "排序 sortBy=abc:asc,def:desc")
        @RequestParam(value = "sortBy", defaultValue = "id:desc") String sortBy,
        @Validated
    @Parameter(required = true, description = "查询条件")
        MyTestViewSearchCmd  searchCmd,
        HttpServletRequest request) {

        Paging<MyTestViewSearchCmd> searchBean = new Paging<>();
        searchBean.setSearchBean(searchCmd);
        searchBean.setStart(maxRows*(page-1));
        searchBean.setCount(maxRows);
        searchBean.parseSort(sortBy);

        StopWatch sw = new StopWatch();
        sw.start();
        Pair<Integer, List<MyTestViewDto>> p= myTestViewService.queryList(searchBean);

        PageModel<MyTestViewDto> result = PageModel
                .builder(p.getRight())
                .total(p.getLeft())
                .build();
        sw.stop();
        result.setTook(sw.getTime());

        BaseModel<PageModel<MyTestViewDto>> res = new BaseModel<>();
        res.setResult(result);
        res.setReturncode(0);
        res.setMessage("success");
        return res;
    }

}
