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
 * @since 2025-01-21
 */
@Getter
@Setter
@TableName("lc_template_form_quote")
@ApiModel(value = "TemplateFormQuoteEntity对象", description = "")
public class TemplateFormQuoteEntity extends BaseUuidEntity {

    @ApiModelProperty("表单id")
    private String formId;

    @ApiModelProperty("类型")
    private String businessType;

    @ApiModelProperty("对应类型id")
    private String businessId;

    @ApiModelProperty("引用表单")
    private String quoteFormId;

    @ApiModelProperty("引用字段")
    private String quoteField;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    @ApiModelProperty("修改人名字")
    private String modifierName;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("业务字段类型")
    private String businessFieldType;

    @ApiModelProperty("引用字段类型")
    private String quoteFieldType;

    @ApiModelProperty("是否聚合")
    private Boolean aggregate;

    @ApiModelProperty("引用类型")
    private String quoteType;
}
