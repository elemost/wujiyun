package com.wuji.workflow.model.flowable.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.SequenceFlow;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = MergeNode.class, visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = StartNode.class, name = "start"),
        @JsonSubTypes.Type(value = ApprovalNode.class, name = "approval"),
        @JsonSubTypes.Type(value = ConditionNode.class, name = "condition"),
        @JsonSubTypes.Type(value = ExclusiveNode.class, name = "exclusive"),
        @JsonSubTypes.Type(value = ParallelNode.class, name = "parallel"),
        @JsonSubTypes.Type(value = InclusiveNode.class, name = "inclusive"),
        @JsonSubTypes.Type(value = SubFlowConfig.class, name = "subFlowTask"),
        @JsonSubTypes.Type(value = MultiApprovalNode.class, name = "multiApproval"),
        @JsonSubTypes.Type(value = EndNode.class, name = "end"),
        @JsonSubTypes.Type(value = FirstNode.class, name = "first"),})
public abstract class Node implements Serializable {
    private static final long serialVersionUID = 132324315232123L;
    // 节点id
    private String id;
    // 父节点id
    private String pid;
    // 节点名称
    private String name;
    // 节点类型
    @JsonTypeId
    private String type;
    // 执行监听器
    private List<NodeListener> executionListeners;
    // 子节点
    private Node child;
    // 分支id
    @JsonIgnore
    private String branchId;

    private String targetNodeId;

    public Node nextChild() {
        if (this.child != null && this.child instanceof MergeNode) {
            Node nodeChild = this.getChild();
            if (this.child != null && this.child instanceof MergeNode) {
                return nodeChild.nextChild();
            } else {
                return nodeChild;
            }
        } else {
            return this.child;
        }
    }

    public abstract List<FlowElement> convert();

    public List<FlowableListener> buildEventListener() {
        if (!CollectionUtils.isEmpty(this.executionListeners)) {
            return this.executionListeners.stream().filter(l -> StringUtils.isNotBlank(l.getImplementation()))
                    .map(listener -> {
                        FlowableListener executionListener = new FlowableListener();
                        executionListener.setEvent(listener.getEvent());
                        executionListener.setImplementationType(listener.getImplementationType());
                        executionListener.setImplementation(listener.getImplementation());
                        return executionListener;
                    }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public SequenceFlow buildSequence(Node next) {
        String sourceRef;
        String targetRef;
        if (Objects.nonNull(next)) {
            sourceRef = next.getPid();
            targetRef = next.getId();
        } else {
            if (StringUtils.isNotBlank(this.branchId)) {
                sourceRef = this.id;
                targetRef = this.branchId;
            } else {
                throw new RuntimeException(String.format("节点 %s 的下一个节点不能为空", this.id));
            }
        }
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId(String.format("%s_%s", sourceRef, targetRef));
        sequenceFlow.setSourceRef(sourceRef);
        sequenceFlow.setTargetRef(targetRef);
        return sequenceFlow;
    }

}
