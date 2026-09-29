package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.utils.JsonObjectUtils;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormDeptSingleServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE.getFieldType();
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
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            List<DepartmentVO> departmentVOList = importCheck.getDeptNameMap().get(value.toString());
            if (CollectionUtils.isNotEmpty(departmentVOList)) {
                DepartmentVO departmentVO = departmentVOList.get(0);
                FormDept formDept = new FormDept();
                formDept.setValue(departmentVO.getDeptId());
                formDept.setLabel(departmentVO.getDeptName());
                formImportCheckResultVO.setValue(Collections.singletonList(formDept));
            } else {
                formImportCheckResultVO.setErrorMessage("当前部门不存在：" + value);
            }
        }
        return formImportCheckResultVO;
    }

    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                               SystemAllDataNameVO importCheck) {
        FormSyncCheckResultVO formImportCheckResultVO = new FormSyncCheckResultVO();
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            List<DepartmentVO> departmentVOList = importCheck.getDeptNameMap().get(value.toString());
            if (CollectionUtils.isNotEmpty(departmentVOList)) {
                DepartmentVO departmentVO = departmentVOList.get(0);
                FormDept formDept = new FormDept();
                formDept.setValue(departmentVO.getDeptId());
                formDept.setLabel(departmentVO.getDeptName());
                formImportCheckResultVO.setValue(Collections.singletonList(formDept));
            }
        }
        return formImportCheckResultVO;
    }

    @Override
    public void dealWhileSend(FormExtraFunctionSync formExtraFunctionSync, JSONObject instValue,
                              SystemAllDataVO systemAllData, JSONObject sendJson) {
        Object value = instValue.get(formExtraFunctionSync.getName());
        if (value != null) {
            List<FormDept> formDeptList = JSONArray.parseArray(JSONArray.toJSONString(value), FormDept.class);
            if (CollectionUtils.isNotEmpty(formDeptList)) {
                FormDept formUser = formDeptList.get(0);
                sendJson.put(formExtraFunctionSync.getMappingField(), formUser.getLabel());
            }
        }
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        JSONArray value = instValue.getJSONArray(formConfigCommon.getName());
        String dealValue = "";
        if (value != null) {
            List<FormDept> formDeptList = JSONArray.parseArray(JSONArray.toJSONString(value), FormDept.class);
            dealValue = formDeptList.stream().map(FormDept::getLabel).collect(Collectors.joining("，"));
        }
        putValue(instValue, dealValue, formId, formConfigCommon);
    }

    @Override
    public void dealWhileReturn(List<LowcodeDataVO> lowcodeDataList, FormConfigCommon formConfigCommon, FormVO info,
                                SystemAllDataVO systemAllDataVO) {
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            JSONObject instValue = lowcodeDataVO.getInstValue();
            JSONArray jsonArray = JsonObjectUtils.getJsonArray(instValue, formConfigCommon.getName());
            List<FormDept> formDeptList = JSONArray.parseArray(JSONArray.toJSONString(jsonArray), FormDept.class);
            if (formDeptList == null) {
                return;
            }
            for (FormDept formUser : formDeptList) {
                formUser.setLabel(systemAllDataVO.getDeptIdToNameMap().get(formUser.getValue()));
            }
            instValue.put(formConfigCommon.getName(), formDeptList);
        }
    }

    @Override
    public void dealWhileExport(int column, Row row, JSONObject instValue,
                                FormMongoDbExportDomain formMongoDbExportDomain, SystemAllDataVO systemAllDataVO) {
        JSONArray value = instValue.getJSONArray(formMongoDbExportDomain.getName());
        List<FormDept> formDeptList = JSONArray.parseArray(JSONArray.toJSONString(value), FormDept.class);
        if (CollectionUtils.isNotEmpty(formDeptList)) {
            FormDept formDept = formDeptList.get(0);
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, formDept.getLabel());
        } else {
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, "");
        }
    }

    @Override
    public void dealWhileUseTemplate(JSONObject jsonObject, FormConfigCommon formConfigCommon, FormUser current,
                                     List<FormDept> currentDept, Boolean needTrans) {
        if (CollectionUtils.isNotEmpty(currentDept)) {
            jsonObject.put(formConfigCommon.getName(), Collections.singletonList(currentDept.get(0)));
        } else {
            jsonObject.put(formConfigCommon.getName(), null);
        }
    }

    @Override
    public String transValue(List<Object> values, String fieldId, String fieldType, SystemAllDataVO systemAllDataVO) {
        List<FormDept> formDeptList = JSONArray.parseArray(JSONArray.toJSONString(values), FormDept.class);
        if (CollectionUtils.isEmpty(formDeptList)) {
            return "";
        }
        FormDept formDept = formDeptList.get(0);
        if (formDept == null) {
            return "";
        }
        return formDept.getLabel();
    }
}
