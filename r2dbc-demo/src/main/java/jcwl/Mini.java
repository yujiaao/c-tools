package jcwl;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;

public class Mini {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = ConnectionFactories
                .get("r2dbc:h2:mem:///testdb");

        Mono.from(connectionFactory.create())
                .flatMapMany(connection -> connection
                        .createStatement("SELECT firstname FROM PERSON WHERE age > $1")
                        .bind("$1", 42)
                        .execute())
                .flatMap(result -> result
                        .map((row, rowMetadata) -> row.get("firstname", String.class)))
                .doOnNext(System.out::println)
                .subscribe();
    }
}
