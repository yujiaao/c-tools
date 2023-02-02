package com.bixuebihui.jmesa;

import com.bixuebihui.datasource.BitmechanicDataSource;
import com.bixuebihui.dbcon.DatabaseConfig;
import com.bixuebihui.jdbc.DbHelper;
import com.bixuebihui.jdbc.IBaseListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("我的第一个测试用例")
public class DataSourceH2 {
    DataSource ds;
    DbHelper db = new DbHelper();
    IBaseListService service = new BasicListService(db);

    public DataSource getDataSource() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAlias("h2");
        databaseConfig.setClassName("org.h2.Driver");
        databaseConfig.setDburl("jdbc:h2:mem:test;MODE=MySQL");
        databaseConfig.setUsername("sa");
        databaseConfig.setPassword("sa");
        BitmechanicDataSource ds = new BitmechanicDataSource();
        ds.setDatabaseConfig(databaseConfig);
        return ds;
    }

    @BeforeEach
    public void init() throws IOException, SQLException, URISyntaxException {
        ds = getDataSource();
        db.setDataSource(ds);

        service.getDbHelper().executeNoQuery(
                new String(Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:config/db/create_db.sql").getAbsolutePath()))));

    }

    @DisplayName("初始化数据检验，需要有三条记录")
    @Test
    public void testInit() throws SQLException {
        Object o = service.getDbHelper().executeScalar("select count(*) from t_main");
        assertEquals(3, Integer.parseInt(o.toString()));
    }
}
