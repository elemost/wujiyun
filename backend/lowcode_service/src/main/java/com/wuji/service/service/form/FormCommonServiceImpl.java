package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.service.context.FormDataContext;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormCommonServiceImpl implements FormDataService {

    @Autowired
    private FormDataContext formDataContext;

    @Override
    public String fieldType() {
        return "common";
    }

    @Override
    public void dealWhileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {

    }

    @Override
    public void dealWhileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {

    }

    @Override
    public void dealWhileReturn(List<LowcodeDataVO> lowcodeDataList, FormConfigCommon formConfigCommon, FormVO info,
                                SystemAllDataVO systemAllDataVO) {

    }

    @Override
    public FormImportCheckResultVO dealWhileImport(FormConfigCommon formConfigCommon, Object value,
                                                   SystemAllDataNameVO importCheck) {
        FormImportCheckResultVO formImportCheckResultVO = new FormImportCheckResultVO();
        formImportCheckResultVO.setValue(value);
        return formImportCheckResultVO;
    }

    @Override
    public void dealWhileExport(int column, Row row, JSONObject instValue,
                                FormMongoDbExportDomain formMongoDbExportDomain, SystemAllDataVO systemAllDataVO) {
        String value = instValue.getString(formMongoDbExportDomain.getName());
        ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, value);
    }


    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                               SystemAllDataNameVO importCheck) {
        FormSyncCheckResultVO formSyncCheckResultVO = new FormSyncCheckResultVO();
        formSyncCheckResultVO.setValue(value);
        return formSyncCheckResultVO;
    }

    @Override
    public void dealWhileSend(FormExtraFunctionSync formExtraFunctionSync, JSONObject instValue,
                              SystemAllDataVO systemAllData, JSONObject sendJson) {
        if (StringUtils.isEmpty(formExtraFunctionSync.getMappingField())) {
            return;
        }
        sendJson.put(formExtraFunctionSync.getMappingField(), instValue.get(formExtraFunctionSync.getName()));
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        Object value = instValue.get(formConfigCommon.getName());
        putValue(instValue, value, formId, formConfigCommon);
    }

    @Override
    public void getAllConfig(FormConfigCommon formConfigCommon, List<FormConfigCommon> subFormList,
                             Boolean needSubForm) {
        subFormList.add(formConfigCommon);
    }

    @Override
    public void dealWhileUseTemplate(JSONObject jsonObject, FormConfigCommon formConfigCommon, FormUser current,
                                     List<FormDept> currentDept, Boolean needTrans) {

    }

    @Override
    public String transValue(List<Object> values, String fieldId, String fieldType, SystemAllDataVO systemAllDataVO) {
        return StringUtils.join(values, "，");
    }

    @Override
    public void dealDetailedReturn(MongodbSearchField mongodbSearchField, List<JSONObject> jsonObjects,
                                   SystemAllDataVO systemAllDataVO) {

    }

    public void whileReturn(List<LowcodeDataVO> lowcodeDataList, FormConfigCommon formConfigCommon, FormVO info,
                            SystemAllDataVO systemAllDataVO) {
        FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
        if (formDataService != null) {
            formDataService.dealWhileReturn(lowcodeDataList, formConfigCommon, info, systemAllDataVO);
        }
    }

    public void whileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                            FormSubmitCheck formSubmitCheck) {
        FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
        if (formDataService != null) {
            formDataService.dealWhileCreate(instValue, formConfigCommon, info, formSubmitCheck);
        }
    }


    public void whileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                            FormSubmitCheck formSubmitCheck) {
        FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
        if (formDataService != null) {
            formDataService.dealWhileUpdate(instValue, formConfigCommon, info, formSubmitCheck);
        }
    }

    public void whileCustomTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                                    SystemAllDataVO systemAllDataVO) {
        FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
        if (formDataService != null) {
            formDataService.customTemplate(instValue, formConfigCommon, formId, systemAllDataVO);
        } else {
            Object value = instValue.getString(formConfigCommon.getName());
            putValue(instValue, value, formId, formConfigCommon);
        }
    }

    public void whileAddSubForm(FormConfigCommon formConfigCommon, List<FormConfigCommon> subFormList,
                                Boolean needSubForm) {
        FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
        if (formDataService != null) {
            formDataService.getAllConfig(formConfigCommon, subFormList, needSubForm);
        } else {
            subFormList.add(formConfigCommon);
        }
    }

    public FormSyncCheckResultVO whileSync(FormExtraFunctionSync formExtraFunctionSync, Object valueObject,
                                           SystemAllDataNameVO importCheck) {
        FormDataService formDataService = formDataContext.getHandler(formExtraFunctionSync.getType());
        if (formDataService != null) {
            return formDataService.dealWhileSync(formExtraFunctionSync, valueObject, importCheck);
        } else {
            return dealWhileSyncWhileNull(formExtraFunctionSync, valueObject, importCheck);
        }
    }

    public FormSyncCheckResultVO dealWhileSyncWhileNull(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                                        SystemAllDataNameVO importCheck) {
        FormSyncCheckResultVO formSyncCheckResultVO = new FormSyncCheckResultVO();
        formSyncCheckResultVO.setValue(value);
        return formSyncCheckResultVO;
    }

    public void whileSend(FormExtraFunctionSync formExtraFunctionSync, JSONObject instValue,
                          SystemAllDataVO systemAllData, JSONObject sendJson) {
        FormDataService formDataService = formDataContext.getHandler(formExtraFunctionSync.getType());
        if (formDataService != null) {
            formDataService.dealWhileSend(formExtraFunctionSync, instValue, systemAllData, sendJson);
        } else {
            dealWhileSendWhileNull(formExtraFunctionSync, instValue, systemAllData, sendJson);
        }
    }

    public void dealWhileSendWhileNull(FormExtraFunctionSync formExtraFunctionSync, JSONObject instValue,
                                       SystemAllDataVO systemAllData, JSONObject sendJson) {
        if (StringUtils.isEmpty(formExtraFunctionSync.getMappingField())) {
            return;
        }
        sendJson.put(formExtraFunctionSync.getMappingField(), instValue.get(formExtraFunctionSync.getName()));
    }

    public void whileUseTemplate(JSONObject jsonObject, FormConfigCommon formConfigCommon, FormUser current,
                                 List<FormDept> currentDept, Boolean needTrans) {
        FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
        if (formDataService != null) {
            formDataService.dealWhileUseTemplate(jsonObject, formConfigCommon, current, currentDept, needTrans);
        }
    }

    public void putValue(JSONObject jsonObject, Object value, String formId, FormConfigCommon formConfigCommon) {
        if (value == null) {
            jsonObject.put(formConfigCommon.getName(), "");
            jsonObject.put(formConfigCommon.getName() + "_" + formId, "");
        } else {
            jsonObject.put(formConfigCommon.getName(), value);
            jsonObject.put(formConfigCommon.getName() + "_" + formId, value);
        }
    }
}
