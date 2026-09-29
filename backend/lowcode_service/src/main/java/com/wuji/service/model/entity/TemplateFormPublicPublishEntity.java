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
 * @since 2025-02-24
 */
@Getter
@Setter
@TableName("lc_template_form_public_publish")
@ApiModel(value = "TemplateFormPublicPublishEntity对象", description = "")
public class TemplateFormPublicPublishEntity extends BaseUuidEntity {

    private String applicationId;

    private String formId;

    @ApiModelProperty("发布类型")
    private String publishType;

    @ApiModelProperty("发布配置")
    private String config;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    @ApiModelProperty("修改人名字")
    private String modifierName;

    @ApiModelProperty("状态")
    private Short state;

    private String accessToken;
}
