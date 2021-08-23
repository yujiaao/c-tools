package jcwl;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class BlockTest {
    public static void main(String[] args) {
        Flux.interval(Duration.ofSeconds(2)).doOnNext(System.out::println).blockLast();
        System.out.println("不再见");

    }
}
