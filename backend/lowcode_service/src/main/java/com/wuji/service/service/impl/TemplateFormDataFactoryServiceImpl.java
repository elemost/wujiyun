package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.service.converter.AbstractTemplateFormDataFactoryConverter;
import com.wuji.service.mapper.TemplateFormDataFactoryMapper;
import com.wuji.service.model.entity.TemplateFormDataFactoryEntity;
import com.wuji.service.model.vo.FormDataFactoryPublishVO;
import com.wuji.service.model.vo.FormDataFactoryVO;
import com.wuji.service.model.vo.TemplateFormDataFactoryVO;
import com.wuji.service.service.FormDataFactoryPublishService;
import com.wuji.service.service.FormDataFactoryService;
import com.wuji.service.service.TemplateFormDataFactoryService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2026-02-03
 */
@Service
public class TemplateFormDataFactoryServiceImpl
        extends ServiceImpl<TemplateFormDataFactoryMapper, TemplateFormDataFactoryEntity>
        implements TemplateFormDataFactoryService {

    @Autowired
    private FormDataFactoryPublishService formDataFactoryPublishService;

    @Autowired
    private TemplateFormDataFactoryMapper templateFormDataFactoryMapper;

    @Autowired
    private FormDataFactoryService formDataFactoryService;

    @Override
    public void generateTemplate(String applicationId, String templateApplicationId, Boolean exist) {
        if (exist) {
            LambdaQueryWrapper<TemplateFormDataFactoryEntity> delete = new LambdaQueryWrapper<>();
            delete.eq(TemplateFormDataFactoryEntity::getApplicationId, templateApplicationId);
            templateFormDataFactoryMapper.delete(delete);
        }
        List<FormDataFactoryPublishVO> formDataFactoryPublishVOS =
                formDataFactoryPublishService.queryPublishList(applicationId, null);
        if (CollectionUtils.isEmpty(formDataFactoryPublishVOS)) {
            return;
        }
        List<FormDataFactoryVO> formDataFactoryVOS = formDataFactoryService.queryByIds(applicationId, null);
        Map<String, FormDataFactoryVO> idToDataFactoryMap =
                formDataFactoryVOS.stream().collect(Collectors.toMap(FormDataFactoryVO::getId, c -> c));
        List<TemplateFormDataFactoryEntity> formDataFactoryEntities = new ArrayList<>();
        for (FormDataFactoryPublishVO formDataFactoryPublishVO : formDataFactoryPublishVOS) {
            TemplateFormDataFactoryEntity formDataFactoryEntity =
                    AbstractTemplateFormDataFactoryConverter.INSTANCE.toEntity(formDataFactoryPublishVO);
            FormDataFactoryVO formDataFactoryVO = idToDataFactoryMap.get(formDataFactoryPublishVO.getId());
            if (formDataFactoryVO != null) {
                formDataFactoryEntity.setSyncConfig(formDataFactoryVO.getSyncConfig());
                formDataFactoryEntity.setSyncConfigError(formDataFactoryVO.getSyncConfigError());
            }
            formDataFactoryEntity.setVersion(1);
            formDataFactoryEntity.setCreator(null);
            formDataFactoryEntity.setModifier(null);
            formDataFactoryEntity.setApplicationId(templateApplicationId);
            formDataFactoryEntities.add(formDataFactoryEntity);
        }
        saveBatch(formDataFactoryEntities);
    }

    @Override
    public List<TemplateFormDataFactoryVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<TemplateFormDataFactoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateFormDataFactoryEntity::getApplicationId, applicationId);
        return templateFormDataFactoryMapper.selectList(queryWrapper).stream()
                .map(AbstractTemplateFormDataFactoryConverter.INSTANCE::toVO).collect(Collectors.toList());
    }
}
