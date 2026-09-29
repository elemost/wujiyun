package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.SsoLoginConfigEntity;
import com.wuji.admin.model.request.SsoLoginConfigSaveRequest;
import com.wuji.admin.model.vo.SsoLoginConfigInfoVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2026-09-16
 */
public interface SsoLoginConfigService extends IService<SsoLoginConfigEntity> {
    Long saveOrUpdate(SsoLoginConfigSaveRequest ssoLoginConfigSaveRequest);

    SsoLoginConfigInfoVO info(String configType);
}
