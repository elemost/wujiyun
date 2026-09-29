package com.wuji.workflow.handler.multiinstance;

import com.wuji.common.enums.UserDefaultEnum;
import com.wuji.common.utils.ToolSpring;
import com.wuji.workflow.model.vo.ModelVO;
import com.wuji.workflow.service.FlowableActivityConfigService;
import com.wuji.workflow.service.ModelManageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.flowable.bpmn.model.Activity;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;

import java.util.List;

/**
 * 多实例串行行为解析类
 *
 * @author wsl
 * @date 2023/5/4
 */
@Slf4j
public class MultiInstanceSequentialBehavior extends SequentialMultiInstanceBehavior {
    private FlowableActivityConfigService flowableActivityConfigService;

    private ModelManageService modelManageService;

    public MultiInstanceSequentialBehavior(Activity activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
        super(activity, innerActivityBehavior);
    }

    @Override
    protected int createInstances(DelegateExecution multiInstanceRootExecution) {
        log.info("MultiInstanceSequentialBehavior.createInstances start");
        if (flowableActivityConfigService == null) {
            flowableActivityConfigService = ToolSpring.getBean(FlowableActivityConfigService.class);
        }

        if (modelManageService == null) {
            modelManageService = ToolSpring.getBean(ModelManageService.class);
        }
        // 获取当前节点的候选执行人，此处自行替换为获取候选用户逻辑
        ModelVO modelVO =
                modelManageService.infoByProcessDefinitionId(multiInstanceRootExecution.getProcessDefinitionId());
        List<String> userList =
                flowableActivityConfigService.getAssigneeUserList(multiInstanceRootExecution.getCurrentActivityId(),
                        modelVO.getModelId(), multiInstanceRootExecution.getVariables());
        if (CollectionUtils.isEmpty(userList)) {
            userList.add(UserDefaultEnum.NO_BODY.getId().toString());
        }
        // 设置候选执行人
        multiInstanceRootExecution.setVariable(multiInstanceRootExecution.getCurrentActivityId(), userList);
        multiInstanceRootExecution.setVariable(multiInstanceRootExecution.getCurrentActivityId() + "_isMultiInstance",
                true);
        return super.createInstances(multiInstanceRootExecution);
    }

}
