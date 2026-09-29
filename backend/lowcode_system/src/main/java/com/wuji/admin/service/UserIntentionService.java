package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.UserIntentionEntity;
import com.wuji.admin.model.request.UserIntentionRequest;

/**
 * <p>
 * 用户意向 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-05-14
 */
public interface UserIntentionService extends IService<UserIntentionEntity> {
    void save(UserIntentionRequest userIntentionRequest);
}
