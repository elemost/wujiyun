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
 * @since 2025-12-16
 */
@Getter
@Setter
@TableName("lc_form_rule")
@ApiModel(value = "FormRuleEntity对象", description = "")
public class FormRuleEntity extends BaseUuidEntity {

    private String formId;

    private String applicationId;

    private String ruleName;

    private String ruleConfig;

    private String ruleType;

    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    private Integer sort;
}
