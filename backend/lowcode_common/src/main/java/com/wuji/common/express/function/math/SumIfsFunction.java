package com.wuji.common.express.function.math;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.Operator;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class SumIfsFunction extends Operator {

    public SumIfsFunction(String name) {
        this.name = name;
    }


    @Override
    public Object executeInner(Object[] lists) throws Exception {

        if (lists.length <= 2) {
            return 0;
        }

        String json = lists[0].toString();

        List<JSONObject> subFormVal = JSONArray.parseArray(JSONObject.toJSONString(lists[0]), JSONObject.class);
        String sumRange = lists[1].toString();
        String criteriaRange = lists[2].toString();
        // 动态参数
        BigDecimal res = BigDecimal.ZERO;
        JSONArray pl = JSON.parseArray(criteriaRange);
        CriteriaRangeParam[] pArr = new CriteriaRangeParam[pl.size()];
        for (int i = 0; i < pl.size(); i++) {
            JSONObject object = (JSONObject) pl.get(i);
            String _criteriaRange = (String) object.get("criteriaRange");
            String _criteria = (String) object.get("criteria");
            CriteriaRangeParam param = new CriteriaRangeParam(_criteriaRange, _criteria);
            pArr[i] = param;
        }
        res = sumifs(subFormVal, sumRange, pArr);

        return res;
    }

    // 计算结果
    private BigDecimal sumifs(List<JSONObject> list, String sumRange, CriteriaRangeParam... params) {

        if (params == null || params.length == 0) {
            return BigDecimal.ZERO;
        }

        // 根据criteria类型 生成Predicate
        List<JSONObject> collect = null;
        for (CriteriaRangeParam param : params) {
            String criteriaRange = param.getCriteriaRange();
            String criteria = param.getCriteria();// 规则
            // 根据规则转换
            // criteria判断类型
            boolean isNum = SumIfsFunction.CriteriaUtil.isNum(criteria);
            boolean isExpress = SumIfsFunction.CriteriaUtil.isExpress(criteria);

            if (isExpress) {// 如果是表达式
                // 提取符号
                String symbol = SumIfsFunction.CriteriaUtil.extractSymbol(criteria);
                String symbol_value = criteria.replaceAll(symbol, "");
                boolean sybolValueIsNum = SumIfsFunction.CriteriaUtil.isNum(symbol_value);
                if (sybolValueIsNum) {// 如果是数字
                    collect = list.stream().filter(paramMap -> {
                        boolean res = false;
                        if ("==".equals(symbol)) {
                            res = Double.parseDouble(paramMap.get(criteriaRange).toString()) ==
                                    Double.parseDouble(symbol_value);
                        } else if (">".equals(symbol)) {
                            res = Double.parseDouble(paramMap.get(criteriaRange).toString()) >
                                    Double.parseDouble(symbol_value);
                        } else if (">=".equals(symbol)) {
                            res = Double.parseDouble(paramMap.get(criteriaRange).toString()) >=
                                    Double.parseDouble(symbol_value);
                        } else if ("<".equals(symbol)) {
                            res = Double.parseDouble(paramMap.get(criteriaRange).toString()) <
                                    Double.parseDouble(symbol_value);
                        } else if ("<=".equals(symbol)) {
                            res = Double.parseDouble(paramMap.get(criteriaRange).toString()) <=
                                    Double.parseDouble(symbol_value);
                        } else if ("!=".equals(symbol)) {
                            res = Double.parseDouble(paramMap.get(criteriaRange).toString()) !=
                                    Double.parseDouble(symbol_value);
                        }
                        return res;
                    }).collect(Collectors.toList());
                } else {
                    collect = list.stream().filter(paramMap -> {
                        boolean res = false;
                        if ("==".equals(symbol)) {
                            res = String.valueOf(paramMap.get(criteriaRange)).equals(symbol_value);
                        } else if ("!=".equals(symbol)) {
                            res = !(String.valueOf(paramMap.get(criteriaRange)).equals(symbol_value));
                        } else {
                            throw new RuntimeException("字符暂不支持的操作符号为：" + symbol);
                        }
                        return res;
                    }).collect(Collectors.toList());
                }

            } else {// 没有表达式 直接默认为==
                if (isNum) {// 如果是数字
                    collect = list.stream()
                            .filter(paramMap -> Double.parseDouble(paramMap.get(criteriaRange).toString()) ==
                                    Double.parseDouble(criteria)).collect(Collectors.toList());
                } else {
                    collect = list.stream()
                            .filter(paramMap -> String.valueOf(paramMap.get(criteriaRange)).equals(criteria))
                            .collect(Collectors.toList());
                }
            }
            // 重新赋值
            list = collect;
        }

        // 满足条件的集合统计出来后，按照sumRange字段统计求和
        BigDecimal sum = BigDecimal.ZERO;
        for (Map<String, Object> map : collect) {
            BigDecimal tmp = new BigDecimal(map.get(sumRange).toString());
            sum = sum.add(tmp);
        }
        return sum;
    }


    @Getter
    public static class CriteriaRangeParam {
        private String criteriaRange;
        private String criteria;

        public CriteriaRangeParam() {

        }

        public CriteriaRangeParam(String criteriaRange, String criteria) {
            this.criteriaRange = criteriaRange;
            this.criteria = criteria;
        }

        public void setCriteriaRange(String criteriaRange) {
            this.criteriaRange = criteriaRange;
        }

        public void setCriteria(String criteria) {
            this.criteria = criteria;
        }
    }

    static class CriteriaUtil {

        public static boolean isNum(String criteria) {
            return StrUtil.isNumeric(criteria);
        }

        public static boolean isExpress(String criteria) {
            List<String> symbols = Arrays.asList("gt", "ge", "lt", "le", "eq", "nq");
            return symbols.stream().anyMatch(criteria::contains);
        }

        public static String extractSymbol(String criteria) {
            List<String> symbols = Arrays.asList("gt", "ge", "lt", "le", "eq", "nq");
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
