package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractTemplateFormModuleConverter;
import com.wuji.service.mapper.TemplateFormModuleMapper;
import com.wuji.service.model.entity.TemplateFormModuleEntity;
import com.wuji.service.model.vo.FormModuleVO;
import com.wuji.service.model.vo.TemplateFormModuleVO;
import com.wuji.service.service.FormModuleService;
import com.wuji.service.service.TemplateFormModuleService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 表单组件表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-01-21
 */
@Service
public class TemplateFormModuleServiceImpl extends ServiceImpl<TemplateFormModuleMapper, TemplateFormModuleEntity>
        implements TemplateFormModuleService {

    @Autowired
    private FormModuleService formModuleService;

    @Autowired
    private TemplateFormModuleMapper templateFormModuleMapper;

    @Override
    public void generateTemplate(String applicationId, String templateApplicationId, Boolean exist) {
        if (exist) {
            LambdaQueryWrapper<TemplateFormModuleEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TemplateFormModuleEntity::getApplicationId, templateApplicationId);
            templateFormModuleMapper.delete(queryWrapper);
        }
        List<FormModuleVO> formModuleVOList = formModuleService.getByApplicationId(applicationId);
        if (CollectionUtils.isEmpty(formModuleVOList)) {
            return;
        }
        List<TemplateFormModuleEntity> templateFormModuleEntityList = new ArrayList<>();
        for (FormModuleVO formModuleVO : formModuleVOList) {
            TemplateFormModuleEntity templateFormModuleEntity =
                    AbstractTemplateFormModuleConverter.INSTANCE.toEntity(formModuleVO);
            // templateFormModuleEntity.setId(ObjectId.getGuid());
            templateFormModuleEntity.setApplicationId(templateApplicationId);
            templateFormModuleEntity.setCreator(UserUtils.getUser().getNickName());
            templateFormModuleEntity.setModifier(UserUtils.getUser().getNickName());
            templateFormModuleEntityList.add(templateFormModuleEntity);
        }
        saveBatch(templateFormModuleEntityList);
    }

    @Override
    public List<TemplateFormModuleVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<TemplateFormModuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateFormModuleEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(TemplateFormModuleEntity::getApplicationId, applicationId);
        List<TemplateFormModuleEntity> formModuleEntities = templateFormModuleMapper.selectList(queryWrapper);
        List<TemplateFormModuleVO> formModuleVOList = new ArrayList<>();
        for (TemplateFormModuleEntity formModuleEntity : formModuleEntities) {
            TemplateFormModuleVO formModuleVO = AbstractTemplateFormModuleConverter.INSTANCE.toVO(formModuleEntity);
            formModuleVOList.add(formModuleVO);
        }
        return formModuleVOList;
    }
}
