package com.wuji.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.platform.converter.AbstractSecretLogConverter;
import com.wuji.platform.mapper.SecretLogMapper;
import com.wuji.platform.model.entity.SecretLogEntity;
import com.wuji.platform.model.request.SecretLogSaveRequest;
import com.wuji.platform.service.SecretLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-08-07
 */
@Service
public class SecretLogServiceImpl extends ServiceImpl<SecretLogMapper, SecretLogEntity> implements SecretLogService {

    @Autowired
    private SecretLogMapper secretLogMapper;

    @Override
    public void save(SecretLogSaveRequest secretLogSaveRequest) {
        SecretLogEntity secretLogEntity = AbstractSecretLogConverter.INSTANCE.toEntity(secretLogSaveRequest);
        secretLogMapper.insert(secretLogEntity);
    }
}
