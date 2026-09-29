package com.wuji.service.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.JsonObjectUtils;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.constant.Constants;
import com.wuji.service.converter.AbstractMongoDbConverter;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.enums.FormFieldGroupRuleEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.enums.MongodbAggregateAddressGroupTypeEnum;
import com.wuji.service.enums.MongodbCalculateEnum;
import com.wuji.service.enums.SearchFilterRelEnum;
import com.wuji.service.enums.SearchMethodEnum;
import com.wuji.service.model.info.FormAddress;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.info.FormPrivilegeFieldConfig;
import com.wuji.service.model.info.MongoFieldRelate;
import com.wuji.service.model.info.MongoSort;
import com.wuji.service.model.info.MongodbSearchCondition;
import com.wuji.service.model.info.MongodbSearchConditionQuote;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamConditionRel;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import com.wuji.service.model.mongo.MapOperation;
import com.wuji.service.model.mongo.ProjectAggregation;
import com.wuji.service.model.request.FormFieldRequest;
import com.wuji.service.model.request.MongoGroupLookUpRequest;
import com.wuji.service.model.request.factory.DataFactoryStageGroupFieldRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MongoSearchUtils {

    public static void buildCommonFilter(Query query, String applicationId, String formId) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("status").ne(FormDataStatusEnum.DELETED.name()));
        criteriaList.add(Criteria.where("applicationId").is(applicationId));
        criteriaList.add(Criteria.where("formId").is(formId));
        Criteria criteria = new Criteria();
        criteria.andOperator(criteriaList);
        query.addCriteria(criteria);
    }

    public static void buildCommonFilterWithout(Query query, String applicationId, String formId) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("status").ne(FormDataStatusEnum.DELETED.name()));
        // criteriaList.add(Criteria.where("applicationId").is(applicationId));
        // criteriaList.add(Criteria.where("formId").is(formId));
        Criteria criteria = new Criteria();
        criteria.andOperator(criteriaList);
        query.addCriteria(criteria);
    }

    public static void buildCommonFilter(List<Criteria> criteriaList, String applicationId, String formId) {
        criteriaList.add(Criteria.where("status").ne(FormDataStatusEnum.DELETED.name()));
        criteriaList.add(Criteria.where("applicationId").is(applicationId));
        criteriaList.add(Criteria.where("formId").is(formId));
    }


    /**
     * 高级搜索筛选
     *
     * @param query
     * @param mongodbSearchFilter
     */
    public static void buildSearchCondition(Query query, MongodbSearchFilter mongodbSearchFilter) {
        if (mongodbSearchFilter == null) {
            return;
        }
        if (CollectionUtils.isEmpty(mongodbSearchFilter.getConditionList())) {
            return;
        }

        Criteria criteria = buildCriteriaByFilter(mongodbSearchFilter, new ArrayList<>());
        if (criteria == null) {
            return;
        }
        query.addCriteria(criteria);
    }

    public static Criteria buildCriteriaByFilter(MongodbSearchFilter mongodbSearchFilter,
                                                 List<MongoGroupLookUpRequest> mongoGroupLookUpRequestList) {
        if (mongodbSearchFilter == null) {
            return null;
        }
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        if (CollectionUtils.isEmpty(mongodbSearchFilter.getConditionList())) {
            return null;
        }
        for (MongodbSearchCondition mongodbSearchCondition : mongodbSearchFilter.getConditionList()) {
            if ("PRIVILEGE".equals(mongodbSearchCondition.getQuoteType())) {
                MongodbSearchConditionQuote quote = mongodbSearchCondition.getQuote();
                MongoGroupLookUpRequest mongoGroupLookUpRequest = AbstractMongoDbConverter.INSTANCE.toRequest(quote);
                mongoGroupLookUpRequest.setFieldId(mongodbSearchCondition.getFieldId());
                mongoGroupLookUpRequest.setFieldType(mongodbSearchCondition.getType());
                mongoGroupLookUpRequestList.add(mongoGroupLookUpRequest);
                if (StringUtils.isEmpty(quote.getSubForm())) {
                    Criteria size = new Criteria(mongoGroupLookUpRequest.getAlias()).not().size(0);
                    criteriaList.add(size);
                }
                continue;
            }
            SearchMethodEnum searchMethodEnum =
                    SearchMethodEnum.valueOf(mongodbSearchCondition.getMethod().toUpperCase());
            Criteria build = getCriteria(mongodbSearchCondition, searchMethodEnum);
            if (build != null) {
                criteriaList.add(build);
            }
        }
        if (CollectionUtils.isEmpty(criteriaList)) {
            return null;
        }
        if (SearchFilterRelEnum.AND.name().equals(mongodbSearchFilter.getRel())) {
            criteria.andOperator(criteriaList);
        } else if (SearchFilterRelEnum.OR.name().equals(mongodbSearchFilter.getRel())) {
            criteria.orOperator(criteriaList);
        }
        return criteria;
    }

    public static void filterValueIsEmpty(MongodbSearchFilter matchRule) {
        if (matchRule != null && CollectionUtils.isNotEmpty(matchRule.getConditionList())) {
            List<MongodbSearchCondition> conditionList = new ArrayList<>();
            for (MongodbSearchCondition mongodbSearchCondition : matchRule.getConditionList()) {
                if (!SearchMethodEnum.valueIsEmpty().contains(mongodbSearchCondition.getMethod())) {
                    if (CollectionUtils.isEmpty(mongodbSearchCondition.getValue())) {
                        continue;
                    }
                }
                conditionList.add(mongodbSearchCondition);
            }
            matchRule.setConditionList(conditionList);
        }
    }

    private static Criteria getCriteria(MongodbSearchCondition mongodbSearchCondition,
                                        SearchMethodEnum searchMethodEnum) {
        Criteria criteria = null;
        String fieldId = getFieldId(mongodbSearchCondition.getFieldId(), mongodbSearchCondition.getType());
        List<Object> value = new ArrayList<>();
        if (!SearchMethodEnum.valueIsEmpty().contains(searchMethodEnum.name())) {
            value = dealUserOrDeptValue(mongodbSearchCondition.getType(), mongodbSearchCondition.dealValue(),
                    mongodbSearchCondition.getFieldId());
        }
        if (SearchMethodEnum.FORMULA == searchMethodEnum && "custom".equals(mongodbSearchCondition.getSearchType())) {
            value = mongodbSearchCondition.getValue();
        }
        if (CollectionUtils.isEmpty(value)) {
            value = Collections.singletonList(null);
        }
        switch (searchMethodEnum) {
            case EQ:
                if (StringUtils.isEmpty(mongodbSearchCondition.getChildFieldId())) {
                    criteria = new Criteria(fieldId).is(value.get(0));
                } else {
                    Criteria search = Criteria.where(mongodbSearchCondition.getChildFieldId()).is(value.get(0));
                    criteria = new Criteria(fieldId).elemMatch(search);
                }
                break;
            case NE:
                if (StringUtils.isEmpty(mongodbSearchCondition.getChildFieldId())) {
                    criteria = new Criteria(fieldId).ne(value.get(0));
                } else {
                    List<Criteria> searchNeList = new ArrayList<>();
                    Criteria search = Criteria.where(mongodbSearchCondition.getChildFieldId()).ne(value.get(0));
                    searchNeList.add(new Criteria(fieldId).elemMatch(search));
                    searchNeList.add(new Criteria(fieldId).isNull());
                    criteria = new Criteria().orOperator(searchNeList);
                }
                break;
            case IN:
                if (StringUtils.isEmpty(mongodbSearchCondition.getChildFieldId())) {
                    criteria = new Criteria(fieldId).in(value);
                } else {
                    if (FormFieldTypeEnum.getUserFieldType().contains(mongodbSearchCondition.getType())) {
                        fieldId = getFieldId(mongodbSearchCondition.getFieldId(), "") + "." +
                                mongodbSearchCondition.getChildFieldId();
                        Criteria search = Criteria.where("assigneeId").in(value);
                        criteria = new Criteria(fieldId).elemMatch(search);
                    } else if (FormFieldTypeEnum.getDeptFieldType().contains(mongodbSearchCondition.getType())) {
                        fieldId = getFieldId(mongodbSearchCondition.getFieldId(), "") + "." +
                                mongodbSearchCondition.getChildFieldId();
                        Criteria search = Criteria.where("value").in(value);
                        criteria = new Criteria(fieldId).elemMatch(search);
                    } else {
                        fieldId = getFieldId(mongodbSearchCondition.getFieldId(), "");
                        Criteria search = Criteria.where(mongodbSearchCondition.getChildFieldId()).in(value);
                        criteria = new Criteria(fieldId).elemMatch(search);
                    }
                }
                break;
            case NI:
                if (StringUtils.isEmpty(mongodbSearchCondition.getChildFieldId())) {
                    criteria = new Criteria(fieldId).nin(value);
                } else {
                    Criteria search = Criteria.where(mongodbSearchCondition.getChildFieldId()).nin(value);
                    criteria = new Criteria(fieldId).elemMatch(search);
                }
                break;
            case UL:
                List<Criteria> criteriaList = new ArrayList<>();
                criteriaList.add(new Criteria(fieldId).isNull());
                criteriaList.add(new Criteria(fieldId).is(""));
                criteria = new Criteria().orOperator(criteriaList);
                break;
            case NU:
                List<Criteria> andList = new ArrayList<>();
                andList.add(new Criteria(fieldId).ne(null));
                andList.add(new Criteria(fieldId).ne(""));
                criteria = new Criteria().andOperator(andList);
                break;
            case NULL:
                criteria = new Criteria(fieldId).isNull();
                break;
            case NU_NULL:
                criteria = new Criteria(fieldId).ne(null);
                break;
            case LK:
                if (StringUtils.isEmpty(mongodbSearchCondition.getChildFieldId())) {
                    if (value.get(0) == null) {
                        criteria = new Criteria(fieldId).regex("");
                    } else {
                        criteria = new Criteria(fieldId).regex((String) value.get(0));
                    }
                } else {
                    Criteria search =
                            Criteria.where(mongodbSearchCondition.getChildFieldId()).regex((String) value.get(0));
                    criteria = new Criteria(fieldId).elemMatch(search);
                }
                break;
            case UK:
                if (StringUtils.isEmpty(mongodbSearchCondition.getChildFieldId())) {
                    criteria = new Criteria(fieldId).not().regex((String) value.get(0));
                } else {
                    Criteria search =
                            Criteria.where(mongodbSearchCondition.getChildFieldId()).not().regex((String) value.get(0));
                    criteria = new Criteria(fieldId).elemMatch(search);
                }
                break;
            case GT:
                if (StringUtils.isEmpty(mongodbSearchCondition.getChildFieldId())) {
                    criteria = new Criteria(fieldId).gt(Double.valueOf(value.get(0).toString()));
                } else {
                    Criteria search = Criteria.where(mongodbSearchCondition.getChildFieldId())
                            .gt(Double.valueOf(value.get(0).toString()));
                    criteria = new Criteria(fieldId).elemMatch(search);
                }
                break;
            case LT:
                if (StringUtils.isEmpty(mongodbSearchCondition.getChildFieldId())) {
                    criteria = new Criteria(fieldId).lt(Double.valueOf(value.get(0).toString()));
                } else {
                    Criteria search = Criteria.where(mongodbSearchCondition.getChildFieldId())
                            .lt(Double.valueOf(value.get(0).toString()));
                    criteria = new Criteria(fieldId).elemMatch(search);
                }
                break;
            case LE:
                if (StringUtils.isEmpty(mongodbSearchCondition.getChildFieldId())) {
                    criteria = new Criteria(fieldId).lte(Double.valueOf(value.get(0).toString()));
                } else {
                    Criteria search = Criteria.where(mongodbSearchCondition.getChildFieldId())
                            .lte(Double.valueOf(value.get(0).toString()));
                    criteria = new Criteria(fieldId).elemMatch(search);
                }
                break;
            case GE:
                if (StringUtils.isEmpty(mongodbSearchCondition.getChildFieldId())) {
                    criteria = new Criteria(fieldId).gte(Double.valueOf(value.get(0).toString()));
                } else {
                    Criteria search = Criteria.where(mongodbSearchCondition.getChildFieldId())
                            .gte(Double.valueOf(value.get(0).toString()));
                    criteria = new Criteria(fieldId).elemMatch(search);
                }
                break;
            case BT:
                criteria = new Criteria(fieldId).in(value);
                break;
            case UBT:
                criteria = new Criteria(fieldId).nin(value);
                break;
            case ALL:
                criteria = new Criteria(fieldId).all(value);
                break;
            case EMPTY:
                fieldId = getFieldId(mongodbSearchCondition.getFieldId(), "");
                List<Criteria> searchEmptyList = new ArrayList<>();
                searchEmptyList.add(new Criteria(fieldId).isNull());
                searchEmptyList.add(new Criteria(fieldId).size(0));
                criteria = new Criteria().orOperator(searchEmptyList);
                break;
            case UN_EMPTY:
                fieldId = getFieldId(mongodbSearchCondition.getFieldId(), "");
                List<Criteria> searchList = new ArrayList<>();
                searchList.add(new Criteria(fieldId).ne(null));
                searchList.add(new Criteria(fieldId).ne(new ArrayList<>()));
                criteria = new Criteria().andOperator(searchList);
                break;
            case RANGE:
                if (StringUtils.isEmpty(mongodbSearchCondition.getChildFieldId())) {
                    criteria = new Criteria(fieldId);
                    if (value.get(0) != null) {
                        criteria.gte(value.get(0));
                    }
                    if (value.get(1) != null) {
                        criteria.lte(value.get(1));
                    }
                } else {
                    Criteria search = Criteria.where(mongodbSearchCondition.getChildFieldId());
                    if (value.get(0) != null) {
                        search.gte(value.get(0));
                    }
                    if (value.get(1) != null) {
                        search.lte(value.get(1));
                    }
                    criteria = new Criteria(fieldId).elemMatch(search);
                }
                break;
            case FORMULA:
                criteria = new Criteria(fieldId);
                if ("custom".equals(mongodbSearchCondition.getSearchType())) {
                    if (value.get(0) != null) {
                        String start = value.get(0).toString();
                        String startUnit = start.substring(start.length() - 1);
                        Integer startNum = Integer.valueOf(start.substring(0, start.length() - 1));
                        Date startTime = TimeUtils.getTransTime(startUnit, startNum);
                        criteria.gte(startTime.getTime());
                    }
                    if (value.get(1) != null) {
                        String end = value.get(1).toString();
                        String endUnit = end.substring(end.length() - 1);
                        Integer endNum = Integer.valueOf(end.substring(0, end.length() - 1));
                        Date endTime = TimeUtils.getTransTime(endUnit, endNum);
                        criteria.lte(endTime.getTime() + 24 * 60 * 60 * 1000 - 1);
                    }
                } else {
                    List<Long> dataList = TimeUtils.transZeroByType(mongodbSearchCondition.getSearchType(), new Date());
                    criteria.gte(dataList.get(0));
                    criteria.lte(dataList.get(1) - 1);
                }
                break;
            default:
                break;
        }
        return criteria;
    }


    public static Boolean checkData(MongodbSearchCondition mongodbSearchCondition, JSONObject jsonObject) {
        List<Object> value;
        SearchMethodEnum searchMethodEnum = SearchMethodEnum.valueOf(mongodbSearchCondition.getMethod());
        if (CollectionUtils.isEmpty(mongodbSearchCondition.getValue())) {
            if (!SearchMethodEnum.valueIsEmpty().contains(searchMethodEnum.name())) {
                return Boolean.FALSE;
            }
        }
        value = dealUserOrDeptValue(mongodbSearchCondition.getType(), mongodbSearchCondition.dealValue(),
                mongodbSearchCondition.getFieldId());
        List<Object> originValue = new ArrayList<>();
        if (StringUtils.isEmpty(mongodbSearchCondition.getChildFieldId())) {
            originValue = getJsonValue(mongodbSearchCondition.getFieldId(), null, mongodbSearchCondition.getType(),
                    jsonObject);
        } else {
            originValue = getJsonValue(mongodbSearchCondition.getChildFieldId(), mongodbSearchCondition.getFieldId(),
                    mongodbSearchCondition.getType(), jsonObject);
        }
        if (FormFieldTypeEnum.ADDRESS_SELECTION.getFieldType().equals(mongodbSearchCondition.getType())) {
            List<FormAddress> formAddresses =
                    JSONArray.parseArray(JSONObject.toJSONString(originValue), FormAddress.class);
            originValue = formAddresses.stream().map(FormAddress::getFullAddress).collect(Collectors.toList());
        } else {
            originValue = dealUserOrDeptValue(mongodbSearchCondition.getType(), originValue,
                    mongodbSearchCondition.getFieldId());
        }
        return checkData(searchMethodEnum, originValue, value, mongodbSearchCondition);
    }

    public static Boolean checkData(MongodbSearchFilter filter, JSONObject jsonObject) {
        Boolean executeResult = Boolean.FALSE;
        for (MongodbSearchCondition mongodbSearchCondition : filter.getConditionList()) {
            Boolean result = MongoSearchUtils.checkData(mongodbSearchCondition, jsonObject);
            executeResult = result;
            if ("AND".equalsIgnoreCase(filter.getRel())) {
                if (!executeResult) {
                    break;
                }
            } else {
                if (result) {
                    break;
                }
            }
        }
        return executeResult;
    }

    public static boolean checkData(SearchMethodEnum searchMethodEnum, List<Object> originValue, List<Object> value,
                                    MongodbSearchCondition mongodbSearchCondition) {
        boolean result = false;
        switch (searchMethodEnum) {
            case EQ:
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null &&
                        value.get(0).equals(originValue.get(0))) {
                    result = true;
                }
                break;
            case NE:
                if (CollectionUtils.isEmpty(originValue) || originValue.get(0) == null) {
                    result = true;
                    break;
                }
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null &&
                        !value.get(0).equals(originValue.get(0))) {
                    result = true;
                }
                break;
            case IN:
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null &&
                        value.contains(originValue.get(0))) {
                    result = true;
                }
                break;
            case NI:
                if (CollectionUtils.isEmpty(originValue) || originValue.get(0) == null) {
                    result = true;
                    break;
                }
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null &&
                        !value.contains(originValue.get(0))) {
                    result = true;
                }
                break;
            case UL:
                if (CollectionUtils.isEmpty(originValue) || originValue.get(0) == null ||
                        StringUtils.isEmpty(originValue.get(0).toString())) {
                    result = true;
                }
                break;
            case NU:
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null &&
                        StringUtils.isNotEmpty(originValue.toString())) {
                    result = true;
                }
                break;
            case LK:
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null &&
                        originValue.toString().contains(value.get(0).toString())) {
                    result = true;
                }
                break;
            case UK:
                if (CollectionUtils.isEmpty(originValue) || originValue.get(0) == null) {
                    result = true;
                    break;
                }
                if (!originValue.toString().contains(value.get(0).toString())) {
                    result = true;
                }
                break;
            case GT:
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null &&
                        Double.parseDouble(originValue.get(0).toString()) >
                                Double.parseDouble(value.get(0).toString())) {
                    result = true;
                }
                break;
            case LT:
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null &&
                        Double.parseDouble(originValue.get(0).toString()) <
                                Double.parseDouble(value.get(0).toString())) {
                    result = true;
                }
                break;
            case LE:
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null &&
                        Double.parseDouble(originValue.get(0).toString()) <=
                                Double.parseDouble(value.get(0).toString())) {
                    result = true;
                }
                break;
            case GE:
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null &&
                        Double.parseDouble(originValue.get(0).toString()) >=
                                Double.parseDouble(value.get(0).toString())) {
                    result = true;
                }
                break;
            case BT:
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null) {
                    List<Object> originValueList =
                            JSONArray.parseArray(JSONArray.toJSONString(originValue), Object.class);
                    if (originValueList.contains(value.get(0))) {
                        result = true;
                    }
                }
                break;
            case UBT:
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null) {
                    List<Object> originValueList =
                            JSONArray.parseArray(JSONArray.toJSONString(originValue), Object.class);
                    if (!originValueList.contains(value.get(0))) {
                        result = true;
                    }
                }
                break;
            case ALL:
                if (CollectionUtils.isNotEmpty(originValue)) {
                    List<Object> originValueList =
                            JSONArray.parseArray(JSONArray.toJSONString(originValue), Object.class);
                    if (new HashSet<>(originValueList).containsAll(value)) {
                        result = true;
                    }
                }
                break;
            case EMPTY:
                if (CollectionUtils.isEmpty(originValue)) {
                    result = true;
                }
                break;
            case UN_EMPTY:
                if (CollectionUtils.isNotEmpty(originValue)) {
                    result = true;
                }
                break;
            case NULL:
                if (CollectionUtils.isEmpty(originValue) || originValue.get(0) == null ||
                        StringUtils.isEmpty(originValue.get(0).toString())) {
                    result = true;
                }
                break;
            case NU_NULL:
                if (CollectionUtils.isNotEmpty(originValue) && originValue.get(0) != null) {
                    result = true;
                }
                break;
            case RANGE:
                if (value.get(0) != null && value.get(1) != null) {
                    if (CollectionUtils.isNotEmpty(originValue) && Double.parseDouble(originValue.get(0).toString()) >=
                            Double.parseDouble(value.get(0).toString()) &&
                            Double.parseDouble(originValue.get(0).toString()) <=
                                    Double.parseDouble(value.get(1).toString())) {
                        result = true;
                    }
                }

                if (value.get(0) != null && value.get(1) == null) {
                    if (CollectionUtils.isNotEmpty(originValue) && Double.parseDouble(originValue.get(0).toString()) >=
                            Double.parseDouble(value.get(0).toString())) {
                        result = true;
                    }
                }

                if (value.get(0) == null && value.get(1) != null) {
                    if (CollectionUtils.isNotEmpty(originValue) && Double.parseDouble(originValue.get(0).toString()) <=
                            Double.parseDouble(value.get(1).toString())) {
                        result = true;
                    }
                }
                break;
            case FORMULA:
                List<Long> timeList = new ArrayList<>();
                if ("custom".equals(mongodbSearchCondition.getSearchType())) {
                    if (value.get(0) != null) {
                        String start = value.get(0).toString();
                        String startUnit = start.substring(start.length() - 1);
                        Integer startNum = Integer.valueOf(start.substring(0, start.length() - 1));
                        Date startTime = TimeUtils.getTransTime(startUnit, startNum);
                        timeList.add(startTime.getTime());
                    } else {
                        timeList.add(null);
                    }
                    if (value.get(1) != null) {
                        String end = value.get(1).toString();
                        String endUnit = end.substring(end.length() - 1);
                        Integer endNum = Integer.valueOf(end.substring(0, end.length() - 1));
                        Date endTime = TimeUtils.getTransTime(endUnit, endNum);
                        timeList.add(endTime.getTime());
                    } else {
                        timeList.add(null);
                    }
                } else {
                    timeList = TimeUtils.transZeroByType(mongodbSearchCondition.getSearchType(), new Date());
                }
                if (timeList.get(0) != null && timeList.get(1) != null) {
                    if (CollectionUtils.isNotEmpty(originValue) && Double.parseDouble(originValue.get(0).toString()) >=
                            Double.parseDouble(timeList.get(0).toString()) &&
                            Double.parseDouble(originValue.get(0).toString()) <=
                                    Double.parseDouble(timeList.get(1).toString())) {
                        result = true;
                    }
                }

                if (timeList.get(0) != null && timeList.get(1) == null) {
                    if (CollectionUtils.isNotEmpty(originValue) && Double.parseDouble(originValue.get(0).toString()) >=
                            Double.parseDouble(timeList.get(0).toString())) {
                        result = true;
                    }
                }

                if (timeList.get(0) == null && timeList.get(1) != null) {
                    if (CollectionUtils.isNotEmpty(originValue) && Double.parseDouble(originValue.get(0).toString()) <=
                            Double.parseDouble(timeList.get(1).toString())) {
                        result = true;
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 字段数据需要特殊处理（现在是部门和用户）
     *
     * @param type
     * @param value
     * @param fieldId
     * @return
     */
    private static List<Object> dealUserOrDeptValue(String type, Object value, String fieldId) {
        if (value == null) {
            return new ArrayList<>();
        }
        Object returnValue = value;
        if (FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE_USED.getFieldType().equals(type) ||
                FormFieldTypeEnum.getDeptFieldType().contains(type)) {
            List<FormDept> formDeptList = JSONArray.parseArray(JSONObject.toJSONString(value), FormDept.class);
            returnValue = formDeptList.stream().map(FormDept::getValue).collect(Collectors.toList());
        }
        // 用户
        if (FormFieldTypeEnum.getUserFieldType().contains(type) ||
                FormFieldTypeEnum.FORM_INPUT_USER_SINGLE_USED.getFieldType().equals(type) ||
                (type.equals("system") && fieldId.equals(FormSystemFieldEnum.CREATE_NAME.getName()))) {
            List<Object> objectList = JSONArray.parseArray(JSONObject.toJSONString(value), Object.class);
            if (CollectionUtils.isNotEmpty(objectList) && objectList.get(0) instanceof String) {
                returnValue = objectList.stream().map(c -> Long.valueOf((String) c)).collect(Collectors.toList());
            } else {
                List<FormUser> formUserList = JSONArray.parseArray(JSONObject.toJSONString(value), FormUser.class);
                returnValue = formUserList.stream().map(FormUser::getAssigneeId).collect(Collectors.toList());
            }
        }
        if (returnValue instanceof List) {
            return JSONArray.parseArray(JSONObject.toJSONString(returnValue), Object.class);
        } else {
            return Collections.singletonList(returnValue);
        }
    }

    public static String getFieldId(String fieldId, String fieldType) {
        // 当字段类型为系统字段时
        if ("system".equals(fieldType)) {
            // 用户类型字段需特殊处理
            if (FormSystemFieldEnum.getUserFieldList().contains(fieldId)) {
                return fieldId + ".assigneeId";
            }
            return fieldId;
        }
        if (FormFieldTypeEnum.getUserFieldType().contains(fieldType)) {
            return "instValue." + fieldId + ".assigneeId";
        }
        if (FormFieldTypeEnum.getDeptFieldType().contains(fieldType)) {
            return "instValue." + fieldId + ".value";
        }
        if (FormFieldTypeEnum.SELECT_DATA.getFieldType().equals(fieldType)) {
            return "instValue." + fieldId + ".uuid";
        }
        if (FormFieldTypeEnum.ADDRESS_SELECTION.getFieldType().equals(fieldType)) {
            return "instValue." + fieldId + ".fullAddress";
        }

        return "instValue." + fieldId;
    }

    public static String getFieldIdNotExistLogic(String fieldId, String fieldType) {
        // 当字段类型为系统字段时
        if ("system".equals(fieldType)) {
            return fieldId;
        }
        return "instValue." + fieldId;
    }

    /**
     * 返回字段
     *
     * @param groupOperation
     * @param mongodbFieldRequestList
     * @return
     */
    public static GroupOperation calculate(GroupOperation groupOperation,
                                           List<MongodbSearchField> mongodbFieldRequestList, Boolean field) {
        for (MongodbSearchField mongodbFieldRequest : mongodbFieldRequestList) {
            if (StringUtils.isEmpty(mongodbFieldRequest.getOp())) {
                continue;
            }
            String fieldId = mongodbFieldRequest.getName();
            if (field) {
                fieldId = getFieldId(mongodbFieldRequest.getName(), mongodbFieldRequest.getType());
            }
            groupOperation =
                    getGroupOperation(groupOperation, mongodbFieldRequest, fieldId, mongodbFieldRequest.getTag());
        }
        return groupOperation;
    }

    public static GroupOperation projectCalculate(GroupOperation groupOperation,
                                                  List<DataFactoryStageGroupFieldRequest> mongodbFieldRequestList,
                                                  Boolean field) {
        for (MongodbSearchField mongodbFieldRequest : mongodbFieldRequestList) {
            String fieldId = mongodbFieldRequest.getName() + "_" + mongodbFieldRequest.getFormId() + "_" +
                    mongodbFieldRequest.getOp();
            groupOperation = getGroupOperation(groupOperation, mongodbFieldRequest, fieldId, fieldId);
        }
        return groupOperation;
    }

    private static GroupOperation getGroupOperation(GroupOperation groupOperation,
                                                    MongodbSearchField mongodbFieldRequest, String fieldId, String as) {

        MongodbCalculateEnum mongodbCalculateEnum = MongodbCalculateEnum.NONE;
        if (mongodbFieldRequest.getOp() != null) {
            mongodbCalculateEnum = MongodbCalculateEnum.valueOf(mongodbFieldRequest.getOp());
        }

        switch (mongodbCalculateEnum) {
            case AVG:
                groupOperation = groupOperation.avg(fieldId).as(as);
                break;
            case MAX:
                groupOperation = groupOperation.max(fieldId).as(as);
                break;
            case MIN:
                groupOperation = groupOperation.min(fieldId).as(as);
                break;
            case SUM:
                groupOperation = groupOperation.sum(fieldId).as(as);
                break;
            case COUNT:
                Document document = MongoFunctionUtils.checkFieldIsNotNullCount("$" + fieldId);
                groupOperation =
                        groupOperation.sum(AggregationExpression.from(MongoExpression.create(document.toJson())))
                                .as(as);
                break;
            case COUNT_DISTINCT:
                groupOperation = groupOperation.addToSet(fieldId).as(as);
                break;
        }
        return groupOperation;
    }

    public static void aggregateUnwind(List<MongodbSearchField> metricList, List<MongodbSearchField> allFieldList,
                                       List<AggregationOperation> aggregationList) {
        for (MongodbSearchField mongodbSearchField : allFieldList) {
            if (FormFieldTypeEnum.getUserDeptFieldType().contains(mongodbSearchField.getType())) {
                String fieldId = MongoSearchUtils.getFieldId(mongodbSearchField.getName(), "");
                UnwindOperation unwind = Aggregation.unwind("$" + fieldId, false);
                aggregationList.add(unwind);
            } else if (FormFieldTypeEnum.arrayFieldType().contains(mongodbSearchField.getType())) {
                String fieldId = MongoSearchUtils.getFieldId(mongodbSearchField.getName(), "");
                UnwindOperation unwind = Aggregation.unwind("$" + fieldId, false);
                aggregationList.add(unwind);
            } else if (StringUtils.isNotEmpty(mongodbSearchField.getSubForm())) {
                String fieldId = MongoSearchUtils.getFieldId(mongodbSearchField.getSubForm(), "");
                UnwindOperation unwind = Aggregation.unwind("$" + fieldId, false);
                aggregationList.add(unwind);
            }
        }
        for (MongodbSearchField mongodbSearchField : metricList) {
            if (FormFieldTypeEnum.getUserDeptFieldType().contains(mongodbSearchField.getType())) {
                String fieldId = MongoSearchUtils.getFieldId(mongodbSearchField.getName(), "");
                UnwindOperation unwind = Aggregation.unwind("$" + fieldId, false);
                aggregationList.add(unwind);
            } else if (StringUtils.isNotEmpty(mongodbSearchField.getSubForm())) {
                String fieldId = MongoSearchUtils.getFieldId(mongodbSearchField.getSubForm(), "");
                UnwindOperation unwind = Aggregation.unwind("$" + fieldId, false);
                aggregationList.add(unwind);
            } else if (FormFieldTypeEnum.FORM_INPUT_ROLE_MULTIPLE.getFieldType().equals(mongodbSearchField.getType())) {
                String fieldId = MongoSearchUtils.getFieldId(mongodbSearchField.getName(), "");
                UnwindOperation unwind = Aggregation.unwind("$" + fieldId, false);
                aggregationList.add(unwind);
            }
        }
    }

    public static void buildFieldList(List<Field> fields, List<MongodbSearchField> mongodbSearchFieldList) {
        for (MongodbSearchField mongodbFieldRequest : mongodbSearchFieldList) {
            String fieldId = MongoSearchUtils.getFieldId(mongodbFieldRequest.getName(), mongodbFieldRequest.getType());
            if (!Constants.FORM_DATE_TYPE.equals(mongodbFieldRequest.getType()) &&
                    !FormFieldTypeEnum.ADDRESS_SELECTION.getFieldType().equals(mongodbFieldRequest.getType()) &&
                    !(FormSystemFieldEnum.CREATE_TIME.getName().equals(mongodbFieldRequest.getName()) ||
                            FormSystemFieldEnum.UPDATE_TIME.getName().equals(mongodbFieldRequest.getName()))) {

                if (FormFieldTypeEnum.getUserDeptFieldType().contains(mongodbFieldRequest.getType()) ||
                        FormSystemFieldEnum.CREATE_NAME.getName().equals(mongodbFieldRequest.getName())) {
                    fields.add(Fields.field(mongodbFieldRequest.getTag(), fieldId));
                } else {
                    fields.add(Fields.field(mongodbFieldRequest.getTag(), fieldId));
                }
            }

        }
    }

    public static void buildFieldListGroup(List<Field> fields, List<MongodbSearchField> mongodbSearchFieldList) {
        for (MongodbSearchField mongodbFieldRequest : mongodbSearchFieldList) {
            String fieldId = MongoSearchUtils.getFieldId(mongodbFieldRequest.getName(), mongodbFieldRequest.getType());
            if (!FormFieldTypeEnum.ADDRESS_SELECTION.getFieldType().equals(mongodbFieldRequest.getType())) {
                if (FormFieldTypeEnum.getUserDeptFieldType().contains(mongodbFieldRequest.getType()) ||
                        FormSystemFieldEnum.CREATE_NAME.getName().equals(mongodbFieldRequest.getName())) {
                    fields.add(Fields.field(mongodbFieldRequest.getTag(), fieldId));
                } else {
                    fields.add(Fields.field(mongodbFieldRequest.getTag(), fieldId));
                }
            }

        }
    }

    public static List<String> coverDateReturn(List<MongodbSearchField> widget,
                                               List<AggregationOperation> aggregationList) {
        List<String> groupDateList = new ArrayList<>();
        AddFieldsOperation addFieldsOperation = MongoSearchUtils.coverDate(widget, groupDateList);
        if (addFieldsOperation != null) {
            aggregationList.add(addFieldsOperation);
        }
        return groupDateList;
    }

    public static void coverAddress(List<MongodbSearchField> allFieldList, List<Field> fields) {
        for (MongodbSearchField mongodbSearchField : allFieldList) {
            String fieldId = MongoSearchUtils.getFieldId(mongodbSearchField.getName(), "");
            if (FormFieldTypeEnum.ADDRESS_SELECTION.getFieldType().equals(mongodbSearchField.getType())) {
                List<String> splitList =
                        Arrays.stream(mongodbSearchField.getGroupType().split("_")).collect(Collectors.toList());
                for (String split : splitList) {
                    fields.add(Fields.field(mongodbSearchField.getTag() + "_" + split, fieldId + "." + split));
                }
            }
        }
    }

    public static AddFieldsOperation coverDate(List<MongodbSearchField> mongodbFieldRequestList,
                                               List<String> searchNameList) {
        boolean exist = false;
        AddFieldsOperation.AddFieldsOperationBuilder fieldsOperationBuilder = Aggregation.addFields();
        for (MongodbSearchField mongodbFieldRequest : mongodbFieldRequestList) {
            String fieldId = getFieldId(mongodbFieldRequest.getName(), mongodbFieldRequest.getType());
            if (Constants.FORM_DATE_TYPE.equals(mongodbFieldRequest.getType()) ||
                    FormSystemFieldEnum.CREATE_TIME.getName().equals(fieldId) ||
                    FormSystemFieldEnum.UPDATE_TIME.getName().equals(fieldId)) {
                fieldsOperationBuilder =
                        addFormattedDateField(fieldsOperationBuilder, mongodbFieldRequest.getGroupType(), fieldId,
                                searchNameList, mongodbFieldRequest.getTag());
                exist = true;
            }
        }
        if (exist) {
            return fieldsOperationBuilder.build();
        } else {
            return null;
        }
    }

    public static AddFieldsOperation.AddFieldsOperationBuilder addFormattedDateField(
            AddFieldsOperation.AddFieldsOperationBuilder fieldsOperationBuilder, String groupType, String fieldId,
            List<String> searchNameList, String tag) {
        // 创建一个表达式，将 `fieldId` 转换为 `Date` 类型
        AggregationExpression toDateExpression =
                AggregationExpression.from(MongoExpression.create("$toDate: \"$" + fieldId + "\""));
        DateOperators.DateOperatorFactory dateOperatorFactory =
                DateOperators.dateOf(toDateExpression).withTimezone(DateOperators.Timezone.valueOf("+08:00"));
        // mongodbFieldRequest.getName();
        // 创建一个表达式，将 `Date` 类型转换为指定格式的字符串
        if (FormFieldGroupRuleEnum.YEAR_QUARTER.name().equals(groupType)) {
            fieldsOperationBuilder = fieldsOperationBuilder.addField(tag).withValueOf(toDateExpression);
            String quarterFormat =
                    "$switch: {branches: [{ case: { $lte: [{ $month: { $toDate: \"$%s\"} }, 3] }, then: 1 },{ case: { $lte: [{ $month: { $toDate: \"$%s\"} }, 6] }, then: 2 },{ case: { $lte: [{ $month: { $toDate: \"$%s\"} }, 9] }, then: 3 }], default: 4}";
            fieldsOperationBuilder =
                    fieldsOperationBuilder.addField(tag + "_year").withValue(dateOperatorFactory.year());
            fieldsOperationBuilder = fieldsOperationBuilder.addField(tag + "_quarter").withValue(
                    AggregationExpression.from(
                            MongoExpression.create(String.format(quarterFormat, fieldId, fieldId, fieldId))));
            searchNameList.add(tag + "_year");
            searchNameList.add(tag + "_quarter");
        } else if (FormFieldGroupRuleEnum.YEAR_WEEK.name().equals(groupType)) {
            fieldsOperationBuilder =
                    fieldsOperationBuilder.addField(tag + "_year").withValue(dateOperatorFactory.isoWeekYear());
            fieldsOperationBuilder =
                    fieldsOperationBuilder.addField(tag + "_week").withValueOf(dateOperatorFactory.isoWeek());
            searchNameList.add(tag + "_year");
            searchNameList.add(tag + "_week");
        } else if (FormFieldGroupRuleEnum.YEAR_MONTH.name().equals(groupType)) {
            fieldsOperationBuilder =
                    fieldsOperationBuilder.addField(tag + "_year").withValue(dateOperatorFactory.year());
            fieldsOperationBuilder =
                    fieldsOperationBuilder.addField(tag + "_month").withValue(dateOperatorFactory.month());
            searchNameList.add(tag + "_year");
            searchNameList.add(tag + "_month");
        } else if (FormFieldGroupRuleEnum.YEAR.name().equals(groupType)) {
            fieldsOperationBuilder =
                    fieldsOperationBuilder.addField(tag + "_year").withValue(dateOperatorFactory.year());
            searchNameList.add(tag + "_year");
        } else {
            fieldsOperationBuilder =
                    fieldsOperationBuilder.addField(tag + "_year").withValueOf(dateOperatorFactory.year());
            fieldsOperationBuilder =
                    fieldsOperationBuilder.addField(tag + "_month").withValue(dateOperatorFactory.month());
            fieldsOperationBuilder =
                    fieldsOperationBuilder.addField(tag + "_day").withValueOf(dateOperatorFactory.dayOfMonth());
            searchNameList.add(tag + "_year");
            searchNameList.add(tag + "_month");
            searchNameList.add(tag + "_day");
        }
        return fieldsOperationBuilder;
    }


    /**
     * 用于本地比对的时候获取value 或 后端自己获取搜索value
     *
     * @param instValue
     * @return
     */
    public static List<Object> getJsonValue(String fieldId, String subForm, String fieldType, JSONObject instValue) {
        if (StringUtils.isNotEmpty(subForm)) {
            List<Object> valueList = new ArrayList<>();
            JSONArray jsonArray = JsonObjectUtils.getJsonArray(instValue, subForm);
            for (int i = 0; i < jsonArray.size(); i++) {
                valueList.add(jsonArray.getJSONObject(i).get(fieldId));
            }
            return valueList;
        }
        Object originValue = instValue.get(fieldId);
        if (originValue == null) {
            return new ArrayList<>();
        }
        if (FormFieldTypeEnum.ADDRESS_SELECTION.getFieldType().equals(fieldType)) {
            FormAddress formAddress = JSONObject.parseObject(JSONObject.toJSONString(originValue), FormAddress.class);
            String value = formAddress.getFullAddress();
            return Collections.singletonList(value);
        }
        if (FormFieldTypeEnum.arrayFieldType().contains(fieldType)) {
            return JSONArray.parseArray(JSONObject.toJSONString(originValue), Object.class);
        }
        if (originValue instanceof ArrayList) {
            return JSONArray.parseArray(JSONObject.toJSONString(originValue), Object.class);
        } else {
            return Collections.singletonList(originValue);
        }
    }

    public static List<MongodbSearchCondition> buildConditionList(List<MongoFieldRelate> relates,
                                                                  Map<Long, DataStreamCalculateVO> nodeIdMap,
                                                                  Map<String, FormConfigEncryptKey> encryptKeyMap) {
        List<MongodbSearchCondition> mongodbSearchConditions = new ArrayList<>();
        for (MongoFieldRelate mongoFieldRelate : relates) {
            MongodbSearchCondition mongodbSearchCondition =
                    AbstractMongoDbConverter.INSTANCE.toCondition(mongoFieldRelate);
            mongodbSearchCondition.setType(mongoFieldRelate.getFieldType());
            List<Object> jsonValue = getJsonValue(nodeIdMap, encryptKeyMap, mongoFieldRelate);
            mongodbSearchCondition.setValue(jsonValue);
            mongodbSearchConditions.add(mongodbSearchCondition);
        }
        return mongodbSearchConditions;
    }

    public static List<MongodbSearchCondition> buildConditionByJson(List<MongoFieldRelate> relates,
                                                                    JSONObject jsonObject,
                                                                    Map<String, FormConfigEncryptKey> encryptKeyMap) {
        List<MongodbSearchCondition> mongodbSearchConditions = new ArrayList<>();
        for (MongoFieldRelate mongoFieldRelate : relates) {
            MongodbSearchCondition mongodbSearchCondition =
                    AbstractMongoDbConverter.INSTANCE.toCondition(mongoFieldRelate);
            mongodbSearchCondition.setType(mongoFieldRelate.getFieldType());
            if (StringUtils.isEmpty(mongoFieldRelate.getMode())) {
                mongoFieldRelate.setMode(mongoFieldRelate.getQuoteType());
            }
            List<Object> jsonValue = getJsonValue(jsonObject, encryptKeyMap, mongoFieldRelate);
            mongodbSearchCondition.setValue(jsonValue);
            mongodbSearchConditions.add(mongodbSearchCondition);
        }
        return mongodbSearchConditions;
    }

    public static List<Object> getJsonValue(Map<Long, DataStreamCalculateVO> nodeIdMap,
                                            Map<String, FormConfigEncryptKey> encryptKeyMap,
                                            MongoFieldRelate mongoFieldRelate) {
        if ("EMPTY".equals(mongoFieldRelate.getMode())) {
            return Collections.singletonList("");
        } else if ("CUSTOM".equals(mongoFieldRelate.getMode())) {
            return aesValue(encryptKeyMap, mongoFieldRelate.dealValue(), mongoFieldRelate.getFieldId());
        } else {
            DataStreamQuoteField quoteField = mongoFieldRelate.getQuoteField();
            if (quoteField != null) {
                return getJsonValueByQuote(nodeIdMap, quoteField);
            }
        }
        return null;
    }

    public static List<Object> getJsonValue(JSONObject jsonObject, Map<String, FormConfigEncryptKey> encryptKeyMap,
                                            MongoFieldRelate mongoFieldRelate) {
        if ("EMPTY".equals(mongoFieldRelate.getMode())) {
            return Collections.singletonList("");
        } else if ("CUSTOM".equals(mongoFieldRelate.getMode())) {
            return aesValue(encryptKeyMap, mongoFieldRelate.dealValue(), mongoFieldRelate.getFieldId());
        } else {
            DataStreamQuoteField quoteField = mongoFieldRelate.getQuoteField();
            if (quoteField != null) {
                return MongoSearchUtils.getJsonValue(quoteField.getQuoteFieldId(), quoteField.getQuoteSubForm(),
                        quoteField.getQuoteFieldType(), jsonObject);
            }
        }
        return null;
    }

    public static List<Object> getJsonValueByQuote(Map<Long, DataStreamCalculateVO> nodeIdMap,
                                                   DataStreamQuoteField quoteField) {
        DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(quoteField.getNodeId());
        if ("calculate".equals(dataStreamCalculateVO.getNodeType())) {
            Object value = dataStreamCalculateVO.getValue();
            if (value == null) {
                return new ArrayList<>();
            }
            if (value instanceof ArrayList) {
                return JSONArray.parseArray(JSONObject.toJSONString(value), Object.class);
            } else {
                return Collections.singletonList(value);
            }
        } else {
            return MongoSearchUtils.getJsonValue(quoteField.getQuoteFieldId(), quoteField.getQuoteSubForm(),
                    quoteField.getQuoteFieldType(), dataStreamCalculateVO.getJsonValue());
        }
    }

    private static List<Object> aesValue(Map<String, FormConfigEncryptKey> encryptKeyMap, List<Object> objectList,
                                         String fieldId) {
        if (encryptKeyMap != null) {
            FormConfigEncryptKey formConfigEncryptKey = encryptKeyMap.get(fieldId);
            if (formConfigEncryptKey != null) {
                List<Object> valueObject = new ArrayList<>();
                for (Object object : objectList) {
                    if (object != null) {
                        valueObject.add(AESUtils.encryptData(object.toString(), UserUtils.getUser().getCompanyUuid()));
                    }
                }
                return valueObject;
            } else {
                return objectList;
            }
        } else {
            return objectList;
        }
    }

    public static MongodbSearchFilter toFilter(DataStreamConditionRel rel, Map<Long, DataStreamCalculateVO> nodeIdMap,
                                               Map<String, FormConfigEncryptKey> encryptKeyMap) {
        List<MongodbSearchCondition> mongodbSearchConditions =
                MongoSearchUtils.buildConditionList(rel.getRelates(), nodeIdMap, encryptKeyMap);
        MongodbSearchFilter mongodbSearchFilter = new MongodbSearchFilter();
        mongodbSearchFilter.setRel(rel.getRel());
        mongodbSearchFilter.setConditionList(mongodbSearchConditions);
        return mongodbSearchFilter;
    }

    public static MongodbSearchFilter toFilter(DataStreamConditionRel rel, JSONObject jsonObject,
                                               Map<String, FormConfigEncryptKey> encryptKeyMap) {
        List<MongodbSearchCondition> mongodbSearchConditions =
                MongoSearchUtils.buildConditionByJson(rel.getRelates(), jsonObject, encryptKeyMap);
        MongodbSearchFilter mongodbSearchFilter = new MongodbSearchFilter();
        mongodbSearchFilter.setRel(rel.getRel());
        mongodbSearchFilter.setConditionList(mongodbSearchConditions);
        return mongodbSearchFilter;
    }

    public static void buildSort(Query query, List<MongoSort> sorts) {
        for (MongoSort mongoSort : sorts) {
            Sort.Direction sort = org.springframework.data.domain.Sort.Direction.DESC;
            if (mongoSort.getSortType().equals("ASC")) {
                sort = Sort.Direction.ASC;
            }
            String fieldId = MongoSearchUtils.getFieldId(mongoSort.getFieldId(), mongoSort.getFieldType());
            query.with(Sort.by(sort, fieldId));
        }
    }

    public static List<Sort.Order> buildSortAgg(List<MongoSort> sorts, Map<String, MongodbSearchField> tagMap) {
        List<Sort.Order> orders = new ArrayList<>();
        for (MongoSort mongoSort : sorts) {
            MongodbSearchField mongodbSearchField = tagMap.get(mongoSort.getTag());
            sort(mongodbSearchField, mongoSort.getSortType(), orders);
        }
        return orders;
    }

    public static List<Sort.Order> buildSortAgg(List<MongoSort> sorts) {
        List<Sort.Order> orders = new ArrayList<>();
        for (MongoSort mongoSort : sorts) {
            String fieldId = MongoSearchUtils.getFieldId(mongoSort.getFieldId(), mongoSort.getFieldType());
            if (mongoSort.getSortType().equals("ASC")) {
                orders.add(Sort.Order.asc(fieldId));
            } else {
                orders.add(Sort.Order.desc(fieldId));
            }
        }
        return orders;
    }

    public static void addSortAgg(List<MongoSort> sorts, List<AggregationOperation> aggregationList) {
        if (CollectionUtils.isNotEmpty(sorts)) {
            List<Sort.Order> orders = MongoSearchUtils.buildSortAgg(sorts);
            SortOperation sort = Aggregation.sort(Sort.by(orders));
            aggregationList.add(sort);
        }
    }

    public static void addSortAggGroup(List<MongoSort> sorts, List<AggregationOperation> aggregationList, Integer size) {
        if (CollectionUtils.isNotEmpty(sorts)) {
            List<Sort.Order> orders = new ArrayList<>();
            if (size == 1) {
                for (MongoSort mongoSort : sorts) {
                    String fieldId = "_id";
                    if (mongoSort.getSortType().equals("ASC")) {
                        orders.add(Sort.Order.asc(fieldId));
                    } else {
                        orders.add(Sort.Order.desc(fieldId));
                    }
                }
            } else {
                for (MongoSort mongoSort : sorts) {
                    String fieldId = "_id." + mongoSort.getTag();
                    if (mongoSort.getSortType().equals("ASC")) {
                        orders.add(Sort.Order.asc(fieldId));
                    } else {
                        orders.add(Sort.Order.desc(fieldId));
                    }
                }
            }
            SortOperation sort = Aggregation.sort(Sort.by(orders));
            aggregationList.add(sort);
        }
    }

    public static void sort(MongodbSearchField mongodbSearchField, String sortType, List<Sort.Order> orders) {
        String fieldType = mongodbSearchField.getType();
        String fieldId = mongodbSearchField.getName();
        String tag = mongodbSearchField.getTag();
        if (FormFieldTypeEnum.INPUT_DATE.getFieldType().equals(fieldType) ||
                FormSystemFieldEnum.CREATE_TIME.getName().equals(fieldId) ||
                FormSystemFieldEnum.UPDATE_TIME.getName().equals(fieldId)) {
            sortList(mongodbSearchField.getGroupType(), mongodbSearchField.getTag(), sortType, orders);
        } else if (FormFieldTypeEnum.ADDRESS_SELECTION.getFieldType().equals(fieldType)) {
            addressSortList(mongodbSearchField.getGroupType(), mongodbSearchField.getTag(), sortType, orders);
        } else {
            if (sortType.equals("ASC")) {
                orders.add(Sort.Order.asc(tag));
            } else {
                orders.add(Sort.Order.desc(tag));
            }
        }
    }

    private static void addressSortList(String groupType, String tag, String sortType, List<Sort.Order> orders) {
        if ("ASC".equals(sortType)) {
            if (MongodbAggregateAddressGroupTypeEnum.province.name().equals(groupType)) {
                orders.add(Sort.Order.asc(tag + "_province"));
            } else if (MongodbAggregateAddressGroupTypeEnum.province_city.name().equals(groupType)) {
                orders.add(Sort.Order.asc(tag + "_province"));
                orders.add(Sort.Order.asc(tag + "_city"));
            } else if (MongodbAggregateAddressGroupTypeEnum.province_city_district.name().equals(groupType)) {
                orders.add(Sort.Order.asc(tag + "_province"));
                orders.add(Sort.Order.asc(tag + "_city"));
                orders.add(Sort.Order.asc(tag + "_district"));
            }
        } else {
            if (MongodbAggregateAddressGroupTypeEnum.province.name().equals(groupType)) {
                orders.add(Sort.Order.desc(tag + "_province"));
            } else if (MongodbAggregateAddressGroupTypeEnum.province_city.name().equals(groupType)) {
                orders.add(Sort.Order.desc(tag + "_province"));
                orders.add(Sort.Order.desc(tag + "_city"));
            } else if (MongodbAggregateAddressGroupTypeEnum.province_city_district.name().equals(groupType)) {
                orders.add(Sort.Order.desc(tag + "_province"));
                orders.add(Sort.Order.desc(tag + "_city"));
                orders.add(Sort.Order.desc(tag + "_district"));
            }
        }
    }

    public static void sortList(String groupType, String tag, String sortType, List<Sort.Order> orders) {
        if ("ASC".equals(sortType)) {
            if (FormFieldGroupRuleEnum.YEAR_QUARTER.name().equals(groupType)) {
                orders.add(Sort.Order.asc(tag + "_year"));
                orders.add(Sort.Order.asc(tag + "_quarter"));
            } else if (FormFieldGroupRuleEnum.YEAR_WEEK.name().equals(groupType)) {
                orders.add(Sort.Order.asc(tag + "_year"));
                orders.add(Sort.Order.asc(tag + "_week"));
            } else if (FormFieldGroupRuleEnum.YEAR_MONTH.name().equals(groupType)) {
                orders.add(Sort.Order.asc(tag + "_year"));
                orders.add(Sort.Order.asc(tag + "_month"));
            } else if (FormFieldGroupRuleEnum.YEAR.name().equals(groupType)) {
                orders.add(Sort.Order.asc(tag + "_year"));
            } else {
                orders.add(Sort.Order.asc(tag + "_year"));
                orders.add(Sort.Order.asc(tag + "_month"));
                orders.add(Sort.Order.asc(tag + "_day"));
            }
        } else {
            if (FormFieldGroupRuleEnum.YEAR_QUARTER.name().equals(groupType)) {
                orders.add(Sort.Order.desc(tag + "_year"));
                orders.add(Sort.Order.desc(tag + "_quarter"));
            } else if (FormFieldGroupRuleEnum.YEAR_WEEK.name().equals(groupType)) {
                orders.add(Sort.Order.desc(tag + "_year"));
                orders.add(Sort.Order.desc(tag + "_week"));
            } else if (FormFieldGroupRuleEnum.YEAR_MONTH.name().equals(groupType)) {
                orders.add(Sort.Order.desc(tag + "_year"));
                orders.add(Sort.Order.desc(tag + "_month"));
            } else if (FormFieldGroupRuleEnum.YEAR.name().equals(groupType)) {
                orders.add(Sort.Order.desc(tag + "_year"));
            } else {
                orders.add(Sort.Order.desc(tag + "_year"));
                orders.add(Sort.Order.desc(tag + "_month"));
                orders.add(Sort.Order.desc(tag + "_day"));
            }
        }
    }

    public static Fields fieldToFields(List<Field> fields) {
        Fields from = Fields.from(fields.get(0));
        for (int i = 1; i < fields.size(); i++) {
            from = from.and(fields.get(i));
        }
        return from;
    }

    public static void aggregationAddLimit(List<AggregationOperation> aggregationList, Integer pageSize,
                                           Integer offSet) {
        LimitOperation limitOperation = Aggregation.limit(pageSize + offSet);
        aggregationList.add(limitOperation);
        SkipOperation skip = Aggregation.skip(Long.valueOf(offSet));
        aggregationList.add(skip);
    }

    public static String[] buildQueryField(List<FormPrivilegeFieldConfig> formPrivilegeFieldConfigs,
                                           List<FormConfigCommon> fields) {
        List<String> fieldList = new ArrayList<>();
        Map<String, FormPrivilegeFieldConfig> fieldConfigMap =
                formPrivilegeFieldConfigs.stream().collect(Collectors.toMap(FormPrivilegeFieldConfig::getName, c -> c));
        for (FormConfigCommon formConfigCommon : fields) {
            FormPrivilegeFieldConfig formPrivilegeFieldConfig = fieldConfigMap.get(formConfigCommon.getName());
            if (FormFieldTypeEnum.SUB_FORM_TYPE.getFieldType().equals(formConfigCommon.getType())) {
                for (FormConfigCommon subForm : formConfigCommon.getColumns()) {
                    FormPrivilegeFieldConfig subFieldConfig = fieldConfigMap.get(subForm.getName());
                    if (subFieldConfig == null) {
                        continue;
                    }
                    String fieldId = getFieldId(formConfigCommon.getName(), "") + "." + subFieldConfig.getName();
                    fieldList.add(fieldId);
                }
                String fieldId = getFieldId(formConfigCommon.getName(), "") + ".id";
                fieldList.add(fieldId);
            } else {
                if (formPrivilegeFieldConfig == null) {
                    continue;
                }
                if (!formPrivilegeFieldConfig.getVisibleFlag()) {
                    continue;
                }
                if ("system".equals(formPrivilegeFieldConfig.getType())) {
                    continue;
                } else {
                    String fieldId = getFieldId(formPrivilegeFieldConfig.getName(), "");
                    fieldList.add(fieldId);
                }
            }
        }
        for (FormSystemFieldEnum formSystemFieldEnum : FormSystemFieldEnum.values()) {
            if (!fieldList.contains(formSystemFieldEnum.getName())) {
                fieldList.add(formSystemFieldEnum.getName());
            }
        }
        String[] strings = new String[fieldList.size()];
        return fieldList.toArray(strings);
    }

    public static void addLimit(List<AggregationOperation> aggregationList, Integer limit, Integer offSet) {
        LimitOperation limitOperation = Aggregation.limit(limit);
        aggregationList.add(limitOperation);
        SkipOperation skip = Aggregation.skip(Long.valueOf(offSet));
        aggregationList.add(skip);
    }

    public static String[] searchField(List<FormFieldRequest> formFields, Boolean contentSystem) {
        List<String> fields = new ArrayList<>();
        if (contentSystem) {
            fields.add("uuid");
            fields.add("formId");
            fields.add("applicationId");
        }
        for (FormFieldRequest formFieldRequest : formFields) {
            if ("system".equals(formFieldRequest.getFieldType())) {
                fields.add(formFieldRequest.getFieldId());
            } else {
                if (StringUtils.isEmpty(formFieldRequest.getSubForm())) {
                    fields.add("instValue." + formFieldRequest.getFieldId());
                } else {
                    fields.add("instValue." + formFieldRequest.getSubForm() + "." + formFieldRequest.getFieldId());
                }
            }
        }
        return fields.toArray(new String[0]);
    }

    public static AggregationOperation summaryProjectField(Map<String, List<String>> fieldsMap) {
        Document fieldDocument = new Document();
        fieldsMap.forEach((key, list) -> {
            if (StringUtils.isEmpty(key)) {
                for (String value : list) {
                    String fieldId = getFieldId(value, "");
                    fieldDocument.append(fieldId, MongoFunctionUtils.ifNull("$" + fieldId));
                }
            } else {
                Document document = new Document();
                for (String value : list) {
                    document.append(value, MongoFunctionUtils.ifNull("$$" + key + "." + value));
                }
                MapOperation mapOperation = new MapOperation(getFieldId(key, ""), key, document);
                fieldDocument.append(getFieldId(key, ""), mapOperation.toDocument(Aggregation.DEFAULT_CONTEXT));
            }
        });
        return new ProjectAggregation(fieldDocument);
    }

    public static String getField(String fieldId, String subForm, String fieldType) {
        if ("system".equals(fieldType)) {
            return fieldId;
        }
        if (StringUtils.isNotEmpty(subForm)) {
            return "instValue." + subForm + "." + fieldId;
        } else {
            return "instValue." + fieldId;
        }
    }

    public static Object qlExpressValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            if (value.toString().contains("instValue.") || value.toString().contains("function_") ||
                    value.toString().contains("field_")) {
                return "$" + value;
            }
        }
        return value;
    }

    public static void addMatch(List<Criteria> criteriaList, List<AggregationOperation> aggregationList) {
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            Criteria search = new Criteria();
            search.andOperator(criteriaList);
            MatchOperation match = Aggregation.match(search);
            aggregationList.add(match);
        }
    }
}
