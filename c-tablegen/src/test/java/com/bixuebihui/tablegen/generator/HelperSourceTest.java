package com.bixuebihui.tablegen.generator;

import com.bixuebihui.tablegen.ProjectConfig;
import com.bixuebihui.tablegen.entry.TableSetInfo;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.AbstractTemplateLoader;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HelperSourceTest {

    protected static Handlebars getHandlebars() {
        TemplateLoader loader = new AbstractTemplateLoader(){
            @Override
            public TemplateSource sourceAt(String location) throws IOException {
                return null;
            }
        };
        loader.setPrefix("/templates/");
        loader.setSuffix(".hbs");

        Handlebars handlebars = new Handlebars(loader);

        handlebars.registerHelpers(ConditionalHelpers.class);
        handlebars.registerHelpers(StringHelpers.class);

        ProjectConfig config = new ProjectConfig();
        TableSetInfo setInfo = new TableSetInfo();

        handlebars.registerHelpers(new HelperSource(config, setInfo));

        return handlebars;
    }

    @Test
    void firstUp() throws IOException {
        assertEquals("Name", HelperSource.firstUp("name"));
        String name = getHandlebars().compileInline("{{firstUp name}}").apply(Map.of("name","name"));
        assertEquals("Name", name);
    }

    @Test
    void firstLow() throws IOException {
        assertEquals("name", HelperSource.firstLow("Name"));
        String name = getHandlebars().compileInline("{{firstLow Name}}").apply(Map.of("Name","Name"));
        assertEquals("name", name);
    }

    @Test
    void getPojoClassName() throws IOException {

        ProjectConfig config = new ProjectConfig();
        TableSetInfo info = new TableSetInfo();

        HelperSource helperSource = new HelperSource(config, info);
        assertEquals("NameOfTable", helperSource.getPojoClassName("nameOfTable"));
        String name = getHandlebars().compileInline("{{getPojoClassName nameOfTable}}").apply(Map.of("nameOfTable","nameOfTable"));
        assertEquals("NameOfTable", name);

    }

    @Test
    void constantName() throws IOException {
        assertEquals("NAME_OF_TABLE", HelperSource.constantName("name_of_table"));
        String name = getHandlebars().compileInline("{{constantName name_of_table}}").apply(Map.of("name_of_table","name_of_table"));
        assertEquals("NAME_OF_TABLE", name);
    }

    @Test
    void join() throws IOException {
        assertEquals("a,b,c", HelperSource.join(new String[]{"a", "b", "c"}, ","));
        String name = getHandlebars().compileInline("{{join items ','}}").apply(Map.of("items",new String[]{"a", "b", "c"}));
        assertEquals("a,b,c", name);
    }

    @Test
    void typeDefaultValue() throws IOException {
        assertEquals("0", HelperSource.typeDefaultValue("int"));
        String name = getHandlebars().compileInline("{{typeDefaultValue int}}").apply(Map.of("int","int"));
        assertEquals("0", name);
    }


    @Test
    void unboxType() throws IOException {
        assertEquals("(int)", HelperSource.unboxType("Integer"));
        String name = getHandlebars().compileInline("{{unboxType Integer}}").apply(Map.of("Integer","Integer"));
        assertEquals("(int)", name);
    }

    @Test
    void let() {

    }

    @Test
    void isKey() {
    }

    @Test
    void isRangeType() {
    }

    @Test
    void contains() throws IOException {
        assertTrue(HelperSource.contains(List.of("a", "b", "c"), "b"));
        assertFalse(HelperSource.contains("a,b,c".split(","), "d"));
        String name = getHandlebars().compileInline("{{contains items 'b'}}").apply(Map.of("items",new String[]{"a", "b", "c"}));
        assertTrue(Boolean.parseBoolean(name));

    }
}
