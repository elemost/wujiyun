package com.wuji.service.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wuji.service.model.info.JsonInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class JsonFunctionUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 合并 JSON 树中同一层级下标题相同的节点。
     * 此方法会修改传入的 JsonNode 对象。
     *
     * @param node 需要处理的 JsonNode (通常是根节点 ArrayNode)
     */
    public static void mergeSameTitleNodes(JsonNode node) {
        if (node == null || !node.isArray()) {
            return; // 如果不是数组节点，则无法处理同级节点
        }

        ArrayNode arrayNode = (ArrayNode) node;
        // 使用 LinkedHashMap 保持处理顺序，并按键（title）分组
        Map<String, List<JsonNode>> groupedNodes = new LinkedHashMap<>();

        // 1. 遍历当前层级的所有节点，按 title 进行分组
        Iterator<JsonNode> elements = arrayNode.elements();
        while (elements.hasNext()) {
            JsonNode childNode = elements.next();
            String title = childNode.get("title").asText();
            Object tagObject = childNode.get("tag");
            Object tagIdObject = childNode.get("tagId");
            String tag = "";
            if (tagObject != null) {
                tag = tagObject.toString();
            }
            if (tagIdObject != null) {
                tag = tagIdObject.toString();
            }
            groupedNodes.computeIfAbsent(title + "_" + tag, k -> new ArrayList<>()).add(childNode);
        }

        // 2. 清空原数组，准备放入合并后的节点
        arrayNode.removeAll();

        // 3. 处理每个分组
        for (Map.Entry<String, List<JsonNode>> entry : groupedNodes.entrySet()) {
            List<JsonNode> nodesToMerge = entry.getValue();

            if (nodesToMerge.size() == 1) {
                // 如果只有一个节点，直接添加回去
                arrayNode.add(nodesToMerge.get(0));
            } else {
                // 如果有多个同名节点，需要合并
                // 取第一个节点作为基础进行合并
                ObjectNode mergedNode = nodesToMerge.get(0).deepCopy(); // 深拷贝避免修改原数据
                ArrayNode mergedChildren = objectMapper.createArrayNode();

                // 收集所有待合并节点的 children
                for (JsonNode nodeToMerge : nodesToMerge) {
                    JsonNode childrenToMerge = nodeToMerge.get("children");
                    if (childrenToMerge != null && childrenToMerge.isArray()) {
                        for (JsonNode child : childrenToMerge) {
                            mergedChildren.add(child);
                        }
                    }
                }

                // 递归处理合并后的 children
                mergeSameTitleNodes(mergedChildren);

                // 将合并后的 children 设置到基础节点上
                mergedNode.set("children", mergedChildren);

                // 将合并后的节点添加回原数组
                arrayNode.add(mergedNode);
            }
        }
    }

    private static final String ROOT_PREFIX = "$"; // JSONPath根节点

    /**
     * 将JSON转换为所有叶子节点的JSONPath语句（支持通配符和精确索引）
     *
     * @param jsonStr 输入JSON字符串
     * @return 去重后的JSONPath列表
     * @throws Exception JSON解析异常
     */
    public static List<String> convertToJsonPaths(String jsonStr) throws Exception {
        Set<String> jsonPaths = new HashSet<>();
        List<JsonInfo> jsonPathMap = new ArrayList<>();
        JsonNode rootNode = objectMapper.readTree(jsonStr);
        traverseToJsonPath(rootNode, ROOT_PREFIX, jsonPaths, jsonPathMap);
        log.info(JSONObject.toJSONString(jsonPathMap));
        return new ArrayList<>(jsonPaths);
    }

    /**
     * 递归遍历JSON，生成JSONPath
     *
     * @param node        当前节点
     * @param currentPath 当前拼接的JSONPath
     * @param jsonPaths   存储结果的集合
     */
    private static void traverseToJsonPath(JsonNode node, String currentPath, Set<String> jsonPaths,
                                           List<JsonInfo> jsonPathList) {
        Map<String, JsonInfo> jsonPathMap =
                jsonPathList.stream().collect(Collectors.toMap(JsonInfo::getJsonPath, c -> c));
        if (node.isObject()) {
            // 处理对象：拼接子属性路径（如 $.address -> $.address.city）
            node.fields().forEachRemaining(entry -> {
                String fieldName = entry.getKey();
                String childPath = currentPath + "." + fieldName;
                JsonInfo jsonInfo = jsonPathMap.get(currentPath);
                if (jsonInfo == null) {
                    jsonInfo = new JsonInfo();
                    jsonInfo.setJsonPath(currentPath);
                    jsonInfo.setType("object");
                    jsonPathMap.put(currentPath, jsonInfo);
                    jsonPathList.add(jsonInfo);
                } else {
                    if (!jsonInfo.getType().equals("object")) {
                        jsonInfo.setType("any_object");
                        jsonInfo.setChildren(new ArrayList<>());
                    }
                }
                if (!jsonInfo.getType().equals("any_object")) {
                    traverseToJsonPath(entry.getValue(), childPath, jsonPaths, jsonInfo.getChildren());
                }
            });
        } else if (node.isArray()) {
            // 处理数组：遍历元素，拼接索引或通配符
            JsonInfo jsonInfo = jsonPathMap.get(currentPath);
            if (jsonInfo == null) {
                jsonInfo = new JsonInfo();
                jsonInfo.setJsonPath(currentPath);
                jsonInfo.setType("array");
                jsonPathList.add(jsonInfo);
            } else {
                if (!jsonInfo.getType().equals("array")) {
                    jsonInfo.setType("any_object");
                    jsonInfo.setChildren(new ArrayList<>());
                }
            }
            if (!jsonInfo.getType().equals("any_object")) {
                for (int i = 0; i < node.size(); i++) {
                    JsonNode element = node.get(i);
                    JsonInfo finalJsonInfo = jsonInfo;
                    element.fields().forEachRemaining(entry -> {
                        String fieldName = entry.getKey();
                        String childPath = currentPath + "." + fieldName;
                        traverseToJsonPath(entry.getValue(), childPath, jsonPaths, finalJsonInfo.getChildren());
                    });
                }
            }
        } else {
            // 基本类型（叶子节点）：记录当前JSONPath
            if (!currentPath.equals(ROOT_PREFIX)) { // 排除根节点本身
                jsonPaths.add(currentPath);
                JsonInfo jsonInfo = jsonPathMap.get(currentPath);
                if (jsonInfo == null) {
                    jsonInfo = new JsonInfo();
                    jsonInfo.setJsonPath(currentPath);
                    jsonInfo.setType("any");
                    jsonPathList.add(jsonInfo);
                }
            }
        }
    }

    // 测试示例
    public static void main(String[] args) throws Exception {
        String json = "{\n" + "    \"result\":\"群ID\",\n" + "    \"结果\": [\n" + "        {\n" +
                "            \"订单ID\": \"A001\",\n" + "            \"车号\": \"001\",\n" +
                "            \"车容积\": 4,\n" + "            \"实际装载\": 3.9\n" + "        },\n" + "        {\n" +
                "            \"订单ID\": \"A001\",\n" + "            \"车号\": \"002\",\n" +
                "            \"车容积\": 5,\n" + "            \"实际装载\": 4.9,\n" + "            \"商品\": {\n" +
                "                \"size\":1,\n" + "                \"data\":[\n" + "                    {\n" +
                "                        \"商品名\": \"钙片\",\n" + "                        \"单位体积\": 0.3,\n" +
                "                        \"数量\": 7,\n" + "                        \"体积小计\": 2.1\n" +
                "                    },\n" + "                    {\n" +
                "                        \"商品名\": \"毛片\",\n" + "                        \"单位体积\": 0.2,\n" +
                "                        \"数量\": 14,\n" + "                        \"体积小计\": 2.8\n" +
                "                    }\n" + "                ]\n" + "            }\n" + "        },\n" + "        {\n" +
                "            \"订单ID\": \"A001\",\n" + "            \"车号\": \"003\",\n" +
                "            \"车容积\": 6,\n" + "            \"实际装载\": 5.2,\n" + "            \"商品\": [\n" +
                "                {\n" + "                    \"商品名\": \"毛片\",\n" +
                "                    \"单位体积\": 0.2,\n" + "                    \"数量\": 26,\n" +
                "                    \"体积小计\": 5.2\n" + "                }\n" + "            ]\n" + "        }\n" +
                "    ]\n" + "}";

        // 1. 使用通配符*的JSONPath（合并数组中相同属性）
        List<String> wildcardPaths = convertToJsonPaths(json);
        System.out.println("带通配符的JSONPath：");
        wildcardPaths.forEach(System.out::println);
    }
}