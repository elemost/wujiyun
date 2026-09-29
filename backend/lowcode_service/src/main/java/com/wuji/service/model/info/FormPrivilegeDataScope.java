package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class FormPrivilegeDataScope {
    private String dataScopeType;

    private List<BusinessSelect> dataScopeList;

    private MongodbSearchFilter filter;
}
