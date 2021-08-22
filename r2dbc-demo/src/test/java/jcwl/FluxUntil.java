package jcwl;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static java.time.temporal.ChronoUnit.MILLIS;

public class FluxUntil {
    public static void main(String[] args) {
        Flux.range(1, 100).buffer(20).subscribe(System.out::println);
        Flux.interval(Duration.of(100, MILLIS),Duration.of(1001, MILLIS)).take(2).toStream().forEach(System.out::println);
//        Flux.range(1, 10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);
//        Flux.range(1, 10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);

    }
}
