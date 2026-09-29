package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseUuidEntity;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzm
 * @since 2024-12-23
 */
@Getter
@Setter
@TableName("lc_form_extra_function")
@ApiModel(value = "FormExtraFunctionEntity对象", description = "")
public class FormExtraFunctionEntity extends BaseUuidEntity {

    private String applicationId;

    private String formId;

    private String config;

    private String creator;

    private String modifier;

    private String functionType;

    private Integer sort;
}
