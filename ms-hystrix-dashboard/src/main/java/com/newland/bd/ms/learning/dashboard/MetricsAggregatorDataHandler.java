package com.newland.bd.ms.learning.dashboard;

import com.netflix.turbine.data.TurbineData;
import com.netflix.turbine.discovery.Instance;
import com.netflix.turbine.handler.PerformanceCriteria;
import com.netflix.turbine.handler.TurbineDataHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.util.MinimalPrettyPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lcs on 2018/3/20.
 *
 * @author lcs
 */
public class MetricsAggregatorDataHandler <T extends TurbineData> implements TurbineDataHandler<T> {
    private final static Logger logger = LoggerFactory.getLogger(MetricsAggregatorDataHandler.class);

    private final PerformanceCriteria perfCriteria;
    protected final String name;
    private final ObjectWriter objectWriter;

    public MetricsAggregatorDataHandler() {
        this.perfCriteria = new MetricsAggregatorPerformanceCriteria();
        this.name = "MetricsAggregatorHandler_" + UUID.randomUUID().toString();

        ObjectMapper objectMapper = new ObjectMapper();

        objectWriter = objectMapper.prettyPrintingWriter(new MinimalPrettyPrinter());
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void handleData(Collection<T> data) {
        print(data);
    }

    protected void print(Collection<? extends TurbineData> dataCollection) {
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
                String jsonStringForDataHash = objectWriter.writeValueAsString(attrs);
                logger.info(jsonStringForDataHash);
            }
        } catch (IOException e) {
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






    private class MetricsAggregatorPerformanceCriteria implements PerformanceCriteria {

        @Override
        public boolean isCritical() {
            return false;
        }

        @Override
        public int getMaxQueueSize() {
            return 1000;
        }

        @Override
        public int numThreads() {
            return 1;
        }
    }
}
