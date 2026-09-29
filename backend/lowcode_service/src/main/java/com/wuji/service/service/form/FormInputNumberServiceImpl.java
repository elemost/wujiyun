package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.UserUtils;
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
import com.wuji.service.utils.MongoDataUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class FormInputNumberServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.INPUT_NUMBER.getFieldType();
    }

    @Override
    public void dealWhileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        dealNumber(instValue, formConfigCommon);
    }

    private static void dealNumber(JSONObject instValue, FormConfigCommon formConfigCommon) {
        Object number = instValue.get(formConfigCommon.getName());
        if (formConfigCommon.getEncrypt()) {
            number = AESUtils.decryData(number, AESUtils.KEY);
            String numberString = number.toString();
            if (formConfigCommon.getEncrypt()) {
                instValue.put(formConfigCommon.getName(),
                        AESUtils.encryptData(numberString, UserUtils.getUser().getCompanyUuid()));
            } else {
                instValue.put(formConfigCommon.getName(), Double.valueOf(numberString));
            }
        } else {
            if (number == null) {
                return;
            }
            String numberString = number.toString();
            if (Objects.equals(numberString, "")) {
                instValue.put(formConfigCommon.getName(), null);
            } else if (!NumberUtils.isCreatable(numberString)) {
                instValue.put(formConfigCommon.getName(), null);
            } else {
                if (number instanceof Integer) {
                    instValue.put(formConfigCommon.getName(), Integer.valueOf(numberString));
                } else if (number instanceof Long) {
                    instValue.put(formConfigCommon.getName(), Long.valueOf(numberString));
                } else {
                    instValue.put(formConfigCommon.getName(), Double.valueOf(numberString));
                }
            }
        }
    }

    @Override
    public void dealWhileReturn(List<LowcodeDataVO> lowcodeDataList, FormConfigCommon formConfigCommon, FormVO info,
                                SystemAllDataVO systemAllDataVO) {
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            if (formConfigCommon.getEncrypt()) {
                JSONObject instValue = lowcodeDataVO.getInstValue();
                Object value = instValue.get(formConfigCommon.getName());
                if (value != null) {
                    Object decryData = AESUtils.decryData(value, UserUtils.getUser().getCompanyUuid());
                    instValue.put(formConfigCommon.getName(),
                            Double.valueOf(AESUtils.encryptData(decryData, AESUtils.KEY).toString()));
                }
            }
        }
    }

    @Override
    public void dealWhileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        dealNumber(instValue, formConfigCommon);
    }

    @Override
    public void dealWhileExport(int column, Row row, JSONObject instValue,
                                FormMongoDbExportDomain formMongoDbExportDomain,
                                SystemAllDataVO systemAllDataVO) {
        Object value = instValue.get(formMongoDbExportDomain.getName());
        if (value == null) {
            return;
        }
        Object encrypt = MongoDataUtils.decryptAndEncryptReturn(value);
        ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, encrypt.toString());
    }

    @Override
    public FormImportCheckResultVO dealWhileImport(FormConfigCommon formConfigCommon, Object value,
                                                   SystemAllDataNameVO importCheck) {
        FormImportCheckResultVO formImportCheckResultVO = new FormImportCheckResultVO();
        if (value == null) {
            return formImportCheckResultVO;
        }
        String numberString = value.toString();
        if (Objects.equals(numberString, "")) {
            return formImportCheckResultVO;
        }
        if (!NumberUtils.isCreatable(numberString)) {
            formImportCheckResultVO.setErrorMessage("当前值非有效数字：" + value);
        }
        formImportCheckResultVO.setValue(value);
        return formImportCheckResultVO;
    }

    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                               SystemAllDataNameVO importCheck) {
        FormSyncCheckResultVO formSyncCheckResultVO = new FormSyncCheckResultVO();
        if (value == null) {
            return formSyncCheckResultVO;
        }
        String numberString = value.toString();
        if (!NumberUtils.isCreatable(numberString)) {
            formSyncCheckResultVO.setValue(0);
        } else {
            if (value instanceof Integer) {
                formSyncCheckResultVO.setValue(Integer.valueOf(numberString));
            } else if (value instanceof Long) {
                formSyncCheckResultVO.setValue(Long.valueOf(numberString));
            } else {
                formSyncCheckResultVO.setValue(Double.valueOf(numberString));
            }
        }
        return formSyncCheckResultVO;
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        Object value = instValue.get(formConfigCommon.getName());
        if (value == null) {
            putValue(instValue, null, formId, formConfigCommon);
            return;
        }
        Object decryData = AESUtils.decryData(value, UserUtils.getUser().getCompanyUuid());
        putValue(instValue, Double.valueOf(decryData.toString()), formId, formConfigCommon);
    }

    @Override
    public void dealDetailedReturn(MongodbSearchField mongodbSearchField, List<JSONObject> jsonObjects,
                                   SystemAllDataVO systemAllDataVO) {
        for (JSONObject jsonObject : jsonObjects) {
            Object value = jsonObject.get(mongodbSearchField.getName());
            if (value == null) {
                return;
            }
            Object encrypt = MongoDataUtils.decryptAndEncryptReturn(value);
            jsonObject.put(mongodbSearchField.getName(), encrypt);
        }
    }

    @Override
    public void dealWhileUseTemplate(JSONObject jsonObject, FormConfigCommon formConfigCommon, FormUser current,
                                     List<FormDept> currentDept, Boolean needTrans) {
        if (!formConfigCommon.getEncrypt()) {
            return;
        }
        Object value = jsonObject.get(formConfigCommon.getName());
        if (value != null) {
            if (needTrans) {
                jsonObject.put(formConfigCommon.getName(), MongoDataUtils.decryptAndEncryptReturn(value));
            } else {
                jsonObject.put(formConfigCommon.getName(), MongoDataUtils.decryptAndEncryptInsert(value));
            }
        }
    }

}
