package com.wuji.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HttpClientStatusLineEnum {
    OK(200, "SUCCESS"),
    NOT_FOUND(404, "接口不存在"),
    FORBIDDEN(403,"接口无权限"),
    UNAUTHORIZED(401, "用户未登录")
    ;

    private final Integer code;

    private final String message;

    public static HttpClientStatusLineEnum getByCode(Integer code) {
        if (code == null) {
            return HttpClientStatusLineEnum.OK;
        }
        for (HttpClientStatusLineEnum statusLineEnum : HttpClientStatusLineEnum.values()) {
            if (statusLineEnum.getCode().equals(code)) {
                return statusLineEnum;
            }
        }
        return HttpClientStatusLineEnum.OK;
    }
}
