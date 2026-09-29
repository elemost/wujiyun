package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzm
 * @since 2024-09-11
 */
@Getter
@Setter
@TableName("sys_user_dept")
@ApiModel(value = "UserDeptEntity对象", description = "")
public class UserDeptEntity {

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("部门id")
    private Long deptId;

    private Long companyId;
}
