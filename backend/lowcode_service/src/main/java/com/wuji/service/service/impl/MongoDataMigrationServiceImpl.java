package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wuji.admin.enums.CompanyInfoKeyEnum;
import com.wuji.admin.model.entity.CompanyEntity;
import com.wuji.admin.model.request.CompanyInfoSaveRequest;
import com.wuji.admin.service.CompanyInfoService;
import com.wuji.admin.service.CompanyService;
import com.wuji.common.model.entity.BaseUuidEntity;
import com.wuji.common.model.info.FormImage;
import com.wuji.service.enums.TemplateApplicationTagTypeEnum;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.domain.LowcodeUpdateDataDomain;
import com.wuji.service.model.entity.TemplateApplicationEntity;
import com.wuji.service.model.info.ApplicationIcon;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.request.TemplateApplicationImgRequest;
import com.wuji.service.model.request.TemplateApplicationTagRequest;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.FormService;
import com.wuji.service.service.MongoDataMigrationService;
import com.wuji.service.service.MongoDbService;
import com.wuji.service.service.TemplateApplicationImgService;
import com.wuji.service.service.TemplateApplicationService;
import com.wuji.service.service.TemplateApplicationTagService;
import com.wuji.service.utils.MongoSearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MongoDataMigrationServiceImpl implements MongoDataMigrationService {

    @Autowired
    private FormService formService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TemplateApplicationService templateApplicationService;

    @Autowired
    private TemplateApplicationTagService templateApplicationTagService;

    @Autowired
    private TemplateApplicationImgService templateApplicationImgService;

    @Autowired
    private CompanyInfoService companyInfoService;

    @Autowired
    private MongoDbService mongoDbService;

    @Autowired
    private CompanyService companyService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void templateInfo() {
        String applicationId = "2zfvu2bgg";
        String formId = "2zfvuak74";
        FormVO info = formService.info(formId, applicationId);
        Query query = new Query();
        MongoSearchUtils.buildCommonFilter(query, applicationId, formId);
        String fieldId = MongoSearchUtils.getFieldId("radios_mkagk1bs", "");
        query.addCriteria(new Criteria(fieldId).is("是"));
        List<LowcodeDataDomain> lowcodeInsertDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());

        List<FormConfigCommon> formConfigCommonList =
                JSONArray.parseArray(JSON.parseObject(info.getConfig()).getString("body"), FormConfigCommon.class);
        Map<String, FormConfigCommon> formConfigMap =
                formConfigCommonList.stream().collect(Collectors.toMap(FormConfigCommon::getLabel, c -> c));
        List<TemplateApplicationEntity> templateApplicationEntityList = new ArrayList<>();
        List<TemplateApplicationTagRequest> templateApplicationTagRequestList = new ArrayList<>();
        List<TemplateApplicationImgRequest> templateApplicationImgRequestList = new ArrayList<>();
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeInsertDataDomains) {
            JSONObject instValue = lowcodeDataDomain.getInstValue();
            TemplateApplicationEntity templateApplicationEntity = new TemplateApplicationEntity();
            String id = getValue(formConfigMap, "模版ID", instValue);
            if (StringUtils.isEmpty(id)) {
                continue;
            }
            templateApplicationEntity.setId(id);
            List<FormImage> formImages = getImage(formConfigMap, instValue, "模版封面");
            if (CollectionUtils.isNotEmpty(formImages)) {
                templateApplicationEntity.setLogo(formImages.get(0).getUrl());
            }
            List<FormImage> iconList = getImage(formConfigMap, instValue, "模板icon");
            if (CollectionUtils.isNotEmpty(iconList)) {
                ApplicationIcon applicationIcon = new ApplicationIcon();
                applicationIcon.setUrl(iconList.get(0).getUrl());
                templateApplicationEntity.setIcon(JSONObject.toJSONString(applicationIcon));

            }
            List<FormImage> infoImageList = getImage(formConfigMap, instValue, "模版预览");
            if (CollectionUtils.isNotEmpty(formImages)) {
                infoImageList.add(0, formImages.get(0));
            }
            if (CollectionUtils.isNotEmpty(infoImageList)) {
                for (FormImage formImage : infoImageList) {
                    TemplateApplicationImgRequest templateApplicationImgRequest = new TemplateApplicationImgRequest();
                    templateApplicationImgRequest.setApplicationId(templateApplicationEntity.getId());
                    templateApplicationImgRequest.setImgUrl(formImage.getUrl());
                    templateApplicationImgRequestList.add(templateApplicationImgRequest);
                }
            }
            String description = getValue(formConfigMap, "模版简介", instValue);
            templateApplicationEntity.setDescription(description);

            String recommend = getValue(formConfigMap, "推荐", instValue);
            if (recommend != null) {
                templateApplicationEntity.setRecommend(Integer.parseInt(recommend));
            } else {
                templateApplicationEntity.setRecommend(0);
            }
            String createTime = getValue(formConfigMap, "生成模版日期", instValue);
            if (createTime != null) {
                templateApplicationEntity.setCreateTime(new Date(Long.parseLong(createTime)));
            }
            String downloadSync = getValue(formConfigMap, "是否同步安装次数", instValue);
            if ("是".equals(downloadSync)) {
                Integer downloadCount = getIntValue(formConfigMap, "模版安装次数", instValue);
                templateApplicationEntity.setDownloadCount(downloadCount);
            }

            String introduce = getValue(formConfigMap, "模板介绍1", instValue);
            templateApplicationEntity.setIntroduce(introduce);
            String applicationName = getValue(formConfigMap, "模版名称", instValue);
            templateApplicationEntity.setApplicationName(applicationName);

            setTag(formConfigMap, instValue, templateApplicationEntity, templateApplicationTagRequestList, "模版场景",
                    TemplateApplicationTagTypeEnum.SCENE.name());
            setTag(formConfigMap, instValue, templateApplicationEntity, templateApplicationTagRequestList, "模版行业",
                    TemplateApplicationTagTypeEnum.INDUSTRY.name());
            setTag(formConfigMap, instValue, templateApplicationEntity, templateApplicationTagRequestList, "行业类别",
                    TemplateApplicationTagTypeEnum.WEB_INDUSTRY.name());
            setTag(formConfigMap, instValue, templateApplicationEntity, templateApplicationTagRequestList, "热门推荐",
                    TemplateApplicationTagTypeEnum.HOT_TAG.name());

            String tagString = getValue(formConfigMap, "模版标签", instValue);
            if (tagString != null) {
                List<String> tags = Arrays.stream(tagString.split(" ")).collect(Collectors.toList());
                for (String scene : tags) {
                    TemplateApplicationTagRequest templateApplicationTagRequest = new TemplateApplicationTagRequest();
                    templateApplicationTagRequest.setTagType(TemplateApplicationTagTypeEnum.TAG.name());
                    templateApplicationTagRequest.setTagValue(scene);
                    templateApplicationTagRequest.setApplicationId(templateApplicationEntity.getId());
                    templateApplicationTagRequestList.add(templateApplicationTagRequest);
                }
            }
            templateApplicationEntityList.add(templateApplicationEntity);
            instValue.put("radios_mkagk1bs", "否");
            instValue.put("radios_mkagwkb4", "否");
            LowcodeUpdateDataDomain lowcodeUpdateDataDomain = new LowcodeUpdateDataDomain();
            lowcodeUpdateDataDomain.setUuid(lowcodeDataDomain.getUuid());
            lowcodeUpdateDataDomain.setInstValue(instValue);
            lowcodeUpdateDataDomain.setCollection(info.getTableName());
            mongoDbService.updateData(lowcodeUpdateDataDomain);
        }
        log.info("success");
        templateApplicationService.updateBatchById(templateApplicationEntityList);
        List<String> templateIdList =
                templateApplicationEntityList.stream().map(BaseUuidEntity::getId).collect(Collectors.toList());
        templateApplicationTagService.saveAll(templateApplicationTagRequestList, templateIdList);
        templateApplicationImgService.saveAll(templateApplicationImgRequestList, templateIdList);
    }

    @Override
    public void companyInfo() {
        String applicationId = "2zfvu2bgg";
        String formId = "31wx2ta68";
        FormVO info = formService.info(formId, applicationId);
        List<FormConfigCommon> formConfigCommonList =
                JSONArray.parseArray(JSON.parseObject(info.getConfig()).getString("body"), FormConfigCommon.class);
        Map<String, FormConfigCommon> formConfigMap =
                formConfigCommonList.stream().collect(Collectors.toMap(FormConfigCommon::getLabel, c -> c));
        FormConfigCommon formConfigCommon = formConfigMap.get("状态");
        Query query = new Query();
        Criteria criteria = new Criteria("instValue." + formConfigCommon.getName()).is("未同步");
        query.addCriteria(criteria);
        List<LowcodeDataDomain> lowcodeInsertDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        List<CompanyInfoSaveRequest> companyInfoSaveRequests = new ArrayList<>();
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeInsertDataDomains) {
            JSONObject instValue = lowcodeDataDomain.getInstValue();
            Long companyId = getLongValue(formConfigMap, "公司ID", instValue);
            List<FormImage> loginLogoList = getImage(formConfigMap, instValue, "登录页面LOGO");
            if (CollectionUtils.isNotEmpty(loginLogoList)) {
                CompanyInfoSaveRequest companyInfo = new CompanyInfoSaveRequest();
                companyInfo.setCompanyId(companyId);
                companyInfo.setConfigKey(CompanyInfoKeyEnum.LOGIN_LOGO.name());
                companyInfo.setConfigValue(loginLogoList.get(0).getUrl());
                companyInfoSaveRequests.add(companyInfo);
            }
            List<FormImage> backgroundList = getImage(formConfigMap, instValue, "登录页面背景图");
            if (CollectionUtils.isNotEmpty(backgroundList)) {
                CompanyInfoSaveRequest companyInfo = new CompanyInfoSaveRequest();
                companyInfo.setCompanyId(companyId);
                companyInfo.setConfigKey(CompanyInfoKeyEnum.BACKGROUND.name());
                companyInfo.setConfigValue(backgroundList.get(0).getUrl());
                companyInfoSaveRequests.add(companyInfo);
            }
            companyInfoService.save(companyInfoSaveRequests,
                    Lists.newArrayList(CompanyInfoKeyEnum.BACKGROUND.name(), CompanyInfoKeyEnum.LOGIN_LOGO.name()));
            instValue.put(formConfigCommon.getName(), "已同步");
            LowcodeUpdateDataDomain lowcodeUpdateDataDomain = new LowcodeUpdateDataDomain();
            lowcodeUpdateDataDomain.setCollection(info.getTableName());
            lowcodeUpdateDataDomain.setUuid(lowcodeDataDomain.getUuid());
            lowcodeUpdateDataDomain.setInstValue(instValue);
            mongoDbService.updateData(lowcodeUpdateDataDomain);
            CompanyEntity companyEntity = new CompanyEntity();
            companyEntity.setId(companyId);
            companyEntity.setCompanyType((short) 2);
            companyService.updateById(companyEntity);
        }

    }

    private static List<FormImage> getImage(Map<String, FormConfigCommon> formConfigMap, JSONObject instValue,
                                            String key) {
        Object logoString = getValueObject(formConfigMap, key, instValue);
        if (logoString != null) {
            return JSONArray.parseArray(JSONObject.toJSONString(logoString), FormImage.class);
        }
        return new ArrayList<>();
    }

    private static String getValue(Map<String, FormConfigCommon> formConfigMap, String key, JSONObject instValue) {
        String templateIdKey = formConfigMap.get(key).getName();
        return instValue.getString(templateIdKey);
    }

    private static Integer getIntValue(Map<String, FormConfigCommon> formConfigMap, String key, JSONObject instValue) {
        String templateIdKey = formConfigMap.get(key).getName();
        return instValue.getIntValue(templateIdKey);
    }

    private static Long getLongValue(Map<String, FormConfigCommon> formConfigMap, String key, JSONObject instValue) {
        String templateIdKey = formConfigMap.get(key).getName();
        return instValue.getLongValue(templateIdKey);
    }

    private static JSONArray getValueObject(Map<String, FormConfigCommon> formConfigMap, String key,
                                            JSONObject instValue) {
        String templateIdKey = formConfigMap.get(key).getName();
        return instValue.getJSONArray(templateIdKey);
    }

    private static void setTag(Map<String, FormConfigCommon> formConfigMap, JSONObject instValue,
                               TemplateApplicationEntity templateApplicationEntity,
                               List<TemplateApplicationTagRequest> templateApplicationTagRequestList, String name,
                               String tagType) {
        JSONArray sceneString = getValueObject(formConfigMap, name, instValue);
        if (sceneString != null) {
            List<String> sceneList = JSONArray.parseArray(JSONArray.toJSONString(sceneString), String.class);
            for (String scene : sceneList) {
                TemplateApplicationTagRequest templateApplicationTagRequest = new TemplateApplicationTagRequest();
                templateApplicationTagRequest.setTagType(tagType);
                templateApplicationTagRequest.setTagValue(scene);
                templateApplicationTagRequest.setApplicationId(templateApplicationEntity.getId());
                templateApplicationTagRequestList.add(templateApplicationTagRequest);
            }
        }
    }
}
