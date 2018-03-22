package com.newland.bd.ms.learning.dashboard.plugins;

import com.newland.bd.ms.learning.dashboard.data.DataFromMetricsAggregator;
import com.newland.bd.ms.learning.dashboard.handler.DataBaseDataHandler;
import com.newland.bd.ms.learning.dashboard.monitor.MetricsMonitor;
import com.newland.bd.ms.learning.dashboard.monitor.MetricsMonitorFactory;

/**
 * Created by lcs on 2018/3/21.
 *
 * @author lcs
 */
public class DefaultMetricsMonitorFactory implements MetricsMonitorFactory {
    private static MetricsMonitor monitor;
    private DataBaseDataHandler<DataFromMetricsAggregator> dbDataHandler = new DataBaseDataHandler<>();
    @Override
    public MetricsMonitor getMetricsMonitor() {
        return monitor;
    }

    @Override
    public void initMetricsMonitor() {
        MetricsMonitor monitor = init();
        monitor.registerListenerToMetricsMonitor(dbDataHandler);
    }

    @Override
    public void shutdownMetricsMonitor() {
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
