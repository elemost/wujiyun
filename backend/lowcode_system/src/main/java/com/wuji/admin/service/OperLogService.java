package com.wuji.admin.service;

import com.wuji.admin.model.domain.OperLogDomain;
import com.wuji.admin.model.entity.OperLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 操作日志 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-04-23
 */
public interface OperLogService extends IService<OperLogEntity> {
    void save(OperLogDomain operLogDomain);
}
