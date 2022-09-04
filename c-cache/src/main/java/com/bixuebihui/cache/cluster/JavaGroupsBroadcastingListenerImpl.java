package com.bixuebihui.cache.cluster;

import com.opensymphony.oscache.base.events.CacheEntryEvent;
import com.opensymphony.oscache.plugins.clustersupport.ClusterNotification;
import com.opensymphony.oscache.plugins.clustersupport.JavaGroupsBroadcastingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xwx
 */
public class JavaGroupsBroadcastingListenerImpl extends
        JavaGroupsBroadcastingListener {
    static  Logger LOG = LoggerFactory.getLogger(JavaGroupsBroadcastingListenerImpl.class);

    @Override
    public void handleClusterNotification(ClusterNotification message) {

        switch (message.getType()) {
            case CacheConstants.CLUSTER_ENTRY_ADD:
                LOG.info("集群新增:" + message.getData());
                if (message.getData() instanceof QflagCacheEvent) {
                    QflagCacheEvent event = (QflagCacheEvent) message.getData();
                    cache.putInCache(event.getKey(), event.getEntry().getContent(),
                            null, null, CLUSTER_ORIGIN);
                }
                break;
            case CacheConstants.CLUSTER_ENTRY_UPDATE:
                LOG.info("集群更新:" + message.getData());
                if (message.getData() instanceof QflagCacheEvent) {
                    QflagCacheEvent event = (QflagCacheEvent) message.getData();
                    cache.putInCache(event.getKey(), event.getEntry().getContent(),
                            null, null, CLUSTER_ORIGIN);
                }
                break;
            case CacheConstants.CLUSTER_ENTRY_DELETE:
                LOG.info("集群删除:" + message.getData());
                if (message.getData() instanceof QflagCacheEvent) {
                    QflagCacheEvent event = (QflagCacheEvent) message.getData();
                    cache.removeEntry(event.getKey());
                }
                break;
            default:
                LOG.warn("unknown type of message:" + message);
        }

    }

    @Override
    public void cacheEntryAdded(CacheEntryEvent event) {
        super.cacheEntryAdded(event);
        if (!CLUSTER_ORIGIN.equals(event.getOrigin())) {
            sendNotification(new ClusterNotification(
                    CacheConstants.CLUSTER_ENTRY_ADD, new QflagCacheEvent(event
                    .getMap(), event.getEntry(), CLUSTER_ORIGIN)));
        }
    }


    @Override
    public void cacheEntryRemoved(CacheEntryEvent event) {

        super.cacheEntryRemoved(event);
        if (!CLUSTER_ORIGIN.equals(event.getOrigin())) {
            sendNotification(new ClusterNotification(
                    CacheConstants.CLUSTER_ENTRY_DELETE, new QflagCacheEvent(
                    event.getMap(), event.getEntry(), CLUSTER_ORIGIN)));
        }
    }

    @Override
    public void cacheEntryUpdated(CacheEntryEvent event) {

        super.cacheEntryUpdated(event);
        if (!CLUSTER_ORIGIN.equals(event.getOrigin())) {
            sendNotification(new ClusterNotification(
                    CacheConstants.CLUSTER_ENTRY_UPDATE, new QflagCacheEvent(
                    event.getMap(), event.getEntry(), CLUSTER_ORIGIN)));
        }
    }

}
