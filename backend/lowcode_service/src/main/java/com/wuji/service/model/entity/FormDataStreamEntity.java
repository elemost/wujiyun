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
 * @since 2024-12-30
 */
@Getter
@Setter
@TableName("lc_form_data_stream")
@ApiModel(value = "FormDataStreamEntity对象", description = "")
public class FormDataStreamEntity extends BaseUuidEntity {

    @ApiModelProperty("名字")
    private String name;

    @ApiModelProperty("配置类型")
    private String configType;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("表单id")
    private String formId;

    private String canvasConfig;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("状态")
    private String state;

    private Integer version;

    private Boolean enable;
}
