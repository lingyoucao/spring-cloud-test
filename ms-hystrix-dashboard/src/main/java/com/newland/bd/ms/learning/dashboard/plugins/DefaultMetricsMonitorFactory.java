package com.newland.bd.ms.learning.dashboard.plugins;

import com.newland.bd.ms.learning.dashboard.data.DataFromMetricsAggregator;
import com.newland.bd.ms.learning.dashboard.handler.DataBaseDataHandler;
import com.newland.bd.ms.learning.dashboard.monitor.MetricsMonitor;
import com.newland.bd.ms.learning.dashboard.monitor.MetrisMonitorFactory;

/**
 * Created by lcs on 2018/3/21.
 *
 * @author lcs
 */
public class DefaultMetricsMonitorFactory implements MetrisMonitorFactory {
    private static MetricsMonitor monitor;
    private DataBaseDataHandler<DataFromMetricsAggregator> dbDataHandler = new DataBaseDataHandler<DataFromMetricsAggregator>();
    @Override
    public MetricsMonitor getMetrisMonitor() {
        return monitor;
    }

    @Override
    public void initMetrisMonitor() {
        MetricsMonitor monitor = init();
        monitor.registerListenertoClusterMonitor(dbDataHandler);
    }

    @Override
    public void shutdownMetrisMonitors() {
        monitor.stopMonitor();
        monitor.getDispatcher().stopDispatcher();
    }


    public static MetricsMonitor init() {
        if (monitor == null) {
            monitor = new MetricsMonitor("default_metrics");
        }
        return monitor;
    }
}
