package jcwl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class FluxTest {
    public static void main(String[] args) {
        Flux<String> ids = ifhrIds();

        Flux<String> combinations =
                ids.flatMap(id -> {
                    Mono<String> nameTask = ifhrName(id);
                    Mono<Integer> statTask = ifhrStat(id);

                    return nameTask.zipWith(statTask,
                            (name, stat) -> "Name " + name + " has stats " + stat);
                });

        Mono<List<String>> result = combinations.collectList();

        List<String> results = result.block();
        assertThat(results).containsExactly(
                "Name 1 has stats 1", "Name 2 has stats 2", "Name 3 has stats 3"
        );



    }

    private static Mono<Integer> ifhrStat(String id) {
        return Mono.just(Integer.parseInt(id));
    }

    private static Mono<String> ifhrName(String id) {
        return Mono.just(id);
    }

    private static Flux<String> ifhrIds() {
        return Flux.just("1","2", "3");
    }
}
