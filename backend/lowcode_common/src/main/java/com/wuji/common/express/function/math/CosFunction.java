package com.wuji.common.express.function.math;
import com.ql.util.express.Operator;
public class CosFunction extends Operator {


    public CosFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] objects) throws Exception {

        // 参数校验
        if (objects.length == 0) {
            return 0;
        }

        double hudu = Double.parseDouble(objects[0].toString());

        return Math.cos(hudu);
    }
}
