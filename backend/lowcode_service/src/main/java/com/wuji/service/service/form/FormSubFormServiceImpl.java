package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.utils.GuidUtils;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.vo.FormSyncCheckResultVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.model.vo.form.FormSubmitCheck;
import com.wuji.service.service.FormDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormSubFormServiceImpl extends FormCommonServiceImpl implements FormDataService {
    @Override
    public String fieldType() {
        return FormFieldTypeEnum.SUB_FORM_TYPE.getFieldType();
    }

    @Override
    public void dealWhileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        List<FormConfigCommon> columns = formConfigCommon.getColumns();
        JSONArray jsonArray = instValue.getJSONArray(formConfigCommon.getName());
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                for (FormConfigCommon subFormConfig : columns) {
                    whileCreate(jsonObject, subFormConfig, info, formSubmitCheck);
                }
                instValue.put(formConfigCommon.getName(), jsonArray);
            }
        }
    }

    @Override
    public void dealWhileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        List<FormConfigCommon> columns = formConfigCommon.getColumns();
        JSONArray jsonArray = instValue.getJSONArray(formConfigCommon.getName());
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                for (FormConfigCommon subFormConfig : columns) {
                    whileUpdate(jsonObject, subFormConfig, info, formSubmitCheck);
                }
                instValue.put(formConfigCommon.getName(), jsonArray);
            }
        }
    }

    @Override
    public void dealWhileReturn(List<LowcodeDataVO> lowcodeDataList, FormConfigCommon formConfigCommon, FormVO info,
                                SystemAllDataVO systemAllDataVO) {
        List<FormConfigCommon> columns = formConfigCommon.getColumns();
        for (FormConfigCommon subFormConfig : columns) {
            whileReturn(lowcodeDataList, subFormConfig, info, systemAllDataVO);
        }
    }

    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object valueObject,
                                               SystemAllDataNameVO importCheck) {
        List<FormExtraFunctionSync> columns = formExtraFunctionSync.getColumns();
        JSONArray returnList = new JSONArray();
        if (valueObject != null) {
            JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(valueObject));
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject returnObject = new JSONObject();
                returnObject.put("id", GuidUtils.getGuid());
                for (FormExtraFunctionSync subFormConfig : columns) {
                    if (StringUtils.isEmpty(subFormConfig.getMappingField())) {
                        continue;
                    }
                    Object value = jsonObject.get(subFormConfig.getMappingField());
                    FormSyncCheckResultVO formSyncCheckResultVO = whileSync(subFormConfig, value, importCheck);
                    returnObject.put(subFormConfig.getName(), formSyncCheckResultVO.getValue());
                }
                returnList.add(returnObject);
            }
        }
        FormSyncCheckResultVO returnResult = new FormSyncCheckResultVO();
        returnResult.setValue(returnList);
        return returnResult;
    }

    @Override
    public void dealWhileSend(FormExtraFunctionSync formExtraFunctionSync, JSONObject instValue,
                              SystemAllDataVO systemAllData, JSONObject sendJson) {
        Object valueObject = instValue.get(formExtraFunctionSync.getName());
        List<FormExtraFunctionSync> columns = formExtraFunctionSync.getColumns();
        JSONArray returnList = new JSONArray();
        if (valueObject != null) {
            JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(valueObject));
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject returnObject = new JSONObject();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                for (FormExtraFunctionSync subFormConfig : columns) {
                    if (StringUtils.isEmpty(subFormConfig.getMappingField())) {
                        continue;
                    }
                    whileSend(subFormConfig, jsonObject, systemAllData, returnObject);
                }
                returnList.add(returnObject);
            }
        }
        sendJson.put(formExtraFunctionSync.getMappingField(), returnList);
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        List<FormConfigCommon> columns = formConfigCommon.getColumns();
        JSONArray jsonArray = instValue.getJSONArray(formConfigCommon.getName());
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                for (FormConfigCommon subFormConfig : columns) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    whileCustomTemplate(jsonObject, subFormConfig, formId, systemAllDataVO);
                }
            }
            instValue.put(formConfigCommon.getName(), jsonArray);
        }
    }

    @Override
    public void getAllConfig(FormConfigCommon formConfigCommon, List<FormConfigCommon> allFormConfigList,
                             Boolean needSubForm) {
        allFormConfigList.add(formConfigCommon);
        if (needSubForm) {
            for (FormConfigCommon subForm : formConfigCommon.getColumns()) {
                subForm.setSubFormName(formConfigCommon.getName());
                subForm.setSubFromLabel(formConfigCommon.getLabel());
                allFormConfigList.add(subForm);
            }
        }
    }

    @Override
    public void dealWhileUseTemplate(JSONObject jsonObject, FormConfigCommon formConfigCommon, FormUser current,
                                     List<FormDept> currentDept, Boolean needTrans) {
        JSONArray jsonArray = jsonObject.getJSONArray(formConfigCommon.getName());
        if (jsonArray == null) {
            return;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            for (FormConfigCommon subForm : formConfigCommon.getColumns()) {
                whileUseTemplate(jsonArray.getJSONObject(i), subForm, current, currentDept, needTrans);
            }
        }
        jsonObject.put(formConfigCommon.getName(), jsonArray);
    }

}
