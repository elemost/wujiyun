package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractApplicationCategoryConverter;
import com.wuji.service.converter.AbstractTemplateApplicationCategoryConverter;
import com.wuji.service.enums.ApplicationCategoryCategoryTypeEnum;
import com.wuji.service.mapper.TemplateApplicationCategoryMapper;
import com.wuji.service.model.entity.ApplicationCategoryEntity;
import com.wuji.service.model.entity.TemplateApplicationCategoryEntity;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.service.FormDataFactoryService;
import com.wuji.service.service.FormDataStreamService;
import com.wuji.service.service.FormExtraFunctionService;
import com.wuji.service.service.FormInfoService;
import com.wuji.service.service.FormPrivilegeService;
import com.wuji.service.service.FormPublicPublishService;
import com.wuji.service.service.FormQuoteService;
import com.wuji.service.service.FormRuleService;
import com.wuji.service.service.TemplateApplicationCategoryService;
import com.wuji.service.service.TemplateFormAggregateService;
import com.wuji.service.service.TemplateFormDataFactoryService;
import com.wuji.service.service.TemplateFormDataStreamService;
import com.wuji.service.service.TemplateFormExtraFunctionService;
import com.wuji.service.service.TemplateFormInfoService;
import com.wuji.service.service.TemplateFormModelService;
import com.wuji.service.service.TemplateFormPrivilegeService;
import com.wuji.service.service.TemplateFormPublicPublishService;
import com.wuji.service.service.TemplateFormQuoteService;
import com.wuji.service.service.TemplateFormRuleService;
import com.wuji.service.service.TemplateFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用目录 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
@Service
public class TemplateApplicationCategoryServiceImpl
        extends ServiceImpl<TemplateApplicationCategoryMapper, TemplateApplicationCategoryEntity>
        implements TemplateApplicationCategoryService {

    @Autowired
    private TemplateApplicationCategoryMapper templateApplicationCategoryMapper;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Autowired
    private TemplateFormService templateFormService;

    @Autowired
    private TemplateFormModelService templateFormModelService;

    @Autowired
    private TemplateFormExtraFunctionService templateFormExtraFunctionService;

    @Autowired
    private TemplateFormPrivilegeService templateFormPrivilegeService;

    @Autowired
    private TemplateFormQuoteService templateFormQuoteService;

    @Autowired
    private FormExtraFunctionService formExtraFunctionServiceImpl;

    @Autowired
    private FormQuoteService formQuoteService;

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @Autowired
    private TemplateFormPublicPublishService templateFormPublicPublishService;

    @Autowired
    private FormPublicPublishService formPublicPublishService;

    @Autowired
    private FormAggregateService formAggregateService;

    @Autowired
    private TemplateFormAggregateService templateFormAggregateService;

    @Autowired
    private TemplateFormDataStreamService templateFormDataStreamService;

    @Autowired
    private FormDataStreamService formDataStreamService;

    @Autowired
    private TemplateFormInfoService templateFormInfoService;

    @Autowired
    private FormInfoService formInfoService;

    @Autowired
    private TemplateFormRuleService templateFormRuleService;

    @Autowired
    private FormRuleService formRuleService;

    @Autowired
    private TemplateFormDataFactoryService templateFormDataFactoryService;

    @Autowired
    private FormDataFactoryService formDataFactoryService;

    @Override
    public void generateTemplate(String sourceApplicationId, String templateApplicationId, Boolean exist) {
        UserDomain user = UserUtils.getUser() == null ? new UserDomain() : UserUtils.getUser();
        List<ApplicationCategoryVO> applicationCategoryVOS = applicationCategoryService.selectList(sourceApplicationId);
        List<TemplateApplicationCategoryEntity> templateApplicationEntityList = new ArrayList<>();
        List<ApplicationCategoryVO> flowableList = new ArrayList<>();
        List<String> formIdList =
                applicationCategoryVOS.stream().map(ApplicationCategoryVO::getId).collect(Collectors.toList());
        if (exist) {
            LambdaQueryWrapper<TemplateApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TemplateApplicationCategoryEntity::getApplicationId, templateApplicationId);
            templateApplicationCategoryMapper.delete(queryWrapper);
        }
        long time = new Date().getTime();
        for (ApplicationCategoryVO applicationCategoryVO : applicationCategoryVOS) {
            if (ApplicationCategoryCategoryTypeEnum.FLOWABLE_FORM.name()
                    .equals(applicationCategoryVO.getCategoryType())) {
                flowableList.add(applicationCategoryVO);
            }
            TemplateApplicationCategoryEntity templateApplicationCategoryEntity =
                    AbstractTemplateApplicationCategoryConverter.INSTANCE.toEntity(applicationCategoryVO);
            templateApplicationCategoryEntity.setApplicationId(templateApplicationId);
            templateApplicationCategoryEntity.setModifier(user.getUserId());
            templateApplicationCategoryEntity.setCreator(user.getUserId());
            time = time + 1000;
            templateApplicationCategoryEntity.setCreateTime(new Date(time));
            templateApplicationEntityList.add(templateApplicationCategoryEntity);
        }
        saveBatch(templateApplicationEntityList);
        // 表单
        templateFormService.generateTemplate(sourceApplicationId, templateApplicationId, exist, formIdList);
        // 表单流程
        templateFormModelService.generateTemplate(templateApplicationId, sourceApplicationId, flowableList, exist);
        // 表单权限
        Map<String, String> privilegeIdMap =
                templateFormPrivilegeService.generateTemplate(sourceApplicationId, templateApplicationId, formIdList,
                        exist);
        // 额外功能（包括自定义按钮）
        templateFormExtraFunctionService.generateTemplate(sourceApplicationId, templateApplicationId, privilegeIdMap,
                exist);
        // 引用关系
        templateFormQuoteService.generateTemplate(sourceApplicationId, templateApplicationId, formIdList, exist);
        templateFormPublicPublishService.generateTemplate(sourceApplicationId, templateApplicationId, exist);
        templateFormAggregateService.generateTemplate(sourceApplicationId, templateApplicationId, exist);
        templateFormDataStreamService.generateTemplate(sourceApplicationId, templateApplicationId, formIdList, exist);
        templateFormInfoService.generateTemplate(sourceApplicationId, templateApplicationId, exist);
        templateFormRuleService.generateTemplate(sourceApplicationId, templateApplicationId, exist);
        templateFormDataFactoryService.generateTemplate(sourceApplicationId, templateApplicationId, exist);
    }

    @Override
    public void useTemplate(String applicationId, String templateApplicationId, String sourceApplicationId,
                            Boolean needData) {
        UserDomain user = UserUtils.getUser() == null ? new UserDomain() : UserUtils.getUser();
        LambdaQueryWrapper<TemplateApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateApplicationCategoryEntity::getApplicationId, templateApplicationId);
        queryWrapper.orderByDesc(TemplateApplicationCategoryEntity::getSortNum);
        queryWrapper.orderByAsc(TemplateApplicationCategoryEntity::getCreateTime);
        List<TemplateApplicationCategoryEntity> templateApplicationCategoryEntityList =
                templateApplicationCategoryMapper.selectList(queryWrapper);
        List<TemplateApplicationCategoryEntity> flowableList = new ArrayList<>();
        List<ApplicationCategoryEntity> applicationCategoryEntityList = new ArrayList<>();
        long time = new Date().getTime();
        for (TemplateApplicationCategoryEntity templateApplicationCategoryEntity : templateApplicationCategoryEntityList) {
            if (ApplicationCategoryCategoryTypeEnum.FLOWABLE_FORM.name()
                    .equals(templateApplicationCategoryEntity.getCategoryType())) {
                flowableList.add(templateApplicationCategoryEntity);
            }
            ApplicationCategoryEntity applicationCategoryEntity =
                    AbstractApplicationCategoryConverter.INSTANCE.toEntity(templateApplicationCategoryEntity);
            applicationCategoryEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            applicationCategoryEntity.setApplicationId(applicationId);
            applicationCategoryEntity.setModifier(user.getUserId());
            time = time + 1000;
            applicationCategoryEntity.setCreateTime(new Date(time));
            applicationCategoryEntity.setCreator(user.getUserId());
            applicationCategoryEntityList.add(applicationCategoryEntity);
        }
        applicationCategoryService.saveBatch(applicationCategoryEntityList);
        // 表单
        templateFormService.useTemplate(applicationId, templateApplicationId, sourceApplicationId, needData);
        // 表单权限
        Map<String, String> categoryToPrivilegeMap =
                formPrivilegeService.useTemplate(applicationId, templateApplicationId, applicationCategoryEntityList);
        // 表单流程
        templateFormModelService.useTemplate(applicationId, templateApplicationId, flowableList);
        // 自定义按钮
        formExtraFunctionServiceImpl.useTemplate(applicationId, templateApplicationId, categoryToPrivilegeMap);
        // 引用关系
        formQuoteService.useTemplate(applicationId, templateApplicationId);
        formPublicPublishService.useTemplate(applicationId, templateApplicationId);
        formAggregateService.useTemplate(applicationId, templateApplicationId);
        formDataStreamService.useTemplate(applicationId, templateApplicationId, sourceApplicationId);
        formInfoService.userTemplate(applicationId, templateApplicationId);
        formRuleService.userTemplate(applicationId, templateApplicationId);
        formDataFactoryService.useTemplate(applicationId, templateApplicationId, sourceApplicationId);
    }
}
