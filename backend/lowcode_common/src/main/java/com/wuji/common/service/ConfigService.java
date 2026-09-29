package com.wuji.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.common.model.entity.ConfigEntity;
import com.wuji.common.model.request.ConfigSaveRequest;
import com.wuji.common.model.vo.ConfigVO;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2024-05-09
 */
public interface ConfigService extends IService<ConfigEntity> {

    void saveOrUpdate(ConfigSaveRequest configSaveRequest);

    ConfigVO detailById(Long id);

    ConfigVO detailByKey(String configKey);

    void init();
}
