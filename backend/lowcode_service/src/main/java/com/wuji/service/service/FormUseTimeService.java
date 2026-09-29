package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormUseTimeEntity;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-11-29
 */
public interface FormUseTimeService extends IService<FormUseTimeEntity> {
    void save(String formId, String applicationId);

    List<FormUseTimeEntity> getLatestForm(Integer limitCount);

    void deleteByForm(String formId, String applicationId);
}
