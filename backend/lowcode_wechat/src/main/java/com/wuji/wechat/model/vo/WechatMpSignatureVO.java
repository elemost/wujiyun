package com.wuji.wechat.model.vo;

import lombok.Data;

@Data
public class WechatMpSignatureVO {
    private String appId;

    private Long timestamp;

    private String nonceStr;

    private String signature;
}
