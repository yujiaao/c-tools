package jcwl;

import reactor.core.publisher.Flux;

public class 空 {

    public static void main(String[] args) {
        System.out.println("!");

        Flux.empty().switchIfEmpty(x -> System.out.println("you not see me"));
        System.out.println("jcwl.空");
    }
}
