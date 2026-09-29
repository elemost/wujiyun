package com.wuji.admin.constant;

import com.wuji.admin.enums.CompanyAppEquityEnum;
import com.wuji.admin.model.vo.CompanyAppVO;
import com.wuji.common.enums.DictDataTypeEnum;

public class Constants {
    public static final String DELETED = "2";

    public static final String NU_DELETED = "0";

    public static final String NORMAL = "0";

    public static final String PASSWORD_KEY = "wujicloudkey2025";

    public static final String ENV = DictDataTypeEnum.ELECLOUD.name().toLowerCase();

    public static String getErrorMessageApp(CompanyAppVO companyAppVO, String freeErrorMessage) {
        CompanyAppEquityEnum companyAppEquityEnum =
                CompanyAppEquityEnum.valueOf(companyAppVO.getEquityId().toUpperCase());
        String errorMessage = String.format("当前%s已经用完，请升级版本!&&您当前版本：", freeErrorMessage) +
                companyAppVO.getEquityName();
        if (companyAppEquityEnum.getAfterVersionName() != null) {
            errorMessage = errorMessage + "，可以升级到" + companyAppEquityEnum.getAfterVersionName();
        } else {
            errorMessage = errorMessage + "，请联系管理员";
        }
        return errorMessage;
    }

    public static String getErrorMessage(String functionName, CompanyAppVO companyAppVO) {
        CompanyAppEquityEnum companyAppEquityEnum =
                CompanyAppEquityEnum.valueOf(companyAppVO.getEquityId().toUpperCase());
        String errorMessage =
                "您帐号的" + functionName + "数量已经用完，请升级版本!&&您当前版本：" + companyAppVO.getEquityName();
        if (companyAppEquityEnum.getAfterVersionName() != null) {
            errorMessage = errorMessage + "，可以升级到" + companyAppEquityEnum.getAfterVersionName();
        } else {
            errorMessage = errorMessage + "，请联系管理员";
        }
        return errorMessage;
    }

    public static String freeErrorMessage = "您的帐号试用已到期，请升级版本！";
}
