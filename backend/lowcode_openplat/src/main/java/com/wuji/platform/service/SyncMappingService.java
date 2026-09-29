package com.wuji.platform.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.platform.model.entity.SyncMappingEntity;
import com.wuji.platform.model.request.SyncMappingSaveRequest;
import com.wuji.platform.model.vo.SyncMappingVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-08-07
 */
public interface SyncMappingService extends IService<SyncMappingEntity> {
    SyncMappingVO info(String applicationId, String formId);

    void save(SyncMappingSaveRequest syncMappingSaveRequest);
}
