package com.newland.bd.ms.learning.dashboard.handler;

import com.netflix.turbine.data.TurbineData;
import com.netflix.turbine.discovery.Instance;
import com.netflix.turbine.handler.PerformanceCriteria;
import com.netflix.turbine.handler.TurbineDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
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
        this.name = "DBHandler_" + UUID.randomUUID().toString();
        this.perfCriteria = new DBPerformanceCriteria();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void handleData(Collection<T> stats) {
        for (TurbineData data : stats) {
            logger.info(data.toString());
        }
        //数据库入库
    }

    @Override
    public void handleHostLost(Instance host) {

    }

    @Override
    public PerformanceCriteria getCriteria() {
        return this.perfCriteria;
    }




    private class DBPerformanceCriteria implements PerformanceCriteria {

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
