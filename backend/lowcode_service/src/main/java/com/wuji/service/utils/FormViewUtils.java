package com.wuji.service.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.service.model.info.FormCalendarViewConfig;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.request.FormViewCalendarRequest;
import com.wuji.service.model.vo.FormFieldGroupInfoVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FormViewUtils {
    /**
     * 【递归核心】动态构建任意层级分组
     *
     * @param docs        数据集
     * @param groupFields 分组配置
     * @param level       当前层级（0=第一层，1=第二层...）
     */
    public static FormFieldGroupInfoVO buildDynamicGroup(List<JSONObject> docs, List<MongodbSearchField> groupFields,
                                                         int level, List<MongodbSearchField> metricList,
                                                         Map<String, FormConfigCommon> nameToMap) {
        FormFieldGroupInfoVO group = new FormFieldGroupInfoVO();
        group.setTotal(docs.size());

        // 没有更多分组层级 → 直接计算汇总（叶子节点）
        if (level >= groupFields.size()) {
            group.setTotal(groupFields.size());
            group.setSubGroups(Collections.emptyList());
            JSONObject jsonObject = docs.get(0);
            if (CollectionUtils.isNotEmpty(metricList)) {
                for (MongodbSearchField metric : metricList) {
                    group.getSummaries().put(metric.getTag(), jsonObject.getBigDecimal(metric.getTag()));
                }
            }
            return group;
        }

        // ===================== 动态获取当前分组字段 =====================
        MongodbSearchField currentGroupField = groupFields.get(level);
        String fieldId = currentGroupField.getName();    // 字段ID：text_ml7stb0l
        String tag = currentGroupField.getTag();        // tag：t_9add24d0
        group.setFieldId(fieldId);
        group.setFormConfigCommon(nameToMap.get(fieldId));
        // ===================== 按 _id[tag] 分组（核心！） =====================
        Map<String, List<JSONObject>> groupByValue = new LinkedHashMap<>();
        for (JSONObject dataGroup : docs) {
            JSONObject idMap = dataGroup.getJSONObject("_id");
            String groupValue = idMap.get(tag) == null ? "" : idMap.get(tag).toString();
            groupByValue.computeIfAbsent(groupValue, k -> new ArrayList<>()).add(dataGroup);
        }

        // ===================== 递归构建子分组 =====================
        List<FormFieldGroupInfoVO> subGroups = new ArrayList<>();
        for (Map.Entry<String, List<JSONObject>> entry : groupByValue.entrySet()) {
            List<JSONObject> value = entry.getValue();
            FormFieldGroupInfoVO subGroup = buildDynamicGroup(value, groupFields, level + 1, metricList,  nameToMap);
            subGroup.setValue(entry.getKey());
            JSONArray jsonArray = value.get(0).getJSONArray("docs");
            if (jsonArray != null) {
                subGroup.setTotal(jsonArray.size());
            } else {
                subGroup.setTotal(0);
            }
            // ===================== 动态获取当前分组字段 =====================
            subGroup.setFieldId(fieldId);
            subGroup.setFormConfigCommon(nameToMap.get(fieldId));
            subGroups.add(subGroup);
        }
        group.setSummaries(mergeSummary(subGroups));
        group.setSubGroups(subGroups);
        return group;
    }

    // 合并子分组汇总（向上汇总）
    private static Map<String, BigDecimal> mergeSummary(List<FormFieldGroupInfoVO> subNodes) {
        Map<String, BigDecimal> total = new HashMap<>();
        for (FormFieldGroupInfoVO sub : subNodes) {
            if (sub.getSummaries() == null) {
                continue;
            }
            sub.getSummaries().forEach((k, v) -> total.merge(k, v, BigDecimal::add));
        }
        return total;
    }

    public static void calendarFilter(FormCalendarViewConfig formCalendarViewConfig,
                                      FormViewCalendarRequest formViewCalendarRequest, List<Criteria> allList) {
        String startDateField = MongoSearchUtils.getFieldId(formCalendarViewConfig.getStartDateField(),
                formCalendarViewConfig.getFieldType());
        String endDateField = MongoSearchUtils.getFieldId(formCalendarViewConfig.getEndDateField(),
                formCalendarViewConfig.getFieldType());
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(new Criteria(startDateField).lte(formViewCalendarRequest.getEndTime().getTime()));
        criteriaList.add(new Criteria(endDateField).gte(formViewCalendarRequest.getStartTime().getTime()));
        allList.add(new Criteria().andOperator(criteriaList));
    }
}
