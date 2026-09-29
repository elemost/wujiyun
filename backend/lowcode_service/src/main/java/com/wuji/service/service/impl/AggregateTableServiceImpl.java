package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.DefaultContext;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.PostService;
import com.wuji.admin.service.UserService;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.SnowFlakeIdUtils;
import com.wuji.service.constant.Constants;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.express.MongoFormulaRunner;
import com.wuji.service.model.info.FormAggregateTable;
import com.wuji.service.model.info.FormAggregateTableField;
import com.wuji.service.model.info.FormAggregateTableRelation;
import com.wuji.service.model.info.FormAggregateTableValField;
import com.wuji.service.model.info.MongodbAggregateData;
import com.wuji.service.model.info.MongodbAggregateMetricsData;
import com.wuji.service.model.info.MongodbSearchCondition;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.mongo.ProjectAggregation;
import com.wuji.service.model.mongo.UnionWithAggregation;
import com.wuji.service.model.vo.AggregateTableAggregateVO;
import com.wuji.service.model.vo.FormAggregateMongoVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.AggregateTableService;
import com.wuji.service.service.FormService;
import com.wuji.service.utils.MongoDataUtils;
import com.wuji.service.utils.MongoFunctionUtils;
import com.wuji.service.utils.MongoSearchUtils;
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
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AggregateTableServiceImpl implements AggregateTableService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FormService formService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Override
    public Object aggregateTable(FormAggregateTable formAggregateTable, MongodbSearchFilter filter) {
        FormAggregateMongoVO formAggregateMongoVO = buildAggregate(formAggregateTable);
        List<AggregationOperation> aggregationList = formAggregateMongoVO.getAggregationList();
        if (filter != null) {
            Criteria criteria = MongoSearchUtils.buildCriteriaByFilter(filter, new ArrayList<>());
            MatchOperation match = Aggregation.match(criteria);
            aggregationList.add(match);
        }
        Aggregation aggregation = Aggregation.newAggregation(aggregationList);
        AggregationResults<JSONObject> aggregate =
                mongoTemplate.aggregate(aggregation, formAggregateMongoVO.getTableName(), JSONObject.class);
        List<JSONObject> mappedResults = aggregate.getMappedResults();
        List<JSONObject> dealList = new ArrayList<>();
        for (JSONObject jsonObject : mappedResults) {
            dealList.add(jsonObject.getJSONObject("instValue"));
        }
        setUserDept(formAggregateTable.getFieldXs(), dealList);
        MongodbAggregateData mongodbAggregateData = dealData(formAggregateTable, dealList);
        AggregateTableAggregateVO aggregateTableAggregateVO = new AggregateTableAggregateVO();
        aggregateTableAggregateVO.setMongodbAggregateData(mongodbAggregateData);
        aggregateTableAggregateVO.setFieldXs(formAggregateTable.getFieldXs());
        aggregateTableAggregateVO.setValFields(formAggregateTable.getValFields());
        aggregateTableAggregateVO.setJoinedFields(formAggregateTable.getJoinedFields());
        return mongodbAggregateData;
    }

    private void setUserDept(List<FormAggregateTableField> allFieldList, List<JSONObject> mappedResults) {
        List<Long> userList = new ArrayList<>();
        List<Long> deptList = new ArrayList<>();
        List<Long> roleIdList = new ArrayList<>();
        for (FormAggregateTableField mongodbSearchField : allFieldList) {
            for (JSONObject jsonObject : mappedResults) {
                if (FormFieldTypeEnum.getDeptFieldType().contains(mongodbSearchField.getType())) {
                    Object deptId = jsonObject.get(mongodbSearchField.getTag());
                    if (deptId != null) {
                        deptList.add(Long.valueOf(deptId.toString()));
                    }
                } else if (FormFieldTypeEnum.getUserFieldType().contains(mongodbSearchField.getType()) ||
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
        List<DepartmentVO> departmentVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(deptList)) {
            departmentVOList = departmentService.queryListByIdList(deptList);
        }
        List<UserVO> userVOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userList)) {
            userVOS = userService.queryByIds(userList);
        }
        List<PostVO> postVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            postVOList = postService.getAllPost();
        }

        Map<Long, String> deptNameMap =
                departmentVOList.stream().collect(Collectors.toMap(DepartmentVO::getDeptId, DepartmentVO::getDeptName));
        Map<Long, String> userNameMap =
                userVOS.stream().collect(Collectors.toMap(UserVO::getUserId, UserVO::getNickName));
        Map<Long, String> postNameMap =
                postVOList.stream().collect(Collectors.toMap(PostVO::getPostId, PostVO::getPostName));
        for (FormAggregateTableField mongodbSearchField : allFieldList) {
            for (JSONObject jsonObject : mappedResults) {
                if (FormFieldTypeEnum.getDeptFieldType().contains(mongodbSearchField.getType())) {
                    Object deptId = jsonObject.get(mongodbSearchField.getTag());
                    if (deptId != null) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("deptId", deptId.toString());
                        jsonObject1.put("name", deptNameMap.getOrDefault(Long.valueOf(deptId.toString()), ""));
                        jsonObject.put(mongodbSearchField.getTag(), jsonObject1);
                    } else {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("deptId", null);
                        jsonObject.put(mongodbSearchField.getTag(), jsonObject1);
                    }
                } else if (FormFieldTypeEnum.getUserFieldType().contains(mongodbSearchField.getType()) ||
                        FormSystemFieldEnum.CREATE_NAME.getName().equals(mongodbSearchField.getName())) {
                    Object userId = jsonObject.get(mongodbSearchField.getTag());
                    JSONObject jsonObject1 = new JSONObject();
                    if (userId != null) {
                        jsonObject1.put("userId", userId.toString());
                        jsonObject1.put("name", userNameMap.getOrDefault(Long.valueOf(userId.toString()), ""));
                    } else {
                        jsonObject1.put("userId", null);
                    }
                    jsonObject.put(mongodbSearchField.getTag(), jsonObject1);
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
    public FormAggregateMongoVO buildAggregate(FormAggregateTable formAggregateTable) {
        List<AggregationOperation> aggregationList = new ArrayList<>();
        FormAggregateMongoVO formAggregateMongoVO = new FormAggregateMongoVO();
        List<FormVO> formVOList =
                formService.getByIdList(formAggregateTable.getFormIds(), formAggregateTable.getApplicationId());
        Map<String, String> formIdToNameMap =
                formVOList.stream().collect(Collectors.toMap(FormVO::getId, FormVO::getTableName));
        String mainFormId = formAggregateTable.getFormIds().get(0);
        String mainTableName = formIdToNameMap.get(mainFormId);
        // union and unwind
        Map<String, String> formIdToApplicationIdMap =
                formVOList.stream().collect(Collectors.toMap(FormVO::getId, FormVO::getApplicationId));
        Map<String, String> fieldAliasMap =
                unionAndUnWind(formAggregateTable, mainFormId, aggregationList, formIdToNameMap,
                        formIdToApplicationIdMap);
        // 计算公式
        addField(formAggregateTable, fieldAliasMap, aggregationList);
        // 处理group
        List<Field> fields = group(formAggregateTable, aggregationList);
        // 最终project
        project(formAggregateTable, fields, aggregationList);
        // 处理sort
        sort(formAggregateTable, aggregationList);
        formAggregateMongoVO.setAggregationList(aggregationList);
        formAggregateMongoVO.setTableName(mainTableName);
        return formAggregateMongoVO;
    }

    private MongodbAggregateData dealData(FormAggregateTable formAggregateTable, List<JSONObject> mappedResults) {
        List<FormAggregateTableField> fieldXs = formAggregateTable.getFieldXs();
        List<Map<String, Object>> xMapList = buildLatMap(mappedResults, fieldXs, "xValue");
        List<Map<String, Object>> yMapList = buildLatMap(mappedResults, new ArrayList<>(), "yValue");
        Map<String, Map<Object, JSONObject>> xyValueMap = mappedResults.stream().collect(
                Collectors.groupingBy(c -> c.getString("yValue"), Collectors.toMap(f -> f.get("xValue"), f -> f)));
        List<JSONObject> x = new ArrayList<>();
        MongodbAggregateData mongodbAggregateData = new MongodbAggregateData();
        for (FormAggregateTableField mongodbField : fieldXs) {
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
                buildReturnData(formAggregateTable, yMapList, xyValueMap, xMapList);
        mongodbAggregateData.setVal(mongodbAggregateMetricsDataList);
        return mongodbAggregateData;
    }

    public List<MongodbAggregateMetricsData> buildReturnData(FormAggregateTable formAggregateTable,
                                                             List<Map<String, Object>> yMapList,
                                                             Map<String, Map<Object, JSONObject>> xyValueMap,
                                                             List<Map<String, Object>> xMapList) {
        List<MongodbAggregateMetricsData> mongodbAggregateMetricsDataList = new ArrayList<>();
        List<FormAggregateTableValField> metricList = formAggregateTable.getValFields();
        for (Map<String, Object> yMap : yMapList) {
            for (FormAggregateTableValField formAggregateTableValField : metricList) {
                List<Object> yValueList = buildLatObjectValueList(yMap, formAggregateTable.getFieldYs());
                Map<Object, JSONObject> yListMap = xyValueMap.get(StringUtils.join(yValueList, "||"));
                List<Object> data = new ArrayList<>();
                MongodbAggregateMetricsData mongodbAggregateMetricsData = new MongodbAggregateMetricsData();
                mongodbAggregateMetricsData.setName(formAggregateTableValField.getName());
                mongodbAggregateMetricsData.setLabel(formAggregateTableValField.getText());
                mongodbAggregateMetricsData.setTag(formAggregateTableValField.getTag());
                mongodbAggregateMetricsData.setY(yValueList);
                BigDecimal sum = BigDecimal.ZERO;
                for (Map<String, Object> xMap : xMapList) {
                    List<Object> xValueList = buildLatObjectValueList(xMap, formAggregateTable.getFieldXs());
                    JSONObject jsonObject = yListMap.get(StringUtils.join(xValueList, "||"));
                    Object val = getObjects(jsonObject, formAggregateTableValField);
                    if (val != null) {
                        sum = sum.add(new BigDecimal(val.toString()));
                    }
                    data.add(val);
                }
                mongodbAggregateMetricsData.setData(data);
                mongodbAggregateMetricsData.setSum(sum);
                mongodbAggregateMetricsDataList.add(mongodbAggregateMetricsData);
            }
        }
        return mongodbAggregateMetricsDataList;
    }

    private Object getObjects(JSONObject jsonObject, FormAggregateTableValField formAggregateTableValField) {
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.get(formAggregateTableValField.getTag());
    }

    private List<Object> buildLatObjectValueList(Map<String, Object> objectMap,
                                                 List<FormAggregateTableField> fieldxList) {
        List<Object> values = new ArrayList<>();
        for (FormAggregateTableField mongodbSearchField : fieldxList) {
            Object value = objectMap.get(mongodbSearchField.getTag());
            values.add(value);
        }
        return values;
    }

    private List<Map<String, Object>> buildLatMap(List<JSONObject> mappedResults,
                                                  List<FormAggregateTableField> fieldList, String labName) {
        List<Map<String, Object>> xMapList = new ArrayList<>();
        for (JSONObject jsonObject : mappedResults) {
            Map<String, Object> xMap = new HashMap<>();
            List<Object> values = new ArrayList<>();
            for (FormAggregateTableField mongodbSearchField : fieldList) {
                String tag = mongodbSearchField.getTag();
                Object value = jsonObject.get(tag);
                values.add(value);
                xMap.put(tag, value);
            }
            jsonObject.put(labName, StringUtils.join(values, "||"));
            xMapList.add(xMap);
        }
        return xMapList.stream().distinct().collect(Collectors.toList());
    }

    private static Map<String, String> unionAndUnWind(FormAggregateTable formAggregateTable, String mainFormId,
                                                      List<AggregationOperation> aggregationList,
                                                      Map<String, String> formIdToNameMap,
                                                      Map<String, String> formIdToApplicationIdMap) {
        List<FormAggregateTableRelation> relations = formAggregateTable.getRelations();
        Map<String, Map<String, String>> relationMap = relations.stream().collect(
                Collectors.toMap(FormAggregateTableRelation::getJoinId, FormAggregateTableRelation::getRelation));
        Map<String, List<String>> formFieldMap = new HashMap<>();
        Map<String, String> fieldAliasMap = new HashMap<>();
        // 计算公式用到的字段
        getFormulaField(formAggregateTable, formFieldMap, fieldAliasMap);
        List<FormAggregateTableField> fieldXs = formAggregateTable.getFieldXs();
        // 构建project字段
        List<AggregationOperation> mainProject =
                unionUnwindFilter(relationMap, mainFormId, fieldXs, fieldAliasMap, formAggregateTable.getFilter(),
                        formIdToApplicationIdMap);
        aggregationList.addAll(mainProject);
        for (int i = 1; i < formAggregateTable.getFormIds().size(); i++) {
            String formId = formAggregateTable.getFormIds().get(i);
            String tableName = formIdToNameMap.get(formId);
            List<AggregationOperation> projectAggregation =
                    unionUnwindFilter(relationMap, formId, fieldXs, fieldAliasMap, formAggregateTable.getFilter(),
                            formIdToApplicationIdMap);
            UnionWithAggregation unionWithAggregation = new UnionWithAggregation(tableName,
                    Aggregation.newAggregation(projectAggregation).toPipeline(Aggregation.DEFAULT_CONTEXT));
            aggregationList.add(unionWithAggregation);
        }
        return fieldAliasMap;
    }

    private static void addField(FormAggregateTable formAggregateTable, Map<String, String> fieldAliasMap,
                                 List<AggregationOperation> aggregationList) {
        MongoFormulaRunner mongoFormulaRunner = new MongoFormulaRunner();
        AddFieldsOperation.AddFieldsOperationBuilder addFieldsOperationBuilder = Aggregation.addFields();
        for (FormAggregateTableValField valField : formAggregateTable.getValFields()) {
            DefaultContext<String, Object> context = new DefaultContext<>();
            for (String alias : fieldAliasMap.keySet()) {
                context.put("$" + alias.replaceAll("\\.", "_"), "instValue." + fieldAliasMap.get(alias));
            }
            try {
                Object functionValue =
                        mongoFormulaRunner.execute(valField.getFormula().replaceAll("\\.", "_"), context, null, true,
                                false);
                if (functionValue == null) {
                    throw new ServiceException("字段不存在运算符");
                }
                if (functionValue instanceof Document) {
                    addFieldsOperationBuilder = addFieldsOperationBuilder.addField("$instValue." + valField.getName())
                            .withValue(AggregationExpression.from(
                                    MongoExpression.create(JSONObject.toJSONString(functionValue))));
                } else {
                    addFieldsOperationBuilder = addFieldsOperationBuilder.addField("$instValue." + valField.getName())
                            .withValue("$" + functionValue);
                }
            } catch (Exception e) {
                log.error("公式转换失败", e);
                throw new ServiceException(ServiceResultCode.FORMULA_ERROR, valField.getText(),
                        e.getCause().getMessage());
            }
        }
        aggregationList.add(addFieldsOperationBuilder.build());
    }

    private static void getFormulaField(FormAggregateTable formAggregateTable, Map<String, List<String>> formFieldMap,
                                        Map<String, String> fieldAliasMap) {
        for (FormAggregateTableValField formAggregateTableValField : formAggregateTable.getValFields()) {
            String regexFormat = "\\$([\\w.]+)\\#%s";
            for (String formId : formAggregateTableValField.getForms()) {
                List<String> fieldList = formFieldMap.getOrDefault(formId, new ArrayList<>());
                String regex = String.format(regexFormat, formId);
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(formAggregateTableValField.getFormula());
                // 查找并打印所有匹配的结果
                while (matcher.find()) {
                    String field = matcher.group(1);
                    String fieldAlias = "field_" + SnowFlakeIdUtils.generateStr();
                    fieldAliasMap.put(field + "#" + formId, fieldAlias);
                    fieldList.add(field);
                }
                formFieldMap.put(formId, fieldList);
            }
        }
    }

    private static void project(FormAggregateTable formAggregateTable, List<Field> fields,
                                List<AggregationOperation> aggregationList) {
        List<String> searchNameList = formAggregateTable.getValFields().stream().map(FormAggregateTableValField::getTag)
                .collect(Collectors.toList());
        String[] search = new String[searchNameList.size()];
        ProjectionOperation project = Aggregation.project(searchNameList.toArray(search));
        if (fields.size() == 1) {
            for (Field field : fields) {
                project = project.and("_id").as("instValue." + field.getName());
            }
        } else {
            for (Field field : fields) {
                project = project.and("_id." + field.getName()).as("instValue." + field.getName());
            }
        }
        for (String searchName : searchNameList) {
            project = project.and(searchName).as("instValue." + searchName);
        }
        MongoExpression mongoExpression = MongoExpression.create(
                "{\"$function\": {\"body\": \"function(str) { return hex_md5(JSON.stringify(str)) }\",\"args\": [\"$_id\"],\"lang\": \"js\"}}");
        project = project.and(AggregationExpression.from(mongoExpression)).as(Constants.UUID);
        project = project.andExclude("_id");
        aggregationList.add(project);
    }

    private static void sort(FormAggregateTable formAggregateTable, List<AggregationOperation> aggregationList) {
        List<Sort.Order> sortOrderList = new ArrayList<>();
        for (FormAggregateTableField formAggregateTableField : formAggregateTable.getFieldXs()) {
            MongodbSearchField mongodbSearchField = new MongodbSearchField();
            mongodbSearchField.setName(formAggregateTableField.getName());
            mongodbSearchField.setTag(formAggregateTableField.getTag());
            MongoSearchUtils.sort(mongodbSearchField, "ASC", sortOrderList);
        }
        if (CollectionUtils.isNotEmpty(sortOrderList)) {
            aggregationList.add(Aggregation.sort(Sort.by(sortOrderList)));
        }
    }

    private static List<Field> group(FormAggregateTable formAggregateTable,
                                     List<AggregationOperation> aggregationList) {
        List<Field> fields = new ArrayList<>();
        for (FormAggregateTableField formAggregateTableField : formAggregateTable.getFieldXs()) {
            fields.add(
                    Fields.field(formAggregateTableField.getTag(), "instValue." + formAggregateTableField.getName()));
        }
        Fields from = MongoSearchUtils.fieldToFields(fields);
        GroupOperation group = Aggregation.group(from);
        for (FormAggregateTableValField formAggregateTableValField : formAggregateTable.getValFields()) {
            if ("count".equals(formAggregateTableValField.getCalculate())) {
                group = group.count().as(formAggregateTableValField.getTag());
            } else {
                group = group.sum("$instValue." + formAggregateTableValField.getName())
                        .as(formAggregateTableValField.getTag());
            }
        }
        aggregationList.add(group);
        return fields;
    }

    private static List<AggregationOperation> unionUnwindFilter(Map<String, Map<String, String>> relationMap,
                                                                String formId, List<FormAggregateTableField> fields,
                                                                Map<String, String> fieldAliasMap,
                                                                MongodbSearchFilter mongodbSearchFilter,
                                                                Map<String, String> formIdToApplicationIdMap) {
        // 数据过滤
        List<AggregationOperation> aggregationList = new ArrayList<>();
        List<Criteria> criteriaList = new ArrayList<>();
        MongoSearchUtils.buildCommonFilter(criteriaList, formIdToApplicationIdMap.get(formId), formId);
        if (mongodbSearchFilter != null && CollectionUtils.isNotEmpty(mongodbSearchFilter.getConditionList())) {
            List<MongodbSearchCondition> filterConditionList =
                    mongodbSearchFilter.getConditionList().stream().filter(c -> formId.equals(c.getFormId()))
                            .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(filterConditionList)) {
                MongodbSearchFilter filter = new MongodbSearchFilter();
                filter.setRel(mongodbSearchFilter.getRel());
                filter.setConditionList(filterConditionList);
                Criteria criteria = MongoSearchUtils.buildCriteriaByFilter(filter, new ArrayList<>());
                criteriaList.add(criteria);
            }
        }
        Document document = new Document();
        List<String> unWindField = new ArrayList<>();
        AddFieldsOperation.AddFieldsOperationBuilder fieldsOperationBuilder = Aggregation.addFields();
        boolean existDate = Boolean.FALSE;
        for (FormAggregateTableField formAggregateTableField : fields) {
            String field = getField(relationMap, formAggregateTableField.getName(), formId);
            String fieldId = MongoSearchUtils.getFieldId(field, formAggregateTableField.getType());
            if (FormFieldTypeEnum.INPUT_DATE.getFieldType().equals(formAggregateTableField.getType()) ||
                    FormSystemFieldEnum.CREATE_TIME.getName().equals(fieldId) ||
                    FormSystemFieldEnum.UPDATE_TIME.getName().equals(fieldId)) {
                fieldsOperationBuilder = MongoSearchUtils.addFormattedDateField(fieldsOperationBuilder,
                        formAggregateTableField.getFormat(), fieldId, new ArrayList<>(),
                        formAggregateTableField.getName());
                List<String> splitList =
                        Arrays.stream(formAggregateTableField.getFormat().split("_")).collect(Collectors.toList());
                List<Object> concat = getConcatList(formAggregateTableField, splitList);
                document.append("instValue." + formAggregateTableField.getName(), MongoFunctionUtils.concat(concat));
                existDate = Boolean.TRUE;
            } else {
                document.append("instValue." + formAggregateTableField.getName(), "$" + fieldId);
            }
            criteriaList.add(Criteria.where(fieldId).ne(null));
            // 用户 部门 类型 字段需特殊处理
            if (FormFieldTypeEnum.getUserDeptFieldType().contains(formAggregateTableField.getType())) {
                UnwindOperation unwind = Aggregation.unwind("instValue." + field);
                aggregationList.add(unwind);
            }
            if (StringUtils.isNotEmpty(formAggregateTableField.getSubForm())) {
                if (!unWindField.contains(formAggregateTableField.getSubForm())) {
                    String subFormField = getField(relationMap, formAggregateTableField.getSubForm(), formId);
                    if (subFormField != null) {
                        UnwindOperation unwind = Aggregation.unwind("instValue." + subFormField);
                        aggregationList.add(unwind);
                        unWindField.add(subFormField);
                    }
                }
            }
        }
        for (String field : fieldAliasMap.keySet()) {
            String alias = fieldAliasMap.get(field);
            if (field.contains(formId)) {
                document.append("instValue." + alias, MongoFunctionUtils.ifNull("$instValue." + field.split("#")[0]));
            } else {
                document.append("instValue." + alias, MongoFunctionUtils.defaultZero());
            }

            if (field.contains(".") && field.contains(formId)) {
                String subForm = field.split("\\.")[0];
                if (!unWindField.contains(subForm)) {
                    UnwindOperation unwind = Aggregation.unwind("instValue." + subForm);
                    aggregationList.add(unwind);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            Criteria criteria = new Criteria();
            criteria.andOperator(criteriaList);
            MatchOperation match = Aggregation.match(criteria);
            aggregationList.add(0, match);
        }
        if (existDate) {
            aggregationList.add(fieldsOperationBuilder.build());
        }
        aggregationList.add(new ProjectAggregation(document));
        return aggregationList;
    }

    private static List<Object> getConcatList(FormAggregateTableField formAggregateTableField, List<String> splitList) {
        List<Object> concat = new ArrayList<>();
        int i = 0;
        for (String split : splitList) {
            if (i == 0) {
                concat.add(MongoFunctionUtils.toString(formAggregateTableField.getName() + "_" + split.toLowerCase()));
            } else {
                concat.add("-");
                concat.add(MongoFunctionUtils.toString(formAggregateTableField.getName() + "_" + split.toLowerCase()));
            }
            i++;
        }
        return concat;
    }

    private static String getField(Map<String, Map<String, String>> relationMap, String formAggregateTableField,
                                   String formId) {
        Map<String, String> subFormMap = relationMap.get(formAggregateTableField);
        String subFormField = "";
        if (subFormMap != null) {
            subFormField = subFormMap.get(formId);
        } else {
            subFormField = formAggregateTableField;
        }
        return subFormField;
    }
}
