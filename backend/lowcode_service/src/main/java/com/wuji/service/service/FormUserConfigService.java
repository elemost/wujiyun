package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormUserConfigEntity;
import com.wuji.service.model.request.FormUserConfigSaveRequest;
import com.wuji.service.model.vo.FormUserConfigVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-04-18
 */
public interface FormUserConfigService extends IService<FormUserConfigEntity> {
    void save(FormUserConfigSaveRequest formUserConfigSaveRequest);

    FormUserConfigVO getInfo(String formId, String applicationId, String configType);
}
