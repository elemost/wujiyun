package com.wuji.open.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeptOpenUpdateRequest {
    @ApiModelProperty("父部门id")
    private Long parentId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("显示顺序")
    private Integer orderNum;

    private Long deptId;
}
