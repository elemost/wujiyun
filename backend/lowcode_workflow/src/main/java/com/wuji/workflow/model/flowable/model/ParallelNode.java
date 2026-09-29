package com.wuji.workflow.model.flowable.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.ParallelGateway;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 并行
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ParallelNode extends BranchNode {
    private String name;

    @Override
    public List<FlowElement> convert() {
        ArrayList<FlowElement> elements = new ArrayList<>();

        // 并行分支
        ParallelGateway parallelGateway = new ParallelGateway();
        parallelGateway.setId(this.getId());
        parallelGateway.setName(this.getName());
        elements.add(parallelGateway);
        List<ConditionNode> children = this.getChildren();
        // 结束网关
        ParallelGateway endParallelGateway = new ParallelGateway();
        endParallelGateway.setId(this.getId() + "_" + "end");
        endParallelGateway.setName(this.getName());
        elements.add(endParallelGateway);
        // 条件分支节点
        if (!CollectionUtils.isEmpty(children)) {
            for (Node next : children) {
                String branchId = endParallelGateway.getId();
                // Optional.ofNullable(this.nextChild()).map(Node::getId).orElse(this.getBranchId());
                next.setBranchId(branchId);
                elements.addAll(next.convert());
            }
        }
        // 下一个节点
        Node child = this.nextChild();
        if (Objects.nonNull(child)) {
            child.setBranchId(this.getBranchId());
            child.setPid(endParallelGateway.getId());
            List<FlowElement> flowElements = child.convert();
            elements.addAll(flowElements);
        }
        return elements;
    }
}
