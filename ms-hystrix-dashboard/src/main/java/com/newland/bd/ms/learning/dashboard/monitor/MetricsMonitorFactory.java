package com.newland.bd.ms.learning.dashboard.monitor;

/**
 * @author lcs
 */
public interface MetricsMonitorFactory {

    /**
     * 获取指标监控
     * @return {@link MetricsMonitor}<T>
     */
    MetricsMonitor getMetricsMonitor();

    /**
     * Init all the necessary cluster monitors
     */
    void initMetricsMonitor();

    /**
     * shutdown all the necessary cluster monitors
     */
    void shutdownMetricsMonitor();
}