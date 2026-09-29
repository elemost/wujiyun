package com.wuji.workflow.model.flowable.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssigneeTypeEnum {
    USER("user", "用户"),
    DEPT("dept", "部门"),
    SELF("self", "发起人自己"),
    POST("post", "职位"),
    NO_BODY("noBody", "无人审批"),
    FORM_USER("formUser", "表单用户"),
    FORM_DEPT("formDept", "表单部门"),
    FORM_POST("formRole", "表单职位"),

    ORG_LEADER("orgLeader", "组织主管"),
    AUTO_REFUSE("autoRefuse", "自动拒绝"),
    AUTO_PASS("autoPass", "自动通过");

    @JsonValue
    private final String type;
    private final String description;
}
