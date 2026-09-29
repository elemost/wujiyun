package com.wuji.quartz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.quartz.constant.ScheduleConstants;
import com.wuji.quartz.converter.AbstractJobConverter;
import com.wuji.quartz.mapper.JobMapper;
import com.wuji.quartz.model.entity.JobEntity;
import com.wuji.quartz.model.request.JobRequest;
import com.wuji.quartz.service.JobService;
import com.wuji.quartz.util.ScheduleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * <p>
 * 定时任务调度表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-04-28
 */
@Service
@Slf4j
public class JobServiceImpl extends ServiceImpl<JobMapper, JobEntity> implements JobService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobMapper jobMapper;

    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     */
    @PostConstruct
    public void init() throws SchedulerException {
        scheduler.clear();
        LambdaQueryWrapper<JobEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobEntity::getDeleted, Boolean.FALSE);
        List<JobEntity> jobList = jobMapper.selectList(queryWrapper);
        for (JobEntity job : jobList) {
            if (StringUtils.isEmpty(job.getCronExpression())) {
                continue;
            }
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
    }

    @Override
    public void pauseJob(String businessId, String businessType) {
        LambdaQueryWrapper<JobEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobEntity::getBusinessId, businessId);
        queryWrapper.eq(JobEntity::getJobGroup, businessType);
        queryWrapper.eq(JobEntity::getDeleted, Boolean.FALSE);
        JobEntity job = jobMapper.selectOne(queryWrapper);
        Long jobId = job.getId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = jobMapper.updateById(job);
        if (rows > 0) {
            try {
                scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
            } catch (Exception e) {
                log.error("暂停job失败", e);
            }
        }
    }

    @Override
    public int resumeJob(String businessId, String businessType) {
        LambdaQueryWrapper<JobEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobEntity::getBusinessId, businessId);
        queryWrapper.eq(JobEntity::getJobGroup, businessType);
        queryWrapper.eq(JobEntity::getDeleted, Boolean.FALSE);
        JobEntity job = jobMapper.selectOne(queryWrapper);
        Long jobId = job.getId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        int rows = jobMapper.updateById(job);
        if (rows > 0) {
            try {
                scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
            } catch (Exception e) {
                log.error("重启job失败", e);
            }
            JobEntity properties = jobMapper.selectById(job.getId());
            try {
                updateSchedulerJob(job, properties.getJobGroup());
            } catch (Exception e) {
                log.error("重启job失败", e);
            }
        }
        return rows;
    }

    @Override
    public void deleteJob(String businessId, String businessType, Boolean realDeleted) {
        LambdaQueryWrapper<JobEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobEntity::getBusinessId, businessId);
        queryWrapper.eq(JobEntity::getJobGroup, businessType);
        queryWrapper.eq(JobEntity::getDeleted, Boolean.FALSE);
        JobEntity job = jobMapper.selectOne(queryWrapper);
        if (job == null) {
            return;
        }
        Long jobId = job.getId();
        String jobGroup = job.getJobGroup();
        if (realDeleted) {
            jobMapper.deleteById(jobId);
        } else {
            JobEntity delete = new JobEntity();
            delete.setDeleted(Boolean.TRUE);
            jobMapper.update(delete, queryWrapper);
        }
        try {
            scheduler.deleteJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        } catch (Exception e) {
            log.error("删除job失败", e);
        }
    }

    @Override
    public void insertJob(JobRequest job) {
        JobEntity entity = AbstractJobConverter.INSTANCE.toEntity(job);
        entity.setStatus("0");
        int rows = jobMapper.insert(entity);
        if (rows > 0) {
            try {
                ScheduleUtils.createScheduleJob(scheduler, entity);
            } catch (Exception e) {
                log.error("创建定时任务失败", e);
            }
        }
    }

    @Override
    public void run(String businessId, String businessType) {
        LambdaQueryWrapper<JobEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobEntity::getBusinessId, businessId);
        queryWrapper.eq(JobEntity::getJobGroup, businessType);
        queryWrapper.eq(JobEntity::getDeleted, Boolean.FALSE);
        JobEntity job = jobMapper.selectOne(queryWrapper);
        // 参数
        JobDataMap dataMap = new JobDataMap();
        try {
            dataMap.put(ScheduleConstants.TASK_PROPERTIES, job);
            JobKey jobKey = ScheduleUtils.getJobKey(job.getId(), businessType);
            if (scheduler.checkExists(jobKey)) {
                scheduler.triggerJob(jobKey, dataMap);
            }
        } catch (Exception e) {
            log.error("执行失败", e);
        }
    }

    /**
     * 更新任务
     *
     * @param job      任务对象
     * @param jobGroup 任务组名
     */
    public void updateSchedulerJob(JobEntity job, String jobGroup) throws SchedulerException {
        Long jobId = job.getId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(scheduler, job);
    }

}
