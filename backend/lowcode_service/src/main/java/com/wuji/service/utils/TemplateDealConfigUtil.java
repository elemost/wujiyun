package com.wuji.service.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.constant.FormConfigKeyConstants;
import com.wuji.service.enums.FormExtraFunctionTypeEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.model.entity.FormExtraFunctionEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TemplateDealConfigUtil {
    public static String deal(String sourceApplicationId, String templateApplicationId, String moduleType,
                              String config, String applicationId) {
        UserDomain user = UserUtils.getUser();
        JSONObject configJson = JSONObject.parseObject(config);
        if (configJson == null) {
            return null;
        }
        if ("METRIC_TABLE".equals(moduleType)) {
            dealUserDeptFilterConfig(configJson, user);
        } else if ("detail_table".equalsIgnoreCase(moduleType)) {
            dealUserDeptFilterConfig(configJson, user);
        } else if ("quickEntrance".equalsIgnoreCase(moduleType)) {
            dealQuickEntrance(sourceApplicationId, templateApplicationId, applicationId, configJson);
        } else {
            return config;
        }
        return JSONObject.toJSONString(configJson);
    }

    public static String dealWhileCopy(String sourceApplicationId, String moduleType, String config,
                                       String applicationId) {
        JSONObject configJson = JSONObject.parseObject(config);
        if (configJson == null) {
            return null;
        }
        if ("quickEntrance".equalsIgnoreCase(moduleType)) {
            dealQuickEntrance(sourceApplicationId, sourceApplicationId, applicationId, configJson);
        } else {
            return config;
        }
        return JSONObject.toJSONString(configJson);
    }

    private static void dealQuickEntrance(String sourceApplicationId, String templateApplicationId,
                                          String applicationId, JSONObject configJson) {
        JSONArray entranceList = configJson.getJSONArray("entranceList");
        JSONArray list = new JSONArray();
        for (int i = 0; i < entranceList.size(); i++) {
            JSONObject entranceJson = entranceList.getJSONObject(i);
            String id = entranceJson.getString("id");
            String application = entranceJson.getString("applicationId");
            if (StringUtils.isEmpty(application)) {
                if (id.equals(sourceApplicationId)) {
                    entranceJson.put("id", templateApplicationId);
                    list.add(entranceJson);
                }
            } else {
                if (application.equals(sourceApplicationId)) {
                    entranceJson.put("applicationId", applicationId);
                    list.add(entranceJson);
                }
            }

        }
        configJson.put("entranceList", list);
    }

    private static void dealUserDeptFilterConfig(JSONObject configJson, UserDomain user) {
        JSONObject widgetJson = configJson.getJSONObject("widget");
        Object filterObject = widgetJson.get("filter");
        dealFilter(user, filterObject, widgetJson);
        configJson.put("widget", widgetJson);
    }

    public static String dealAggregateConfig(JSONObject configJson, UserDomain user) {
        if (configJson == null) {
            return null;
        }
        JSONObject formAggregateTable = configJson.getJSONObject("formAggregateTable");
        if (formAggregateTable == null) {
            return JSONObject.toJSONString(configJson);
        }
        Object filterObject = formAggregateTable.get("filter");
        dealFilter(user, filterObject, formAggregateTable);
        configJson.put("formAggregateTable", formAggregateTable);
        return JSONObject.toJSONString(configJson);
    }

    private static void dealFilter(UserDomain user, Object filterObject, JSONObject widgetJson) {
        if (filterObject != null) {
            JSONObject filterJson = widgetJson.getJSONObject("filter");
            JSONArray conditionList = filterJson.getJSONArray("conditionList");
            for (int i = 0; i < conditionList.size(); i++) {
                JSONObject conditionJson = conditionList.getJSONObject(i);
                String type = conditionJson.getString("type");
                String name = conditionJson.getString("fieldId");
                if (FormFieldTypeEnum.getUserFieldType().contains(type) ||
                        name.equals(FormSystemFieldEnum.CREATE_NAME.getName())) {
                    JSONArray valueList = conditionJson.getJSONArray("value");
                    if (!valueList.isEmpty()) {
                        conditionJson.put("value", Collections.singletonList(FormUser.getCurrent(user)));
                    }
                } else if (FormFieldTypeEnum.getDeptFieldType().contains(type)) {
                    JSONArray valueList = conditionJson.getJSONArray("value");
                    if (!valueList.isEmpty()) {
                        conditionJson.put("value", FormDept.getCurrentDept(user));
                    }
                }
            }
            filterJson.put("conditionList", conditionList);
            widgetJson.put("filter", filterJson);
        }
    }

    public static String dealConfig(String config) {
        if (StringUtils.isEmpty(config)) {
            return config;
        }
        JSONObject configJson = JSONObject.parseObject(config);
        String body = configJson.getString("body");
        if (StringUtils.isEmpty(body)) {
            return config;
        }
        JSONArray jsonArray = JSONArray.parseArray(body);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String type = jsonObject.getString(FormConfigKeyConstants.TYPE);
            if (FormFieldTypeEnum.getDeptFieldType().contains(type) ||
                    FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE_USED.getFieldType().equals(type)) {
                String range = jsonObject.getString(FormConfigKeyConstants.DEPT_RANGE);
                if ("custom".equals(range)) {
                    Object cusDept = jsonObject.get(FormConfigKeyConstants.CUS_DEPT);
                    if (cusDept != null) {
                        jsonObject.put(FormConfigKeyConstants.CUS_DEPT, FormDept.getDefaultDept());
                    }
                }
                String defaultValueType = jsonObject.getString(FormConfigKeyConstants.DEFAULT_VALUE_TYPE);
                if ("custom".equals(defaultValueType)) {
                    Object value = jsonObject.get(FormConfigKeyConstants.VALUE);
                    if (value != null) {
                        jsonObject.put(FormConfigKeyConstants.VALUE, FormDept.getDefaultDept());
                    }
                }
            } else if (FormFieldTypeEnum.getUserFieldType().contains(type) ||
                    FormFieldTypeEnum.FORM_INPUT_USER_SINGLE_USED.getFieldType().equals(type)) {
                String range = jsonObject.getString(FormConfigKeyConstants.DEPT_RANGE);
                if ("custom".equals(range)) {
                    Object cusDept = jsonObject.get(FormConfigKeyConstants.CUS_DEPT);
                    if (cusDept != null) {
                        jsonObject.put(FormConfigKeyConstants.CUS_DEPT, FormUser.getDefaultUser());
                    }
                }
                String defaultValueType = jsonObject.getString(FormConfigKeyConstants.DEFAULT_VALUE_TYPE);
                if ("custom".equals(defaultValueType)) {
                    Object value = jsonObject.get(FormConfigKeyConstants.VALUE);
                    if (value != null) {
                        jsonObject.put(FormConfigKeyConstants.VALUE, FormUser.getDefaultUser());
                    }
                }
            }
        }
        configJson.put("body", jsonArray);
        return JSONObject.toJSONString(configJson);
    }

    public static String dealFunctionConfig(String config) {
        JSONObject configJson = JSONObject.parseObject(config);
        if (configJson == null) {
            return config;
        }
        JSONObject filterJson = configJson.getJSONObject("filter");
        if (filterJson == null) {
            return config;
        }
        JSONArray conditionList = filterJson.getJSONArray("conditionList");
        for (int i = 0; i < conditionList.size(); i++) {
            JSONObject conditionJson = conditionList.getJSONObject(i);
            String type = conditionJson.getString(FormConfigKeyConstants.TYPE);
            String name = conditionJson.getString("fieldId");
            if (FormFieldTypeEnum.getUserFieldType().contains(type) ||
                    name.equals(FormSystemFieldEnum.CREATE_NAME.getName())) {
                JSONArray valueList = conditionJson.getJSONArray("value");
                if (!valueList.isEmpty()) {
                    conditionJson.put("value", FormUser.getDefaultUserObject());
                }
            } else if (FormFieldTypeEnum.getDeptFieldType().contains(type)) {
                JSONArray valueList = conditionJson.getJSONArray("value");
                if (!valueList.isEmpty()) {
                    conditionJson.put("value", FormDept.getDefaultDeptObject());
                }
            }
        }
        filterJson.put("conditionList", conditionList);
        configJson.put("filter", filterJson);
        JSONObject actionConfig = configJson.getJSONObject("actionConfig");
        JSONArray actions = actionConfig.getJSONArray("actions");
        if (actions != null) {
            for (int i = 0; i < actions.size(); i++) {
                JSONObject action = actions.getJSONObject(i);
                String type = action.getString("itemType");
                String name = action.getString("currentName");
                String setting = action.getString("setting");
                if ("custom".equals(setting)) {
                    if (FormFieldTypeEnum.getUserFieldType().contains(type) ||
                            name.equals(FormSystemFieldEnum.CREATE_NAME.getName())) {
                        JSONArray valueList = action.getJSONArray("value");
                        if (!valueList.isEmpty()) {
                            // JSONObject jsonObject = new JSONObject();
                            // jsonObject.put("label", "当前用户");
                            // jsonObject.put("value", 9999L);
                            // action.put("value", Collections.singletonList(jsonObject));
                            action.put("value", FormUser.getDefaultUser());
                        }
                    } else if (FormFieldTypeEnum.getDeptFieldType().contains(type)) {
                        JSONArray valueList = action.getJSONArray("value");
                        if (!valueList.isEmpty()) {
                            action.put("value", FormDept.getDefaultDept());
                        }
                    }
                }
            }
        }
        actionConfig.put("actions", actions);
        configJson.put("actionConfig", actionConfig);
        return JSONObject.toJSONString(configJson);
    }

    public static void buildConfig(List<FormExtraFunctionEntity> formExtraFunctionEntityList,
                                    Map<String, String> functionIdMap, Map<String, String> dataStreamIdMap) {
        for (FormExtraFunctionEntity formExtraFunctionEntity : formExtraFunctionEntityList) {
            if (FormExtraFunctionTypeEnum.CUSTOM_BUTTON.name().equals(formExtraFunctionEntity.getFunctionType())) {
                JSONObject jsonObject = JSONObject.parseObject(formExtraFunctionEntity.getConfig());
                String action = jsonObject.getString("action");
                if ("info".equals(action)) {
                    String businessId = jsonObject.getString("businessId");
                    jsonObject.put("businessId", functionIdMap.get(businessId));
                } else if ("INTELLECTUAL_ASSISTANT".equals(action)) {
                    String assistantId = jsonObject.getString("assistantId");
                    String newDataStreamId = dataStreamIdMap.get(assistantId);
                    if (newDataStreamId != null && assistantId != null) {
                        jsonObject.put("assistantId", newDataStreamId);
                    }
                }
                formExtraFunctionEntity.setConfig(jsonObject.toJSONString());
            }
        }
    }
}
