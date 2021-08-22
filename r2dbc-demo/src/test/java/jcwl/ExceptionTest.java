package jcwl;

import reactor.core.publisher.Mono;

public class ExceptionTest {
    public static void main(String[] args) {
        System.out.println("!");
        Mono.defer(()->
                Mono.error(new RuntimeException("test"))
        ).subscribe(System.out::println
        );
        System.out.println("jcwl.空");
    }
}
