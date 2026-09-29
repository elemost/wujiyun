package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户与岗位关联表
 * </p>
 *
 * @author hzm
 * @since 2024-10-22
 */
@Getter
@Setter
@TableName("sys_user_post")
@ApiModel(value = "UserPostEntity对象", description = "用户与岗位关联表")
public class UserPostEntity {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("岗位ID")
    private Long postId;

    private Long companyId;
}
