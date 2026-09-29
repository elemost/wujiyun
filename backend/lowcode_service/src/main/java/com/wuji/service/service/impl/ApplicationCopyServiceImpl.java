package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.SnowFlakeIdUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.enums.ApplicationCategoryCategoryTypeEnum;
import com.wuji.service.mapper.ApplicationCategoryMapper;
import com.wuji.service.mapper.ApplicationMapper;
import com.wuji.service.model.entity.ApplicationCategoryEntity;
import com.wuji.service.model.entity.ApplicationEntity;
import com.wuji.service.model.request.ApplicationCopyRequest;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.ApplicationCopyService;
import com.wuji.service.service.ApplicationPrivilegeService;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.service.FormDataFactoryService;
import com.wuji.service.service.FormDataStreamService;
import com.wuji.service.service.FormExtraFunctionService;
import com.wuji.service.service.FormInfoService;
import com.wuji.service.service.FormModelService;
import com.wuji.service.service.FormPrivilegeService;
import com.wuji.service.service.FormPublicPublishService;
import com.wuji.service.service.FormQuoteService;
import com.wuji.service.service.FormRuleService;
import com.wuji.service.service.FormSerialNumberService;
import com.wuji.service.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApplicationCopyServiceImpl implements ApplicationCopyService {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ApplicationCategoryMapper applicationCategoryMapper;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Autowired
    private ApplicationPrivilegeService applicationPrivilegeService;

    @Autowired
    private FormService formService;

    @Autowired
    private FormModelService formModelService;

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @Autowired
    private FormExtraFunctionService formExtraFunctionServiceImpl;

    @Autowired
    private FormQuoteService formQuoteService;

    @Autowired
    private FormPublicPublishService formPublicPublishService;

    @Autowired
    private FormAggregateService formAggregateService;

    @Autowired
    private FormDataStreamService formDataStreamService;

    @Autowired
    private FormRuleService formRuleService;

    @Autowired
    private FormDataFactoryService formDataFactoryService;

    @Autowired
    private FormInfoService formInfoService;

    @Autowired
    private FormSerialNumberService formSerialNumberService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copy(ApplicationCopyRequest applicationCopyRequest) {
        UserDomain user = UserUtils.getUser() == null ? new UserDomain() : UserUtils.getUser();
        String applicationId = SnowFlakeIdUtils.generateStr();
        ApplicationEntity applicationEntity = applicationMapper.selectById(applicationCopyRequest.getApplicationId());
        applicationEntity.setId(applicationId);
        applicationEntity.setCompanyId(user.getCompanyId());
        applicationEntity.setCreator(user.getUserId());
        applicationEntity.setModifier(user.getUserId());
        applicationEntity.setCreateTime(new Date());
        applicationEntity.setModifyTime(new Date());
        applicationEntity.setApplicationName(applicationCopyRequest.getApplicationName());
        applicationMapper.insert(applicationEntity);
        copyApplication(applicationId, applicationCopyRequest.getApplicationId(), false,
                applicationCopyRequest.getNeedData());
        applicationPrivilegeService.copy(applicationId, applicationCopyRequest.getApplicationId());
        return applicationId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String share(ApplicationCopyRequest applicationCopyRequest) {
        UserDomain user = UserUtils.getUser() == null ? new UserDomain() : UserUtils.getUser();
        String applicationId = SnowFlakeIdUtils.generateStr();
        ApplicationEntity applicationEntity = applicationMapper.selectById(applicationCopyRequest.getApplicationId());
        applicationEntity.setId(applicationId);
        applicationEntity.setCompanyId(user.getCompanyId());
        applicationEntity.setCreator(user.getUserId());
        applicationEntity.setModifier(user.getUserId());
        applicationEntity.setCreateTime(new Date());
        applicationEntity.setModifyTime(new Date());
        applicationMapper.insert(applicationEntity);
        copyApplication(applicationId, applicationCopyRequest.getApplicationId(), true,
                applicationCopyRequest.getNeedData());
        applicationPrivilegeService.saveDefaultPrivilege(applicationId);
        return applicationId;
    }

    public void copyApplication(String applicationId, String sourceApplicationId, Boolean share, Boolean needDate) {
        UserDomain user = UserUtils.getUser() == null ? new UserDomain() : UserUtils.getUser();
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationCategoryEntity::getApplicationId, sourceApplicationId);
        queryWrapper.orderByDesc(ApplicationCategoryEntity::getSortNum);
        queryWrapper.orderByAsc(ApplicationCategoryEntity::getCreateTime);
        List<ApplicationCategoryEntity> sourceApplicationCategoryEntityList =
                applicationCategoryMapper.selectList(queryWrapper);
        List<ApplicationCategoryEntity> flowableList = new ArrayList<>();
        List<ApplicationCategoryEntity> applicationCategoryList = new ArrayList<>();
        int i = sourceApplicationCategoryEntityList.size();
        for (ApplicationCategoryEntity sourceApplicationCategoryEntity : sourceApplicationCategoryEntityList) {
            if (ApplicationCategoryCategoryTypeEnum.FLOWABLE_FORM.name()
                    .equals(sourceApplicationCategoryEntity.getCategoryType())) {
                flowableList.add(sourceApplicationCategoryEntity);
            }
            ApplicationCategoryEntity applicationCategoryEntity =
                    JSONObject.parseObject(JSONObject.toJSONString(sourceApplicationCategoryEntity),
                            ApplicationCategoryEntity.class);
            applicationCategoryEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            applicationCategoryEntity.setApplicationId(applicationId);
            applicationCategoryEntity.setModifier(user.getUserId());
            applicationCategoryEntity.setCreateTime(new Date());
            applicationCategoryEntity.setCreator(user.getUserId());
            applicationCategoryEntity.setSortNum(i);
            applicationCategoryList.add(applicationCategoryEntity);
            i--;
        }
        applicationCategoryService.saveBatch(applicationCategoryList);
        List<String> formIdList = sourceApplicationCategoryEntityList.stream().map(ApplicationCategoryEntity::getId)
                .collect(Collectors.toList());
        // 表单
        formService.copyApplication(applicationId, sourceApplicationId, share, needDate);
        formModelService.copyApplication(applicationId, sourceApplicationId, flowableList);
        Map<String, String> privilegeMap =
                formPrivilegeService.copyApplication(applicationId, sourceApplicationId, applicationCategoryList,
                        share);
        // 自定义按钮
        formExtraFunctionServiceImpl.copyApplication(applicationId, sourceApplicationId, privilegeMap, share);
        // 引用关系
        formQuoteService.copyApplication(applicationId, sourceApplicationId);
        // 公开发布
        formPublicPublishService.copyApplication(applicationId, sourceApplicationId);
        // 聚合表
        formAggregateService.copyApplication(applicationId, sourceApplicationId, share);
        // 数据流
        formDataStreamService.copyApplication(applicationId, sourceApplicationId, formIdList, share);
        // 数据工厂
        formDataFactoryService.copyApplication(applicationId, sourceApplicationId);
        // 业务规则
        formRuleService.copyApplication(applicationId, sourceApplicationId);
        // 自定义详情页
        formInfoService.copyApplication(applicationId, sourceApplicationId);
        // 序列号复制
        formSerialNumberService.copyApplication(applicationId, sourceApplicationId, needDate);
    }
}
