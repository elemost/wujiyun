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
 * @since 2025-09-09
 */
@Getter
@Setter
@TableName("lc_form_info")
@ApiModel(value = "FormInfoEntity对象", description = "")
public class FormInfoEntity extends BaseUuidEntity {


    private String applicationId;

    private String formId;

    private String infoConfig;

    @ApiModelProperty("是否为默认详情页")
    private Boolean defaultConfig;

    @ApiModelProperty("详情页名称")
    private String infoName;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    @ApiModelProperty("修改人名字")
    private String modifierName;

    private Boolean enable;

    private Integer sort;

    private String otherConfig;
}
