package jcwl;

import reactor.core.publisher.Flux;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.MILLIS;

public class HotFluxTest {
    public static void main(String[] args) throws InterruptedException {
        final Flux<Long> source = Flux.interval(Duration.of(1000,MILLIS))
                .take(10)
                .publish()
                .autoConnect();
        source.subscribe();
        Thread.sleep(5000);
        source
                .toStream()
                .forEach(System.out::println);

    }
}
