package com.bixuebihui.test.controller.autogenerate;


import com.bixuebihui.test.cmd.InfoAuthorTermDataDelCmd;

import com.bixuebihui.test.dto.InfoAuthorTermDataDto;
import com.bixuebihui.test.cmd.InfoAuthorTermDataSearchCmd;
import com.bixuebihui.test.service.autogenerate.InfoAuthorTermDataService;
import org.apache.commons.lang3.tuple.Pair;

import com.bixuebihui.query.Paging;
import com.bixuebihui.BaseModel;
import com.bixuebihui.PageModel;




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
@RequestMapping("/info-author-term-data")
public class  InfoAuthorTermDataController {
    private final InfoAuthorTermDataService infoAuthorTermDataService;

    public InfoAuthorTermDataController(InfoAuthorTermDataService infoAuthorTermDataService) {
        this.infoAuthorTermDataService = infoAuthorTermDataService;
    }

    @PostMapping(value = "/list", produces = {"application/json;charset=UTF-8"})
    public BaseModel<PageModel<InfoAuthorTermDataDto>> list(
    @RequestParam(value = "maxRows", defaultValue = "10") Long maxRows,
    @RequestParam(value = "page", defaultValue = "1") Long page,
    @RequestParam(value = "sortBy", defaultValue = "id,dt:desc") String sortBy,
        @Validated
    InfoAuthorTermDataSearchCmd  searchCmd) {

        Paging<InfoAuthorTermDataSearchCmd> searchBean = new Paging<>();
        searchBean.setSearchBean(searchCmd);
        searchBean.setStart(maxRows*(page-1));
        searchBean.setCount(maxRows);
        searchBean.parseSort(sortBy);

        StopWatch sw = new StopWatch();
        sw.start();
        Pair<Integer, List<InfoAuthorTermDataDto>> p= infoAuthorTermDataService.queryList(searchBean);

        PageModel<InfoAuthorTermDataDto> result = PageModel
                .builder(p.getRight())
                .total(p.getLeft())
                .build();
        sw.stop();
        result.setTook(sw.getTime());

        BaseModel<PageModel<InfoAuthorTermDataDto>> res = new BaseModel<>();
        res.setResult(result);
        res.setReturncode(0);
        res.setMessage("success");
        return res;
    }

    @PostMapping(value = "/del", produces = {"application/json;charset=UTF-8"})
    public BaseModel<Boolean> del(
        @Validated
    InfoAuthorTermDataDelCmd  delCmd) {

        BaseModel<Boolean> res = new BaseModel<>();
            res.setResult( infoAuthorTermDataService.del(delCmd));
            res.setReturncode(0);
            res.setMessage("success");
        return res;
    }

    @PostMapping(value = "/save", produces = {"application/json;charset=UTF-8"})
    public BaseModel<InfoAuthorTermDataDto> save(
        @Validated
        @RequestBody
        InfoAuthorTermDataDto  dto) {

        BaseModel<InfoAuthorTermDataDto> res = new BaseModel<>();
            res.setResult( infoAuthorTermDataService.save(dto));
            res.setReturncode(0);
            res.setMessage("success");
        return res;
    }

}
