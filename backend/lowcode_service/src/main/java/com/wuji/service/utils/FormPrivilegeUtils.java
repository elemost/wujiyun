package com.wuji.service.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wuji.admin.cache.DepartmentCache;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.FormPublicPublishCache;
import com.wuji.service.enums.FormPrivilegeDataScopeTypeEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.enums.MongodbSearchConditionQuoteTypeEnum;
import com.wuji.service.enums.SearchFilterRelEnum;
import com.wuji.service.model.domain.FormPrivilegeDataScopeDomain;
import com.wuji.service.model.entity.FormPrivilegeEntity;
import com.wuji.service.model.info.FormExtraFunctionButton;
import com.wuji.service.model.info.FormPrivilegeDataScope;
import com.wuji.service.model.info.FormPrivilegeFieldConfig;
import com.wuji.service.model.info.FormViewConfig;
import com.wuji.service.model.info.MongodbSearchCondition;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.FormPublicPublishVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormPrivilegeUtils {
    public static List<FormPrivilegeDataScopeDomain> getDataScope(List<FormPrivilegeVO> formPrivilegeList) {
        UserDomain user = UserUtils.getUser();
        List<FormPrivilegeDataScopeDomain> formPrivilegeDataScopeDomainList = new ArrayList<>();
        for (FormPrivilegeVO formPrivilegeVO : formPrivilegeList) {
            FormPrivilegeDataScopeDomain formPrivilegeDataScopeDomain = new FormPrivilegeDataScopeDomain();
            formPrivilegeDataScopeDomain.setViewPrivilegeList(formPrivilegeVO.getViewPrivilegeList());
            formPrivilegeDataScopeDomain.setOperatePrivilegeList(formPrivilegeVO.getOperatePrivilegeList());
            for (FormPrivilegeDataScope formPrivilegeDataScope : formPrivilegeVO.getDataScopeList()) {
                FormPrivilegeDataScopeTypeEnum formPrivilegeDataScopeTypeEnum =
                        FormPrivilegeDataScopeTypeEnum.valueOf(formPrivilegeDataScope.getDataScopeType());
                switch (formPrivilegeDataScopeTypeEnum) {
                    case ALL:
                        formPrivilegeDataScopeDomain.setAll(Boolean.TRUE);
                        break;
                    case OWNER_DEPT:
                        formPrivilegeDataScopeDomain.getDeptIdList().addAll(user.getDeptIdList());
                        break;
                    case CUSTOM_DEPT:
                        formPrivilegeDataScopeDomain.getDeptIdList()
                                .addAll(formPrivilegeDataScope.getDataScopeList().stream()
                                        .map(c -> Long.valueOf(c.getBusinessId())).collect(Collectors.toList()));
                        break;
                    case OWNER:
                        formPrivilegeDataScopeDomain.getUserIdList().add(Long.valueOf(user.getUserId()));
                        break;
                    case CUSTOM_FILTER:
                        formPrivilegeDataScopeDomain.setFilter(formPrivilegeDataScope.getFilter());
                        break;
                    default:
                        break;
                }
            }
            if (CollectionUtils.isNotEmpty(formPrivilegeDataScopeDomain.getDeptIdList())) {
                List<Long> allChildren = DepartmentCache.getAllChildren(UserUtils.getUser().getCompanyId(),
                        formPrivilegeDataScopeDomain.getDeptIdList());
                formPrivilegeDataScopeDomain.setDeptIdList(allChildren);
            }
            formPrivilegeDataScopeDomainList.add(formPrivilegeDataScopeDomain);
        }
        return formPrivilegeDataScopeDomainList;
    }

    public static List<FormExtraFunctionButton> getButton(LowcodeDataVO lowcodeDataVO,
                                                          List<FormExtraFunctionVO> formExtraFunctionVOList,
                                                          String location) {
        List<FormExtraFunctionButton> formExtraFunctionButtonList = new ArrayList<>();
        for (FormExtraFunctionVO formExtraFunctionVO : formExtraFunctionVOList) {
            FormExtraFunctionButton formExtraFunctionButton =
                    JSONObject.parseObject(JSONObject.toJSONString(formExtraFunctionVO.getFormExtraFunctionButton()),
                            FormExtraFunctionButton.class);
            // if (!formExtraFunctionButton.getShowLocation().contains(location)) {
            //     continue;
            // }
            MongodbSearchFilter filter = formExtraFunctionButton.getFilter();
            if (CollectionUtils.isEmpty(filter.getConditionList())) {
                formExtraFunctionButton.setId(formExtraFunctionVO.getId());
                formExtraFunctionButton.setConforms(Boolean.TRUE);
                formExtraFunctionButtonList.add(formExtraFunctionButton);
            }
            String rel = filter.getRel();
            boolean add = Boolean.FALSE;
            JSONObject jsonObject = FormSystemFieldEnum.putSystemValue(lowcodeDataVO);
            for (MongodbSearchCondition mongodbSearchCondition : filter.getConditionList()) {
                Boolean conform = MongoSearchUtils.checkData(mongodbSearchCondition, jsonObject);
                if (rel.equals(SearchFilterRelEnum.AND.name())) {
                    add = conform;
                    if (!conform) {
                        break;
                    }
                } else {
                    if (conform) {
                        add = Boolean.TRUE;
                    }
                }
            }
            if (add) {
                if ("QRCODE".equals(formExtraFunctionButton.getTriggeredType()) &&
                        Lists.newArrayList("create", "update")
                                .contains(formExtraFunctionButton.getAction().toLowerCase())) {
                    FormPublicPublishVO config = FormPublicPublishCache.getConfig(
                            formExtraFunctionButton.getBusinessId() + "_" + lowcodeDataVO.getApplicationId());
                    if (config == null || (short) 0 == config.getState()) {
                        formExtraFunctionButton.setConforms(Boolean.TRUE);
                        formExtraFunctionButton.setDeactivationDisplayMode("2");
                        formExtraFunctionButton.setDeactivationDisplayTip("目标表单未公开发布");
                    } else {
                        formExtraFunctionButton.setConforms(Boolean.TRUE);
                    }
                    formExtraFunctionButton.setId(formExtraFunctionVO.getId());
                    formExtraFunctionButtonList.add(formExtraFunctionButton);
                } else {
                    formExtraFunctionButton.setConforms(Boolean.TRUE);
                    formExtraFunctionButton.setId(formExtraFunctionVO.getId());
                    formExtraFunctionButtonList.add(formExtraFunctionButton);
                }
            } else {
                if ("2".equals(formExtraFunctionButton.getDeactivationDisplayMode())) {
                    formExtraFunctionButton.setConforms(Boolean.FALSE);
                    formExtraFunctionButton.setId(formExtraFunctionVO.getId());
                    formExtraFunctionButtonList.add(formExtraFunctionButton);
                }
            }
        }
        return formExtraFunctionButtonList;
    }

    public static List<FormPrivilegeFieldConfig> getFormPrivilegeFieldConfigs(FormPrivilegeVO formPrivilegeVO,
                                                                              FormVO viewForm) {
        List<FormPrivilegeFieldConfig> fieldPrivilegeList = new ArrayList<>();
        if (formPrivilegeVO != null && CollectionUtils.isNotEmpty(formPrivilegeVO.getFieldPrivilegeList())) {
            fieldPrivilegeList = formPrivilegeVO.getFieldPrivilegeList();
        } else {
            if (viewForm != null) {
                FormViewConfig formViewConfig = JSONObject.parseObject(viewForm.getConfig(), FormViewConfig.class);
                if (formViewConfig != null && CollectionUtils.isNotEmpty(formViewConfig.getFields())) {
                    fieldPrivilegeList = formViewConfig.getFields();
                }
            }
        }
        return fieldPrivilegeList;
    }

    public static void dealPrivilege(FormPrivilegeEntity formPrivilegeEntity, String formPrivilegeEntity1) {
        formPrivilegeEntity.setUserPrivilege("ALL");
        if (StringUtils.isNotEmpty(formPrivilegeEntity1)) {
            List<FormPrivilegeDataScope> formPrivilegeDataScopes =
                    JSONArray.parseArray(formPrivilegeEntity1, FormPrivilegeDataScope.class);
            if (CollectionUtils.isNotEmpty(formPrivilegeDataScopes)) {
                if (formPrivilegeDataScopes.stream().map(FormPrivilegeDataScope::getDataScopeType)
                        .collect(Collectors.toList()).contains("CUSTOM_DEPT")) {
                    formPrivilegeEntity.setDataScope("[{\"dataScopeType\":\"ALL\"}]");
                }
                FormPrivilegeDataScope formPrivilegeDataScope = formPrivilegeDataScopes.stream()
                        .filter(c -> FormPrivilegeDataScopeTypeEnum.CUSTOM_FILTER.name().equals(c.getDataScopeType()))
                        .findFirst().orElse(null);
                if (formPrivilegeDataScope != null && formPrivilegeDataScope.getFilter() != null) {
                    if (CollectionUtils.isNotEmpty(formPrivilegeDataScope.getFilter().getConditionList())) {
                        formPrivilegeDataScope.getFilter().getConditionList().stream()
                                .filter(c -> MongodbSearchConditionQuoteTypeEnum.PRIVILEGE.name()
                                        .equals(c.getQuoteType())).findFirst().ifPresent(
                                        mongodbSearchCondition -> formPrivilegeEntity.setDataScope(
                                                "[{\"dataScopeType\":\"ALL\"}]"));
                    }
                }
            }
        }
    }
}
