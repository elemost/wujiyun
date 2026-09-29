package com.wuji.console.controller;

import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.DefaultContext;
import com.wuji.common.express.FormulaRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/receive")
@Slf4j
public class Test {
    // public static void main(String[] args) throws Exception {
    //     //(1)addOperator
    //     ExpressRunner runner = new ExpressRunner();
    //     DefaultContext<String, Object> context = new DefaultContext<String, Object>();
    //     runner.addFunctionOfClassMethod("concatenate", TextExpress.class.getName(), "concatenate",
    //             new String[]{"Object"}, null);
    //     runner.addFunctionOfClassMethod("abs", MathExpress.class.getName(), "abs",
    //             new String[]{"Object"}, "请输入数字");
    //     runner.addFunctionOfClassMethod("len", MathExpress.class.getName(), "len",
    //             new String[]{"Object"}, null);
    //     Object a = runner.execute("abs(a)", context, null, false, false);
    //     System.out.println(a); // 返回结果 [1, 2, 3]
    //     // context.put("b", "ces");
    //     // Object r = runner.execute("len(concatenate(a))", context, null, false, false);
    //     // System.out.println(r); // 返回结果 [1, 2, 3]
    // }

    // public static void main(String[] args) throws IOException {
    //     try (Workbook workbook = new XSSFWorkbook();) {
    //         Sheet sheet = workbook.createSheet();
    //         Row row = sheet.createRow(0);
    //         Cell cell = row.createCell(0);
    //         Map<String, Object> map = new HashMap<>();
    //         map.put("key", "黄");
    //         // 设置公式
    //         String key = String.format("CONCATENATE(1,2,%s)", "\"" + map.get("key") + "\"");
    //         cell.setCellFormula(key);
    //         FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
    //         cell = evaluator.evaluateInCell(cell);
    //         System.out.println("The formula in " + cell.getAddress() + " evaluates to: " + cell.getStringCellValue());
    //     }
    // }

    public static void main(String[] args) throws Exception {

        FormulaRunner formulaRunner = new FormulaRunner(true, false);
        // 创建上下文
        DefaultContext<String, Object> context = new DefaultContext<>();
        String express = "number_mkzcrhb4/number_mkzcrx1y";
        context.put("number_mkzcrhb4", new BigDecimal(30));
        context.put("number_mkzcrx1y", new BigDecimal(30));
        Object object = formulaRunner.execute(express, context, null, true, false);
        System.out.println(object);
    }

    // public static void main(String[] args) throws Exception {

        // MongoFormulaRunner formulaRunner = new MongoFormulaRunner(true, false);
        // // 创建上下文
        // DefaultContext<String, Object> context = new DefaultContext<>();
        // String express = "MAX('aaaa') / MAX('bbbb')";
        //
        // Object object = formulaRunner.execute(express, context, null, true, false);
        // Document object1 = (Document) object;
        // System.out.println(object1.toJson());
    // }

    // public static void main(String[] args) throws Exception {
    //     // 操作系统适配
    //     String os = System.getProperty("os.name").toLowerCase();
    //     String pythonCmd = "/Users/huangzeman/Desktop/文件/python/java_test/bin/python";
    //     // 参数准备
    //     String jsonParams = "{\"numbers\":[1,2,3,4,5],\"operation\":\"square\"}";
    //     // 进程构建
    //     ProcessBuilder pb = new ProcessBuilder(pythonCmd, "/Users/huangzeman/Desktop/文件/python/demo.py");
    //     Process process = pb.start();
    //     // 参数传递
    //     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    //     writer.write(jsonParams);
    //     writer.newLine();
    //     writer.flush();
    //     // 结果处理
    //     BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    //     String line;
    //     while ((line = reader.readLine()) != null) {
    //         System.out.println("Python输出: " + line);
    //     }
    //
    //     // 错误处理
    //     BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    //     while ((line = errorReader.readLine()) != null) {
    //         System.err.println("Python错误: " + line);
    //     }
    //
    //     // 进程状态
    //     int exitCode = process.waitFor();
    //     System.out.println("Python进程退出码: " + exitCode);
    //
    // }


    @PostMapping("/test")
    public void receiveTest(@RequestBody JSONObject jsonObject) {
        log.info(jsonObject.toJSONString());
    }
}
