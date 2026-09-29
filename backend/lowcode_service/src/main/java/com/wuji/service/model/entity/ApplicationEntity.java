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
 * @since 2024-08-19
 */
@Getter
@Setter
@TableName("lc_application")
@ApiModel(value = "ApplicationEntity对象", description = "应用")
public class ApplicationEntity extends BaseUuidEntity {

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("访问地址")
    private String visitUrl;

    private String icon;

    private String applicationType;

    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    private String templateId;

    private String applicationNature;
}
