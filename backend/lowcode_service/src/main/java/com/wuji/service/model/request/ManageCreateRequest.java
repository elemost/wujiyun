package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ManageCreateRequest {
    @ApiModelProperty("管理组名称")
    private String manageName;

    @ApiModelProperty("部门管理")
    private String deptScope;

    private String deptScopeType;

    @ApiModelProperty("职位管理范围")
    private String postScope;

    private String postScopeType;

    @ApiModelProperty("内部角色可见")
    private Boolean roleRead;

    @ApiModelProperty("内部角色可管理")
    private Boolean roleWrite;

    @ApiModelProperty("内部部门")
    private Boolean deptManage;

    @ApiModelProperty("互联组织")
    private Boolean corpCoopManage;

    @ApiModelProperty("是否可操作应用")
    private Boolean appUpdate;
}
