package jcwl;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class SubscribeTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        Mono.just(1).flatMap(type ->{
            if(type==1){
                list.add("OK");
            }
            return Mono.empty();
        }).subscribe();

        System.out.println(list);
    }
}
