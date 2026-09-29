package com.wuji.workflow.handler.multiinstance;

import com.wuji.common.utils.ToolSpring;
import com.wuji.workflow.service.FlowableActivityConfigService;
import com.wuji.workflow.service.WorkFlowService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;

/**
 * 自定义的 ActivityBehaviorFactory 实现类，目的如下：
 * 1. 自定义 {@link #createUserTaskActivityBehavior(UserTask)}：实现自定义的流程任务的 assignee 负责人的分配
 *
 * @author YT
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BpmActivityBehaviorFactory extends DefaultActivityBehaviorFactory {

    private FlowableActivityConfigService flowableActivityConfigService;

    private WorkFlowService workFlowService;

    /**
     * 重写父类的方法，自定义串行多实例行为，用于控制顺序（串行）多实例任务的执行逻辑。
     *
     * @param activity
     * @param innerActivityBehavior
     * @return
     */
    @Override
    public SequentialMultiInstanceBehavior createSequentialMultiInstanceBehavior(Activity activity,
                                                                                 AbstractBpmnActivityBehavior innerActivityBehavior) {
        if (flowableActivityConfigService == null) {
            flowableActivityConfigService = ToolSpring.getBean(FlowableActivityConfigService.class);
        }

        if (workFlowService == null) {
            workFlowService = ToolSpring.getBean(WorkFlowService.class);
        }
        return new MultiInstanceSequentialBehavior(activity, innerActivityBehavior);
    }

    /**
     * 重写父类的方法，自定义并行多实例行为，用于控制并行多实例任务的执行逻辑。
     *
     * @param activity
     * @param innerActivityBehavior
     * @return
     */
    @Override
    public ParallelMultiInstanceBehavior createParallelMultiInstanceBehavior(Activity activity,
                                                                             AbstractBpmnActivityBehavior innerActivityBehavior) {
        if (flowableActivityConfigService == null) {
            flowableActivityConfigService = ToolSpring.getBean(FlowableActivityConfigService.class);
        }

        if (workFlowService == null) {
            workFlowService = ToolSpring.getBean(WorkFlowService.class);
        }
        return new MultiInstanceParallelBehavior(activity, innerActivityBehavior);
    }
}
