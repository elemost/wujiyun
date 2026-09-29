package com.wuji.workflow.listeners.global;

import com.wuji.common.utils.ToolSpring;
import com.wuji.workflow.constant.FlowableConstant;
import com.wuji.workflow.interfaces.ProcessInstanceInterface;
import com.wuji.workflow.service.WorkFlowService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("historicActivityInstanceEndListen")
public class HistoricActivityInstanceEndListener extends AbstractFlowableEngineEventListener {

    private final Map<String, ProcessInstanceInterface> processInstanceInterfaceMap;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private RuntimeService runtimeService;

    public HistoricActivityInstanceEndListener() {
        this.processInstanceInterfaceMap = ToolSpring.getBeansOfType(ProcessInstanceInterface.class);
    }

    @Override
    protected void historicActivityInstanceEnded(FlowableEngineEntityEvent event) {
        HistoricActivityInstanceEntityImpl historicActivityInstanceEntity =
                (HistoricActivityInstanceEntityImpl) event.getEntity();
        // 审批节点抄送
        if ("userTask".equals(historicActivityInstanceEntity.getActivityType())) {
            HistoricTaskInstance historicTaskInstance =
                    historyService.createHistoricTaskInstanceQuery().taskId(historicActivityInstanceEntity.getTaskId())
                            .singleResult();
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().includeProcessVariables()
                    .processInstanceId(historicActivityInstanceEntity.getProcessInstanceId()).singleResult();
            Map<String, Object> processVariables = processInstance.getProcessVariables();
            ProcessInstanceInterface processInstanceInterface = processInstanceInterfaceMap.get(
                    processVariables.getOrDefault(FlowableConstant.FLOWABLE_CALLBACK_SERVICE, "").toString());
            if (processInstanceInterface != null) {
                processInstanceInterface.taskFinish(historicTaskInstance, processInstance.getProcessVariables());
            }
        }
    }
}
