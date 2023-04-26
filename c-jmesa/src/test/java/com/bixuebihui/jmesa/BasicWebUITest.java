package com.bixuebihui.jmesa;

import com.bixuebihui.BeanFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BasicWebUITest {

    String id = "view_businesses_per_category";

    BasicWebUI ui = (BasicWebUI) BeanFactory.createObjectById(id);
    //IBaseListService service;

    @BeforeEach
    public void setUp() {

        //ui.setService(service);
        ui.setId(id);
        String[] params = {"10142"};
        ((BasicListService) ui.getListService()).setSqlParams(params);
    }


    @Test
    public void testRender() throws Exception {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        String res = ui.render(request, response);
        System.out.println(res);
        assertTrue(res.indexOf(id) > 0);

    }


    @Test
    public void testGetColsTemplate() throws Exception {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        String res = ui.render(request, response);
        System.out.println(res);
        assertTrue(res.indexOf(id) > 0);

    }

}
