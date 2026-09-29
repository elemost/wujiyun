package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractTemplateFormDataStreamConverter;
import com.wuji.service.mapper.TemplateFormDataStreamMapper;
import com.wuji.service.model.entity.TemplateFormDataStreamEntity;
import com.wuji.service.model.vo.FormDataStreamPublishVO;
import com.wuji.service.model.vo.TemplateFormDataStreamVO;
import com.wuji.service.service.FormDataStreamPublishService;
import com.wuji.service.service.TemplateFormDataStreamService;
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
 * @since 2025-04-09
 */
@Service
public class TemplateFormDataStreamServiceImpl
        extends ServiceImpl<TemplateFormDataStreamMapper, TemplateFormDataStreamEntity>
        implements TemplateFormDataStreamService {

    @Autowired
    private FormDataStreamPublishService formDataStreamPublishService;

    @Autowired
    private TemplateFormDataStreamMapper templateFormDataStreamMapper;

    @Override
    public void generateTemplate(String applicationId, String templateApplicationId, List<String> formIdList, Boolean exist) {
        if (exist) {
            LambdaQueryWrapper<TemplateFormDataStreamEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TemplateFormDataStreamEntity::getApplicationId, templateApplicationId);
            templateFormDataStreamMapper.delete(queryWrapper);
        }
        List<FormDataStreamPublishVO> formDataStreamPublishVOS =
                formDataStreamPublishService.getByApplicationIdList(applicationId);
        if (CollectionUtils.isEmpty(formDataStreamPublishVOS)) {
            return;
        }
        List<TemplateFormDataStreamEntity> templateFormDataStreamEntityList = new ArrayList<>();
        for (FormDataStreamPublishVO formDataStreamPublishVO : formDataStreamPublishVOS) {
            TemplateFormDataStreamEntity templateFormDataStreamEntity =
                    AbstractTemplateFormDataStreamConverter.INSTANCE.toEntity(formDataStreamPublishVO);
            templateFormDataStreamEntity.setApplicationId(templateApplicationId);
            templateFormDataStreamEntity.setModifier(UserUtils.getUser().getNickName());
            templateFormDataStreamEntity.setCreator(UserUtils.getUser().getNickName());
            templateFormDataStreamEntity.setVersion(1);
            templateFormDataStreamEntityList.add(templateFormDataStreamEntity);
        }
        saveBatch(templateFormDataStreamEntityList);
    }

    @Override
    public List<TemplateFormDataStreamVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<TemplateFormDataStreamEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateFormDataStreamEntity::getApplicationId, applicationId);
        queryWrapper.eq(TemplateFormDataStreamEntity::getDeleted, Boolean.FALSE);
        List<TemplateFormDataStreamEntity> formDataStreamEntityList =
                templateFormDataStreamMapper.selectList(queryWrapper);
        return formDataStreamEntityList.stream().map(AbstractTemplateFormDataStreamConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }
}
