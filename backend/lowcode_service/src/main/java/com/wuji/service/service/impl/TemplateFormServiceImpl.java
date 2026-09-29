package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractTemplateFormConverter;
import com.wuji.service.enums.ApplicationCategoryCategoryTypeEnum;
import com.wuji.service.mapper.TemplateFormMapper;
import com.wuji.service.model.entity.FormEntity;
import com.wuji.service.model.entity.TemplateFormEntity;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.TemplateFormVO;
import com.wuji.service.service.FormModuleService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.TemplateFormModuleService;
import com.wuji.service.service.TemplateFormService;
import com.wuji.service.utils.TemplateDealConfigUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 表单 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
@Service
public class TemplateFormServiceImpl extends ServiceImpl<TemplateFormMapper, TemplateFormEntity>
        implements TemplateFormService {

    @Autowired
    private FormService formService;

    @Autowired
    private TemplateFormMapper templateFormMapper;

    @Autowired
    private TemplateFormModuleService templateFormModuleService;

    @Autowired
    private FormModuleService formModuleService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void generateTemplate(String sourceApplicationId, String templateApplicationId, Boolean exist,
                                 List<String> formIdList) {
        if (exist) {
            LambdaQueryWrapper<TemplateFormEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TemplateFormEntity::getApplicationId, templateApplicationId);
            templateFormMapper.delete(queryWrapper);
            mongoTemplate.dropCollection(templateApplicationId);
        }
        UserDomain user = UserUtils.getUser();
        List<FormVO> formVOList = formService.getByIdList(formIdList, sourceApplicationId);
        List<TemplateFormEntity> templateFormEntityList = new ArrayList<>();
        for (FormVO formVO : formVOList) {
            TemplateFormEntity templateFormEntity = AbstractTemplateFormConverter.INSTANCE.toEntity(formVO);
            // templateFormEntity.setTableName(formVO.getId() + "_" + templateApplicationId);
            templateFormEntity.setTableName(templateApplicationId);
            templateFormEntity.setCreator(user.getUserId());
            templateFormEntity.setModifier(user.getUserId());
            templateFormEntity.setApplicationId(templateApplicationId);
            templateFormEntityList.add(templateFormEntity);
            if (ApplicationCategoryCategoryTypeEnum.getFormType().contains(formVO.getFormType())) {
                formMongoDbService.copyData(formVO.getConfig(), formVO.getTableName(),
                        templateFormEntity.getTableName(), Boolean.FALSE, exist, formVO.getApplicationId(),
                        formVO.getId(), templateFormEntity.getApplicationId());
            }
        }
        if (CollectionUtils.isNotEmpty(templateFormEntityList)) {
            saveBatch(templateFormEntityList);
        }
        templateFormModuleService.generateTemplate(sourceApplicationId, templateApplicationId, exist);
    }

    @Override
    public void useTemplate(String applicationId, String templateApplicationId, String sourceApplicationId,
                            Boolean needData) {
        UserDomain user = UserUtils.getUser();
        List<TemplateFormVO> templateFormVOList = getByApplicationId(templateApplicationId);
        if (CollectionUtils.isEmpty(templateFormVOList)) {
            return;
        }
        List<FormEntity> formEntityList = new ArrayList<>();
        for (TemplateFormVO templateFormVO : templateFormVOList) {
            FormEntity formEntity = AbstractTemplateFormConverter.INSTANCE.toEntity(templateFormVO);
            formEntity.setApplicationId(applicationId);
            formEntity.setTableName(applicationId);
            formEntity.setCreator(user.getUserId());
            formEntity.setModifier(user.getUserId());
            formEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            formEntity.setConfig(TemplateDealConfigUtil.dealConfig(templateFormVO.getConfig()));
            formEntityList.add(formEntity);
            if (ApplicationCategoryCategoryTypeEnum.getFormType().contains(templateFormVO.getFormType())) {
                if (needData) {
                    formMongoDbService.copyData(templateFormVO.getConfig(), templateFormVO.getTableName(),
                            formEntity.getTableName(), Boolean.TRUE, Boolean.FALSE, templateFormVO.getApplicationId(),
                            templateFormVO.getId(), formEntity.getApplicationId());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(formEntityList)) {
            formService.saveBatch(formEntityList);
        }
        formModuleService.useTemplate(applicationId, templateApplicationId, sourceApplicationId);
    }

    @Override
    public List<TemplateFormVO> getByIdList(List<String> idList, String templateApplicationId) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<TemplateFormEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TemplateFormEntity::getId, idList);
        queryWrapper.in(TemplateFormEntity::getApplicationId, templateApplicationId);
        List<TemplateFormEntity> templateFormEntityList = templateFormMapper.selectList(queryWrapper);
        return templateFormEntityList.stream().map(AbstractTemplateFormConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TemplateFormVO> getByApplicationId(String templateApplicationId) {
        LambdaQueryWrapper<TemplateFormEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TemplateFormEntity::getApplicationId, templateApplicationId);
        List<TemplateFormEntity> templateFormEntityList = templateFormMapper.selectList(queryWrapper);
        return templateFormEntityList.stream().map(AbstractTemplateFormConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }
}
