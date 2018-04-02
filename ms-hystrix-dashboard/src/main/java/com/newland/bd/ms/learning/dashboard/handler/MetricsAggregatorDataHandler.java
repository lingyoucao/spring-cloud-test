package com.newland.bd.ms.learning.dashboard.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.netflix.turbine.data.AggDataFromCluster;
import com.netflix.turbine.data.TurbineData;
import com.netflix.turbine.discovery.Instance;
import com.netflix.turbine.handler.PerformanceCriteria;
import com.netflix.turbine.handler.TurbineDataHandler;
import com.newland.bd.ms.learning.dashboard.data.DataFromMetricsAggregator;
import com.newland.bd.ms.learning.dashboard.model.CircuitData;
import com.newland.bd.ms.learning.dashboard.model.ThreadPoolsData;
import com.newland.bd.ms.learning.dashboard.monitor.MetricsMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lcs on 2018/3/20.
 *
 * @author lcs
 */
public class MetricsAggregatorDataHandler implements TurbineDataHandler<AggDataFromCluster> {
    private final static Logger logger = LoggerFactory.getLogger(MetricsAggregatorDataHandler.class);

    private final PerformanceCriteria perfCriteria;
    protected final String name;
    private MetricsMonitor monitor;

    private final ConcurrentHashMap<DataFromMetricsAggregator.DataKey, String> dataHash = new ConcurrentHashMap<DataFromMetricsAggregator.DataKey, String>();

    private static final Object CurrentTime = "currentTime";
    private static final String GROUP = "group";
    private static final String THREAD_POOL = "threadPool";


    private final AtomicLong lastFlushTime = new AtomicLong(0L);


    private final ConcurrentHashMap<DataFromMetricsAggregator.DataKey, DataFromMetricsAggregator> aggregateData = new ConcurrentHashMap<DataFromMetricsAggregator.DataKey, DataFromMetricsAggregator>();

    private ConcurrentHashMap<DataFromMetricsAggregator.DataKey, AtomicLong> timeHolder = new ConcurrentHashMap<DataFromMetricsAggregator.DataKey, AtomicLong>();


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
    public void handleData(Collection<AggDataFromCluster> data) {
        print(data);
    }

    private void writeToTuple(Collection<DataFromMetricsAggregator> dataCollection) {
        monitor.getDispatcher().pushData(monitor.getStatsInstance(), dataCollection);
    }

    protected void print(Collection<AggDataFromCluster> dataCollection) {
        List<DataFromMetricsAggregator> list = new ArrayList<>();

        try {
            for (AggDataFromCluster data : dataCollection) {
                //跳过meta数据
                if ("meta".equals(data.getKey().getName()) && "meta".equals(data.getKey().getType())) {
                    continue;
                }

                Map<String, Object> attrs = data.getAttributes();
                Long currentTime = Long.parseLong(attrs.get(CurrentTime).toString());

                Map<String, Long> sourceNumericAttributes = data.getNumericAttributes();
                //移除时间
                attrs.remove(CurrentTime);
                HashMap<String, Map<String, ? extends Number>> nestedAttrs = data.getNestedMapAttributes();
                if (nestedAttrs != null && nestedAttrs.keySet().size() > 0) {
                    for (String nestedMapKey : nestedAttrs.keySet()) {
                        Map<String, ? extends Number> nestedMap = nestedAttrs.get(nestedMapKey);
                        if (nestedMap != null) {
                            attrs.put(nestedMapKey, nestedMap);
                        }
                    }
                }

                //转为字符串
                String string = JSON.toJSONString(attrs);
                DataFromMetricsAggregator.DataKey dataKey = getDataKey(data);

                String lastMessageForDataType = dataHash.get(dataKey);
                String jsonStringForDataHash = JSON.toJSONString(attrs);

                //如果数据没有变化，不用重复写
                if (lastMessageForDataType != null && lastMessageForDataType.equals(jsonStringForDataHash)) {
                    logger.debug("We skipped delivering a message since it hadn't changed: " + dataKey);
                    continue;
                } else {
                    // store the raw json string so we skip it next time if this data object doesn't change
                    dataHash.put(dataKey, jsonStringForDataHash);
                }

                //获取处理的开始时间
                Long beginTime = null;
                if (timeHolder.get(dataKey) == null || timeHolder.get(dataKey).get() == 0L) {
                    timeHolder.put(dataKey, new AtomicLong(currentTime));
                }
                beginTime = timeHolder.get(dataKey).get();

                //处理了多久了
                Long period = currentTime - beginTime;


                //数据处理
                DataFromMetricsAggregator aggregatorData = aggregateData.get(dataKey);
                if (aggregatorData == null) {
                    DataFromMetricsAggregator instanceData = new DataFromMetricsAggregator(data.getType(), data.getName());
                    /*if ("HystrixThreadPool".equals(data.getKey().getType())) {

                    } else if ("HystrixCommand".equals(data.getKey().getType())) {

                    }*/
                    aggregateData.putIfAbsent(dataKey, instanceData);
                }
                aggregatorData = aggregateData.get(dataKey);


                //时间到了，输出
                if (period >= 10000) {
                    if ("HystrixCommand".equals(data.getKey().getType())) {
                        //发送到订阅端
                        //monitor.getDispatcher().pushData(monitor.getStatsInstance(), aggregatorData);
                        //TODO 这边先直接输出
                        logger.info("统计结果:" + aggregatorData.getNumericAttributes().get("rollingCountFallbackSuccess"));

                    }
                    //重置时间
                    timeHolder.put(dataKey, new AtomicLong(currentTime));
                    //将数据清0
                    aggregatorData.clear();
                } else {
                    //否则进行统计
                    aggregatorData.aggregator(dataKey, sourceNumericAttributes, currentTime);
                }

            }


            /*//计算数据
            long now = System.currentTimeMillis();
            if (lastFlushTime.get() == 0L || (now - lastFlushTime.get()) > 10000) {
                monitor.getDispatcher().pushData(monitor.getStatsInstance(), aggregateData.values());
                //aggregateData.clear();
                lastFlushTime.set(now);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private DataFromMetricsAggregator.DataKey getDataKey(TurbineData data) {
        Object groupObject = data.getAttributes().get(GROUP);
        Object threadPoolObject = data.getAttributes().get(THREAD_POOL);
        String group = String.valueOf("");
        String threadPool = String.valueOf("");
        if (groupObject != null) {
            group = groupObject.toString();
        }
        if (threadPoolObject != null) {
            threadPool = threadPoolObject.toString();
        }
        DataFromMetricsAggregator.DataKey dataKey = new DataFromMetricsAggregator.DataKey(data.getType(), data.getName(), group, threadPool);
        return dataKey;
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