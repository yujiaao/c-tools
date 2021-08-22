package jcwl;

import reactor.core.publisher.Mono;

import java.util.Optional;

public class MonoTest {
    public static void main(String[] args) {
        Mono.fromSupplier(() -> "Hello1").subscribe(System.out::println);
        Mono.justOrEmpty(Optional.of("Hello2")).subscribe(System.out::println);
        Mono.create(sink -> sink.success("Hello3")).subscribe(System.out::println);
    }
}
