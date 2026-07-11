package com.bixuebihui;

import junit.framework.TestCase;

public class BaseModelTest extends TestCase {

    public void testConstructorsAndAccessors() {
        BaseModel<String> empty = new BaseModel<>();
        empty.setReturncode(0);
        empty.setMessage("ok");
        empty.setResult("data");
        assertEquals(Integer.valueOf(0), empty.getReturncode());
        assertEquals("ok", empty.getMessage());
        assertEquals("data", empty.getResult());

        BaseModel<String> withCode = new BaseModel<>(1);
        assertEquals(Integer.valueOf(1), withCode.getReturncode());

        BaseModel<String> withMessage = new BaseModel<>(2, "msg");
        assertEquals(Integer.valueOf(2), withMessage.getReturncode());
        assertEquals("msg", withMessage.getMessage());

        BaseModel<String> full = new BaseModel<>(3, "done", "payload");
        assertEquals(Integer.valueOf(3), full.getReturncode());
        assertEquals("done", full.getMessage());
        assertEquals("payload", full.getResult());
        assertTrue(full.toString().contains("payload"));
    }

    public void testBuilder() {
        BaseModel<Integer> model = BaseModel.<Integer>newBuilder()
                .returncode(200)
                .message("success")
                .result(42)
                .build();

        assertEquals(Integer.valueOf(200), model.getReturncode());
        assertEquals("success", model.getMessage());
        assertEquals(Integer.valueOf(42), model.getResult());
    }
}
