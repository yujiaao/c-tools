package jcwl;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TupleTest {

    public static void main(String[] args) {
        Consumer<String> consumer = System.out::println;

//Now in Java 8 style for brievety
        Function<Integer, String> transformation = integer -> "" + integer;

        Supplier<Integer> supplier = () -> 123;


        Consumer<Tuple2<Consumer<String>, String>> biConsumer = tuple -> {
            for(int i = 0; i < 10; i++){
                //Correct typing, compiler happy
                tuple.getT1().accept(tuple.getT2());
            }
        };

        biConsumer.accept(
                Tuples.of(
                        consumer,
                        transformation.apply(supplier.get())
                )
        );
    }
}
