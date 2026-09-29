package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum PluginTypeEnum {
    DING_TALK_APP_MESSAGE("DING_TALK");

    private String platform;

    public static String getByPlatform(String pluginType) {
        if (Arrays.stream(PluginTypeEnum.values()).map(Enum::name).collect(Collectors.toList()).contains(pluginType)) {
            return PluginTypeEnum.valueOf(pluginType).platform;
        }
        return null;
    }
}
