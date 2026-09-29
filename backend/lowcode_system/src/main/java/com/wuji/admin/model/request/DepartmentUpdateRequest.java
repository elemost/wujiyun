package com.wuji.admin.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DepartmentUpdateRequest {
    private Long deptId;

    @ApiModelProperty("父部门id")
    private Long parentId;

    @ApiModelProperty("祖级列表")
    private String ancestors;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("显示顺序")
    private Integer orderNum;

    @ApiModelProperty("负责人")
    private String leader;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    private String deptType;

    @ApiModelProperty("部门状态（0正常 1停用）")
    private String status;
}
