package com.wuji.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WorkflowResultCode {
    WORKFLOW_INSTANCE_TASK_ALL_EMPTY("3001", 3001, "流程任务id和实例id不能同时为空"),
    MODULE_KET_SAME("3002", 3002, "模型标识不能重复"),
    PUBLISH_FLOWABLE_ERROR("3003",3003,"流程图不合规范，请重新设计"),
    MODEL_NOT_EXIST("3004",3004,"流程模型不存在！"),
    CAN_NOT_RETURN("3005",3005," 当前节点相对于目标节点，不属于串行关系，无法回退！"),
    PROCESS_INSTANCE_CAN_NOT_FIND("3006", 3006, "未找到流程实例，流程可能已发生变化!"),
    USER_EXIST_TASK("3007", 3007, "当前用户已存在对应任务，不可委派"),
    FLOWABLE_DESIGN_FAIL("3008", 3008, "流程设计失败！")
    ;


    ;



    private final String code;
    private final Integer subCode;
    private final String message;
}
