package com.wuji.service.model.info;

import lombok.Data;

@Data
public class FormMessageMarkdown {
    private Long nodeId;

    private String fieldId;

    private String fieldType;

    private String valueType;

    private String markdownType;

    private String customValue;

    private String label;

    private String urlLabel;

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
