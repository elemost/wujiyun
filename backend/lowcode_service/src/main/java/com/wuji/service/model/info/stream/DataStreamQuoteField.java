package com.wuji.service.model.info.stream;

import lombok.Data;

@Data
public class DataStreamQuoteField {
    private String id;

    private Long nodeId;

    private String quoteFieldId;

    private String quoteFieldType;

    private String quoteSubForm;

    private String quoteScope;

    private String quoteTableName;

    private String buttonId;

    public String getQuoteKey() {
        return getNodeId() + "_" + quoteSubForm + "_" + quoteTableName;
    }

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
