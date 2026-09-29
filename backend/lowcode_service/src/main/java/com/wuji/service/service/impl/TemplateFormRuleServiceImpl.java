package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.service.converter.AbstractTemplateFormRuleConverter;
import com.wuji.service.mapper.TemplateFormRuleMapper;
import com.wuji.service.model.entity.TemplateFormRuleEntity;
import com.wuji.service.model.vo.FormRuleVO;
import com.wuji.service.model.vo.TemplateFormRuleVO;
import com.wuji.service.service.FormRuleService;
import com.wuji.service.service.TemplateFormRuleService;
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
 * @since 2025-12-31
 */
@Service
public class TemplateFormRuleServiceImpl extends ServiceImpl<TemplateFormRuleMapper, TemplateFormRuleEntity>
        implements TemplateFormRuleService {

    @Autowired
    private FormRuleService formRuleService;

    @Autowired
    private TemplateFormRuleMapper templateFormRuleMapper;

    @Override
    public void generateTemplate(String sourceApplicationId, String templateApplicationId, Boolean exist) {
        if (exist) {
            LambdaQueryWrapper<TemplateFormRuleEntity> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(TemplateFormRuleEntity::getApplicationId, templateApplicationId);
            templateFormRuleMapper.delete(deleteWrapper);
        }
        List<FormRuleVO> formRuleVOS = formRuleService.queryListByApplication(sourceApplicationId);
        if (CollectionUtils.isEmpty(formRuleVOS)) {
            return;
        }
        List<TemplateFormRuleEntity> templateFormRuleEntityList = new ArrayList<>();
        for (FormRuleVO formRuleVO : formRuleVOS) {
            TemplateFormRuleEntity ruleEntity = AbstractTemplateFormRuleConverter.INSTANCE.toEntity(formRuleVO);
            ruleEntity.setApplicationId(templateApplicationId);
            templateFormRuleEntityList.add(ruleEntity);
        }
        saveBatch(templateFormRuleEntityList);
    }

    @Override
    public List<TemplateFormRuleVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<TemplateFormRuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateFormRuleEntity::getApplicationId, applicationId);
        queryWrapper.orderByDesc(TemplateFormRuleEntity::getSort);
        queryWrapper.orderByAsc(TemplateFormRuleEntity::getId);
        return templateFormRuleMapper.selectList(queryWrapper).stream()
                .map(AbstractTemplateFormRuleConverter.INSTANCE::toVO).collect(Collectors.toList());
    }
}
