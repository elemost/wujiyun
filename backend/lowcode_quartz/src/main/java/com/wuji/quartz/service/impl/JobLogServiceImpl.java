package com.wuji.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.quartz.mapper.JobLogMapper;
import com.wuji.quartz.model.entity.JobLogEntity;
import com.wuji.quartz.service.JobLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定时任务调度日志表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-04-28
 */
@Service
public class JobLogServiceImpl extends ServiceImpl<JobLogMapper, JobLogEntity> implements JobLogService {

}
