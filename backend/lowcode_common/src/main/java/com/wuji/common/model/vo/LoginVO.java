package com.wuji.common.model.vo;

import lombok.Data;

/**
 * 登录
 * @author caijiquan
 */
@Data
public class LoginVO {
    private String token;
    /**
     * 是否新用户
     */
    private Boolean newUser = Boolean.FALSE;

    private Boolean qiCodeNewUser;

    private String openId;

    private String redirect;

    private Long userId;

}
