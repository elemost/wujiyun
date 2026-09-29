package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DataStreamPluginTypeEnum {

    WE_COM_APP("企业微信应用短信"),
    WE_COM_THIRD_APP("企业三方应用短信"),
    IN_MAIL("站内信"),
    SYNC_DATA("数据推送"),
    HTTP("HTTP请求");

    private final String msg;
}
