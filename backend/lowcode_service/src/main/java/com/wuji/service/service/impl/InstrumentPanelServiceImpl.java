package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import com.ql.util.express.DefaultContext;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.service.AdminCommonService;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.PostService;
import com.wuji.admin.service.UserService;
import com.wuji.common.api.FormDataFactoryExecuteApi;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.express.FormulaRunner;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.FormDataFactoryParamVO;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.StringUtil;
import com.wuji.service.constant.Constants;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.enums.MongodbCalculateEnum;
import com.wuji.service.enums.MongodbSearchFieldGroupTypeEnum;
import com.wuji.service.enums.MongodbWidgetPermissionEnum;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.express.MongoFormulaRunner;
import com.wuji.service.model.domain.FormPrivilegeDataScopeDomain;
import com.wuji.service.model.info.FormAggregateDate;
import com.wuji.service.model.info.FormColumn;
import com.wuji.service.model.info.MongodbAggregateData;
import com.wuji.service.model.info.MongodbAggregateFormula;
import com.wuji.service.model.info.MongodbAggregateMetricsData;
import com.wuji.service.model.info.MongodbAggregateShowSubSummary;
import com.wuji.service.model.info.MongodbAggregateShowSummary;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.MongodbWidget;
import com.wuji.service.model.info.excel.ScoreData;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import com.wuji.service.model.mongo.ProjectAggregation;
import com.wuji.service.model.request.MongodbAggregateCheckRequest;
import com.wuji.service.model.request.MongodbAggregateRequest;
import com.wuji.service.model.request.MongodbDetailedCheckRequest;
import com.wuji.service.model.request.MongodbDetailedListRequest;
import com.wuji.service.model.request.MongodbGanttRequest;
import com.wuji.service.model.vo.FormAggregateMongoVO;
import com.wuji.service.model.vo.FormColumnsResultVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.InstrumentPanelFunctionVO;
import com.wuji.service.model.vo.InstrumentPanelPivotTableVO;
import com.wuji.service.model.vo.MongodbAggregateAllVO;
import com.wuji.service.model.vo.MongodbAggregateVO;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormPrivilegeService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.InstrumentPanelService;
import com.wuji.service.utils.FormPrivilegeUtils;
import com.wuji.service.utils.JsonFunctionUtils;
import com.wuji.service.utils.MongoDataUtils;
import com.wuji.service.utils.MongoFunctionUtils;
import com.wuji.service.utils.MongoSearchUtils;
import com.wuji.service.utils.MultiLevelSummaryRowInserter;
import com.wuji.service.utils.PivotTableExportUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class InstrumentPanelServiceImpl extends FormMongoDbCommonServiceImpl implements InstrumentPanelService {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private FormService formService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FormAggregateService formAggregateService;

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private FormDataFactoryExecuteApi formDataFactoryExecuteApi;

    @Autowired
    private PostService postService;

    @Autowired
    private AdminCommonService adminCommonService;

    @Override
    public MongodbAggregateAllVO aggregate(MongodbAggregateRequest mongodbAggregateRequest) {
        MongodbWidget widget = mongodbAggregateRequest.getWidget();
        List<MongodbSearchField> allFieldList = new ArrayList<>();
        allFieldList.addAll(widget.getFieldxList());
        allFieldList.addAll(widget.getFieldyList());
        if (allFieldList.size() > 1) {
            if (allFieldList.stream().anyMatch(c -> c.getType().equals(FormFieldTypeEnum.INPUT_DATE.getFieldType()) ||
                    FormSystemFieldEnum.getTimeField().contains(c.getName()))) {
                for (MongodbSearchField field : widget.getMetricList()) {
                    if (StringUtils.isNotEmpty(field.getGrowth()) &&
                            MongodbSearchFieldGroupTypeEnum.groupTypeList().contains(field.getGrowth())) {
                        return null;
                    }
                }
            }
        }
        filterCheckAndSearchValue(mongodbAggregateRequest.getFilter(), mongodbAggregateRequest.getApplicationId(),
                mongodbAggregateRequest.getFormId());
        filterCheckAndSearchValue(widget.getFilter(), mongodbAggregateRequest.getApplicationId(),
                mongodbAggregateRequest.getFormId());
        MongodbAggregateAllVO mongodbAggregateAllVO = getAggregateData(mongodbAggregateRequest);
        List<JSONObject> mappedResults = mongodbAggregateAllVO.getMappedResults();
        MongodbAggregateVO mongodbAggregateVO = new MongodbAggregateVO();
        mongodbAggregateVO.setDataSize(mappedResults.size());
        mongodbAggregateAllVO.setMongodbAggregateVO(mongodbAggregateVO);
        mongodbAggregateAllVO.setMappedResults(mappedResults);
        setUserDept(allFieldList, mappedResults, mongodbAggregateVO);
        dealData(widget, mongodbAggregateAllVO);
        buildData(mappedResults, mongodbAggregateRequest, mongodbAggregateAllVO);
        return mongodbAggregateAllVO;
    }

    @Override
    public void growthRate(MongodbAggregateRequest mongodbAggregateRequest) {


    }

    private void setUserDept(List<MongodbSearchField> allFieldList, List<JSONObject> mappedResults,
                             MongodbAggregateVO mongodbAggregateVO) {
        List<Long> userList = new ArrayList<>();
        List<Long> deptList = new ArrayList<>();
        List<Long> roleIdList = new ArrayList<>();
        for (MongodbSearchField mongodbSearchField : allFieldList) {
            for (JSONObject jsonObject : mappedResults) {
                if (FormFieldTypeEnum.getDeptFieldTypeExistUse().contains(mongodbSearchField.getType())) {
                    Object deptId = jsonObject.get(mongodbSearchField.getTag());
                    if (deptId != null) {
                        deptList.add(Long.valueOf(deptId.toString()));
                    }
                } else if (FormFieldTypeEnum.getUserFieldTypeExistUse().contains(mongodbSearchField.getType()) ||
                        FormSystemFieldEnum.CREATE_NAME.getName().equals(mongodbSearchField.getName())) {
                    Object userId = jsonObject.get(mongodbSearchField.getTag());
                    if (userId != null) {
                        userList.add(Long.valueOf(userId.toString()));
                    }
                } else if (FormFieldTypeEnum.getRoleFieldType().contains(mongodbSearchField.getType())) {
                    Object roleId = jsonObject.get(mongodbSearchField.getTag());
                    if (roleId != null) {
                        roleIdList.add(Long.valueOf(roleId.toString()));
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(deptList)) {
            List<DepartmentVO> departmentVOList = departmentService.queryListByIdList(deptList);
            mongodbAggregateVO.setDepartmentList(departmentVOList);
        }
        if (CollectionUtils.isNotEmpty(userList)) {
            List<UserVO> userVOS = userService.queryByIds(userList);
            mongodbAggregateVO.setUserList(userVOS);
        }
        List<PostVO> postVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            postVOList = postService.getAllPost();
        }
        Map<Long, String> postNameMap =
                postVOList.stream().collect(Collectors.toMap(PostVO::getPostId, PostVO::getPostName));

        // Map<Long, String> deptNameMap = mongodbAggregateVO.getDepartmentList().stream()
        //         .collect(Collectors.toMap(DepartmentVO::getDeptId, DepartmentVO::getDeptName));
        // Map<Long, String> userNameMap = mongodbAggregateVO.getUserList().stream()
        //         .collect(Collectors.toMap(UserVO::getUserId, UserVO::getNickName));
        for (MongodbSearchField mongodbSearchField : allFieldList) {
            for (JSONObject jsonObject : mappedResults) {
                if (FormFieldTypeEnum.getDeptFieldTypeExistUse().contains(mongodbSearchField.getType())) {
                    Object deptId = jsonObject.get(mongodbSearchField.getTag());
                    if (deptId != null) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("deptId", deptId.toString());
                        jsonObject1.put("value", deptId.toString());
                        // jsonObject1.put("name", deptNameMap.getOrDefault(Long.valueOf(deptId.toString()), ""));
                        jsonObject.put(mongodbSearchField.getTag(), jsonObject1);
                    } else {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("deptId", null);
                        jsonObject1.put("value", null);
                        jsonObject.put(mongodbSearchField.getTag(), jsonObject1);
                    }
                } else if (FormFieldTypeEnum.getUserFieldTypeExistUse().contains(mongodbSearchField.getType()) ||
                        FormSystemFieldEnum.CREATE_NAME.getName().equals(mongodbSearchField.getName())) {
                    Object userId = jsonObject.get(mongodbSearchField.getTag());
                    if (userId != null) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("userId", userId.toString());
                        jsonObject1.put("assigneeId", userId.toString());
                        // jsonObject1.put("name", userNameMap.getOrDefault(Long.valueOf(userId.toString()), ""));
                        jsonObject.put(mongodbSearchField.getTag(), jsonObject1);
                    } else {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("userId", null);
                        jsonObject1.put("assigneeId", null);
                        jsonObject.put(mongodbSearchField.getTag(), jsonObject1);
                    }
                } else if (FormFieldTypeEnum.getRoleFieldType().contains(mongodbSearchField.getType())) {
                    Object roleId = jsonObject.get(mongodbSearchField.getTag());
                    JSONObject jsonObject1 = new JSONObject();
                    if (roleId != null) {
                        jsonObject1.put("roleId", roleId.toString());
                        jsonObject1.put("name", postNameMap.getOrDefault(Long.valueOf(roleId.toString()), ""));
                    } else {
                        jsonObject1.put("userId", null);
                    }
                    jsonObject.put(mongodbSearchField.getTag(), jsonObject1);
                }
            }
        }
    }

    @Override
    public MongodbAggregateAllVO getAggregateData(MongodbAggregateRequest mongodbAggregateRequest) {
        List<Criteria> criteriaList = new ArrayList<>();
        MongodbWidget widget = mongodbAggregateRequest.getWidget();
        String tableName;
        List<AggregationOperation> aggregationList = new ArrayList<>();
        if (mongodbAggregateRequest.getFormId().startsWith(Constants.AGGREGATE_TABLE)) {
            FormAggregateMongoVO formAggregateMongoVO =
                    formAggregateService.buildAggregate(mongodbAggregateRequest.getFormId(),
                            mongodbAggregateRequest.getApplicationId());
            aggregationList.addAll(formAggregateMongoVO.getAggregationList());
            tableName = formAggregateMongoVO.getTableName();
        } else if (mongodbAggregateRequest.getFormId().startsWith(Constants.FAC_PREFIX)) {
            FormDataFactoryParamVO factoryParam =
                    formDataFactoryExecuteApi.getParam(mongodbAggregateRequest.getApplicationId(),
                            mongodbAggregateRequest.getFormId());
            aggregationList.addAll(factoryParam.getAggregationOperations());
            tableName = factoryParam.getTableName();
        } else {
            FormVO info =
                    formService.info(mongodbAggregateRequest.getFormId(), mongodbAggregateRequest.getApplicationId());
            // 增加权限过滤条件
            criteriaList = getCriteriaList(widget.getPermission(), info);
            if (criteriaList == null) {
                return null;
            }
            tableName = info.getTableName();
            MongoSearchUtils.buildCommonFilter(criteriaList, mongodbAggregateRequest.getApplicationId(),
                    mongodbAggregateRequest.getFormId());
        }
        // 添加系统级别的过滤条件
        addSystemFilterCondition(widget, criteriaList, mongodbAggregateRequest);
        // 添加条件过滤条件
        buildFilter(mongodbAggregateRequest, criteriaList);
        MongoSearchUtils.addMatch(criteriaList, aggregationList);
        // 所有维度列表
        List<MongodbSearchField> allFieldList = new ArrayList<>();
        allFieldList.addAll(widget.getFieldxList());
        allFieldList.addAll(widget.getFieldyList());
        MongoSearchUtils.aggregateUnwind(widget.getMetricList(), allFieldList, aggregationList);
        MongoFormulaRunner mongoFormulaRunner = new MongoFormulaRunner();
        List<MongodbAggregateFormula> aggregateFormulas =
                getMongodbAggregateFormulas(widget, mongoFormulaRunner, aggregationList);
        List<Field> fields = new ArrayList<>();
        // 将计算公式进行拆分
        Map<String, Object> functionDocumentMap = new HashMap<>();
        // 最终的输出field计算公式
        Map<String, Object> functionMap = new HashMap<>();
        List<InstrumentPanelFunctionVO> functionVOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(aggregateFormulas)) {
            for (MongodbAggregateFormula aggregateFormula : aggregateFormulas) {
                InstrumentPanelFunctionVO instrumentPanelFunctionVO = new InstrumentPanelFunctionVO();
                instrumentPanelFunctionVO.setFunctionId(aggregateFormula.getName());
                functionVOS.add(instrumentPanelFunctionVO);
                List<String> functionList = StringUtil.extractAllFunctionCalls(aggregateFormula.getFormula());
                String formula = aggregateFormula.getFormula();
                String finalFormula = aggregateFormula.getFormula();
                if (CollectionUtils.isEmpty(functionList)) {
                    List<DataStreamQuoteField> quotes = aggregateFormula.getQuotes();
                    for (DataStreamQuoteField dataStreamQuoteField : quotes) {
                        String field = MongoSearchUtils.getField(dataStreamQuoteField.getQuoteFieldId(),
                                dataStreamQuoteField.getQuoteSubForm(), dataStreamQuoteField.getQuoteFieldType());
                        functionDocumentMap.put(dataStreamQuoteField.getQuoteFieldId(), field);
                    }
                    instrumentPanelFunctionVO.setFields(
                            quotes.stream().map(DataStreamQuoteField::getQuoteFieldId).collect(Collectors.toList()));
                    instrumentPanelFunctionVO.setFinalFunction(formula);
                    continue;
                }
                List<String> functionFields = new ArrayList<>();

                for (String function : functionList) {
                    Object functionValue = getFunctionValue(aggregateFormula, mongoFormulaRunner, function);
                    String guid = "function_" + ObjectId.getGuid();
                    functionDocumentMap.put(guid, functionValue);
                    formula = formula.replace(function, "'" + guid + "'");
                    finalFormula = finalFormula.replace(function, guid);
                    functionFields.add(guid);
                }
                instrumentPanelFunctionVO.setFields(functionFields);
                instrumentPanelFunctionVO.setFinalFunction(finalFormula);
                Object functionValue = getFunctionValue(aggregateFormula, mongoFormulaRunner, formula);
                functionMap.put(aggregateFormula.getName(), functionValue);
            }
        }
        groupConditions(fields, allFieldList, aggregationList, widget, functionDocumentMap);
        // 用于project函数搜索
        aggregateProject(widget, fields, aggregationList, functionDocumentMap, functionMap);
        aggregateSort(allFieldList, widget, aggregationList);
        try {
            List<Document> documentList = MongoFunctionUtils.toDocument(aggregationList);
            AggregateIterable<JSONObject> aggregate =
                    mongoTemplate.getCollection(tableName).aggregate(documentList, JSONObject.class).collation(
                            Collation.builder().locale("zh").collationStrength(CollationStrength.PRIMARY).build());
            List<JSONObject> mappedResults = new ArrayList<>();
            Iterator<JSONObject> iterator = aggregate.iterator();
            while (iterator.hasNext()) {
                mappedResults.add(new JSONObject(iterator.next()));
            }
            MongodbAggregateAllVO mongodbAggregateAllVO = new MongodbAggregateAllVO();
            mongodbAggregateAllVO.setMappedResults(mappedResults);
            mongodbAggregateAllVO.setFunctions(functionVOS);
            mongodbAggregateAllVO.setFunctionDocumentMap(functionDocumentMap);
            mongodbAggregateAllVO.setFunctionMap(functionMap);
            return mongodbAggregateAllVO;
        } catch (Exception e) {
            log.error("查询报错", e);
            if (e.getMessage().contains("Invalid reference")) {
                throw new ServiceException(ServiceResultCode.PARAM_ERROR, "请查看配置是否存在字段丢失！");
            } else {
                throw e;
            }
        }
    }

    private static List<MongodbAggregateFormula> getMongodbAggregateFormulas(MongodbWidget widget,
                                                                             MongoFormulaRunner mongoFormulaRunner,
                                                                             List<AggregationOperation> aggregationList) {
        List<MongodbAggregateFormula> aggregateFormulas = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(widget.getFormulas())) {
            AddFieldsOperation.AddFieldsOperationBuilder addFields = Aggregation.addFields();
            boolean needAddField = Boolean.FALSE;
            for (MongodbAggregateFormula aggregateFormula : widget.getFormulas()) {
                String fieldId = MongoSearchUtils.getFieldId(aggregateFormula.getName(), "");
                if (aggregateFormula.getAdvancedFormula()) {
                    List<String> functionList = StringUtil.extractAllFunctionCalls(aggregateFormula.getFormula());
                    aggregateFormulas.add(aggregateFormula);
                    if (!functionList.isEmpty()) {
                        continue;
                    }
                    needAddField = Boolean.TRUE;
                    try {
                        Object functionValue =
                                getFunctionValue(aggregateFormula, mongoFormulaRunner, aggregateFormula.getFormula());
                        if (functionValue instanceof Document) {
                            AggregationExpression expression = AggregationExpression.from(
                                    MongoExpression.create(JSONObject.toJSONString(functionValue)));
                            addFields = addFields.addField(fieldId).withValue(expression);
                        } else {
                            addFields = addFields.addField(fieldId).withValue(functionValue);
                        }
                    } catch (Exception e) {
                        log.error("公式转换失败", e);
                        throw new ServiceException(ServiceResultCode.FORMULA_ERROR, e.getCause().getMessage());
                    }
                } else {
                    addFields = addFields.addField(fieldId).withValueOfExpression(
                            aggregateFormula.getFormula().replace("count", "size").replaceAll("\\$", ""));
                }
            }
            if (needAddField) {
                aggregationList.add(addFields.build());
            }
        }
        return aggregateFormulas;
    }

    private static Object getFunctionValue(MongodbAggregateFormula aggregateFormula,
                                           MongoFormulaRunner mongoFormulaRunner, String formula) {
        try {
            DefaultContext<String, Object> context = new DefaultContext<>();
            if (CollectionUtils.isNotEmpty(aggregateFormula.getQuotes())) {
                for (DataStreamQuoteField quoteField : aggregateFormula.getQuotes()) {
                    context.put(quoteField.getId(),
                            MongoSearchUtils.getField(quoteField.getQuoteFieldId(), quoteField.getQuoteSubForm(),
                                    quoteField.getQuoteFieldType()));
                }
            }
            return mongoFormulaRunner.execute(formula, context, null, true, false);
        } catch (Exception e) {
            log.error("公式错误", e);
        }
        return null;
    }

    @Override
    public QueryPageVO<JSONObject> detailedList(MongodbDetailedListRequest mongodbDetailedListRequest) {
        filterCheckAndSearchValue(mongodbDetailedListRequest.getFilter(), mongodbDetailedListRequest.getApplicationId(),
                mongodbDetailedListRequest.getFormId());
        filterCheckAndSearchValue(mongodbDetailedListRequest.getWidget().getFilter(),
                mongodbDetailedListRequest.getApplicationId(), mongodbDetailedListRequest.getFormId());
        MongodbWidget widget = mongodbDetailedListRequest.getWidget();
        // 根据仪表盘组件数据设置控制数据权限
        String tableName;
        List<AggregationOperation> aggregationList = new ArrayList<>();
        List<Criteria> criteriaList = new ArrayList<>();
        if (mongodbDetailedListRequest.getFormId().startsWith(Constants.AGGREGATE_TABLE)) {
            FormAggregateMongoVO formAggregateMongoVO =
                    formAggregateService.buildAggregate(mongodbDetailedListRequest.getFormId(),
                            mongodbDetailedListRequest.getApplicationId());
            aggregationList = formAggregateMongoVO.getAggregationList();
            tableName = formAggregateMongoVO.getTableName();
        } else if (mongodbDetailedListRequest.getFormId().startsWith(Constants.FAC_PREFIX)) {
            FormDataFactoryParamVO factoryParam =
                    formDataFactoryExecuteApi.getParam(mongodbDetailedListRequest.getApplicationId(),
                            mongodbDetailedListRequest.getFormId());
            aggregationList.addAll(factoryParam.getAggregationOperations());
            tableName = factoryParam.getTableName();
        } else {
            FormVO info = formService.info(mongodbDetailedListRequest.getFormId(),
                    mongodbDetailedListRequest.getApplicationId());
            // 增加权限过滤条件
            criteriaList = getCriteriaList(widget.getPermission(), info);
            if (criteriaList == null) {
                return null;
            }
            tableName = info.getTableName();
            MongoSearchUtils.buildCommonFilter(criteriaList, mongodbDetailedListRequest.getApplicationId(),
                    mongodbDetailedListRequest.getFormId());
        }
        setCriteriaList(mongodbDetailedListRequest, criteriaList);
        MongoSearchUtils.addMatch(criteriaList, aggregationList);
        int limit = mongodbDetailedListRequest.getPageSize();
        if (widget.getShowTopNum() != null && widget.getShowTopNum().getEnable() &&
                widget.getShowTopNum().getLimit() != null) {
            int size = widget.getShowTopNum().getLimit() -
                    (mongodbDetailedListRequest.getPageNum() - 1) * mongodbDetailedListRequest.getPageSize();
            limit = Math.min(limit, size);
        }
        int countSize = getAggregateCountSize(aggregationList, tableName);
        MongoSearchUtils.addLimit(aggregationList, limit + mongodbDetailedListRequest.getOffSet(),
                mongodbDetailedListRequest.getOffSet());
        List<MongodbSearchField> fields = widget.getFields();
        buildProject(fields, widget, aggregationList);
        MongoSearchUtils.addSortAgg(mongodbDetailedListRequest.getSorts(), aggregationList);
        // 执行查找到的匹配的全部文档信息
        List<Document> documentList = MongoFunctionUtils.toDocument(aggregationList);
        AggregateIterable<JSONObject> aggregate =
                mongoTemplate.getCollection(tableName).aggregate(documentList, JSONObject.class);
        List<JSONObject> mappedResults = new ArrayList<>();
        Iterator<JSONObject> iterator = aggregate.iterator();
        while (iterator.hasNext()) {
            mappedResults.add(new JSONObject(iterator.next()));
        }
        List<MongodbSearchField> fieldList = widget.getFields();
        SystemAllDataVO systemAllData = adminCommonService.getSystemAllData();
        for (MongodbSearchField mongodbSearchField : fieldList) {
            FormDataService formDataService = formDataContext.getHandler(mongodbSearchField.getType());
            if (formDataService != null) {
                formDataService.dealDetailedReturn(mongodbSearchField, mappedResults, systemAllData);
            }
        }
        return new QueryPageVO<>(mongodbDetailedListRequest.getPageNum(), mongodbDetailedListRequest.getPageSize(),
                countSize, mappedResults);
    }

    @Override
    public void checkDetailedFormat(MongodbDetailedCheckRequest mongodbDetailedCheckRequest) {
        try {
            List<Field> fieldIdList = new ArrayList<>();
            for (MongodbSearchField mongodbSearchField : mongodbDetailedCheckRequest.getFields()) {
                String fieldId = MongoSearchUtils.getFieldIdNotExistLogic(mongodbSearchField.getName(),
                        mongodbSearchField.getType());
                fieldIdList.add(Fields.field(fieldId, fieldId));
            }
            Fields from = Fields.from(fieldIdList.get(0));
            for (int i = 1; i < fieldIdList.size(); i++) {
                from = from.and(fieldIdList.get(i));
            }
            // FormVO info = formService.info(mongodbDetailedCheckRequest.getFormId(),
            //         mongodbDetailedCheckRequest.getApplicationId());
            // ProjectionOperation projectionOperation = Aggregation.project(from);
            // projectionOperation.andExpression(mongodbDetailedCheckRequest.getFormula()).as("formula");
            // List<AggregationOperation> aggregationList = new ArrayList<>();
            // aggregationList.add(projectionOperation);
            // // 执行查找到的匹配的全部文档信息
            // AggregationResults<JSONObject> aggregate =
            //         mongoTemplate.aggregate(Aggregation.newAggregation(aggregationList), info.getTableName(),
            //                 JSONObject.class);
        } catch (Exception e) {
            log.info("错误", e);
            throw new ServiceException(ServiceResultCode.FORMULA_ERROR, "");
        }
    }

    @Override
    public void checkAggregateFormat(MongodbAggregateCheckRequest mongodbAggregateCheckRequest) {
        List<String> functionListAdd = Lists.newArrayList("sum", "max", "min", "average");
        // 正则表达式，用于匹配$开头到)结尾的内容
        String regex = "\\$([^)]+)";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建matcher对象
        Matcher matcher = pattern.matcher(mongodbAggregateCheckRequest.getFormula());
        List<String> matchFieldList = new ArrayList<>();
        // 查找匹配的部分
        while (matcher.find()) {
            matchFieldList.add(matcher.group());
        }
        boolean contain = Boolean.FALSE;
        for (String function : functionListAdd) {
            if (mongodbAggregateCheckRequest.getFormula().contains(function)) {
                contain = true;
                break;
            }
        }
        if (matchFieldList.size() > 1 && contain) {
            throw new ServiceException(ServiceResultCode.FORMULA_ERROR, "");
        }
        if (!matchFieldList.isEmpty()) {
            String matchField = matchFieldList.get(0);
            List<String> split = Arrays.stream(matchField.split("\\.")).collect(Collectors.toList());
            if (split.size() > 3) {
                throw new ServiceException(ServiceResultCode.FORMULA_ERROR, "");
            }

            MongodbSearchField matchSearchField = null;
            for (MongodbSearchField mongodbSearchField : mongodbAggregateCheckRequest.getFields()) {
                List<String> list =
                        Arrays.stream(mongodbSearchField.getName().split("\\.")).collect(Collectors.toList());
                if (split.get(split.size() - 1).equals(list.get(list.size() - 1))) {
                    matchSearchField = mongodbSearchField;
                    break;
                }
            }

            if (matchSearchField == null) {
                throw new ServiceException(ServiceResultCode.FORMULA_ERROR, "");
            }

            if (!FormFieldTypeEnum.INPUT_NUMBER.getFieldType().equals(matchSearchField.getType())) {
                for (String function : functionListAdd) {
                    if (mongodbAggregateCheckRequest.getFormula().contains(function)) {
                        throw new ServiceException(ServiceResultCode.FORMULA_ERROR,
                                function + "聚合函数的参数必须为数值类型");
                    }
                }
            }
            if (mongodbAggregateCheckRequest.getFormula().contains("count")) {
                if (StringUtils.isEmpty(matchSearchField.getSubForm())) {
                    throw new ServiceException(ServiceResultCode.FORMULA_ERROR, "count聚合函数的参数必须为数组类型");
                }
            }
        } else {
            List<String> subFormList = new ArrayList<>();
            List<String> mainList = new ArrayList<>();
            List<String> functionList = Lists.newArrayList("+", "-", "*", "/");
            List<MongodbSearchField> containList = new ArrayList<>();
            for (MongodbSearchField mongodbSearchField : mongodbAggregateCheckRequest.getFields()) {
                if (FormFieldTypeEnum.INPUT_NUMBER.getFieldType().equals(mongodbSearchField.getType())) {
                    continue;
                }
                if (mongodbAggregateCheckRequest.getFormula().contains(mongodbSearchField.getName())) {
                    for (String function : functionList) {
                        if (mongodbAggregateCheckRequest.getFormula().contains(function)) {
                            throw new ServiceException(ServiceResultCode.FORMULA_ERROR,
                                    function + "运算符的运算字段应该为数值类型");
                        }
                    }
                    if (StringUtils.isNotEmpty(mongodbSearchField.getSubForm())) {
                        if (!subFormList.isEmpty()) {
                            if (!mongodbSearchField.getSubForm().equals(subFormList.get(0))) {
                                throw new ServiceException(ServiceResultCode.FORMULA_ERROR,
                                        "运算符中只能使用同一个子表单内的字段");
                            }
                        }
                        if (!mainList.isEmpty()) {
                            throw new ServiceException(ServiceResultCode.FORMULA_ERROR,
                                    "主表字段不允许与子表字段互相计算");
                        }
                        subFormList.add(mongodbSearchField.getSubForm());
                    } else {
                        if (!subFormList.isEmpty()) {
                            throw new ServiceException(ServiceResultCode.FORMULA_ERROR,
                                    "主表字段不允许与子表字段互相计算");
                        }
                        mainList.add(mongodbSearchField.getName());
                    }
                    containList.add(mongodbSearchField);
                }
            }
            if (containList.size() > 1) {
                boolean contains = false;
                for (String function : functionList) {
                    if (mongodbAggregateCheckRequest.getFormula().contains(function)) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    throw new ServiceException(ServiceResultCode.FORMULA_ERROR, "");
                }
            }
        }

        // try {
        //     List<AggregationOperation> aggregationList = new ArrayList<>();
        //     FormVO info = formService.info(mongodbAggregateCheckRequest.getFormId(),
        //             mongodbAggregateCheckRequest.getApplicationId());
        //
        //     List<Field> fieldIdList = new ArrayList<>();
        //     for (MongodbSearchField mongodbSearchField : mongodbAggregateCheckRequest.getFields()) {
        //         String fieldId = MongoSearchUtils.getFieldIdNotExistLogic(mongodbSearchField.getName(),
        //                 mongodbSearchField.getType());
        //         fieldIdList.add(Fields.field(fieldId, fieldId));
        //     }
        //     Fields from = Fields.from(fieldIdList.get(0));
        //     for (int i = 1; i < fieldIdList.size(); i++) {
        //         from = from.and(fieldIdList.get(i));
        //     }
        //
        //     ProjectionOperation projectionOperation = Aggregation.project(from);
        //     projectionOperation.andExpression(mongodbAggregateCheckRequest.getFormula()).as("formula");
        //     aggregationList.add(projectionOperation);
        //     // 执行查找到的匹配的全部文档信息
        //     AggregationResults<JSONObject> aggregate =
        //             mongoTemplate.aggregate(Aggregation.newAggregation(aggregationList), info.getTableName(),
        //                     JSONObject.class);
        // } catch (Exception e) {
        //     log.info("错误", e);
        //     throw new ServiceException(ServiceResultCode.FORMULA_ERROR, "");
        // }
    }

    @Override
    public void checkAggregateFormatNew(MongodbAggregateCheckRequest mongodbAggregateCheckRequest) {
        String formula = mongodbAggregateCheckRequest.getFormula();
        checkFormula(formula, "sum", mongodbAggregateCheckRequest.getFields());
        checkFormula(formula, "max", mongodbAggregateCheckRequest.getFields());
        checkFormula(formula, "min", mongodbAggregateCheckRequest.getFields());
        checkFormula(formula, "average", mongodbAggregateCheckRequest.getFields());
        checkFormula(formula, "count", mongodbAggregateCheckRequest.getFields());

    }

    @Override
    public List<JSONObject> gantt(MongodbGanttRequest mongodbGanttRequest) {
        List<AggregationOperation> aggregationList = new ArrayList<>();
        List<Criteria> criteriaList = new ArrayList<>();
        MongoSearchUtils.buildCommonFilter(criteriaList, mongodbGanttRequest.getApplicationId(),
                mongodbGanttRequest.getFormId());
        addCriteria(mongodbGanttRequest.getFilter(), criteriaList);
        addCriteria(mongodbGanttRequest.getWidget().getFilter(), criteriaList);
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            Criteria criteria = new Criteria();
            criteria.andOperator(criteriaList);
            aggregationList.add(Aggregation.match(criteria));
        }
        MongodbGanttRequest.GanttWidget widget = mongodbGanttRequest.getWidget();
        List<MongodbSearchField> fieldxList = widget.getFieldxList();
        MongodbGanttRequest.GanttField ganttFields = widget.getGanttFields();
        List<MongodbSearchField> allFields = new ArrayList<>(fieldxList);
        allFields.add(ganttFields.getEnd());
        allFields.add(ganttFields.getStart());
        if (ganttFields.getProgress() != null) {
            allFields.add(ganttFields.getProgress());
        }
        if (widget.getChartLabel().getField() != null) {
            allFields.add(widget.getChartLabel().getField());
        }
        Document project = getProject(allFields);
        ProjectAggregation projectAggregation = new ProjectAggregation(project);
        MongoSearchUtils.addSortAgg(widget.getSorts(), aggregationList);
        aggregationList.add(projectAggregation);
        FormVO formVO = formService.info(mongodbGanttRequest.getFormId(), mongodbGanttRequest.getApplicationId());
        // 执行查找到的匹配的全部文档信息
        Aggregation aggregation = Aggregation.newAggregation(aggregationList);
        AggregationResults<JSONObject> aggregate =
                mongoTemplate.aggregate(aggregation, formVO.getTableName(), JSONObject.class);
        return aggregate.getMappedResults();
    }


    private Document getProject(List<MongodbSearchField> allFields) {
        Document fieldDocument = new Document();
        for (MongodbSearchField mongodbSearchField : allFields) {
            fieldDocument.append(MongoSearchUtils.getFieldId(mongodbSearchField.getTag(), ""),
                    MongoSearchUtils.getFieldId(mongodbSearchField.getName(), ""));
        }
        fieldDocument.append("uuid", 1);
        fieldDocument.append("_id", 1);
        return fieldDocument;
    }

    public static void main(String[] args) throws Exception {

        String formula = "sum($instValue.number_medw066l)/sum($instValue.number_mejr3deq)";
        checkFormula(formula, "sum", new ArrayList<>());
        checkFormula(formula, "max", new ArrayList<>());
        checkFormula(formula, "min", new ArrayList<>());
        checkFormula(formula, "average", new ArrayList<>());
        checkFormula(formula, "count", new ArrayList<>());

    }

    @Override
    public InstrumentPanelPivotTableVO pivotTable(MongodbAggregateRequest mongodbAggregateRequest) {
        MongodbAggregateAllVO aggregate = aggregate(mongodbAggregateRequest);
        MongodbAggregateVO mongodbAggregateVO = aggregate.getMongodbAggregateVO();
        if (mongodbAggregateVO == null) {
            return null;
        }
        FormColumnsResultVO formColumn = getFormColumns(mongodbAggregateVO, mongodbAggregateRequest);
        List<JSONObject> returnDataList = getJsonObjects(mongodbAggregateVO, mongodbAggregateRequest);
        MongodbWidget widget = mongodbAggregateRequest.getWidget();
        MongodbAggregateShowSubSummary showSubSummary = widget.getShowSubSummary();

        if (showSubSummary != null && showSubSummary.getShowSubSummary() &&
                CollectionUtils.isNotEmpty(showSubSummary.getSubSummaryFields()) &&
                CollectionUtils.isNotEmpty(returnDataList)) {
            List<JSONObject> subSummaryList = returnDataList;
            if (widget.getShowSummary().getShouldShowSummaryRow()) {
                subSummaryList = returnDataList.subList(0, returnDataList.size() - 1);
            }
            List<String> allTags =
                    widget.getFieldxList().stream().map(MongodbSearchField::getTag).collect(Collectors.toList());
            List<List<String>> dimensionLevels = getDimensionLevels(showSubSummary, allTags);
            List<String> sumFields = getMetricListKey(formColumn.getMaxNumber(), allTags);
            // 5. 插入多级小计行
            subSummaryList = MultiLevelSummaryRowInserter.insertMultiLevelSummaryRows(subSummaryList, dimensionLevels,
                    sumFields);
            MultiLevelSummaryRowInserter.setSubSummaryLabel(subSummaryList, allTags.size());
            calculate(aggregate, subSummaryList, mongodbAggregateRequest, dimensionLevels);
            if (widget.getShowSummary().getShouldShowSummaryRow()) {
                subSummaryList.add(returnDataList.get(returnDataList.size() - 1));
            }
            returnDataList = subSummaryList;
        }
        InstrumentPanelPivotTableVO instrumentPanelPivotTableVO = new InstrumentPanelPivotTableVO();
        instrumentPanelPivotTableVO.setColumns(formColumn.getColumnList());
        instrumentPanelPivotTableVO.setDataList(returnDataList);
        instrumentPanelPivotTableVO.setDepartmentList(mongodbAggregateVO.getDepartmentList());
        instrumentPanelPivotTableVO.setUserList(mongodbAggregateVO.getUserList());
        return instrumentPanelPivotTableVO;
    }

    @Override
    public void export(MongodbAggregateRequest mongodbAggregateRequest, HttpServletResponse response) {
        InstrumentPanelPivotTableVO instrumentPanelPivotTableVO = pivotTable(mongodbAggregateRequest);
        ScoreData scoreData =
                JSONObject.parseObject(JSONObject.toJSONString(instrumentPanelPivotTableVO), ScoreData.class);
        PivotTableExportUtils.export(scoreData, response);
    }

    private static void calculate(MongodbAggregateAllVO aggregate, List<JSONObject> subSummaryList,
                                  MongodbAggregateRequest mongodbAggregateRequest, List<List<String>> dimensionLevels) {
        if (CollectionUtils.isEmpty(aggregate.getFunctions())) {
            return;
        }
        MongodbWidget widget = mongodbAggregateRequest.getWidget();
        Map<String, MultiLevelSummaryRowInserter.Summary> groupSummaryMap =
                getGroupSummaryMap(aggregate, widget, widget.getShowSubSummary(), Boolean.TRUE, false);
        Map<String, MultiLevelSummaryRowInserter.Summary> groupSummaryMapY =
                getGroupSummaryMap(aggregate, widget, widget.getShowSubSummary(), Boolean.FALSE, false);
        Map<String, List<String>> dimensionLevelMap = new HashMap<>();
        for (List<String> dimensions : dimensionLevels) {
            String remark =
                    dimensions.size() > 1 ? String.join("+", dimensions) + "维度小计" : dimensions.get(0) + "维度小计";
            dimensionLevelMap.put(remark, dimensions);
        }
        Map<String, String> metricTagToNameMap = widget.getMetricList().stream()
                .collect(Collectors.toMap(MongodbSearchField::getTag, MongodbSearchField::getName));
        MongodbAggregateVO aggregateMongodbAggregateVO = aggregate.getMongodbAggregateVO();
        Map<String, InstrumentPanelFunctionVO> functionIdMap = aggregate.getFunctions().stream()
                .collect(Collectors.toMap(InstrumentPanelFunctionVO::getFunctionId, c -> c));
        List<MongodbAggregateMetricsData> val = aggregateMongodbAggregateVO.getData().getVal();
        for (JSONObject jsonObject : subSummaryList) {
            if (!jsonObject.containsKey("remark")) {
                continue;
            }
            String remark = jsonObject.getString("remark");
            List<String> dimensions = dimensionLevelMap.get(remark);
            for (MongodbAggregateMetricsData mongodbAggregateMetricsData : val) {
                if (mongodbAggregateMetricsData.getSummaryCol()) {
                    if (CollectionUtils.isEmpty(widget.getFieldyList())) {
                        continue;
                    }
                    calculateFunction(jsonObject, mongodbAggregateMetricsData, dimensions, groupSummaryMapY,
                            metricTagToNameMap, functionIdMap, false);
                } else {
                    calculateFunction(jsonObject, mongodbAggregateMetricsData, dimensions, groupSummaryMap,
                            metricTagToNameMap, functionIdMap, Boolean.TRUE);
                }
            }
        }
    }

    private static void calculateFunction(JSONObject jsonObject,
                                          MongodbAggregateMetricsData mongodbAggregateMetricsData,
                                          List<String> dimensions,
                                          Map<String, MultiLevelSummaryRowInserter.Summary> groupSummaryMap,
                                          Map<String, String> metricTagToNameMap,
                                          Map<String, InstrumentPanelFunctionVO> functionIdMap, Boolean needY) {
        String key = "";
        if (CollectionUtils.isNotEmpty(dimensions)) {
            key = MultiLevelSummaryRowInserter.generateGroupKey(jsonObject, dimensions);
            if (needY) {
                String yKey = mongodbAggregateMetricsData.getY().stream().map(c -> c != null ? c.toString() : "")
                        .collect(Collectors.joining("||"));
                key = yKey + "||" + key;
            }
        }
        MultiLevelSummaryRowInserter.Summary summary = groupSummaryMap.get(key);
        if (summary != null) {
            String functionId = metricTagToNameMap.get(mongodbAggregateMetricsData.getTag());
            InstrumentPanelFunctionVO instrumentPanelFunctionVO = functionIdMap.get(functionId);
            if (instrumentPanelFunctionVO == null) {
                return;
            }
            FormulaRunner formulaRunner = new FormulaRunner();
            try {
                DefaultContext<String, Object> context = new DefaultContext<>();
                for (String fieldKey : instrumentPanelFunctionVO.getFields()) {
                    context.put(fieldKey, Double.valueOf(summary.getSumMap().get(fieldKey).toString()));
                }
                Object functionValue =
                        formulaRunner.execute(instrumentPanelFunctionVO.getFinalFunction(), context, null, true, false);
                jsonObject.put(mongodbAggregateMetricsData.getFinalDataKey(), functionValue);
            } catch (Exception e) {
                log.error("公式执行失败", e);
                jsonObject.put(mongodbAggregateMetricsData.getFinalDataKey(), 0);
            }
        }
    }

    private static Map<String, MultiLevelSummaryRowInserter.Summary> getGroupSummaryMap(MongodbAggregateAllVO aggregate,
                                                                                        MongodbWidget widget,
                                                                                        MongodbAggregateShowSubSummary showSubSummary,
                                                                                        Boolean needY, Boolean all) {
        Map<String, MultiLevelSummaryRowInserter.Summary> groupSummaryMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(aggregate.getFunctions())) {
            List<String> allTags =
                    widget.getFieldxList().stream().map(MongodbSearchField::getTag).collect(Collectors.toList());
            List<String> sumFields = new ArrayList<>();
            for (InstrumentPanelFunctionVO instrumentPanelFunctionVO : aggregate.getFunctions()) {
                sumFields.addAll(instrumentPanelFunctionVO.getFields());
            }
            if (all) {
                Map<String, MultiLevelSummaryRowInserter.Summary> current =
                        MultiLevelSummaryRowInserter.getGroupSummaryMap(new ArrayList<>(), aggregate.getMappedResults(),
                                sumFields);
                groupSummaryMap.putAll(current);
                return groupSummaryMap;
            }
            for (String subSummaryField : showSubSummary.getSubSummaryFields()) {
                int index = allTags.indexOf(subSummaryField);
                List<String> strings = allTags.subList(0, index + 1);
                if (needY) {
                    strings.add(0, "yValue");
                }
                Map<String, MultiLevelSummaryRowInserter.Summary> current =
                        MultiLevelSummaryRowInserter.getGroupSummaryMap(strings, aggregate.getMappedResults(),
                                sumFields);
                groupSummaryMap.putAll(current);
            }
        }
        return groupSummaryMap;
    }

    private static List<List<String>> getDimensionLevels(MongodbAggregateShowSubSummary showSubSummary,
                                                         List<String> allTags) {
        List<List<String>> dimensionLevels = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();
        for (String str : showSubSummary.getSubSummaryFields()) {
            // 获取元素在基准列表中的索引，不存在则返回 -1
            int index = allTags.indexOf(str);
            indexList.add(index);
        }
        indexList.sort(Collections.reverseOrder());
        for (Integer index : indexList) {
            List<String> dimensionLevel = new ArrayList<>();
            for (int i = 0; i <= index; i++) {
                dimensionLevel.add(i + 1 + "");
            }
            dimensionLevels.add(dimensionLevel);
        }
        return dimensionLevels;
    }

    private static List<String> getMetricListKey(Integer maxIndex, List<String> allTags) {
        List<String> sumFields = new ArrayList<>();
        for (int i = allTags.size(); i < maxIndex; i++) {
            sumFields.add(i + 1 + "");
        }
        return sumFields;
    }

    private static void checkFormula(String format, String function, List<MongodbSearchField> fieldList) {
        // 定义正则表达式
        String regex = String.format("%s\\(([^)]+)\\)", function);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(format);
        List<String> functionList = new ArrayList<>();
        while (matcher.find()) {
            // 提取括号内的内容
            String content = matcher.group(1);
            functionList.add(content);
        }
        checkField(functionList, fieldList, function);
    }

    private static void checkField(List<String> sumList, List<MongodbSearchField> fieldList, String function) {
        for (String formula : sumList) {
            List<String> fields = getFields(formula);
            for (String matchField : fields) {
                List<String> split = Arrays.stream(matchField.split("\\.")).collect(Collectors.toList());
                if (split.size() > 3) {
                    throw new ServiceException(ServiceResultCode.FORMULA_ERROR, "");
                }
                MongodbSearchField matchSearchField = null;
                for (MongodbSearchField mongodbSearchField : fieldList) {
                    List<String> list =
                            Arrays.stream(mongodbSearchField.getName().split("\\.")).collect(Collectors.toList());
                    if (split.get(split.size() - 1).equals(list.get(list.size() - 1))) {
                        matchSearchField = mongodbSearchField;
                        break;
                    }
                }

                if (matchSearchField == null) {
                    throw new ServiceException(ServiceResultCode.FORMULA_ERROR, "");
                }
                List<String> functionListAdd = Lists.newArrayList("sum", "max", "min", "average");
                if (functionListAdd.contains(function)) {
                    if (!FormFieldTypeEnum.INPUT_NUMBER.getFieldType().equals(matchSearchField.getType())) {
                        throw new ServiceException(ServiceResultCode.FORMULA_ERROR,
                                function + "聚合函数的参数必须为数值类型");
                    }
                } else {
                    if (StringUtils.isEmpty(matchSearchField.getSubForm())) {
                        throw new ServiceException(ServiceResultCode.FORMULA_ERROR,
                                "count聚合函数的参数必须为数组类型");
                    }
                }
            }
        }
    }

    private static List<String> getFields(String function) {
        String regex = "\\$instValue\\.([a-zA-Z0-9_]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(function);
        // 存储提取出的字段名
        List<String> fields = new ArrayList<>();
        // 查找所有匹配项
        while (matcher.find()) {
            String fieldName = matcher.group(1); // group(1) 是括号内的捕获组
            fields.add(fieldName);
        }
        return fields;
    }

    private static List<JSONObject> getJsonObjects(MongodbAggregateVO mongodbAggregateVO,
                                                   MongodbAggregateRequest mongodbAggregateRequest) {
        MongodbAggregateData data = mongodbAggregateVO.getData();
        List<JSONObject> xData = data.getX();
        int dataSize = 1;
        if (CollectionUtils.isNotEmpty(mongodbAggregateVO.getFieldxList())) {
            JSONObject demoJson = xData.get(0);
            JSONArray demoArray = demoJson.getJSONArray("data");
            dataSize = demoArray.size();
        } else {
            MongodbWidget widget = mongodbAggregateRequest.getWidget();
            if (widget.getShowSummary() == null || !widget.getShowSummary().getShouldShowSummaryCol()) {
                return new ArrayList<>();
            }
        }
        List<JSONObject> returnDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(mongodbAggregateVO.getFieldxList())) {
            for (int i = 0; i < dataSize; i++) {
                Integer number = 1;
                JSONObject returnData = new JSONObject();
                for (JSONObject jsonObject : xData) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    returnData.put(String.valueOf(number), jsonArray.getString(i));
                    number++;
                }
                List<MongodbAggregateMetricsData> val = data.getVal();
                for (MongodbAggregateMetricsData mongodbAggregateMetricsData : val) {
                    returnData.put(String.valueOf(number), mongodbAggregateMetricsData.getData().get(i));
                    number++;
                }
                returnDataList.add(returnData);
            }
        }
        MongodbAggregateShowSummary showSummary = mongodbAggregateRequest.getWidget().getShowSummary();
        if (showSummary != null && showSummary.getShouldShowSummaryRow()) {
            Integer number = 1;
            JSONObject returnData = new JSONObject();
            if (CollectionUtils.isNotEmpty(mongodbAggregateVO.getFieldxList())) {
                for (int i = 0; i < mongodbAggregateVO.getFieldxList().size(); i++) {
                    returnData.put(String.valueOf(number), "汇总");
                    number++;
                }
            } else {
                returnData.put(String.valueOf(number), "汇总");
                number++;
            }
            List<MongodbAggregateMetricsData> val = data.getVal();
            for (MongodbAggregateMetricsData mongodbAggregateMetricsData : val) {
                returnData.put(String.valueOf(number), mongodbAggregateMetricsData.getSum());
                number++;
            }
            returnDataList.add(returnData);
        }
        return returnDataList;
    }

    private static FormColumnsResultVO getFormColumns(MongodbAggregateVO mongodbAggregateVO,
                                                      MongodbAggregateRequest mongodbAggregateRequest) {
        Map<String, List<Integer>> metricsMap = new HashMap<>();
        List<MongodbSearchField> fieldyList = mongodbAggregateVO.getFieldyList();
        List<MongodbSearchField> fieldxList = mongodbAggregateVO.getFieldxList();
        List<FormColumn> columns = new ArrayList<>();
        int number = 1;
        if (CollectionUtils.isEmpty(fieldyList)) {
            for (MongodbSearchField mongodbSearchField : fieldxList) {
                FormColumn child = new FormColumn();
                child.setTitle(mongodbSearchField.getLabel());
                child.setValue(mongodbSearchField.getLabel());
                child.setKey(Integer.toString(number));
                child.setDataIndex(Integer.toString(number));
                child.setTag(mongodbSearchField.getTag());
                columns.add(child);
                number++;
            }
            for (MongodbAggregateMetricsData mongodbAggregateMetricsData : mongodbAggregateVO.getData().getVal()) {
                if (mongodbAggregateMetricsData.getSummaryCol() != null &&
                        mongodbAggregateMetricsData.getSummaryCol()) {
                    continue;
                }
                FormColumn formColumn = new FormColumn();
                formColumn.setTitle(mongodbAggregateMetricsData.getLabel());
                formColumn.setValue(mongodbAggregateMetricsData.getLabel());
                formColumn.setKey(Integer.toString(number));
                formColumn.setDataIndex(Integer.toString(number));
                formColumn.setTag(mongodbAggregateMetricsData.getTag());
                columns.add(formColumn);
                putIndex(mongodbAggregateMetricsData, metricsMap, number);
                number++;
            }
        } else {
            FormColumn formColumn = new FormColumn();
            formColumn.setTitle(fieldyList.get(0).getLabel());
            formColumn.setTag(fieldyList.get(0).getTag());
            formColumn.setValue(fieldyList.get(0).getLabel());
            FormColumn parent = formColumn;
            if (fieldyList.size() > 1) {
                for (MongodbSearchField mongodbSearchField : fieldyList.subList(1, fieldyList.size())) {
                    FormColumn children = new FormColumn();
                    children.setTitle(mongodbSearchField.getLabel());
                    children.setValue(mongodbSearchField.getLabel());
                    children.setTag(mongodbSearchField.getTag());
                    parent.setChildren(Collections.singletonList(children));
                    parent = children;
                }
            }

            List<FormColumn> children = new ArrayList<>();
            if (CollectionUtils.isEmpty(fieldxList)) {
                FormColumn child = new FormColumn();
                child.setTitle("");
                child.setKey(Integer.toString(number));
                child.setDataIndex(Integer.toString(number));
                children.add(child);
                number++;
            } else {
                for (MongodbSearchField mongodbSearchField : fieldxList) {
                    FormColumn child = new FormColumn();
                    child.setTitle(mongodbSearchField.getLabel());
                    child.setValue(mongodbSearchField.getLabel());
                    child.setKey(Integer.toString(number));
                    child.setDataIndex(Integer.toString(number));
                    child.setTag(mongodbSearchField.getTag());
                    children.add(child);
                    number++;
                }
            }
            parent.setChildren(children);
            MongodbAggregateData data = mongodbAggregateVO.getData();
            List<MongodbAggregateMetricsData> val = data.getVal();
            List<FormColumn> sumColumnList = new ArrayList<>();
            for (MongodbAggregateMetricsData mongodbAggregateMetricsData : val) {
                List<Object> y = mongodbAggregateMetricsData.getY();
                FormColumn dataFormColumn = new FormColumn();
                if (mongodbAggregateMetricsData.getSummaryCol() != null &&
                        mongodbAggregateMetricsData.getSummaryCol()) {
                    dataFormColumn.setTitle("汇总");
                    dataFormColumn.setRowSpan(fieldyList.size());
                } else {
                    dataFormColumn.setTagId(fieldyList.get(0).getTag());
                    dataFormColumn.setTitle(y.get(0).toString());
                    dataFormColumn.setValue(y.get(0).toString());
                }
                parent = dataFormColumn;
                if (fieldyList.size() > 1) {
                    for (int i = 1; i < fieldyList.size(); i++) {
                        if (CollectionUtils.isNotEmpty(y)) {
                            FormColumn column = new FormColumn();
                            Object object = y.get(i);
                            column.setTitle(object.toString());
                            column.setValue(object.toString());
                            column.setTagId(fieldyList.get(i).getTag());
                            parent.setChildren(Collections.singletonList(column));
                            parent = column;
                        } else {
                            FormColumn column = new FormColumn();
                            column.setTitle("");
                            parent.setChildren(Collections.singletonList(column));
                            parent = column;
                        }
                    }
                }
                FormColumn child = new FormColumn();
                child.setTitle(mongodbAggregateMetricsData.getLabel());
                child.setValue(mongodbAggregateMetricsData.getLabel());
                child.setKey(Integer.toString(number));
                child.setDataIndex(Integer.toString(number));
                child.setTag(mongodbAggregateMetricsData.getTag());
                putIndex(mongodbAggregateMetricsData, metricsMap, number);
                parent.getChildren().add(child);
                number++;
                if (mongodbAggregateMetricsData.getSummaryCol() != null &&
                        mongodbAggregateMetricsData.getSummaryCol()) {
                    sumColumnList.add(dataFormColumn);
                } else {
                    columns.add(dataFormColumn);
                }
            }
            try {
                JsonNode node = objectMapper.readTree(JSONObject.toJSONString(columns));
                JsonFunctionUtils.mergeSameTitleNodes(node);
                String value = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
                columns = JSONObject.parseArray(value, FormColumn.class);
            } catch (Exception e) {
                log.error("合并json失败", e);
            }
            if (CollectionUtils.isNotEmpty(sumColumnList)) {
                try {
                    JsonNode node = objectMapper.readTree(JSONObject.toJSONString(sumColumnList));
                    JsonFunctionUtils.mergeSameTitleNodes(node);
                    String value = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
                    sumColumnList = JSONObject.parseArray(value, FormColumn.class);
                } catch (Exception e) {
                    log.error("合并json失败", e);
                }
            }
            columns.add(0, formColumn);
            columns.addAll(sumColumnList);
        }
        FormColumnsResultVO formColumnsResultVO = new FormColumnsResultVO();
        formColumnsResultVO.setColumnList(columns);
        formColumnsResultVO.setMetricsMap(metricsMap);
        formColumnsResultVO.setMaxNumber(number);
        return formColumnsResultVO;
    }

    private static void putIndex(MongodbAggregateMetricsData mongodbAggregateMetricsData,
                                 Map<String, List<Integer>> metricsMap, int number) {
        List<Integer> integers = metricsMap.get(mongodbAggregateMetricsData.getTag());
        if (integers == null) {
            integers = new ArrayList<>();
            integers.add(number);
        }
        mongodbAggregateMetricsData.setFinalDataKey(number + "");
        metricsMap.put(mongodbAggregateMetricsData.getTag(), integers);
    }

    private static void dealData(MongodbWidget widget, MongodbAggregateAllVO mongodbAggregateAllVO) {
        List<JSONObject> mappedResults = mongodbAggregateAllVO.getMappedResults();
        List<MongodbSearchField> metricList = widget.getMetricList();
        for (MongodbSearchField metric : metricList) {
            if (MongodbCalculateEnum.COUNT_DISTINCT.name().equals(metric.getOp())) {
                for (JSONObject jsonObject : mappedResults) {
                    JSONArray jsonArray = jsonObject.getJSONArray(metric.getTag());
                    if (jsonArray != null) {
                        jsonObject.put(metric.getTag(), jsonArray.size());
                    } else {
                        jsonObject.put(metric.getTag(), 0);
                    }
                }
            }
        }
    }

    private static void aggregateProject(MongodbWidget widget, List<Field> fields,
                                         List<AggregationOperation> aggregationList,
                                         Map<String, Object> functionDocumentMap, Map<String, Object> functionMap) {
        List<MongodbSearchField> metricList = widget.getMetricList();
        List<String> searchNameList = new ArrayList<>(functionDocumentMap.keySet());
        for (MongodbSearchField mongodbSearchField : metricList) {
            if (functionMap.get(mongodbSearchField.getName()) != null) {
                continue;
            }
            searchNameList.add(mongodbSearchField.getTag());
        }
        String[] search = new String[searchNameList.size()];
        ProjectionOperation project = Aggregation.project(searchNameList.toArray(search));
        if (fields.size() == 1) {
            for (Field field : fields) {
                project = project.and("_id").as(field.getName());
            }
        } else {
            for (Field field : fields) {
                project = project.and("_id." + field.getName()).as(field.getName());
            }
        }
        project = project.andExclude("_id");
        for (MongodbSearchField mongodbSearchField : metricList) {
            if (!"custom_field".equals(mongodbSearchField.getType())) {
                continue;
            }
            Object document = functionMap.get(mongodbSearchField.getName());
            if (document != null) {
                if (document instanceof Document) {
                    AggregationExpression aggregationExpression =
                            AggregationExpression.from(MongoExpression.create(((Document) document).toJson()));
                    project = project.and(aggregationExpression).as(mongodbSearchField.getTag());
                } else {
                    project = project.and(document.toString()).as(mongodbSearchField.getTag());
                }
            }
        }
        aggregationList.add(project);
    }

    private static void aggregateSort(List<MongodbSearchField> allFieldList, MongodbWidget widget,
                                      List<AggregationOperation> aggregationList) {
        List<Sort.Order> sortOrderList = new ArrayList<>();
        Map<String, MongodbSearchField> tagMap =
                allFieldList.stream().collect(Collectors.toMap(MongodbSearchField::getTag, c -> c));
        List<MongodbSearchField> metricList = widget.getMetricList();
        for (MongodbSearchField mongodbSearchField : metricList) {
            tagMap.put(mongodbSearchField.getTag(), mongodbSearchField);
        }
        if (CollectionUtils.isNotEmpty(widget.getDefaultSorts())) {
            sortOrderList = MongoSearchUtils.buildSortAgg(widget.getDefaultSorts(), tagMap);
            aggregationList.add(Aggregation.sort(Sort.by(sortOrderList)));
        } else {
            for (MongodbSearchField mongodbSearchField : allFieldList) {
                MongoSearchUtils.sort(mongodbSearchField, "ASC", sortOrderList);
            }
            if (CollectionUtils.isNotEmpty(sortOrderList)) {
                aggregationList.add(Aggregation.sort(Sort.by(sortOrderList)));
            }
        }
    }

    private void groupConditions(List<Field> fields, List<MongodbSearchField> allFieldList,
                                 List<AggregationOperation> aggregationList, MongodbWidget widget,
                                 Map<String, Object> functionDocumentMap) {
        if (CollectionUtils.isEmpty(allFieldList)) {
            GroupOperation group = Aggregation.group();
            group = MongoSearchUtils.calculate(group, widget.getMetricList(), true);
            group = functionFieldAgg(functionDocumentMap, group);
            aggregationList.add(group);
            return;
        }
        MongoSearchUtils.buildFieldList(fields, allFieldList);
        List<String> groupSpecialList = MongoSearchUtils.coverDateReturn(allFieldList, aggregationList);
        MongoSearchUtils.coverAddress(allFieldList, fields);
        for (String groupDate : groupSpecialList) {
            fields.add(Fields.field(groupDate));
        }
        Fields from = MongoSearchUtils.fieldToFields(fields);
        GroupOperation group = Aggregation.group(from);
        group = MongoSearchUtils.calculate(group, widget.getMetricList(), true);
        group = functionFieldAgg(functionDocumentMap, group);
        aggregationList.add(group);
    }

    private static GroupOperation functionFieldAgg(Map<String, Object> functionDocumentMap, GroupOperation group) {
        for (String key : functionDocumentMap.keySet()) {
            Object o = functionDocumentMap.get(key);
            if (o instanceof String) {
                group = group.sum(o.toString()).as(key);
                continue;
            }
            Document document = (Document) o;
            if (document.containsKey("$sum")) {
                Object value = document.get("$sum");
                if (value instanceof Document) {
                    AggregationExpression expression =
                            AggregationExpression.from(MongoExpression.create(((Document) value).toJson()));
                    group = group.sum(expression).as(key);
                } else {
                    group = group.sum(value.toString()).as(key);
                }
            } else if (document.containsKey("$max")) {
                Object value = document.get("$max");
                if (value instanceof Document) {
                    AggregationExpression expression =
                            AggregationExpression.from(MongoExpression.create(value.toString()));
                    group = group.max(expression).as(key);
                } else {
                    group = group.max(value.toString()).as(key);
                }
            } else if (document.containsKey("$min")) {
                Object value = document.get("$min");
                if (value instanceof Document) {
                    AggregationExpression expression =
                            AggregationExpression.from(MongoExpression.create(value.toString()));
                    group = group.min(expression).as(key);
                } else {
                    group = group.min(value.toString()).as(key);
                }
            } else if (document.containsKey("$avg")) {
                Object value = document.get("$avg");
                if (value instanceof Document) {
                    AggregationExpression expression =
                            AggregationExpression.from(MongoExpression.create(value.toString()));
                    group = group.avg(expression).as(key);
                } else {
                    group = group.avg(value.toString()).as(key);
                }
            }
        }
        return group;
    }

    private void buildData(List<JSONObject> mappedResults, MongodbAggregateRequest mongodbAggregateRequest,
                           MongodbAggregateAllVO mongodbAggregateAllVO) {
        MongodbAggregateVO mongodbAggregateVO = mongodbAggregateAllVO.getMongodbAggregateVO();
        MongodbWidget widget = mongodbAggregateRequest.getWidget();
        List<Map<String, Object>> xMapList = buildLatMap(mappedResults, widget.getFieldxList(), "xValue");
        List<Map<String, Object>> yMapList = buildLatMap(mappedResults, widget.getFieldyList(), "yValue");
        Map<String, Map<Object, JSONObject>> xyValueMap = mappedResults.stream().collect(
                Collectors.groupingBy(c -> c.getString("yValue"), Collectors.toMap(f -> f.get("xValue"), f -> f)));
        List<JSONObject> x = new ArrayList<>();
        List<MongodbSearchField> fieldxList = widget.getFieldxList();
        MongodbAggregateData mongodbAggregateData = new MongodbAggregateData();
        for (MongodbSearchField mongodbField : fieldxList) {
            JSONObject dataJson = new JSONObject();
            List<Object> data = new ArrayList<>();
            for (Map<String, Object> xMap : xMapList) {
                Object value = xMap.get(mongodbField.getTag());
                if (value != null && FormFieldTypeEnum.INPUT_TEXT.getFieldType().equals(mongodbField.getType())) {
                    data.add(MongoDataUtils.decryptAndEncryptReturn(value));
                } else {
                    data.add(value);
                }
            }
            dataJson.put("data", data);
            x.add(dataJson);
        }
        mongodbAggregateData.setX(x);
        List<MongodbAggregateMetricsData> mongodbAggregateMetricsDataList =
                buildReturnData(widget, yMapList, xyValueMap, xMapList);
        mongodbAggregateData.setVal(mongodbAggregateMetricsDataList);
        limit(widget, mongodbAggregateData);
        // 计算列的总数
        if (CollectionUtils.isNotEmpty(mongodbAggregateMetricsDataList)) {
            calculateCol(widget, mongodbAggregateMetricsDataList, mongodbAggregateAllVO);
        }

        // 占比
        if (CollectionUtils.isNotEmpty(mongodbAggregateMetricsDataList)) {
            highCalculate(widget, mongodbAggregateMetricsDataList, x);
        }
        mongodbAggregateVO.setData(mongodbAggregateData);
        mongodbAggregateVO.setFieldxList(widget.getFieldxList());
        if ("PIE_CHART".equals(widget.getType()) && CollectionUtils.isEmpty(widget.getFieldyList())) {
            List<MongodbSearchField> fieldyList = new ArrayList<>();
            MongodbSearchField mongodbSearchField = new MongodbSearchField();
            mongodbSearchField.setName("key");
            fieldyList.add(mongodbSearchField);
            mongodbAggregateVO.setFieldyList(fieldyList);
        } else {
            mongodbAggregateVO.setFieldyList(widget.getFieldyList());
        }
        mongodbAggregateVO.setMetricList(widget.getMetricList());
    }

    /**
     * 为null时说明没有权限 为空时说明有全部权限
     *
     * @param permission 权限id
     * @param info       表单详情
     * @return mongo查询条件
     */
    private List<Criteria> getCriteriaList(String permission, FormVO info) {
        List<Criteria> criteriaList = new ArrayList<>();
        if (MongodbWidgetPermissionEnum.FORM.name().equals(permission)) {
            List<FormPrivilegeVO> formPrivilegeVOList =
                    formPrivilegeService.getUserPrivilegeByCategory(Collections.singletonList(info.getId()),
                            info.getApplicationId());
            if (CollectionUtils.isEmpty(formPrivilegeVOList)) {
                return null;
            }
            // 获取数据范围
            List<FormPrivilegeDataScopeDomain> formPrivilegeDataScopeDomainList =
                    FormPrivilegeUtils.getDataScope(formPrivilegeVOList);
            criteriaList =
                    getPrivilegeCriteria(formPrivilegeDataScopeDomainList, new ArrayList<>(), info.getApplicationId(),
                            info.getId());
        }
        return criteriaList;
    }

    private static void addSystemFilterCondition(MongodbWidget widget, List<Criteria> criteriaList,
                                                 MongodbAggregateRequest mongodbAggregateRequest) {
        for (MongodbSearchField mongodbSearchField : widget.getFieldyList()) {
            String fieldId = MongoSearchUtils.getFieldId(mongodbSearchField.getName(), mongodbSearchField.getType());
            criteriaList.add(Criteria.where(fieldId).ne(null));
        }
        for (MongodbSearchField mongodbSearchField : widget.getFieldxList()) {
            String fieldId = MongoSearchUtils.getFieldId(mongodbSearchField.getName(), mongodbSearchField.getType());
            criteriaList.add(Criteria.where(fieldId).ne(null));
        }

    }

    private List<Map<String, Object>> buildLatMap(List<JSONObject> mappedResults, List<MongodbSearchField> fieldxList,
                                                  String labName) {
        List<Map<String, Object>> xMapList = new ArrayList<>();
        for (JSONObject jsonObject : mappedResults) {
            Map<String, Object> xMap = new HashMap<>();
            List<Object> values = new ArrayList<>();
            for (MongodbSearchField mongodbSearchField : fieldxList) {
                String tag = mongodbSearchField.getTag();
                Object value;
                if (Constants.FORM_DATE_TYPE.equals(mongodbSearchField.getType()) ||
                        FormSystemFieldEnum.CREATE_TIME.getName().equals(mongodbSearchField.getName()) ||
                        FormSystemFieldEnum.UPDATE_TIME.getName().equals(mongodbSearchField.getName()) ||
                        FormFieldTypeEnum.ADDRESS_SELECTION.getFieldType().equals(mongodbSearchField.getType())) {
                    JSONObject dateType = new JSONObject();
                    List<String> splitList =
                            Arrays.stream(mongodbSearchField.getGroupType().split("_")).collect(Collectors.toList());
                    for (String split : splitList) {
                        dateType.put(split.toLowerCase(), jsonObject.get(tag + "_" + split.toLowerCase()));
                    }
                    value = dateType;
                } else {
                    value = jsonObject.get(tag);
                }
                values.add(value);
                xMap.put(tag, value);
            }
            jsonObject.put(labName, StringUtils.join(values, "||"));
            xMapList.add(xMap);
        }
        return xMapList.stream().distinct().collect(Collectors.toList());
    }

    private static void calculateCol(MongodbWidget widget,
                                     List<MongodbAggregateMetricsData> mongodbAggregateMetricsDataList,
                                     MongodbAggregateAllVO mongodbAggregateAllVO) {
        if (widget.getShowSummary() != null && widget.getShowSummary().getShouldShowSummaryCol()) {
            Map<String, List<MongodbAggregateMetricsData>> tagMap = mongodbAggregateMetricsDataList.stream()
                    .collect(Collectors.groupingBy(MongodbAggregateMetricsData::getTag));
            List<MongodbSearchField> metricList = widget.getMetricList();
            for (MongodbSearchField mongodbSearchField : metricList) {
                List<MongodbAggregateMetricsData> list = tagMap.get(mongodbSearchField.getTag());
                MongodbAggregateMetricsData firstRow = list.get(0);
                MongodbAggregateMetricsData summaryCol = new MongodbAggregateMetricsData();
                summaryCol.setName(firstRow.getName());
                summaryCol.setLabel(firstRow.getLabel());
                summaryCol.setTag(firstRow.getTag());
                List<Object> dataList = getList(list, firstRow);
                summaryCol.setData(dataList);
                BigDecimal sum = BigDecimal.ZERO;
                for (Object data : dataList) {
                    if (data != null) {
                        sum = sum.add(new BigDecimal(data.toString()));
                    }
                }
                summaryCol.setSum(sum);
                summaryCol.setSummaryCol(true);
                mongodbAggregateMetricsDataList.add(summaryCol);
            }
        }
    }

    private static List<Object> getList(List<MongodbAggregateMetricsData> list, MongodbAggregateMetricsData firstRow) {
        List<Object> dataList = new ArrayList<>();
        for (int i = 0; i < firstRow.getData().size(); i++) {
            BigDecimal sum = BigDecimal.ZERO;
            for (MongodbAggregateMetricsData mongodbAggregateMetricsData : list) {
                Object value = mongodbAggregateMetricsData.getData().get(i);
                if (value != null) {
                    sum = sum.add(new BigDecimal(value.toString()));
                }
            }
            dataList.add(sum);
        }
        return dataList;
    }

    private static void limit(MongodbWidget widget, MongodbAggregateData mongodbAggregateData) {
        List<MongodbAggregateMetricsData> mongodbAggregateMetricsDataList = mongodbAggregateData.getVal();
        if (widget.getShowTopNum() != null && widget.getShowTopNum().getEnable() &&
                widget.getShowTopNum().getLimit() != null) {
            int limit = widget.getShowTopNum().getLimit();
            if ("PIE_CHART".equals(widget.getType())) {
                if (mongodbAggregateMetricsDataList.size() > limit) {
                    List<MongodbAggregateMetricsData> returnList = mongodbAggregateMetricsDataList.subList(0, limit);
                    List<MongodbAggregateMetricsData> otherList =
                            mongodbAggregateMetricsDataList.subList(limit, mongodbAggregateMetricsDataList.size());
                    MongodbAggregateMetricsData mongodbAggregateMetricsData =
                            mongodbAggregateMetricsDataList.get(limit - 1);
                    MongodbAggregateMetricsData other = new MongodbAggregateMetricsData();
                    other.setOtherCol(true);
                    other.setSum(otherList.stream().mapToDouble(c -> Double.parseDouble(c.getSum().toString())).sum());
                    other.setTag(mongodbAggregateMetricsData.getTag());
                    other.setName(mongodbAggregateMetricsData.getName());
                    other.setLabel(mongodbAggregateMetricsData.getLabel());
                    other.setData(Collections.singletonList(other.getSum()));
                    returnList.add(other);
                    mongodbAggregateData.setVal(returnList);
                }
            } else if ("METRIC_TABLE".equals(widget.getType())) {
                for (MongodbAggregateMetricsData mongodbAggregateMetricsData : mongodbAggregateMetricsDataList) {
                    if (mongodbAggregateMetricsData.getData().size() > limit) {
                        mongodbAggregateMetricsData.setData(
                                mongodbAggregateMetricsData.getData().subList(0, widget.getShowTopNum().getLimit()));
                    }
                }
                for (JSONObject jsonObject : mongodbAggregateData.getX()) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.size() > limit) {
                        jsonObject.put("data", jsonArray.subList(0, widget.getShowTopNum().getLimit()));
                    }
                }
            }
        }
    }

    public List<MongodbAggregateMetricsData> buildReturnData(MongodbWidget widget, List<Map<String, Object>> yMapList,
                                                             Map<String, Map<Object, JSONObject>> xyValueMap,
                                                             List<Map<String, Object>> xMapList) {
        List<MongodbAggregateMetricsData> mongodbAggregateMetricsDataList = new ArrayList<>();
        List<MongodbSearchField> metricList = widget.getMetricList();
        for (Map<String, Object> yMap : yMapList) {
            for (MongodbSearchField mongodbSearchField : metricList) {
                List<Object> yValueList = buildLatObjectValueList(yMap, widget.getFieldyList());
                Map<Object, JSONObject> yListMap = xyValueMap.get(StringUtils.join(yValueList, "||"));
                List<Object> data = new ArrayList<>();
                MongodbAggregateMetricsData mongodbAggregateMetricsData = new MongodbAggregateMetricsData();
                mongodbAggregateMetricsData.setName(mongodbSearchField.getName());
                mongodbAggregateMetricsData.setLabel(mongodbSearchField.getLabel());
                mongodbAggregateMetricsData.setTag(mongodbSearchField.getTag());
                if ("PIE_CHART".equals(widget.getType()) && yValueList.isEmpty()) {
                    yValueList.add(mongodbSearchField.getLabel());
                }
                mongodbAggregateMetricsData.setY(yValueList);
                BigDecimal sum = BigDecimal.ZERO;
                for (Map<String, Object> xMap : xMapList) {
                    List<Object> xValueList = buildLatObjectValueList(xMap, widget.getFieldxList());
                    JSONObject jsonObject = yListMap.get(StringUtils.join(xValueList, "||"));
                    Object val = getObjects(jsonObject, mongodbSearchField);
                    if (val != null) {
                        sum = sum.add(new BigDecimal(val.toString()));
                    }
                    data.add(val);
                }
                mongodbAggregateMetricsData.setDataGroup(mongodbSearchField.getDataGroup());
                mongodbAggregateMetricsData.setData(data);
                mongodbAggregateMetricsData.setSum(sum);
                mongodbAggregateMetricsDataList.add(mongodbAggregateMetricsData);
            }
        }
        return mongodbAggregateMetricsDataList;
    }

    /**
     * 高级计算
     *
     * @param widget
     * @param metricsDataList
     */
    private void highCalculate(MongodbWidget widget, List<MongodbAggregateMetricsData> metricsDataList,
                               List<JSONObject> x) {
        for (MongodbSearchField mongodbSearchField : widget.getMetricList()) {
            List<MongodbAggregateMetricsData> metricsDatas =
                    metricsDataList.stream().filter(c -> c.getTag().equals(mongodbSearchField.getTag()))
                            .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(metricsDatas)) {
                continue;
            }
            int size = metricsDatas.get(0).getData().size();
            if (StringUtils.isNotEmpty(mongodbSearchField.getGrowth())) {
                int index = IntStream.range(0, widget.getFieldxList().size())
                        .filter(c -> FormFieldTypeEnum.INPUT_DATE.getFieldType()
                                .equals(widget.getFieldxList().get(c).getType())).findFirst().orElse(-1);
                if (MongodbSearchFieldGroupTypeEnum.groupTypeList().contains(mongodbSearchField.getGrowth())) {
                    MongodbSearchFieldGroupTypeEnum groupTypeEnum =
                            MongodbSearchFieldGroupTypeEnum.valueOf(mongodbSearchField.getGrowth());
                    JSONObject jsonObject = x.get(index);
                    List<FormAggregateDate> formAggregateDateList =
                            JSONArray.parseArray(jsonObject.getString("data"), FormAggregateDate.class);
                    for (MongodbAggregateMetricsData mongodbAggregateMetricsData : metricsDatas) {
                        List<Object> data = mongodbAggregateMetricsData.getData();
                        List<Object> calculateList = new ArrayList<>();
                        HashMap<Date, Object> dateToDataMap = new HashMap<>();
                        for (int i = 0; i < size; i++) {
                            FormAggregateDate formAggregateDate = formAggregateDateList.get(i);
                            formAggregateDate.setTransDate(formAggregateDate.getDate());
                            formAggregateDate.setTransNextDate(
                                    formAggregateDate.getNextDate(groupTypeEnum.getTransType().name()));
                            dateToDataMap.put(formAggregateDate.getTransDate(), data.get(i));
                        }
                        for (int i = 0; i < size; i++) {
                            FormAggregateDate formAggregateDate = formAggregateDateList.get(i);
                            Object object = dateToDataMap.get(formAggregateDate.getTransNextDate());
                            Object initial = mongodbAggregateMetricsData.getData().get(i);
                            if (object == null || initial == null) {
                                calculateList.add(i, null);
                            } else {
                                BigDecimal bigDecimal = new BigDecimal(object.toString());
                                BigDecimal calculate = new BigDecimal(initial.toString()).subtract(bigDecimal);
                                if (groupTypeEnum.getNeedDivide()) {
                                    if (bigDecimal.compareTo(BigDecimal.ZERO) != 0) {
                                        calculate = calculate.divide(bigDecimal, 8, RoundingMode.HALF_UP);
                                    }
                                }
                                calculateList.add(i, calculate);
                            }
                        }
                        mongodbAggregateMetricsData.setData(calculateList);
                    }
                } else {
                    for (MongodbAggregateMetricsData mongodbAggregateMetricsData : metricsDatas) {
                        MongodbSearchFieldGroupTypeEnum groupTypeEnum =
                                MongodbSearchFieldGroupTypeEnum.valueOf(mongodbSearchField.getGrowth());
                        List<Object> data = mongodbAggregateMetricsData.getData();
                        List<Object> calculateList = new ArrayList<>();
                        calculateList.add(null);
                        for (int i = 1; i < size; i++) {
                            Object object = data.get(i);
                            Object initial = data.get(i - 1);
                            if (object == null || initial == null) {
                                calculateList.add(i, null);
                            } else {
                                BigDecimal nextValue = new BigDecimal(object.toString());
                                BigDecimal value = new BigDecimal(initial.toString());
                                BigDecimal calculate = nextValue.subtract(value);
                                if (groupTypeEnum.getNeedDivide()) {
                                    if (value.compareTo(BigDecimal.ZERO) == 0) {
                                        calculate = null;
                                    } else {
                                        BigDecimal calculateValue = calculate.divide(value, 8, RoundingMode.HALF_UP);
                                        if (nextValue.compareTo(value) > 0) {
                                            calculate = calculateValue.abs();
                                        } else if (nextValue.compareTo(value) < 0) {
                                            calculate = calculateValue.abs().negate();
                                        } else if (nextValue.compareTo(value) == 0) {
                                            calculate = BigDecimal.ZERO;
                                        }
                                    }
                                }
                                calculateList.add(i, calculate);
                            }
                        }
                        mongodbAggregateMetricsData.setData(calculateList);
                    }
                }
            } else {
                if (mongodbSearchField.getHasPercent()) {
                    if (!widget.getFieldxList().isEmpty() && !widget.getFieldyList().isEmpty()) {
                        for (int i = 0; i < size; i++) {
                            proportion(metricsDataList, mongodbSearchField, i);
                        }
                    } else if (!widget.getFieldxList().isEmpty()) {
                        if (widget.getFieldxList().size() == 1) {
                            for (int i = 0; i < size; i++) {
                                proportionOnlyX(metricsDataList, mongodbSearchField, i);
                            }
                        } else {
                            for (MongodbAggregateMetricsData mongodbAggregateMetricsData : metricsDataList) {
                                Map<String, Double> sumMap = new HashMap<>();
                                Map<Integer, String> indexMap = new HashMap<>();
                                buildSumAndIndex(x, mongodbAggregateMetricsData, size, sumMap, indexMap);
                                proportionOnlyXMany(mongodbAggregateMetricsData, mongodbSearchField, sumMap, indexMap);
                            }
                        }
                    } else if (!widget.getFieldyList().isEmpty()) {
                        if (widget.getFieldyList().size() == 1) {
                            for (int i = 0; i < size; i++) {
                                proportionOnlyY(metricsDataList, mongodbSearchField, i);
                            }
                        }
                    }
                    if (!"PIE_CHART".equals(widget.getType())) {
                        for (MongodbAggregateMetricsData mongodbAggregateMetricsData : metricsDataList) {
                            if (mongodbAggregateMetricsData.getSum() != null) {
                                mongodbAggregateMetricsData.setSum(1);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void buildSumAndIndex(List<JSONObject> x, MongodbAggregateMetricsData mongodbAggregateMetricsData,
                                         int size, Map<String, Double> sumMap, Map<Integer, String> indexMap) {
        for (int i = 0; i < size; i++) {
            List<String> keyList = new ArrayList<>();
            for (int xi = 0; xi < x.size() - 1; xi++) {
                JSONObject jsonObject = x.get(xi);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                Object object = jsonArray.get(i);
                keyList.add(object.toString());
            }
            Object object = mongodbAggregateMetricsData.getData().get(i);
            String key = StringUtils.join(keyList, "||");
            if (object != null) {
                Double v = sumMap.get(key);
                if (v == null) {
                    sumMap.put(key, Double.valueOf(object.toString()));
                } else {
                    sumMap.put(key, Double.parseDouble(object.toString()) + v);
                }
            }
            indexMap.put(i, key);
        }
    }

    private void proportionOnlyXMany(MongodbAggregateMetricsData mongodbAggregateMetricsData,
                                     MongodbSearchField mongodbSearchField, Map<String, Double> sumMap,
                                     Map<Integer, String> indexMap) {

        for (int i = 0; i < mongodbAggregateMetricsData.getData().size(); i++) {
            if (mongodbSearchField.getHasPercent()) {
                String key = indexMap.get(i);
                double sum = sumMap.get(key);
                Object data = mongodbAggregateMetricsData.getData().get(i);
                if (data != null) {
                    mongodbAggregateMetricsData.getData().remove(i);
                    double calculate = Double.parseDouble(data.toString()) / sum;
                    mongodbAggregateMetricsData.getData()
                            .add(i, new BigDecimal(calculate).setScale(8, RoundingMode.HALF_UP));
                }
            }
        }
    }


    private void proportionOnlyX(List<MongodbAggregateMetricsData> mongodbAggregateMetricsDataList,
                                 MongodbSearchField mongodbSearchField, int i) {

        // 计算占比
        if (mongodbSearchField.getHasPercent()) {
            for (MongodbAggregateMetricsData mongodbAggregateMetricsData : mongodbAggregateMetricsDataList) {
                if (!mongodbSearchField.getTag().equals(mongodbAggregateMetricsData.getTag())) {
                    continue;
                }
                double sum = Double.parseDouble(mongodbAggregateMetricsData.getSum().toString());
                Object data = mongodbAggregateMetricsData.getData().get(i);
                if (data != null) {
                    mongodbAggregateMetricsData.getData().remove(i);
                    double calculate = Double.parseDouble(data.toString()) / sum;
                    mongodbAggregateMetricsData.getData()
                            .add(i, new BigDecimal(calculate).setScale(8, RoundingMode.HALF_UP));
                }
            }
        }
    }

    private void proportionOnlyY(List<MongodbAggregateMetricsData> mongodbAggregateMetricsDataList,
                                 MongodbSearchField mongodbSearchField, int i) {

        // 计算占比
        if (mongodbSearchField.getHasPercent()) {
            BigDecimal sum = BigDecimal.ZERO;
            for (MongodbAggregateMetricsData mongodbAggregateMetricsData : mongodbAggregateMetricsDataList) {
                Object data = mongodbAggregateMetricsData.getData().get(i);
                if (data != null) {
                    sum = sum.add(new BigDecimal(data.toString()));
                }
            }
            for (MongodbAggregateMetricsData mongodbAggregateMetricsData : mongodbAggregateMetricsDataList) {
                if (mongodbAggregateMetricsData.getSum() != null) {
                    mongodbAggregateMetricsData.setSum(
                            new BigDecimal(mongodbAggregateMetricsData.getSum().toString()).divide(sum, 8,
                                    RoundingMode.HALF_UP));
                }
            }
        }
    }

    private static void proportion(List<MongodbAggregateMetricsData> mongodbAggregateMetricsDataList,
                                   MongodbSearchField mongodbSearchField, int i) {
        double sum = 0d;
        // 计算合计算列
        for (MongodbAggregateMetricsData mongodbAggregateMetricsData : mongodbAggregateMetricsDataList) {
            Object data = mongodbAggregateMetricsData.getData().get(i);
            if (data != null) {
                sum = sum + Double.parseDouble(data.toString());
            }
        }
        // 计算占比
        if (mongodbSearchField.getHasPercent()) {
            for (MongodbAggregateMetricsData mongodbAggregateMetricsData : mongodbAggregateMetricsDataList) {
                Object data = mongodbAggregateMetricsData.getData().get(i);
                if (data != null) {
                    mongodbAggregateMetricsData.getData().remove(i);
                    double calculate = Double.parseDouble(data.toString()) / sum;
                    mongodbAggregateMetricsData.getData()
                            .add(i, new BigDecimal(calculate).setScale(8, RoundingMode.HALF_UP));
                }
            }
        }
    }

    private Object getObjects(JSONObject jsonObject, MongodbSearchField mongodbSearchField) {
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.get(mongodbSearchField.getTag());
    }

    private List<Object> buildLatObjectValueList(Map<String, Object> objectMap, List<MongodbSearchField> fieldxList) {
        List<Object> values = new ArrayList<>();
        for (MongodbSearchField mongodbSearchField : fieldxList) {
            Object value = objectMap.get(mongodbSearchField.getTag());
            values.add(value);
        }
        return values;
    }

    private static void buildFilter(MongodbAggregateRequest mongodbAggregateRequest, List<Criteria> criteriaList) {
        List<Criteria> searchList = new ArrayList<>();
        addCriteria(mongodbAggregateRequest.getFilter(), searchList);
        addCriteria(mongodbAggregateRequest.getWidget().getFilter(), searchList);
        if (CollectionUtils.isNotEmpty(searchList)) {
            Criteria criteria = new Criteria();
            criteria.andOperator(searchList);
            criteriaList.add(criteria);
        }
    }

    private static void addCriteria(MongodbSearchFilter mongodbSearchFilter, List<Criteria> criteriaList) {
        if (mongodbSearchFilter != null) {
            if (CollectionUtils.isNotEmpty(mongodbSearchFilter.getConditionList())) {
                Criteria criteriaByFilter =
                        MongoSearchUtils.buildCriteriaByFilter(mongodbSearchFilter, new ArrayList<>());
                if (criteriaByFilter != null) {
                    criteriaList.add(criteriaByFilter);
                }
            }
        }
    }

    private void setCriteriaList(MongodbDetailedListRequest mongodbDetailedListRequest, List<Criteria> criteriaList) {
        MongodbWidget widget = mongodbDetailedListRequest.getWidget();
        List<Criteria> searchList = new ArrayList<>();
        // MongoSearchUtils.buildCommonFilter(query);
        buildFilter(searchList, mongodbDetailedListRequest, widget);
        if (CollectionUtils.isNotEmpty(searchList)) {
            Criteria criteria = new Criteria();
            criteria.andOperator(searchList);
            criteriaList.add(criteria);
        }
    }

    private static void buildFilter(List<Criteria> criteriaList, MongodbDetailedListRequest mongodbDetailedListRequest,
                                    MongodbWidget widget) {
        Criteria criteriaByFilter =
                MongoSearchUtils.buildCriteriaByFilter(mongodbDetailedListRequest.getFilter(), new ArrayList<>());
        if (criteriaByFilter != null) {
            criteriaList.add(criteriaByFilter);
        }
        Criteria criteriaByFilter1 = MongoSearchUtils.buildCriteriaByFilter(widget.getFilter(), new ArrayList<>());
        if (criteriaByFilter1 != null) {
            criteriaList.add(criteriaByFilter1);
        }
    }

    private static void buildProject(List<MongodbSearchField> fields, MongodbWidget widget,
                                     List<AggregationOperation> aggregationList) {
        List<Field> fieldIdList = new ArrayList<>();
        List<MongodbSearchField> formatList = new ArrayList<>();
        for (MongodbSearchField mongodbSearchField : fields) {
            if ("custom_field".equals(mongodbSearchField.getType())) {
                formatList.add(mongodbSearchField);
            }
            String fieldId = MongoSearchUtils.getFieldIdNotExistLogic(mongodbSearchField.getName(),
                    mongodbSearchField.getType());
            fieldIdList.add(Fields.field(fieldId, fieldId));
        }

        if (CollectionUtils.isNotEmpty(widget.getFormulas())) {
            Map<String, MongodbAggregateFormula> nameToMap =
                    widget.getFormulas().stream().collect(Collectors.toMap(MongodbAggregateFormula::getName, c -> c));
            MongoFormulaRunner mongoFormulaRunner = new MongoFormulaRunner();
            AddFieldsOperation.AddFieldsOperationBuilder addFields = Aggregation.addFields();
            for (MongodbSearchField mongodbSearchField : formatList) {
                MongodbAggregateFormula mongodbAggregateFormula = nameToMap.get(mongodbSearchField.getName());
                String fieldId = MongoSearchUtils.getFieldId(mongodbAggregateFormula.getName(), "");
                if (mongodbAggregateFormula.getAdvancedFormula()) {
                    try {
                        Object functionValue = getFunctionValue(mongodbAggregateFormula, mongoFormulaRunner,
                                mongodbAggregateFormula.getFormula());
                        if (functionValue == null) {
                            continue;
                        }
                        if (functionValue instanceof Document) {
                            AggregationExpression expression = AggregationExpression.from(
                                    MongoExpression.create(JSONObject.toJSONString(functionValue)));
                            addFields = addFields.addField(fieldId).withValue(expression);
                        } else {
                            addFields = addFields.addField(fieldId).withValue(functionValue);
                        }
                    } catch (Exception e) {
                        log.error("公式转换失败", e);
                        throw new ServiceException(ServiceResultCode.FORMULA_ERROR, e.getCause().getMessage());
                    }
                } else {
                    addFields = addFields.addField(fieldId)
                            .withValueOfExpression(mongodbAggregateFormula.getFormula().replaceAll("\\$", ""));
                }
            }
            aggregationList.add(addFields.build());
        }
        Fields from = Fields.from(fieldIdList.get(0));
        for (int i = 1; i < fieldIdList.size(); i++) {
            from = from.and(fieldIdList.get(i));
        }
        ProjectionOperation project = Aggregation.project(from);
        project.andExclude("_id");
        aggregationList.add(project);
    }
}
