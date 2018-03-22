package com.newland.bd.ms.learning.dashboard.monitor;

/**
 * Created by lcs on 2018/3/21.
 *
 * @author lcs
 */
public interface MetricsMonitorFactory {

    /**
     * get metrics monitors
     * @return {@link MetricsMonitor}<T>
     */
    MetricsMonitor getMetricsMonitor();

    /**
     * Init all the necessary metrics monitors
     */
    void initMetricsMonitor();

    /**
     * shutdown all the necessary metrics monitors
     */
    void shutdownMetricsMonitor();
}