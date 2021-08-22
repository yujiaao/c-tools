package jcwl;

import reactor.core.publisher.Flux;

public class CheckPointTest {
    public static void main(String[] args) {
        Flux.just(1, 0).checkpoint("test0").map(x -> 1 / x).checkpoint("test1").subscribe(System.out::println);

    }
}
