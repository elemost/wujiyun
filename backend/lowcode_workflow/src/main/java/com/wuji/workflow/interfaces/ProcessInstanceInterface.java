package com.wuji.workflow.interfaces;

import com.wuji.workflow.model.vo.ModelVO;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.delegate.DelegateTask;

import java.util.Map;

public interface ProcessInstanceInterface {
    /**
     * 流程结束处理数据
     *
     * @param historicProcessInstance
     */
    void processEnd(HistoricProcessInstance historicProcessInstance);


    /**
     * 流程实例开始
     *
     * @param historicProcessInstance
     */
    void processStart(HistoricProcessInstance historicProcessInstance);

    void subFlowTaskStart(DelegateTask delegateTask);

    void callBackWhileTaskCreate(Task task);

    Map<String, Boolean> checkCondition(ProcessInstance processInstance, ModelVO modelVO, String pid);

    void taskFinish(HistoricTaskInstance historicTaskInstance, Map<String, Object> processVariables);

}
