package com.bixuebihui;


import org.apache.commons.lang3.StringUtils;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author xwx
 */
public class SqlUtils {


    public static String formGroupQuery(String tableName, String keyName, String field, String groupFunction, String where, String having, String[] groupBy) {
        assert field != null;

        String keyFieldName = StringUtils.isEmpty(keyName) ? field : keyName;
        final boolean noGroup = (groupBy == null || groupBy.length == 0);

        String groupKeys = noGroup ? "" : " concat(" + StringUtils.join(groupBy, ",") + ") ";

        String groupByClause = noGroup ? "" : "group by gv_key";

        return "select count(" + keyFieldName + ") gv_cnt, " + groupFunction + "(" + field + ") gv_sm " + (
                !groupKeys.isEmpty() ? "," + groupKeys + " gv_key" : "")
                + " from " + tableName + " " + where + " " + groupByClause + " " + (having == null ? "" : having);
    }


    /**
     * 批量更新表
     * <p>
     * 按主键分批次执行
     *
     * @param maxId          表的最大主键ID
     *                       如果为0则直接返回
     * @param updateFunction 更新函数，接收当前批次的起始和结束ID，返回更新的记录数
     * @param logger         日志记录器
     * @return
     */
    public static int batchUpdateByPrimaryKey(long maxId, BiFunction<Long, Long, Integer> updateFunction,
                                              Consumer<String> logger) {

        if (maxId == 0) {
            return 0;
        }

        logger.accept("更新范围: ID 1 到 " + maxId);

        final int BATCH_SIZE = 5000;
        long currentId = 1;
        int totalUpdated = 0;
        int batchCount = 0;

        while (currentId <= maxId) {
            batchCount++;
            long batchEnd = Math.min(currentId + BATCH_SIZE - 1, maxId);

            logger.accept("处理批次 " + batchCount + ": ID " + currentId + " 到 " + batchEnd);

            try {
                // 查询当前批次需要更新的记录
                int records = updateFunction.
                        apply(currentId, batchEnd);
                totalUpdated += records;

                logger.accept("批次 " + batchCount + " 更新成功，影响行数: " + records);

            } catch (Exception e) {
                logger.accept("处理批次 " + batchCount + " 时发生异常: " + e.getMessage());
                break;
            }

            // 更新当前ID到下一批次
            currentId = batchEnd + 1;

            // 短暂暂停，避免对数据库造成过大压力
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.accept("线程休眠被中断" + e.getMessage());
                break;
            }
        }

        logger.accept("批量更新完成！总批次数: " + batchCount + ", 总更新记录数: " + totalUpdated);

        return totalUpdated;
    }

}
