package com.wuji.workflow.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class FlowableOperateLogRequest {
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

    private Date taskCreateTime;

    @ApiModelProperty("评论")
    private String comment;
}
