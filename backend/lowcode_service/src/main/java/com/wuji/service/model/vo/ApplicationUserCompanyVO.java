package com.wuji.service.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ApplicationUserCompanyVO {
    private Long companyId;

    private Long deptId;

    private Long userId;

    private String nickName;

    private String companyUuid;

    private List<Long> deptIdList;

    private List<Long> dataScopeDeptIdList;

    private Boolean adminUser;
}
