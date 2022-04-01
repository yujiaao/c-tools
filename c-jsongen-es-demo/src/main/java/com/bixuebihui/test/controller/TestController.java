package com.bixuebihui.test.controller;


import com.bixuebihui.elasticsearch.EsActiveRecordImpl;
import com.bixuebihui.elasticsearch.EsOperator;
import com.bixuebihui.elasticsearch.EsRequest;
import com.bixuebihui.jsongen.MobileVideoInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xwx
 */
@RestController
@RequestMapping("/es")
@Tag(name = "开发人员工具-数据源")
public class TestController {

    String indexName =  "mobile-video-info";

    private EsActiveRecordImpl<MobileVideoInfo, Long> repository = new EsActiveRecordImpl<>(
            new EsOperator<>( new EsRequest("http://yz-test1.es6.autohome.com.cn:80","","", indexName)) {
                @Override
                protected Class<MobileVideoInfo> getType() {
                    return MobileVideoInfo.class;
                }

    });

    @GetMapping
    public List<MobileVideoInfo> getAllConfig() {
        return ((EsActiveRecordImpl<MobileVideoInfo, Long>)repository.limit(0,10))
                .findAllDocMeta()
                .stream()
                .map(x -> {
                    x.get_source().setAdditionalProperty("_id", x.get_id());
                    return x.get_source();
                })
                .collect(Collectors.toList());
    }


    @GetMapping("/_update")
    public MobileVideoInfo update(
            @RequestParam(value = "id", required = true) int id) {
        MobileVideoInfo content = repository.eq("biz_id", id).find();

        int recommend = content.getIsRecommend().intValue();

         repository.eq("biz_id", id)
                .update("is_recommend", recommend==0?1:0);

        return repository.eq("biz_id", id).find();
    }

}
