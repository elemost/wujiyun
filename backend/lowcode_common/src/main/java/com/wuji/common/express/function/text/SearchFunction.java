package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;

public class SearchFunction extends Operator {

    public SearchFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {
        int rst = 0;
        if (lists.length <= 1) {
            return null;
        } else if (lists.length > 3) {
            return null;
        } else { // 参数如果有两个
            if (lists.length == 2) {
                Object find_text = lists[0];
                Object within_text = lists[1];

                if (find_text instanceof Character && within_text instanceof String) {
                    String findText = handleC2S(((Character) find_text));
                    String withinText = ((String) within_text).toLowerCase();
                    rst = handleRst(findText, withinText, 0);
                } else if (find_text instanceof String && within_text instanceof String) {
                    String findText = ((String) find_text).toLowerCase();
                    String withinText = ((String) within_text).toLowerCase();
                    rst = handleRst(findText, withinText, 0);
                } else if (find_text instanceof String && within_text instanceof Character) {
                    String findText = ((String) find_text).toLowerCase();
                    String withinText = handleC2S(((Character) within_text));
                    rst = handleRst(findText, withinText, 0);
                } else {
                    return 0;
                }
            } else {
                Object find_text = lists[0];
                Object within_text = lists[1];
                Object start_num = lists[2];

                if (find_text instanceof Character && within_text instanceof String && start_num instanceof Integer) {
                    String findText = handleC2S(((Character) find_text));
                    String withinText = ((String) within_text).toLowerCase();
                    int startNum = (Integer) start_num;
                    rst = handleRst(findText, withinText, startNum);
                } else if (find_text instanceof String && within_text instanceof String) {
                    String findText = ((String) find_text).toLowerCase();
                    String withinText = ((String) within_text).toLowerCase();
                    int startNum = (Integer) start_num;
                    rst = handleRst(findText, withinText, startNum);
                } else if (find_text instanceof String && within_text instanceof Character &&
                        start_num instanceof Integer) {
                    String findText = ((String) find_text).toLowerCase();
                    String withinText = handleC2S(((Character) within_text));
                    int startNum = (Integer) start_num;
                    rst = handleRst(findText, withinText, startNum);
                } else {
                    return null;
                }

            }
        }
        return rst;
    }

    private String handleC2S(Character find_text) {
        return find_text.toString().toLowerCase();

    }

    private int handleRst(String findText, String withinText, int startNum) {
        // 截取子串
        withinText = withinText.substring(startNum);
        int position = withinText.indexOf(findText);
        position = position + 1;
        return position;
    }


}
