package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.TimeUtils;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.model.domain.FormMongoDbExportDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.vo.FormImportCheckResultVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.utils.ExcelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class FormSystemServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Override
    public String fieldType() {
        return "system";
    }

    @Override
    public void dealWhileExport(int column, Row row, JSONObject instValue,
                                FormMongoDbExportDomain formMongoDbExportDomain,
                                SystemAllDataVO systemAllDataVO) {
        FormSystemFieldEnum formSystemFieldEnum = FormSystemFieldEnum.getByName(formMongoDbExportDomain.getName());
        Object value = instValue.get(formMongoDbExportDomain.getName());
        if (value == null) {
            return;
        }
        switch (Objects.requireNonNull(formSystemFieldEnum)) {
            case CREATE_TIME:
            case UPDATE_TIME:
                ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row,
                        TimeUtils.formatDateTime(new Date(Long.parseLong(value.toString())), TimeUtils.TIME_FORMAT));
                break;
            case CREATE_NAME:
                FormUser formUser = JSONObject.parseObject(JSONObject.toJSONString(value), FormUser.class);
                if (formUser == null) {
                    ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, "");
                    break;
                }
                UserVO userVO = systemAllDataVO.getUserIdMap().get(formUser.getAssigneeId());
                if (userVO != null) {
                    ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, userVO.getNickName());
                } else {
                    ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, "");
                }
                break;
            case STATUS:
                String message = FormDataStatusEnum.valueOf(value.toString()).getMessage();
                ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, message);
                break;
            case UUID:
                ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, value.toString());
                break;
        }

    }

    @Override
    public FormImportCheckResultVO dealWhileImport(FormConfigCommon formConfigCommon, Object value,
                                                   SystemAllDataNameVO importCheck) {
        if (value == null) {
            return new FormImportCheckResultVO();
        }
        FormSystemFieldEnum formSystemFieldEnum = FormSystemFieldEnum.getByName(formConfigCommon.getName());
        FormImportCheckResultVO formImportCheckResultVO = new FormImportCheckResultVO();
        if (Objects.requireNonNull(formSystemFieldEnum) == FormSystemFieldEnum.UUID) {
            formImportCheckResultVO.setValue(value.toString());
        }
        return formImportCheckResultVO;
    }

    @Override
    public void dealDetailedReturn(MongodbSearchField mongodbSearchField, List<JSONObject> jsonObjects,
                                   SystemAllDataVO systemAllDataVO) {
        if ("system".equals(mongodbSearchField.getType()) &&
                FormSystemFieldEnum.CREATE_NAME.getName().equals(mongodbSearchField.getName())) {
            for (JSONObject jsonObject : jsonObjects) {
                FormUser object = jsonObject.getObject(mongodbSearchField.getName(), FormUser.class);
                jsonObject.put(FormSystemFieldEnum.CREATE_NAME.getAlias(), object.getAssigneeName());
            }
        }
    }

    @Override
    public String transValue(List<Object> values, String fieldId, String fieldType, SystemAllDataVO systemAllDataVO) {
        if (CollectionUtils.isEmpty(values)) {
            return "";
        }
        Object value = values.get(0);
        FormSystemFieldEnum formSystemFieldEnum = FormSystemFieldEnum.getByName(fieldId);
        switch (Objects.requireNonNull(formSystemFieldEnum)) {
            case CREATE_TIME:
            case UPDATE_TIME:
                return TimeUtils.formatDateTime(new Date(Long.parseLong(value.toString())), TimeUtils.TIME_FORMAT);
            case CREATE_NAME:
                FormUser formUser = JSONObject.parseObject(JSONObject.toJSONString(value), FormUser.class);
                return formUser.getAssigneeName();
            case STATUS:
                return FormDataStatusEnum.valueOf(value.toString()).getMessage();
        }
        return "";
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        FormSystemFieldEnum formSystemFieldEnum = FormSystemFieldEnum.getByName(formConfigCommon.getName());
        Object value = instValue.get(formConfigCommon.getName());
        if (value == null) {
            return;
        }
        switch (Objects.requireNonNull(formSystemFieldEnum)) {
            case CREATE_TIME:
            case UPDATE_TIME:
                String time =
                        TimeUtils.formatDateTime(new Date(Long.parseLong(value.toString())), TimeUtils.TIME_FORMAT);
                putValue(instValue, time, formId, formConfigCommon);
                break;
            case CREATE_NAME:
                FormUser formUser = JSONObject.parseObject(JSONObject.toJSONString(value), FormUser.class);
                putValue(instValue, formUser.getAssigneeName(), formId, formConfigCommon);
                break;
            case STATUS:
                String message = FormDataStatusEnum.valueOf(value.toString()).getMessage();
                putValue(instValue, message, formId, formConfigCommon);
                break;
            case UUID:
                putValue(instValue, value, formId, formConfigCommon);
                break;
        }
    }
}
