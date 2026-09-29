package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormPrivilegeConfigVO {
    private List<String> operatePrivilegeList;

    private Boolean viewPrivilege = false;

    private String id;

    @ApiModelProperty("名称")
    private String groupName;

    @ApiModelProperty("查看权限")
    private List<String> viewPrivilegeList;
}
