package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.service.converter.AbstractFormDataFactoryInputConverter;
import com.wuji.service.mapper.FormDataFactoryInputMapper;
import com.wuji.service.model.entity.FormDataFactoryInputEntity;
import com.wuji.service.model.request.FormDataFactoryInputRequest;
import com.wuji.service.service.FormDataFactoryInputService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2026-02-11
 */
@Service
public class FormDataFactoryInputServiceImpl extends ServiceImpl<FormDataFactoryInputMapper, FormDataFactoryInputEntity>
        implements FormDataFactoryInputService {


    @Override
    public void saveBatch(List<FormDataFactoryInputRequest> factoryInputs) {
        if (CollectionUtils.isEmpty(factoryInputs)) {
            return;
        }
        List<FormDataFactoryInputEntity> formDataFactoryInputEntities =
                factoryInputs.stream().map(AbstractFormDataFactoryInputConverter.INSTANCE::toEntity)
                        .collect(Collectors.toList());
        saveBatch(formDataFactoryInputEntities);
    }
}
