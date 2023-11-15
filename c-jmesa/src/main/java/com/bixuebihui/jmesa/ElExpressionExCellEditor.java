package com.bixuebihui.jmesa;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jmesa.view.editor.expression.ElExpressionCellEditor;
import org.jmesa.view.editor.expression.Expression;

import javax.el.FunctionMapper;
import javax.el.VariableMapper;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author xwx
 */
public class ElExpressionExCellEditor extends ElExpressionCellEditor {

    public static final String FUNCTION_PREFIX = "fn";

    protected Map<String, ?> additionalContext;

    /**
     * Classes, that need to expose methods to el-expression, see the example below:
     *         Class[] functions={Math.class};
     *
     *         String temp = "${fn:escapeXml(row.body)} = ${fn:sin(key)}";
     *
     *         Map<String, Double> map = new HashMap<>();
     *         map.put("key", Math.PI/4);
     *
     *         Map<String, Object> row =  new HashMap<>();
     *         row.put("body", "sin(PI/4)");
     *
     *         ElExpressionExCellEditor e = new ElExpressionExCellEditor("row", temp, map, functions);
     *         Object res = e.getValue(row, "body", 1);
     *         assertEquals("sin(PI/4) = 0.7071067811865475", res);
     */

    private FunctionMapper functionMapper;

    public ElExpressionExCellEditor(Expression expression, Map<String, ?> context) {
        this(expression.getVar(), expression.getTemplate(), context, null);
    }

    public ElExpressionExCellEditor(String var, Object template, Map<String, ?> context) {
        this(var, template, context, null);
    }

    public ElExpressionExCellEditor(String var, Object template, Map<String, ?> context, Class[] functions) {
        super(var, template);
        this.additionalContext = context;
        this.functionMapper = new StringFunctionMapper(functions);
    }


    /**
     * Creates a VariableResolver based on the current row bean and variable name.
     *
     * @param item The row's backing bean.
     */
    @Override
    protected VariableMapper getVariableMapper(Object item) {
        additionalContext.forEach((k, v) ->
            context.getELResolver().setValue(context, null, k, v)
        );

        return super.getVariableMapper(item);
    }

    @Override
    protected FunctionMapper getFunctionMapper() {
        return functionMapper;
    }


    protected static class StringFunctionMapper extends FunctionMapper {
        Map<String, Method> map = new Hashtable<>();

        private Class[] functions;

        protected StringFunctionMapper(Class[] functions){
            this.functions = functions;
        }


        /**
         * 避免命名冲突，三个参数以上的方法加上参数个数后缀
         */
        @Override
        public Method resolveFunction(String prefix, String localName) {
            if (FUNCTION_PREFIX.equals(prefix)) {
                if (map.size() == 0) {

                    // put all static method of StringUtils and StringEscapeUtils classes
                    if (functions != null) {
                        for (Class f : functions) {
                            for (Method m : f.getMethods()) {
                                Class<?>[] types = m.getParameterTypes();
                                String name = changeName(m, types);
                                map.put(name, m);
                            }
                        }
                    }

                    for (Method m : Math.class.getMethods()) {
                        Class<?>[] types = m.getParameterTypes();
                        String name = changeName(m, types);
                        map.put(name, m);
                    }

                    for (Method m : StringUtils.class.getMethods()) {
                        Class<?>[] types = m.getParameterTypes();
                        String name = changeName(m, types);
                        map.put(name, m);
                    }


                    for (Method m : StringEscapeUtils.class.getMethods()) {
                        Class<?>[] types = m.getParameterTypes();

                        if (types.length > 0) {
                            if (types[0] == java.io.Writer.class) {
                                //忽略参数带Writer方法
                                continue;
                            }
                        }

                        String name = changeName(m, types);

                        map.put(name, m);
                    }
                }
                return map.get(localName);

            }
            return null;
        }

        private String changeName(Method m, Class<?>[] types) {
            String name = m.getName();
            if (types != null && types.length > 2) {
                name += types.length;
            }
            return name;
        }

    };


}
