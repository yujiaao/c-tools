package com.bixuebihui.jmesa.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@RunWith(MockitoJUnitRunner.class)
public class EsTableTest {
    public static String host= "http://localhost:9200";
    public static String username = "elastic";
    public static String password = "changeme";


    @Test
    public void json() throws SQLException, JsonProcessingException {
        String tableName = "test";
        EsTable et = new EsTable(host,
                username, password,
                tableName, tableName);

        // et.request = Mockito.mock(EsRequest);

        int size=2;
        String res = et.json("{\"exportType\":\"json\", \"maxRows\":"+size+"}");
        ObjectMapper mapper = new ObjectMapper();
        Map map= mapper.readValue(res, Map.class);

        Object obj = map.get("data");
        assertEquals(size, ((List)obj).size());

        res = et.json("{\"exportType\":\"json\", \"maxRows\":1, \"page\":2}");
        Map map2= mapper.readValue(res, Map.class);

        Object obj2 = map2.get("data");
        assertEquals(1, ((List)obj2).size());
    }

    @Test
    public void jsonWithSort() throws SQLException, JsonProcessingException {
        String tableName = "test";
        EsTable et = new EsTable(host,
                username, password,
                tableName, tableName);

        int size=2;
        String res = et.json("{\"exportType\":\"json\", \"maxRows\":"+size+", \"sort\":{\"height\":\"desc\",\"age\":\"desc\"}}");
        ObjectMapper mapper = new ObjectMapper();
        Map map= mapper.readValue(res, Map.class);

        Object obj = map.get("data");
        assertEquals(size, ((List)obj).size());



    }

}
