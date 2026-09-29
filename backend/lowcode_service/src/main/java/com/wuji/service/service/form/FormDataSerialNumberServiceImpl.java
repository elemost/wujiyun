package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.utils.QRCodeUtils;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.vo.FormSyncCheckResultVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.form.FormSubmitCheck;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.SerialNumberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormDataSerialNumberServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Autowired
    private SerialNumberService serialNumberService;

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.SERIAL_NUMBER.getFieldType();
    }

    @Override
    public void dealWhileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        Object number = instValue.get(formConfigCommon.getName());
        if (number == null || StringUtils.isEmpty(number.toString())) {
            String serialNumber =
                    serialNumberService.getSerialNumber(formConfigCommon.getRules(), formConfigCommon.getName(),
                            info.getId(), info.getApplicationId());
            instValue.put(formConfigCommon.getName(), serialNumber);
        }
    }

    @Override
    public void dealWhileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        Object number = instValue.get(formConfigCommon.getName());
        if (number == null || StringUtils.isEmpty(number.toString())) {
            String serialNumber =
                    serialNumberService.getSerialNumber(formConfigCommon.getRules(), formConfigCommon.getName(),
                            info.getId(), info.getApplicationId());
            instValue.put(formConfigCommon.getName(), serialNumber);
        }
    }

    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                               SystemAllDataNameVO importCheck) {
        return new FormSyncCheckResultVO();
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        Object value = instValue.get(formConfigCommon.getName());
        if (value != null) {
            instValue.put(formConfigCommon.getName() + "_qrcode", QRCodeUtils.generateAndSaveQRCode(value.toString()));
            instValue.put(formConfigCommon.getName() + "_" + formId + "_qrcode",
                    QRCodeUtils.generateAndSaveQRCode(value.toString()));
        } else {
            instValue.put(formConfigCommon.getName() + "_qrcode", "");
            instValue.put(formConfigCommon.getName() + "_" + formId + "_qrcode", "");
        }
        putValue(instValue, value, formId, formConfigCommon);
    }
}
