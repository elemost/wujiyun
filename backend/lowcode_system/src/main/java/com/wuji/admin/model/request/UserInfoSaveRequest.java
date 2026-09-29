package com.wuji.admin.model.request;

import lombok.Data;

@Data
public class UserInfoSaveRequest {

    private Long userId;

    private String infoKey;

    private String infoValue;
}
