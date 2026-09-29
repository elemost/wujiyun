package com.wuji.workflow.model.vo;

import com.wuji.workflow.model.domain.AssigneeDomain;
import com.wuji.workflow.model.domain.CandidateDomain;
import lombok.Data;
import org.flowable.engine.task.Comment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ProcessNodeVO {
    /**
     * 流程ID
     */
    private String procDefId;
    /**
     * 活动ID
     */
    private String activityId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 活动类型
     */
    private String activityType;

    private String auditType;
    /**
     * 活动耗时
     */
    private String duration;
    /**
     * 执行人Id
     */
    private Long assigneeId;
    /**
     * 执行人名称
     */
    private String assigneeName;

    private List<AssigneeDomain> assigneeList = new ArrayList<>();
    /**
     * 候选执行人
     */
    private List<CandidateDomain> candidateList = new ArrayList<>();
    /**
     * 任务意见
     */
    private List<Comment> commentList = new ArrayList<>();
    /**
     * 创建时间
     */
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 结束时间
     */
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Boolean end;

    private String elementOperation;
}
