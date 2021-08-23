package jcwl;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class RepeatTest {

    @Test
    public void testRepeat(){
        MyClass myClass = new MyClass();
       Mono.defer(() -> myClass.myMethod());
//                .repeatWhen(repeat -> repeat.zipWith(Flux.range(1, 5), (e, idx) -> idx)
//                        .flatMap(time -> Mono.delay(Duration.ofSeconds(time)))
//                )
//                .blockLast();
    }
}
