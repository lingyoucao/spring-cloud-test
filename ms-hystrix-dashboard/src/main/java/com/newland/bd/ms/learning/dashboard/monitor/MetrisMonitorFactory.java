package com.newland.bd.ms.learning.dashboard.monitor;

/**
 * @author lcs
 */
public interface MetrisMonitorFactory {

    /**
     * @return {@link MetricsMonitor}<T>
     */
    MetricsMonitor getMetrisMonitor();

    /**
     * Init all the necessary cluster monitors
     */
    void initMetrisMonitor();

    /**
     * shutdown all the necessary cluster monitors
     */
    void shutdownMetrisMonitors();
}