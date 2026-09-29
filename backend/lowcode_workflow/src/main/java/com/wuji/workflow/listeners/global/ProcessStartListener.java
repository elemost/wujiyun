package com.wuji.workflow.listeners.global;


import com.wuji.common.utils.ToolSpring;
import com.wuji.workflow.constant.FlowableConstant;
import com.wuji.workflow.interfaces.ProcessInstanceInterface;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableProcessStartedEvent;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Slf4j
@Component("processStartListener")
public class ProcessStartListener extends AbstractFlowableEngineEventListener {

    @Autowired
    private HistoryService historyService;

    private final Map<String, ProcessInstanceInterface> processInstanceInterfaceMap;

    public ProcessStartListener() {
        this.processInstanceInterfaceMap = ToolSpring.getBeansOfType(ProcessInstanceInterface.class);
    }

    @Override
    protected void processStarted(FlowableProcessStartedEvent event) {
        FlowableEntityEventImpl flowableEntityEvent = (FlowableEntityEventImpl) event;
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().includeProcessVariables()
                        .processInstanceId(flowableEntityEvent.getProcessInstanceId()).singleResult();
        Map<String, Object> processVariables = historicProcessInstance.getProcessVariables();
        ProcessInstanceInterface processInstanceInterface = processInstanceInterfaceMap.get(
                processVariables.getOrDefault(FlowableConstant.FLOWABLE_CALLBACK_SERVICE, "").toString());
        if (processInstanceInterface != null) {
            processInstanceInterface.processStart(historicProcessInstance);
        }
    }
}
