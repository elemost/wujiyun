package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormDataFactoryInputEntity;
import com.wuji.service.model.request.FormDataFactoryInputRequest;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2026-02-11
 */
public interface FormDataFactoryInputService extends IService<FormDataFactoryInputEntity> {
    void saveBatch(List<FormDataFactoryInputRequest> factoryInputs);
}
