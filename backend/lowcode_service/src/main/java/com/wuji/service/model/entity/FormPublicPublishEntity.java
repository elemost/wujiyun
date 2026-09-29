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
 * @since 2025-02-10
 */
@Getter
@Setter
@TableName("lc_form_public_publish")
@ApiModel(value = "FormPublicPublishEntity对象", description = "")
public class FormPublicPublishEntity extends BaseUuidEntity {

    private String applicationId;

    private String formId;

    @ApiModelProperty("发布类型 FORM_FILL")
    private String publishType;

    @ApiModelProperty("发布配置")
    private String config;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    @ApiModelProperty("修改人名字")
    private String modifierName;

    private Short state;

    private String accessToken;
}
