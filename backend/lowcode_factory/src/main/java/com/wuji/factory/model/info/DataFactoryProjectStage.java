package com.wuji.factory.model.info;

import com.ql.util.express.DefaultContext;
import com.wuji.factory.converter.AbstractFormDataFactoryExecuteConverter;
import com.wuji.factory.model.domain.DataFactoryField;
import com.wuji.service.express.FactoryMongoFormulaRunner;
import com.wuji.service.model.info.MongoSort;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import com.wuji.service.model.request.factory.DataFactoryReturnFieldVO;
import com.wuji.service.model.request.factory.DataFactoryStageGroupFieldRequest;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.utils.MongoFunctionUtils;
import com.wuji.service.utils.MongoSearchUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class DataFactoryProjectStage extends DataFactoryStage {

    private List<DataFactoryField> fields;

    @Override
    public void convert(Map<String, DataFactoryStage> idToStageMap, DataFactoryStage mainStage,
                        Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                        Map<String, FieldExistNameVO> formIdToMap) {
        List<AggregationOperation> aggregationOperationList =
                new ArrayList<>(stageIdToAggregationListMap.getOrDefault(this.getInput().get(0), new ArrayList<>()));
        List<DataFactoryReturnFieldVO> returnFields = new ArrayList<>();
        setQuoteField();
        List<DataFactoryField> dataFactoryFields = sortByQuoteRelation(fields);
        for (DataFactoryField mainField : dataFactoryFields) {
            String aliasName = MongoSearchUtils.getFieldIdNotExistLogic(mainField.getAliasName(), "");
            if ("formula".equals(mainField.getRelFieldType())) {
                addFormula(mainField, aliasName, aggregationOperationList);
            } else if ("summary_add".equals(mainField.getRelFieldType())) {
                summaryAdd(mainField, aggregationOperationList);
            } else if ("group_sort".equals(mainField.getRelFieldType())) {
                MongoSearchUtils.addSortAgg(mainField.getSorts(), aggregationOperationList);
                List<Field> fields = new ArrayList<>();
                List<MongodbSearchField> mongodbSearchFieldList = new ArrayList<>();
                for (DataFactoryStageGroupFieldRequest fieldRequest : mainField.getGroupFields()) {
                    MongodbSearchField mongodbSearchField =
                            com.wuji.service.converter.AbstractFormDataFactoryExecuteConverter.INSTANCE.toField(
                                    fieldRequest);
                    fieldRequest.setTag(fieldRequest.getAliasName());
                    mongodbSearchField.setTag(fieldRequest.getAliasName());
                    mongodbSearchFieldList.add(mongodbSearchField);
                }
                MongoSearchUtils.buildFieldList(fields, mongodbSearchFieldList);
                List<String> groupDateList =
                        MongoSearchUtils.coverDateReturn(mongodbSearchFieldList, aggregationOperationList);
                for (String groupDate : groupDateList) {
                    fields.add(Fields.field(groupDate));
                }
                GroupOperation group = null;
                if (CollectionUtils.isNotEmpty(mainField.getGroupFields())) {
                    Fields from = MongoSearchUtils.fieldToFields(fields);
                    group = Aggregation.group(from);
                } else {
                    group = Aggregation.group();
                }
                group = group.push("$$ROOT").as("docs");
                aggregationOperationList.add(group);
                Document doc = getSortDocument(mainField.getAliasName());
                AggregationExpression aggregationExpression =
                        AggregationExpression.from(MongoExpression.create((doc).toJson()));
                AddFieldsOperation addFieldsOperation =
                        Aggregation.addFields().addField("docs").withValueOf(aggregationExpression).build();
                aggregationOperationList.add(addFieldsOperation);
                Document append =
                        new Document().append("input", "$docs").append("as", "item").append("in", "$$item.instValue");
                ProjectionOperation projectionOperation = Aggregation.project().and(AggregationExpression.from(
                        MongoExpression.create((new Document().append("$map", append)).toJson()))).as("instValue");
                aggregationOperationList.add(projectionOperation);
                aggregationOperationList.add(Aggregation.unwind("instValue"));
            }
        }
        setReturnField(dataFactoryFields, returnFields, idToStageMap);
        stageIdToAggregationListMap.put(this.getId(), aggregationOperationList);
    }

    private void summaryAdd(DataFactoryField mainField, List<AggregationOperation> aggregationOperationList) {
        MongoSearchUtils.addSortAgg(mainField.getSorts(), aggregationOperationList);
        List<Field> fields = new ArrayList<>();
        List<MongodbSearchField> mongodbSearchFieldList = new ArrayList<>();
        for (DataFactoryStageGroupFieldRequest fieldRequest : mainField.getGroupFields()) {
            MongodbSearchField mongodbSearchField =
                    com.wuji.service.converter.AbstractFormDataFactoryExecuteConverter.INSTANCE.toField(fieldRequest);
            fieldRequest.setTag(fieldRequest.getAliasName());
            mongodbSearchField.setTag(fieldRequest.getAliasName());
            mongodbSearchFieldList.add(mongodbSearchField);
        }
        MongoSearchUtils.buildFieldList(fields, mongodbSearchFieldList);
        List<String> groupDateList = MongoSearchUtils.coverDateReturn(mongodbSearchFieldList, aggregationOperationList);
        for (String groupDate : groupDateList) {
            fields.add(Fields.field(groupDate));
        }
        GroupOperation group = null;
        if (CollectionUtils.isNotEmpty(mainField.getGroupFields())) {
            Fields from = MongoSearchUtils.fieldToFields(fields);
            group = Aggregation.group(from);
        } else {
            group = Aggregation.group();
        }
        String sumAliasName = "sum_" + mainField.getName();
        String metricName = MongoSearchUtils.getFieldIdNotExistLogic(mainField.getMetric().getAliasName(), "");
        group = group.push("$$ROOT").as("docs").push(metricName).as(sumAliasName);
        aggregationOperationList.add(group);
        Document doc = getDoc(mainField.getAliasName(), sumAliasName);
        AggregationExpression aggregationExpression =
                AggregationExpression.from(MongoExpression.create((doc).toJson()));
        AddFieldsOperation addFieldsOperation =
                Aggregation.addFields().addField("docs").withValueOf(aggregationExpression).build();
        aggregationOperationList.add(addFieldsOperation);
        Document append = new Document().append("input", "$docs").append("as", "item").append("in", "$$item.instValue");
        ProjectionOperation projectionOperation = Aggregation.project().and(AggregationExpression.from(
                MongoExpression.create((new Document().append("$map", append)).toJson()))).as("instValue");
        aggregationOperationList.add(projectionOperation);
        aggregationOperationList.add(Aggregation.unwind("instValue"));
    }

    private void setReturnField(List<DataFactoryField> dataFactoryFields, List<DataFactoryReturnFieldVO> returnFields,
                                Map<String, DataFactoryStage> idToStageMap) {
        String input = this.getInput().get(0);
        DataFactoryStage dataFactoryStage = idToStageMap.get(input);
        Map<String, DataFactoryReturnFieldVO> aliasNameMap = dataFactoryStage.getReturnFields().stream()
                .collect(Collectors.toMap(DataFactoryReturnFieldVO::getAliasName, c -> c));
        for (DataFactoryField mainField : dataFactoryFields) {
            DataFactoryReturnFieldVO dataFactoryReturnFieldVO =
                    AbstractFormDataFactoryExecuteConverter.INSTANCE.toVO(mainField);
            DataFactoryReturnFieldVO sourceField = aliasNameMap.get(dataFactoryReturnFieldVO.getName());
            if (sourceField != null) {
                dataFactoryReturnFieldVO.setGroupType(sourceField.getGroupType());
                dataFactoryReturnFieldVO.setDisplayFormat(sourceField.getDisplayFormat());
            }
            returnFields.add(dataFactoryReturnFieldVO);
        }
        this.setReturnFields(returnFields);
    }

    private void setQuoteField() {
        for (DataFactoryField field : fields) {
            List<DataStreamQuoteField> quoteFields = new ArrayList<>();
            if ("summary_add".equals(field.getRelFieldType())) {
                List<MongoSort> sorts = field.getSorts();
                for (MongoSort mongoSort : sorts) {
                    DataStreamQuoteField dataStreamQuoteField = new DataStreamQuoteField();
                    dataStreamQuoteField.setQuoteFieldId(mongoSort.getFieldId());
                    quoteFields.add(dataStreamQuoteField);
                }
                for (DataFactoryStageGroupFieldRequest groupField : field.getGroupFields()) {
                    DataStreamQuoteField dataStreamQuoteField = new DataStreamQuoteField();
                    dataStreamQuoteField.setQuoteFieldId(groupField.getAliasName());
                    quoteFields.add(dataStreamQuoteField);
                }
                DataStreamQuoteField dataStreamQuoteField = new DataStreamQuoteField();
                dataStreamQuoteField.setQuoteFieldId(field.getMetric().getAliasName());
                quoteFields.add(dataStreamQuoteField);
                field.setQuoteFields(quoteFields.stream().distinct().collect(Collectors.toList()));
            } else if ("group_sort".equals(field.getRelFieldType())) {
                List<MongoSort> sorts = field.getSorts();
                for (MongoSort mongoSort : sorts) {
                    DataStreamQuoteField dataStreamQuoteField = new DataStreamQuoteField();
                    dataStreamQuoteField.setQuoteFieldId(mongoSort.getFieldId());
                    quoteFields.add(dataStreamQuoteField);
                }
                for (DataFactoryStageGroupFieldRequest groupField : field.getGroupFields()) {
                    DataStreamQuoteField dataStreamQuoteField = new DataStreamQuoteField();
                    dataStreamQuoteField.setQuoteFieldId(groupField.getAliasName());
                    quoteFields.add(dataStreamQuoteField);
                }
                field.setQuoteFields(quoteFields.stream().distinct().collect(Collectors.toList()));
            }
        }
    }

    private static void addFormula(DataFactoryField mainField, String aliasName,
                                   List<AggregationOperation> aggregationOperationList) {
        FactoryMongoFormulaRunner mongoFormulaRunner = new FactoryMongoFormulaRunner();
        try {
            DefaultContext<String, Object> context = new DefaultContext<>();
            if (CollectionUtils.isNotEmpty(mainField.getQuoteFields())) {
                for (DataStreamQuoteField quoteField : mainField.getQuoteFields()) {
                    context.put(quoteField.getId(),
                            MongoSearchUtils.getField(quoteField.getQuoteFieldId(), quoteField.getQuoteSubForm(),
                                    quoteField.getQuoteFieldType()));
                }
            }
            String functionString = mainField.getFunction().replaceAll("&&", "and");
            Object function = mongoFormulaRunner.execute(functionString, context, null, true, false);
            AddFieldsOperation.AddFieldsOperationBuilder addFieldsOperationBuilder = Aggregation.addFields();
            if (function instanceof Document) {
                AggregationExpression from =
                        AggregationExpression.from(MongoExpression.create(((Document) function).toJson()));
                addFieldsOperationBuilder = addFieldsOperationBuilder.addField(aliasName).withValueOf(from);
            } else {
                addFieldsOperationBuilder = addFieldsOperationBuilder.addField(aliasName).withValue(function);
            }
            aggregationOperationList.add(addFieldsOperationBuilder.build());
        } catch (Exception e) {
            log.error("公式错误", e);
        }
    }

    public static List<DataFactoryField> sortByQuoteRelation(List<DataFactoryField> fieldList) {
        // 1. 构建 aliasName -> FieldInfo 的映射，方便快速查找
        Map<String, DataFactoryField> aliasToFieldMap = new HashMap<>();
        for (DataFactoryField field : fieldList) {
            if (field.getAliasName() != null) {
                aliasToFieldMap.put(field.getAliasName(), field);
            }
        }

        // 2. 拓扑排序核心逻辑
        List<DataFactoryField> sortedList = new ArrayList<>();
        Set<DataFactoryField> visited = new HashSet<>(); // 已访问的节点
        Set<DataFactoryField> visiting = new HashSet<>(); // 正在访问的节点（处理循环引用）

        // 遍历所有字段进行排序
        for (DataFactoryField field : fieldList) {
            if (!visited.contains(field)) {
                dfsSort(field, aliasToFieldMap, sortedList, visited, visiting);
            }
        }

        return sortedList;
    }

    /**
     * 深度优先遍历实现拓扑排序
     */
    private static void dfsSort(DataFactoryField currentField, Map<String, DataFactoryField> aliasToFieldMap,
                                List<DataFactoryField> sortedList, Set<DataFactoryField> visited,
                                Set<DataFactoryField> visiting) {
        // 处理循环引用
        if (visiting.contains(currentField)) {
            return; // 发现循环引用，直接返回，避免栈溢出
        }

        if (visited.contains(currentField)) {
            return;
        }

        visiting.add(currentField);

        // 先递归处理当前字段引用的所有字段
        if (currentField.getQuoteFields() != null && !currentField.getQuoteFields().isEmpty()) {
            for (DataStreamQuoteField quoteField : currentField.getQuoteFields()) {
                String quoteFieldId = quoteField.getQuoteFieldId();
                DataFactoryField referencedField = aliasToFieldMap.get(quoteFieldId);
                if (referencedField != null && !visited.contains(referencedField)) {
                    dfsSort(referencedField, aliasToFieldMap, sortedList, visited, visiting);
                }
            }
        }

        // 处理完依赖后，将当前字段加入排序结果
        visiting.remove(currentField);
        visited.add(currentField);
        sortedList.add(currentField);
    }

    public Document getDoc(String metricName, String sumField) {
        return new Document("$map", new Document()
                // 遍历原 docs 数组，同时保留索引
                .append("input", new Document("$range", Arrays.asList(0, new Document("$size", "$docs"))))
                .append("as", "index").append("in", new Document("$let", new Document()
                        // 定义局部变量：当前文档、当前及之前的所有数值
                        .append("vars", new Document()
                                // 当前索引对应的文档
                                .append("currentDoc", new Document("$arrayElemAt", Arrays.asList("$docs", "$$index")))
                                // 截取从0到当前索引的数值数组（用于累加）
                                .append("cumulativeValues", new Document("$slice", Arrays.asList("$" + sumField, 0,
                                        new Document("$add", Arrays.asList("$$index", 1))))))
                        // 计算当前文档的累计值，并合并到 instValue 中
                        .append("in", new Document("$mergeObjects", Arrays.asList("$$currentDoc",
                                new Document("instValue",
                                        new Document("$mergeObjects", Arrays.asList("$$currentDoc.instValue",
                                                // 核心：对截取的前缀数组求和（逐级累加）
                                                new Document(metricName, new Document("$reduce",
                                                        new Document().append("input", "$$cumulativeValues")
                                                                .append("initialValue", 0).append("in",
                                                                        new Document("$add", Arrays.asList("$$value",
                                                                                MongoFunctionUtils.ifNull(
                                                                                        "$$this")))))))))))))));
    }

    public Document getSortDocument(String metricName) {
        return new Document("$map", new Document()
                // $map 的 input：生成 0 到 docs数组长度-1 的索引数组
                .append("input", new Document("$range", Arrays.asList(0, new Document("$size", "$docs"))))
                // $map 的 as 别名
                .append("as", "index")
                // $map 的 in 逻辑（核心嵌套）
                .append("in", new Document("$let", new Document()
                        // 定义局部变量 currentDoc
                        .append("vars", new Document("currentDoc",
                                new Document("$arrayElemAt", Arrays.asList("$docs", "$$index"))))
                        // $let 的 in 逻辑：合并文档 + 注入排序序号
                        .append("in", new Document("$mergeObjects", Arrays.asList(
                                // 第一个元素：原文档 $$currentDoc
                                "$$currentDoc",
                                // 第二个元素：新增 instValue 下的 sort_rank 字段
                                new Document("instValue",
                                        new Document("$mergeObjects", Arrays.asList("$$currentDoc.instValue",
                                                // 核心：对截取的前缀数组求和（逐级累加）
                                                new Document(metricName,
                                                        new Document("$add", Arrays.asList("$$index", 1))))))))))));
    }

    @Override
    public void sort(List<DataFactoryStage> dataFactoryStageList, Map<String, DataFactoryStage> idToStageMap) {
        dataFactoryStageList.add(0, this);
        DataFactoryStage dataFactoryStage = idToStageMap.get(this.getInput().get(0));
        dataFactoryStage.sort(dataFactoryStageList, idToStageMap);
    }

    @Override
    public String getFormId(Map<String, DataFactoryStage> idToStageMap) {
        DataFactoryStage dataFactoryStage = idToStageMap.get(this.getInput().get(0));
        return dataFactoryStage.getFormId(idToStageMap);
    }
}
