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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormDeptMultipleServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.FORM_INPUT_DEPT_MULTIPLE.getFieldType();
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
            List<String> notExist = new ArrayList<>();
            List<String> deptNameList = Arrays.stream(value.toString().split("，")).collect(Collectors.toList());
            List<FormDept> formDeptList = new ArrayList<>();
            for (String deptName : deptNameList) {
                List<DepartmentVO> departmentVOList = importCheck.getDeptNameMap().get(deptName);
                if (CollectionUtils.isNotEmpty(departmentVOList)) {
                    DepartmentVO departmentVO = departmentVOList.get(0);
                    FormDept formDept = new FormDept();
                    formDept.setValue(departmentVO.getDeptId());
                    formDept.setLabel(departmentVO.getDeptName());
                    formDeptList.add(formDept);
                } else {
                    notExist.add(deptName);
                }
            }
            if (CollectionUtils.isNotEmpty(notExist)) {
                formImportCheckResultVO.setErrorMessage("当前部门不存在：" + StringUtils.join(notExist, "，"));
            } else {
                formImportCheckResultVO.setValue(formDeptList);
            }
        }
        return formImportCheckResultVO;
    }

    @Override
    public void dealWhileSend(FormExtraFunctionSync formExtraFunctionSync, JSONObject instValue,
                              SystemAllDataVO systemAllData, JSONObject sendJson) {
        Object value = instValue.get(formExtraFunctionSync.getName());
        if (value == null) {
            return;
        }
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(value));
        List<FormDept> formDeptList = jsonArray.toJavaList(FormDept.class);
        List<String> deptNameList = new ArrayList<>();
        for (FormDept formDept : formDeptList) {
            deptNameList.add(formDept.getLabel());
        }
        sendJson.put(formExtraFunctionSync.getMappingField(), deptNameList);
    }

    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                               SystemAllDataNameVO importCheck) {
        FormSyncCheckResultVO formSyncCheckResultVO = new FormSyncCheckResultVO();
        if (value == null) {
            return new FormSyncCheckResultVO();
        }
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(value));
        List<String> deptNameList = jsonArray.toJavaList(String.class);
        List<FormDept> formDeptList = new ArrayList<>();
        for (String deptName : deptNameList) {
            List<DepartmentVO> departmentVOList = importCheck.getDeptNameMap().get(deptName);
            if (CollectionUtils.isNotEmpty(departmentVOList)) {
                DepartmentVO departmentVO = departmentVOList.get(0);
                FormDept formDept = new FormDept();
                formDept.setValue(departmentVO.getDeptId());
                formDept.setLabel(departmentVO.getDeptName());
                formDeptList.add(formDept);
            }
        }
        formSyncCheckResultVO.setValue(formDeptList);
        return formSyncCheckResultVO;
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        JSONArray value = instValue.getJSONArray(formConfigCommon.getName());
        List<FormDept> formDeptList = JSONArray.parseArray(JSONArray.toJSONString(value), FormDept.class);
        String dealValue = "";
        if (CollectionUtils.isNotEmpty(formDeptList)) {
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
            if (CollectionUtils.isEmpty(formDeptList)) {
                continue;
            }
            for (FormDept formUser : formDeptList) {
                formUser.setLabel(systemAllDataVO.getDeptIdToNameMap().get(formUser.getValue()));
            }
            instValue.put(formConfigCommon.getName(), formDeptList);
        }
    }

    @Override
    public void dealWhileExport(int column, Row row, JSONObject instValue,
                                FormMongoDbExportDomain formMongoDbExportDomain,
                                SystemAllDataVO systemAllDataVO) {
        JSONArray value = instValue.getJSONArray(formMongoDbExportDomain.getName());
        List<FormDept> formDeptList = JSONArray.parseArray(JSONArray.toJSONString(value), FormDept.class);
        if (CollectionUtils.isNotEmpty(formDeptList)) {
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row,
                    formDeptList.stream().map(FormDept::getLabel).collect(Collectors.joining("，")));
        } else {
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, "");
        }
    }

    @Override
    public void dealWhileUseTemplate(JSONObject jsonObject, FormConfigCommon formConfigCommon, FormUser current,
                                     List<FormDept> currentDept, Boolean needTrans) {
        jsonObject.put(formConfigCommon.getName(), currentDept);
    }

    @Override
    public String transValue(List<Object> values, String fieldId, String fieldType, SystemAllDataVO systemAllDataVO) {
        List<FormDept> formDeptList = JSONArray.parseArray(JSONArray.toJSONString(values), FormDept.class);
        List<String> deptNameList = new ArrayList<>();
        for (FormDept formDept : formDeptList) {
            if (formDept == null) {
                continue;
            }
            deptNameList.add(formDept.getLabel());
        }
        return StringUtils.join(deptNameList, "，");
    }
}
