package com.wuji.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.platform.model.entity.SecretLogEntity;
import com.wuji.platform.model.request.SecretLogSaveRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-08-07
 */
public interface SecretLogService extends IService<SecretLogEntity> {
    void save(SecretLogSaveRequest secretLogSaveRequest);
}
