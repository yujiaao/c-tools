package jcwl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.MILLIS;

public class TakeOneTest {
    public static void main(String[] args) {
        Flux.just(5, 10)
                .take(1, true)
                .doOnNext(System.out::println)
                .log()
                .flatMap(x -> Flux.interval(Duration.of(x * 10,MILLIS),
                        Duration.of(100, MILLIS)).take(x))

                .log()
                .subscribe();

    }

}
