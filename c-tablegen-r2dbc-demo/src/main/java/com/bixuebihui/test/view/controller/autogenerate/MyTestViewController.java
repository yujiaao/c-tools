package com.bixuebihui.test.view.controller.autogenerate;


import com.bixuebihui.test.view.dto.MyTestViewDto;
import com.bixuebihui.test.view.cmd.MyTestViewSearchCmd;
import com.bixuebihui.test.view.service.autogenerate.MyTestViewService;
import org.apache.commons.lang3.tuple.Pair;

import com.bixuebihui.query.Paging;
import com.bixuebihui.BaseModel;
import com.bixuebihui.PageModel;



import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.validation.annotation.Validated;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.RequestBody;
import com.sdadas.spring2ts.annotations.SharedService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author generated
 */
@SharedService
@RestController
@RequestMapping("/my-test-view")

public class  MyTestViewController {
    @Autowired
    MyTestViewService myTestViewService;

    
    @PostMapping(value = "/list", produces = {"application/json;charset=UTF-8"})
    public BaseModel<PageModel<MyTestViewDto>> list(
    
        @RequestParam(value = "maxRows", defaultValue = "10") Long maxRows,
    
        @RequestParam(value = "page", defaultValue = "1") Long page,
    
        @RequestParam(value = "sortBy", defaultValue = "id:desc") String sortBy,
        @Validated
    
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
        result.setTook(sw.getDuration().toMillis());

        BaseModel<PageModel<MyTestViewDto>> res = new BaseModel<>();
        res.setResult(result);
        res.setReturncode(0);
        res.setMessage("success");
        return res;
    }

}
