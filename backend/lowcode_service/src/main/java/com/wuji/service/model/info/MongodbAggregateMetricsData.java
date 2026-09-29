package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class MongodbAggregateMetricsData {
    private List<Object> data;

    private String name;

    private String label;

    private Object sum;

    private String tag;

    private List<Object> y;

    private Boolean otherCol;

    private Boolean summaryCol = Boolean.FALSE;

    private String dataGroup;

    private String finalDataKey;
}
