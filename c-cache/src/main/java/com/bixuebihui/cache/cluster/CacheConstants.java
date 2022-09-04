package com.bixuebihui.cache.cluster;


/**
 * @author xwx
 */
public class CacheConstants {
    /**
     * 添加缓存对象操作
     */
    public final static int ACTION_ADD_OBJ = 1;
    /**
     * 更新缓存对象操作
     */
    public final static int ACTION_UPDATE_OBJ = 2;
    /**
     * 删除缓存对象操作
     */
    public final static int ACTION_DELETE_OBJ = 3;
    /**
     * 刷新缓存对象
     */
    public final static int ACTION_FLUSH_OBJ = 4;


    /**
     * 集群entry add处理
     */
    public final static int CLUSTER_ENTRY_ADD = 20;

    /**
     * 集群entry update处理
     */
    public final static int CLUSTER_ENTRY_UPDATE = 21;

    /**
     * 集群entry delete处理
     */
    public final static int CLUSTER_ENTRY_DELETE = 22;
}

