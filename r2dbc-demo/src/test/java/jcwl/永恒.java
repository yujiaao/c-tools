package jcwl;

import reactor.core.publisher.Flux;

public class 永恒 {
    public static void main(String[] args) {
        System.out.println("!");

        Flux.range(1, 1000)
                .timeout(Flux.never(), v -> Flux.never())
                .subscribe(System.out::println);

        System.out.println("jcwl.永恒");
    }
}
