package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;

public class MidFunction extends Operator {

    public MidFunction(String name) {
        this.name = name;
    }


    @Override
    public Object executeInner(Object[] list) {

        String rst = "";
        if (list.length == 0) {
            return rst;
        } else if (list.length != 3) {//操作数不是3的话直接抛异常
            return rst;
        } else {
            // 3个参数
            // 必需。 包含要提取字符的文本字符串
            Object text = list[0];
            // 必需。 文本中要提取的第一个字符的位置。 文本中第一个字符的 start_num 为 1，以此类推。
            Object startNum = list[1];
            // 必需。 指定希望从文本中返回字符的个数。
            Object numChars = list[2];
            // 整型类型
            if (startNum instanceof Integer && numChars instanceof Integer) {
                String textStr = text.toString();
                if ((Integer) startNum == 0) {
                    return "";
                } else {
                    int startNumInx = (Integer) startNum - 1;
                    int numCharsInx = (Integer) numChars;
                    rst = textStr.substring(startNumInx, startNumInx + numCharsInx);
                }
            }
        }
        return rst;
    }
}
