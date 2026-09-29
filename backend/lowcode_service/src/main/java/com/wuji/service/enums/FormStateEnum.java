package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FormStateEnum {
    DRAFT("草稿"),
    PUBLISH("已发布");

    private final String msg;
}

