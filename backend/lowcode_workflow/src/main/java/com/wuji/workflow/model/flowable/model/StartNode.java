package com.wuji.workflow.model.flowable.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class StartNode extends Node {

    @Override
    public List<FlowElement> convert() {
        ArrayList<FlowElement> elements = new ArrayList<>();
        // 开始节点
        StartEvent startEvent = new StartEvent();
        startEvent.setId(this.getId());
        startEvent.setName(this.getName());
        startEvent.setExecutionListeners(this.buildEventListener());
        elements.add(startEvent);
        // 下一个节点的连线
        Node child = this.nextChild();
        SequenceFlow sequenceFlow = this.buildSequence(child);
        elements.add(sequenceFlow);
        // 下一个节点
        if (Objects.nonNull(child)) {
            List<FlowElement> flowElements = child.convert();
            elements.addAll(flowElements);
        }
        return elements;
    }
}
