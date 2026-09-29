package com.wuji.admin.model.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CompanyAppDetailVO {
    private List<CompanyAppVO> companyAppVOList;

    private List<String> functionList;

    private Map<String, ClientFunctionVO> clientFunctionMap;

    private Boolean canUsed = Boolean.TRUE;

    private Boolean expire = Boolean.FALSE;

    private CompanyAppVO companyAppVO;
}
