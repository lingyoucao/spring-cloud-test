package com.newland.bd.ms.learning.dashboard.init;

import com.netflix.turbine.data.AggDataFromCluster;
import com.netflix.turbine.monitor.cluster.ClusterMonitor;
import com.netflix.turbine.monitor.cluster.ClusterMonitorFactory;
import com.netflix.turbine.plugins.PluginsFactory;
import com.newland.bd.ms.learning.dashboard.handler.MetricsAggregatorDataHandler;
import com.newland.bd.ms.learning.dashboard.monitor.MetricsMonitor;
import com.newland.bd.ms.learning.dashboard.monitor.MetricsMonitorFactory;
import com.newland.bd.ms.learning.dashboard.plugins.DefaultMetricsMonitorFactory;
import com.newland.bd.ms.learning.dashboard.plugins.MetricsPluginsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lcs on 2018/3/20.
 *
 * @author lcs
 */
public class MetricsInit {
    private final static Logger logger = LoggerFactory.getLogger(MetricsInit.class);

    public static void start() {
        try {
            //指标监控工厂
            MetricsMonitorFactory metricsMonitorFactory = MetricsPluginsFactory.getMetricsMonitorFactory();
            if(metricsMonitorFactory == null) {
                MetricsPluginsFactory.setMetricsMonitorFactory(new DefaultMetricsMonitorFactory());
            }
            MetricsPluginsFactory.getMetricsMonitorFactory().initMetricsMonitor();
            MetricsMonitor metricsMonitor =  MetricsPluginsFactory.getMetricsMonitorFactory().getMetricsMonitor();


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
                    new MetricsAggregatorDataHandler(metricsMonitor);
            clusterMonitor.registerListenertoClusterMonitor(metricsAggregatorDataHandler);
            clusterMonitor.startMonitor();
        } catch(Exception e) {
            logger.info("Caught ex. Stopping metrics monitor", e);
        } catch(Throwable  t) {
            logger.info("Caught throwable. metrics monitor", t);
        } finally {

        }

    }
}
