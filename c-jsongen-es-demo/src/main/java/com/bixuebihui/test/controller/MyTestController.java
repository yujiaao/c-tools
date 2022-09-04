package com.bixuebihui.test.controller;


import com.bixuebihui.elasticsearch.BaseEsOperator;
import com.bixuebihui.elasticsearch.EsActiveRecordImpl;
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
public class MyTestController {

    String indexName =  "my-index-000001";

    private EsActiveRecordImpl<MobileVideoInfo, Long> repository = new EsActiveRecordImpl<>(
            new BaseEsOperator<>( new EsRequest("http://localhost:9200","","", indexName)) {
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


    @GetMapping("/update")
    public String update(
            @RequestParam(value = "abc", required = true) int abc) {
//        MobileVideoInfo content = repository.eq("abc", abc).find();
//
//        int recommend = content.getIsRecommend().intValue();

         repository.eq("abc", abc)
                .update(new String[]{"is_recommend", "author_name"}, new Object[]{1, "邢'万\"祥"});

         return "OK";

//        return repository.eq("abc", abc).find();
    }

}
