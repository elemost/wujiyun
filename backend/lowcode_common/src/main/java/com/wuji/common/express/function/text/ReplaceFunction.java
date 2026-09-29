package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;

public class ReplaceFunction extends Operator {

    public ReplaceFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        String rst = "";
        if (list.length == 0) {
            return "";
        } else if (list.length != 4) {//操作数不是3的话直接抛异常
            return "";
        } else {
            // old_text: 必需。要替换其部分字符的文本。
            Object oldText = list[0];
            // start_num: 必需。old_text 中要替换为 new_text 的字符位置。
            Object startNum = list[1];
            //num_chars: 必需。old_text 中希望使用 new_text 来进行替换的字符数。
            Object numChars = list[2];
            // new_text: 必需。将替换 old_text 中字符的文本。
            Object newText = list[3];

            // 整型类型
            if (startNum instanceof Integer && numChars instanceof Integer) {
                String oldTextStr = oldText.toString();
                if ((Integer) startNum == 0) {
                    return "";
                } else {
                    char[] arr = oldTextStr.toCharArray();
                    int startNumInx = (Integer) startNum - 1;
                    int numCharsInx = (Integer) numChars;
                    char[] rs = newText.toString().toCharArray();
                    int j = 0;
                    for (int i = startNumInx; i < startNumInx + numCharsInx; i++) {
                        arr[i] = rs[j++];
                    }
                    rst = new String(arr);
                }
            } else {
                return "";
            }
        }
        return rst;
    }
}
