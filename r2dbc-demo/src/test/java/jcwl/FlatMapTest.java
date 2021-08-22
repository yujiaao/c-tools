package jcwl;

import reactor.core.publisher.Flux;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.MILLIS;

public class FlatMapTest {
    public static void main(String[] args) {
        Flux.just(5, 10)
                .flatMap(x -> Flux.interval(Duration.of(x * 10,MILLIS),
                        Duration.of(100, MILLIS)).take(x))
                .toStream()
                .forEach(System.out::println);

    }
}
