package com.newland.bd.ms.learning.dashboard.plugins;

import com.newland.bd.ms.learning.dashboard.monitor.MetricsMonitorFactory;

/**
 * @author lcs
 */
public class MetricsPluginsFactory {

    private static MetricsMonitorFactory metrisMonitorFactory;

    public static MetricsMonitorFactory getMetrisMonitorFactory() {
        return metrisMonitorFactory;
    }

    public static void setMetrisMonitorFactory(MetricsMonitorFactory metrisMonitorFactory) {
        MetricsPluginsFactory.metrisMonitorFactory = metrisMonitorFactory;
    }

}
