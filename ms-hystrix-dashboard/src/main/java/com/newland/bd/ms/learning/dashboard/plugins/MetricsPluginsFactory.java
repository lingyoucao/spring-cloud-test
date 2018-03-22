package com.newland.bd.ms.learning.dashboard.plugins;

import com.newland.bd.ms.learning.dashboard.monitor.MetricsMonitorFactory;

/**
 * @author lcs
 */
public class MetricsPluginsFactory {

    private static MetricsMonitorFactory metricsMonitorFactory;

    public static MetricsMonitorFactory getMetricsMonitorFactory() {
        return metricsMonitorFactory;
    }

    public static void setMetricsMonitorFactory(MetricsMonitorFactory factory) {
        metricsMonitorFactory = factory;
    }

}
