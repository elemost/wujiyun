package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseEntity;
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
 * @since 2025-08-22
 */
@Getter
@Setter
@TableName("lc_across_app")
@ApiModel(value = "AcrossAppEntity对象", description = "")
public class AcrossAppEntity extends BaseEntity {

    private Long companyId;

    private String applicationId;

    private String formId;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    @ApiModelProperty("修改人名字")
    private String modifierName;

    private String configAppId;
}
