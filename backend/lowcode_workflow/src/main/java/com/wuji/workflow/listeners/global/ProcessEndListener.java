package com.wuji.workflow.listeners.global;


import com.wuji.common.utils.ToolSpring;
import com.wuji.workflow.constant.FlowableConstant;
import com.wuji.workflow.enums.ProcessStateEnum;
import com.wuji.workflow.interfaces.ProcessInstanceInterface;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Slf4j
@Component("processEndListener")
public class ProcessEndListener extends AbstractFlowableEngineEventListener {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;

    private final Map<String, ProcessInstanceInterface> processInstanceInterfaceMap;

    public ProcessEndListener() {
        this.processInstanceInterfaceMap = ToolSpring.getBeansOfType(ProcessInstanceInterface.class);
    }

    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        FlowableEntityEventImpl flowableEntityEvent = (FlowableEntityEventImpl) event;
        String processInstanceId = flowableEntityEvent.getProcessInstanceId();
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().includeProcessVariables()
                        .processInstanceId(processInstanceId).singleResult();
        Object status = historicProcessInstance.getProcessVariables().get(FlowableConstant.PROCESS_STATUS_SING_KEY);
        if (status != null) {
            if (!ProcessStateEnum.TERMINATED.getStatus().equals(status)) {
                runtimeService.setVariable(flowableEntityEvent.getProcessInstanceId(),
                        FlowableConstant.PROCESS_STATUS_SING_KEY, ProcessStateEnum.COMPLETED.getStatus());
            }
        }
        Map<String, Object> processVariables = historicProcessInstance.getProcessVariables();
        ProcessInstanceInterface processInstanceInterface = processInstanceInterfaceMap.get(
                processVariables.getOrDefault(FlowableConstant.FLOWABLE_CALLBACK_SERVICE, "").toString());
        if (processInstanceInterface != null) {
            processInstanceInterface.processEnd(historicProcessInstance);
        }
    }
}
