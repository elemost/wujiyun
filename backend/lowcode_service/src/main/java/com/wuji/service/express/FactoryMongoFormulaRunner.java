package com.wuji.service.express;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressResourceLoader;
import com.ql.util.express.parse.NodeTypeManager;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.express.function.MongoAddFunction;
import com.wuji.service.express.function.MongoAndCommonFunction;
import com.wuji.service.express.function.MongoDivideFunction;
import com.wuji.service.express.function.MongoEqualFunction;
import com.wuji.service.express.function.MongoGtFunction;
import com.wuji.service.express.function.MongoGteFunction;
import com.wuji.service.express.function.MongoLtFunction;
import com.wuji.service.express.function.MongoLteFunction;
import com.wuji.service.express.function.MongoMultiplyFunction;
import com.wuji.service.express.function.MongoNotEqualFunction;
import com.wuji.service.express.function.MongoSubtractFunction;
import com.wuji.service.express.function.date.MongoDateFunction;
import com.wuji.service.express.function.date.MongoDatedeltaFunction;
import com.wuji.service.express.function.date.MongoDatedifFunction;
import com.wuji.service.express.function.date.MongoDayFunction;
import com.wuji.service.express.function.date.MongoHourFunction;
import com.wuji.service.express.function.date.MongoIsoweeknumFunction;
import com.wuji.service.express.function.date.MongoMinuteFunction;
import com.wuji.service.express.function.date.MongoMonthFunction;
import com.wuji.service.express.function.date.MongoNowFunction;
import com.wuji.service.express.function.date.MongoSecondFunction;
import com.wuji.service.express.function.date.MongoTimestampFunction;
import com.wuji.service.express.function.date.MongoTodayFunction;
import com.wuji.service.express.function.date.MongoWeeknumFunction;
import com.wuji.service.express.function.date.MongoYearFunction;
import com.wuji.service.express.function.logic.MongoAndFunction;
import com.wuji.service.express.function.logic.MongoIfFunction;
import com.wuji.service.express.function.logic.MongoIfsFunction;
import com.wuji.service.express.function.math.MongoAbsFunction;
import com.wuji.service.express.function.math.MongoCeilingFunction;
import com.wuji.service.express.function.math.MongoFactoryAverageFunction;
import com.wuji.service.express.function.math.MongoFactoryMaxFunction;
import com.wuji.service.express.function.math.MongoFactoryMinFunction;
import com.wuji.service.express.function.math.MongoFactorySumFunction;
import com.wuji.service.express.function.math.MongoFixedFunction;
import com.wuji.service.express.function.math.MongoFloorFunction;
import com.wuji.service.express.function.math.MongoIntFunction;
import com.wuji.service.express.function.math.MongoLogFunction;
import com.wuji.service.express.function.math.MongoModFunction;
import com.wuji.service.express.function.math.MongoPowerFunction;
import com.wuji.service.express.function.math.MongoRandFunction;
import com.wuji.service.express.function.math.MongoRoundFunction;
import com.wuji.service.express.function.math.MongoSqrtFunction;
import com.wuji.service.express.function.text.MongoCharFunction;
import com.wuji.service.express.function.text.MongoConcatenateFunction;
import com.wuji.service.express.function.text.MongoExactFunction;
import com.wuji.service.express.function.text.MongoIsemptyFunction;
import com.wuji.service.express.function.text.MongoLeftFunction;
import com.wuji.service.express.function.text.MongoLenFunction;
import com.wuji.service.express.function.text.MongoLowerFunction;
import com.wuji.service.express.function.text.MongoMidFunction;
import com.wuji.service.express.function.text.MongoReplaceFunction;
import com.wuji.service.express.function.text.MongoReptFunction;
import com.wuji.service.express.function.text.MongoRightFunction;
import com.wuji.service.express.function.text.MongoSearchFunction;
import com.wuji.service.express.function.text.MongoTextFunction;
import com.wuji.service.express.function.text.MongoTrimFunction;
import com.wuji.service.express.function.text.MongoUpperFunction;
import com.wuji.service.express.function.text.MongoValueFunction;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class FactoryMongoFormulaRunner extends ExpressRunner {
    private Map<String, Object> contextMap;

    // 新增带Map参数的构造方法
    public FactoryMongoFormulaRunner(Map<String, Object> contextMap) {
        super();
        this.contextMap = contextMap;
    }

    public FactoryMongoFormulaRunner() {
        super();
    }

    public FactoryMongoFormulaRunner(boolean isPrecise, boolean isTrace) {
        super(isPrecise, isTrace);
    }

    public FactoryMongoFormulaRunner(boolean isPrecise, boolean isStrace, NodeTypeManager nodeTypeManager) {
        super(isPrecise, isStrace, nodeTypeManager);
    }

    public FactoryMongoFormulaRunner(boolean isPrecise, boolean isTrace, IExpressResourceLoader iExpressResourceLoader,
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
        addLogicFunction();
        addMathFunction();
        addDateFunction();
        addTextFunction();
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
            this.replaceOperator("and", new MongoAndCommonFunction("and"));
        } catch (Exception e) {
            log.error("覆盖公式失败", e);
        }
    }

    public void addLogicFunction() {
        addFunctionWithContext("IF", new MongoIfFunction("IF"));
        addFunctionWithContext("AND", new MongoAndFunction("AND"));
        addFunctionWithContext("IFS", new MongoIfsFunction("IFS"));
    }

    public void addTextFunction() {
        addFunctionWithContext("CONCATENATE", new MongoConcatenateFunction("CONCATENATE"));
        addFunctionWithContext("CHAR", new MongoCharFunction("CHAR"));
        addFunctionWithContext("EXACT", new MongoExactFunction("EXACT"));
        addFunctionWithContext("ISEMPTY", new MongoIsemptyFunction("ISEMPTY"));
        addFunctionWithContext("LEFT", new MongoLeftFunction("LEFT"));
        addFunctionWithContext("RIGHT", new MongoRightFunction("RIGHT"));
        addFunctionWithContext("LOWER", new MongoLowerFunction("LOWER"));
        addFunctionWithContext("LEN", new MongoLenFunction("LEN"));
        addFunctionWithContext("MID", new MongoMidFunction("MID"));
        addFunctionWithContext("REPLACE", new MongoReplaceFunction("REPLACE"));
        addFunctionWithContext("SEARCH", new MongoSearchFunction("SEARCH"));
        addFunctionWithContext("TEXT", new MongoTextFunction("TEXT"));
        addFunctionWithContext("TRIM", new MongoTrimFunction("TRIM"));
        addFunctionWithContext("VALUE", new MongoValueFunction("VALUE"));
        addFunctionWithContext("UPPER", new MongoUpperFunction("UPPER"));
        addFunctionWithContext("REPT", new MongoReptFunction("REPT"));
    }

    public void addMathFunction() {
        addFunctionWithContext("MAX", new MongoFactoryMaxFunction("MAX"));
        addFunctionWithContext("MIN", new MongoFactoryMinFunction("MIN"));
        addFunctionWithContext("AVERAGE", new MongoFactoryAverageFunction("AVERAGE"));
        addFunctionWithContext("CEILING", new MongoCeilingFunction("CEILING"));
        addFunctionWithContext("ABS", new MongoAbsFunction("ABS"));
        addFunctionWithContext("FIXED", new MongoFixedFunction("FIXED"));
        addFunctionWithContext("FLOOR", new MongoFloorFunction("FLOOR"));
        addFunctionWithContext("INT", new MongoIntFunction("INT"));
        addFunctionWithContext("LOG", new MongoLogFunction("LOG"));
        addFunctionWithContext("MOD", new MongoModFunction("MOD"));
        addFunctionWithContext("POWER", new MongoPowerFunction("POWER"));
        addFunctionWithContext("RAND", new MongoRandFunction("RAND"));
        addFunctionWithContext("ROUND", new MongoRoundFunction("ROUND"));
        addFunctionWithContext("SQRT", new MongoSqrtFunction("SQRT"));
        addFunctionWithContext("SUM", new MongoFactorySumFunction("SUM"));
    }

    public void addDateFunction() {
        this.addFunction("DATE", new MongoDateFunction("DATE"));
        this.addFunction("DATEDELTA", new MongoDatedeltaFunction("DATEDELTA"));
        this.addFunction("DATEDIF", new MongoDatedifFunction("DATEDIF"));
        this.addFunction("DAY", new MongoDayFunction("DAY"));
        this.addFunction("HOUR", new MongoHourFunction("HOUR"));
        this.addFunction("ISOWEEKNUM", new MongoIsoweeknumFunction("ISOWEEKNUM"));
        addFunctionWithContext("MINUTE", new MongoMinuteFunction("MINUTE"));
        this.addFunction("MONTH", new MongoMonthFunction("MONTH"));
        this.addFunction("NOW", new MongoNowFunction("NOW"));
        this.addFunction("SECOND", new MongoSecondFunction("SECOND"));
        this.addFunction("TIMESTAMP", new MongoTimestampFunction("TIMESTAMP"));
        this.addFunction("TODAY", new MongoTodayFunction("TODAY"));
        this.addFunction("WEEKNUM", new MongoWeeknumFunction("WEEKNUM"));
        addFunctionWithContext("YEAR", new MongoYearFunction("YEAR"));
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
