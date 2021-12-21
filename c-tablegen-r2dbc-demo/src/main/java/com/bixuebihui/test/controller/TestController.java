package com.bixuebihui.test.controller;

import com.bixuebihui.test.business.TestGenManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xwx
 */
@RestController
@RequestMapping("/datasource")
@Tag(name = "开发人员工具-数据源")
//@Authorize(permission = "datasource", description = "数据源管理")
public class TestController {
   static Logger LOG = LoggerFactory.getLogger(TestController.class);

////    @Autowired
////    private DynamicDataSourceConfigRepository<? extends DynamicDataSourceConfig> repository;
//
//    @GetMapping
////    @Authorize(action = Permission.ACTION_QUERY)
//    @ApiOperation("获取全部数据源信息")
//    public ResponseMessage<List<? extends DynamicDataSourceConfig>> getAllConfig() {
//        return ResponseMessage.ok(repository.findAll());
//    }

    @Resource
    TestGenManager manager;

    @GetMapping("/count")
    public Mono<String> count(){
        return manager.count().flatMap( x -> Mono.just("have records="+x));
    }
    @GetMapping("/test")
    public Mono<String> scale(){
        return manager.getFirst().doOnNext(x -> LOG.info(x.toXml())).map( x -> "first = " + StringEscapeUtils.escapeHtml(x.toXml()));
    }

    @GetMapping("/id")
    public Mono<List<Long>> ids(){
        return manager.getIds();
    }


    @GetMapping("/insert")
    public Mono<Integer> insert(){
        return manager.batchInsert();
    }


    @GetMapping("/insert-sequence")
    public Mono<Integer> insertAutoNewKey(){
        return manager.batchInsertAutoNewKey();
    }
}
