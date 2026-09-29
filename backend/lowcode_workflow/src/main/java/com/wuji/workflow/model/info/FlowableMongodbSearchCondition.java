package com.wuji.workflow.model.info;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FlowableMongodbSearchCondition {

    private String fieldId;

    private String childFieldId;

    private String method;

    private String type;

    private String searchType;

    // CURRENT_USER CURRENT_DEPT
    private String valueType;

    private List<Object> value = new ArrayList<>();

    private String formId;

    // public List<Object> value() {
    //     if ("CURRENT_USER".equals(valueType)) {
    //         value.add(FormUser.getCurrent(UserUtils.getUser()));
    //     } else if ("CURRENT_DEPT".equals(valueType)) {
    //         List<FormDept> currentDept = FormDept.getCurrentDept(UserUtils.getUser());
    //         value.addAll(currentDept);
    //     } else {
    //         if (FormFieldTypeEnum.checkUserField(this.type, this.fieldId)) {
    //             List<Object> returnList = new ArrayList<>();
    //             List<FormUser> formUserList = JSONArray.parseArray(JSONObject.toJSONString(value), FormUser.class);
    //             for (FormUser formUser : formUserList) {
    //                 if (UserDefaultEnum.CURRENT_USER.getId().equals(formUser.getAssigneeId())) {
    //                     returnList.add(FormUser.getCurrent(UserUtils.getUser()));
    //                 } else {
    //                     returnList.add(formUser);
    //                 }
    //             }
    //             return returnList;
    //         } else if (FormFieldTypeEnum.checkDeptField(this.type, this.fieldId)) {
    //             List<FormDept> formDeptList = JSONArray.parseArray(JSONObject.toJSONString(value), FormDept.class);
    //             List<Object> returnList = new ArrayList<>();
    //             for (FormDept formDept : formDeptList) {
    //                 if (DeptDefaultEnum.CURRENT_DEPT.getId().equals(formDept.getValue())) {
    //                     List<FormDept> currentDept = FormDept.getCurrentDept(UserUtils.getUser());
    //                     formDeptList.addAll(currentDept);
    //                 } else {
    //                     returnList.add(formDept);
    //                 }
    //             }
    //             return returnList;
    //         }
    //     }
    //     return value;
    // }
}
