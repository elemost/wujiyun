package com.wuji.service.model.info.plugin;

import lombok.Data;

import java.util.List;

@Data
public class DataStreamParamMapping {

    private String fieldId;

    // text user
    private String fieldType;

    // 当类型为组合的时候
    private String content;

    // subform
    private String iteratorType;

    private String subForm;

    // custom
    private String resolverType;

    private Object resolverArg;

    private List<DataStreamParamMapping> children;

    private Long nodeId;

    public Long getNodeId() {
        if (nodeId == null) {
            return null;
        }
        if (nodeId == 2) {
            return 1L;
        } else {
            return nodeId;
        }
    }
}
