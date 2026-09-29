package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseUuidEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 聚合表
 * </p>
 *
 * @author hzm
 * @since 2025-03-10
 */
@Getter
@Setter
@TableName("lc_form_aggregate")
@ApiModel(value = "FormAggregateEntity对象", description = "聚合表")
public class FormAggregateEntity extends BaseUuidEntity {

    private String name;

    private String config;

    private String applicationId;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;
}
