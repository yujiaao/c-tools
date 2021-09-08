package jcwl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

public class FibonacciTest {
    public Flux<Long> generateFibonacciWithTuples() {
        return Flux.generate(
                () -> Tuples.of(0L, 1L),
                (state, sink) -> {
                    sink.next(state.getT1());
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                }
        );
    }

    public static void main(String[] args) {

        new FibonacciTest().generateFibonacciWithTuples()
                .flatMap(x ->{ System.out.println(x.longValue()+""); return Mono.just(x);})
                .log().take(50).subscribe();
    }
}
