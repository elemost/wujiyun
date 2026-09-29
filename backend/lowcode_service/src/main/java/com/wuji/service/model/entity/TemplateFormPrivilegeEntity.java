package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseUuidEntity;
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
@TableName("lc_template_form_privilege")
@ApiModel(value = "TemplateFormPrivilegeEntity对象", description = "")
public class TemplateFormPrivilegeEntity extends BaseUuidEntity {

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("页面id")
    private String categoryId;

    @ApiModelProperty("用户权限类型")
    private String userPrivilege;

    @ApiModelProperty("名称")
    private String groupName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("字段类型")
    private String fieldPrivilege;

    @ApiModelProperty("分组类型")
    private String groupType;

    @ApiModelProperty("查看权限")
    private String viewPrivilege;

    @ApiModelProperty("操作权限")
    private String operatePrivilege;

    @ApiModelProperty("数据范围")
    private String dataScope;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    @ApiModelProperty("修改人名字")
    private String modifierName;

    @ApiModelProperty("操作字段类型")
    private String operateFieldPrivilege;

    private Integer sort;

    private String privilegeConfig;
}
