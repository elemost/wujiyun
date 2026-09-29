package com.wuji.service.model.domain;

import com.wuji.service.model.info.MongodbSearchFilter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormPrivilegeDataScopeDomain {
    private Boolean all = Boolean.FALSE;

    private List<Long> userIdList = new ArrayList<>();

    private List<Long> deptIdList = new ArrayList<>();

    private MongodbSearchFilter filter;

    @ApiModelProperty("操作权限")
    private List<String> operatePrivilegeList;

    @ApiModelProperty("查看权限")
    private List<String> viewPrivilegeList;
}
