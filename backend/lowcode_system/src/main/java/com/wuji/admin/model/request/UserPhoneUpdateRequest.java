package com.wuji.admin.model.request;

import lombok.Data;

@Data
public class UserPhoneUpdateRequest {
    private String phonenumber;

    private String code;
}
