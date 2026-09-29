package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractTemplateFormQuoteConverter;
import com.wuji.service.mapper.TemplateFormQuoteMapper;
import com.wuji.service.model.entity.TemplateFormQuoteEntity;
import com.wuji.service.model.vo.FormQuoteInfoVO;
import com.wuji.service.model.vo.TemplateFormQuoteVO;
import com.wuji.service.service.FormQuoteService;
import com.wuji.service.service.TemplateFormQuoteService;
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
 * @since 2025-01-21
 */
@Service
public class TemplateFormQuoteServiceImpl extends ServiceImpl<TemplateFormQuoteMapper, TemplateFormQuoteEntity>
        implements TemplateFormQuoteService {

    @Autowired
    private FormQuoteService formQuoteService;

    @Autowired
    private TemplateFormQuoteMapper templateFormQuoteMapper;


    @Override
    public void generateTemplate(String applicationId, String templateApplicationId, List<String> formIdList,
                                 Boolean exist) {
        if (exist) {
            LambdaQueryWrapper<TemplateFormQuoteEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TemplateFormQuoteEntity::getApplicationId, templateApplicationId);
            templateFormQuoteMapper.delete(queryWrapper);
        }
        List<FormQuoteInfoVO> formQuoteInfoVOList = formQuoteService.getByFormIdList(applicationId, formIdList);
        if (CollectionUtils.isEmpty(formQuoteInfoVOList)) {
            return;
        }
        List<TemplateFormQuoteEntity> templateFormQuoteEntityList = new ArrayList<>();
        for (FormQuoteInfoVO formQuoteInfoVO : formQuoteInfoVOList) {
            TemplateFormQuoteEntity templateFormQuoteEntity =
                    AbstractTemplateFormQuoteConverter.INSTANCE.toEntity(formQuoteInfoVO);
            templateFormQuoteEntity.setApplicationId(templateApplicationId);
            templateFormQuoteEntity.setCreatorName(UserUtils.getUser().getUserName());
            templateFormQuoteEntity.setModifierName(UserUtils.getUser().getUserName());
            templateFormQuoteEntityList.add(templateFormQuoteEntity);
        }
        saveBatch(templateFormQuoteEntityList);
    }

    @Override
    public List<TemplateFormQuoteVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<TemplateFormQuoteEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateFormQuoteEntity::getApplicationId, applicationId);
        queryWrapper.eq(TemplateFormQuoteEntity::getDeleted, Boolean.FALSE);
        List<TemplateFormQuoteEntity> templateFormQuoteEntityList = templateFormQuoteMapper.selectList(queryWrapper);
        return templateFormQuoteEntityList.stream().map(AbstractTemplateFormQuoteConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }
}
