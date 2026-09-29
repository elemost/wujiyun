package com.wuji.quartz.util;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.constant.Constants;
import com.wuji.common.utils.ExceptionUtil;
import com.wuji.common.utils.ToolSpring;
import com.wuji.quartz.constant.ScheduleConstants;
import com.wuji.quartz.model.entity.JobEntity;
import com.wuji.quartz.model.entity.JobLogEntity;
import com.wuji.quartz.service.JobLogService;
import com.wuji.quartz.service.JobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抽象quartz调用
 *
 * @author ruoyi
 */
public abstract class AbstractQuartzJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class);

    /**
     * 线程本地变量
     */
    private static final ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    private JobService jobService;

    @Override
    public void execute(JobExecutionContext context) {
        Object object = context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES);
        JobEntity jobEntity = JSONObject.parseObject(JSONObject.toJSONString(object), JobEntity.class);
        if (jobService == null) {
            jobService = ToolSpring.getBean(JobService.class);
        }
        jobEntity = jobService.getById(jobEntity.getId());
        try {
            before(context, jobEntity);
            doExecute(context, jobEntity);
            after(context, jobEntity, null);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            after(context, jobEntity, e);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void before(JobExecutionContext context, JobEntity sysJob) {
        threadLocal.set(new Date());
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void after(JobExecutionContext context, JobEntity sysJob, Exception e) {
        Date startTime = threadLocal.get();
        threadLocal.remove();

        final JobLogEntity jobLogEntity = new JobLogEntity();
        jobLogEntity.setJobName(sysJob.getJobName());
        jobLogEntity.setJobGroup(sysJob.getJobGroup());
        jobLogEntity.setInvokeTarget(sysJob.getInvokeTarget());
        jobLogEntity.setStartTime(startTime);
        jobLogEntity.setEndTime(new Date());
        jobLogEntity.setJobId(sysJob.getId());
        long runMs = jobLogEntity.getEndTime().getTime() - jobLogEntity.getStartTime().getTime();
        jobLogEntity.setJobMessage(jobLogEntity.getJobName() + " 总共耗时：" + runMs + "毫秒");
        if (e != null) {
            jobLogEntity.setStatus(Constants.FAIL);
            String errorMsg = ExceptionUtil.getExceptionMessage(e).substring(0, 2000);
            jobLogEntity.setExceptionInfo(errorMsg);
        } else {
            jobLogEntity.setStatus(Constants.SUCCESS);
        }
        ToolSpring.getBean(JobLogService.class).save(jobLogEntity);
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, JobEntity sysJob) throws Exception;
}
