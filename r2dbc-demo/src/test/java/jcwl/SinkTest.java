package jcwl;

import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Random;

public class SinkTest {
    public static void main(String[] args) {
        Flux.generate(sink -> {
            sink.next("Hello");
            sink.complete();
        }).subscribe(System.out::println);


        final Random random = new Random();
        Flux.generate(ArrayList::new, (list, sink) -> {
            int value = random.nextInt(100);
            list.add(value);
            sink.next(value);
            if (list.size() == 10) {
                sink.complete();
            }
            return list;
        }).subscribe(System.out::println);

    }
}
