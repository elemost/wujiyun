package com.wuji.service.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.cache.CompanyCache;
import com.wuji.admin.cache.DepartmentCache;
import com.wuji.common.enums.DeptDefaultEnum;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.enums.RoleDefaultEnum;
import com.wuji.common.enums.UserDefaultEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDeptUtils {
    /**
     * 转化 当前部门 当前用户
     *
     * @param fieldType
     * @param fieldId
     * @param value
     * @return
     */
    public static List<Object> dealUserDept(String fieldType, String fieldId, Object value) {
        if (FormFieldTypeEnum.checkUserField(fieldType, fieldId)) {
            List<Object> returnList = new ArrayList<>();
            List<FormUser> formUserList = JSONArray.parseArray(JSONArray.toJSONString(value), FormUser.class);
            if (formUserList == null) {
                formUserList = new ArrayList<>();
            }
            for (FormUser formUser : formUserList) {
                if (Objects.equals(formUser.getAssigneeId(), UserDefaultEnum.CURRENT_USER.getId())) {
                    returnList.add(FormUser.getCurrent(UserUtils.getUser()));
                } else {
                    returnList.add(formUser);
                }
            }
            return returnList;
        } else if (FormFieldTypeEnum.checkDeptField(fieldType, fieldId)) {
            List<FormDept> formDeptList = JSONArray.parseArray(JSONObject.toJSONString(value), FormDept.class);
            if (formDeptList == null) {
                formDeptList = new ArrayList<>();
            }
            List<Object> returnList = new ArrayList<>();
            for (FormDept formDept : formDeptList) {
                if (Objects.equals(formDept.getValue(), DeptDefaultEnum.CURRENT_DEPT.getId())) {
                    List<FormDept> defaultDept = getDefaultDept(fieldType);
                    returnList.addAll(defaultDept);
                } else if (Objects.equals(formDept.getValue(), DeptDefaultEnum.CURRENT_ALL_DEPT.getId())) {
                    List<FormDept> defaultDept = getDefaultDept(fieldType);
                    List<Long> deptIdList = defaultDept.stream().map(FormDept::getValue).collect(Collectors.toList());
                    List<Long> allChildren =
                            DepartmentCache.getAllChildren(UserUtils.getUser().getCompanyId(), deptIdList);
                    for (Long deptId : allChildren) {
                        FormDept formDept1 = new FormDept();
                        formDept1.setValue(deptId);
                        returnList.add(formDept1);
                    }
                } else if (Objects.equals(formDept.getValue(), DeptDefaultEnum.CURRENT_PARENT_DEPT.getId())) {
                    List<Long> allChildren = DepartmentCache.getAllChildren(UserUtils.getUser().getCompanyId(),
                            UserUtils.getUser().getDataScopeDeptIdList());
                    for (Long deptId : allChildren) {
                        FormDept formDept1 = new FormDept();
                        formDept1.setValue(deptId);
                        returnList.add(formDept1);
                    }
                } else {
                    returnList.add(formDept);
                }
            }
            return returnList;
        } else if (FormFieldTypeEnum.FORM_INPUT_USER_SINGLE_USED.getFieldType().equals(fieldType)) {
            List<Object> returnList = new ArrayList<>();
            List<Object> formUserList = JSONArray.parseArray(JSONArray.toJSONString(value), Object.class);
            if (formUserList == null) {
                formUserList = new ArrayList<>();
            }
            for (Object object : formUserList) {
                if (object instanceof LinkedHashMap || object instanceof JSONObject) {
                    FormUser formUser = JSONObject.parseObject(JSONObject.toJSONString(object), FormUser.class);
                    if (Objects.equals(formUser.getAssigneeId(), UserDefaultEnum.CURRENT_USER.getId())) {
                        returnList.add(FormUser.getCurrent(UserUtils.getUser()));
                    } else {
                        returnList.add(formUser);
                    }
                } else {
                    if (Objects.equals(object.toString(), UserDefaultEnum.CURRENT_USER.getId().toString())) {
                        returnList.add(FormUser.getCurrent(UserUtils.getUser()));
                    } else {
                        returnList.add(object);
                    }
                }
            }
            return returnList;
        } else if (FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE_USED.getFieldType().equals(fieldType)) {
            List<Object> returnList = new ArrayList<>();
            List<Object> formDeptList = JSONArray.parseArray(JSONArray.toJSONString(value), Object.class);
            if (formDeptList == null) {
                formDeptList = new ArrayList<>();
            }
            for (Object object : formDeptList) {
                if (object instanceof LinkedHashMap || object instanceof JSONObject) {
                    FormDept formDept = JSONObject.parseObject(JSONObject.toJSONString(object), FormDept.class);
                    if (Objects.equals(formDept.getValue(), DeptDefaultEnum.CURRENT_DEPT.getId())) {
                        returnList.add(FormUser.getCurrent(UserUtils.getUser()));
                    } else {
                        returnList.add(formDept);
                    }
                } else {
                    if (Objects.equals(object.toString(), DeptDefaultEnum.CURRENT_DEPT.getId().toString())) {
                        returnList.add(FormUser.getCurrent(UserUtils.getUser()));
                    } else {
                        returnList.add(object);
                    }
                }
            }
            return returnList;
        } else if (FormFieldTypeEnum.FORM_INPUT_ROLE_SINGLE.getFieldType().equals(fieldType)) {
            if (value == null) {
                return null;
            }
            List<Object> roleIdList = new ArrayList<>();
            List<Long> roleIds = JSONArray.parseArray(JSONArray.toJSONString(value), Long.class);
            if (CollectionUtils.isNotEmpty(roleIds)) {
                if (Objects.equals(roleIds.get(0), RoleDefaultEnum.CURRENT_ROLE.getId())) {
                    roleIdList.add(UserUtils.getUser().getPostIdList().get(0));
                } else {
                    roleIdList.addAll(roleIds);
                }
            }
            return roleIdList;
        } else if (FormFieldTypeEnum.FORM_INPUT_ROLE_MULTIPLE.getFieldType().equals(fieldType)) {
            List<Object> returnList = new ArrayList<>();
            List<Long> roleIds = JSONArray.parseArray(JSONArray.toJSONString(value), Long.class);
            if (roleIds == null) {
                roleIds = new ArrayList<>();
            }
            for (Long roleId : roleIds) {
                if (Objects.equals(RoleDefaultEnum.CURRENT_ROLE.getId(), roleId)) {
                    if (CollectionUtils.isNotEmpty(UserUtils.getUser().getPostIdList())) {
                        returnList.addAll(UserUtils.getUser().getPostIdList());
                    }
                } else {
                    returnList.add(roleId);
                }
                returnList = returnList.stream().distinct().collect(Collectors.toList());
            }
            return returnList;
        }
        return null;
    }

    /**
     * 获取默认公司value
     *
     * @param fieldType 字段类型
     * @return 部门列表
     */
    public static List<FormDept> getDefaultDept(String fieldType) {
        List<FormDept> returnList = new ArrayList<>();
        List<FormDept> currentDept = FormDept.getCurrentDept(UserUtils.getUser());
        if (CollectionUtils.isEmpty(currentDept)) {
            FormDept company = new FormDept();
            company.setValue(0L);
            company.setLabel(CompanyCache.getCompany(UserUtils.getUser().getCompanyId()).getCompanyName());
            returnList.add(company);
        } else {
            if (FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE.getFieldType().equals(fieldType)) {
                returnList.add(currentDept.get(0));
            } else {
                returnList.addAll(currentDept);
            }
        }
        return returnList;
    }
}
