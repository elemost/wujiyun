package com.wuji.wechat.model.vo;

import lombok.Data;

@Data
public class WeChatMiniAppLoginVO {
    private String openId;

    private String mobile;

    private String unionId;
}
