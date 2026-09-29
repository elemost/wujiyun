package com.wuji.workflow.model.entity;

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
 * @since 2024-09-23
 */
@Getter
@Setter
@TableName("lc_flowable_activity_config")
@ApiModel(value = "FlowableActivityConfigEntity对象", description = "")
public class FlowableActivityConfigEntity extends BaseUuidEntity {

    @ApiModelProperty("模型id")
    private String modelId;

    @ApiModelProperty("任务id")
    private String activityId;

    private String activityPid;

    private String conditionConfig;

    private String activityName;

    @ApiModelProperty("审批人配置")
    private String assigneeConfig;

    @ApiModelProperty("字段配置")
    private String fieldConfig;

    @ApiModelProperty("抄送配置")
    private String copyConfig;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    @ApiModelProperty("修改人名字")
    private String modifierName;

    private String subFlowConfig;

    private String activityType;

    private String auditType;

    private String remindConfig;

    private String buttonConfig;

    private String otherConfig;
}
