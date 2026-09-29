package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FormDataStatusEnum {
    DRAFT("草稿"),
    PASS("完成"),
    NO_PASS("已拒绝"),
    APPROVING("审批中"),
    DELETED("删除"),
    ;

    private final String message;

}
