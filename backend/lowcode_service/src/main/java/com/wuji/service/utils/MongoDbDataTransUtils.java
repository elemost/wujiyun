package com.wuji.service.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.DefaultContext;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.FunctionException;
import com.wuji.common.express.FormulaRunner;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.utils.JsonObjectUtils;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.SnowFlakeIdUtils;
import com.wuji.service.constant.Constants;
import com.wuji.service.converter.AbstractFormDataStreamConverter;
import com.wuji.service.enums.DataStreamFieldQuoteTypeEnum;
import com.wuji.service.enums.DataStreamNodeTypeEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.model.domain.DataStreamCreateTransDomain;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.info.MongoFieldRelate;
import com.wuji.service.model.info.MongodbSearchCondition;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.stream.DataStreamCalculate;
import com.wuji.service.model.info.stream.DataStreamCalculateNode;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamConditionRel;
import com.wuji.service.model.info.stream.DataStreamFieldTrans;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import com.wuji.service.model.info.stream.DataStreamSubFormFilter;
import com.wuji.workflow.model.info.FlowableDataTrans;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class MongoDbDataTransUtils {

    /**
     * 数据转化   当sourceParentId不存在 targetParentId 存在时现在时属于子传父的情况 这种情况只add
     *
     * @param sourceDataDomain
     * @param flowableDataTransList
     * @param targetDataDomain
     * @return
     */
    public static JSONObject dataTrans(LowcodeDataDomain sourceDataDomain,
                                       List<FlowableDataTrans> flowableDataTransList,
                                       LowcodeDataDomain targetDataDomain) {
        JSONObject targetJson = null;
        JSONObject sourceJson = sourceDataDomain.getInstValue();
        if (targetDataDomain == null) {
            targetJson = new JSONObject();
        } else {
            targetJson = targetDataDomain.getInstValue();
        }

        List<FlowableDataTrans> subToParentList = new ArrayList<>();
        for (FlowableDataTrans flowableDataTrans : flowableDataTransList) {
            if (StringUtils.isEmpty(flowableDataTrans.getSourceParentFieldId()) &&
                    StringUtils.isEmpty(flowableDataTrans.getTargetParentFieldId())) {
                targetJson.put(flowableDataTrans.getTargetFieldId(),
                        getValue(sourceDataDomain, flowableDataTrans.getSourceFieldId(),
                                flowableDataTrans.getSourceType()));
            } else {
                if (StringUtils.isNotEmpty(flowableDataTrans.getSourceParentFieldId())) {
                    JSONArray sourceJsonJSONArray =
                            JsonObjectUtils.getJsonArray(sourceJson, flowableDataTrans.getSourceParentFieldId());
                    JSONArray targetJsonJSONArray =
                            JsonObjectUtils.getJsonArray(targetJson, flowableDataTrans.getTargetParentFieldId());
                    if (sourceJsonJSONArray != null) {
                        for (int i = 0; i < sourceJsonJSONArray.size(); i++) {
                            JSONObject subSourceJson = sourceJsonJSONArray.getJSONObject(i);
                            JSONObject subTargetJson = null;
                            if (i + 1 > targetJsonJSONArray.size()) {
                                subTargetJson = new JSONObject();
                                subTargetJson.put("id", ObjectId.getGuid());
                                subTargetJson.put(flowableDataTrans.getTargetFieldId(),
                                        subSourceJson.get(flowableDataTrans.getSourceFieldId()));
                                targetJsonJSONArray.add(subTargetJson);
                            } else {
                                subTargetJson = targetJsonJSONArray.getJSONObject(i);
                                subTargetJson.put(flowableDataTrans.getTargetFieldId(),
                                        subSourceJson.get(flowableDataTrans.getSourceFieldId()));
                            }
                        }
                    }
                    targetJson.put(flowableDataTrans.getTargetParentFieldId(), targetJsonJSONArray);
                } else {
                    subToParentList.add(flowableDataTrans);
                }
            }
        }
        Map<String, List<FlowableDataTrans>> targetParentIdMap =
                subToParentList.stream().collect(Collectors.groupingBy(FlowableDataTrans::getTargetParentFieldId));
        JSONObject finalTargetJson = targetJson;
        targetParentIdMap.forEach((targetParentId, flowableDataTrans) -> {
            JSONObject parentJson = new JSONObject();
            parentJson.put("id", ObjectId.getGuid());
            for (FlowableDataTrans flowableDataTrans1 : flowableDataTrans) {
                parentJson.put(flowableDataTrans1.getTargetFieldId(),
                        getValue(sourceDataDomain, flowableDataTrans1.getSourceFieldId(),
                                flowableDataTrans1.getSourceType()));
            }
            JSONArray targetJsonArray = JsonObjectUtils.getJsonArray(finalTargetJson, targetParentId);
            targetJsonArray.add(parentJson);
            finalTargetJson.put(targetParentId, targetJsonArray);
        });
        return finalTargetJson;
    }

    private static Object getValue(LowcodeDataDomain sourceDataDomain, String key, String type) {
        if (!"system".equals(type)) {
            return sourceDataDomain.getInstValue().get(key);
        } else {
            if (FormSystemFieldEnum.CREATE_TIME.getName().equals(key)) {
                return sourceDataDomain.getCreateTime();
            } else if (FormSystemFieldEnum.UPDATE_TIME.getName().equals(key)) {
                return sourceDataDomain.getModifyTime();
            } else if (FormSystemFieldEnum.CREATE_NAME.getName().equals(key)) {
                return sourceDataDomain.getCreator();
            }
        }
        return null;
    }

    public static Boolean checkWhileCreate(Map<Long, DataStreamCalculateVO> nodeIdMap,
                                           List<DataStreamFieldTrans> fieldTransList) {
        boolean create = true;
        for (DataStreamFieldTrans dataStreamFieldTrans : fieldTransList) {
            DataStreamQuoteField quoteField = dataStreamFieldTrans.getQuoteField();
            if (quoteField != null) {
                DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(quoteField.getNodeId());
                if (DataStreamNodeTypeEnum.typeExistSize().contains(dataStreamCalculateVO.getNodeType())) {
                    if (!"system".equals(quoteField.getQuoteFieldType()) ||
                            !Constants.SIZE.equals(quoteField.getQuoteFieldId())) {
                        if (dataStreamCalculateVO.getResultNull()) {
                            create = false;
                            break;
                        }
                    }
                } else {
                    if (dataStreamCalculateVO.getResultNull()) {
                        create = false;
                        break;
                    }
                }
            }
        }
        return create;
    }

    public static Boolean checkWhileNull(Map<Long, DataStreamCalculateVO> nodeIdMap,
                                         List<MongoFieldRelate> fieldTransList) {
        boolean create = true;
        for (MongoFieldRelate dataStreamFieldTrans : fieldTransList) {
            if (dataStreamFieldTrans.getNodeId() != null) {
                DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(dataStreamFieldTrans.getNodeId());
                if (DataStreamNodeTypeEnum.typeExistSize().contains(dataStreamCalculateVO.getNodeType())) {
                    if (!"system".equals(dataStreamFieldTrans.getFieldType()) ||
                            !Constants.SIZE.equals(dataStreamFieldTrans.getFieldId())) {
                        if (dataStreamCalculateVO.getResultNull()) {
                            create = false;
                            break;
                        }
                    }
                } else {
                    if (dataStreamCalculateVO.getResultNull()) {
                        create = false;
                        break;
                    }
                }
            }
            DataStreamQuoteField quoteField = dataStreamFieldTrans.getQuoteField();
            if (quoteField != null) {
                DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(quoteField.getNodeId());
                if (DataStreamNodeTypeEnum.typeExistSize().contains(dataStreamCalculateVO.getNodeType())) {
                    if (!"system".equals(quoteField.getQuoteFieldType()) ||
                            !Constants.SIZE.equals(quoteField.getQuoteFieldId())) {
                        if (dataStreamCalculateVO.getResultNull()) {
                            create = false;
                            break;
                        }
                    }
                } else {
                    if (dataStreamCalculateVO.getResultNull()) {
                        create = false;
                        break;
                    }
                }
            }
        }
        return create;
    }

    public static DataStreamCreateTransDomain streamTransWhileCreate(Map<Long, DataStreamCalculateVO> nodeIdMap,
                                                                     List<DataStreamFieldTrans> fieldTransList,
                                                                     List<DataStreamSubFormFilter> subFormFilterList,
                                                                     Map<String, FormConfigEncryptKey> encryptKeyMap) {
        List<JSONObject> returnList = new ArrayList<>();
        JSONObject targetJson = new JSONObject();
        List<DataStreamFieldTrans> subFieldTransList = new ArrayList<>();
        boolean needBatch = false;
        JSONArray jsonArray = new JSONArray();
        List<DataStreamFieldTrans> batchList = new ArrayList<>();
        List<DataStreamFieldTrans> calculateList = new ArrayList<>();
        for (DataStreamFieldTrans dataStreamFieldTrans : fieldTransList) {
            if (DataStreamFieldQuoteTypeEnum.CALCULATE.name().equals(dataStreamFieldTrans.getQuoteType())) {
                calculateList.add(dataStreamFieldTrans);
                continue;
            }
            DataStreamQuoteField quoteField = dataStreamFieldTrans.getQuoteField();
            if (StringUtils.isEmpty(dataStreamFieldTrans.getSubForm())) {
                // 非子表单处理
                if (quoteField == null || StringUtils.isEmpty(quoteField.getQuoteSubForm())) {
                    targetJson.put(dataStreamFieldTrans.getFieldId(),
                            getValue(nodeIdMap, dataStreamFieldTrans, targetJson));
                } else {
                    needBatch = true;
                    DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(quoteField.getNodeId());
                    jsonArray = JsonObjectUtils.getJsonArray(dataStreamCalculateVO.getJsonValue(),
                            quoteField.getQuoteSubForm());
                    batchList.add(dataStreamFieldTrans);
                }
            } else {
                subFieldTransList.add(dataStreamFieldTrans);
            }
        }
        if (needBatch) {
            if (jsonArray == null || jsonArray.isEmpty()) {
                returnList.add(targetJson);
            } else {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject returnObject = new JSONObject();
                    returnObject.putAll(targetJson);
                    for (DataStreamFieldTrans dataStreamFieldTrans : batchList) {
                        DataStreamQuoteField quoteField = dataStreamFieldTrans.getQuoteField();
                        returnObject.put(dataStreamFieldTrans.getFieldId(),
                                jsonObject.get(quoteField.getQuoteFieldId()));
                    }
                    Map<String, List<DataStreamFieldTrans>> fieldIdMap =
                            subFieldTransList.stream().collect(Collectors.groupingBy(DataStreamFieldTrans::getSubForm));
                    int finalI = i;
                    fieldIdMap.forEach((key, list) -> {
                        JSONArray jsonArrayWhileCreate =
                                getJsonArrayWhileCreate(nodeIdMap, list, subFormFilterList, finalI, encryptKeyMap);
                        returnObject.put(key, jsonArrayWhileCreate);
                    });
                    returnList.add(returnObject);
                }
            }
        } else {
            Map<String, List<DataStreamFieldTrans>> fieldIdMap =
                    subFieldTransList.stream().collect(Collectors.groupingBy(DataStreamFieldTrans::getSubForm));
            fieldIdMap.forEach((key, list) -> {
                JSONArray jsonArrayWhileCreate =
                        getJsonArrayWhileCreate(nodeIdMap, list, subFormFilterList, null, encryptKeyMap);
                targetJson.put(key, jsonArrayWhileCreate);
            });
            returnList.add(targetJson);
        }
        if (CollectionUtils.isNotEmpty(calculateList)) {
            for (JSONObject jsonObject : returnList) {
                LowcodeDataDomain lowcodeDataDomain = new LowcodeDataDomain();
                lowcodeDataDomain.setInstValue(jsonObject);
                JSONObject returnJson =
                        MongoDbDataTransUtils.streamTransWhileUpdate(nodeIdMap, calculateList, lowcodeDataDomain,
                                new DataStreamConditionRel(), encryptKeyMap);
                jsonObject.putAll(returnJson);
            }
        }
        DataStreamCreateTransDomain dataStreamCreateTransDomain = new DataStreamCreateTransDomain();
        dataStreamCreateTransDomain.setMore(needBatch);
        dataStreamCreateTransDomain.setReturnList(returnList);
        return dataStreamCreateTransDomain;
    }

    public static JSONObject streamTransWhileUpdate(Map<Long, DataStreamCalculateVO> nodeIdMap,
                                                    List<DataStreamFieldTrans> fieldTransList,
                                                    LowcodeDataDomain targetDataDomain,
                                                    DataStreamConditionRel condition,
                                                    Map<String, FormConfigEncryptKey> encryptKeyMap) {
        JSONObject targetJson = new JSONObject();
        targetJson.putAll(targetDataDomain.getInstValue());
        for (DataStreamFieldTrans dataStreamFieldTrans : fieldTransList) {
            String subForm = dataStreamFieldTrans.getSubForm();
            DataStreamQuoteField quoteField = dataStreamFieldTrans.getQuoteField();
            if (StringUtils.isEmpty(subForm)) {
                if (!DataStreamFieldQuoteTypeEnum.NODE_FIELD.name().equals(dataStreamFieldTrans.getQuoteType())) {
                    targetJson.put(dataStreamFieldTrans.getFieldId(),
                            getValueNotNeedField(dataStreamFieldTrans, nodeIdMap, targetJson));
                } else {
                    String quoteSubForm = quoteField.getQuoteSubForm();
                    if (StringUtils.isEmpty(quoteSubForm)) {
                        // 非子表单处理
                        targetJson.put(dataStreamFieldTrans.getFieldId(),
                                getValue(nodeIdMap, dataStreamFieldTrans, targetJson));
                    } else {
                        // 赋值为子表单数据
                        DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(quoteField.getNodeId());
                        JSONArray sourceJsonJSONArray =
                                JsonObjectUtils.getJsonArray(dataStreamCalculateVO.getJsonValue(), quoteSubForm);
                        JSONArray conformData =
                                getConformData(sourceJsonJSONArray, condition, targetDataDomain.getInstValue());
                        if (!conformData.isEmpty()) {
                            JSONObject jsonObject = conformData.getJSONObject(0);
                            targetJson.put(dataStreamFieldTrans.getFieldId(),
                                    jsonObject.get(quoteField.getQuoteFieldId()));
                        }
                    }
                }
            } else if (StringUtils.isNotEmpty(subForm)) {
                if (quoteField == null &&
                        !DataStreamFieldQuoteTypeEnum.CALCULATE.name().equals(dataStreamFieldTrans.getQuoteType())) {
                    Object value = null;
                    if (!DataStreamFieldQuoteTypeEnum.needFieldQuoteTypeList()
                            .contains(dataStreamFieldTrans.getQuoteType())) {
                        value = getValueNotNeedField(dataStreamFieldTrans, nodeIdMap, null);
                    }
                    JSONArray jsonArray = JsonObjectUtils.getJsonArray(targetDataDomain.getInstValue(), subForm);
                    List<Integer> conformDataList = getConformData(jsonArray, condition, nodeIdMap, encryptKeyMap);
                    if (!conformDataList.isEmpty()) {
                        JSONArray targetJsonArray = JsonObjectUtils.getJsonArray(targetJson, subForm);
                        for (Integer index : conformDataList) {
                            targetJsonArray.getJSONObject(index).put(dataStreamFieldTrans.getFieldId(), value);
                        }
                        targetJson.put(subForm, targetJsonArray);
                    }
                } else {
                    if (DataStreamFieldQuoteTypeEnum.CALCULATE.name().equals(dataStreamFieldTrans.getQuoteType()) ||
                            StringUtils.isEmpty(quoteField.getQuoteSubForm())) {
                        // 为子表单赋值
                        subToNormal(nodeIdMap, targetDataDomain, condition, dataStreamFieldTrans, targetJson,
                                encryptKeyMap);
                    } else {
                        // 为子表单赋子表单值
                        subToSub(nodeIdMap, targetDataDomain, condition, dataStreamFieldTrans, subForm, quoteField);
                    }
                }
            }
        }
        return targetJson;
    }

    private static void subToSub(Map<Long, DataStreamCalculateVO> nodeIdMap, LowcodeDataDomain targetDataDomain,
                                 DataStreamConditionRel condition, DataStreamFieldTrans dataStreamFieldTrans,
                                 String subForm, DataStreamQuoteField quoteField) {
        JSONArray jsonArray = JsonObjectUtils.getJsonArray(targetDataDomain.getInstValue(), subForm);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(quoteField.getNodeId());
            JSONArray sourceJsonJSONArray =
                    JsonObjectUtils.getJsonArray(dataStreamCalculateVO.getJsonValue(), quoteField.getQuoteSubForm());
            JSONArray conformDataList = getConformData(sourceJsonJSONArray, condition, jsonObject);

            if (!conformDataList.isEmpty()) {
                JSONObject conformData = conformDataList.getJSONObject(0);
                jsonObject.put(dataStreamFieldTrans.getFieldId(), conformData.get(quoteField.getQuoteFieldId()));
            }
        }
    }

    // 修改时给子表单赋值 且赋值对象为非子表单
    private static void subToNormal(Map<Long, DataStreamCalculateVO> nodeIdMap, LowcodeDataDomain targetDataDomain,
                                    DataStreamConditionRel condition, DataStreamFieldTrans dataStreamFieldTrans,
                                    JSONObject targetJson, Map<String, FormConfigEncryptKey> encryptKeyMap) {
        String subForm = dataStreamFieldTrans.getSubForm();
        JSONArray jsonArray = JsonObjectUtils.getJsonArray(targetDataDomain.getInstValue(), subForm);
        List<Integer> conformDataList = getConformData(jsonArray, condition, nodeIdMap, encryptKeyMap);
        if (DataStreamFieldQuoteTypeEnum.CALCULATE.name().equals(dataStreamFieldTrans.getQuoteType())) {
            JSONArray targetJsonArray = JsonObjectUtils.getJsonArray(targetJson, subForm);
            for (Integer index : conformDataList) {
                JSONObject jsonObject = targetJsonArray.getJSONObject(index);
                Object value = getValueNotNeedField(dataStreamFieldTrans, nodeIdMap, jsonObject);
                targetJsonArray.getJSONObject(index).put(dataStreamFieldTrans.getFieldId(), value);
            }
        } else {
            DataStreamQuoteField quoteField = dataStreamFieldTrans.getQuoteField();
            DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(quoteField.getNodeId());
            Object value = "";
            if (DataStreamNodeTypeEnum.valueIsJson().contains(dataStreamCalculateVO.getNodeType())) {
                value = dataStreamCalculateVO.getJsonValue().get(quoteField.getQuoteFieldId());
            } else {
                value = dataStreamCalculateVO.getValue();
            }
            if (!conformDataList.isEmpty()) {
                JSONArray targetJsonArray = JsonObjectUtils.getJsonArray(targetJson, subForm);
                for (Integer index : conformDataList) {
                    targetJsonArray.getJSONObject(index).put(dataStreamFieldTrans.getFieldId(), value);
                }
            }
        }
    }

    /**
     * 获取符合的数据
     *
     * @param sourceJsonJSONArray
     * @param condition
     * @return
     */
    public static JSONArray getConformData(JSONArray sourceJsonJSONArray, DataStreamConditionRel condition,
                                           JSONObject targetObject) {
        JSONArray conformData = new JSONArray();
        List<MongoFieldRelate> relates = condition.getRelates();
        for (int i = 0; i < sourceJsonJSONArray.size(); i++) {
            JSONObject valueObject = sourceJsonJSONArray.getJSONObject(i);
            boolean conform = true;
            for (MongoFieldRelate mongoFieldRelate : relates) {
                conform = isConform(targetObject, mongoFieldRelate, valueObject);
                if (!conform) {
                    break;
                }
            }
            if (conform) {
                conformData.add(valueObject);
            }
        }
        return conformData;
    }

    private static boolean isConform(JSONObject targetObject, MongoFieldRelate mongoFieldRelate,
                                     JSONObject valueObject) {
        MongodbSearchCondition mongodbSearchCondition = getMongodbSearchCondition(mongoFieldRelate);
        if (mongoFieldRelate.getQuoteField() == null) {
            mongodbSearchCondition.setValue(mongoFieldRelate.getValue());
        } else {
            Object valueWithKey = valueObject.get(mongoFieldRelate.getQuoteField().getQuoteFieldId());
            mongodbSearchCondition.setValue(Collections.singletonList(valueWithKey));
        }
        return MongoSearchUtils.checkData(mongodbSearchCondition, targetObject);
    }

    private static List<Integer> getConformData(JSONArray sourceJsonJSONArray, DataStreamConditionRel condition,
                                                Map<Long, DataStreamCalculateVO> nodeIdMap,
                                                Map<String, FormConfigEncryptKey> encryptKeyMap) {
        List<Integer> conformDataIndex = new ArrayList<>();
        MongodbSearchFilter filter = MongoSearchUtils.toFilter(condition, nodeIdMap, encryptKeyMap);
        for (int i = 0; i < sourceJsonJSONArray.size(); i++) {
            JSONObject valueObject = sourceJsonJSONArray.getJSONObject(i);
            if (CollectionUtils.isNotEmpty(condition.getRelates())) {
                Boolean conform = MongoSearchUtils.checkData(filter, valueObject);
                if (conform) {
                    conformDataIndex.add(i);
                }
            } else {
                conformDataIndex.add(i);
            }
        }
        return conformDataIndex;
    }

    private static MongodbSearchCondition getMongodbSearchCondition(MongoFieldRelate mongoFieldRelate) {
        MongodbSearchCondition mongodbSearchCondition = new MongodbSearchCondition();
        mongodbSearchCondition.setFieldId(mongoFieldRelate.getFieldId());
        mongodbSearchCondition.setMethod(mongoFieldRelate.getMethod());
        // if (StringUtils.isNotEmpty(mongoFieldRelate.getSubForm())) {
        //     mongodbSearchCondition.setFieldId(mongoFieldRelate.getSubForm());
        //     mongodbSearchCondition.setChildFieldId(mongoFieldRelate.getFieldId());
        // } else {
        //     mongodbSearchCondition.setFieldId(mongoFieldRelate.getFieldId());
        // }
        mongodbSearchCondition.setFieldId(mongoFieldRelate.getFieldId());
        mongodbSearchCondition.setType(mongoFieldRelate.getFieldType());
        return mongodbSearchCondition;
    }

    private static JSONArray getJsonArrayWhileCreate(Map<Long, DataStreamCalculateVO> nodeIdMap,
                                                     List<DataStreamFieldTrans> list,
                                                     List<DataStreamSubFormFilter> subFormFilterList,
                                                     Integer queryMoreIndex,
                                                     Map<String, FormConfigEncryptKey> encryptKeyMap) {
        int maxRow = 0;
        for (DataStreamFieldTrans fieldTrans : list) {
            DataStreamQuoteField quoteField = fieldTrans.getQuoteField();
            if (DataStreamFieldQuoteTypeEnum.NODE_FIELD.name().equalsIgnoreCase(fieldTrans.getQuoteType())) {
                if (StringUtils.isNotEmpty(quoteField.getQuoteSubForm())) {
                    DataStreamCalculateVO calculate = nodeIdMap.get(quoteField.getNodeId());
                    if (DataStreamNodeTypeEnum.valueIsJson().contains(calculate.getNodeType())) {
                        if (DataStreamNodeTypeEnum.MORE.getNodeType().equalsIgnoreCase(calculate.getNodeType()) &&
                                queryMoreIndex != null) {
                            JSONArray sourceJsonJSONArray = JsonObjectUtils.getJsonArray(calculate.getJsonValue(),
                                    quoteField.getQuoteSubForm());
                            JSONObject jsonObject = sourceJsonJSONArray.getJSONObject(queryMoreIndex);
                            if (StringUtils.isNotEmpty(quoteField.getQuoteTableName())) {
                                JSONArray jsonArray = jsonObject.getJSONArray(quoteField.getQuoteTableName());
                                if (CollectionUtils.isNotEmpty(subFormFilterList)) {
                                    DataStreamSubFormFilter dataStreamSubFormFilter = subFormFilterList.stream()
                                            .filter(c -> quoteField.getQuoteKey().equals(c.getQuoteKey())).findFirst()
                                            .orElse(null);
                                    if (dataStreamSubFormFilter != null) {
                                        jsonArray = getConformJson(jsonArray, dataStreamSubFormFilter.getCondition(),
                                                nodeIdMap, encryptKeyMap);
                                    }
                                }
                                maxRow = Math.max(maxRow, jsonArray.size());
                            }
                        } else {
                            maxRow = getMaxRow(nodeIdMap, subFormFilterList, encryptKeyMap, quoteField, calculate,
                                    maxRow);
                        }
                    } else {
                        maxRow = Math.max(maxRow, 1);
                    }
                } else {
                    maxRow = Math.max(maxRow, 1);
                }
            }
        }
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < maxRow; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", SnowFlakeIdUtils.generateStr());
            for (DataStreamFieldTrans dataStreamFieldTrans : list) {
                Object value = null;
                if (!DataStreamFieldQuoteTypeEnum.NODE_FIELD.name()
                        .equalsIgnoreCase(dataStreamFieldTrans.getQuoteType())) {
                    value = getValueNotNeedField(dataStreamFieldTrans, nodeIdMap, null);
                } else {
                    DataStreamQuoteField quoteField = dataStreamFieldTrans.getQuoteField();
                    DataStreamCalculateVO calculateVO = nodeIdMap.get(quoteField.getNodeId());
                    if (StringUtils.isEmpty(quoteField.getQuoteSubForm())) {
                        value = getValueWithKey(calculateVO, quoteField.getQuoteFieldId());
                    } else if (StringUtils.isNotEmpty(quoteField.getQuoteSubForm())) {
                        if (queryMoreIndex != null) {
                            JSONArray sourceJsonJSONArray = JsonObjectUtils.getJsonArray(calculateVO.getJsonValue(),
                                    quoteField.getQuoteSubForm());
                            JSONObject queryModeIndexObject = sourceJsonJSONArray.getJSONObject(queryMoreIndex);
                            if (StringUtils.isEmpty(quoteField.getQuoteTableName())) {
                                value = getValue(nodeIdMap, null, calculateVO, dataStreamFieldTrans, i, encryptKeyMap,
                                        jsonObject);
                            } else {
                                JSONArray queryMoreJsonArray =
                                        queryModeIndexObject.getJSONArray(quoteField.getQuoteTableName());
                                DataStreamSubFormFilter dataStreamSubFormFilter = subFormFilterList.stream()
                                        .filter(c -> quoteField.getQuoteKey().equals(c.getQuoteKey())).findFirst()
                                        .orElse(null);
                                if (dataStreamSubFormFilter != null) {
                                    value = getObject(nodeIdMap, dataStreamSubFormFilter.getCondition(),
                                            dataStreamFieldTrans, i, queryMoreJsonArray, encryptKeyMap, jsonObject);
                                } else {
                                    value = getObject(nodeIdMap, null, dataStreamFieldTrans, i, queryMoreJsonArray,
                                            encryptKeyMap, jsonObject);
                                }
                            }
                        } else {
                            DataStreamSubFormFilter dataStreamSubFormFilter = subFormFilterList.stream()
                                    .filter(c -> quoteField.getQuoteKey().equals(c.getQuoteKey())).findFirst()
                                    .orElse(null);
                            if (dataStreamSubFormFilter != null) {
                                value = getValue(nodeIdMap, dataStreamSubFormFilter.getCondition(), calculateVO,
                                        dataStreamFieldTrans, i, encryptKeyMap, jsonObject);
                            } else {
                                value = getValue(nodeIdMap, null, calculateVO, dataStreamFieldTrans, i, encryptKeyMap,
                                        jsonObject);
                            }
                        }
                    }
                }
                jsonObject.put(dataStreamFieldTrans.getFieldId(), value);
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    private static int getMaxRow(Map<Long, DataStreamCalculateVO> nodeIdMap,
                                 List<DataStreamSubFormFilter> subFormFilterList,
                                 Map<String, FormConfigEncryptKey> encryptKeyMap, DataStreamQuoteField quoteField,
                                 DataStreamCalculateVO calculateVO, int maxRow) {
        DataStreamSubFormFilter dataStreamSubFormFilter =
                subFormFilterList.stream().filter(c -> quoteField.getQuoteKey().equals(c.getQuoteKey())).findFirst()
                        .orElse(null);
        if (dataStreamSubFormFilter != null) {
            maxRow = getMaxRow(nodeIdMap, dataStreamSubFormFilter.getCondition(), calculateVO, quoteField, maxRow,
                    encryptKeyMap);
        } else {
            maxRow = getMaxRow(nodeIdMap, null, calculateVO, quoteField, maxRow, encryptKeyMap);
        }
        return maxRow;
    }

    private static Object getValue(Map<Long, DataStreamCalculateVO> nodeIdMap, DataStreamConditionRel subformFilter,
                                   DataStreamCalculateVO calculateVO, DataStreamFieldTrans dataStreamFieldTrans, int i,
                                   Map<String, FormConfigEncryptKey> encryptKeyMap, JSONObject jsonObject) {
        DataStreamQuoteField quoteField = dataStreamFieldTrans.getQuoteField();
        JSONArray sourceJsonJSONArray =
                JsonObjectUtils.getJsonArray(calculateVO.getJsonValue(), quoteField.getQuoteSubForm());
        return getObject(nodeIdMap, subformFilter, dataStreamFieldTrans, i, sourceJsonJSONArray, encryptKeyMap,
                jsonObject);
    }

    private static Object getObject(Map<Long, DataStreamCalculateVO> nodeIdMap, DataStreamConditionRel subformFilter,
                                    DataStreamFieldTrans fieldTrans, int i, JSONArray sourceJsonJSONArray,
                                    Map<String, FormConfigEncryptKey> encryptKeyMap, JSONObject returnJson) {
        Object value = null;
        if (subformFilter != null) {
            sourceJsonJSONArray = getConformJson(sourceJsonJSONArray, subformFilter, nodeIdMap, encryptKeyMap);
        }
        String quoteFieldId = fieldTrans.getQuoteField().getQuoteFieldId();
        if (sourceJsonJSONArray.size() > i) {
            if (DataStreamFieldQuoteTypeEnum.CALCULATE.name().equals(fieldTrans.getQuoteType())) {
                value = getValueNotNeedField(fieldTrans, nodeIdMap, returnJson);
            } else {
                value = sourceJsonJSONArray.getJSONObject(i).get(quoteFieldId);
            }
        }
        return value;
    }

    private static int getMaxRow(Map<Long, DataStreamCalculateVO> nodeIdMap, DataStreamConditionRel subformFilter,
                                 DataStreamCalculateVO calculateVO, DataStreamQuoteField quoteField, int maxRow,
                                 Map<String, FormConfigEncryptKey> encryptKeyMap) {
        JSONArray sourceJsonJSONArray =
                JsonObjectUtils.getJsonArray(calculateVO.getJsonValue(), quoteField.getQuoteSubForm());
        if (subformFilter != null) {
            sourceJsonJSONArray = getConformJson(sourceJsonJSONArray, subformFilter, nodeIdMap, encryptKeyMap);
        }
        maxRow = Math.max(maxRow, sourceJsonJSONArray.size());
        return maxRow;
    }

    private static JSONArray getConformJson(JSONArray sourceJsonJSONArray,
                                            DataStreamConditionRel dataStreamConditionRel,
                                            Map<Long, DataStreamCalculateVO> nodeIdMap,
                                            Map<String, FormConfigEncryptKey> encryptKeyMap) {
        if (dataStreamConditionRel == null) {
            return sourceJsonJSONArray;
        }
        JSONArray returnJson = new JSONArray();
        MongodbSearchFilter mongodbSearchFilter =
                MongoSearchUtils.toFilter(dataStreamConditionRel, nodeIdMap, encryptKeyMap);
        for (int i = 0; i < sourceJsonJSONArray.size(); i++) {
            JSONObject jsonObject = sourceJsonJSONArray.getJSONObject(i);
            Boolean checkData = MongoSearchUtils.checkData(mongodbSearchFilter, jsonObject);
            if (checkData) {
                returnJson.add(jsonObject);
            }
        }
        return returnJson;
    }

    private static Object getValue(Map<Long, DataStreamCalculateVO> nodeIdMap,
                                   DataStreamFieldTrans dataStreamFieldTrans, JSONObject jsonObject) {
        if (!DataStreamFieldQuoteTypeEnum.NODE_FIELD.name().equalsIgnoreCase(dataStreamFieldTrans.getQuoteType())) {
            return getValueNotNeedField(dataStreamFieldTrans, nodeIdMap, jsonObject);
        } else {
            DataStreamQuoteField quoteField = dataStreamFieldTrans.getQuoteField();
            DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(quoteField.getNodeId());
            if (StringUtils.isEmpty(quoteField.getQuoteSubForm())) {
                return getValueWithKey(dataStreamCalculateVO, quoteField.getQuoteFieldId());
            } else {
                return null;
            }
        }
    }

    public static Object getValueExistMore(Map<Long, DataStreamCalculateVO> nodeIdMap, DataStreamQuoteField quoteField,
                                           Boolean existNull) {
        DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(quoteField.getNodeId());
        if (StringUtils.isEmpty(quoteField.getQuoteSubForm())) {
            return getValueWithKey(dataStreamCalculateVO, quoteField.getQuoteFieldId());
        } else {
            JSONArray jsonArray =
                    JsonObjectUtils.getJsonArray(dataStreamCalculateVO.getJsonValue(), quoteField.getQuoteSubForm());
            if (CollectionUtils.isNotEmpty(jsonArray)) {
                if (StringUtils.isEmpty(quoteField.getQuoteTableName())) {
                    return getObjects(quoteField, jsonArray, existNull);
                } else {
                    List<Object> values = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONArray subJsonArray =
                                JsonObjectUtils.getJsonArray(jsonObject, quoteField.getQuoteTableName());
                        List<Object> objects = getObjects(quoteField, subJsonArray, existNull);
                        values.addAll(objects);
                    }
                    return values;
                }
            } else {
                return new ArrayList<>();
            }
        }
    }

    private static List<Object> getObjects(DataStreamQuoteField quoteField, JSONArray jsonArray, Boolean existNull) {
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Object object = jsonObject.get(quoteField.getQuoteFieldId());
            if (object == null) {
                if (existNull) {
                    values.add(null);
                }
                continue;
            }
            if (FormFieldTypeEnum.getUserFieldType().contains(quoteField.getQuoteFieldType())) {
                values.addAll(JSONArray.parseArray(JSONObject.toJSONString(object), FormUser.class));
            } else if (FormFieldTypeEnum.getDeptFieldType().contains(quoteField.getQuoteFieldType())) {
                values.addAll(JSONArray.parseArray(JSONObject.toJSONString(object), FormDept.class));
            } else if (FormFieldTypeEnum.arrayFieldType().contains(quoteField.getQuoteFieldType())) {
                values.addAll(JSONArray.parseArray(JSONObject.toJSONString(object), Object.class));
            } else {
                values.add(object);
            }
        }
        return values;
    }

    private static Object getValueWithKey(DataStreamCalculateVO dataStreamCalculateVO, String fieldId) {
        if (DataStreamNodeTypeEnum.valueIsJson().contains(dataStreamCalculateVO.getNodeType())) {
            // if (FormSystemFieldEnum.CREATE_NAME.getName().equals(fieldId)) {
            //     return Lists.newArrayList(dataStreamCalculateVO.getJsonValue().get(fieldId));
            // } else {
            //     return dataStreamCalculateVO.getJsonValue().get(fieldId);
            // }
            return dataStreamCalculateVO.getJsonValue().get(fieldId);
        } else {
            return dataStreamCalculateVO.getValue();
        }
    }

    private static Object getValueNotNeedField(DataStreamFieldTrans dataStreamFieldTrans,
                                               Map<Long, DataStreamCalculateVO> nodeIdMap, JSONObject subFormData) {
        if (dataStreamFieldTrans.getQuoteType().equalsIgnoreCase(DataStreamFieldQuoteTypeEnum.CUSTOM.name())) {
            return dataStreamFieldTrans.dealCustomValue();
        }

        if (dataStreamFieldTrans.getQuoteType().equalsIgnoreCase(DataStreamFieldQuoteTypeEnum.EMPTY.name())) {
            return "";
        }
        if (dataStreamFieldTrans.getQuoteType().equalsIgnoreCase(DataStreamFieldQuoteTypeEnum.CALCULATE.name())) {
            Object functionValue = null;
            DefaultContext<String, Object> context = new DefaultContext<>();
            DataStreamCalculate calculate = dataStreamFieldTrans.getCalculate();
            if (calculate.getValMap() != null) {
                calculate.getValMap().forEach((key, val) -> {
                    if (StringUtils.isNotEmpty(val.getQuoteSubForm())) {
                        Object object = subFormData.get(val.getQuoteFieldId());
                        context.put(key, object);
                    } else {
                        DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(val.getNodeId());
                        if (dataStreamCalculateVO == null) {
                            Object object = subFormData.get(val.getQuoteFieldId());
                            context.put(key, object);
                        } else {
                            Object value = MongoDbDataTransUtils.getValueExistMore(nodeIdMap, val, false);
                            context.put(key, value);
                        }
                    }
                });
            }
            FormulaRunner formulaRunner = new FormulaRunner();
            try {
                functionValue = formulaRunner.execute(calculate.getFormula(), context, null, true, false);
                DataStreamCalculateNode dataStreamCalculateNode =
                        AbstractFormDataStreamConverter.INSTANCE.toNode(calculate);
                functionValue = checkAndDealValue(dataStreamCalculateNode, functionValue);
            } catch (Exception e) {
                log.error("公式执行失败", e);
            }
            return functionValue;
        }
        return null;
    }

    public static Object checkAndDealValue(DataStreamCalculateNode dataStreamCalculateNode, Object functionValue) {
        if (functionValue == null) {
            return null;
        }
        if ("STRING".equals(dataStreamCalculateNode.getOutputType())) {
            functionValue = functionValue.toString();
        } else if ("NUMBER".equals(dataStreamCalculateNode.getOutputType())) {
            if (!NumberUtils.isCreatable(functionValue.toString())) {
                throw new FunctionException(ResultCode.FORMULA_ERROR, "当前值不为数字：" + functionValue);
            }
            if (functionValue instanceof String) {
                functionValue = Double.valueOf(functionValue.toString());
            }
        } else if ("DATE".equals(dataStreamCalculateNode.getOutputType())) {
            if (!NumberUtils.isCreatable(functionValue.toString())) {
                throw new FunctionException(ResultCode.FORMULA_ERROR, "当前值不为时间戳：" + functionValue);
            }
            if (functionValue instanceof Long) {
                functionValue = Long.valueOf(functionValue.toString());
                try {
                    new Date(Long.parseLong(functionValue.toString()));
                } catch (Exception e) {
                    throw new FunctionException(ResultCode.FORMULA_ERROR, "当前值不为时间戳：" + functionValue);
                }
            } else {
                throw new FunctionException(ResultCode.FORMULA_ERROR, "当前值不为时间戳：" + functionValue);
            }
        } else if ("USER".equals(dataStreamCalculateNode.getOutputType())) {

        }
        return functionValue;
    }
}
