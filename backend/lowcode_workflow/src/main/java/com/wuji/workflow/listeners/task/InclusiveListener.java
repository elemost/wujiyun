package com.wuji.workflow.listeners.task;

import com.wuji.common.utils.ToolSpring;
import com.wuji.workflow.constant.FlowableConstant;
import com.wuji.workflow.interfaces.ProcessInstanceInterface;
import com.wuji.workflow.model.vo.ModelVO;
import com.wuji.workflow.service.ModelManageService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("inclusiveListener")
public class InclusiveListener implements ExecutionListener {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ModelManageService modelManageService;

    private final Map<String, ProcessInstanceInterface> processInstanceInterfaceMap;

    public InclusiveListener() {
        this.processInstanceInterfaceMap = ToolSpring.getBeansOfType(ProcessInstanceInterface.class);
    }

    @Override
    public void notify(DelegateExecution delegateTask) {

        ModelVO modelVO = modelManageService.infoByProcessDefinitionId(delegateTask.getProcessDefinitionId());
        ProcessInstance processInstance =
                runtimeService.createProcessInstanceQuery().processInstanceId(delegateTask.getProcessInstanceId())
                        .includeProcessVariables().singleResult();
        ProcessInstanceInterface processInstanceInterface = processInstanceInterfaceMap.get(
                processInstance.getProcessVariables().getOrDefault(FlowableConstant.FLOWABLE_CALLBACK_SERVICE, "")
                        .toString());
        if (processInstanceInterface != null) {
            Map<String, Boolean> conditionCheckMap = processInstanceInterface.checkCondition(processInstance, modelVO,
                    delegateTask.getCurrentActivityId());
            conditionCheckMap.forEach((key, val) -> {
                if (val) {
                    runtimeService.setVariable(delegateTask.getId(), key.replace("-", ""), "1");
                } else {
                    runtimeService.setVariable(delegateTask.getId(), key.replace("-", ""), "0");
                }
            });

        }
    }


}
