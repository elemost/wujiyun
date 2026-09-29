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
 * @since 2025-08-07
 */
@Getter
@Setter
@TableName("op_sync_mapping")
@ApiModel(value = "SyncMappingEntity对象", description = "")
public class SyncMappingEntity extends BaseUuidEntity {

    private String applicationId;

    private String formId;

    private String mappingConfig;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;
}
