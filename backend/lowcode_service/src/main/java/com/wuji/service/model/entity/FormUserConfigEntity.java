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
 * @since 2025-04-18
 */
@Getter
@Setter
@TableName("lc_form_user_config")
@ApiModel(value = "FormUserConfigEntity对象", description = "")
public class FormUserConfigEntity extends BaseUuidEntity {

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("配置类型")
    private String configType;

    private Long userId;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;
}
