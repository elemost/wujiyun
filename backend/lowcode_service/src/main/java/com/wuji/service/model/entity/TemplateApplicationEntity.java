package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseUuidEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 应用
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
@Getter
@Setter
@TableName("lc_template_application")
@ApiModel(value = "TemplateApplicationEntity对象", description = "应用")
public class TemplateApplicationEntity extends BaseUuidEntity {

    private String sourceApplicationId;

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("访问地址")
    private String visitUrl;

    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("应用类型")
    private String applicationType;

    @ApiModelProperty("图标")
    private String icon;

    private String introduce;

    private String logo;

    private Integer downloadCount;

    private Integer recommend;

    private String applicationNature;
}
