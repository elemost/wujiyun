package com.wuji.admin.model.vo;

import lombok.Data;

@Data
public class UserImportErrorVO {
    private Integer row;

    private String errorMessage;

    private Boolean success = Boolean.TRUE;
}
