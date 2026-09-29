package com.wuji.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum FlowOperateEnum {

    /**
     * 说明
     */
    NORMAL("1", "正常"),
    REBACK("2", "退回"),
    REJECT("3", "驳回"),
    DELEGATE("4", "委派"),
    TRANSFER("5", "转办"),
    STOP("6", "终止"),
    REVOKE("7", "撤回"),
    CLAIM("8", "暂存"),;

    /**
     * 类型
     */
    private final String type;

    /**
     * 说明
     */
    private final String remark;

    public static String getRemarkByType(String type) {
        if (StringUtils.isNotEmpty(type)) {
            return "";
        }
        for (FlowOperateEnum flowOperateEnum :FlowOperateEnum.values()) {
            if (flowOperateEnum.type.equals(type)) {
                return flowOperateEnum.remark;
            }
        }
        return "";
    }
}
