package com.wuji.service.enums;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wuji.common.model.info.FormUser;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.vo.LowcodeDataVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum FormSystemFieldEnum {
    CREATE_TIME("提交时间", "system", "createTime", "createTimeString"),
    UPDATE_TIME("更新时间", "system", "modifyTime", "modifyTimeString"),
    STATUS("状态", "system", "status", "statusName"), CURRENT_NODE("当前节点", "system", "taskName", "taskName"),
    ASSIGNEE_NAME("当前负责人", "system", "currentAssignee", "currentAssignee"),
    CREATE_NAME("提交人", "system", "creator", "creatorName"), UUID("data_uuid", "system", "uuid", "uuid"),
    ;

    private final String label;

    private final String type;

    private final String name;

    private final String alias;

    public static List<String> getTimeField() {
        return Lists.newArrayList(CREATE_TIME.getName(), UPDATE_TIME.getName());
    }

    public static FormSystemFieldEnum getByName(String name) {
        for (FormSystemFieldEnum formSystemFieldEnum : FormSystemFieldEnum.values()) {
            if (formSystemFieldEnum.name.equals(name)) {
                return formSystemFieldEnum;
            }
        }
        return null;
    }

    public static List<FormConfigCommon> getFormSystemField() {
        List<FormConfigCommon> formConfigCommonList = new ArrayList<>();
        for (FormSystemFieldEnum formSystemFieldEnum : Lists.newArrayList(CREATE_TIME, UPDATE_TIME, STATUS,
                CREATE_NAME)) {
            FormConfigCommon formConfigCommon = new FormConfigCommon();
            formConfigCommon.setSystem(Boolean.TRUE);
            formConfigCommon.setType(formSystemFieldEnum.type);
            formConfigCommon.setLabel(formSystemFieldEnum.label);
            formConfigCommon.setName(formSystemFieldEnum.name);
            formConfigCommonList.add(formConfigCommon);
        }
        return formConfigCommonList;
    }

    public static List<FormConfigCommon> getFlowableFormSystemField() {
        List<FormConfigCommon> formConfigCommonList = new ArrayList<>();
        for (FormSystemFieldEnum formSystemFieldEnum : Lists.newArrayList(CREATE_TIME, UPDATE_TIME, STATUS,
                CURRENT_NODE, ASSIGNEE_NAME, CREATE_NAME)) {
            FormConfigCommon formConfigCommon = new FormConfigCommon();
            formConfigCommon.setSystem(Boolean.TRUE);
            formConfigCommon.setType(formSystemFieldEnum.type);
            formConfigCommon.setLabel(formSystemFieldEnum.label);
            formConfigCommon.setName(formSystemFieldEnum.name);
            formConfigCommonList.add(formConfigCommon);
        }
        return formConfigCommonList;
    }

    public static FormConfigCommon dataUuidCommon() {
        FormSystemFieldEnum formSystemFieldEnum = UUID;
        FormConfigCommon formConfigCommon = new FormConfigCommon();
        formConfigCommon.setSystem(Boolean.TRUE);
        formConfigCommon.setType(formSystemFieldEnum.type);
        formConfigCommon.setLabel(formSystemFieldEnum.label);
        formConfigCommon.setName(formSystemFieldEnum.name);
        return formConfigCommon;
    }

    public static List<String> getUserFieldList() {
        return Lists.newArrayList(CREATE_NAME.getName());
    }

    public static JSONObject putSystemValue(LowcodeDataDomain lowcodeDataDomain) {
        JSONObject returnJson = new JSONObject();
        returnJson.putAll(lowcodeDataDomain.getInstValue());
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(lowcodeDataDomain));
        for (FormSystemFieldEnum formSystemFieldEnum : FormSystemFieldEnum.values()) {
            returnJson.put(formSystemFieldEnum.name, jsonObject.get(formSystemFieldEnum.name));
        }
        return returnJson;
    }

    public static JSONObject putSystemValue(LowcodeDataVO lowcodeDataVO) {
        JSONObject returnJson = new JSONObject();
        returnJson.putAll(lowcodeDataVO.getInstValue());
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(lowcodeDataVO));
        for (FormSystemFieldEnum formSystemFieldEnum : FormSystemFieldEnum.values()) {
            if (CREATE_NAME == formSystemFieldEnum) {
                if (lowcodeDataVO.getCreator() != null) {
                    FormUser formUser = new FormUser();
                    formUser.setAssigneeName(lowcodeDataVO.getCreatorName());
                    formUser.setAssigneeId(Long.valueOf(lowcodeDataVO.getCreator()));
                    returnJson.put(formSystemFieldEnum.name, formUser);
                }
            } else {
                returnJson.put(formSystemFieldEnum.name, jsonObject.get(formSystemFieldEnum.name));
            }
        }
        return returnJson;
    }
}
