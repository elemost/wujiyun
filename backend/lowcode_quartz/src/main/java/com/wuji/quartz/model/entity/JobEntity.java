package com.wuji.quartz.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 定时任务调度表
 * </p>
 *
 * @author hzm
 * @since 2025-04-28
 */
@Getter
@Setter
@TableName("lc_job")
@ApiModel(value = "JobEntity对象", description = "定时任务调度表")
public class JobEntity extends BaseEntity {

    @ApiModelProperty("业务id")
    private String businessId;

    @ApiModelProperty("任务名称")
    private String jobName;

    @ApiModelProperty("任务组名")
    private String jobGroup;

    @ApiModelProperty("调用目标字符串")
    private String invokeTarget;

    @ApiModelProperty("cron执行表达式")
    private String cronExpression;

    @ApiModelProperty("计划执行错误策略（1立即执行 2执行一次 3放弃执行）")
    private String misfirePolicy;

    @ApiModelProperty("是否并发执行（0允许 1禁止）")
    private String concurrent;

    @ApiModelProperty("状态（0正常 1暂停）")
    private String status;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("备注信息")
    private String remark;

    private Date startTime;

    private Date endTime;
}
