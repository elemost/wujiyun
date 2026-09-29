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

import java.util.List;
import java.util.Map;

@Service
public class FormRoleSingleServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.FORM_INPUT_ROLE_SINGLE.getFieldType();
    }

    @Override
    public void dealWhileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        Object object = instValue.get(formConfigCommon.getName());
        if (object == null) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(object);
        List<Object> objectList =
                UserDeptUtils.dealUserDept(formConfigCommon.getType(), formConfigCommon.getName(), jsonArray);
        if (CollectionUtils.isEmpty(objectList)) {
            instValue.put(formConfigCommon.getName(), null);
        } else {
            instValue.put(formConfigCommon.getName(), objectList.get(0));
        }
    }

    @Override
    public void dealWhileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        Object object = instValue.get(formConfigCommon.getName());
        if (object == null) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(object);
        List<Object> objectList =
                UserDeptUtils.dealUserDept(formConfigCommon.getType(), formConfigCommon.getName(), jsonArray);
        if (CollectionUtils.isEmpty(objectList)) {
            instValue.put(formConfigCommon.getName(), null);
        } else {
            instValue.put(formConfigCommon.getName(), objectList.get(0));
        }
    }

    @Override
    public FormImportCheckResultVO dealWhileImport(FormConfigCommon formConfigCommon, Object value,
                                                   SystemAllDataNameVO importCheck) {
        FormImportCheckResultVO formImportCheckResultVO = new FormImportCheckResultVO();
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            PostVO roleVO = importCheck.getRoleMap().get(value.toString());
            if (roleVO != null) {
                formImportCheckResultVO.setValue(roleVO.getPostId());
            } else {
                formImportCheckResultVO.setErrorMessage("这些角色不存在：" + value);
            }
        }
        return formImportCheckResultVO;
    }

    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                               SystemAllDataNameVO importCheck) {
        FormSyncCheckResultVO formSyncCheckResultVO = new FormSyncCheckResultVO();
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            PostVO roleVO = importCheck.getRoleMap().get(value.toString());
            if (roleVO != null) {
                formSyncCheckResultVO.setValue(roleVO.getPostId());
            } else {
                throw new ServiceException(ServiceResultCode.PARAM_ERROR, "角色不存在：" + value);
            }
        }
        return formSyncCheckResultVO;
    }

    @Override
    public void dealWhileReturn(List<LowcodeDataVO> lowcodeDataList, FormConfigCommon formConfigCommon, FormVO info,
                                SystemAllDataVO systemAllDataVO) {
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            JSONObject instValue = lowcodeDataVO.getInstValue();
            Object value = instValue.get(formConfigCommon.getName());
            if (value == null) {
                continue;
            }
            Long roleId = Long.valueOf(value.toString());
            Map<Long, PostVO> roleIdMap = systemAllDataVO.getRoleIdMap();
            PostVO roleVO = roleIdMap.get(roleId);
            if (roleVO == null) {
                instValue.put(formConfigCommon.getName(), null);
            } else {
                FormRole formRole = new FormRole();
                formRole.setRoleId(roleId);
                formRole.setRoleName(roleVO.getPostName());
                instValue.put(formConfigCommon.getName(), formRole);
            }
        }
    }

    @Override
    public void dealWhileSend(FormExtraFunctionSync formExtraFunctionSync, JSONObject instValue,
                              SystemAllDataVO systemAllData, JSONObject sendJson) {
        Map<Long, PostVO> roleIdMap = systemAllData.getRoleIdMap();
        Object value = instValue.get(formExtraFunctionSync.getName());
        if (value != null) {
            PostVO roleVO = roleIdMap.get(Long.valueOf(value.toString()));
            if (roleVO != null) {
                sendJson.put(formExtraFunctionSync.getMappingField(), roleVO.getPostName());
            }
        }
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        Object value = instValue.get(formConfigCommon.getName());
        Object dealValue = null;
        if (value != null) {
            PostVO roleVO = systemAllDataVO.getRoleIdMap().get(Long.valueOf(value.toString()));
            if (roleVO != null) {
                dealValue = roleVO.getPostName();
            }
        }
        putValue(instValue, dealValue, formId, formConfigCommon);
    }

    @Override
    public void dealWhileExport(int column, Row row, JSONObject instValue,
                                FormMongoDbExportDomain formMongoDbExportDomain, SystemAllDataVO systemAllDataVO) {
        Object value = instValue.get(formMongoDbExportDomain.getName());
        if (value != null) {
            FormRole formRole = JSONObject.parseObject(JSONObject.toJSONString(value), FormRole.class);
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, formRole.getRoleName());
        } else {
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, "");
        }
    }

    @Override
    public void dealWhileUseTemplate(JSONObject jsonObject, FormConfigCommon formConfigCommon, FormUser current,
                                     List<FormDept> currentDept, Boolean needTrans) {
        if (CollectionUtils.isNotEmpty(UserUtils.getUser().getPostIdList())) {
            jsonObject.put(formConfigCommon.getName(), UserUtils.getUser().getPostIdList().get(0));
        } else {
            jsonObject.put(formConfigCommon.getName(), null);
        }
    }

    @Override
    public String transValue(List<Object> values, String fieldId, String fieldType, SystemAllDataVO systemAllDataVO) {
        if (CollectionUtils.isEmpty(values)) {
            return "";
        }
        Object value = values.get(0);
        if (value == null) {
            return "";
        }
        PostVO roleVO = systemAllDataVO.getRoleIdMap().get(Long.parseLong(value.toString()));
        if (roleVO == null) {
            return "";
        }
        return roleVO.getPostName();
    }

    @Override
    public void dealDetailedReturn(MongodbSearchField mongodbSearchField, List<JSONObject> jsonObjects,
                                   SystemAllDataVO systemAllDataVO) {
        for (JSONObject jsonObject : jsonObjects) {
            JSONObject instValue = jsonObject.getJSONObject("instValue");
            Object value = instValue.get(mongodbSearchField.getName());
            if (value == null) {
                continue;
            }
            Long roleId = Long.valueOf(value.toString());
            Map<Long, PostVO> roleIdMap = systemAllDataVO.getRoleIdMap();
            PostVO roleVO = roleIdMap.get(roleId);
            if (roleVO == null) {
                instValue.put(mongodbSearchField.getName(), null);
            } else {
                FormRole formRole = new FormRole();
                formRole.setRoleId(roleId);
                formRole.setRoleName(roleVO.getPostName());
                instValue.put(mongodbSearchField.getName(), formRole);
            }
        }
    }
}
