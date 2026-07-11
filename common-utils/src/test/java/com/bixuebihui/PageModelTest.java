package com.bixuebihui;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class PageModelTest extends TestCase {

    public void testBuilderAndAccessors() {
        List<String> items = Arrays.asList("a", "b");
        PageModel<String> page = PageModel.builder(items)
                .total(100)
                .took(50)
                .build();

        assertEquals(100, page.getTotal());
        assertEquals(items, page.getItems());
        assertEquals(50, page.getTook());

        page.setTotal(200);
        page.setItems(Arrays.asList("c"));
        page.setTook(80);
        assertEquals(200, page.getTotal());
        assertEquals(1, page.getItems().size());
        assertEquals(80, page.getTook());
    }
}
