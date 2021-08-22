package jcwl;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class FluxDelayTest {
    public static void main(String[] args) throws InterruptedException {
       // Integer x =
                Flux.range(1, 10).delayElements(Duration.ofSeconds(1))
               // .blockFirst(Duration.ofSeconds(4));
               .subscribe(System.out::println);
      //  System.out.println(x);

        System.out.println("Below chain");
        Thread.sleep(4000);
    }
}
