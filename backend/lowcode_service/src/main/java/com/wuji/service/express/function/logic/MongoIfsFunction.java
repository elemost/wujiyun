package com.wuji.service.express.function.logic;

import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoIfsFunction extends BaseMongoFunction {

    public MongoIfsFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) {
        List<Object> collect = Arrays.stream(lists).map(MongoSearchUtils::qlExpressValue).collect(Collectors.toList());
        if (collect.size() % 2 != 0) {
            return buildNestedCond(collect.subList(0, lists.length - 1), 0, collect.get(lists.length - 1));
        } else {
            return buildNestedCond(collect, 0, null);
        }
    }

    /**
     * 递归构建嵌套的$cond表达式
     *
     * @param ifsPairs 所有"条件-结果"对
     * @param index    当前处理的索引（从0开始，每次+2）
     * @return 嵌套的$cond Document
     */
    private static Document buildNestedCond(List<Object> ifsPairs, int index, Object defaultValue) {
        // 递归终止条件：处理到最后一对参数
        if (index >= ifsPairs.size()) {
            // 兜底值（如果所有条件都不满足，返回null，可根据需求改为默认值）
            return new Document("$literal", defaultValue);
        }

        // 当前条件（logical_testN）：需是MongoDB支持的条件表达式Document
        Document currentCondition = (Document) ifsPairs.get(index);
        // 当前结果（value_if_trueN）：可以是任意类型（字符串、数字、Document等）
        Object currentValue = ifsPairs.get(index + 1);

        // 构建当前$cond
        Document cond = new Document();
        List<Object> condList = new ArrayList<>();
        condList.add(currentCondition); // if条件
        condList.add(currentValue);     // then结果
        // else部分：递归处理下一对参数
        condList.add(buildNestedCond(ifsPairs, index + 2, defaultValue));
        cond.put("$cond", condList);

        return cond;
    }
}
