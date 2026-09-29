package com.wuji.service.model.info.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamGatewayNode extends DataStreamCommon {
    private List<DataStreamConditionNode> children;

    // match_all match_one
    private String triggerType;
}
