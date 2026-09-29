package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.QRCodeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.model.domain.FormMongoDbExportDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.info.form.FormConfigQuickEdit;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.model.vo.form.FormSubmitCheck;
import com.wuji.service.model.vo.form.MongoSameNameVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.utils.ExcelUtils;
import com.wuji.service.utils.MongoDataUtils;
import com.wuji.service.utils.MongoSearchUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FormInputTextServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.INPUT_TEXT.getFieldType();
    }

    @Override
    public void dealWhileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        if (formConfigCommon.getDuplicateValue() != null && formConfigCommon.getDuplicateValue()) {
            String quickEdit = formConfigCommon.getQuickEdit();
            if (StringUtils.isEmpty(quickEdit)) {
                checkSameName(instValue, formConfigCommon, formSubmitCheck, null, info);
            } else {
                FormConfigQuickEdit formConfigQuickEdit = JSONObject.parseObject(quickEdit, FormConfigQuickEdit.class);
                if (StringUtils.isEmpty(formConfigQuickEdit.getTableName())) {
                    checkSameName(instValue, formConfigCommon, formSubmitCheck, null, info);
                } else {
                    checkSubSameName(instValue, formConfigCommon, formSubmitCheck, formConfigQuickEdit, null, info);
                }
            }
        }
        Object value = instValue.get(formConfigCommon.getName());
        if (value == null) {
            return;
        }
        Object generalWord = AESUtils.decryData(value, AESUtils.KEY);
        if (formConfigCommon.getEncrypt() != null && formConfigCommon.getEncrypt()) {
            Object encryptData = AESUtils.encryptData(generalWord, UserUtils.getUser().getCompanyUuid());
            instValue.put(formConfigCommon.getName(), encryptData);
        }
    }

    @Override
    public void dealWhileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        Object value = instValue.get(formConfigCommon.getName());
        if (value == null) {
            return;
        }
        Object generalWord = AESUtils.decryData(value, AESUtils.KEY);
        if (formConfigCommon.getEncrypt() != null && formConfigCommon.getEncrypt()) {
            Object encryptData = AESUtils.encryptData(generalWord, UserUtils.getUser().getCompanyUuid());
            instValue.put(formConfigCommon.getName(), encryptData);
        } else {
            if (formConfigCommon.getDuplicateValue() != null && formConfigCommon.getDuplicateValue()) {
                String quickEdit = formConfigCommon.getQuickEdit();
                if (StringUtils.isEmpty(quickEdit)) {
                    checkSameName(instValue, formConfigCommon, formSubmitCheck, formSubmitCheck.getUuid(), info);
                } else {
                    FormConfigQuickEdit formConfigQuickEdit =
                            JSONObject.parseObject(quickEdit, FormConfigQuickEdit.class);
                    if (StringUtils.isEmpty(formConfigQuickEdit.getTableName())) {
                        checkSameName(instValue, formConfigCommon, formSubmitCheck, formSubmitCheck.getUuid(), info);
                    } else {
                        checkSubSameName(instValue, formConfigCommon, formSubmitCheck, formConfigQuickEdit,
                                formSubmitCheck.getUuid(), info);
                    }
                }
            }
        }
    }

    private void checkSubSameName(JSONObject instValue, FormConfigCommon formConfigCommon,
                                  FormSubmitCheck formSubmitCheck, FormConfigQuickEdit formConfigQuickEdit, String uuid,
                                  FormVO info) {
        Query query = new Query();
        List<Object> jsonValue =
                MongoSearchUtils.getJsonValue(formConfigCommon.getName(), formConfigQuickEdit.getTableName(),
                        formConfigCommon.getType(), instValue);
        String fieldId = MongoSearchUtils.getFieldId(formConfigCommon.getSubFormName(), "");
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where(fieldId).elemMatch(new Criteria(formConfigCommon.getName()).is(jsonValue)));
        if (StringUtils.isNotEmpty(uuid)) {
            criteriaList.add(Criteria.where("uuid").ne(uuid));
        }
        MongoSearchUtils.buildCommonFilter(criteriaList, info.getApplicationId(), info.getId());
        Criteria criteria = new Criteria();
        criteria = criteria.andOperator(criteriaList);
        query.addCriteria(criteria);
        long count = mongoTemplate.count(query, Long.class, info.getTableName());
        if (count > 0) {
            formSubmitCheck.getSameNameList()
                    .add(new MongoSameNameVO(formConfigCommon.getName(), formConfigCommon.getLabel(), null, jsonValue));
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
                    instValue.put(formConfigCommon.getName(), AESUtils.encryptData(decryData, AESUtils.KEY));
                }
            }
        }
    }

    @Override
    public void dealWhileExport(int column, Row row, JSONObject instValue,
                                FormMongoDbExportDomain formMongoDbExportDomain,
                                SystemAllDataVO systemAllDataVO) {
        Object value = instValue.get(formMongoDbExportDomain.getName());
        if (value == null) {
            return;
        }
        Object decryData = AESUtils.decryData(value, UserUtils.getUser().getCompanyUuid());
        ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, decryData.toString());
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

    private void checkSameName(JSONObject instValue, FormConfigCommon formConfigCommon, FormSubmitCheck formSubmitCheck,
                               String uuid, FormVO info) {
        Query query = new Query();
        Object value = instValue.get(formConfigCommon.getName());
        if (value == null || StringUtils.isEmpty(value.toString())) {
            return;
        }
        String fieldId = MongoSearchUtils.getFieldId(formConfigCommon.getName(), formConfigCommon.getType());
        List<Criteria> criteriaList = new ArrayList<>();
        Criteria criteria = new Criteria();
        MongoSearchUtils.buildCommonFilter(criteriaList, info.getApplicationId(), info.getId());
        if (StringUtils.isNotEmpty(uuid)) {
            criteriaList.add(Criteria.where("uuid").ne(uuid));
        }
        criteriaList.add(new Criteria(fieldId).is(value));
        query.addCriteria(criteria.andOperator(criteriaList));
        long count = mongoTemplate.count(query, Long.class, info.getTableName());
        if (count > 0) {
            formSubmitCheck.getSameNameList()
                    .add(new MongoSameNameVO(formConfigCommon.getName(), formConfigCommon.getLabel(), null, value));
        }
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        Object value = instValue.get(formConfigCommon.getName());
        if (value != null) {
            Object decryData = AESUtils.decryData(value, UserUtils.getUser().getCompanyUuid());
            instValue.put(formConfigCommon.getName() + "_qrcode",
                    QRCodeUtils.generateAndSaveQRCode(decryData.toString()));
            instValue.put(formConfigCommon.getName() + "_" + formId + "_qrcode",
                    QRCodeUtils.generateAndSaveQRCode(decryData.toString()));
            putValue(instValue, decryData, formId, formConfigCommon);
        } else {
            putValue(instValue, null, formId, formConfigCommon);
            instValue.put(formConfigCommon.getName() + "_qrcode", "");
            instValue.put(formConfigCommon.getName() + "_" + formId + "_qrcode", "");
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
