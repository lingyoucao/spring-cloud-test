package com.newland.bd.ms.learning.dashboard;

import com.netflix.turbine.data.AggDataFromCluster;
import com.netflix.turbine.monitor.cluster.ClusterMonitor;
import com.netflix.turbine.monitor.cluster.ClusterMonitorFactory;
import com.netflix.turbine.plugins.PluginsFactory;
import com.netflix.turbine.streaming.TurbineStreamingConnection;
import com.netflix.turbine.streaming.servlet.TurbineStreamServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lcs on 2018/3/20.
 *
 * @author lcs
 */
public class TurbineMetris {
    private final static Logger logger = LoggerFactory.getLogger(MetricsAggregatorDataHandler.class);

    public static void start() {
        //启动集群监听（集群监听读取队列并汇总数据）

        //TODO 集群名称先采用默认
        String clusterName = "default";

        ClusterMonitorFactory<AggDataFromCluster> clusterMonFactory
                = (ClusterMonitorFactory<AggDataFromCluster>) PluginsFactory.getClusterMonitorFactory();
        if (clusterMonFactory == null) {
            throw new RuntimeException("Must configure plugin for ClusterMonitorFactory");
        }

        ClusterMonitor<AggDataFromCluster> clusterMonitor = clusterMonFactory.getClusterMonitor(clusterName);
        if (clusterMonitor == null) {
            return;
        }

        MetricsAggregatorDataHandler<AggDataFromCluster> metricsAggregatorDataHandler =
                new MetricsAggregatorDataHandler<AggDataFromCluster>();
        try {
            clusterMonitor.registerListenertoClusterMonitor(metricsAggregatorDataHandler);
            clusterMonitor.startMonitor();
        } catch(Exception e) {
            logger.info("Caught ex. Stopping StreamingConnection", e);
        } catch(Throwable  t) {
            logger.info("Caught throwable. StreamingConnection", t);
        } finally {

        }

    }
}
