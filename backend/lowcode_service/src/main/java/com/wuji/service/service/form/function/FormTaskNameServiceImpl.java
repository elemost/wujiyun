package com.wuji.service.service.form.function;

import com.wuji.service.converter.AbstractFormWorkflowConverter;
import com.wuji.service.model.request.FormDataExtraParameterRequest;
import com.wuji.service.model.vo.FormPendingTaskVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormFunctionDataService;
import com.wuji.workflow.model.vo.PendingTaskVO;
import com.wuji.workflow.service.WorkFlowService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FormTaskNameServiceImpl implements FormFunctionDataService {

    @Autowired
    private WorkFlowService workFlowService;

    @Override
    public String fieldType() {
        return "system_taskName";
    }


    @Override
    public void dealFunctionReturn(List<LowcodeDataVO> lowcodeDataList,
                                   FormDataExtraParameterRequest formDataExtraParameterRequest, FormVO info) {
        // 获取流程任务map
        Map<String, List<PendingTaskVO>> processInstanceIdMap = getProcessInstanceIdMap(lowcodeDataList);
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            List<PendingTaskVO> taskVOS = processInstanceIdMap.get(lowcodeDataVO.getProcessInstanceId());
            List<FormPendingTaskVO> tasks = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(taskVOS)) {
                lowcodeDataVO.setTaskName(
                        taskVOS.stream().map(PendingTaskVO::getTaskName).distinct().collect(Collectors.joining("，")));
                for (PendingTaskVO taskVO : taskVOS) {
                    FormPendingTaskVO formPendingTaskVO = AbstractFormWorkflowConverter.INSTANCE.toVO(taskVO);
                    tasks.add(formPendingTaskVO);
                }
            }
            lowcodeDataVO.setTasks(tasks);
        }
    }

    private Map<String, List<PendingTaskVO>> getProcessInstanceIdMap(List<LowcodeDataVO> lowcodeDataList) {
        List<String> processInstanceIdList =
                lowcodeDataList.stream().map(LowcodeDataVO::getProcessInstanceId).filter(Objects::nonNull)
                        .collect(Collectors.toList());
        // 任务
        Map<String, List<PendingTaskVO>> processInstanceIdMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(processInstanceIdList)) {
            List<PendingTaskVO> taskVOList = workFlowService.getTaskByProcessInstanceIdList(processInstanceIdList);
            processInstanceIdMap =
                    taskVOList.stream().collect(Collectors.groupingBy(PendingTaskVO::getProcessInstanceId));
        }
        return processInstanceIdMap;
    }
}
