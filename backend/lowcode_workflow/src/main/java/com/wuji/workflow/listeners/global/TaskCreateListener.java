package com.wuji.workflow.listeners.global;

import com.wuji.common.utils.ToolSpring;
import com.wuji.workflow.constant.FlowableConstant;
import com.wuji.workflow.interfaces.ProcessInstanceInterface;
import com.wuji.workflow.model.vo.ModelVO;
import com.wuji.workflow.service.FlowableActivityConfigService;
import com.wuji.workflow.service.ModelManageService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component("taskCreateListener")
public class TaskCreateListener extends AbstractFlowableEngineEventListener {

    @Autowired
    private FlowableActivityConfigService flowableActivityConfigService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ModelManageService modelManageService;

    private final Map<String, ProcessInstanceInterface> processInstanceInterfaceMap;

    public TaskCreateListener() {
        this.processInstanceInterfaceMap = ToolSpring.getBeansOfType(ProcessInstanceInterface.class);
    }

    protected void taskCreated(FlowableEngineEntityEvent event) {

        TaskEntityImpl entity = (TaskEntityImpl) event.getEntity();
        Task task = taskService.createTaskQuery().includeProcessVariables().taskId(entity.getId()).singleResult();
        boolean isMultiInstance =
                taskService.getVariable(task.getId(), task.getTaskDefinitionKey() + "_isMultiInstance") == null;
        if (isMultiInstance) {
            ModelVO modelVO = modelManageService.infoByProcessDefinitionId(task.getProcessDefinitionId());
            List<String> candidateList =
                    flowableActivityConfigService.getAssigneeUserList(task.getTaskDefinitionKey(), modelVO.getModelId(),
                            task.getProcessVariables());
            for (String candidate : candidateList) {
                taskService.addCandidateUser(task.getId(), candidate);
            }
        }
        Map<String, Object> processVariables = task.getProcessVariables();
        ProcessInstanceInterface processInstanceInterface = processInstanceInterfaceMap.get(
                processVariables.getOrDefault(FlowableConstant.FLOWABLE_CALLBACK_SERVICE, "").toString());
        if (processInstanceInterface != null) {
            processInstanceInterface.callBackWhileTaskCreate(task);
        }
    }

}
