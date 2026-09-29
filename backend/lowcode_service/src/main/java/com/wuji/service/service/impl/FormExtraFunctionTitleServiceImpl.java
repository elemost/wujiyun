package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.enums.FormExtraFunctionTypeEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.mapper.FormExtraFunctionMapper;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.entity.FormExtraFunctionEntity;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormExtraFunctionTitle;
import com.wuji.service.model.request.FormExtraFunctionTitleSaveRequest;
import com.wuji.service.service.FormExtraFunctionTitleService;
import com.wuji.service.utils.FormConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("formExtraFunctionTitleServiceImpl")
public class FormExtraFunctionTitleServiceImpl extends FormExtraFunctionServiceImpl
        implements FormExtraFunctionTitleService {

    @Autowired
    private FormExtraFunctionMapper formExtraFunctionMapper;

    @Override
    public void buildTitle(List<LowcodeDataDomain> lowcodeDataDomainList, String config, String formId,
                           String applicationId) {
        LambdaQueryWrapper<FormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionEntity::getFormId, formId);
        queryWrapper.eq(FormExtraFunctionEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormExtraFunctionEntity::getFunctionType, FormExtraFunctionTypeEnum.CUSTOM_TITLE.name());
        queryWrapper.eq(FormExtraFunctionEntity::getDeleted, Boolean.FALSE);
        FormExtraFunctionEntity formExtraFunctionEntity = formExtraFunctionMapper.selectOne(queryWrapper);
        if (formExtraFunctionEntity == null) {
            FormConfigUtils.buildDataTitle(config, lowcodeDataDomainList);
        } else {
            FormExtraFunctionTitle formExtraFunctionTitle =
                    JSONObject.parseObject(formExtraFunctionEntity.getConfig(), FormExtraFunctionTitle.class);
            String formula = formExtraFunctionTitle.getFormula();
            if ("default".equals(formExtraFunctionTitle.getType())) {
                formula = formExtraFunctionTitle.getDefaultTitle();
            }
            String regexFormat = "\\$\\{([\\w.]+)\\}";
            String regex = String.format(regexFormat, formId);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(formula);
            List<String> fieldList = new ArrayList<>();
            // 查找并打印所有匹配的结果
            while (matcher.find()) {
                String field = matcher.group(1);
                fieldList.add(field);
            }
            for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataDomainList) {
                Map<String, String> titleMap = new HashMap<>();
                for (String field : fieldList) {
                    if (FormSystemFieldEnum.CREATE_NAME.getName().equals(field)) {
                        FormUser formUser =
                                JSONObject.parseObject(JSONObject.toJSONString(lowcodeDataDomain.getCreator()),
                                        FormUser.class);
                        titleMap.put(field, formUser.getAssigneeName());
                    } else {
                        titleMap.put(field, lowcodeDataDomain.getInstValue().getOrDefault(field, "").toString());
                    }
                }
                String title = formula;
                for (String key : titleMap.keySet()) {
                    title = title.replaceAll("\\$\\{" +key + "\\}",  titleMap.get(key));
                }
                lowcodeDataDomain.setDataTitle(title);
            }
        }
    }

    public static void main(String[] args) {

    }


    @Override
    public void saveTitle(String config, String formId, String applicationId) {
        FormConfigCommon defaultTitle = FormConfigUtils.getDefaultTitle(config);
        String title = "";
        if (defaultTitle != null) {
            title = "${" + defaultTitle.getName() + "}";
        } else {
            title = "${" + FormSystemFieldEnum.CREATE_NAME.getName() + "}";
        }
        LambdaQueryWrapper<FormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionEntity::getFormId, formId);
        queryWrapper.eq(FormExtraFunctionEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormExtraFunctionEntity::getFunctionType, FormExtraFunctionTypeEnum.CUSTOM_TITLE.name());
        queryWrapper.eq(FormExtraFunctionEntity::getDeleted, Boolean.FALSE);
        FormExtraFunctionEntity formExtraFunctionEntity = formExtraFunctionMapper.selectOne(queryWrapper);
        FormExtraFunctionTitle formExtraFunctionTitle = null;
        if (formExtraFunctionEntity == null) {
            formExtraFunctionEntity = new FormExtraFunctionEntity();
            formExtraFunctionEntity.setId(ObjectId.getGuid());
            formExtraFunctionEntity.setFormId(formId);
            formExtraFunctionEntity.setApplicationId(applicationId);
            formExtraFunctionTitle = new FormExtraFunctionTitle();
            formExtraFunctionTitle.setType("default");
            formExtraFunctionTitle.setDefaultTitle(title);
            formExtraFunctionEntity.setConfig(JSONObject.toJSONString(formExtraFunctionTitle));
            formExtraFunctionEntity.setCreator(UserUtils.getUser().getNickName());
            formExtraFunctionEntity.setModifier(UserUtils.getUser().getNickName());
            formExtraFunctionEntity.setFunctionType(FormExtraFunctionTypeEnum.CUSTOM_TITLE.name());
            formExtraFunctionMapper.insert(formExtraFunctionEntity);
        } else {
            formExtraFunctionTitle =
                    JSONObject.parseObject(formExtraFunctionEntity.getConfig(), FormExtraFunctionTitle.class);
            formExtraFunctionTitle.setType("default");
            formExtraFunctionTitle.setDefaultTitle(title);
            formExtraFunctionEntity.setModifier(UserUtils.getUser().getNickName());
            formExtraFunctionEntity.setConfig(JSONObject.toJSONString(formExtraFunctionTitle));
            formExtraFunctionEntity.setFunctionType(FormExtraFunctionTypeEnum.CUSTOM_TITLE.name());
            formExtraFunctionMapper.updateById(formExtraFunctionEntity);
        }
    }

    @Override
    public void saveTitleConfig(FormExtraFunctionTitleSaveRequest formExtraFunctionTitleSaveRequest) {
        LambdaQueryWrapper<FormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionEntity::getFormId, formExtraFunctionTitleSaveRequest.getFormId());
        queryWrapper.eq(FormExtraFunctionEntity::getApplicationId,
                formExtraFunctionTitleSaveRequest.getApplicationId());
        queryWrapper.eq(FormExtraFunctionEntity::getFunctionType, FormExtraFunctionTypeEnum.CUSTOM_TITLE.name());
        queryWrapper.eq(FormExtraFunctionEntity::getDeleted, Boolean.FALSE);
        FormExtraFunctionEntity formExtraFunctionEntity = formExtraFunctionMapper.selectOne(queryWrapper);
        if (formExtraFunctionEntity == null) {
            formExtraFunctionEntity = new FormExtraFunctionEntity();
            formExtraFunctionEntity.setId(ObjectId.getGuid());
            formExtraFunctionEntity.setFormId(formExtraFunctionTitleSaveRequest.getFormId());
            formExtraFunctionEntity.setApplicationId(formExtraFunctionTitleSaveRequest.getApplicationId());
            formExtraFunctionEntity.setConfig(
                    JSONObject.toJSONString(formExtraFunctionTitleSaveRequest.getConfigJson()));
            formExtraFunctionEntity.setCreator(UserUtils.getUser().getNickName());
            formExtraFunctionEntity.setModifier(UserUtils.getUser().getNickName());
            formExtraFunctionEntity.setFunctionType(FormExtraFunctionTypeEnum.CUSTOM_TITLE.name());
            formExtraFunctionMapper.insert(formExtraFunctionEntity);
        } else {
            formExtraFunctionEntity.setConfig(
                    JSONObject.toJSONString(formExtraFunctionTitleSaveRequest.getConfigJson()));
            formExtraFunctionEntity.setModifier(UserUtils.getUser().getNickName());
            formExtraFunctionMapper.updateById(formExtraFunctionEntity);
        }
    }
}
