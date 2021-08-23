package jcwl;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class MergeTest {
    public static void main(String[] args) {
        Flux.merge(Flux.interval(Duration.of(0, ChronoUnit.MILLIS),
                                        Duration.of(100, ChronoUnit.MILLIS))
                                .take(5),
                        Flux.interval(Duration.of(50, ChronoUnit.MILLIS),
                                        Duration.of(100, ChronoUnit.MILLIS))
                                .take(5))
                .toStream()
                .forEach(System.out::println);
        Flux.mergeSequential(Flux.interval(Duration.of(0, ChronoUnit.MILLIS),
                                Duration.of(100, ChronoUnit.MILLIS)).take(5),
                        Flux.interval(Duration.of(50, ChronoUnit.MILLIS),
                                Duration.of(100, ChronoUnit.MILLIS)).take(5))
                .toStream()
                .forEach(System.out::println);

    }
}
