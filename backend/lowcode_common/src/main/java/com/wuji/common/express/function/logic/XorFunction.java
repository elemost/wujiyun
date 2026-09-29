package com.wuji.common.express.function.logic;

import cn.hutool.core.util.ObjectUtil;
import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.op.OperatorBase;

public class XorFunction extends OperatorBase {

    public XorFunction(String name) {
        this.name = name;
    }


    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        if (list.length < 2) {
            new OperateData(Boolean.FALSE, Boolean.class);
        }
        OperateData rst = null;
        OperateData flag = null;
        Object zero = list.get(0).getObject(parent);
        Object one = list.get(1).getObject(parent);
        if (list.length == 2) {
            if (ObjectUtil.equal(zero, one)) { //
                rst = new OperateData(Boolean.FALSE, Boolean.class);
            } else {
                rst = new OperateData(Boolean.TRUE, Boolean.class);
            }
        } else {
            if (ObjectUtil.equal(zero, one)) { //
                rst = new OperateData(Boolean.FALSE, Boolean.class);
                flag = rst;
            } else {
                rst = new OperateData(Boolean.TRUE, Boolean.class);
                flag = rst;
            }
            Boolean flagB = (Boolean) flag.getObject(parent);
            for (int i = 2; i < list.length; i++) {
                OperateData tmp = (OperateData) list.get(i);
                Boolean tmpB = (Boolean) tmp.getObject(parent);
                if (ObjectUtil.equal(tmpB, flagB)) {
                    flag = new OperateData(Boolean.FALSE, Boolean.class);
                    flagB = (Boolean) flag.getObject(parent);
                    rst = flag;
                } else {
                    flag = new OperateData(Boolean.TRUE, Boolean.class);
                    flagB = (Boolean) flag.getObject(parent);
                    rst = flag;
                }
            }
        }
        return rst;
    }

}
