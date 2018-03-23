package com.newland.bd.ms.learning.dashboard.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.netflix.turbine.data.TurbineData;
import com.netflix.turbine.discovery.Instance;
import com.netflix.turbine.handler.PerformanceCriteria;
import com.netflix.turbine.handler.TurbineDataHandler;
import com.newland.bd.ms.learning.dashboard.data.DataFromMetricsAggregator;
import com.newland.bd.ms.learning.dashboard.model.CircuitData;
import com.newland.bd.ms.learning.dashboard.model.ThreadPoolsData;
import com.newland.bd.ms.learning.dashboard.monitor.MetricsMonitor;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.util.MinimalPrettyPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by lcs on 2018/3/20.
 *
 * @author lcs
 */
public class MetricsAggregatorDataHandler<T extends TurbineData> implements TurbineDataHandler<T> {
    private final static Logger logger = LoggerFactory.getLogger(MetricsAggregatorDataHandler.class);

    private final PerformanceCriteria perfCriteria;
    protected final String name;
    private MetricsMonitor monitor;

    public MetricsAggregatorDataHandler(MetricsMonitor monitor) {
        this.perfCriteria = new MetricsAggregatorPerformanceCriteria();
        this.name = "MetricsAggregatorHandler_" + UUID.randomUUID().toString();
        this.monitor = monitor;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void handleData(Collection<T> data) {
        print(data);
    }

    private void writeToTuple(Collection<DataFromMetricsAggregator> dataCollection) {
        monitor.getDispatcher().pushData(monitor.getStatsInstance(), dataCollection);
    }

    protected void print(Collection<? extends TurbineData> dataCollection) {
        List<DataFromMetricsAggregator> list = new ArrayList<>();

        try {
            for (TurbineData data : dataCollection) {
                Map<String, Object> attrs = data.getAttributes();
                HashMap<String, Map<String, ? extends Number>> nestedAttrs = data.getNestedMapAttributes();
                if (nestedAttrs != null && nestedAttrs.keySet().size() > 0) {
                    for (String nestedMapKey : nestedAttrs.keySet()) {
                        Map<String, ? extends Number> nestedMap = nestedAttrs.get(nestedMapKey);
                        if (nestedMap != null) {
                            attrs.put(nestedMapKey, nestedMap);
                        }
                    }
                }
                if("meta".equals(data.getKey().getName()) && "meta".equals(data.getKey().getType())) {
                    continue;
                }
                String string = JSON.toJSONString(attrs);
                DataFromMetricsAggregator instanceData = new DataFromMetricsAggregator( data.getKey().getType(), data.getKey().getName());
                if("HystrixThreadPool".equals(data.getKey().getType())) {
                    ThreadPoolsData threadPoolsData = JSON.parseObject(string, new TypeReference<ThreadPoolsData>() {});
                    instanceData.setThreadPoolsData(threadPoolsData);
                } else if( "HystrixCommand".equals(data.getKey().getType())) {
                    CircuitData circuitData = JSON.parseObject(string, new TypeReference<CircuitData>() {});
                    instanceData.setCircuitData(circuitData);
                }
                list.add(instanceData);
            }
            if(!list.isEmpty()) {
                writeToTuple(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleHostLost(Instance host) {

    }

    @Override
    public PerformanceCriteria getCriteria() {
        return perfCriteria;
    }


    public MetricsMonitor getMetricsMonitor() {
        return this.monitor;
    }


    private class MetricsAggregatorPerformanceCriteria implements PerformanceCriteria {

        @Override
        public boolean isCritical() {
            return false;
        }

        @Override
        public int getMaxQueueSize() {
            return 10000;
        }

        @Override
        public int numThreads() {
            return 1;
        }
    }
}