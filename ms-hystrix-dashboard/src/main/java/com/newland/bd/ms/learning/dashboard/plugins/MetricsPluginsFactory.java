package com.newland.bd.ms.learning.dashboard.plugins;

import com.newland.bd.ms.learning.dashboard.monitor.MetricsMonitorFactory;

/**
 * Created by lcs on 2018/3/21.
 *
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
