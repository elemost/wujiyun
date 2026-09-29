package com.wuji.workflow.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wuji.workflow.model.domain.AssigneeDomain;
import com.wuji.workflow.model.domain.CandidateDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class FlowableOperateLogVO {
    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected String id;

    @ApiModelProperty("操作类型")
    private String operate;

    private String operateName;

    @ApiModelProperty("节点key")
    private String taskKey;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    private String activityName;

    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    /**
     * 创建时间
     */
    protected Date createTime;

    @ApiModelProperty("创建人")
    private String creator;

    private String creatorName;

    @ApiModelProperty("评论")
    private String comment;

    private String duration;

    private Boolean end;

    private String activityType;

    private String auditType;

    private List<AssigneeDomain> assigneeList = new ArrayList<>();
    /**
     * 候选执行人
     */
    private List<CandidateDomain> candidateList = new ArrayList<>();
}
