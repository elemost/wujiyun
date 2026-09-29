package com.wuji.common.enums;

import com.wuji.common.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum SuiteApplicationEnum {
    JXC("ww04af11eddb293320", "3v42ztzw0"),
    CRM("wwc292abe4dd98c76f", "3v6li6u7k"),
    CKGL("ww4f2818b668ea0d30","3vvxsl9gw"),
    CGGG("ww4e953b94b68a8a86","3vvy36drk"),
    GDGL("ww4189bc36e0745faf","3w4amdf74"),;

    private final String suiteId;

    private final String templateApplicationId;

    public static String getSuiteIdByApp(String templateApplicationId) {
        Map<String, String> collect = Arrays.stream(SuiteApplicationEnum.values()).collect(
                Collectors.toMap(SuiteApplicationEnum::getTemplateApplicationId, SuiteApplicationEnum::getSuiteId));
        String suitId = collect.get(templateApplicationId == null ? "" : templateApplicationId);
        if (suitId == null) {
            return Constants.getDefaultSuiteId();
        } else {
            return suitId;
        }
    }

    public static String getSuiteIdByAppId(String templateApplicationId) {
        Map<String, String> collect = Arrays.stream(SuiteApplicationEnum.values()).collect(
                Collectors.toMap(SuiteApplicationEnum::getTemplateApplicationId, SuiteApplicationEnum::getSuiteId));
        return collect.get(templateApplicationId);
    }
}
