package jcwl;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FluxToMonoTest {


    @Test
    public void testMonoToFlux(){
        Flux<Integer> flux = Flux.just(1, 2, 3, 4);
        flux.collect(Collectors.reducing((i1, i2) -> i1))
                .map(op -> op.get()).subscribe(System.out::println);
    }


}
