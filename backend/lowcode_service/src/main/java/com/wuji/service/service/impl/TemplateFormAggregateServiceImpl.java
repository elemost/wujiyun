package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractTemplateFormAggregateConverter;
import com.wuji.service.mapper.TemplateFormAggregateMapper;
import com.wuji.service.model.entity.TemplateFormAggregateEntity;
import com.wuji.service.model.vo.FormAggregateVO;
import com.wuji.service.model.vo.TemplateFormAggregateVO;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.service.TemplateFormAggregateService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 聚合表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
@Service
public class TemplateFormAggregateServiceImpl
        extends ServiceImpl<TemplateFormAggregateMapper, TemplateFormAggregateEntity>
        implements TemplateFormAggregateService {

    @Autowired
    private FormAggregateService formAggregateService;

    @Autowired
    private TemplateFormAggregateMapper templateFormAggregateMapper;

    @Override
    public void generateTemplate(String applicationId, String templateApplicationId, Boolean exist) {
        if (exist) {
            LambdaQueryWrapper<TemplateFormAggregateEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TemplateFormAggregateEntity::getApplicationId, templateApplicationId);
            templateFormAggregateMapper.delete(queryWrapper);
        }
        List<FormAggregateVO> formAggregateVOList = formAggregateService.getByApplicationId(applicationId);
        if (CollectionUtils.isEmpty(formAggregateVOList)) {
            return;
        }
        List<TemplateFormAggregateEntity> templateFormAggregateEntityList = new ArrayList<>();
        for (FormAggregateVO formAggregateVO : formAggregateVOList) {
            TemplateFormAggregateEntity templateFormAggregateEntity =
                    AbstractTemplateFormAggregateConverter.INSTANCE.toEntity(formAggregateVO);
            templateFormAggregateEntity.setModifier(UserUtils.getUser().getNickName());
            templateFormAggregateEntity.setCreator(UserUtils.getUser().getNickName());
            templateFormAggregateEntity.setApplicationId(templateApplicationId);
            templateFormAggregateEntityList.add(templateFormAggregateEntity);
        }
        saveBatch(templateFormAggregateEntityList);
    }

    @Override
    public List<TemplateFormAggregateVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<TemplateFormAggregateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateFormAggregateEntity::getApplicationId, applicationId);
        List<TemplateFormAggregateEntity> records = templateFormAggregateMapper.selectList(queryWrapper);
        List<TemplateFormAggregateVO> formAggregateVOList = new ArrayList<>();
        for (TemplateFormAggregateEntity templateFormAggregateEntity : records) {
            TemplateFormAggregateVO formAggregateVO =
                    AbstractTemplateFormAggregateConverter.INSTANCE.toVO(templateFormAggregateEntity);
            formAggregateVOList.add(formAggregateVO);
        }
        return formAggregateVOList;
    }
}
