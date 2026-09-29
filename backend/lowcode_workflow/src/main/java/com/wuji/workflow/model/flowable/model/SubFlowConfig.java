package com.wuji.workflow.model.flowable.model;

import com.wuji.workflow.enums.FlowableListenerDefaultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubFlowConfig extends Node {
    private String subFormId;
    // 任务监听器
    private List<NodeListener> taskListeners;

    @Override
    public List<FlowElement> convert() {
        ArrayList<FlowElement> elements = new ArrayList<>();
        // 用户节点
        UserTask userTask = new UserTask();
        userTask.setId(this.getId());
        userTask.setName(this.getName());
        userTask.setTaskListeners(FlowableListenerDefaultEnum.getListenerByType("subFlowTask"));
        // 审批人
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
        multiInstanceLoopCharacteristics.setSequential(false);
        multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfCompletedInstances/nrOfInstances >= 1}");

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
