package com.wuji.workflow.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzm
 * @since 2026-06-26
 */
@Getter
@Setter
@TableName("act_ru_execution")
@ApiModel(value = "RuExecutionEntity对象", description = "")
public class RuExecutionEntity {

      private String id;

    private Integer rev;

    private String procInstId;

    private String businessKey;

    private String parentId;

    private String procDefId;

    private String superExec;

    private String rootProcInstId;

    private String actId;

    private Byte isActive;

    private Byte isConcurrent;

    private Byte isScope;

    private Byte isEventScope;

    private Byte isMiRoot;

    private Integer suspensionState;

    private Integer cachedEntState;

    private String tenantId;

    private String name;

    private String startActId;

    private LocalDateTime startTime;

    private String startUserId;

    private LocalDateTime lockTime;

    private String lockOwner;

    private Byte isCountEnabled;

    private Integer evtSubscrCount;

    private Integer taskCount;

    private Integer jobCount;

    private Integer timerJobCount;

    private Integer suspJobCount;

    private Integer deadletterJobCount;

    private Integer externalWorkerJobCount;

    private Integer varCount;

    private Integer idLinkCount;

    private String callbackId;

    private String callbackType;

    private String referenceId;

    private String referenceType;

    private String propagatedStageInstId;

    private String businessStatus;
}
