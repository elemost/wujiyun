package com.wuji.workflow.listeners.task;

import com.wuji.common.utils.ToolSpring;
import com.wuji.workflow.constant.FlowableConstant;
import com.wuji.workflow.interfaces.ProcessInstanceInterface;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("subFlowTaskListener")
public class SubFlowTaskListener implements TaskListener {

    private final Map<String, ProcessInstanceInterface> processInstanceInterfaceMap;

    public SubFlowTaskListener() {
        this.processInstanceInterfaceMap = ToolSpring.getBeansOfType(ProcessInstanceInterface.class);
    }

    public void notify(DelegateTask delegateTask) {

        Map<String, Object> variables = delegateTask.getVariables();
        ProcessInstanceInterface processInstanceInterface = processInstanceInterfaceMap.get(
                variables.getOrDefault(FlowableConstant.FLOWABLE_CALLBACK_SERVICE, "").toString());
        if (processInstanceInterface != null) {
            processInstanceInterface.subFlowTaskStart(delegateTask );
        }
    }
}
