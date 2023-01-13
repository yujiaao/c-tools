package com.bixuebihui.test.controller;

import com.bixuebihui.test.dto.HistoryHandlebarsTemplateDto;
import com.bixuebihui.test.cmd.HistoryHandlebarsTemplateSearchCmd;
import com.bixuebihui.test.service.HistoryHandlebarsTemplateService;
import org.apache.commons.lang3.tuple.Pair;

import com.bixuebihui.query.Paging;
import com.bixuebihui.BaseModel;
import com.bixuebihui.PageModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.validation.annotation.Validated;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author generated
 */
@RestController
@RequestMapping("/history_handlebars_template")
@Api(tags = "数据源模板历史记录表")
public class  HistoryHandlebarsTemplateController {
    @Autowired
    HistoryHandlebarsTemplateService historyHandlebarsTemplateService;

    @ApiOperation("取history_handlebars_template列表")
    @GetMapping(value = "/list", produces = {"application/json;charset=UTF-8"})
    public BaseModel<PageModel<HistoryHandlebarsTemplateDto>> list(@ApiParam(name = "pageSize", value = "一页大小")
                                                                @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize,

                                                                @ApiParam(name = "pageNum", value = "页码")
                                                                @RequestParam(value = "pageNum", defaultValue = "1") Long pageNum,

                                                                @Validated
                                                                @ApiParam(value = "查询条件", required = true)
                                                                HistoryHandlebarsTemplateSearchCmd  searchCmd,
                                                                HttpServletRequest request
    ) {

        Paging<HistoryHandlebarsTemplateSearchCmd> searchBean = new Paging<>();
        searchBean.setSearchBean(searchCmd);
        searchBean.setStart(pageSize*pageNum);
        searchBean.setCount(pageSize);

        StopWatch sw = new StopWatch();
        sw.start();
        Pair<Integer, List<HistoryHandlebarsTemplateDto>> p= historyHandlebarsTemplateService.queryList(searchBean);

        PageModel<HistoryHandlebarsTemplateDto> result = PageModel
                .builder(p.getRight())
                .total(p.getLeft())
                .build();
        sw.stop();
        result.setTook(sw.getTime());


        BaseModel<PageModel<HistoryHandlebarsTemplateDto>> res = new BaseModel<>();
        res.setResult(result);
        res.setReturncode(0);
        res.setMessage("success");
        return res;
    }

}
