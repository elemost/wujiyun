package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Getter
public enum SexEnum {
    MAN("1", "男"),
    WOMAN("2","女");
    private String code;

    private String desc;

    public static String getDesc(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        for (SexEnum sexEnum : SexEnum.values()) {
            if (sexEnum.getCode().equals(code)) {
                return sexEnum.getDesc();
            }
        }
        return "";
    }
}
