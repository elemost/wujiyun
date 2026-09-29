package com.wuji.admin.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class CompanyVO {
    private Long companyId;

    private String companyName;

    private String pullConfig;

    private String secretId;

    private String dataSource;

    private String companyUuid;

    private String logo;

    private String loginLogo;

    private String background;

    private Boolean separateLoginPage;

    private Short companyType;

    private String subjectColor;

    private String userLimit;

    private Map<String, String> infoMap;

    private String channelType;

    private String mainId;

    private String suiteId;

    private Date createTime;
}