package com.bixuebihui;

import junit.framework.TestCase;

public class SqlUtilsTest extends TestCase {

    /**
     * select count(id) gv_cnt, COUNT(user_id) gv_sm , concat(user_id) gv_key
     * from task  where task_id = ? group by  concat(user_id) gv_key having count(user_id)>1
     */
    public void testFormGroupQuery() {
        assertEquals("select count(id) gv_cnt, COUNT(user_id) gv_sm , concat(user_id)  gv_key" +
                        " from task where task_id = 1321 group by gv_key having count(user_id)>1",
                SqlUtils.formGroupQuery("task", "id", "user_id",
                        "COUNT", "where task_id = 1321", "having count(user_id)>1", new String[]{"user_id"}

                ));
    }
}
