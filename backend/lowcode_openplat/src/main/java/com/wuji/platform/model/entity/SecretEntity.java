package com.wuji.platform.model.entity;

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
 * @since 2025-08-06
 */
@Getter
@Setter
@TableName("op_secret")
@ApiModel(value = "SecretEntity对象", description = "")
public class SecretEntity extends BaseUuidEntity {

    private Long companyId;

    @ApiModelProperty("开放平台key")
    private String appSecret;

    private String appKey;

    private String remark;

    @ApiModelProperty("状态 OPEN CLOSE")
    private String state;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;
}
