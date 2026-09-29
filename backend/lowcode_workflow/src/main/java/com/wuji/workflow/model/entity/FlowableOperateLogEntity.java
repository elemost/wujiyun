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
 *
 * </p>
 *
 * @author hzm
 * @since 2024-11-25
 */
@Getter
@Setter
@TableName("lc_flowable_operate_log")
@ApiModel(value = "FlowableOperateLogEntity对象", description = "")
public class FlowableOperateLogEntity {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected String id;

    @ApiModelProperty("操作类型")
    private String operate;

    @ApiModelProperty("节点key")
    private String taskKey;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    /**
     * 创建时间
     */
    protected Date createTime;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("评论")
    private String comment;

    private String duration;

    private Date taskCreateTime;
}
