package com.wuji.workflow.model.flowable.model;


import com.alibaba.fastjson.JSONObject;
import com.wuji.workflow.model.flowable.enums.ApprovalMultiEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description：审批节点
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApprovalNode extends AssigneeNode {

    // 多人审批方式
    private ApprovalMultiEnum multi;
    // 多人会签通过百分比
    private BigDecimal multiPercent;
    // 任务监听器
    private List<NodeListener> taskListeners;

    private List<JSONObject> buttonConfig;

    private Boolean batchAudit;

    @Override
    public List<FlowElement> convert() {

        ArrayList<FlowElement> elements = new ArrayList<>();
        // 用户节点
        UserTask userTask = new UserTask();
        userTask.setId(this.getId());
        userTask.setName(this.getName());
        // userTask.setAsynchronous(true);
        // userTask.setFormKey(this.getFormKey());
        userTask.setExecutionListeners(this.buildEventListener());
        if (!CollectionUtils.isEmpty(this.taskListeners)) {
            List<FlowableListener> listeners =
                    this.taskListeners.stream().filter(l -> StringUtils.isNotBlank(l.getImplementation()))
                            .map(listener -> {
                                FlowableListener eventListener = new FlowableListener();
                                eventListener.setEvent(listener.getEvent());
                                eventListener.setImplementation(listener.getImplementation());
                                eventListener.setImplementationType(listener.getImplementationType());
                                return eventListener;
                            }).collect(Collectors.toList());
            userTask.setTaskListeners(listeners);
        }
        // 审批人
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
        if (this.getMulti() == ApprovalMultiEnum.SEQUENTIAL) {
            multiInstanceLoopCharacteristics.setSequential(true);
        } else if (this.getMulti() == ApprovalMultiEnum.JOINT) {
            multiInstanceLoopCharacteristics.setSequential(false);
            multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfCompletedInstances/nrOfInstances == 1}");
        } else if (this.getMulti() == ApprovalMultiEnum.SINGLE) {
            multiInstanceLoopCharacteristics.setSequential(false);
            multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfCompletedInstances > 0}");
        }
        multiInstanceLoopCharacteristics.setElementVariable("assignee");
        // multiInstanceLoopCharacteristics.setLoopCardinality(this.getId());
        // multiInstanceLoopCharacteristics.setInputDataItem(String.format("${%sCollection}", this.getId()));
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
