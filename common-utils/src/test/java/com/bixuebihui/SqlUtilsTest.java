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

    public void testFormGroupQueryWithoutGroupBy() {
        assertEquals("select count(amount) gv_cnt, SUM(amount) gv_sm  from orders where status = 1  ",
                SqlUtils.formGroupQuery("orders", "", "amount", "SUM", "where status = 1", null, null));
    }

    public void testBatchUpdateByPrimaryKey() {
        assertEquals(0, SqlUtils.batchUpdateByPrimaryKey(0, (start, end) -> 0, msg -> {
        }));

        final int[] updated = {0};
        int total = SqlUtils.batchUpdateByPrimaryKey(3, (start, end) -> {
            updated[0] += (int) (end - start + 1);
            return (int) (end - start + 1);
        }, msg -> {
        });

        assertEquals(3, total);
        assertEquals(3, updated[0]);
    }

    public void testBatchUpdateByPrimaryKeyStopsOnException() {
        int total = SqlUtils.batchUpdateByPrimaryKey(10_000, (start, end) -> {
            throw new RuntimeException("boom");
        }, msg -> {
        });

        assertEquals(0, total);
    }
}
