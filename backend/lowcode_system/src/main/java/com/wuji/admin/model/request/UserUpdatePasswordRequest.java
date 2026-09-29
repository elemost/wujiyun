package com.wuji.admin.model.request;

import lombok.Data;

@Data
public class UserUpdatePasswordRequest {
    private String password;

    private Long userId;

    private String originPassword;
}
