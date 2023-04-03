package com.bixuebihui.tablegen.generator;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PojoGeneratorTest {

    @Test
    void getExtendsClasses() throws IOException {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        String res = gen.generate("test", 0);

        System.out.println(res);
    }

    @Test
    void getAnnotationForClass() {
        PojoGenerator gen = new PojoGenerator();
        String res = gen.getAnnotationForClass();
        assertEquals("io.swagger.annotations.ApiModel;", res);
    }

    @Test
    void getTargetFileName() {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        String res = gen.getTargetFileName("test");

        assertThat( res, Matchers.endsWith("src/main/java/com/exapmle/mytest/pojo/Test.java"));
    }

    @Test
    void getClassName() {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        String res = gen.getClassName("test");

        assertThat(res, Matchers.endsWith("Test"));

    }


    @Test
    void mapLookup() throws IOException {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        Handlebars h = gen.getHandlebars();
        Map<String, Object> con = gen.getContextMap("test");

        Template temp = h.compileInline("{{typeDefaultValue 'Long'}}");

        String res = temp.apply(Context.newBuilder(con).build());
        assertEquals("0L", res);

        temp = h.compileInline("{{typeDefaultValue 'String'}}");

        res = temp.apply(Context.newBuilder(con).build());
        assertEquals("&quot;&quot;", res);


    }



    @Test
    void varAssign() throws IOException {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        Handlebars h = gen.getHandlebars();
        Map<String, Object> con = gen.getContextMap("test");

        Template temp = h.compileInline("{{#let 'type'}}Long{{/let}} {{typeDefaultValue type}}");

        String res = temp.apply(Context.newBuilder(con).build());
        assertEquals(" 0L", res);


    }
}
