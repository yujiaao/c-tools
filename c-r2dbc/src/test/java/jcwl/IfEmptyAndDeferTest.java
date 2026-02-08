package jcwl;

import org.junit.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

public class IfEmptyAndDeferTest {

    @Test
    public void testRepeat() throws Exception {
        Mono<Integer> res = Mono.just(1);
        Callable<Mono<Integer>> call = () -> {
            System.out.println("test");
            int a = 4;
            return Mono.just(a);
        };

        Callable<Mono<Integer>> call1 = () -> {
            System.out.println("test1");
            int a = 5;
            return Mono.just(a);
        };

        res.flatMap(x -> Mono.empty())
                .switchIfEmpty(Mono.just(2))
                .subscribe(System.out::println);

        res.flatMap(Mono::just)
                .switchIfEmpty(Mono.just(3))
                .subscribe(System.out::println);

        res.flatMap(Mono::just)
                .switchIfEmpty(call.call())
                .subscribe(System.out::println);

        // res.flatMap(Mono::just)
        //         .switchIfEmpty(Mono.defer(() -> {
        //             try {
        //                 return call1.call();
        //             } catch (Exception e) {
        //                 e.printStackTrace();
        //             }
        //             return Mono.just(-1);
        //         }))
        //         .subscribe(System.out::println);
    }

}
