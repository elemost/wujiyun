package com.wuji.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserDeptVO implements Serializable {
    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("部门id")
    private Long deptId;

    private String deptName;
}
