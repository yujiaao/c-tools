package com.bixuebihui.test.view.controller.autogenerate;


import com.bixuebihui.BaseModel;
import com.bixuebihui.PageModel;
import com.bixuebihui.query.Paging;
import com.bixuebihui.test.view.cmd.MyTestViewSearchCmd;
import com.bixuebihui.test.view.dto.MyTestViewDto;
import com.bixuebihui.test.view.service.autogenerate.MyTestViewService;
import com.sdadas.spring2ts.annotations.SharedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author generated
 */
@SharedService
@RestController
@RequestMapping("/my-test-view")
@Api(tags = " select test_gen.*, degree from test_gen left join t_edu on test_gen.edu_id&#x3D;t_edu.id")
public class  MyTestViewController {
    @Autowired
    MyTestViewService myTestViewService;

    @ApiOperation("取my_test_view列表")
    @PostMapping(value = "/list", produces = {"application/json;charset=UTF-8"})
    public BaseModel<PageModel<MyTestViewDto>> list(
    @ApiParam(name = "maxRows", value = "一页大小")
    @RequestParam(value = "maxRows", defaultValue = "10") Long maxRows,
    @ApiParam(name = "page", value = "页码")
    @RequestParam(value = "page", defaultValue = "1") Long page,
    @ApiParam(name = "sortBy", value = "排序 sortBy=abc:asc,def:desc")
    @RequestParam(value = "sortBy", defaultValue = "id:desc") String sortBy,
        @Validated
    @ApiParam(value = "查询条件", required = true)
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
