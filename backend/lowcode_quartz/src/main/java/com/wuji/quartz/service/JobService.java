package com.wuji.quartz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.quartz.model.entity.JobEntity;
import com.wuji.quartz.model.request.JobRequest;

/**
 * <p>
 * 定时任务调度表 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-04-28
 */
public interface JobService extends IService<JobEntity> {

    /**
     * 暂停定时任务
     *
     * @param businessId
     * @param businessType
     */
    void pauseJob(String businessId, String businessType);

    /**
     * 重启job
     * @param businessId
     * @param businessType
     * @return
     */
    int resumeJob(String businessId, String businessType);


    void deleteJob(String businessId, String businessType, Boolean realDeleted);

    void insertJob(JobRequest job);

    void run(String businessId, String businessType);
}
