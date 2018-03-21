package com.newland.bd.ms.learning.dashboard.data;

import com.netflix.turbine.data.TurbineData;
import com.netflix.turbine.discovery.Instance;
import com.netflix.turbine.monitor.TurbineDataMonitor;
import com.newland.bd.ms.learning.dashboard.model.CircuitData;
import com.newland.bd.ms.learning.dashboard.model.ThreadPoolsData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lcs on 2018/3/21.
 *
 * @author lcs
 */
public class DataFromMetricsAggregator extends TurbineData{

    private Instance host;

    private ThreadPoolsData threadPoolsData;
    private CircuitData circuitData;
    public DataFromMetricsAggregator(TurbineDataMonitor<DataFromMetricsAggregator> monitor,
                                  String type,
                                  String name,
                                  long dateTime) {
        super(monitor, type, name);
        super.setCreationTime(dateTime);
    }


    @Override
    public HashMap<String, Long> getNumericAttributes() {
        return null;
    }

    @Override
    public HashMap<String, String> getStringAttributes() {
        return null;
    }

    @Override
    public HashMap<String, Map<String, ? extends Number>> getNestedMapAttributes() {
        return null;
    }


    public ThreadPoolsData getThreadPoolsData() {
        return threadPoolsData;
    }

    public void setThreadPoolsData(ThreadPoolsData threadPoolsData) {
        this.threadPoolsData = threadPoolsData;
    }

    public CircuitData getCircuitData() {
        return circuitData;
    }

    public void setCircuitData(CircuitData circuitData) {
        this.circuitData = circuitData;
    }
}
