package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class SumProductFunction extends Operator {

    public SumProductFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        if (lists.length == 0) {
            return 0;
        }
        // 判断数组中的内容是不是数字，若不是数字抛出异常
        boolean checkNumeric = checkArgs(lists);
        // 最终的数组在sum中
        BigDecimal res = BigDecimal.ZERO;
        if (checkNumeric) { //如果是数字，就进行加权求和
            // 栈,先进后出
            Stack<List<Object>> stack = new Stack<>();

            for (Object list : lists) {
                Object[] sub = (Object[]) list;
                List<Object> stackList = new ArrayList<Object>();
                Collections.addAll(stackList, sub);
                stack.push(stackList);
            }


            // 取出来栈顶元素
            List<Object> one = stack.pop();
            List<Object> mulVal = new ArrayList<>();
            Stack<List<Object>> sum = new Stack<>();
            while (!stack.isEmpty()) {
                List<Object> two = stack.pop();
                List<Object> calVal = cal(two, one);
                sum.push(calVal);
                mulVal.addAll(calVal);
                one = calVal;
            }

            List<Object> nums = sum.pop();
            for (Object num : nums) {
                res = res.add(new BigDecimal(num + ""));
            }
        }
        return res;
    }

    //判断数组中的内容是不是数字，若不是数字抛出异常
    private boolean checkArgs(Object[] lists) {
        boolean rst = false;
        for (Object list : lists) {
            Object[] sub = (Object[]) list;
            for (Object ele : sub) {
                if (ele == null) {
                    return false;
                }
                boolean isnum = NumberUtils.isCreatable(ele.toString());
                if (!isnum) {
                    return false;
                } else {
                    rst = true;
                }
            }
        }
        return rst;
    }

    private List<Object> cal(List<Object> one, List<Object> two) {
        List<Object> res = new ArrayList<>();
        for (int i = 0; i < one.size(); i++) {
            // 转成高精度
            Object n1 = one.get(i);
            BigDecimal bigDecimal1 = new BigDecimal(n1 + "");
            for (int j = 0; j < two.size(); j++) {
                Object n2 = two.get(j);
                BigDecimal bigDecimal2 = new BigDecimal(n2 + "");
                if (i == j) {
                    BigDecimal n3 = bigDecimal1.multiply(bigDecimal2);
                    res.add(n3);
                }

            }
        }
        return res;
    }

}
