package jcwl;

import reactor.core.publisher.Flux;

public class DisposalTest {
    public static void main(String[] args) {
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error " + error),
                () -> System.out.println("Done"),
                sub -> sub.request(10)).dispose();
    }
}
