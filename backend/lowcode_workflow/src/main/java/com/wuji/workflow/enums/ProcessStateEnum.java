package com.wuji.workflow.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum ProcessStateEnum {
    /**
     * 进行中（审批中）
     */
    RUNNING("running"),
    /**
     * 已终止
     */
    TERMINATED("terminated"),
    /**
     * 已完成
     */
    COMPLETED("completed"),
    /**
     * 已取消
     */
    CANCELED("canceled"),


    REJECT("rejected"),

    DELEGATE("delegated"),

    TRANSFER("transfer");
    private final String status;

    public static List<ProcessStateEnum> running() {
        return Lists.newArrayList(RUNNING, REJECT, DELEGATE, TRANSFER);
    }

    public static List<ProcessStateEnum> finish() {
        return Lists.newArrayList(COMPLETED, TERMINATED);
    }
    public static ProcessStateEnum getByStatus(String status) {
        for (ProcessStateEnum processStateEnum : ProcessStateEnum.values()) {
            if (processStateEnum.getStatus().equals(status)) {
                return processStateEnum;
            }
        }
        return null;
    }
}
