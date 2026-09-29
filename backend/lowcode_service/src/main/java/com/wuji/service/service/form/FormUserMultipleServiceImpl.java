package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.JsonObjectUtils;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.model.domain.FormMongoDbExportDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormExtraFunctionSync;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormUserMultipleServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.FORM_INPUT_USER_MULTIPLE.getFieldType();
    }

    @Override
    public void dealWhileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        JSONArray jsonArray = instValue.getJSONArray(formConfigCommon.getName());
        if (jsonArray == null) {
            return;
        }
        List<Object> objectList =
                UserDeptUtils.dealUserDept(formConfigCommon.getType(), formConfigCommon.getName(), jsonArray);
        instValue.put(formConfigCommon.getName(), objectList);
    }

    @Override
    public void dealWhileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        JSONArray jsonArray = instValue.getJSONArray(formConfigCommon.getName());
        if (jsonArray == null) {
            return;
        }
        List<Object> objectList =
                UserDeptUtils.dealUserDept(formConfigCommon.getType(), formConfigCommon.getName(), jsonArray);
        instValue.put(formConfigCommon.getName(), objectList);
    }

    @Override
    public FormImportCheckResultVO dealWhileImport(FormConfigCommon formConfigCommon, Object value,
                                                   SystemAllDataNameVO importCheck) {
        FormImportCheckResultVO formImportCheckResultVO = new FormImportCheckResultVO();
        List<FormUser> formUserList = new ArrayList<>();
        List<String> notExist = new ArrayList<>();
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            List<String> userNameList = Arrays.stream(value.toString().split("，")).collect(Collectors.toList());
            for (String userName : userNameList) {
                UserVO userVO = importCheck.getPhonenumberMap().get(userName);
                if (userVO != null) {
                    FormUser formUser = new FormUser();
                    formUser.setAssigneeId(userVO.getUserId());
                    formUser.setAssigneeName(userVO.getNickName());
                    formUserList.add(formUser);
                } else {
                    Map<String, List<UserCompanyVO>> nickNameMap = importCheck.getNickNameMap();
                    List<UserCompanyVO> userVOS = nickNameMap.get(userName);
                    if (CollectionUtils.isNotEmpty(userVOS)) {
                        UserCompanyVO userCompanyVO = userVOS.get(0);
                        FormUser formUser = new FormUser();
                        formUser.setAssigneeId(userCompanyVO.getUserId());
                        formUser.setAssigneeName(userCompanyVO.getNickName());
                        formUserList.add(formUser);
                    } else {
                        notExist.add(userName);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(notExist)) {
            formImportCheckResultVO.setErrorMessage("这些用户不存在：" + String.join("，", notExist));
        } else {
            formImportCheckResultVO.setValue(formUserList);
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
        List<FormUser> formUserList = new ArrayList<>();
        if (jsonArray != null) {
            List<String> phonenumberList = jsonArray.toJavaList(String.class);
            for (String phonenumber : phonenumberList) {
                UserVO userVO = importCheck.getPhonenumberMap().get(phonenumber);
                if (userVO != null) {
                    FormUser formUser = new FormUser();
                    formUser.setAssigneeId(userVO.getUserId());
                    formUser.setAssigneeName(userVO.getNickName());
                    formUserList.add(formUser);
                } else {
                    throw new ServiceException(ServiceResultCode.PARAM_ERROR, "用户不存在：" + phonenumber);
                }
            }
        }
        FormSyncCheckResultVO formSyncCheckResultVO = new FormSyncCheckResultVO();
        formSyncCheckResultVO.setValue(formUserList);
        return formSyncCheckResultVO;
    }

    @Override
    public void dealWhileSend(FormExtraFunctionSync formExtraFunctionSync, JSONObject instValue,
                              SystemAllDataVO systemAllData, JSONObject sendJson) {
        Object value = instValue.get(formExtraFunctionSync.getName());
        if (value == null) {
            return;
        }
        Map<Long, UserVO> userIdMap = systemAllData.getUserIdMap();
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(value));
        List<FormUser> formUserList = jsonArray.toJavaList(FormUser.class);
        List<String> phoneNumberList = new ArrayList<>();
        for (FormUser formUser : formUserList) {
            UserVO userVO = userIdMap.get(formUser.getAssigneeId());
            if (userVO != null) {
                phoneNumberList.add(userVO.getPhonenumber());
            }
        }
        sendJson.put(formExtraFunctionSync.getMappingField(), phoneNumberList);
    }

    @Override
    public void dealWhileReturn(List<LowcodeDataVO> lowcodeDataList, FormConfigCommon formConfigCommon, FormVO info,
                                SystemAllDataVO systemAllDataVO) {
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            JSONObject instValue = lowcodeDataVO.getInstValue();
            JSONArray jsonArray = JsonObjectUtils.getJsonArray(instValue, formConfigCommon.getName());
            List<FormUser> formUserList = JSONArray.parseArray(JSONArray.toJSONString(jsonArray), FormUser.class);
            if (CollectionUtils.isEmpty(formUserList)) {
                continue;
            }
            for (FormUser formUser : formUserList) {
                formUser.setAssigneeName(systemAllDataVO.getUserIdToNameMap().get(formUser.getAssigneeId()));
            }
            instValue.put(formConfigCommon.getName(), formUserList);
        }
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        JSONArray value = instValue.getJSONArray(formConfigCommon.getName());
        List<FormUser> formUserList = JSONArray.parseArray(JSONArray.toJSONString(value), FormUser.class);
        Object dealValue = null;
        if (CollectionUtils.isNotEmpty(formUserList)) {
            dealValue = formUserList.stream().map(FormUser::getAssigneeName).collect(Collectors.joining("，"));
        }
        putValue(instValue, dealValue, formId, formConfigCommon);
    }

    @Override
    public void dealWhileExport(int column, Row row, JSONObject instValue,
                                FormMongoDbExportDomain formMongoDbExportDomain,
                                SystemAllDataVO systemAllDataVO) {
        JSONArray value = instValue.getJSONArray(formMongoDbExportDomain.getName());
        List<FormUser> formUserList = JSONArray.parseArray(JSONArray.toJSONString(value), FormUser.class);
        Map<Long, UserCompanyVO> userIdMap = systemAllDataVO.getUserIdCompanyMap();
        List<String> mobileList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(formUserList)) {
            for (FormUser formUser : formUserList) {
                UserCompanyVO userVO = userIdMap.get(formUser.getAssigneeId());
                if (userVO != null) {
                    mobileList.add(userVO.getNickName());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(mobileList)) {
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, String.join("，", mobileList));
        } else {
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, "");
        }
    }

    @Override
    public void dealWhileUseTemplate(JSONObject jsonObject, FormConfigCommon formConfigCommon, FormUser current,
                                     List<FormDept> currentDept, Boolean needTrans) {
        jsonObject.put(formConfigCommon.getName(), Collections.singletonList(current));
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
}
