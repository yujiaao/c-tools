package com.bixuebihui.jmesa;

import org.junit.jupiter.api.Test;

import javax.el.FunctionMapper;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElExpressionExCellEditorTest {

    public static int inc(int i){
        return i+1;
    }

    /**
     * el-expression example
     * https://javaee.github.io/tutorial/jsf-el007.html#BNAIM
     *
     * https://docs.oracle.com/cd/E19798-01/821-1841/gjddd/index.html
     */
    @Test
    public void testLambdaAndStream(){

        String temp = "${((x, y) -> x + y)(3, 5.5)} and ${[1,2,3,4].stream().sum()} ${fn:sin(3.14)}";

        Map<String, Double> map = new HashMap<>();
        Map<String, Object> row =  new HashMap<>();

        ElExpressionExCellEditor e = new ElExpressionExCellEditor("row", temp, map );
        Object res = e.getValue(row, null, 1);

        assertEquals("8.5 and 10 0.0015926529164868282", res);
    }

    @Test
    public void testPerformance(){
        for (int i=0;i<10; i++){
            testLambdaAndStream();
            testCustomFunctions();
        }
    }



    @Test
    public void testCustomFunctions(){

        Class[] functions={ElExpressionExCellEditorTest.class};

        String temp = "${fn:escapeXml(row.body)} = ${fn:inc(key)}";

        Map<String, Integer> map = new HashMap<>();
        map.put("key", 123);

        Map<String, Object> row =  new HashMap<>();
        row.put("body", "inc(123)");

        ElExpressionExCellEditor e = new ElExpressionExCellEditor("row", temp, map, functions);
        Object res = e.getValue(row, "body", 1);

        assertEquals("inc(123) = 124", res);
    }

    @Test
    public void testGetVariableResolverObject() {
        String temp = "'<span title=\\'#{fn:escapeXml(row.body)}\\'>#{fn:escapeXml(fn:abbreviate(row.body,15))} ${key}</span>";

        Map<String, Integer> map = new HashMap<>();
        map.put("key", 123);

        Map<String, Object> row =  new HashMap<>();
        row.put("body", "A simple text test is very small length but exceeds the boundary");

        ElExpressionExCellEditor e = new ElExpressionExCellEditor("row", temp, map);

        Object res = e.getValue(row, "body", 1);
        assertEquals("'<span title='A simple text test is very small length but exceeds the boundary'>A simple tex... 123</span>",res);

    }

    @Test
    public void testGetFunctionMapper() {
        String temp = "'<span title=\\'#{fn:escapeXml(row.body)}\\'>#{fn:escapeXml(fn:abbreviate(row.body,100))}</span>";

        Map<String, ?> map = new HashMap<>();

        ElExpressionExCellEditor e = new ElExpressionExCellEditor("row", temp, map);
        FunctionMapper f = e.getFunctionMapper();

        String name1 = "escapeXml";
        Method m = f.resolveFunction("fn", name1);
        assertEquals(name1, m.getName());

        String name2 = "abbreviate";
        Method m2 = f.resolveFunction("fn", name2);
        assertEquals(name2, m2.getName());

    }

}
