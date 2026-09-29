package com.wuji.workflow.model.flowable.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowElement;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class EndNode extends Node {

    @Override
    public List<FlowElement> convert() {
        ArrayList<FlowElement> elements = new ArrayList<>();
        // 结束节点
        EndEvent endEvent = new EndEvent();
        endEvent.setId(this.getId());
        endEvent.setName(this.getName());
        endEvent.setExecutionListeners(this.buildEventListener());
        elements.add(endEvent);
        return elements;
    }
}
