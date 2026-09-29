package com.wuji.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.ImplementationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum FlowableListenerDefaultEnum {
    SUB_FLOW_TASK("subFlowTask", "create", "${subFlowTaskListener}",
            ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION),
    INCLUSIVE("inclusive", "start", "${inclusiveListener}", ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION),
    FIRST("first", "create", "${FirstTaskListener}", ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION),

    ;

    private final String type;

    private final String event;

    private final String implementation;

    private final String implementationType;


    public static List<FlowableListener> getListenerByType(String type) {
        List<FlowableListener> flowableListenerList = new ArrayList<>();
        for (FlowableListenerDefaultEnum flowableListenerDefaultEnum : Arrays.stream(
                        FlowableListenerDefaultEnum.values()).filter(c -> c.getType().equals(type))
                .collect(Collectors.toList())) {
            FlowableListener eventListener = new FlowableListener();
            eventListener.setEvent(flowableListenerDefaultEnum.getEvent());
            eventListener.setImplementation(flowableListenerDefaultEnum.getImplementation());
            eventListener.setImplementationType(flowableListenerDefaultEnum.getImplementationType());
            flowableListenerList.add(eventListener);
        }
        return flowableListenerList;
    }

}
