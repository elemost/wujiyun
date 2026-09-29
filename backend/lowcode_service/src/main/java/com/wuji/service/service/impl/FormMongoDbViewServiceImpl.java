package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.TimeUtils;
import com.wuji.service.converter.AbstractFormMongoDbConverter;
import com.wuji.service.model.domain.FormPrivilegeDataScopeDomain;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormCalendarViewConfig;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormPrivilegeFieldConfig;
import com.wuji.service.model.info.MongoSort;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.FormViewCalendarRequest;
import com.wuji.service.model.request.FormViewFieldGroupRequest;
import com.wuji.service.model.request.FormViewLevelRequest;
import com.wuji.service.model.request.FormViewMongoDbRequest;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.model.vo.FormFieldGroupInfoVO;
import com.wuji.service.model.vo.FormFieldGroupVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.model.vo.SearchFilterVO;
import com.wuji.service.service.FormMongoDbViewService;
import com.wuji.service.service.FormPrivilegeService;
import com.wuji.service.service.FormService;
import com.wuji.service.utils.FormPrivilegeUtils;
import com.wuji.service.utils.FormViewUtils;
import com.wuji.service.utils.MongoSearchUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormMongoDbViewServiceImpl extends FormMongoDbCommonServiceImpl implements FormMongoDbViewService {

    @Autowired
    private FormService formService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @Override
    public QueryPageVO<LowcodeDataVO> queryListView(FormViewMongoDbRequest formViewMongoDbRequest) {
        FormVO viewForm =
                formService.info(formViewMongoDbRequest.getFormId(), formViewMongoDbRequest.getApplicationId());
        FormVO info = formService.info(viewForm.getSourceId(), formViewMongoDbRequest.getApplicationId());
        Query query = new Query();
        FormSearchDataRequest formSearchDataRequest =
                AbstractFormMongoDbConverter.INSTANCE.toRequest(formViewMongoDbRequest);
        formSearchDataRequest.setFormId(info.getId());
        JSONObject jsonObject = JSONObject.parseObject(viewForm.getConfig());
        MongodbSearchFilter filter = JSONObject.parseObject(jsonObject.getString("filter"), MongodbSearchFilter.class);
        formSearchDataRequest.setViewFilter(filter);
        buildSearchFilter(formSearchDataRequest, null, query);
        long count = mongoTemplate.count(query, info.getTableName());
        query.limit(formViewMongoDbRequest.getPageSize());
        query.skip((long) (formViewMongoDbRequest.getPageNum() - 1) * formViewMongoDbRequest.getPageSize());
        if (CollectionUtils.isEmpty(formViewMongoDbRequest.getSorts())) {
            query.with(Sort.by(Sort.Direction.DESC, "createTime"));
        } else {
            MongoSearchUtils.buildSort(query, formViewMongoDbRequest.getSorts());
        }
        List<FormPrivilegeFieldConfig> formPrivilegeFieldConfigs =
                FormPrivilegeUtils.getFormPrivilegeFieldConfigs(null, viewForm);
        if (CollectionUtils.isNotEmpty(formPrivilegeFieldConfigs)) {
            queryField(info, query, formPrivilegeFieldConfigs);
        }
        // 执行查找到的匹配的全部文档信息
        List<LowcodeDataDomain> lowcodeInsertDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        List<LowcodeDataVO> lowcodeDataList =
                getLowcodeDataVOS(info, lowcodeInsertDataDomains, null, Boolean.TRUE, false);
        return new QueryPageVO<>(formViewMongoDbRequest.getPageNum(), formViewMongoDbRequest.getPageSize(), (int) count,
                lowcodeDataList);
    }

    @Override
    public QueryPageVO<LowcodeDataVO> queryListViewPrivilege(FormViewMongoDbRequest formViewMongoDbRequest) {
        FormPrivilegeVO formPrivilegeVO = formPrivilegeService.detail(formViewMongoDbRequest.getGroupId());
        List<FormPrivilegeVO> formPrivilegeList = Collections.singletonList(formPrivilegeVO);
        // 获取数据范围
        List<FormPrivilegeDataScopeDomain> formPrivilegeDataScopeDomainList =
                FormPrivilegeUtils.getDataScope(formPrivilegeList);
        if (CollectionUtils.isEmpty(formPrivilegeDataScopeDomainList)) {
            return new QueryPageVO<>(formViewMongoDbRequest.getPageNum(), formViewMongoDbRequest.getPageSize(), 0,
                    new ArrayList<>());
        }
        filterCheckAndSearchValue(formViewMongoDbRequest.getFilter(), formViewMongoDbRequest.getApplicationId(),
                formViewMongoDbRequest.getFormId());
        FormVO viewForm =
                formService.info(formViewMongoDbRequest.getFormId(), formViewMongoDbRequest.getApplicationId());
        FormVO info = formService.info(viewForm.getSourceId(), formViewMongoDbRequest.getApplicationId());
        Query query = new Query();
        FormSearchDataRequest formSearchDataRequest =
                AbstractFormMongoDbConverter.INSTANCE.toRequest(formViewMongoDbRequest);
        JSONObject jsonObject = JSONObject.parseObject(viewForm.getConfig());
        MongodbSearchFilter filter = JSONObject.parseObject(jsonObject.getString("filter"), MongodbSearchFilter.class);
        formSearchDataRequest.setViewFilter(filter);
        formSearchDataRequest.setFormId(info.getId());
        buildSearchFilter(formSearchDataRequest, formPrivilegeDataScopeDomainList, query);
        long count = mongoTemplate.count(query, info.getTableName());
        query.limit(formViewMongoDbRequest.getPageSize());
        query.skip((long) (formViewMongoDbRequest.getPageNum() - 1) * formViewMongoDbRequest.getPageSize());
        if (CollectionUtils.isEmpty(formViewMongoDbRequest.getSorts())) {
            query.with(Sort.by(Sort.Direction.DESC, "createTime"));
        } else {
            MongoSearchUtils.buildSort(query, formViewMongoDbRequest.getSorts());
        }
        List<FormPrivilegeFieldConfig> formPrivilegeFieldConfigs =
                FormPrivilegeUtils.getFormPrivilegeFieldConfigs(formPrivilegeVO, viewForm);
        if (CollectionUtils.isNotEmpty(formPrivilegeFieldConfigs)) {
            queryField(info, query, formPrivilegeFieldConfigs);
        }
        // 执行查找到的匹配的全部文档信息
        List<LowcodeDataDomain> lowcodeInsertDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        List<LowcodeDataVO> lowcodeDataList =
                getLowcodeDataVOS(viewForm, lowcodeInsertDataDomains, formPrivilegeList, Boolean.TRUE, false);
        return new QueryPageVO<>(formViewMongoDbRequest.getPageNum(), formViewMongoDbRequest.getPageSize(), (int) count,
                lowcodeDataList);
    }

    @Override
    public List<Long> monthlyData(FormViewCalendarRequest formViewCalendarRequest) {
        FormVO viewForm =
                formService.info(formViewCalendarRequest.getFormId(), formViewCalendarRequest.getApplicationId());
        FormCalendarViewConfig formCalendarViewConfig =
                JSONObject.parseObject(viewForm.getConfig(), FormCalendarViewConfig.class);
        FormVO info = formService.info(viewForm.getSourceId(), formViewCalendarRequest.getApplicationId());
        Query query = new Query();
        FormSearchDataRequest formSearchDataRequest =
                AbstractFormMongoDbConverter.INSTANCE.toRequest(formViewCalendarRequest);
        formSearchDataRequest.setFormId(info.getId());
        List<Criteria> criteriaList = new ArrayList<>();
        SearchFilterVO searchFilterVO = buildSearchFilter(formSearchDataRequest);
        criteriaList.add(searchFilterVO.getSearch());
        FormViewUtils.calendarFilter(formCalendarViewConfig, formViewCalendarRequest, criteriaList);
        query.addCriteria(new Criteria().andOperator(criteriaList));
        query.with(Sort.by(Sort.Direction.ASC, MongoSearchUtils.getFieldId(formCalendarViewConfig.getStartDateField(),
                formCalendarViewConfig.getFieldType())));
        List<LowcodeDataDomain> lowcodeInsertDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        List<Long> matchDateList = new ArrayList<>();
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeInsertDataDomains) {
            JSONObject instValue = lowcodeDataDomain.getInstValue();
            Long startTime = instValue.getLong(formCalendarViewConfig.getStartDateField());
            Long endTime = instValue.getLong(formCalendarViewConfig.getEndDateField());
            List<Long> midnightTimestamps = TimeUtils.getMidnightTimestamps(startTime, endTime);
            matchDateList.addAll(midnightTimestamps);
        }
        return matchDateList.stream().distinct().sorted().collect(Collectors.toList());
    }

    @Override
    public List<LowcodeDataVO> queryDataByTime(FormViewCalendarRequest formViewCalendarRequest) {
        FormVO viewForm =
                formService.info(formViewCalendarRequest.getFormId(), formViewCalendarRequest.getApplicationId());
        FormCalendarViewConfig formCalendarViewConfig =
                JSONObject.parseObject(viewForm.getConfig(), FormCalendarViewConfig.class);
        FormVO info = formService.info(viewForm.getSourceId(), formViewCalendarRequest.getApplicationId());
        Query query = new Query();
        FormSearchDataRequest formSearchDataRequest =
                AbstractFormMongoDbConverter.INSTANCE.toRequest(formViewCalendarRequest);
        formSearchDataRequest.setFormId(info.getId());
        List<Criteria> criteriaList = new ArrayList<>();
        SearchFilterVO searchFilterVO = buildSearchFilter(formSearchDataRequest);
        if (searchFilterVO.getSearch() != null) {
            criteriaList.add(searchFilterVO.getSearch());
        }
        FormViewUtils.calendarFilter(formCalendarViewConfig, formViewCalendarRequest, criteriaList);
        query.addCriteria(new Criteria().andOperator(criteriaList));
        query.with(Sort.by(Sort.Direction.ASC, MongoSearchUtils.getFieldId(formCalendarViewConfig.getStartDateField(),
                formCalendarViewConfig.getFieldType())));
        List<LowcodeDataDomain> lowcodeInsertDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        return getLowcodeDataVOS(info, lowcodeInsertDataDomains, searchFilterVO.getFormPrivilegeList(), Boolean.FALSE,
                false);
    }

    @Override
    public FormFieldGroupVO fieldGroup(FormViewFieldGroupRequest formViewFieldGroupRequest) {
        FormVO info =
                formService.info(formViewFieldGroupRequest.getFormId(), formViewFieldGroupRequest.getApplicationId());
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        List<Criteria> criteriaList = new ArrayList<>();
        List<MongodbSearchField> groupFields = formViewFieldGroupRequest.getGroupFields();
        for (MongodbSearchField mongodbSearchField : groupFields) {
            String fieldId = MongoSearchUtils.getFieldId(mongodbSearchField.getName(), mongodbSearchField.getType());
            criteriaList.add(Criteria.where(fieldId).ne(null));
        }
        FormSearchDataRequest formSearchDataRequest =
                AbstractFormMongoDbConverter.INSTANCE.toRequest(formViewFieldGroupRequest);
        formSearchDataRequest.setFormId(info.getId());
        SearchFilterVO searchFilterVO = buildSearchFilter(formSearchDataRequest);
        if (searchFilterVO.getSearch() != null) {
            criteriaList.add(searchFilterVO.getSearch());
        }
        MongoSearchUtils.addMatch(criteriaList, aggregationOperations);
        MongoSearchUtils.aggregateUnwind(formViewFieldGroupRequest.getMetricList(), groupFields, aggregationOperations);
        List<Field> fields = new ArrayList<>();
        MongoSearchUtils.buildFieldListGroup(fields, groupFields);
        MongoSearchUtils.coverAddress(groupFields, fields);
        Fields from = MongoSearchUtils.fieldToFields(fields);
        GroupOperation group = Aggregation.group(from);
        if (CollectionUtils.isNotEmpty(formViewFieldGroupRequest.getMetricList())) {
            group = MongoSearchUtils.calculate(group, formViewFieldGroupRequest.getMetricList(), true);
        }
        group = group.push("$$ROOT").as("docs");
        aggregationOperations.add(group);
        List<MongoSort> mongoSorts = groupFields.stream().map(AbstractFormMongoDbConverter.INSTANCE::toMongoSort)
                .collect(Collectors.toList());
        mongoSorts =
                mongoSorts.stream().filter(c -> StringUtils.isNotEmpty(c.getSortType())).collect(Collectors.toList());
        MongoSearchUtils.addSortAggGroup(mongoSorts, aggregationOperations, fields.size());
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations).withOptions(
                AggregationOptions.builder().collation(Collation.of("zh").strength(Collation.ComparisonLevel.primary()))
                        .build());
        List<JSONObject> mappedResults =
                mongoTemplate.aggregate(aggregation, info.getTableName(), JSONObject.class).getMappedResults();
        FormFieldGroupInfoVO formFieldGroupInfoVO;
        List<LowcodeDataDomain> lowcodeInsertDataDomains = new ArrayList<>();
        if (fields.size() == 1) {
            for (JSONObject jsonObject : mappedResults) {
                JSONObject map = new JSONObject();
                map.put(groupFields.get(0).getTag(), jsonObject.get("_id"));
                jsonObject.put("_id", map);
                List<LowcodeDataDomain> lowcodeDataDomains = getLowcodeDataDomains(jsonObject, groupFields);
                lowcodeInsertDataDomains.addAll(lowcodeDataDomains);
            }
        } else {
            for (JSONObject jsonObject : mappedResults) {
                List<LowcodeDataDomain> lowcodeDataDomains = getLowcodeDataDomains(jsonObject, groupFields);
                lowcodeInsertDataDomains.addAll(lowcodeDataDomains);
            }
        }
        FieldExistNameVO fieldExistNameVO =
                formService.getAllFormConfigCommonList(info.getId(), Boolean.TRUE, info.getApplicationId(),
                        Boolean.TRUE);
        Map<String, FormConfigCommon> nameToMap =
                fieldExistNameVO.getFields().stream().collect(Collectors.toMap(FormConfigCommon::getName, c -> c));
        formFieldGroupInfoVO = FormViewUtils.buildDynamicGroup(mappedResults, groupFields, 0,
                formViewFieldGroupRequest.getMetricList(), nameToMap);
        List<LowcodeDataVO> lowcodeDataVOS =
                getLowcodeDataVOS(info, lowcodeInsertDataDomains, searchFilterVO.getFormPrivilegeList(), Boolean.TRUE,
                        Boolean.FALSE);
        FormFieldGroupVO formFieldGroupVO = new FormFieldGroupVO();
        formFieldGroupVO.setGroupInfos(formFieldGroupInfoVO.getSubGroups());
        formFieldGroupVO.setDatas(lowcodeDataVOS);
        return formFieldGroupVO;
    }

    private static List<LowcodeDataDomain> getLowcodeDataDomains(JSONObject jsonObject,
                                                                 List<MongodbSearchField> groupFields) {
        List<LowcodeDataDomain> lowcodeDataDomains =
                JSONArray.parseArray(jsonObject.getJSONArray("docs").toJSONString(), LowcodeDataDomain.class);
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataDomains) {
            for (MongodbSearchField mongodbSearchField : groupFields) {
                if (mongodbSearchField.getType().equals(FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE.getFieldType()) ||
                        mongodbSearchField.getType().equals(FormFieldTypeEnum.FORM_INPUT_USER_SINGLE.getFieldType())) {
                    JSONObject object = lowcodeDataDomain.getInstValue().getJSONObject(mongodbSearchField.getName());
                    lowcodeDataDomain.getInstValue()
                            .put(mongodbSearchField.getName(), Collections.singletonList(object));

                }
            }
        }
        return lowcodeDataDomains;
    }

    @Override
    public List<LowcodeDataVO> level(FormViewLevelRequest formViewLevelRequest) {
        FormPrivilegeVO formPrivilegeVO = formPrivilegeService.detail(formViewLevelRequest.getGroupId());
        List<FormPrivilegeVO> formPrivilegeList = Collections.singletonList(formPrivilegeVO);
        // 获取数据范围
        List<FormPrivilegeDataScopeDomain> formPrivilegeDataScopeDomainList =
                FormPrivilegeUtils.getDataScope(formPrivilegeList);
        if (CollectionUtils.isEmpty(formPrivilegeDataScopeDomainList)) {
            return new ArrayList<>();
        }
        filterCheckAndSearchValue(formViewLevelRequest.getFilter(), formViewLevelRequest.getApplicationId(),
                formViewLevelRequest.getFormId());
        FormVO viewForm = formService.info(formViewLevelRequest.getFormId(), formViewLevelRequest.getApplicationId());
        FormVO info = viewForm;
        if (StringUtils.isNotEmpty(viewForm.getSourceId())) {
            info = formService.info(viewForm.getSourceId(), formViewLevelRequest.getApplicationId());
        }

        Query query = new Query();
        FormSearchDataRequest formSearchDataRequest =
                AbstractFormMongoDbConverter.INSTANCE.toRequest(formViewLevelRequest);
        JSONObject jsonObject = JSONObject.parseObject(viewForm.getConfig());
        MongodbSearchFilter filter = JSONObject.parseObject(jsonObject.getString("filter"), MongodbSearchFilter.class);
        formSearchDataRequest.setViewFilter(filter);
        formSearchDataRequest.setFormId(info.getId());
        buildSearchFilter(formSearchDataRequest, formPrivilegeDataScopeDomainList, query);
        List<LowcodeDataDomain> lowcodeInsertDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        List<LowcodeDataVO> lowcodeDataVOS =
                getLowcodeDataVOS(info, lowcodeInsertDataDomains, formPrivilegeList, Boolean.FALSE, false);
        Map<String, List<LowcodeDataVO>> lowcodeDataVOMap = new HashMap<>();
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataVOS) {
            JSONObject formSelectData =
                    lowcodeDataVO.getInstValue().getJSONObject(formViewLevelRequest.getParentFieldId());
            if (formSelectData == null) {
                List<LowcodeDataVO> lowcodeDataVOList = lowcodeDataVOMap.getOrDefault("0", new ArrayList<>());
                lowcodeDataVOList.add(lowcodeDataVO);
                lowcodeDataVOMap.put("0", lowcodeDataVOList);
            } else {
                String uuid = formSelectData.getString("uuid");
                if (StringUtils.isEmpty(uuid)) {
                    uuid = "0";
                }
                List<LowcodeDataVO> lowcodeDataList = lowcodeDataVOMap.getOrDefault(uuid, new ArrayList<>());
                lowcodeDataList.add(lowcodeDataVO);
                lowcodeDataVOMap.put(uuid, lowcodeDataList);
            }
        }
        List<LowcodeDataVO> lowcodeDataVOList = lowcodeDataVOMap.get("0");
        setChildren(lowcodeDataVOList, lowcodeDataVOMap);
        return lowcodeDataVOList;
    }

    private void setChildren(List<LowcodeDataVO> lowcodeDataVOList, Map<String, List<LowcodeDataVO>> lowcodeDataVOMap) {
        if (CollectionUtils.isEmpty(lowcodeDataVOList)) {
            return;
        }
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataVOList) {
            List<LowcodeDataVO> children = lowcodeDataVOMap.get(lowcodeDataVO.getUuid());
            lowcodeDataVO.setChildren(children);
            if (CollectionUtils.isNotEmpty(children)) {
                setChildren(children, lowcodeDataVOMap);
            }
        }
    }

}
