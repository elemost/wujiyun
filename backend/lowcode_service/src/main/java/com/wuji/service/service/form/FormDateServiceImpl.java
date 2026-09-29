package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.utils.TimeUtils;
import com.wuji.service.model.domain.FormMongoDbExportDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.vo.FormImportCheckResultVO;
import com.wuji.service.model.vo.FormSyncCheckResultVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.utils.ExcelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FormDateServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.INPUT_DATE.getFieldType();
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        Long time = instValue.getLong(formConfigCommon.getName());
        String value = "";
        if (time != null) {
            value = TimeUtils.formatDateTime(new Date(time), TimeUtils.TIME_FORMAT);
        }
        putValue(instValue, value, formId, formConfigCommon);
    }

    @Override
    public void dealWhileExport(int column, Row row, JSONObject instValue,
                                FormMongoDbExportDomain formMongoDbExportDomain,
                                SystemAllDataVO systemAllDataVO) {
        Long time = instValue.getLong(formMongoDbExportDomain.getName());
        if (time != null) {
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row,
                    TimeUtils.formatDateTime(new Date(time), TimeUtils.TIME_FORMAT));
        }
    }

    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                               SystemAllDataNameVO importCheck) {
        FormSyncCheckResultVO formSyncCheckResultVO = new FormSyncCheckResultVO();
        try {
            long count = Long.parseLong(value.toString());
            new Date(count);
            formSyncCheckResultVO.setValue(value);
        } catch (Exception e) {
            formSyncCheckResultVO.setErrorMessage(formExtraFunctionSync.getMappingField() + ":格式错误");
        }
        return formSyncCheckResultVO;
    }

    @Override
    public FormImportCheckResultVO dealWhileImport(FormConfigCommon formConfigCommon, Object value,
                                                   SystemAllDataNameVO importCheck) {
        if (value == null) {
            return new FormImportCheckResultVO();
        }
        if (StringUtils.isEmpty(value.toString())) {
            return new FormImportCheckResultVO();
        }
        FormImportCheckResultVO formImportCheckResultVO = new FormImportCheckResultVO();
        Date date = TimeUtils.convertDate(value.toString());
        if (date != null) {
            formImportCheckResultVO.setValue(date.getTime());
        } else {
            formImportCheckResultVO.setErrorMessage("日期格式错误：" + value);
        }
        return formImportCheckResultVO;
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
        return TimeUtils.formatDateTime(new Date(Long.parseLong(value.toString())));
    }
}
