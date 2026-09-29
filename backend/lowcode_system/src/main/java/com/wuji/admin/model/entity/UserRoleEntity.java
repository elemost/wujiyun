package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzm
 * @since 2024-04-22
 */
@Getter
@Setter
@TableName("sys_user_role")
@ApiModel(value = "UserRoleEntity对象", description = "")
public class UserRoleEntity {

    private Long userId;

    private Long roleId;

    private Long companyId;
}
