package com.wuji.wechat.model.request;

import lombok.Data;

@Data
public class WeChatQrCodeRequest {
    private String scene;

    private String page;

    private String env_version;

    private Boolean check_path;
}
