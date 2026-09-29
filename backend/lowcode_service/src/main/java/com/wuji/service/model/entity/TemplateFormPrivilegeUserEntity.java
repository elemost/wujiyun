package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2025-01-20
 */
@Getter
@Setter
@TableName("lc_template_form_privilege_user")
@ApiModel(value = "TemplateFormPrivilegeUserEntity对象", description = "")
public class TemplateFormPrivilegeUserEntity {

    @TableId(type = IdType.AUTO)
    protected Long id;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("页面id")
    private String categoryId;

    private String groupId;

    private String businessId;

    private String businessType;

    private Boolean deleted;
}
