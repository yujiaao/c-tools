package com.bixuebihui.test.controller;

import com.bixuebihui.test.business.TestGenManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        return manager.getFirst().doOnNext(x -> LOG.info(x.toXml())).map( x -> "first = " + StringEscapeUtils.escapeHtml4(x.toXml()));
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

    @GetMapping("/all")
    public Mono<String> repeatRecords(){
        AtomicInteger count = new AtomicInteger();
        return Flux.range(0, 200).flatMap((it) -> {
            System.out.println("step:" + it);
            return manager.ar().findAll().flatMap(x -> Mono.just(x.size()));
        }).flatMap(x -> {
            count.accumulateAndGet(x, (left, right) -> left+right);
            return Mono.empty();
        }).then(
                Mono.just("records:="+count.get())
        );

    }



}
