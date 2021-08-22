package r2dbc;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DatabaseClient.class})
@EnableAutoConfiguration
public class DdlTest {
    @Autowired
    DatabaseClient databaseClient;

/**
ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:mysql://root@unix?unixSocket=%2Fpath%2Fto%2Fmysql.sock")


 ConnectionFactory connectionFactory = ConnectionFactories.get(
 "r2dbcs:mysql://root:database-password-in-here@127.0.0.1:3306/r2dbc?" +
 "zeroDate=use_round&" +
 "sslMode=verify_identity&" +
 "useServerPrepareStatement=true&" +
 "tlsVersion=TLSv1.3%2CTLSv1.2%2CTLSv1.1&" +
 "sslCa=%2Fpath%2Fto%2Fmysql%2Fca.pem&" +
 "sslKey=%2Fpath%2Fto%2Fmysql%2Fclient-key.pem&" +
 "sslCert=%2Fpath%2Fto%2Fmysql%2Fclient-cert.pem&" +
 "sslKeyPassword=key-pem-password-in-here"
 )

 // Creating a Mono using Project Reactor
 Mono<Connection> connectionMono = Mono.from(connectionFactory.create());


 ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
 .option(DRIVER, "mysql")
 .option(HOST, "127.0.0.1")
 .option(USER, "root")
 .option(PORT, 3306)  // optional, default 3306
 .option(PASSWORD, "database-password-in-here") // optional, default null, null means has no password
 .option(DATABASE, "r2dbc") // optional, default null, null means not specifying the database
 .option(CONNECT_TIMEOUT, Duration.ofSeconds(3)) // optional, default null, null means no timeout
 .option(SSL, true) // optional, default sslMode is "preferred", it will be ignore if sslMode is set
 .option(Option.valueOf("sslMode"), "verify_identity") // optional, default "preferred"
 .option(Option.valueOf("sslCa"), "/path/to/mysql/ca.pem") // required when sslMode is verify_ca or verify_identity, default null, null means has no server CA cert
 .option(Option.valueOf("sslCert"), "/path/to/mysql/client-cert.pem") // optional, default null, null means has no client cert
 .option(Option.valueOf("sslKey"), "/path/to/mysql/client-key.pem") // optional, default null, null means has no client key
 .option(Option.valueOf("sslKeyPassword"), "key-pem-password-in-here") // optional, default null, null means has no password for client key (i.e. "sslKey")
 .option(Option.valueOf("tlsVersion"), "TLSv1.3,TLSv1.2,TLSv1.1") // optional, default is auto-selected by the server
 .option(Option.valueOf("sslHostnameVerifier"), "com.example.demo.MyVerifier") // optional, default is null, null means use standard verifier
 .option(Option.valueOf("sslContextBuilderCustomizer"), "com.example.demo.MyCustomizer") // optional, default is no-op customizer
 .option(Option.valueOf("zeroDate"), "use_null") // optional, default "use_null"
 .option(Option.valueOf("useServerPrepareStatement"), true) // optional, default false
 .option(Option.valueOf("tcpKeepAlive"), true) // optional, default false
 .option(Option.valueOf("tcpNoDelay"), true) // optional, default false
 .option(Option.valueOf("autodetectExtensions"), false) // optional, default false
 .build();
 ConnectionFactory connectionFactory = ConnectionFactories.get(options);

 // Creating a Mono using Project Reactor
 Mono<Connection> connectionMono = Mono.from(connectionFactory.create());

 // Minimum configuration for unix domain socket
 ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
 .option(DRIVER, "mysql")
 .option(Option.valueOf("unixSocket"), "/path/to/mysql.sock")
 .option(USER, "root")
 .build();
 ConnectionFactory connectionFactory = ConnectionFactories.get(options);

 Mono<Connection> connectionMono = Mono.from(connectionFactory.create());

*/

    @Autowired
    private ReactiveClientUserSortingRepository reactiveClientUserSortingRepository;


    @Test
    public void testDoDDL() {

        List<String> ddl = Collections.unmodifiableList(Arrays.asList("drop table if exists client_user;",
                "create table client_user(user_id varchar(64) not null primary key,nick_name varchar(32),phone_number varchar(16),gender tinyint default 0) charset = utf8mb4;"));
        ddl.forEach(sql -> databaseClient.sql(sql)
                .fetch()
                .rowsUpdated()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete());
    }

    @Test
    public void testCRUD() {
        ClientUser clientUser = new ClientUser();

        clientUser.setGender(2);
        clientUser.setNickName("r2dbc");
        clientUser.setPhoneNumber("9527");
        clientUser.setUserId("snowflake");
        Mono<Integer> res = reactiveClientUserSortingRepository.addClientUser(1L + "", "test", "123456", 1);
        res.log()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

//        Mono<ClientUser> save = reactiveClientUserSortingRepository.save(clientUser);
//
//        reactiveClientUserSortingRepository.save(clientUser)
//                .log()
//                .as(StepVerifier::create)
//                .expectNextCount(1)
//                .verifyComplete();

    }

}
