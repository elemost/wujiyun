package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompanyAppEquityEnum {
    EP_COMMUNITY("ep_community","ep_standard", "标准版"),
    EP_STANDARD("ep_standard","ep_professional", "专业版"),
    EP_PROFESSIONAL("ep_professional","ep_ultimate", "旗舰版"),
    EP_ULTIMATE("ep_ultimate",null,null);

    private final String version;

    private final String afterVersion;

    private final String afterVersionName;

}
