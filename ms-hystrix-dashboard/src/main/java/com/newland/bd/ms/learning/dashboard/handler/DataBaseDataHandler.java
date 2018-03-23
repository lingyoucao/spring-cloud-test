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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lcs on 2018/3/21.
 *
 * @author lcs
 */
public class DataBaseDataHandler<T extends TurbineData> implements TurbineDataHandler<T>  {


    private final static Logger logger = LoggerFactory.getLogger(DataBaseDataHandler.class);


    protected final String name;
    private final PerformanceCriteria perfCriteria;

    public DataBaseDataHandler() {
        this.name = "DataBaseHandler_" + UUID.randomUUID().toString();
        this.perfCriteria = new DataBasePerformanceCriteria();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void handleData(Collection<T> stats) {
        writeToDb(stats);
    }

    @Override
    public void handleHostLost(Instance host) {

    }

    @Override
    public PerformanceCriteria getCriteria() {
        return this.perfCriteria;
    }


    public void writeToDb(Collection<? extends TurbineData> dataCollection) {
        for (TurbineData d : dataCollection) {
            DataFromMetricsAggregator data = (DataFromMetricsAggregator) d;

            if(data.getCircuitData() != null) {
                logger.info("CircuitData=" + JSON.toJSONString(data.getCircuitData()));
            }
            if(data.getThreadPoolsData() != null) {
                logger.info("ThreadPoolsData=" + JSON.toJSONString(data.getThreadPoolsData()));
            }
        }
    }


    private class DataBasePerformanceCriteria implements PerformanceCriteria {

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
