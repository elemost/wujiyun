package com.wuji.common.express.function.high;

import cn.hutool.core.lang.UUID;
import com.ql.util.express.Operator;

public class UuidFunction extends Operator {

    public UuidFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return UUID.fastUUID();
    }
}
