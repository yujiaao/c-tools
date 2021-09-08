package jcwl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class FluxMergeTest {
    public static void main(String[] args) {
        Flux.just(1,2,4)
                .mergeWith(Flux.just(3))
                .collectList().map(
                        x -> x.stream().sorted().collect(Collectors.toList())
                )
                .flatMap(x -> {
                    System.out.println(x);
                    return Mono.just(x);
                })
                .subscribe();

//
//
//                Flux.merge(2,  Flux.just(1,2,4),Flux.just(3))
//                .flatMap(x -> {
//                    System.out.println(x);
//                    return Mono.just(x);
//                })
//                .subscribe();

        Flux.mergeComparing(1, Integer::compare, Flux.just(1,2,4),Flux.just(3))
                .flatMap(x -> {
                    System.out.println(x);
                    return Mono.just(x);
                })
                .subscribe();

    }
}
