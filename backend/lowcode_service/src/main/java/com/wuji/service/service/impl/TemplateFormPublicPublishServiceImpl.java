package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.ObjectId;
import com.wuji.service.converter.AbstractTemplateFormPublicPublishConverter;
import com.wuji.service.mapper.TemplateFormPublicPublishMapper;
import com.wuji.service.model.entity.TemplateFormPublicPublishEntity;
import com.wuji.service.model.vo.FormPublicPublishVO;
import com.wuji.service.model.vo.TemplateFormPublicPublishVO;
import com.wuji.service.service.FormPublicPublishService;
import com.wuji.service.service.TemplateFormPublicPublishService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-02-24
 */
@Service
public class TemplateFormPublicPublishServiceImpl
        extends ServiceImpl<TemplateFormPublicPublishMapper, TemplateFormPublicPublishEntity>
        implements TemplateFormPublicPublishService {

    @Autowired
    private FormPublicPublishService formPublicPublishService;

    @Autowired
    private TemplateFormPublicPublishMapper templateFormPublicPublishMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateTemplate(String applicationId, String templateApplicationId, Boolean exist) {
        if (exist) {
            LambdaQueryWrapper<TemplateFormPublicPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TemplateFormPublicPublishEntity::getApplicationId, templateApplicationId);
            templateFormPublicPublishMapper.delete(queryWrapper);
        }
        List<FormPublicPublishVO> formPublicPublishVOS = formPublicPublishService.getByApplicationId(applicationId);
        if (CollectionUtils.isEmpty(formPublicPublishVOS)) {
            return;
        }
        List<TemplateFormPublicPublishEntity> templateFormPublicPublishEntityList = new ArrayList<>();
        for (FormPublicPublishVO formPublicPublishVO : formPublicPublishVOS) {
            TemplateFormPublicPublishEntity templateFormPublicPublishEntity =
                    AbstractTemplateFormPublicPublishConverter.INSTANCE.toEntity(formPublicPublishVO);
            templateFormPublicPublishEntity.setId(ObjectId.getGuid());
            templateFormPublicPublishEntity.setApplicationId(templateApplicationId);
            templateFormPublicPublishEntityList.add(templateFormPublicPublishEntity);
        }
        saveBatch(templateFormPublicPublishEntityList);
    }

    @Override
    public List<TemplateFormPublicPublishVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<TemplateFormPublicPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateFormPublicPublishEntity::getApplicationId, applicationId);
        List<TemplateFormPublicPublishEntity> templateFormPublicPublishEntityList =
                templateFormPublicPublishMapper.selectList(queryWrapper);
        return templateFormPublicPublishEntityList.stream()
                .map(AbstractTemplateFormPublicPublishConverter.INSTANCE::toVO).collect(Collectors.toList());
    }
}
