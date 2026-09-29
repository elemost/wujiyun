package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractFormDataFactoryPublishConverter;
import com.wuji.service.mapper.FormDataFactoryPublishMapper;
import com.wuji.service.model.entity.FormDataFactoryPublishEntity;
import com.wuji.service.model.request.FormDataFactoryPublishRequest;
import com.wuji.service.model.vo.FormDataFactoryPublishVO;
import com.wuji.service.service.FormDataFactoryPublishService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2026-01-17
 */
@Service
public class FormDataFactoryPublishServiceImpl
        extends ServiceImpl<FormDataFactoryPublishMapper, FormDataFactoryPublishEntity>
        implements FormDataFactoryPublishService {

    @Autowired
    private FormDataFactoryPublishMapper formDataFactoryPublishMapper;

    @Override
    public void publish(FormDataFactoryPublishRequest formDataFactoryPublishRequest) {
        LambdaQueryWrapper<FormDataFactoryPublishEntity> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(FormDataFactoryPublishEntity::getId, formDataFactoryPublishRequest.getId());
        updateWrapper.eq(FormDataFactoryPublishEntity::getApplicationId,
                formDataFactoryPublishRequest.getApplicationId());
        FormDataFactoryPublishEntity update = new FormDataFactoryPublishEntity();
        update.setLastVersion(Boolean.FALSE);
        formDataFactoryPublishMapper.update(update, updateWrapper);
        FormDataFactoryPublishEntity formDataFactoryPublishEntity =
                AbstractFormDataFactoryPublishConverter.INSTANCE.toEntity(formDataFactoryPublishRequest);
        formDataFactoryPublishEntity.setLastVersion(Boolean.TRUE);
        formDataFactoryPublishEntity.setCreator(UserUtils.getUser().getNickName());
        formDataFactoryPublishEntity.setModifier(UserUtils.getUser().getNickName());
        formDataFactoryPublishMapper.insert(formDataFactoryPublishEntity);
    }

    @Override
    public void batchPublish(List<FormDataFactoryPublishRequest> formDataFactoryPublishRequests) {
        List<FormDataFactoryPublishEntity> formDataFactoryPublishList = new ArrayList<>();
        for (FormDataFactoryPublishRequest formDataFactoryPublishRequest : formDataFactoryPublishRequests) {
            FormDataFactoryPublishEntity formDataFactoryPublishEntity =
                    AbstractFormDataFactoryPublishConverter.INSTANCE.toEntity(formDataFactoryPublishRequest);
            formDataFactoryPublishEntity.setLastVersion(Boolean.TRUE);
            formDataFactoryPublishEntity.setCreator(UserUtils.getUser().getNickName());
            formDataFactoryPublishEntity.setModifier(UserUtils.getUser().getNickName());
            formDataFactoryPublishList.add(formDataFactoryPublishEntity);
        }
        saveBatch(formDataFactoryPublishList);
    }

    @Override
    public void updateName(String id, String applicationId, String factoryName) {
        LambdaQueryWrapper<FormDataFactoryPublishEntity> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(FormDataFactoryPublishEntity::getId, id);
        updateWrapper.eq(FormDataFactoryPublishEntity::getApplicationId, applicationId);
        FormDataFactoryPublishEntity formDataFactoryPublishEntity = new FormDataFactoryPublishEntity();
        formDataFactoryPublishEntity.setFactoryName(factoryName);
        formDataFactoryPublishMapper.update(formDataFactoryPublishEntity, updateWrapper);
    }

    @Override
    public FormDataFactoryPublishVO getLastVersion(String applicationId, String id) {
        LambdaQueryWrapper<FormDataFactoryPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataFactoryPublishEntity::getId, id);
        queryWrapper.eq(FormDataFactoryPublishEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormDataFactoryPublishEntity::getLastVersion, Boolean.TRUE);
        FormDataFactoryPublishEntity formDataFactoryPublishEntity =
                formDataFactoryPublishMapper.selectOne(queryWrapper);
        return AbstractFormDataFactoryPublishConverter.INSTANCE.toVO(formDataFactoryPublishEntity);
    }

    @Override
    public void delete(String applicationId, String id) {
        LambdaQueryWrapper<FormDataFactoryPublishEntity> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(FormDataFactoryPublishEntity::getId, id);
        updateWrapper.eq(FormDataFactoryPublishEntity::getApplicationId, applicationId);
        FormDataFactoryPublishEntity update = new FormDataFactoryPublishEntity();
        update.setDeleted(Boolean.TRUE);
        update.setModifier(UserUtils.getUser().getNickName());
        formDataFactoryPublishMapper.update(update, updateWrapper);
    }

    @Override
    public List<FormDataFactoryPublishVO> queryPublishList(String applicationId, List<String> ids) {
        LambdaQueryWrapper<FormDataFactoryPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataFactoryPublishEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormDataFactoryPublishEntity::getLastVersion, Boolean.TRUE);
        queryWrapper.eq(FormDataFactoryPublishEntity::getDeleted, Boolean.FALSE);
        queryWrapper.in(CollectionUtils.isNotEmpty(ids), FormDataFactoryPublishEntity::getId, ids);
        List<FormDataFactoryPublishEntity> formDataFactoryPublishEntities =
                formDataFactoryPublishMapper.selectList(queryWrapper);
        return formDataFactoryPublishEntities.stream().map(AbstractFormDataFactoryPublishConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }
}
