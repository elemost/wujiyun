package com.wuji.quartz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QuartzResultCode {
    JOB_ERROR("5001", 5001, "定时任务执行失败:%s")
    ;

    private final String code;
    private final Integer subCode;
    private final String message;
}
