package com.wuji.workflow.model.flowable.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class FirstNode extends AssigneeNode {

    @Override
    public List<FlowElement> convert() {
        ArrayList<FlowElement> elements = new ArrayList<>();
        // 用户节点
        UserTask userTask = new UserTask();
        userTask.setId(this.getId());
        if (StringUtils.isNotEmpty(this.getName())) {
            userTask.setName(this.getName());
        } else {
            userTask.setName("流程发起");
        }
        userTask.setExecutionListeners(this.buildEventListener());

        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
        multiInstanceLoopCharacteristics.setSequential(false);
        multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfCompletedInstances > 0}");
        multiInstanceLoopCharacteristics.setElementVariable("assignee");
        multiInstanceLoopCharacteristics.setInputDataItem(this.getId());
        userTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
        userTask.setAssignee(String.format("${%s}", "assignee"));
        elements.add(userTask);
        // 下一个节点的连线
        Node child = this.nextChild();
        SequenceFlow sequenceFlow = this.buildSequence(child);
        elements.add(sequenceFlow);
        // 下一个节点
        if (Objects.nonNull(child)) {
            child.setBranchId(this.getBranchId());
            List<FlowElement> flowElements = child.convert();
            elements.addAll(flowElements);
        }
        return elements;
    }
}
