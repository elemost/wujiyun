package com.wuji.plugin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseUuidEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 插件表
 * </p>
 *
 * @author hzm
 * @since 2025-07-10
 */
@Getter
@Setter
@TableName("lc_plugin")
@ApiModel(value = "PluginEntity对象", description = "插件表")
public class PluginEntity extends BaseUuidEntity {

    private String pluginConfig;

    private String pluginType;

    private String pluginParam;

    private String pluginReturn;

    private String pluginName;

    private String functionType;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    private Boolean defaultInstall;
}
