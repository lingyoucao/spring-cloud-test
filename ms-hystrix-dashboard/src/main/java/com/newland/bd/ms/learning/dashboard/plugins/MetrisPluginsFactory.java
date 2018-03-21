package com.newland.bd.ms.learning.dashboard.plugins;

import com.newland.bd.ms.learning.dashboard.monitor.MetrisMonitorFactory;

/**
 * @author lcs
 */
public class MetrisPluginsFactory {

    private static MetrisMonitorFactory metrisMonitorFactory;

    public static MetrisMonitorFactory getMetrisMonitorFactory() {
        return metrisMonitorFactory;
    }

    public static void setMetrisMonitorFactory(MetrisMonitorFactory metrisMonitorFactory) {
        MetrisPluginsFactory.metrisMonitorFactory = metrisMonitorFactory;
    }

}
