package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.MainCompanyEntity;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-11-10
 */
public interface MainCompanyService extends IService<MainCompanyEntity> {
    Long checkAndSave(String companyName);
}
