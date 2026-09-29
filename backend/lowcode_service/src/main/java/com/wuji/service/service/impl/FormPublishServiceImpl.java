package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractFormPublishConverter;
import com.wuji.service.mapper.FormPublishMapper;
import com.wuji.service.model.entity.FormPublishEntity;
import com.wuji.service.model.request.FormPublishPublishRequest;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.FormPublishVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.FormPublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 表单发布表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-08-20
 */
@Service
public class FormPublishServiceImpl extends ServiceImpl<FormPublishMapper, FormPublishEntity>
        implements FormPublishService {

    @Autowired
    private FormPublishMapper formPublishMapper;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(FormPublishPublishRequest formPublishPublishRequest) {
        if (formPublishPublishRequest.getVersion() > 1) {
            LambdaQueryWrapper<FormPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FormPublishEntity::getApplicationId, formPublishPublishRequest.getApplicationId());
            queryWrapper.eq(FormPublishEntity::getId, formPublishPublishRequest.getId());
            FormPublishEntity formPublishEntity = new FormPublishEntity();
            formPublishEntity.setLastVersion(Boolean.FALSE);
            formPublishMapper.update(formPublishEntity, queryWrapper);
        }
        FormPublishEntity formPublishEntity = AbstractFormPublishConverter.INSTANCE.toEntity(formPublishPublishRequest);
        formPublishEntity.setCreator(UserUtils.getUser().getUserId());
        formPublishEntity.setLastVersion(Boolean.TRUE);
        formPublishMapper.insert(formPublishEntity);
    }

    @Override
    public FormPublishVO info(String categoryId, Integer version) {
        LambdaQueryWrapper<FormPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPublishEntity::getId, categoryId);
        if (version == null) {
            queryWrapper.eq(FormPublishEntity::getLastVersion, Boolean.TRUE);
        } else {
            queryWrapper.eq(FormPublishEntity::getVersion, version);
        }
        queryWrapper.last(" limit 1");
        FormPublishEntity formPublishEntity = formPublishMapper.selectOne(queryWrapper);
        ApplicationCategoryVO applicationCategoryVO = applicationCategoryService.info(categoryId, null);
        FormPublishVO formPublishVO = AbstractFormPublishConverter.INSTANCE.toVO(formPublishEntity);
        formPublishVO.setFormName(applicationCategoryVO.getCategoryName());
        formPublishVO.setShowType(applicationCategoryVO.getShowType());
        formPublishVO.setPublished(applicationCategoryVO.getPublished());
        return formPublishVO;
    }

    @Override
    public void updateByCategory(String categoryId, String config) {
        LambdaQueryWrapper<FormPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPublishEntity::getId, categoryId);
        FormPublishEntity formPublishEntity = new FormPublishEntity();
        formPublishEntity.setConfig(config);
        formPublishMapper.update(formPublishEntity, queryWrapper);
    }
}
