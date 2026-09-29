package com.wuji.quartz.util;

import com.wuji.quartz.model.entity.JobEntity;
import org.quartz.JobExecutionContext;

/**
 * 定时任务处理（允许并发执行）
 *
 * @author ruoyi
 */
public class QuartzJobExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, JobEntity sysJob) throws Exception {
        JobInvokeUtil.invokeMethod(sysJob);
    }
}
