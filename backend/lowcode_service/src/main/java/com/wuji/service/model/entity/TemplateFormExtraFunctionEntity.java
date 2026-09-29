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
@TableName("lc_template_form_extra_function")
@ApiModel(value = "TemplateFormExtraFunctionEntity对象", description = "")
public class TemplateFormExtraFunctionEntity extends BaseUuidEntity {

    private String applicationId;

    private String formId;

    private String config;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    private String functionType;

    private Integer sort;
}
