package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.wuji.service.converter.AbstractTemplateApplicationTagConverter;
import com.wuji.service.enums.TemplateApplicationTagTypeEnum;
import com.wuji.service.mapper.TemplateApplicationTagMapper;
import com.wuji.service.model.entity.TemplateApplicationTagEntity;
import com.wuji.service.model.request.TemplateApplicationTagRequest;
import com.wuji.service.model.vo.TemplateApplicationTagVO;
import com.wuji.service.service.TemplateApplicationTagService;
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
 * @since 2025-03-03
 */
@Service
public class TemplateApplicationTagServiceImpl
        extends ServiceImpl<TemplateApplicationTagMapper, TemplateApplicationTagEntity>
        implements TemplateApplicationTagService {

    @Autowired
    private TemplateApplicationTagMapper templateApplicationTagMapper;

    @Override
    public List<TemplateApplicationTagVO> getByApplicationIdList(List<String> applicationIdList) {
        if (CollectionUtils.isEmpty(applicationIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<TemplateApplicationTagEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TemplateApplicationTagEntity::getApplicationId, applicationIdList);
        queryWrapper.in(TemplateApplicationTagEntity::getTagType,
                Lists.newArrayList(TemplateApplicationTagTypeEnum.TAG.name(),
                        TemplateApplicationTagTypeEnum.HOT_TAG.name()));
        List<TemplateApplicationTagEntity> templateApplicationTagEntities =
                templateApplicationTagMapper.selectList(queryWrapper);
        return templateApplicationTagEntities.stream().map(AbstractTemplateApplicationTagConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAll(List<TemplateApplicationTagRequest> templateApplicationTagEntityList,
                        List<String> applicationIdList) {
        List<TemplateApplicationTagEntity> templateApplicationTagEntities = templateApplicationTagEntityList.stream()
                .map(AbstractTemplateApplicationTagConverter.INSTANCE::toEntity).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(applicationIdList)) {
            return;
        }
        LambdaQueryWrapper<TemplateApplicationTagEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.in(TemplateApplicationTagEntity::getApplicationId, applicationIdList);
        templateApplicationTagMapper.delete(deleteWrapper);
        saveBatch(templateApplicationTagEntities);
    }
}
