package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.service.model.domain.FormMongoDbExportDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.vo.FormImportCheckResultVO;
import com.wuji.service.model.vo.FormSyncCheckResultVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormTreeSelectServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.TREE_SELECT.getFieldType();
    }

    @Override
    public FormImportCheckResultVO dealWhileImport(FormConfigCommon formConfigCommon, Object value,
                                                   SystemAllDataNameVO importCheck) {
        FormImportCheckResultVO formImportCheckResultVO = new FormImportCheckResultVO();
        formImportCheckResultVO.setValue(value);
        if (value != null && StringUtils.isNotEmpty(value.toString())) {
            formImportCheckResultVO.setValue(Arrays.stream(value.toString().split("，")).collect(Collectors.toList()));
        }
        return formImportCheckResultVO;
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        JSONArray jsonArray = instValue.getJSONArray(formConfigCommon.getName());
        if (jsonArray != null) {
            List<String> objectList = JSONArray.parseArray(JSONObject.toJSONString(jsonArray), String.class);
            instValue.put(formConfigCommon.getName(), String.join("，", objectList));
        } else {
            instValue.put(formConfigCommon.getName(), "");
        }
    }

    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                               SystemAllDataNameVO importCheck) {
        FormSyncCheckResultVO formSyncCheckResultVO = new FormSyncCheckResultVO();
        try {
            List<String> values = JSONArray.parseArray(JSONObject.toJSONString(value), String.class);
        } catch (Exception e) {
            formSyncCheckResultVO.setErrorMessage(formExtraFunctionSync.getMappingField() + ":格式错误");
            return formSyncCheckResultVO;
        }
        formSyncCheckResultVO.setValue(value);
        return formSyncCheckResultVO;
    }

    @Override
    public void dealWhileExport(int column, Row row, JSONObject instValue,
                                FormMongoDbExportDomain formMongoDbExportDomain,
                                SystemAllDataVO systemAllDataVO) {
        JSONArray jsonArray = instValue.getJSONArray(formMongoDbExportDomain.getName());
        if (jsonArray != null) {
            List<String> objectList = JSONArray.parseArray(JSONObject.toJSONString(jsonArray), String.class);
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, String.join("，", objectList));
        }
    }
}
