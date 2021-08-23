package jcwl;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class MonoToFluxTest {
    private <T> Flux<T> monoTofluxUsingFlatMapMany(Mono<List<T>> monoList) {
        return monoList
                .flatMapMany(Flux::fromIterable)
                .log();
    }

    private Mono<List<String>> monoOfList() {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        list.add("four");

        return Mono.just(list);
    }


    @Test
    public void testMonoToFlux(){
        Flux<String> f = monoTofluxUsingFlatMapMany(monoOfList());
        f.subscribe(System.out::println);

    }


}
