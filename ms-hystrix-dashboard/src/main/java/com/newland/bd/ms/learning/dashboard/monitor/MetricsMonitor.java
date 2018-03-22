package com.newland.bd.ms.learning.dashboard.monitor;

import com.netflix.turbine.discovery.Instance;
import com.netflix.turbine.handler.TurbineDataDispatcher;
import com.netflix.turbine.handler.TurbineDataHandler;
import com.netflix.turbine.monitor.TurbineDataMonitor;
import com.newland.bd.ms.learning.dashboard.data.DataFromMetricsAggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lcs on 2018/3/21.
 *
 * @author lcs
 */
public class MetricsMonitor extends TurbineDataMonitor<DataFromMetricsAggregator> {
    private static final Logger logger = LoggerFactory.getLogger(MetricsMonitor.class);
    private TurbineDataDispatcher<DataFromMetricsAggregator> dispatcher = new TurbineDataDispatcher("ALL_METRICS_MONITOR_DISPATCHER");

    protected final Instance statsInstance;

    private String name;

    public MetricsMonitor(String name) {
        this.statsInstance = new Instance(name, "metrics_monitor", true);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void startMonitor() throws Exception {

    }

    @Override
    public void stopMonitor() {

    }

    @Override
    public TurbineDataDispatcher<DataFromMetricsAggregator> getDispatcher() {
        return dispatcher;
    }

    @Override
    public Instance getStatsInstance() {
        return statsInstance;
    }

    public void registerListenerToMetricsMonitor(TurbineDataHandler<DataFromMetricsAggregator> eventHandler) {

        TurbineDataHandler<DataFromMetricsAggregator> oldHandler = getDispatcher().findHandlerForHost(getStatsInstance(), eventHandler.getName());
        if (oldHandler == null) {
            logger.info("Registering event handler for cluster monitor: " + eventHandler.getName());
            getDispatcher().registerEventHandler(getStatsInstance(), eventHandler);
            logger.info("All event handlers for cluster monitor: " +getDispatcher().getAllHandlerNames().toString());
        } else {
            logger.info("Handler: " + oldHandler.getName() + " already registered to host: " + getStatsInstance());
        }
    }
}
