package com.wuji.service.express.function;

import com.ql.util.express.Operator;

import java.util.Map;

/**
 * MongoDB公式函数基类，提供上下文Map的支持
 */
public abstract class BaseMongoFunction extends Operator {

    // 上下文参数Map
    protected Map<String, Object> contextMap;

    public BaseMongoFunction(String name) {
        super();
    }

    /**
     * 设置上下文Map参数
     * @param contextMap 要传递的Map参数
     */
    public void setContextMap(Map<String, Object> contextMap) {
        this.contextMap = contextMap;
    }

    /**
     * 获取上下文Map参数
     * @return 上下文Map
     */
    public Map<String, Object> getContextMap() {
        return contextMap;
    }
}
