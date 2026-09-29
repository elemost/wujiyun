package com.wuji.common.express.function.text;

import cn.hutool.core.util.ObjectUtil;
import com.ql.util.express.Operator;

public class IsEmptyFunction extends Operator {

    public IsEmptyFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {

        if (list.length == 0) {
            return null;
        }
        return ObjectUtil.isNull(list[0]) || ObjectUtil.isEmpty(list[0]);
    }
}
