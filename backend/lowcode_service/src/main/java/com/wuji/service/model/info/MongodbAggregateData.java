package com.wuji.service.model.info;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class MongodbAggregateData {
    private List<JSONObject> x;

    private List<MongodbAggregateMetricsData> val;
}
