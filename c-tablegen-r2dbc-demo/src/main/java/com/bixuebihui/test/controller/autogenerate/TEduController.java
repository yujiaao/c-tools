package com.bixuebihui.test.controller.autogenerate;


import com.bixuebihui.test.cmd.TEduDelCmd;

import com.bixuebihui.test.dto.TEduDto;
import com.bixuebihui.test.cmd.TEduSearchCmd;
import com.bixuebihui.test.service.autogenerate.TEduService;
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
@RequestMapping("/t-edu")

public class  TEduController {
    @Autowired
    TEduService tEduService;

    
    @PostMapping(value = "/list", produces = {"application/json;charset=UTF-8"})
    public BaseModel<PageModel<TEduDto>> list(
    
        @RequestParam(value = "maxRows", defaultValue = "10") Long maxRows,
    
        @RequestParam(value = "page", defaultValue = "1") Long page,
    
        @RequestParam(value = "sortBy", defaultValue = "id:desc") String sortBy,
        @Validated
    
        TEduSearchCmd  searchCmd,
        HttpServletRequest request) {

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
        result.setTook(sw.getDuration().toMillis());

        BaseModel<PageModel<TEduDto>> res = new BaseModel<>();
        res.setResult(result);
        res.setReturncode(0);
        res.setMessage("success");
        return res;
    }

    
    @PostMapping(value = "/del", produces = {"application/json;charset=UTF-8"})
    public BaseModel<Boolean> del(
        @Validated
    
        TEduDelCmd  delCmd,
        HttpServletRequest request) {

        BaseModel<Boolean> res = new BaseModel<>();
            res.setResult( tEduService.del(delCmd));
            res.setReturncode(0);
            res.setMessage("success");
        return res;
    }

    
    @PostMapping(value = "/save", produces = {"application/json;charset=UTF-8"})
    public BaseModel<TEduDto> save(
        @Validated
        
            @RequestBody
            TEduDto  dto, HttpServletRequest request) {

        BaseModel<TEduDto> res = new BaseModel<>();
            res.setResult( tEduService.save(dto));
            res.setReturncode(0);
            res.setMessage("success");
        return res;
    }

}
