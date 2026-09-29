package com.wuji.common.express.function.math;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.Operator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SumIfFunction extends Operator {

    public SumIfFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        //边界判断
        if (lists.length < 3 || lists.length > 4) {
            return 0;
        }

        BigDecimal res = BigDecimal.ZERO;
        Object range = null;
        Object criteria = null;
        List<JSONObject> subFormVal = null;
        String rangeS = "";
        String key = "";
        if (lists.length == 3) { // 两个参数
            // 获取参数
            range = lists[1];
            criteria = lists[2];

            rangeS = range.toString();
            subFormVal = JSONArray.parseArray(JSONObject.toJSONString(lists[0]), JSONObject.class);
            res = cal(subFormVal, rangeS, criteria.toString(), rangeS);
        } else {
            // 三个参数处理
            Object sumRange = lists[3];
            range = lists[1];
            criteria = lists[2];

            rangeS = range.toString();
            subFormVal = JSONArray.parseArray(JSONObject.toJSONString(lists[0]), JSONObject.class);
            // 循环subFormVal集合
            res = cal(subFormVal, rangeS, criteria.toString(), sumRange.toString());
        }
        return res;
    }

    // 计算结果
    private BigDecimal cal(List<JSONObject> list, String range, String criteria, String sumRange) {

        // criteria判断类型
        boolean isNum = CriteriaUtil.isNum(criteria);
        boolean isExpress = CriteriaUtil.isExpress(criteria);

        // 根据criteria类型 生成Predicate
        List<Map<String, Object>> collect = null;
        if (isExpress) {// 如果是表达式
            // 提取符号
            String symbol = CriteriaUtil.extractSymbol(criteria);
            String symbol_value = criteria.replaceAll(symbol, "");
            boolean sybolValueIsNum = CriteriaUtil.isNum(symbol_value);
            if (sybolValueIsNum) {// 如果是数字
                collect = list.stream().filter(paramMap -> {
                    boolean res = false;
                    if ("==".equals(symbol)) {
                        res = Double.parseDouble(paramMap.get(range).toString()) == Double.parseDouble(symbol_value);
                    } else if (">".equals(symbol)) {
                        res = Double.parseDouble(paramMap.get(range).toString()) > Double.parseDouble(symbol_value);
                    } else if (">=".equals(symbol)) {
                        res = Double.parseDouble(paramMap.get(range).toString()) >= Double.parseDouble(symbol_value);
                    } else if ("<".equals(symbol)) {
                        res = Double.parseDouble(paramMap.get(range).toString()) < Double.parseDouble(symbol_value);
                    } else if ("<=".equals(symbol)) {
                        res = Double.parseDouble(paramMap.get(range).toString()) <= Double.parseDouble(symbol_value);
                    } else if ("!=".equals(symbol)) {
                        res = Double.parseDouble(paramMap.get(range).toString()) != Double.parseDouble(symbol_value);
                    }
                    return res;
                }).collect(Collectors.toList());
            } else {
                collect = list.stream().filter(paramMap -> {
                    boolean res = false;
                    if ("==".equals(symbol)) {
                        res = String.valueOf(paramMap.get(range)).equals(symbol_value);
                    } else if ("!=".equals(symbol)) {
                        res = !(String.valueOf(paramMap.get(range)).equals(symbol_value));
                    } else {
                        throw new RuntimeException("字符暂不支持的操作符号为：" + symbol);
                    }
                    return res;
                }).collect(Collectors.toList());
            }

        } else {// 没有表达式 直接默认为==
            if (isNum) {// 如果是数字
                collect = list.stream().filter(paramMap -> Double.parseDouble(paramMap.get(range).toString()) ==
                        Double.parseDouble(criteria)).collect(Collectors.toList());
            } else {
                collect = list.stream().filter(paramMap -> String.valueOf(paramMap.get(range)).equals(criteria))
                        .collect(Collectors.toList());
            }
        }

        // 满足条件的集合统计出来后，按照sumRange字段统计求和
        BigDecimal sum = BigDecimal.ZERO;
        for (Map<String, Object> map : collect) {
            BigDecimal tmp = new BigDecimal(map.get(sumRange).toString());
            sum = sum.add(tmp);
        }
        return sum;

    }


    static class CriteriaUtil {

        public static boolean isNum(String criteria) {
            return StrUtil.isNumeric(criteria);
        }

        public static boolean isExpress(String criteria) {
            List<String> symbols = Arrays.asList(">", ">=", "<", "<=", "==", "!=");
            return symbols.stream().anyMatch(criteria::contains);
        }

        /***
         * 提取表达式中的符号
         * @param criteria
         * @return
         */
        public static String extractSymbol(String criteria) {
            List<String> symbols = Arrays.asList(">", ">=", "<", "<=", "==", "!=");
            final Optional<String> first = symbols.stream().filter(new Predicate<String>() {
                @Override
                public boolean test(String s) {
                    return criteria.contains(s);
                }
            }).findFirst();
            return first.get();
        }
    }

}
