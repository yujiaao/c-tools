package jcwl;

import reactor.core.publisher.Flux;

public class SampleSubTest {
    public static void main(String[] args) {
        SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(ss);

    }
}
