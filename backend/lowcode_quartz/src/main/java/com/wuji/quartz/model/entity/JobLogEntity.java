package com.wuji.quartz.model.entity;

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
 * 定时任务调度日志表
 * </p>
 *
 * @author hzm
 * @since 2025-04-28
 */
@Getter
@Setter
@TableName("lc_job_log")
@ApiModel(value = "JobLogEntity对象", description = "定时任务调度日志表")
public class JobLogEntity {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("任务id")
    private Long jobId;

    @ApiModelProperty("任务名称")
    private String jobName;

    @ApiModelProperty("任务组名")
    private String jobGroup;

    @ApiModelProperty("调用目标字符串")
    private String invokeTarget;

    @ApiModelProperty("日志信息")
    private String jobMessage;

    @ApiModelProperty("执行状态（0正常 1失败）")
    private String status;

    @ApiModelProperty("异常信息")
    private String exceptionInfo;

   private Date startTime;

   private Date endTime;
}
