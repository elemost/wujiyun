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
 * 流程表单绑定表
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
@Getter
@Setter
@TableName("lc_template_form_model")
@ApiModel(value = "TemplateFormModelEntity对象", description = "流程表单绑定表")
public class TemplateFormModelEntity {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    @ApiModelProperty("表单id")
    private String formId;

    @ApiModelProperty("流程模块id")
    private String modelId;

    @ApiModelProperty("业务类型")
    private String businessType;

    private String applicationId;

    private String status;
}
