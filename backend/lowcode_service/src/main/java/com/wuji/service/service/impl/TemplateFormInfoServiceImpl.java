package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractTemplateFormInfoConverter;
import com.wuji.service.mapper.TemplateFormInfoMapper;
import com.wuji.service.model.entity.TemplateFormInfoEntity;
import com.wuji.service.model.vo.FormInfoVO;
import com.wuji.service.model.vo.TemplateFormInfoVO;
import com.wuji.service.service.FormInfoService;
import com.wuji.service.service.TemplateFormInfoService;
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
 * @since 2025-09-09
 */
@Service
public class TemplateFormInfoServiceImpl extends ServiceImpl<TemplateFormInfoMapper, TemplateFormInfoEntity>
        implements TemplateFormInfoService {

    @Autowired
    private FormInfoService formInfoService;

    @Autowired
    private TemplateFormInfoMapper templateFormInfoMapper;

    @Override
    public void generateTemplate(String sourceApplicationId, String templateApplicationId, Boolean exist) {
        if (exist) {
            LambdaQueryWrapper<TemplateFormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TemplateFormInfoEntity::getApplicationId, templateApplicationId);
            templateFormInfoMapper.delete(queryWrapper);
        }
        List<FormInfoVO> formInfoVOS = formInfoService.queryByApplicationId(sourceApplicationId);
        if (CollectionUtils.isEmpty(formInfoVOS)) {
            return;
        }
        List<TemplateFormInfoEntity> templateFormInfoEntityList = new ArrayList<>();
        for (FormInfoVO formInfoVO : formInfoVOS) {
            TemplateFormInfoEntity templateFormInfoEntity =
                    AbstractTemplateFormInfoConverter.INSTANCE.toEntity(formInfoVO);
            templateFormInfoEntity.setApplicationId(templateApplicationId);
            templateFormInfoEntity.setCreatorName(UserUtils.getUser().getNickName());
            templateFormInfoEntity.setModifierName(UserUtils.getUser().getNickName());
            templateFormInfoEntityList.add(templateFormInfoEntity);
        }
        saveBatch(templateFormInfoEntityList);
    }

    @Override
    public List<TemplateFormInfoVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<TemplateFormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateFormInfoEntity::getApplicationId, applicationId);
        return templateFormInfoMapper.selectList(queryWrapper).stream()
                .map(AbstractTemplateFormInfoConverter.INSTANCE::toVO).collect(Collectors.toList());
    }
}
