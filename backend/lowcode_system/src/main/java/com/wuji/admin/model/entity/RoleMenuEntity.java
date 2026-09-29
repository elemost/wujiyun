package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseEntity;
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
 * @since 2024-04-22
 */
@Getter
@Setter
@TableName("sys_role_menu")
@ApiModel(value = "RoleMenuEntity对象", description = "")
public class RoleMenuEntity {

    private Long roleId;

    private Long menuId;
}
