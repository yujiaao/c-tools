package jcwl;

import com.bixuebihui.r2dbc.sql.DbHelper;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class R2dbcApp {

  private static final Logger log = LoggerFactory.getLogger(R2dbcApp.class);

  public static void main(String[] args) {

    ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///test?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

    R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);

    template.getDatabaseClient().sql("CREATE TABLE person" +
        "(id VARCHAR(255) PRIMARY KEY," +
        "name VARCHAR(255)," +
        "age INT)")
      .fetch()
      .rowsUpdated()
            .log()
      .as(StepVerifier::create)
      .expectNextCount(1)
      .verifyComplete();

    DbHelper dbHelper = new DbHelper();
    dbHelper.setDataSource(connectionFactory);
    Integer res = dbHelper.executeNoQuery("insert into person (id, name, age) values(?,?,?)", new Object[]{"joe", "Joe", 34}).block();
    System.out.println(res);

    dbHelperTest(connectionFactory);
  }

  private static void dbHelperTest(ConnectionFactory connectionFactory) {
    DbHelper dbHelper = new DbHelper();
    dbHelper.setDataSource(connectionFactory);
    Mono<List<Map<String, Object>>> res = dbHelper.executeQuery("select * from person where id=?", new Object[]{"joe"});
    List<Map<String, Object>> list = res.block();
    assertEquals("joe", list.get(0).get("id".toUpperCase(Locale.ROOT)));
  }
}
