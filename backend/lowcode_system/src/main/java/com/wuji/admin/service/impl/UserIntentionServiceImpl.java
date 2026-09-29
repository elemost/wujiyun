package com.wuji.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.mapper.UserIntentionMapper;
import com.wuji.admin.model.entity.UserIntentionEntity;
import com.wuji.admin.model.request.UserIntentionRequest;
import com.wuji.admin.service.UserIntentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户意向 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-05-14
 */
@Service
public class UserIntentionServiceImpl extends ServiceImpl<UserIntentionMapper, UserIntentionEntity>
        implements UserIntentionService {

    @Autowired
    private UserIntentionMapper userIntentionMapper;

    @Override
    public void save(UserIntentionRequest userIntentionRequest) {

    }
}
