package com.wuji.workflow.model.flowable.model;

import com.wuji.workflow.enums.FlowableListenerDefaultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.InclusiveGateway;
import org.flowable.bpmn.model.SequenceFlow;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 包容
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InclusiveNode extends BranchNode {
    private String name;

    @Override
    public List<FlowElement> convert() {
        ArrayList<FlowElement> elements = new ArrayList<>();

        // 并行分支
        InclusiveGateway inclusiveGateway = new InclusiveGateway();
        inclusiveGateway.setId(this.getId());
        inclusiveGateway.setName(this.getName());
        inclusiveGateway.setExecutionListeners(FlowableListenerDefaultEnum.getListenerByType("inclusive"));
        elements.add(inclusiveGateway);
        List<ConditionNode> children = this.getChildren();
        // 结束网关
        InclusiveGateway endInclusiveGateway = new InclusiveGateway();
        endInclusiveGateway.setId(this.getId() + "_" + "end");
        endInclusiveGateway.setName(this.getName());
        elements.add(endInclusiveGateway);
        // 条件分支节点
        if (!CollectionUtils.isEmpty(children)) {
            for (Node next : children) {
                String branchId = endInclusiveGateway.getId();
                // Optional.ofNullable(this.nextChild()).map(Node::getId).orElse(this.getBranchId());
                next.setBranchId(branchId);
                elements.addAll(next.convert());
            }
        }

        // 下一个节点
        Node child = this.nextChild();

        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setTargetRef(child == null ? this.getBranchId() : child.getId());
        sequenceFlow.setId(String.format("%s_%s", endInclusiveGateway.getId(), sequenceFlow.getTargetRef()));
        sequenceFlow.setSourceRef(endInclusiveGateway.getId());

        elements.add(sequenceFlow);
        if (Objects.nonNull(child)) {
            child.setBranchId(this.getBranchId());
            child.setPid(endInclusiveGateway.getId());
            List<FlowElement> flowElements = child.convert();
            elements.addAll(flowElements);
        }
        return elements;
    }
}
