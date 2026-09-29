package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseUuidEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 表单组件表
 * </p>
 *
 * @author hzm
 * @since 2025-01-21
 */
@Getter
@Setter
@TableName("lc_template_form_module")
@ApiModel(value = "TemplateFormModuleEntity对象", description = "表单组件表")
public class TemplateFormModuleEntity extends BaseUuidEntity {

    @ApiModelProperty("对应的表单id")
    private String formId;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("业务id")
    private String businessId;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("组件类型")
    private String moduleType;

    @ApiModelProperty("应用id")
    private String applicationId;
}
