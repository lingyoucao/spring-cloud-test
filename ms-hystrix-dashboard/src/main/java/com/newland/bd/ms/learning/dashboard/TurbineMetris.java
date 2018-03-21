package com.newland.bd.ms.learning.dashboard;

import com.netflix.turbine.data.AggDataFromCluster;
import com.netflix.turbine.monitor.cluster.ClusterMonitor;
import com.netflix.turbine.monitor.cluster.ClusterMonitorFactory;
import com.netflix.turbine.plugins.PluginsFactory;
import com.newland.bd.ms.learning.dashboard.handler.MetricsAggregatorDataHandler;
import com.newland.bd.ms.learning.dashboard.monitor.MetricsMonitor;
import com.newland.bd.ms.learning.dashboard.monitor.MetrisMonitorFactory;
import com.newland.bd.ms.learning.dashboard.plugins.DefaultMetricsMonitorFactory;
import com.newland.bd.ms.learning.dashboard.plugins.MetrisPluginsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lcs on 2018/3/20.
 *
 * @author lcs
 */
public class TurbineMetris {
    private final static Logger logger = LoggerFactory.getLogger(TurbineMetris.class);

    public static void start() {
        try {
            //指标监控工厂
            MetrisMonitorFactory metrisMonitorFactory = MetrisPluginsFactory.getMetrisMonitorFactory();
            if(metrisMonitorFactory == null) {
                MetrisPluginsFactory.setMetrisMonitorFactory(new DefaultMetricsMonitorFactory());
            }
            MetrisPluginsFactory.getMetrisMonitorFactory().initMetrisMonitor();
            MetricsMonitor metricsMonitor =  MetrisPluginsFactory.getMetrisMonitorFactory().getMetrisMonitor();


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
            logger.info("Caught ex. Stopping StreamingConnection", e);
        } catch(Throwable  t) {
            logger.info("Caught throwable. StreamingConnection", t);
        } finally {

        }

    }
}
