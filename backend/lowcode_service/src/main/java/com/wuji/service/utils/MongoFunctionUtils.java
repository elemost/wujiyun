package com.wuji.service.utils;

import com.google.common.collect.Lists;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.service.enums.FormFieldGroupRuleEnum;
import com.wuji.service.model.request.factory.DataFactoryRelationRequest;
import org.bson.Document;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MongoFunctionUtils {

    /**
     * let 里面使用 localField
     *
     * @return
     */
    public static Document let(List<DataFactoryRelationRequest> mongoDataFactoryRelationList) {
        Document document = new Document();
        for (DataFactoryRelationRequest mongoDataFactoryRelationRequest : mongoDataFactoryRelationList) {
            String leftField = mongoDataFactoryRelationRequest.getLeftField();
            String aliasLeftField = mongoDataFactoryRelationRequest.getAliasLeftField();
            if (FormFieldTypeEnum.INPUT_DATE.getFieldType()
                    .equals(mongoDataFactoryRelationRequest.getRightFieldType())) {
                leftField = leftField + "_date";
                aliasLeftField = aliasLeftField + "_date";
            }
            document.append(leftField, "$" +
                    MongoSearchUtils.getField(aliasLeftField, "", ""));
        }
        return document;
    }

    public static Document pipeline(List<DataFactoryRelationRequest> mongoDataFactoryRelationList) {
        List<Document> documentList = new ArrayList<>();
        for (DataFactoryRelationRequest mongoDataFactoryRelationRequest : mongoDataFactoryRelationList) {
            documentList.add(eq(mongoDataFactoryRelationRequest));
        }
        return new Document("$match", new Document("$expr", new Document("$and", documentList)));
    }


    /**
     * 左边为当前集合id 右边是本地字段id
     *
     * @param mongoDataFactoryRelationRequest
     * @return
     */
    public static Document eq(DataFactoryRelationRequest mongoDataFactoryRelationRequest) {
        String fieldId = MongoSearchUtils.getField(mongoDataFactoryRelationRequest.getAliasRightField(), "", "");
        String aliasLeftField = mongoDataFactoryRelationRequest.getAliasLeftField();
        if (FormFieldTypeEnum.INPUT_DATE.getFieldType().equals(mongoDataFactoryRelationRequest.getRightFieldType())) {
            fieldId = fieldId + "_date";
            aliasLeftField = aliasLeftField + "_date";
        }
        return new Document("$eq", Arrays.asList("$" + fieldId, "$$" + aliasLeftField));
    }

    public static Document ifNull(Object field) {
        return new Document("$ifNull", Arrays.asList(field, 0));
    }

    public static Document ifNullString(Object field) {
        return new Document("$ifNull", Arrays.asList(field, ""));
    }

    public static Document checkFieldIsNotNullCount(Object field) {
        return new Document("$cond",
                new Document("if", new Document("$eq", Lists.newArrayList(ifNullString(field), ""))).append("then", 0)
                        .append("else", 1));
    }

    public static Document defaultZero() {
        return new Document("$literal", 0);
    }

    public static Document concat(List<Object> concatList) {
        return new Document("$concat", concatList);
    }

    public static Document toString(String value) {
        return new Document("$toString", "$" + value);
    }

    public static AggregationExpression orExpress(String fieldId) {

        String orExpress = "{ $or: [ " + "{ $eq: [ '$fieldId', null ] }, " +  // 情况1：值为null
                "{ $eq: [ '$fieldId', '' ] }, " +   // 情况2：空字符串
                "{ $eq: [ { $type: '$fieldId' }, 'missing' ] } " +  // 情况3：字段不存在
                "] }";
        orExpress = orExpress.replaceAll("fieldId", fieldId);

        return AggregationExpression.from(MongoExpression.create(orExpress));
    }

    public static AggregationExpression subFormAgg(String fieldId, String subForm, String op) {
        String mapExpress =
                "{ $op: { $map: { input: \"$instValue.subForm\", as: \"subForm\", in: \"$$subForm.fieldId\" } } \n" +
                        " }";
        mapExpress = mapExpress.replaceAll("fieldId", fieldId);
        mapExpress = mapExpress.replaceAll("op", op.toLowerCase());
        mapExpress = mapExpress.replaceAll("subForm", subForm);
        return AggregationExpression.from(MongoExpression.create(mapExpress));
    }

    public static AggregationExpression subFormFunctionAgg(String fieldId, String subForm, String op,
                                                           Document document) {
        Document jsonObject = new Document();
        jsonObject.put("$" + op.toLowerCase(), document);
        String mapExpress = jsonObject.toJson();
        mapExpress = mapExpress.replaceAll("instValue\\." + subForm + "\\.", "\\$" + subForm + ".");
        return AggregationExpression.from(MongoExpression.create(mapExpress));
    }

    public static Object getFunction(String formula) {
        AddFieldsOperation addFieldsOperation =
                Aggregation.addFields().addField("fieldId").withValueOfExpression(formula).build();
        Document document = addFieldsOperation.toDocument(Aggregation.DEFAULT_CONTEXT);
        return document.get("$addFields", Document.class).get("fieldId");
    }

    public static Document dateFromPart(Object year, Object month, Object day, Object quarter) {
        Document document = new Document();
        document.put("year", year);
        document.put("timezone", "Asia/Shanghai");
        if (quarter != null) {
            Document multiply = new Document("$multiply", Lists.newArrayList(quarter, 3));
            document.put("month", new Document("$add", Lists.newArrayList(1, multiply)));
            document.put("day", day != null ? day : 1);
        } else {
            document.put("month", month != null ? month : 1);
            document.put("day", day != null ? day : 1);
        }
        Document dateFromParts = new Document("$dateFromParts", document);
        return new Document("$toLong", dateFromParts);
    }

    public static Document dateFromPartWeek(Object year, Object week) {
        Document document = new Document();
        document.put("isoWeekYear", year);
        document.put("timezone", "Asia/Shanghai");
        document.put("isoWeek", week);
        document.put("isoDayOfWeek", 1);
        Document dateFromParts = new Document("$dateFromParts", document);
        return new Document("$toLong", dateFromParts);
    }

    public static Document dateFromPart(String groupType, String tag) {
        Document document = null;
        if (FormFieldGroupRuleEnum.YEAR_QUARTER.name().equals(groupType)) {
            document = dateFromPart("$" + tag + "_year", null, null, null);
        } else if (FormFieldGroupRuleEnum.YEAR_MONTH.name().equals(groupType)) {
            document = MongoFunctionUtils.dateFromPart("$" + tag + "_year", "$" + tag + "_month", null, null);
        } else if (FormFieldGroupRuleEnum.YEAR.name().equals(groupType)) {
            document = dateFromPart("$" + tag + "_year", null, null, null);
        } else if (FormFieldGroupRuleEnum.YEAR_WEEK.name().equals(groupType)) {
            document = dateFromPartWeek("$" + tag + "_year", "$" + tag + "_week");
        }else {
            document = dateFromPart("$" + tag + "_year", "$" + tag + "_month", "$" + tag + "_day", null);
        }
        return document;
    }

    public static Document dateTrunc(String fieldId, String unit) {
        return new Document().append("$dateTrunc",
                new Document().append("date", new Document("$toDate", fieldId)).append("unit", unit)
                        .append("timezone", "Asia/Shanghai"));
    }

    public static List<Document> toDocument(List<AggregationOperation> aggregationOperations) {
        List<Document> documentList = new ArrayList<>();
        for (AggregationOperation aggregationOperation : aggregationOperations) {
            documentList.addAll(aggregationOperation.toPipelineStages(Aggregation.DEFAULT_CONTEXT));
        }
        return documentList;
    }
    public static void main(String[] args) {
        getFunction("text == null");
    }
}
