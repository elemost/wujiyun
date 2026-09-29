package com.wuji.service.enums;

import com.google.common.collect.Lists;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.service.model.info.FormConfigCommon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum SystemDefaultFieldEnum {
    // applicationId formId uuid taskDefinitionKey
    // approvalPage/%s/%s/%s/%s
    AUDIT_URL("审批页面", "systemAuditUrl", "mobile/processCenter/waitting", FormFieldTypeEnum.INPUT_TEXT.getFieldType()),
    // applicationId formId uuid
    INFO_URL("表单详情页", "systemInfoUrl", "%s/web/independent/detail/%s/%s?groupId=admin_privilege",
                    FormFieldTypeEnum.INPUT_TEXT.getFieldType()),
    BUTTON_URL("自定义按钮操作页", "systemButtonUrl", "%s/web/independent/cusbuttonPage/%s?dataUuid=%s&buttonId=%s",
            FormFieldTypeEnum.INPUT_TEXT.getFieldType()),
    FLOWABLE_AUDIT_URL("审批页面", "systemFlowableAuditUrl", "approvalPage/%s/%s/%s/%s", FormFieldTypeEnum.INPUT_TEXT.getFieldType()),
    ;

    private final String label;

    private final String name;

    private final String url;

    private final String fieldType;

    public static List<String> urlKeyList() {
        return Arrays.stream(SystemDefaultFieldEnum.values()).map(SystemDefaultFieldEnum::getName)
                .collect(Collectors.toList());
    }

    public static List<FormConfigCommon> flowableSystemField() {
        List<FormConfigCommon> formConfigCommonList = new ArrayList<>();
        for (SystemDefaultFieldEnum systemDefaultFieldEnum : Lists.newArrayList(FLOWABLE_AUDIT_URL)) {
            FormConfigCommon formConfigCommon = new FormConfigCommon();
            formConfigCommon.setSystemUrl(Boolean.TRUE);
            formConfigCommon.setType(systemDefaultFieldEnum.fieldType);
            formConfigCommon.setLabel(systemDefaultFieldEnum.label);
            formConfigCommon.setName(systemDefaultFieldEnum.name);
            formConfigCommonList.add(formConfigCommon);
        }
        return formConfigCommonList;
    }

    public static List<FormConfigCommon> dataStreamSystemField() {
        List<FormConfigCommon> configList = new ArrayList<>();
        for (SystemDefaultFieldEnum systemDefaultFieldEnum : Lists.newArrayList(AUDIT_URL, INFO_URL, BUTTON_URL)) {
            FormConfigCommon formConfigCommon = new FormConfigCommon();
            formConfigCommon.setName(systemDefaultFieldEnum.getName());
            formConfigCommon.setLabel(systemDefaultFieldEnum.getLabel());
            formConfigCommon.setType(systemDefaultFieldEnum.getFieldType());
            formConfigCommon.setSystemUrl(Boolean.TRUE);
            configList.add(formConfigCommon);
        }
        return configList;
    }

    public static String getFinalUrl(String applicationId, String formId, String uuid, String buttonId, String name, String taskId) {
        switch (name) {
            case "systemAuditUrl":
                return AUDIT_URL.getUrl();
            case "systemInfoUrl":
                return String.format(INFO_URL.url, applicationId, formId, uuid);
            case "systemButtonUrl":
                return String.format(BUTTON_URL.url, applicationId, formId, uuid, buttonId);
            case "systemFlowableAuditUrl":
                return String.format(FLOWABLE_AUDIT_URL.url, applicationId, formId, uuid, taskId);
        }
        return null;
    }
}
