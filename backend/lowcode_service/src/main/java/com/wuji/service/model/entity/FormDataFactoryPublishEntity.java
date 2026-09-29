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
 * @since 2026-01-17
 */
@Getter
@Setter
@TableName("lc_form_data_factory_publish")
@ApiModel(value = "FormDataFactoryPublishEntity对象", description = "")
public class FormDataFactoryPublishEntity extends BaseUuidEntity {

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("数据工厂类型")
    private String factoryType;

    @ApiModelProperty("数据工厂配置")
    private String factoryConfig;

    @ApiModelProperty("对应表名")
    private String tableName;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    private String factoryName;

    private String syncConfig;

    private Integer version;

    private Boolean lastVersion;
}
