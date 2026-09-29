package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormRole;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.utils.JsonObjectUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.model.domain.FormMongoDbExportDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.vo.FormImportCheckResultVO;
import com.wuji.service.model.vo.FormSyncCheckResultVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.model.vo.form.FormSubmitCheck;
import com.wuji.service.service.FormDataService;
import com.wuji.service.utils.ExcelUtils;
import com.wuji.service.utils.UserDeptUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormRoleMultipleServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.FORM_INPUT_ROLE_MULTIPLE.getFieldType();
    }

    @Override
    public void dealWhileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        Object object = instValue.get(formConfigCommon.getName());
        if (object == null) {
            return;
        }
        List<Object> objectList =
                UserDeptUtils.dealUserDept(formConfigCommon.getType(), formConfigCommon.getName(), object);
        if (CollectionUtils.isEmpty(objectList)) {
            instValue.put(formConfigCommon.getName(), null);
        } else {
            instValue.put(formConfigCommon.getName(), objectList);
        }
    }

    @Override
    public void dealWhileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        Object object = instValue.get(formConfigCommon.getName());
        if (object == null) {
            return;
        }
        List<Object> objectList =
                UserDeptUtils.dealUserDept(formConfigCommon.getType(), formConfigCommon.getName(), object);
        if (CollectionUtils.isEmpty(objectList)) {
            instValue.put(formConfigCommon.getName(), null);
        } else {
            instValue.put(formConfigCommon.getName(), objectList);
        }
    }

    @Override
    public FormImportCheckResultVO dealWhileImport(FormConfigCommon formConfigCommon, Object value,
                                                   SystemAllDataNameVO importCheck) {
        FormImportCheckResultVO formImportCheckResultVO = new FormImportCheckResultVO();
        List<Long> roleIds = new ArrayList<>();
        List<String> notExist = new ArrayList<>();
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            List<String> roleNames = Arrays.stream(value.toString().split("，")).collect(Collectors.toList());
            for (String roleName : roleNames) {
                PostVO roleVO = importCheck.getRoleMap().get(roleName);
                if (roleVO != null) {
                    roleIds.add(roleVO.getPostId());
                } else {
                    notExist.add(roleName);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(notExist)) {
            formImportCheckResultVO.setErrorMessage("这些用户不存在：" + String.join("，", notExist));
        } else {
            formImportCheckResultVO.setValue(roleIds);
        }
        return formImportCheckResultVO;
    }

    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                               SystemAllDataNameVO importCheck) {
        if (value == null) {
            return new FormSyncCheckResultVO();
        }
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(value));
        List<Long> roleIds = new ArrayList<>();
        if (jsonArray != null) {
            List<String> roleNames = jsonArray.toJavaList(String.class);
            for (String roleName : roleNames) {
                PostVO roleVO = importCheck.getRoleMap().get(roleName);
                if (roleVO != null) {
                    roleIds.add(roleVO.getPostId());
                } else {
                    throw new ServiceException(ServiceResultCode.PARAM_ERROR, "角色不存在：" + roleName);
                }
            }
        }
        FormSyncCheckResultVO formSyncCheckResultVO = new FormSyncCheckResultVO();
        formSyncCheckResultVO.setValue(roleIds);
        return formSyncCheckResultVO;
    }

    @Override
    public void dealWhileSend(FormExtraFunctionSync formExtraFunctionSync, JSONObject instValue,
                              SystemAllDataVO systemAllData, JSONObject sendJson) {
        Object value = instValue.get(formExtraFunctionSync.getName());
        if (value == null) {
            return;
        }
        Map<Long, PostVO> roleIdMap = systemAllData.getRoleIdMap();
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(value));
        List<Long> roleIdList = jsonArray.toJavaList(Long.class);
        List<String> roleNameList = new ArrayList<>();
        for (Long roleId : roleIdList) {
            PostVO roleVO = roleIdMap.get(roleId);
            if (roleVO != null) {
                roleNameList.add(roleVO.getPostName());
            }
        }
        sendJson.put(formExtraFunctionSync.getMappingField(), roleNameList);
    }

    @Override
    public void dealWhileReturn(List<LowcodeDataVO> lowcodeDataList, FormConfigCommon formConfigCommon, FormVO info,
                                SystemAllDataVO systemAllDataVO) {
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            JSONObject instValue = lowcodeDataVO.getInstValue();
            JSONArray jsonArray = JsonObjectUtils.getJsonArray(instValue, formConfigCommon.getName());
            List<Long> roleIdList = jsonArray.toJavaList(Long.class);
            List<FormRole> formRoles = new ArrayList<>();
            if (CollectionUtils.isEmpty(roleIdList)) {
                continue;
            }
            for (Long roleId : roleIdList) {
                PostVO roleVO = systemAllDataVO.getRoleIdMap().get(roleId);
                if (roleVO != null) {
                    FormRole formRole = new FormRole();
                    formRole.setRoleId(roleId);
                    formRole.setRoleName(roleVO.getPostName());
                    formRoles.add(formRole);
                }
            }
            instValue.put(formConfigCommon.getName(), formRoles);
        }
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        JSONArray value = instValue.getJSONArray(formConfigCommon.getName());
        List<Long> roleIds = JSONArray.parseArray(JSONArray.toJSONString(value), Long.class);
        Object dealValue = null;
        if (CollectionUtils.isNotEmpty(roleIds)) {
            List<String> roleNameList = new ArrayList<>();
            for (Long roleId : roleIds) {
                PostVO roleVO = systemAllDataVO.getRoleIdMap().get(roleId);
                if (roleVO != null) {
                    roleNameList.add(roleVO.getPostName());
                }
            }
            dealValue = String.join("，", roleNameList);
        }
        putValue(instValue, dealValue, formId, formConfigCommon);
    }

    @Override
    public void dealWhileExport(int column, Row row, JSONObject instValue,
                                FormMongoDbExportDomain formMongoDbExportDomain, SystemAllDataVO systemAllDataVO) {
        JSONArray value = instValue.getJSONArray(formMongoDbExportDomain.getName());
        List<FormRole> roles = JSONArray.parseArray(JSONArray.toJSONString(value), FormRole.class);
        List<String> roleNames = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roles)) {
            for (FormRole formRole : roles) {
                roleNames.add(formRole.getRoleName());
            }
        }
        if (CollectionUtils.isNotEmpty(roleNames)) {
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, String.join("，", roleNames));
        } else {
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, "");
        }
    }

    @Override
    public void dealWhileUseTemplate(JSONObject jsonObject, FormConfigCommon formConfigCommon, FormUser current,
                                     List<FormDept> currentDept, Boolean needTrans) {
        jsonObject.put(formConfigCommon.getName(), UserUtils.getUser().getPostIdList());
    }

    @Override
    public String transValue(List<Object> values, String fieldId, String fieldType, SystemAllDataVO systemAllDataVO) {
        List<FormUser> formUserList = JSONArray.parseArray(JSONArray.toJSONString(values), FormUser.class);
        List<String> assigneeNameList = new ArrayList<>();
        for (FormUser formUser : formUserList) {
            if (formUser == null) {
                continue;
            }
            assigneeNameList.add(formUser.getAssigneeName());
        }
        return StringUtils.join(assigneeNameList, "，");
    }

    @Override
    public void dealDetailedReturn(MongodbSearchField mongodbSearchField, List<JSONObject> jsonObjects,
                                   SystemAllDataVO systemAllDataVO) {
        for (JSONObject jsonObject : jsonObjects) {
            JSONObject instValue = jsonObject.getJSONObject("instValue");
            JSONArray jsonArray = JsonObjectUtils.getJsonArray(instValue, mongodbSearchField.getName());
            List<Long> roleIdList = jsonArray.toJavaList(Long.class);
            List<FormRole> formRoles = new ArrayList<>();
            if (CollectionUtils.isEmpty(roleIdList)) {
                continue;
            }
            for (Long roleId : roleIdList) {
                PostVO roleVO = systemAllDataVO.getRoleIdMap().get(roleId);
                if (roleVO != null) {
                    FormRole formRole = new FormRole();
                    formRole.setRoleId(roleId);
                    formRole.setRoleName(roleVO.getPostName());
                    formRoles.add(formRole);
                }
            }
            instValue.put(mongodbSearchField.getName(), formRoles);
        }
    }
}
