package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationSortTypeEnum {
    CREATE_TIME("创建时间"),
    UPDATE_TIME("修改时间");

    private final String msg;
}
