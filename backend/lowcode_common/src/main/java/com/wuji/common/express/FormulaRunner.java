package com.wuji.common.express;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressResourceLoader;
import com.ql.util.express.parse.NodeTypeManager;
import com.wuji.common.express.function.date.DateDeltaFunction;
import com.wuji.common.express.function.date.DateDifFunction;
import com.wuji.common.express.function.date.DateFunction;
import com.wuji.common.express.function.date.DayFunction;
import com.wuji.common.express.function.date.DaysFunction;
import com.wuji.common.express.function.date.HourFunction;
import com.wuji.common.express.function.date.IsoWeekNumFunction;
import com.wuji.common.express.function.date.MinuteFunction;
import com.wuji.common.express.function.date.MonthFunction;
import com.wuji.common.express.function.date.NowFunction;
import com.wuji.common.express.function.date.SecondFunction;
import com.wuji.common.express.function.date.TimestampFunction;
import com.wuji.common.express.function.date.TodayFunction;
import com.wuji.common.express.function.date.YearFunction;
import com.wuji.common.express.function.high.IndexFunction;
import com.wuji.common.express.function.high.TextDeptFunction;
import com.wuji.common.express.function.high.TextUserFunction;
import com.wuji.common.express.function.high.UuidFunction;
import com.wuji.common.express.function.logic.AndFunction;
import com.wuji.common.express.function.logic.FalseFunction;
import com.wuji.common.express.function.logic.IfFunction;
import com.wuji.common.express.function.logic.IfsFunction;
import com.wuji.common.express.function.logic.NotFunction;
import com.wuji.common.express.function.logic.OrFunction;
import com.wuji.common.express.function.logic.TrueFunction;
import com.wuji.common.express.function.logic.XorFunction;
import com.wuji.common.express.function.math.AbsFunction;
import com.wuji.common.express.function.math.AvgFunction;
import com.wuji.common.express.function.math.CeilingFunction;
import com.wuji.common.express.function.math.CosFunction;
import com.wuji.common.express.function.math.CotFunction;
import com.wuji.common.express.function.math.CountFunction;
import com.wuji.common.express.function.math.CountIfFunction;
import com.wuji.common.express.function.math.FixedFunction;
import com.wuji.common.express.function.math.FloorFunction;
import com.wuji.common.express.function.math.IntFunction;
import com.wuji.common.express.function.math.LargeFunction;
import com.wuji.common.express.function.math.LogFunction;
import com.wuji.common.express.function.math.MaxFunction;
import com.wuji.common.express.function.math.MinFunction;
import com.wuji.common.express.function.math.ModFunction;
import com.wuji.common.express.function.math.PowerFunction;
import com.wuji.common.express.function.math.ProductFunction;
import com.wuji.common.express.function.math.RadiansFunction;
import com.wuji.common.express.function.math.RandFunction;
import com.wuji.common.express.function.math.RoundFunction;
import com.wuji.common.express.function.math.SinFunction;
import com.wuji.common.express.function.math.SmallFunction;
import com.wuji.common.express.function.math.SqrtFunction;
import com.wuji.common.express.function.math.SumFunction;
import com.wuji.common.express.function.math.SumIfFunction;
import com.wuji.common.express.function.math.SumIfsFunction;
import com.wuji.common.express.function.math.SumProductFunction;
import com.wuji.common.express.function.math.TanFunction;
import com.wuji.common.express.function.text.CharFunction;
import com.wuji.common.express.function.text.ConcatenateFunction;
import com.wuji.common.express.function.text.ExactFunction;
import com.wuji.common.express.function.text.JoinFunction;
import com.wuji.common.express.function.text.LeftFunction;
import com.wuji.common.express.function.text.LenFunction;
import com.wuji.common.express.function.text.LowerFunction;
import com.wuji.common.express.function.text.MidFunction;
import com.wuji.common.express.function.text.ReplaceFunction;
import com.wuji.common.express.function.text.ReptFunction;
import com.wuji.common.express.function.text.RightFunction;
import com.wuji.common.express.function.text.RmbCapFunction;
import com.wuji.common.express.function.text.SearchFunction;
import com.wuji.common.express.function.text.SplitFunction;
import com.wuji.common.express.function.text.TextFunction;
import com.wuji.common.express.function.text.TrimFunction;
import com.wuji.common.express.function.text.UnionFunction;
import com.wuji.common.express.function.text.UpperFunction;
import com.wuji.common.express.function.text.ValueFunction;
import com.wuji.common.express.function.user.UserRemoveFunction;


public class FormulaRunner extends ExpressRunner {

    public FormulaRunner() {
        super();
    }

    public FormulaRunner(boolean isPrecise, boolean isTrace) {
        super(isPrecise, isTrace);
    }

    public FormulaRunner(boolean isPrecise, boolean isStrace, NodeTypeManager nodeTypeManager) {
        super(isPrecise, isStrace, nodeTypeManager);
    }

    public FormulaRunner(boolean isPrecise, boolean isTrace, IExpressResourceLoader iExpressResourceLoader,
                         NodeTypeManager nodeTypeManager) {
        super(isPrecise, isTrace, iExpressResourceLoader, nodeTypeManager);
    }

    @Override
    public void addSystemFunctions() {
        // ExpressRunner 的内部系统函数
        super.addSystemFunctions();
        // 扩展公式函数
        this.customFunction();
    }

    /***
     * 自定义公式函数
     */
    public void customFunction() {

        // 逻辑公式函数
        this.addLogicFunction();

        // 数学公式函数
        this.addMathFunction();

        // 文本函数
        this.addTextFunction();

        // 日期函数
        this.addDateFunction();

        //高级函数
        this.addHighFunction();
    }

    public void addTextFunction() {
        // CHAR函数
        this.addFunction("CHAR", new CharFunction("CHAR"));

        // CONCATENATE函数
        this.addFunction("CONCATENATE", new ConcatenateFunction("CONCATENATE"));

        // EXACT函数
        this.addFunction("EXACT", new ExactFunction("EXACT"));

        // IP函数
        // this.addFunction("IP", new IpFunction("IP"));

        // ISEMPTY函数
        // this.addFunction("ISEMPTY", new IsEmptyFunction("ISEMPTY"));

        // JOIN函数
        this.addFunction("JOIN", new JoinFunction("JOIN"));

        // LEFT函数
        this.addFunction("LEFT", new LeftFunction("LEFT"));

        // LEN函数
        this.addFunction("LEN", new LenFunction("LEN"));

        // LOWER函数
        this.addFunction("LOWER", new LowerFunction("LOWER"));

        // MID函数
        this.addFunction("MID", new MidFunction("MID"));

        // REPLACE函数
        this.addFunction("REPLACE", new ReplaceFunction("REPLACE"));

        // REPT函数
        this.addFunction("REPT", new ReptFunction("REPT"));

        // RIGHT函数
        this.addFunction("RIGHT", new RightFunction("RIGHT"));

        // RMBCAP函数
        this.addFunction("RMBCAP", new RmbCapFunction("RMBCAP"));

        // SEARCH函数
        this.addFunction("SEARCH", new SearchFunction("SEARCH"));

        // SPLIT函数
        this.addFunction("SPLIT", new SplitFunction("SPLIT"));

        // TEXT函数
        this.addFunction("TEXT", new TextFunction("TEXT"));

        // TRIM函数
        this.addFunction("TRIM", new TrimFunction("TRIM"));

        // UNION函数
        this.addFunction("UNION", new UnionFunction("UNION"));

        // UPPER函数
        this.addFunction("UPPER", new UpperFunction("UPPER"));

        // VALUE函数
        this.addFunction("VALUE", new ValueFunction("VALUE"));

    }

    public void addLogicFunction() {
        // AND函数
        this.addFunction("AND", new AndFunction("AND"));

        // IF函数
        this.addFunction("IF", new IfFunction("IF"));

        // IFS函数
        this.addFunction("IFS", new IfsFunction("IFS"));

        // XOR函数
        this.addFunction("XOR", new XorFunction("XOR"));

        // TRUE函数
        this.addFunction("TRUE", new TrueFunction("TRUE"));

        // FALSE函数
        this.addFunction("FALSE", new FalseFunction("FALSE"));

        // NOT函数
        this.addFunction("NOT", new NotFunction("NOT"));

        // OR函数
        this.addFunction("OR", new OrFunction("OR"));
    }

    public void addMathFunction() {
        // ABS函数
        this.addFunction("ABS", new AbsFunction("ABS"));

        // AVERAGE函数
        this.addFunction("AVERAGE", new AvgFunction("AVERAGE"));

        // CEILING函数
        this.addFunction("CEILING", new CeilingFunction("CEILING"));

        // RADIANS函数
        this.addFunction("RADIANS", new RadiansFunction("RADIANS"));

        // COS函数
        this.addFunction("COS", new CosFunction("COS"));

        // COT函数
        this.addFunction("COT", new CotFunction("COT"));

        // COUNT函数
        this.addFunction("COUNT", new CountFunction("COUNT"));

        // COUNTIF函数
        this.addFunction("COUNTIF", new CountIfFunction("COUNTIF"));

        // FIXED函数
        this.addFunction("FIXED", new FixedFunction("FIXED"));

        // FLOOR函数
        this.addFunction("FLOOR", new FloorFunction("FLOOR"));

        // INT函数
        this.addFunction("INT", new IntFunction("INT"));

        // LARGE函数
        this.addFunction("LARGE", new LargeFunction("LARGE"));

        // LOG函数
        this.addFunction("LOG", new LogFunction("LOG"));

        // MAX函数
        this.addFunction("MAX", new MaxFunction("MAX"));

        // MIN函数
        this.addFunction("MIN", new MinFunction("MIN"));

        // MOD函数
        this.addFunction("MOD", new ModFunction("MOD"));

        // POWER函数
        this.addFunction("POWER", new PowerFunction("POWER"));

        // PRODUCT函数
        this.addFunction("PRODUCT", new ProductFunction("PRODUCT"));

        // RAND函数
        this.addFunction("RAND", new RandFunction("RAND"));

        // ROUND函数
        this.addFunction("ROUND", new RoundFunction("ROUND"));

        // SIN函数
        this.addFunction("SIN", new SinFunction("SIN"));

        // SMALL函数
        this.addFunction("SMALL", new SmallFunction("SMALL"));

        // SQRT函数
        this.addFunction("SQRT", new SqrtFunction("SQRT"));

        // SUM函数
        this.addFunction("SUM", new SumFunction("SUM"));

        // SUMIF函数
        this.addFunction("SUMIF", new SumIfFunction("SUMIF"));

        // SUMIFS函数
        this.addFunction("SUMIFS", new SumIfsFunction("SUMIFS"));

        // SUMPRODUCT函数
        this.addFunction("SUMPRODUCT", new SumProductFunction("SUMPRODUCT"));

        // TAN函数
        this.addFunction("TAN", new TanFunction("TAN"));

    }

    public void addDateFunction() {
        // DATE函数
        this.addFunction("DATE", new DateFunction("DATE"));

        // DATEDELTA函数
        this.addFunction("DATEDELTA", new DateDeltaFunction("DATEDELTA"));

        // DATEDIF函数
        this.addFunction("DATEDIF", new DateDifFunction("DATEDIF"));

        // DAY函数
        this.addFunction("DAY", new DayFunction("DAY"));

        // DAYS函数
        this.addFunction("DAYS", new DaysFunction("DAYS"));

        // HOUR函数
        this.addFunction("HOUR", new HourFunction("HOUR"));

        // ISOWEEKNUM函数
        this.addFunction("ISOWEEKNUM", new IsoWeekNumFunction("ISOWEEKNUM"));

        // MINUTE函数
        this.addFunction("MINUTE", new MinuteFunction("MINUTE"));

        // MONTH函数
        this.addFunction("MONTH", new MonthFunction("MONTH"));

        // SECOND函数
        this.addFunction("SECOND", new SecondFunction("SECOND"));

        // YEAR函数
        this.addFunction("YEAR", new YearFunction("YEAR"));

        // TIMESTAMP函数
        this.addFunction("TIMESTAMP", new TimestampFunction("TIMESTAMP"));

        this.addFunction("NOW", new NowFunction("NOW"));

        this.addFunction("TODAY", new TodayFunction("TODAY"));


    }

    public void addHighFunction() {
        // uuid函数
        this.addFunction("UUID", new UuidFunction("UUID"));

        // index函数
        this.addFunction("INDEX", new IndexFunction("INDEX"));

        // TEXTUSER函数
        this.addFunction("TEXTUSER", new TextUserFunction("TEXTUSER"));

        // TEXTDEPT函数
        this.addFunction("TEXTDEPT", new TextDeptFunction("TEXTDEPT"));

        this.addFunction("REMOVEUSER", new UserRemoveFunction("REMOVEUSER"));
    }

}
