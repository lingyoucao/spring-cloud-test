package com.newland.bd.ms.learning.dashboard.init;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
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

    private static final DynamicStringProperty clusterConfig = DynamicPropertyFactory.getInstance().getStringProperty("turbine.aggregator.cluster-config","default");


    /**
     * 指标采集实现思路
     *      1、按集群分类，MetricsAggregatorDataHandler接入到turbine，成为它的客户端，采集turbine原始的数据并汇聚。
     *      2、所有MetricsAggregatorDataHandler采集到的数据，通过MetricsMonitor监听将数据汇入到派发器TurbineDataDispatcher中。
     *      3、派发器TurbineDataDispatcher将数据输出到tuple中
     *      4、MetricsMonitor注册数据处理DataBaseDataHandler，即汇聚的客户端，接入到tuple中获取汇聚后的数据。
     * 为什么这么做：
     *      1、turbine的架构实现即是发布订阅模式，指标采集的实现基于turbine的架构实现扩展，扩展后形成多级订阅
     *      2、这么做的好处可以实现数据处理的解耦，MetricsAggregatorDataHandler负责采集turbine原始数据并处理，
     *         DataBaseDataHandler实现指标数据的入库。发布端不管有没客户端订阅，只管发布数据，直到队列满会丢弃数据，当队列的数据
     *         被消费了才会继续填充队列。客户端假设是数据库入库处理，可能处理比较慢，但它不会反过来影响发布端发布数据。
     *      3、未来如果想增加客户端，比如将数据写入到消息队列、缓存，可以很方便的实现扩展，只需再注册一个数据处理的客户端。
     */
    public static void start() {
        try {

            /*******注册指标采集*******/

            //指标监控工厂方法
            MetricsMonitorFactory metricsMonitorFactory = MetricsPluginsFactory.getMetricsMonitorFactory();
            if(metricsMonitorFactory == null) {
                MetricsPluginsFactory.setMetricsMonitorFactory(new DefaultMetricsMonitorFactory());
            }
            //初始化指标监控
            MetricsPluginsFactory.getMetricsMonitorFactory().initMetricsMonitor();
            MetricsMonitor metricsMonitor =  MetricsPluginsFactory.getMetricsMonitorFactory().getMetricsMonitor();
            //集群数据处理，进行指标汇总聚合。
            MetricsAggregatorDataHandler metricsAggregatorDataHandler = new MetricsAggregatorDataHandler(metricsMonitor);

            /*********注册Turbine客户端**********/

            //turbine集群监控工厂
            ClusterMonitorFactory<AggDataFromCluster> clusterMonFactory
                    = (ClusterMonitorFactory<AggDataFromCluster>) PluginsFactory.getClusterMonitorFactory();
            if (clusterMonFactory == null) {
                throw new RuntimeException("Must configure plugin for ClusterMonitorFactory");
            }
            //根据集群初始化turbine客户端，即注册指标汇聚处理
            String clusterNames = clusterConfig.getValue();
            for (String clusterName : clusterNames.split(",")) {
                ClusterMonitor<AggDataFromCluster> clusterMonitor = clusterMonFactory.getClusterMonitor(clusterName);
                if (clusterMonitor == null) {
                    continue;
                }
                clusterMonitor.registerListenertoClusterMonitor(metricsAggregatorDataHandler);
                clusterMonitor.startMonitor();
            }

        } catch(Exception e) {
            logger.info("Caught ex. Stopping metrics monitor", e);
        } catch(Throwable  t) {
            logger.info("Caught throwable. metrics monitor", t);
        } finally {

        }

    }
}
