package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;

public class ValueFunction extends Operator {

    public ValueFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        if (lists.length == 0) {
            return null;
        }
        // 获取文本值
        Object numberText = lists[0];

        // 数字类型
        if (numberText instanceof String) {
            String str = numberText.toString();

            if (!NumberUtils.isCreatable(str)) {
                return null;
            }
        }
        return new BigDecimal(numberText.toString());
    }

}
