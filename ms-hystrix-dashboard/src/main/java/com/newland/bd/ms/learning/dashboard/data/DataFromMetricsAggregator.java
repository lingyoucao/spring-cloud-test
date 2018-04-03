package com.newland.bd.ms.learning.dashboard.data;

import com.alibaba.fastjson.JSON;
import com.netflix.turbine.data.TurbineData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lcs on 2018/3/21.
 *
 * @author lcs
 */
public class DataFromMetricsAggregator extends TurbineData{

    private final static Logger logger = LoggerFactory.getLogger(DataFromMetricsAggregator.class);

    private ConcurrentHashMap<String, AtomicLong> numericAttributes = new ConcurrentHashMap<String, AtomicLong>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicLong>> nestedMapAttributes = new ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicLong>>();


    private ConcurrentHashMap<DataKey, DataKeyHolder> reportingHostsWithLastData = new ConcurrentHashMap<DataKey, DataKeyHolder>();

    public DataFromMetricsAggregator(String type, String name) {
        super(null, type, name);
    }


    @Override
    public HashMap<String, Long> getNumericAttributes() {
        HashMap<String, Long> values = new HashMap<String, Long>(32);
        for (String attributeName : numericAttributes.keySet()) {
            AtomicLong nValue = numericAttributes.get(attributeName);
            if (nValue != null) {
                values.put(attributeName, nValue.get());
            }
        }
        return values;
    }

    @Override
    public HashMap<String, String> getStringAttributes() {
        return null;
    }

    @Override
    public HashMap<String, Map<String, ? extends Number>> getNestedMapAttributes() {
        HashMap<String, Map<String, ? extends Number>> values = new HashMap<String, Map<String, ? extends Number>>(32);
        for (String nestedAttrName : nestedMapAttributes.keySet()) {
            Map<String, Long> nestedMap = new HashMap<String, Long>();
            ConcurrentHashMap<String, AtomicLong> concurrentMap = nestedMapAttributes.get(nestedAttrName);
            for (String attrName : concurrentMap.keySet()) {
                nestedMap.put(attrName, concurrentMap.get(attrName).longValue());
            }
            values.put(nestedAttrName, nestedMap);
        }
        return values;
    }





    public void aggregator(DataKey dataKey, Map<String, Long> sourceNumericAttributes, Long currentTime) {

        logger.info("数据源："+ JSON.toJSONString(sourceNumericAttributes));

        DataKeyHolder historicalDataHolder = reportingHostsWithLastData.get(dataKey);
        if (historicalDataHolder == null) {

            historicalDataHolder = reportingHostsWithLastData.putIfAbsent(dataKey, new DataKeyHolder());
            // if it's not null that means another thread beat us to setting it and we got it back
            if (historicalDataHolder == null) {
                // this means we created it and need to retrieve it again
                historicalDataHolder = reportingHostsWithLastData.get(dataKey);
            }
        }

        // Record the last time the data was touched, which is NOW
        historicalDataHolder.lastEventTime.set(System.currentTimeMillis());

        // Two types of values ...
        // 1) numerical that we are summing together
        // 2) strings that we want to know the values across the cluster and if they differ show how many hosts are reporting the value
        Map<String, Long> historical = historicalDataHolder.lastData.get();



//      /* numeric attributes */
        aggregateNumericMap(sourceNumericAttributes, /** the source attrs */
                numericAttributes, historical
        );

        historicalDataHolder.lastData.set(sourceNumericAttributes);



//    /* nested map attributes */
        /*if (data.getNestedMapAttributes().size() > 0) {

            for (String nestedMapKey : data.getNestedMapAttributes().keySet()) {

                // find the nestedMap. If it does not exist, then create one in a thread safe way
                ConcurrentHashMap<String, AtomicLong> aggNestedMap = nestedMapAttributes.get(nestedMapKey);
                if (aggNestedMap == null) {
                    aggNestedMap = new ConcurrentHashMap<String, AtomicLong>();
                    nestedMapAttributes.putIfAbsent(nestedMapKey, aggNestedMap);
                    aggNestedMap = nestedMapAttributes.get(nestedMapKey);
                }

                Map<String, ? extends Number> sourceMap = data.getNestedMapAttributes().get(nestedMapKey);
                Map<String, ? extends Number> historicalNestedMap = null;
                if (historical != null) {
                    HashMap<String, Map<String, ? extends Number>> historicalNestedMapAttrs = historical.getNestedMapAttributes();
                    if (historicalNestedMapAttrs != null) {
                        historicalNestedMap = historicalNestedMapAttrs.get(nestedMapKey); // can be null
                    }
                }

                aggregateNumericMap(sourceMap, *//** the source attrs *//*
                        aggNestedMap, *//** the target attrs *//*
                        historicalNestedMap *//** historical attrs*//*);
            }
        }*/




    }



    public void clear() {
        numericAttributes.clear();
    }

    private void aggregateNumericMap(Map<String, ? extends Number> sourceAttrs,
                                     ConcurrentHashMap<String, AtomicLong> targetAttrs,
                                     Map<String, ? extends Number> historicalAttrs) {

        for (String attributeName : sourceAttrs.keySet()) {

            AtomicLong sum = targetAttrs.get(attributeName);
            if (sum == null) {
                // it doesn't exist so add this value
                targetAttrs.putIfAbsent(attributeName, new AtomicLong(0));
                // now retrieve it again so we can add to it
                sum = targetAttrs.get(attributeName);
            }

            // decrement the past value (if we have a past value) and add the current new value
            int valueToAdd = sourceAttrs.get(attributeName).intValue();

            if (historicalAttrs != null) {
                Number historicalNumericalValue = historicalAttrs.get(attributeName);
                if (historicalNumericalValue != null) {
                    valueToAdd -= historicalNumericalValue.intValue();
                }
            }
            // atomically add the delta (+/-)
            if(valueToAdd > 0 ) {
                sum.addAndGet(valueToAdd);
            }
        }
    }

    private static class DataKeyHolder {

        public AtomicReference<Map<String,Long>> lastData = new AtomicReference<Map<String,Long>>();

        public final AtomicReference<Long> lastEventTime = new AtomicReference<Long>(0L);
    }

    public static class DataKey {
        private final String name;
        private final String type;
        private final String group;
        private final String threadPool;
        public DataKey(String type, String name, String group,String threadPool) {
            this.type = type;
            this.name = name;
            this.group = group;
            this.threadPool = threadPool;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type, group, threadPool);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            DataKey other = (DataKey) obj;
            boolean equals = (name != null) ? name.equals(other.name) : other.name == null;
            equals &= (type != null) ? type.equals(other.type) : other.type == null;
            equals &= (group != null) ? group.equals(other.group) : other.group == null;
            equals &= (threadPool != null) ? threadPool.equals(other.threadPool) : other.threadPool == null;

            return equals;
        }

        @Override
        public String toString() {
            return "DataKey{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", group='" + group + '\'' +
                    ", threadPool='" + threadPool + '\'' +
                    '}';
        }
    }
}
