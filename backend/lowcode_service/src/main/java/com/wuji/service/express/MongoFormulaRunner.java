package com.wuji.service.express;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressResourceLoader;
import com.ql.util.express.parse.NodeTypeManager;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.express.function.MongoAddFunction;
import com.wuji.service.express.function.MongoAverageFunction;
import com.wuji.service.express.function.MongoCountFunction;
import com.wuji.service.express.function.MongoDivideFunction;
import com.wuji.service.express.function.MongoEqualFunction;
import com.wuji.service.express.function.MongoGtFunction;
import com.wuji.service.express.function.MongoGteFunction;
import com.wuji.service.express.function.MongoLtFunction;
import com.wuji.service.express.function.MongoLteFunction;
import com.wuji.service.express.function.MongoMaxFunction;
import com.wuji.service.express.function.MongoMinFunction;
import com.wuji.service.express.function.MongoMultiplyFunction;
import com.wuji.service.express.function.MongoNotEqualFunction;
import com.wuji.service.express.function.MongoSubtractFunction;
import com.wuji.service.express.function.MongoSumFunction;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@Slf4j
public class MongoFormulaRunner extends ExpressRunner {

    private Map<String, Object> contextMap;

    // 新增带Map参数的构造方法
    public MongoFormulaRunner(Map<String, Object> contextMap) {
        super();
        this.contextMap = contextMap;
    }

    public MongoFormulaRunner() {
        super();
    }

    public MongoFormulaRunner(boolean isPrecise, boolean isTrace) {
        super(isPrecise, isTrace);
    }

    public MongoFormulaRunner(boolean isPrecise, boolean isStrace, NodeTypeManager nodeTypeManager) {
        super(isPrecise, isStrace, nodeTypeManager);
    }

    public MongoFormulaRunner(boolean isPrecise, boolean isTrace, IExpressResourceLoader iExpressResourceLoader,
                              NodeTypeManager nodeTypeManager) {
        super(isPrecise, isTrace, iExpressResourceLoader, nodeTypeManager);
    }

    @Override
    public void addSystemFunctions() {
        // 扩展公式函数
        this.customFunction();
    }

    /***
     * 自定义公式函数
     */
    public void customFunction() {
        this.addFunction("MAX", new MongoMaxFunction("MAX"));
        this.addFunction("MIN", new MongoMinFunction("MIN"));
        this.addFunction("SUM", new MongoSumFunction("SUM"));
        this.addFunction("AVERAGE", new MongoAverageFunction("AVERAGE"));
        this.addFunction("COUNT", new MongoCountFunction("COUNT"));
        try {
            this.replaceOperator("==", new MongoEqualFunction("=="));
            this.replaceOperator("+", new MongoAddFunction("+"));
            this.replaceOperator("!=", new MongoNotEqualFunction("!="));
            this.replaceOperator("-", new MongoSubtractFunction("-"));
            this.replaceOperator("*", new MongoMultiplyFunction("*"));
            this.replaceOperator(">", new MongoGtFunction(">"));
            this.replaceOperator("<", new MongoLtFunction("<"));
            this.replaceOperator(">=", new MongoGteFunction(">="));
            this.replaceOperator("<=", new MongoLteFunction("<="));
            this.replaceOperator("/", new MongoDivideFunction("/"));
        } catch (Exception e) {
            log.error("覆盖公式失败", e);
        }
    }

    /**
     * 注册函数并设置上下文Map
     */
    private void addFunctionWithContext(String funcName, BaseMongoFunction function) {
        // 设置Map参数
        function.setContextMap(this.contextMap);
        // 注册函数
        this.addFunction(funcName, function);
    }


}
