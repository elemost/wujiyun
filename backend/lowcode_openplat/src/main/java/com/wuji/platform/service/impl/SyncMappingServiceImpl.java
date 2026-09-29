package com.wuji.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.platform.converter.AbstractSyncMappingConverter;
import com.wuji.platform.mapper.SyncMappingMapper;
import com.wuji.platform.model.entity.SyncMappingEntity;
import com.wuji.platform.model.request.SyncMappingSaveRequest;
import com.wuji.platform.model.vo.SyncMappingVO;
import com.wuji.platform.service.SyncMappingService;
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
public class SyncMappingServiceImpl extends ServiceImpl<SyncMappingMapper, SyncMappingEntity>
        implements SyncMappingService {

    @Autowired
    private SyncMappingMapper syncMappingMapper;

    @Override
    public SyncMappingVO info(String applicationId, String formId) {
        LambdaQueryWrapper<SyncMappingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SyncMappingEntity::getApplicationId, applicationId);
        queryWrapper.eq(SyncMappingEntity::getFormId, formId);
        SyncMappingEntity exist = syncMappingMapper.selectOne(queryWrapper);
        return AbstractSyncMappingConverter.INSTANCE.toVO(exist);
    }

    @Override
    public void save(SyncMappingSaveRequest syncMappingSaveRequest) {
        UserDomain user = UserUtils.getUser();
        LambdaQueryWrapper<SyncMappingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SyncMappingEntity::getApplicationId, syncMappingSaveRequest.getApplicationId());
        queryWrapper.eq(SyncMappingEntity::getFormId, syncMappingSaveRequest.getFormId());
        SyncMappingEntity exist = syncMappingMapper.selectOne(queryWrapper);
        if (exist == null) {
            SyncMappingEntity saveSyncMapping = AbstractSyncMappingConverter.INSTANCE.toEntity(syncMappingSaveRequest);
            saveSyncMapping.setMappingConfig(syncMappingSaveRequest.getMappingConfig());
            saveSyncMapping.setFormId(syncMappingSaveRequest.getFormId());
            saveSyncMapping.setApplicationId(syncMappingSaveRequest.getApplicationId());
            saveSyncMapping.setCreator(user.getNickName());
            saveSyncMapping.setModifier(user.getNickName());
            saveSyncMapping.setId(ObjectId.getGuid());
            syncMappingMapper.insert(saveSyncMapping);
        } else {
            SyncMappingEntity saveSyncMapping = new SyncMappingEntity();
            saveSyncMapping.setMappingConfig(syncMappingSaveRequest.getMappingConfig());
            saveSyncMapping.setId(exist.getId());
            saveSyncMapping.setModifier(user.getNickName());
            syncMappingMapper.updateById(saveSyncMapping);
        }
    }
}
