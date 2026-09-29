package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseUuidEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 表单
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
@Getter
@Setter
@TableName("lc_template_form")
@ApiModel(value = "TemplateFormEntity对象", description = "表单")
public class TemplateFormEntity extends BaseUuidEntity {

    private String applicationId;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("来源id")
    private String sourceId;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("对应表名")
    private String tableName;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("版本")
    private Integer version;

    @ApiModelProperty("表单类型")
    private String formType;

    private String formConfig;
}
