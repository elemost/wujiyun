package com.wuji.common.express.function.logic;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.op.OperatorBase;

public class IfFunction extends OperatorBase {

    public IfFunction(String name) {
        this.name = name;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        if (list.length < 2) {
            return new OperateData("", String.class);
        } else {
            Object obj = list.get(0).getObject(parent);
            if (obj == null) {
                return new OperateData("", String.class);
            } else if (!(obj instanceof Boolean)) {
                return new OperateData("", String.class);
            } else {
                return (Boolean) obj ? list.get(1) : (list.length == 3 ? list.get(2) : null);
            }
        }
    }
}
