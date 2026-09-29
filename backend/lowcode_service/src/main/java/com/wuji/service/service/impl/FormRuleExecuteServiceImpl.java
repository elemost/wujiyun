package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.JsonObjectUtils;
import com.wuji.service.converter.AbstractMongoDbConverter;
import com.wuji.service.enums.FormRuleStateEnum;
import com.wuji.service.enums.FormRuleTypeEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.model.info.FormRuleCondition;
import com.wuji.service.model.info.FormRuleConditionRel;
import com.wuji.service.model.info.FormRuleVerify;
import com.wuji.service.model.info.MongodbSearchCondition;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import com.wuji.service.model.request.FormRuleActionCheckRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.vo.FormRuleVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormRuleExecuteService;
import com.wuji.service.service.FormRuleService;
import com.wuji.service.utils.MongoDbDataTransUtils;
import com.wuji.service.utils.MongoSearchUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormRuleExecuteServiceImpl implements FormRuleExecuteService {

    @Autowired
    private FormRuleService formRuleService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Override
    public void checkVerifySubmit(String applicationId, String formId, String uuid, JSONObject instValue) {
        List<FormRuleVO> formRuleVOS = formRuleService.queryList(formId, applicationId, FormRuleTypeEnum.VERIFY.name(),
                FormRuleStateEnum.UP.name());
        for (FormRuleVO formRuleVO : formRuleVOS) {
            if (StringUtils.isEmpty(formRuleVO.getRuleConfig())) {
                continue;
            }
            FormRuleVerify formRuleVerify = JSONObject.parseObject(formRuleVO.getRuleConfig(), FormRuleVerify.class);
            if (!formRuleVerify.getCheckWhileSave()) {
                continue;
            }
            if ("SUBMIT".equals(formRuleVerify.getAfterErrorAction())) {
                continue;
            }
            boolean executeResult = isExecuteResult(instValue, formRuleVO, formRuleVerify.getMatchRule());
            if (executeResult) {
                throw new ServiceException(ServiceResultCode.FORM_SUBMIT_RULE_ERROR, formRuleVerify.getPromptContent(),
                        formRuleVerify.getSpecifiedField());
            }
        }
    }

    @Override
    public boolean isExecuteResult(JSONObject instValue, FormRuleVO formRuleVO, FormRuleConditionRel matchRule) {
        boolean executeResult = false;
        for (FormRuleCondition formRuleCondition : matchRule.getRelates()) {
            executeResult = checkCondition(instValue, formRuleVO.getApplicationId(), formRuleCondition);
            if ("AND".equalsIgnoreCase(matchRule.getRel())) {
                if (!executeResult) {
                    break;
                }
            } else {
                if (executeResult) {
                    break;
                }
            }
        }
        return executeResult;
    }

    private boolean checkCondition(JSONObject instValue, String applicationId, FormRuleCondition formRuleCondition) {
        boolean executeResult = Boolean.FALSE;
        MongodbSearchCondition mongodbSearchCondition =
                AbstractMongoDbConverter.INSTANCE.toCondition(formRuleCondition);
        if ("CUSTOM".equals(formRuleCondition.getQuoteType())) {
            mongodbSearchCondition.setValue(formRuleCondition.getValue());
            executeResult = MongoSearchUtils.checkData(mongodbSearchCondition, instValue);
        } else if ("NODE_FIELD".equals(formRuleCondition.getQuoteType())) {
            DataStreamQuoteField quoteField = formRuleCondition.getQuoteField();
            List<Object> values =
                    MongoSearchUtils.getJsonValue(quoteField.getQuoteFieldId(), quoteField.getQuoteSubForm(),
                            quoteField.getQuoteFieldType(), instValue);
            mongodbSearchCondition.setValue(values);
            executeResult = MongoSearchUtils.checkData(mongodbSearchCondition, instValue);
        } else if ("FORM_DATA".equals(formRuleCondition.getQuoteType()) ||
                "DATA_FACTORY".equals(formRuleCondition.getQuoteType())) {
            MongodbSearchFilter mongodbSearchFilter =
                    MongoSearchUtils.toFilter(formRuleCondition.getFilter(), instValue, new HashMap<>());
            FormSearchDataRequest formSearchDataRequest = new FormSearchDataRequest();
            formSearchDataRequest.setFormId(formRuleCondition.getQuoteFormId());
            formSearchDataRequest.setApplicationId(applicationId);
            formSearchDataRequest.setPageSize(500);
            formSearchDataRequest.setFilter(mongodbSearchFilter);
            QueryPageVO<LowcodeDataVO> lowcodeDataVOQueryPageVO =
                    formMongoDbService.queryListLink(formSearchDataRequest);
            List<LowcodeDataVO> list = lowcodeDataVOQueryPageVO.getList();
            DataStreamQuoteField quoteField = formRuleCondition.getQuoteField();
            if (StringUtils.isEmpty(formRuleCondition.getFieldSubForm())) {
                if (CollectionUtils.isEmpty(list)) {
                    executeResult = true;
                } else {
                    LowcodeDataVO lowcodeDataVO = list.get(0);
                    JSONObject jsonObject = FormSystemFieldEnum.putSystemValue(lowcodeDataVO);
                    List<Object> values =
                            MongoSearchUtils.getJsonValue(quoteField.getQuoteFieldId(), quoteField.getQuoteSubForm(),
                                    quoteField.getQuoteFieldType(), jsonObject);
                    mongodbSearchCondition.setValue(values);
                    executeResult = MongoSearchUtils.checkData(mongodbSearchCondition, instValue);
                }
            } else {
                List<JSONObject> collect =
                        list.stream().map(FormSystemFieldEnum::putSystemValue).collect(Collectors.toList());
                JSONArray sourceJsonJSONArray = JSONArray.parseArray(JSONArray.toJSONString(collect));
                JSONArray jsonArray = JsonObjectUtils.getJsonArray(instValue, formRuleCondition.getFieldSubForm());
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONArray conformDataList = MongoDbDataTransUtils.getConformData(sourceJsonJSONArray,
                            formRuleCondition.getSubformFilter(), jsonObject);
                    if (conformDataList.isEmpty()) {
                        executeResult = true;
                        break;
                    }
                    List<Object> values =
                            MongoSearchUtils.getJsonValue(quoteField.getQuoteFieldId(), quoteField.getQuoteSubForm(),
                                    quoteField.getQuoteFieldType(), conformDataList.getJSONObject(0));
                    mongodbSearchCondition.setValue(values);
                    if(StringUtils.isNotEmpty(mongodbSearchCondition.getChildFieldId())) {
                        mongodbSearchCondition.setFieldId(mongodbSearchCondition.getChildFieldId());
                        mongodbSearchCondition.setChildFieldId(null);
                    }
                    executeResult = MongoSearchUtils.checkData(mongodbSearchCondition, jsonObject);
                    if (executeResult) {
                        break;
                    }
                }
            }
        }
        return executeResult;
    }

    @Override
    public Boolean checkWhileAction(FormRuleActionCheckRequest formRuleActionCheckRequest) {
        boolean executeResult = false;
        FormRuleConditionRel matchRule = formRuleActionCheckRequest.getMatchRule();
        for (FormRuleCondition formRuleCondition : matchRule.getRelates()) {
            executeResult = checkCondition(formRuleActionCheckRequest.getInstValue(),
                    formRuleActionCheckRequest.getApplicationId(), formRuleCondition);
            if ("AND".equalsIgnoreCase(matchRule.getRel())) {
                if (!executeResult) {
                    break;
                }
            } else {
                if (executeResult) {
                    break;
                }
            }
        }
        return executeResult;
    }
}
