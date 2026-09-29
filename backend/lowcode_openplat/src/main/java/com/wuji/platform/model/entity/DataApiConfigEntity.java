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
 * @since 2025-09-09
 */
@Getter
@Setter
@TableName("op_data_api_config")
@ApiModel(value = "DataApiConfigEntity对象", description = "")
public class DataApiConfigEntity extends BaseUuidEntity {

    private String configName;

    private String config;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    private Long companyId;

    private String applicationId;

    private String configType;
}
