package com.wuji.workflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 抄送表
 * </p>
 *
 * @author hzm
 * @since 2024-09-12
 */
@Getter
@Setter
@TableName("lc_flowable_copy")
@ApiModel(value = "FlowableCopyEntity对象", description = "抄送表")
public class FlowableCopyEntity {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("发起人")
    private String initiator;

    @ApiModelProperty("表单id")
    private String formId;

    @ApiModelProperty("流程业务key")
    private String businessType;

    @ApiModelProperty("模型id")
    private String modelId;

    @ApiModelProperty("任务节点id")
    private String activityId;

    @ApiModelProperty("对应流程")
    private String processDefinitionId;

    @ApiModelProperty("对应流程名称")
    private String processDefinitionName;

    private String applicationId;

    private String dataUuid;
    /**
     * 创建时间
     */
    protected Date createTime;

    private Long companyId;
}
