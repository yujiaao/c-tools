package jcwl;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class FluxWindowTest {
    public static void main(String[] args) {
        Flux.range(1, 100).window(20).subscribe(x -> {
            System.out.println(x);
            x.subscribe(System.out::println);
        });
        Flux.interval(Duration.of(100, ChronoUnit.MILLIS))
                .window(Duration.of(1001, ChronoUnit.MILLIS))
                .take(2).toStream().forEach(System.out::println);

    }
}
