package com.wuji.service.model.entity;

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
 * @since 2024-11-09
 */
@Getter
@Setter
@TableName("lc_form_quote")
@ApiModel(value = "FormQuoteEntity对象", description = "")
public class FormQuoteEntity extends BaseEntity {

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

    private Boolean deleted;

    private String quoteFieldType;

    private String businessFieldType;

    private String applicationId;

    private Boolean aggregate;

    private String quoteType;
}
