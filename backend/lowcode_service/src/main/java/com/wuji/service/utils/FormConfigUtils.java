package com.wuji.service.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormUser;
import com.wuji.service.enums.ApplicationCategoryCategoryTypeEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormDataTitle;
import com.wuji.service.model.vo.LowcodeDataVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FormConfigUtils {

    public static List<FormConfigCommon> getConfigList(String config, String formType, Boolean containSystem) {
        List<FormConfigCommon> formConfigCommonList = new ArrayList<>();
        if (StringUtils.isNotEmpty(config)) {
            if (StringUtils.isNotEmpty(JSON.parseObject(config).getString("body"))) {
                formConfigCommonList =
                        JSONArray.parseArray(JSON.parseObject(config).getString("body"), FormConfigCommon.class);
            }
        }
        if (containSystem) {
            if (ApplicationCategoryCategoryTypeEnum.getFlowerFormType().contains(formType)) {
                formConfigCommonList.addAll(FormSystemFieldEnum.getFlowableFormSystemField());
            } else if (ApplicationCategoryCategoryTypeEnum.getFormType().contains(formType)) {
                formConfigCommonList.addAll(FormSystemFieldEnum.getFormSystemField());
            }
        }
        return formConfigCommonList;
    }

    public static List<FormConfigCommon> getConfigSpreadList(String config, String formType, Boolean containSystem) {
        List<FormConfigCommon> formConfigCommonList = new ArrayList<>();
        if (StringUtils.isNotEmpty(config)) {
            formConfigCommonList =
                    JSONArray.parseArray(JSON.parseObject(config).getString("body"), FormConfigCommon.class);
        }
        if (containSystem) {
            if (ApplicationCategoryCategoryTypeEnum.getFlowerFormType().contains(formType)) {
                formConfigCommonList.addAll(FormSystemFieldEnum.getFlowableFormSystemField());
            } else if (ApplicationCategoryCategoryTypeEnum.getFormType().contains(formType)) {
                formConfigCommonList.addAll(FormSystemFieldEnum.getFormSystemField());
            }
        }
        List<FormConfigCommon> returnList = new ArrayList<>();
        for (FormConfigCommon formConfigCommon : formConfigCommonList) {
            if (!FormFieldTypeEnum.SUB_FORM_TYPE.getFieldType().equals(formConfigCommon.getType())) {
                returnList.add(formConfigCommon);
            } else {
                if (CollectionUtils.isNotEmpty(formConfigCommon.getColumns())) {
                    for (FormConfigCommon subform : formConfigCommon.getColumns()) {
                        subform.setLabel(formConfigCommon.getLabel() + "-" + subform.getLabel());
                    }
                }
            }
        }
        return returnList;
    }


    public static FormConfigCommon getDefaultTitle(String config) {
        List<FormConfigCommon> formConfigCommonList = new ArrayList<>();
        if (StringUtils.isNotEmpty(config)) {
            formConfigCommonList =
                    JSONArray.parseArray(JSON.parseObject(config).getString("body"), FormConfigCommon.class);
        }
        if (CollectionUtils.isEmpty(formConfigCommonList)) {
            return null;
        }
        return formConfigCommonList.stream().filter(c -> FormFieldTypeEnum.defaultTitle().contains(c.getType()))
                .findFirst().orElse(null);
    }


    public static void buildDataTitle(String config, List<LowcodeDataDomain> lowcodeDataDomainList) {
        FormConfigCommon defaultTitle = getDefaultTitle(config);
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataDomainList) {
            if (defaultTitle != null) {
                if (lowcodeDataDomain.getInstValue() != null) {
                    lowcodeDataDomain.setDataTitle(
                            lowcodeDataDomain.getInstValue().getOrDefault(defaultTitle.getName(), "").toString());
                } else {
                    FormUser formUser = JSONObject.parseObject(JSONObject.toJSONString(lowcodeDataDomain.getCreator()),
                            FormUser.class);
                    lowcodeDataDomain.setDataTitle(formUser.getAssigneeName());
                }
            } else {
                FormUser formUser =
                        JSONObject.parseObject(JSONObject.toJSONString(lowcodeDataDomain.getCreator()), FormUser.class);
                lowcodeDataDomain.setDataTitle(formUser.getAssigneeName());
            }
        }
    }

    public static void buildDataTitleVO(String config, List<LowcodeDataVO> lowcodeDataVOS) {
        FormConfigCommon defaultTitle = getDefaultTitle(config);
        for (LowcodeDataVO lowcodeData : lowcodeDataVOS) {
            if (defaultTitle != null) {
                if (lowcodeData.getInstValue() != null) {
                    lowcodeData.setDataTitle(
                            lowcodeData.getInstValue().getOrDefault(defaultTitle.getName(), "").toString());
                } else {
                    FormUser formUser =
                            JSONObject.parseObject(JSONObject.toJSONString(lowcodeData.getCreator()), FormUser.class);
                    lowcodeData.setDataTitle(formUser.getAssigneeName());
                }
            } else {
                FormUser formUser =
                        JSONObject.parseObject(JSONObject.toJSONString(lowcodeData.getCreator()), FormUser.class);
                lowcodeData.setDataTitle(formUser.getAssigneeName());
            }
        }
    }

    public static List<FormDataTitle> getFormDataTitle(List<LowcodeDataDomain> lowcodeDataDomainList) {
        List<FormDataTitle> formDataTitles = new ArrayList<>();
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataDomainList) {
            FormDataTitle formDataTitle =
                    new FormDataTitle(lowcodeDataDomain.getUuid(), lowcodeDataDomain.getDataTitle());
            formDataTitles.add(formDataTitle);
        }
        return formDataTitles;
    }
}
