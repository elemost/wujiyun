package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;

public class ReptFunction extends Operator {

    public ReptFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        String rst = "";
        if (list.length == 0) {
            return "";
        } else if (list.length != 2) {//操作数不是3的话直接抛异常
            return "";
        } else {
            // 必需。需要重复显示的文本。
            Object text = list[0];
            // 必需。用于指定文本重复次数的正数。
            Object numTimes = list[1];

            // 整型类型
            if (numTimes instanceof Integer && (Integer) numTimes >= 0) {
                String textStr = text.toString();
                StringBuilder sb = new StringBuilder(textStr);
                int numT = (Integer) numTimes;
                for (int i = 1; i < numT; i++) {
                    sb.append(textStr);
                }
                rst = sb.toString();
            } else {
                return "";
            }
        }
        return rst;
    }
}
