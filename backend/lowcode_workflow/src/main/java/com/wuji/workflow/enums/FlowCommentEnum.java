package com.wuji.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum FlowCommentEnum {

    /**
     * 说明
     */
    NORMAL("1", "同意"),
    REBACK("2", "驳回"),
    REJECT("3", "终止"),
    DELEGATE("4", "委派"),
    TRANSFER("5", "转办"),
    // STOP("6", "终止"),
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
        if (StringUtils.isEmpty(type)) {
            return "";
        }
        for (FlowCommentEnum flowCommentEnum :FlowCommentEnum.values()) {
            if (flowCommentEnum.type.equals(type)) {
                return flowCommentEnum.remark;
            }
        }
        return "";
    }
}
