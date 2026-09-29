package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FormDataStreamConfigTypeEnum {

    FORM("表单触发"),
    TIME("定时触发"),
    BUTTON("按钮触发");
    private final String msg;
}
