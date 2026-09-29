package com.wuji.common.express.function.logic;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.op.OperatorBase;


public class IfsFunction extends OperatorBase {

    public IfsFunction(String name) {
        this.name = name;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        if (list.length < 2) {
            new OperateData("", String.class);
        }
        OperateData rst = null;
        for (int i = 0; i < list.length - 1; i += 2) {
            Object obj = list.get(i).getObject(parent);
            if (obj == null) {
                new OperateData("", String.class);
            } else if (!(obj instanceof Boolean)) {
                new OperateData("", String.class);
            } else {
                if ((Boolean) obj) {
                    rst = list.get(i + 1);
                    break;
                }
            }
        }
        return rst;
    }
}
