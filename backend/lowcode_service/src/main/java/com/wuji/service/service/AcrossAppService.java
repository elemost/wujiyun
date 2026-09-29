package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.AcrossAppEntity;
import com.wuji.service.model.request.AcrossAppSaveRequest;
import com.wuji.service.model.vo.AcrossAppVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-08-22
 */
public interface AcrossAppService extends IService<AcrossAppEntity> {
    void save(List<AcrossAppSaveRequest> acrossAppSaveRequests, String configAppId);

    List<AcrossAppVO> getByAppId(String configAppId);
}
