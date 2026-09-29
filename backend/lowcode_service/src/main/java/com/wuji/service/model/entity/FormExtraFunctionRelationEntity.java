package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2024-12-25
 */
@Getter
@Setter
@TableName("lc_form_extra_function_relation")
@ApiModel(value = "FormExtraFunctionRelationEntity对象", description = "")
public class FormExtraFunctionRelationEntity {
    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    @ApiModelProperty("功能id")
    private String functionId;

    @ApiModelProperty("业务id")
    private String businessId;

    @ApiModelProperty("业务类型")
    private String businessType;
}
