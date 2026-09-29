package com.wuji.framework.model.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String userName;

    private String password;

    private String loginType;

    private String service;

    private String ticket;

    private String ip;

    private String secretId;

    private String code;

    private Long companyId;

    private String url;

    private String mobile;

    private String openId;

    private String userId;

    private String phoneCode;

    private String suiteId;

    private String token;
}
