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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormUserSingleServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.FORM_INPUT_USER_SINGLE.getFieldType();
    }

    @Override
    public void dealWhileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        Object object = instValue.get(formConfigCommon.getName());
        if (object == null || StringUtils.isEmpty(object.toString())) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        if (object instanceof List) {
            jsonArray = instValue.getJSONArray(formConfigCommon.getName());
        } else {
            jsonArray.add(object);
        }
        List<Object> objectList =
                UserDeptUtils.dealUserDept(formConfigCommon.getType(), formConfigCommon.getName(), jsonArray);
        instValue.put(formConfigCommon.getName(), objectList);
    }

    @Override
    public void dealWhileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        Object object = instValue.get(formConfigCommon.getName());
        if (object == null) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        if (object instanceof ArrayList) {
            jsonArray = instValue.getJSONArray(formConfigCommon.getName());
        } else {
            jsonArray.add(object);
        }
        List<Object> objectList =
                UserDeptUtils.dealUserDept(formConfigCommon.getType(), formConfigCommon.getName(), jsonArray);
        instValue.put(formConfigCommon.getName(), objectList);
    }

    @Override
    public FormImportCheckResultVO dealWhileImport(FormConfigCommon formConfigCommon, Object value,
                                                   SystemAllDataNameVO importCheck) {
        FormImportCheckResultVO formImportCheckResultVO = new FormImportCheckResultVO();
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            UserVO userVO = importCheck.getPhonenumberMap().get(value.toString());
            if (userVO != null) {
                FormUser formUser = new FormUser();
                formUser.setAssigneeId(userVO.getUserId());
                formUser.setAssigneeName(userVO.getNickName());
                formImportCheckResultVO.setValue(Collections.singletonList(formUser));
            } else {
                Map<String, List<UserCompanyVO>> nickNameMap = importCheck.getNickNameMap();
                List<UserCompanyVO> userVOS = nickNameMap.get(value.toString());
                if (CollectionUtils.isNotEmpty(userVOS)) {
                    UserCompanyVO companyVO = userVOS.get(0);
                    FormUser formUser = new FormUser();
                    formUser.setAssigneeId(companyVO.getUserId());
                    formUser.setAssigneeName(companyVO.getNickName());
                    formImportCheckResultVO.setValue(Collections.singletonList(formUser));
                } else {
                    formImportCheckResultVO.setErrorMessage("这些用户不存在：" + value);
                }
            }
        }
        return formImportCheckResultVO;
    }

    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                               SystemAllDataNameVO importCheck) {
        FormSyncCheckResultVO formSyncCheckResultVO = new FormSyncCheckResultVO();
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            UserVO userVO = importCheck.getPhonenumberMap().get(value.toString());
            if (userVO != null) {
                FormUser formUser = new FormUser();
                formUser.setAssigneeId(userVO.getUserId());
                formUser.setAssigneeName(userVO.getNickName());
                formSyncCheckResultVO.setValue(Collections.singletonList(formUser));
            } else {
                throw new ServiceException(ServiceResultCode.PARAM_ERROR, "用户不存在：" + value);
            }
        }
        return formSyncCheckResultVO;
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
    public void dealWhileSend(FormExtraFunctionSync formExtraFunctionSync, JSONObject instValue,
                              SystemAllDataVO systemAllData, JSONObject sendJson) {
        Map<Long, UserVO> userIdMap = systemAllData.getUserIdMap();
        Object value = instValue.get(formExtraFunctionSync.getName());
        if (value != null) {
            List<FormUser> formUserList = JSONArray.parseArray(JSONArray.toJSONString(value), FormUser.class);
            if (CollectionUtils.isNotEmpty(formUserList)) {
                FormUser formUser = formUserList.get(0);
                UserVO userVO = userIdMap.get(formUser.getAssigneeId());
                if (userVO != null) {
                    sendJson.put(formExtraFunctionSync.getMappingField(), userVO.getPhonenumber());
                }
            }
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
        if (CollectionUtils.isNotEmpty(formUserList)) {
            FormUser formUser = formUserList.get(0);
            UserCompanyVO userVO = systemAllDataVO.getUserIdCompanyMap().get(formUser.getAssigneeId());
            if (userVO != null) {
                ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, userVO.getNickName());
            } else {
                ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, "");
            }
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
        if (CollectionUtils.isEmpty(formUserList)) {
            return "";
        }
        FormUser formUser = formUserList.get(0);
        if (formUser == null) {
            return "";
        }
        return formUser.getAssigneeName();
    }
}
