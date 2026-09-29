package com.wuji.service.utils;


import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支持多级维度嵌套插入小计行（如先插key=1+2的小计，再插key=1的小计）
 */
public class MultiLevelSummaryRowInserter {

    // 通用小计存储类
    @Getter
    public static class Summary {
        private final Map<String, BigDecimal> sumMap = new HashMap<>();

        public void addValue(String field, BigDecimal value) {
            BigDecimal val = value == null ? BigDecimal.ZERO : value;
            sumMap.put(field, sumMap.getOrDefault(field, BigDecimal.ZERO).add(val));
        }
    }

    // 标记小计行的常量（避免小计行被重复计算）
    private static final String SUMMARY_LEVEL_MARK = "_summary_level";


    public static Map<String, Summary> getGroupSummaryMap(List<String> dimensions, List<JSONObject> mappedResults,
                                                          List<String> sumFields) {
        // 步骤2：按当前维度分组并计算小计
        Map<String, List<JSONObject>> groupDataMap = new HashMap<>();
        Map<String, Summary> groupSummaryMap = new HashMap<>();
        for (JSONObject row : mappedResults) {
            String groupKey = "";
            if (CollectionUtils.isNotEmpty(dimensions)) {
                groupKey = generateGroupKey(row, dimensions);
                // 分组数据
                groupDataMap.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(row);
            }
            // 计算小计
            Summary summary = groupSummaryMap.getOrDefault(groupKey, new Summary());
            for (String field : sumFields) {
                summary.addValue(field, row.getBigDecimal(field));
            }
            groupSummaryMap.put(groupKey, summary);
        }
        return groupSummaryMap;
    }

    /**
     * 核心方法：按维度层级嵌套插入小计行
     *
     * @param originalDataList 原始数据列表
     * @param dimensionLevels  维度层级列表（从细到粗，如 [[1,2], [1]]）
     * @param sumFields        求和字段列表
     * @return 包含所有层级小计行的最终列表
     */
    public static List<JSONObject> insertMultiLevelSummaryRows(List<JSONObject> originalDataList,
                                                               List<List<String>> dimensionLevels,
                                                               List<String> sumFields) {

        // 初始化最终列表（先复制原始数据，避免修改原列表）
        List<JSONObject> finalDataList = new ArrayList<>();
        for (JSONObject row : originalDataList) {
            JSONObject newRow = JSONObject.parseObject(JSONObject.toJSONString(row));
            newRow.put(SUMMARY_LEVEL_MARK, "original"); // 标记为原始行
            finalDataList.add(newRow);
        }

        // 遍历每个维度层级（从细到粗）
        for (List<String> dimensions : dimensionLevels) {
            // 步骤1：筛选当前层级需要计算的行（仅原始行 + 比当前维度细的小计行，避免重复计算）
            List<JSONObject> calcRows = new ArrayList<>();
            for (JSONObject row : finalDataList) {
                String rowType = (String) row.get(SUMMARY_LEVEL_MARK);
                // 仅处理原始行，或维度比当前更细的小计行（当前维度是[1]时，跳过[1]层级的小计行）
                if ("original".equals(rowType) ||
                        (rowType.startsWith("summary_") && rowType.split("_").length > dimensions.size() + 1)) {
                    calcRows.add(row);
                }
            }

            // 步骤2：按当前维度分组并计算小计
            Map<String, List<JSONObject>> groupDataMap = new HashMap<>();
            Map<String, Summary> groupSummaryMap = new HashMap<>();
            for (JSONObject row : calcRows) {
                String groupKey = generateGroupKey(row, dimensions);
                // 分组数据
                groupDataMap.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(row);
                // 计算小计
                Summary summary = groupSummaryMap.getOrDefault(groupKey, new Summary());
                for (String field : sumFields) {
                    summary.addValue(field, row.getBigDecimal(field));
                }
                groupSummaryMap.put(groupKey, summary);
            }

            // 步骤3：构建当前维度的小计行，并插入到对应位置
            List<JSONObject> tempList = new ArrayList<>();
            String currentLevelMark = "summary_" + String.join("_", dimensions); // 标记当前维度层级
            int i = 0;
            while (i < finalDataList.size()) {
                JSONObject currentRow = finalDataList.get(i);
                tempList.add(currentRow);

                // 判断当前行是否是当前维度分组的最后一行
                String currentRowGroupKey = generateGroupKey(currentRow, dimensions);
                List<JSONObject> groupRows = groupDataMap.get(currentRowGroupKey);
                if (groupRows != null && i == findLastRowIndex(finalDataList, groupRows)) {
                    // 构建当前维度的小计行
                    JSONObject summaryRow = buildSummaryRow(currentRowGroupKey, dimensions, sumFields,
                            groupSummaryMap.get(currentRowGroupKey), currentLevelMark);
                    tempList.add(summaryRow);
                }
                i++;
            }
            // 更新最终列表
            finalDataList = tempList;
        }

        // 移除标记字段（可选，根据实际需求决定是否保留）
        finalDataList.forEach(row -> row.remove(SUMMARY_LEVEL_MARK));
        return finalDataList;
    }

    /**
     * 生成分组键
     */
    public static String generateGroupKey(JSONObject row, List<String> dimensions) {
        StringBuilder key = new StringBuilder();
        for (String dim : dimensions) {
            Object val = row.get(dim);
            key.append(val == null ? "空" : val.toString()).append("||");
        }
        return key.length() > 0 ? key.delete(key.length() - 2, key.length()).toString() : "";
    }

    /**
     * 查找分组最后一行在列表中的索引
     */
    private static int findLastRowIndex(List<JSONObject> fullList, List<JSONObject> groupRows) {
        int lastIndex = -1;
        for (JSONObject row : groupRows) {
            int idx = fullList.indexOf(row);
            if (idx > lastIndex) {
                lastIndex = idx;
            }
        }
        return lastIndex;
    }

    /**
     * 构建小计行
     */
    private static JSONObject buildSummaryRow(String groupKey, List<String> dimensions, List<String> sumFields,
                                              Summary summary, String levelMark) {

        JSONObject summaryRow = new JSONObject();
        // 维度字段：保留分组值
        String[] dimValues = groupKey.split("\\|\\|");
        for (int i = 0; i < dimensions.size(); i++) {
            summaryRow.put(dimensions.get(i), dimValues[i]);
        }
        // 求和字段：填入小计值
        for (String field : sumFields) {
            summaryRow.put(field, summary.getSumMap().get(field));
        }
        // 其他字段：标注维度层级小计
        summaryRow.put("remark",
                dimensions.size() > 1 ? String.join("+", dimensions) + "维度小计" : dimensions.get(0) + "维度小计");
        // 标记小计行层级（内部使用）
        summaryRow.put(SUMMARY_LEVEL_MARK, levelMark);
        return summaryRow;
    }

    public static void setSubSummaryLabel(List<JSONObject> returnDataList, Integer maxIndex) {
        for (JSONObject jsonObject : returnDataList) {
            if (jsonObject.containsKey("remark")) {
                for (int i = 0; i < maxIndex; i++) {
                    String key = i + 1 + "";
                    if (jsonObject.get(key) == null) {
                        jsonObject.putIfAbsent(key, "小计");
                        break;
                    }
                }
            }
        }
    }
}