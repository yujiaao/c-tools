package com.bixuebihui.test.controller;


import com.bixuebihui.elasticsearch.BaseEsOperator;
import com.bixuebihui.elasticsearch.EsActiveRecordImpl;
import com.bixuebihui.elasticsearch.EsRequest;

import com.example.es.pojo.Contents;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xwx
 */
@RestController
@RequestMapping("/es")
@Tag(name = "开发人员工具-数据源")
public class MyTestController {

    String indexName =  "my-index-000001";

    private EsActiveRecordImpl<Contents, Long> repository = new EsActiveRecordImpl<>(
            new BaseEsOperator<>( new EsRequest("http://localhost:9200","","", indexName)) {
                @Override
                protected Class<Contents> getType() {
                    return Contents.class;
                }

    });

    @GetMapping
    public List<Contents> getAllConfig() {
        return ((EsActiveRecordImpl<Contents, Long>)repository.limit(0,10))
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

         repository.eq("abc", abc)
                 .update(Map.of("is_recommend", 1, "author_name", "邢'万\"祥"));

         return "OK";


    }



}
