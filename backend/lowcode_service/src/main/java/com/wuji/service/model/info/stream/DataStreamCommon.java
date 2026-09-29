package com.wuji.service.model.info.stream;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true,
        include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({@JsonSubTypes.Type(value = DataStreamTriggerNode.class, name = "trigger"),
        @JsonSubTypes.Type(value = DataStreamCreateNode.class, name = "create"),
        @JsonSubTypes.Type(value = DataStreamDeleteNode.class, name = "delete"),
        @JsonSubTypes.Type(value = DataStreamCalculateNode.class, name = "calculate"),
        @JsonSubTypes.Type(value = DataStreamUpdateNode.class, name = "update"),
        @JsonSubTypes.Type(value = DataStreamQueryOneNode.class, name = "one"),
        @JsonSubTypes.Type(value = DataStreamQueryMoreNode.class, name = "more"),
        @JsonSubTypes.Type(value = DataStreamGatewayNode.class, name = "gateway"),
        @JsonSubTypes.Type(value = DataStreamConditionNode.class, name = "condition"),
        @JsonSubTypes.Type(value = DataStreamTimeTriggerNode.class, name = "time_trigger"),
        @JsonSubTypes.Type(value = DataStreamPluginNode.class, name = "plugin"),
        @JsonSubTypes.Type(value = DataStreamCycleNode.class, name = "loop"),
        @JsonSubTypes.Type(value = DataStreamEndNode.class, name = "end"),
        @JsonSubTypes.Type(value = DataStreamStartNode.class, name = "start"),
        @JsonSubTypes.Type(value = DataStreamDemoNode.class, name = "demo"),
        @JsonSubTypes.Type(value = DataStreamFlowableNode.class, name = "flowable"),
        @JsonSubTypes.Type(value = DataStreamButtonTriggerNode.class, name = "button_trigger"),})
public class DataStreamCommon {
    private String name;

    private Long nodeId;

    private DataStreamCommon child;

    private String type;

    private String applicationId;

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
