package com.wuji.workflow.model.flowable.model;

import lombok.Data;
import org.flowable.bpmn.model.FlowElement;

import java.util.List;

/**
 * @description：分支节点
 */
@Data
public abstract class BranchNode extends Node {
    private List<ConditionNode> children;

    public abstract List<FlowElement> convert();

}
