package jcwl;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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


    @Test
    public void map(){

        Mono.just("1").map(a -> a + "1").subscribe(System.out::println);
        Mono.just("1").flatMap(a -> Mono.just(a + "1")).subscribe(System.out::println);
        Flux.just("1").map(a -> a + "1").subscribe(System.out::println);
        Flux.just("1").flatMap(a -> Flux.just(a + "1")).subscribe(System.out::println);

    }
}
